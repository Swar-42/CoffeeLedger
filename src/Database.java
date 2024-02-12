import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import org.h2.tools.RunScript;


public class Database {

    private Connection conn;
    private static Database instance = null;
    
    private Database() {
        try {
            load();
        } catch (ClassNotFoundException | SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
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
        if (dataExists(name, "people")) {
            throw new IllegalArgumentException(name + " already exists in table people");
        }

        Statement stmt = conn.createStatement();
        String sql = 
          "INSERT INTO people (name, bought, paid) "
        + "VALUES (\'" + name + "\', 0, 0);";
        stmt.execute(sql);

        stmt.close();
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
        if (dataExists(name, "orders")) {
            throw new IllegalArgumentException(name + " already exists in table orders");
        }
        
        Statement stmt = conn.createStatement();
        String sql = 
          "INSERT INTO orders (name, price) "
        + "VALUES (\'" + name + "\', " + price + ");";
        stmt.execute(sql);

        stmt.close();
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

    /**
     * adds a group order to the database, updating group_orders and group_order_details
     * @param name - name of the group order
     * @param personOrderMap - map of person name to order name for each person ordering
     * @throws SQLException
     */
    public void addGroupOrder(String name, Map<String, String> personOrderMap) throws SQLException{
        if (dataExists(name, "group_orders")) {
            throw new IllegalArgumentException(name + " already exists in table group_orders");
        }
        // add to group_orders
        Statement stmt = conn.createStatement();
        String sql = 
          "INSERT INTO group_orders (name) "
        + "VALUES (\'" + name + "\');";
        stmt.execute(sql);

        // add to group_order_details
        for (String person : personOrderMap.keySet()) {
            int personId = getId(person, "people");
            int orderId = getId(personOrderMap.get(person), "orders");
            int nameId = getId(name, "group_orders");
            sql = 
              "INSERT INTO group_order_details (group_order_id, person_id, order_id) "
            + "VALUES (" + nameId + ", " + personId + ", " + orderId + ")";
            stmt.execute(sql);
        }

        stmt.close();
    }

    /**
     * prints the group_orders table
     * @throws SQLException
     */
    public void printGroupOrders() throws SQLException {
        Statement stmt = conn.createStatement();
        String query = "SELECT * FROM group_orders";
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            System.out.println(rs.getString(1) + " " + rs.getString(2));
        }
        rs.close();
        stmt.close();
    }

    /**
     * prints the group_order_details table
     * @throws SQLException
     */
    public void printGroupOrderDetails() throws SQLException {
        Statement stmt = conn.createStatement();
        String query = "SELECT * FROM group_order_details";
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            System.out.println(rs.getString(1) + " " + rs.getString(2) + " " + rs.getString(3));
        }
        rs.close();
        stmt.close();
    }

    public void clear() throws SQLException{
        Statement stmt = conn.createStatement();
        String sql = 
          "DELETE FROM group_order_details WHERE TRUE; "
        + "DELETE FROM group_orders WHERE TRUE; "
        + "DELETE FROM orders WHERE TRUE; "
        + "DELETE FROM people WHERE TRUE; ";
        stmt.execute(sql);
        stmt.close();
    }

    private boolean dataExists(String dataName, String tableName) throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = 
          "SELECT \'" + dataName + "\' IN "
        + "  (SELECT name FROM " + tableName + ");"; 
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        String s = rs.getString(1);

        stmt.close();
        rs.close();


        return (s.equals("TRUE"));
    }

    private int getId(String name, String tableName) throws SQLException {
        if (!dataExists(name, tableName)) {
            throw new IllegalArgumentException(name + " not found in table " + tableName);
        }

        Statement stmt = conn.createStatement();
        String sql = 
          "SELECT id FROM " + tableName + " WHERE name = \'" + name + "\';";
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        return rs.getInt(1);
    }

    private void runScript(String filepath) throws SQLException, IOException {
        InputStreamReader isr = new InputStreamReader(new FileInputStream(filepath));
        RunScript.execute(conn, isr);
        isr.close();
    }
}
