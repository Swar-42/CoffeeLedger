import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.h2.tools.RunScript;


public class Database {
    
    /**
     * load the database, initializing it if it does not exist, and returning the db connection
     * @return the connection to the database
     * @throws ClassNotFoundException h2 driver cannot be found
     * @throws SQLException cannot get connection with DriverManager or db cannot be initialized
     * @throws IOException db cannot be initialized
     */
    public static Connection load() throws ClassNotFoundException, SQLException, IOException {
        Class.forName("org.h2.Driver");
        Connection conn;
        try {
            conn = DriverManager.getConnection("jdbc:h2:./coffeeledger.db;ifexists=true");
        } catch (SQLException e) {
            // if db does not exist
            conn = DriverManager.getConnection("jdbc:h2:./coffeeledger.db");
            init(conn);
        }
        return conn;
    }

    /**
     * initializes the database at conn
     * @param conn - connection from which to initialize the database.
     * @throws SQLException 
     * @throws IOException 
     */
    public static void init(Connection conn) throws SQLException, IOException {
        // initialize script
        InputStreamReader isr = new InputStreamReader(new FileInputStream("resources/init.sql"));
        RunScript.execute(conn, isr);
        isr.close();
    }
}
