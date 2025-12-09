import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class CriadorTabela {
    public static void main (String [] args){
        try (Connection conexao = ConexaoDB.conectar();
            Statement stmt = conexao.createStatement()) {
                //Definindo o comando sql para criar a tabela 
                String comandoSql = "CREATE TABLE produtos (" +
                "id_produto INTEGER PRIMARY KEY," +
                "nome_produto TEXT NOT NULL," +
                "quantidade INTEGER," +
                "preco REAL," +
                "status TEXT" +
                ");";
            System.out.println(comandoSql);
            //Executando o comando SQL
            stmt.execute(comandoSql);

            System.out.println("Tabela 'Produtos' criada com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro na criação da tabela: " + e.getMessage());
            e.printStackTrace();
        }
    }
}