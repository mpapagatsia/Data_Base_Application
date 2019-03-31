
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class DBConnection {
    private Connection conn;
    private static DBConnection stable_connection = null;

    public DBConnection(){
        conn = null;
    }

    /*Creates the connection to the Database*/
    public Boolean createConnection(String user, String password){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
            System.out.println("New instance");
        } catch (Exception ex) {
            // handle the error
            return false;
        }

        try {
            String connectionUrl = "jdbc:mysql://localhost:3306/employees?"+ "allowPublicKeyRetrieval=true&"+
                    "useSSL=false&"+"useUnicode=true&"+"useJDBCCompliantTimezoneShift=true&"+
                    "useLegacyDatetimeCode=false&"+"serverTimezone=UTC&"+ "user="+ user +"&password="+ password;
            conn = DriverManager.getConnection(connectionUrl);
            System.out.println("Connection Established!\n"+ conn);

        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            return false;
        }
        stable_connection = this;
        return true;
    }

    /*Returns the DBconnection object*/
    public static DBConnection getInstance(){
        if (stable_connection == null){
            stable_connection = new DBConnection();
        }

        return stable_connection;
    }

    public Connection getConnection(){
        return conn;
    }

    public void setConnection(Connection conn){
        this.conn = conn;
    }
}
