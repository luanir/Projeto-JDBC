import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ProdutoGUI extends Application {

    private ProdutoDAO produtoDAO;
    private ObservableList<Produto> produtos;
    private TableView<Produto> tableView;
    private TextField nomeInput, quantidadeInput, precoInput;
    private ComboBox<String> statusComboBox;
    private Connection conexaoDB;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage palco) {

        conexaoDB = ConexaoDB.conectar();
        produtoDAO = new ProdutoDAO(conexaoDB);
        produtos = FXCollections.observableArrayList(produtoDAO.listarTodos());

        palco.setTitle("Gerenciamento de Estoque de Produtos");

        /* ===== FORMULÁRIO ===== */

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        HBox nomeBox = criarCampo("Produto:", nomeInput = new TextField());
        HBox quantidadeBox = criarCampo("Quantidade:", quantidadeInput = new TextField());
        HBox precoBox = criarCampo("Preço:", precoInput = new TextField());

        statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll(
                "Estoque Alto",
                "Estoque Normal",
                "Estoque Baixo"
        );

        HBox statusBox = new HBox(10, new Label("Status:"), statusComboBox);
        statusBox.setAlignment(Pos.CENTER_LEFT);

        /* ===== BOTÕES ===== */

        Button addButton = new Button("Adicionar");
        Button updateButton = new Button("Atualizar");
        Button deleteButton = new Button("Excluir");
        Button clearButton = new Button("Limpar");
        Button deleteAllButton = new Button("Excluir tudo");

        HBox buttonBox = new HBox(10,
                addButton, updateButton, clearButton, deleteButton, deleteAllButton
        );

        /* ===== TABLE VIEW ===== */

        tableView = new TableView<>();
        tableView.setItems(produtos);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        tableView.getColumns().addAll(List.of(
                criarColuna("ID", "id"),
                criarColuna("Produto", "nome"),
                criarColuna("Quantidade", "quantidade"),
                criarColuna("Preço", "preco"),
                criarColuna("Status", "status")
        ));

        VBox.setVgrow(tableView, Priority.ALWAYS);

        /* ===== EVENTOS ===== */

        addButton.setOnAction(e -> adicionarProduto());
        updateButton.setOnAction(e -> atualizarProduto());
        deleteButton.setOnAction(e -> excluirProduto());
        deleteAllButton.setOnAction(e -> excluirTodos());
        clearButton.setOnAction(e -> limparCampos());

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null) {
                nomeInput.setText(n.getNome());
                quantidadeInput.setText(String.valueOf(n.getQuantidade()));
                precoInput.setText(String.valueOf(n.getPreco()));
                statusComboBox.setValue(n.getStatus());
            }
        });

        vbox.getChildren().addAll(
                nomeBox, quantidadeBox, precoBox, statusBox, buttonBox, tableView
        );

        BorderPane root = new BorderPane(vbox);
        Scene cena = new Scene(root, 800, 600);
        cena.getStylesheets().add("styles-produtos.css");

        palco.setScene(cena);
        palco.show();
    }

    /* ===== MÉTODOS CRUD ===== */

    private void adicionarProduto() {
        if (!validarCampos()) return;

        Produto produto = criarProduto();
        produtoDAO.inserir(produto);
        atualizarTabela();
        limparCampos();
    }

    private void atualizarProduto() {
        Produto selecionado = tableView.getSelectionModel().getSelectedItem();
        if (selecionado == null || !validarCampos()) return;

        Produto novo = criarProduto();
        selecionado.setNome(novo.getNome());
        selecionado.setQuantidade(novo.getQuantidade());
        selecionado.setPreco(novo.getPreco());
        selecionado.setStatus(novo.getStatus());

        produtoDAO.atualizar(selecionado);
        atualizarTabela();
        limparCampos();
    }

    private void excluirProduto() {
        Produto selecionado = tableView.getSelectionModel().getSelectedItem();
        if (selecionado == null) return;

        produtoDAO.excluir(selecionado.getId());
        atualizarTabela();
        limparCampos();
    }

    private void excluirTodos() {
        produtoDAO.excluirTodos();
        atualizarTabela();
        limparCampos();
    }

    /* ===== UTILITÁRIOS ===== */

    private Produto criarProduto() {
        double preco = Double.parseDouble(precoInput.getText().replace(',', '.'));
        return new Produto(
                nomeInput.getText(),
                Integer.parseInt(quantidadeInput.getText()),
                preco,
                statusComboBox.getValue()
        );
    }

    private void atualizarTabela() {
        produtos.setAll(produtoDAO.listarTodos());
    }

    private boolean validarCampos() {
        if (nomeInput.getText().isEmpty() ||
                quantidadeInput.getText().isEmpty() ||
                precoInput.getText().isEmpty() ||
                statusComboBox.getValue() == null) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText(null);
            alert.setContentText("Preencha todos os campos!");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    private void limparCampos() {
        nomeInput.clear();
        quantidadeInput.clear();
        precoInput.clear();
        statusComboBox.setValue(null);
        tableView.getSelectionModel().clearSelection();
    }

    private HBox criarCampo(String label, TextField campo) {
        HBox box = new HBox(10, new Label(label), campo);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private TableColumn<Produto, String> criarColuna(String titulo, String propriedade) {
        TableColumn<Produto, String> col = new TableColumn<>(titulo);
        col.setCellValueFactory(new PropertyValueFactory<>(propriedade));
        return col;
    }

    @Override
    public void stop() {
        try {
            if (conexaoDB != null && !conexaoDB.isClosed()) {
                conexaoDB.close();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }
}