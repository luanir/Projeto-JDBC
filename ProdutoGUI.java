import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.sql.Connection;
import java.sql.SQLException;

public class ProdutoGUI extends Application {
    private ProdutoDAO produtoDAO;
    private  ObservableList<Produto> produtos;
    private TableView<Produto> tableView;
    private  TextField nomeInput, quantidadeInput, precoInput;
    private ComboBox<String> statusComboBox;
    private Connection conexaoDB;

    public static void main (String [] args){
        launch(args);
    }

    @Override
    public void start (Stage palco) {
        conexaoDB = ConexaoDB.conectar();
        produtoDAO = new ProdutoDAO(conexaoDB); //Inicializa o DAO
        produtos = FXCollections.observableArrayList(produtoDAO.listarTodos()); //Carrega todos os produtos do banco de dados

        palco.setTitle("Gerenciamento de Estoque de Produtos");

        //VBOX
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10, 10, 10, 10));
        vbox.setSpacing(10);
        
        //HBOX
        HBox nomeProdutoHbox = new HBox();
        nomeProdutoHbox.setSpacing(10);
        Label nomeLabel = new Label("Produto: ");
        nomeInput = new TextField();
        nomeProdutoHbox.getChildren().addAll(nomeLabel, nomeInput);
        //HBOX
        HBox quantidadeHbox = new HBox();
        quantidadeHbox.setSpacing(10);
        Label quantidadLabel = new Label("Quantidade: ");
        quantidadeInput = new TextField();
        quantidadeHbox.getChildren().addAll(quantidadLabel, quantidadeInput);
        //HBOX
        HBox precoHbox = new HBox();
        precoHbox.setSpacing(10);
        Label precoLabel = new Label("Preço: ");
        precoInput = new TextField();
        precoHbox.getChildren().addAll(precoLabel, precoInput);
        //HBOX
        HBox statusBox = new HBox();
        statusBox.setSpacing(10);
        Label statusLabel = new Label("Status: ");
        statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll("Estoque Alto",
        "Estoque Normal", "Estoque Baixo");
        statusBox.getChildren().addAll(statusLabel, statusComboBox);

        //BOTÂO Adicionar
        Button addButton = new Button("Adicionar");
        addButton.setOnAction(e ->{
            String preco = precoInput.getText().replace(',', '.'); //Substitui vírugla por ponto no preço
            Produto produto = new Produto(nomeInput.getText(),
               Integer.parseInt(quantidadeInput.getText()),
                 Double.parseDouble(preco),
                    statusComboBox.getValue());
                produtoDAO.inserir(produto); //Insere o novo produto no banco de dados
                produtos.setAll(produtoDAO.listarTodos());
                limparCampos(); //Limpa os campos de entrada para uma nova digitação
        });

        //BOTÂO Atualizar
        Button updateButton = new Button("Atualizar");
        updateButton.setOnAction(e->{
                Produto selectedProduto = tableView.getSelectionModel().getSelectedItem(); //Obtém o produto selecionado
                if (selectedProduto != null){
                    String preco = precoInput.getText().replace(',', '.'); //Substitui vírugla por ponto no preço
                    selectedProduto.setNome(nomeInput.getText());
                    selectedProduto.setQuantidade(Integer.parseInt(quantidadeInput.getText()));
                    selectedProduto.setPreco(Double.parseDouble(preco));
                    selectedProduto.setStatus(statusComboBox.getValue());
                    produtoDAO.atualizar(selectedProduto); //Atualiza o produto no banco de dados
                    produtos.setAll(produtoDAO.listarTodos());
                    limparCampos(); //Limpar campos de entradas
                }
        });

        //BOTÂO Excluir
        Button deleteButton = new Button("Excluir");
        deleteButton.setOnAction(e-> {
            Produto selectedProduto = tableView.getSelectionModel().getSelectedItem(); //Obtém o produto selecionado
            if (selectedProduto != null){
                produtoDAO.excluir(selectedProduto.getId()); //Exclui o produto do banco de dados
                produtos.setAll(produtoDAO.listarTodos()); //Atualiza a lista de produtos
                limparCampos();  //Limpar campos de entradas
            }
        });

        //BOTÂO Limpar
        Button clearButton = new Button ("Limpar");
        clearButton.setOnAction(e-> limparCampos());

        //BOTÃO Excluir tudo
        Button deleteAllButton = new Button("Excluir tudo");
        deleteAllButton.setOnAction(e-> {
            produtoDAO.excluirTodos();
            produtos.setAll(produtoDAO.listarTodos());
            limparCampos();
        });

        tableView = new TableView<>();
        tableView.setItems(produtos); //Define a lista de produtos na tabela
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS); //Ajusta o tamanho das colunas
        List<TableColumn<Produto, ?>> columns = List.of(
            criarColuna("ID", "id"),
            criarColuna("Produto", "nome"),
            criarColuna("Quantidade", "quantidade"),
            criarColuna("Preço", "preco"),
            criarColuna("Status", "status")
        );
        tableView.getColumns().addAll(columns);

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if(newSelection != null) {
                    nomeInput.setText(newSelection.getNome());
                    quantidadeInput.setText(String.valueOf(newSelection.getQuantidade()));
                    precoInput.setText(String.valueOf(newSelection.getPreco()));
                    statusComboBox.setValue(newSelection.getStatus());
                } 
        });

        HBox buttonBox = new HBox();
        buttonBox.setSpacing(10);
        buttonBox.getChildren().addAll(addButton, updateButton,  clearButton, deleteButton, deleteAllButton); //Adiciona o botão limpar ao hbox

        vbox.getChildren().addAll(nomeProdutoHbox, quantidadeHbox, precoHbox, statusBox, buttonBox, tableView);

        Scene cena = new Scene (vbox, 800, 600);
        cena.getStylesheets().add("styles-produtos.css"); //Adiciona folha de estilos
        palco.setScene(cena);
        palco.show();
    }

    /*Metodo stop é chamado automaticamente a aplicação javaFX é ENCERRADA.*/
    @Override
    public void stop(){
        try {
            conexaoDB.close();
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }
    
    /* Metodo de limpar o campo na entrada do form
    Este metodo e chamado apos adicionar atualizar ou excluir o produto
    para garantir que os campos de entrada estejam prontos para uma nova entrada */

    private void limparCampos(){
        nomeInput.clear();
        quantidadeInput.clear();
        precoInput.clear();
        statusComboBox.setValue(null);
    }

    /* Metodo criar coluna
    Cria uma coluna para o TableView
    @param title o titulo da coluna sera exibido no cabeçalho
    @param property A propriedade do objeto Produto que esta oluna deve exibir
    @return A coluna configurada para a tableView */

    private TableColumn<Produto, String> criarColuna(String title, String property) {
        TableColumn<Produto, String> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(property)); //Define a propriedade da coluna
        return col;
    }
}