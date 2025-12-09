
import java.sql.Connection;
import java.util.List;

public class Main3 {
    public static void main(String[] args) {
        try (Connection conexao = ConexaoDB.conectar()) {
            ProdutoDAO produtoDAO = new ProdutoDAO(conexao);

            // Lista todos os produtos
            mostrarProdutos(produtoDAO);

            //Excluir por ID
            produtoDAO.excluir(3);

            //Excluir Todos
            //produtoDAO.excluirTodos();

            System.out.println("Lista após excluir todos");
            // Lista todos os produtos
            mostrarProdutos(produtoDAO);


        } catch (Exception e) {
            System.err.println("Erro geral: " + e.getMessage());
        }
    }

    // Método para listar os produtos
    private static void mostrarProdutos(ProdutoDAO produtoDAO) {
        List<Produto> todosProdutos = produtoDAO.listarTodos();
        if (todosProdutos.isEmpty()) {
            System.out.println("Nenhum produto encontrado.");
        } else {
            System.out.println("Lista de produtos:");
            for (Produto p : todosProdutos) {
                System.out.println(p.getId() + ": " + p.getNome() + " - " + p.getPreco());
            }
        }
    }
}