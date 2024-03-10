package application.NavigazioneService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author raffy
 */
public class NavigazioneService {
    public void visualizzaProdotto(Prodotto prod){        
    }
    public Prodotto getProductByCodice(int codice){   
        Prodotto product = null;
        String sql = "SELECT * FROM prodotto WHERE CodiceProdotto=?";
        DriverManagerConnectionPool dm = new DriverManagerConnectionPool();
        try{ 
            Connection conn = dm.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(sql);            
            preparedStatement.setInt(1, codice);
            ResultSet resultSet = preparedStatement.executeQuery();           
            if (resultSet.next()) {
                product = new Prodotto();
                product.setCodice(resultSet.getInt("CodiceProdotto"));
                product.setNome(resultSet.getString("Nome"));
                product.setTop_descrizione(resultSet.getString("TopDescrizione"));
                product.setPrezzo(resultSet.getDouble("Prezzo"));           
            }
            dm.releaseConnection(conn);
        } catch (SQLException e) {
            e.printStackTrace();           
        }
        return product;
    }
}
