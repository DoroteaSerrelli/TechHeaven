package model;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class DriverManagerConnectionPool  {			
	private List<Connection> freeDbConnections;
	static {	
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
                        e.printStackTrace();
		} 
	}	
	public DriverManagerConnectionPool() {
		freeDbConnections = new LinkedList<>();
	}
	
    private synchronized Connection createDBConnection() throws SQLException {			        
        Connection newConnection = null;
	String ip = "localhost";
        String port = "3306";
        String db = "techheaven";        
        try{
            InputStream propsInput = getClass().getResourceAsStream("/config.properties");
            Properties pr = new Properties();
            pr.load(propsInput);              
            String username = pr.getProperty("DB_USER");
            String password = pr.getProperty("DB_PASSWORD");      
            newConnection = DriverManager.getConnection("jdbc:mysql://"+ ip+":"+ port+"/"+db+"?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", username, password);
            newConnection.setAutoCommit(false);
            return newConnection;                
	}
        catch(IOException | SQLException e){     
             e.printStackTrace(); // or use a logger to log the exception
            return null;
        }
    }

	public synchronized Connection getConnection() throws SQLException {
		Connection connection;

		if (!freeDbConnections.isEmpty()) {
			connection = (Connection) freeDbConnections.get(0);
			freeDbConnections.remove(0);

			try {
				if (connection.isClosed())
					connection = getConnection();
			} catch (SQLException e) {
				connection.close();
				connection = getConnection();
			}
		} else {
			connection = createDBConnection();		
		}

		return connection;
	}

	public synchronized void releaseConnection(Connection connection) throws SQLException {
		if(connection != null) freeDbConnections.add(connection);
	}
}
