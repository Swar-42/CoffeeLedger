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

    private Connection conn;
    
    public Database() {
        try {
            load();
        } catch (ClassNotFoundException | SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public void close() throws SQLException {
        conn.close();
    }

    /**
     * load the database, initializing it if it does not exist, and returning the db connection
     * @return the connection to the database
     * @throws ClassNotFoundException h2 driver cannot be found
     * @throws SQLException cannot get connection with DriverManager or db cannot be initialized
     * @throws IOException db cannot be initialized
     */
    public void load() throws ClassNotFoundException, SQLException, IOException {
        Class.forName("org.h2.Driver");
        try {
            conn = DriverManager.getConnection("jdbc:h2:./coffeeledger.db;ifexists=true");
        } catch (SQLException e) {
            // if db does not exist
            conn = DriverManager.getConnection("jdbc:h2:./coffeeledger.db");
            init();
        }
    }

    /**
     * initializes the database
     * @throws SQLException 
     * @throws IOException 
     */
    public void init() throws SQLException, IOException {
        // initialize script
        runScript("resources/init.sql");
    }

    /**
     * adds row to the 'people' table
     * @param name - name of the person to add
     * @return id of the person added
     * @throws SQLException 
     */
    public void addPerson(String name) throws SQLException {
        Statement stmt = conn.createStatement();
        // make sure name does not exist in table
        String sql = 
          "SELECT \'" + name + "\' IN "
        + "  (SELECT name FROM people);"; 
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        String s = rs.getString(1);
        if (s.equals("TRUE")) {
            throw new IllegalArgumentException(name + " already exists in table people");
        }

        sql = 
          "INSERT INTO people (name, bought, paid) "
        + "VALUES (\'" + name + "\', 0, 0);";
        stmt.execute(sql);
    }

    /**
     * prints the people table
     * @throws SQLException
     */
    public void printPeople() throws SQLException {
        Statement stmt = conn.createStatement();
        String query = "SELECT * FROM people";
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            System.out.println(rs.getString(1) + " " + rs.getString(2) + " " + rs.getString(3) + " " + rs.getString(4));
        }
        rs.close();
        stmt.close();
    }

    /**
     * adds row to the 'orders' table
     * @param name name of the order
     * @param price price of the order
     * @throws SQLException
     */
    public void addOrder(String name, double price) throws SQLException {
        Statement stmt = conn.createStatement();
        // make sure name does not exist in table
        String sql = 
          "SELECT \'" + name + "\' IN "
        + "  (SELECT name FROM orders);"; 
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        String s = rs.getString(1);
        if (s.equals("TRUE")) {
            throw new IllegalArgumentException(name + " already exists in table orders");
        }

        sql = 
          "INSERT INTO orders (name, price) "
        + "VALUES (\'" + name + "\', " + price + ");";
        stmt.execute(sql);
    }

    /**
     * prints the orders table
     * @throws SQLException
     */
    public void printOrders() throws SQLException {
        Statement stmt = conn.createStatement();
        String query = "SELECT * FROM orders";
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            System.out.println(rs.getString(1) + " " + rs.getString(2) + " " + rs.getString(3));
        }
        rs.close();
        stmt.close();
    }

    private void runScript(String filepath) throws SQLException, IOException {
        InputStreamReader isr = new InputStreamReader(new FileInputStream(filepath));
        RunScript.execute(conn, isr);
        isr.close();
    }
}
