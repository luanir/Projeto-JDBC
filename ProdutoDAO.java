import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {
    private final Connection CONEXAO_DB;

    //Contrutor que inicializa a conexão com o banco de dados
    public ProdutoDAO(Connection conexao) {
        this.CONEXAO_DB = conexao;
    }

    //Metodo para inserir um novo produto no banco de dados
    public void inserir(Produto produto) {
        String sql = "INSERT INTO produtos (nome_produto, quantidade, preco, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = CONEXAO_DB.prepareStatement(sql)){
            stmt.setString(1, produto.getNome());
            stmt.setInt(2, produto.getQuantidade());
            stmt.setDouble(3, produto.getPreco());
            stmt.setString(4, produto.getStatus());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao inserir produto: " + e.getMessage());
        }
    }

    //Metodo para excluir todos os produtos do banco de dados
    public void excluirTodos() {
        String sql = "DELETE FROM produtos";
        try (PreparedStatement stmt = CONEXAO_DB.prepareStatement(sql)){
            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Erro na exclusao de todos os produtos: " + ex.getMessage());
        }
    }
    //Metodo para consultar um produto pelo seu ID
    public Produto consultarPorId(int id) {
    String sql = "SELECT * FROM produtos WHERE id_produto = ?";
    try (PreparedStatement stmt = CONEXAO_DB.prepareStatement(sql)) {
        stmt.setInt(1, id); //Definindo o parametro do id antes de executar a consulta
        try(ResultSet rs = stmt.executeQuery()) {
            if(rs.next()) {
                    Produto produto = new Produto();
                    produto.setId(rs.getInt("id_produto"));
                    produto.setNome(rs.getString("nome_produto"));
                    produto.setQuantidade(rs.getInt("quantidade"));
                    produto.setPreco(rs.getDouble("preco"));
                    produto.setStatus(rs.getString("status"));
                    return produto;
                }
        } 
         } catch (SQLException e) {
            System.err.println("Erro ao consultar por ID: " + e.getMessage());
         }
      return null;
    }

    //Metodo para atualizar as informações de um produto no banco de dados
    public void atualizar(Produto produto) {
        String sql = "UPDATE produtos SET nome_produto = ?, quantidade = ?, preco = ?, status = ? WHERE id_produto = ?";
        try(PreparedStatement stmt = CONEXAO_DB.prepareStatement(sql)) {
            stmt.setString(1, produto.getNome());
            stmt.setInt(2, produto.getQuantidade());
            stmt.setDouble(3, produto.getPreco());
            stmt.setString(4, produto.getStatus());
            stmt.setInt(5, produto.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar o produto: " + e.getMessage());
        }
    }
    //Metodo para excluir um produto pelo seu id
    public void excluir(int id){
        String sql = "DELETE FROM produtos WHERE id_produto = ?";
        try(PreparedStatement stmt = CONEXAO_DB.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao excluir produto: " + e.getMessage());
        }
    }
    //Metodo para listar todos os produtos do banco de dados
    public List<Produto> listarTodos() {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT * FROM produtos";
        try (PreparedStatement stmt = CONEXAO_DB.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Produto produto = new Produto();
                produto.setId(rs.getInt("id_produto"));
                produto.setNome(rs.getString("nome_produto"));
                produto.setQuantidade(rs.getInt("quantidade"));
                produto.setPreco(rs.getDouble("preco"));
                produto.setStatus(rs.getString("status"));
                produtos.add(produto);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar produtos: " + e.getMessage());
        }
        return produtos;
    }
}