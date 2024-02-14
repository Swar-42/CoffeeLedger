import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.h2.tools.RunScript;


public class CoffeeLedger {

    private Connection conn;
    private static CoffeeLedger instance = null;
    
    private CoffeeLedger() {
        try {
            load();
        } catch (ClassNotFoundException | SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public static CoffeeLedger getInstance() {
        if (instance == null) {
            instance = new CoffeeLedger();
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
     * 
     * @param name name of the group order
     * @return a map of person name to their order's cost
     */
    public Map<String, Double> getGroupOrder(String name) throws SQLException {
        int groupOrderId = getId(name, "group_orders");
        Statement stmt = conn.createStatement();
        String sql = 
          "SELECT p.name, o.price FROM people AS p "
        + "  JOIN group_order_details AS gd ON p.id = gd.person_id "
        + "  JOIN orders AS o ON o.id = gd.order_id "
        + "WHERE gd.group_order_id = " + groupOrderId + ";";
        ResultSet rs = stmt.executeQuery(sql);
        Map<String, Double> out = new HashMap<String, Double>();
        while (rs.next()) {
            out.put(rs.getString(1), rs.getDouble(2));
        }
        
        stmt.close();
        rs.close();
        return out;
    }

    /**
     * updates the people table, adding cost to name's bought column
     * @param name name of the person whose row will be updated
     * @param cost dollar amount to add to bought column
     */
    public void addBought(String name, double cost) throws SQLException {
        int personId = getId(name, "people");
        Statement stmt = conn.createStatement();
        String sql = 
          "UPDATE people SET "
        + "  bought = bought + " + cost + " "
        + "WHERE id = " + personId + ";";
        stmt.execute(sql);

        stmt.close();
    }

    public void addPaid(String name, double cost) throws SQLException {
        int personId = getId(name, "people");
        Statement stmt = conn.createStatement();
        String sql = 
          "UPDATE people SET "
        + "  paid = paid + " + cost + " "
        + "WHERE id = " + personId + ";";
        stmt.execute(sql);

        stmt.close();
    }

    /**
     * calculates the person (from the people table) with the most debt (bought - paid)
     * @return
     * @throws SQLException
     */
    public String mostDebt() throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = 
          "SELECT name FROM people "
        + "WHERE (bought - paid) = "
        + "  (SELECT MAX(bought - paid) FROM people)"
        + "ORDER BY RAND()"
        + "LIMIT 1;";
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        String out = rs.getString(1);

        stmt.close();
        rs.close();
        return out;
    }

    public double getDebt(String name) throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = 
          "SELECT (bought - paid) FROM people "
        + "WHERE name = \'" + name + "\';";
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        double out = rs.getDouble(1);

        stmt.close();
        rs.close();
        return out;
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
            System.out.println(String.format("%s) %-30s : $%,.2f", rs.getString(1), rs.getString(2), rs.getDouble(3)));
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

    public void setToPay(String name) throws SQLException {
        int personId = getId(name, "people");
        Statement stmt = conn.createStatement();
        String sql = "UPDATE stored_vars SET person_to_pay = " + personId + ";";
        stmt.execute(sql);
        stmt.close();   
    }

    public String getToPay() throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = 
          "SELECT p.name FROM stored_vars AS sv "
        + "  JOIN people AS p ON sv.person_to_pay = p.id";
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        String out = rs.getString(1);
        
        rs.close();
        stmt.close();
        return out;
    }

    public List<String> getOrderNames() throws SQLException {
        return getColumn("name", "orders");
    }

    public List<String> getOrderPrices() throws SQLException {
        return getColumn("price", "orders");
    }

    public List<String> getPeopleNames() throws SQLException {
        return getColumn("name", "people");
    }

    public List<String> getPeopleBoughtAmts() throws SQLException {
        return getColumn("bought", "people");
    }

    public List<String> getPeoplePaidAmts() throws SQLException {
        return getColumn("paid", "people");
    }

    private List<String> getColumn(String colName, String tableName) throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = "SELECT " + colName + " FROM " + tableName + ";";
        ResultSet rs = stmt.executeQuery(sql);
        List<String> out = new ArrayList<String>();
        while (rs.next()) {
            out.add(rs.getString(1));
        }

        stmt.close();
        rs.close();
        return out;
    }

    /**
     * clears all tables of all rows
     * @throws SQLException
     */
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

    /**
     * checks if the provided data is in the given table
     * @param dataName name of data to check existence of
     * @param tableName table to check
     * @return True if data is in table, false otherwise
     * @throws SQLException
     */
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

    /**
     * gets the id of the data with 'name' in 'tableName'
     * @param name name of the data to get the id for
     * @param tableName table to check
     * @return id as an integer
     * @throws SQLException
     */
    private int getId(String name, String tableName) throws SQLException {
        if (!dataExists(name, tableName)) {
            throw new IllegalArgumentException(name + " not found in table " + tableName);
        }

        Statement stmt = conn.createStatement();
        String sql = 
          "SELECT id FROM " + tableName + " WHERE name = \'" + name + "\';";
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        int out = rs.getInt(1);

        stmt.close();
        rs.close();
        return out;
    }

    private void runScript(String filepath) throws SQLException, IOException {
        InputStreamReader isr = new InputStreamReader(new FileInputStream(filepath));
        RunScript.execute(conn, isr);
        isr.close();
    }
}
