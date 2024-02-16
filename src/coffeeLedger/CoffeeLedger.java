package coffeeLedger;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.h2.tools.RunScript;

import topics.GroupOrderItem;
import topics.OrderItem;
import topics.PersonItem;
import topics.TopicItem;

/**
 * Singleton class which directly interfaces with the h2 database and issues it SQL commands.
 */
public class CoffeeLedger {
    public static final String[] COLUMN_LABELS = new String[]{"name", "bought", "paid", "price"};
    // the names of the columns which are accessible by an outsider
    public static final Set<String> accessibleColumns = new HashSet<String>(Arrays.asList(COLUMN_LABELS));
    public static final String[] TABLE_NAMES = new String[]{"people", "orders", "group_orders"};
    // the names of the tables which are accessible by an outsider
    public static final Set<String> accessibleTables = new HashSet<String>(Arrays.asList(TABLE_NAMES));

    // the connection to the database
    private Connection conn;
    // the singleton instance of this
    private static CoffeeLedger instance = null;
    
    private CoffeeLedger() {
        try {
            load();
        } catch (ClassNotFoundException | SQLException | IOException e) {
            System.err.println("Unable to load database");
            e.printStackTrace();
        }
    }

    /*
     * gets the singleton instance of the class.
     */
    public static CoffeeLedger getInstance() {
        if (instance == null) {
            instance = new CoffeeLedger();
        }
        return instance;
    }

    /**
     * closes the database connection
     * @throws SQLException
     */
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
        runScript("/init.sql");
    }

    /**
     * Returns the group order as a map of person name to their order's price.
     * @param name name of the group order
     * @return a map of person name to their order's price
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

    /**
     * Adds the given amount to the person's 'paid' total
     * @param name - name of person to modify
     * @param cost - price to add to their 'paid' column
     * @throws SQLException
     */
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
     * Adds the corresponding price for each item in the group order to the proper person's 'bought' column
     * @param groupOrderName - name of the group order to process
     * @throws SQLException
     */
    public void updateBought(String groupOrderName) throws SQLException {
        int id = getId(groupOrderName, "group_orders");
        Statement stmt = conn.createStatement();
        String sql = 
          "UPDATE people p_out SET "
        + "p_out.bought = "
        + "  (SELECT (p.bought + o.price) "
        + "  FROM people AS p "
        + "    JOIN group_order_details AS gd ON p.id = gd.person_id "
        + "    JOIN group_orders AS g ON g.id = gd.group_order_id "
        + "    JOIN orders AS o ON o.id = gd.order_id "
        + "  WHERE g.id = " + id + " AND p_out.id = p.id) "
        + "WHERE p_out.id IN "
        + "  (SELECT gd.person_id "
        + "  FROM group_order_details AS gd "
        + "  WHERE gd.group_order_id = " + id + ");";
        stmt.execute(sql);

        stmt.close();
    }

    /**
     * calculates the person (from the people table) with the most debt (bought - paid)
     * @return name of the person with the most debt.
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

    /**
     * calculates and returns the debt of the provided person.
     * @param name name of the person to calculate debt
     * @return amount of debt for provided person
     * @throws SQLException
     */
    public double getDebt(String name) throws SQLException {
        int id = getId(name, "people");
        Statement stmt = conn.createStatement();
        String sql = 
          "SELECT (bought - paid) FROM people "
        + "WHERE id = " + id + ";";
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        double out = rs.getDouble(1);

        stmt.close();
        rs.close();
        return out;
    }

    /**
     * calculates and returns the total cost of the given group order
     * @param groupOrderName name of the group order to calculate cost with
     * @return the total cost of the group order
     * @throws SQLException
     */
    public double getCost(String groupOrderName) throws SQLException {
        int id = getId(groupOrderName, "group_orders");
        Statement stmt = conn.createStatement();
        String sql = 
          "SELECT SUM(o.price) FROM orders AS o "
        + "  JOIN group_order_details AS gd ON o.id = gd.order_id "
        + "  JOIN group_orders AS g ON g.id = gd.group_order_id "
        + "WHERE g.id = " + id + ";";
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        double cost = rs.getDouble(1);

        stmt.close();
        rs.close();
        return cost;
    }

    /**
     * returns an entire column of a given table as a list of strings
     * @param colName name of the column to get
     * @param tableName name of the table to get
     * @return the entire column queried as a list of strings
     * @throws SQLException
     */
    public List<String> getColumn(String colName, String tableName) throws SQLException {
        if (!validTable(tableName) || !validColumn(colName)) {
            throw new IllegalArgumentException("Invalid table name (" + tableName + ") or column name (" + colName + ")");
        }

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
     * returns the value at rowName and colName in tableName as a String
     * @param rowName name of the row from which to get the value
     * @param colName name of the column from which to get the value
     * @param tableName name of the table from which to get the value
     * @return the value at the specified row, column, and table
     * @throws SQLException
     */
    public String getValue(String rowName, String colName, String tableName) throws SQLException {
        if (!validTable(tableName) || !validColumn(colName)) {
            throw new IllegalArgumentException("Invalid table name (" + tableName + ") or column name (" + colName + ")");
        }

        int id = getId(rowName, tableName);
        Statement stmt = conn.createStatement();
        String sql = 
          "SELECT " + colName + " FROM " + tableName + " "
        + "WHERE id = " + id + ";";
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        String out = rs.getString(1);

        rs.close();
        stmt.close();
        return out;
    }

    /**
     * updates the value at the provided row, column, and table with a new String value
     * @param rowName name of the row from which to update a value
     * @param colName name of the column from which to update a value
     * @param tableName name of the table from which to update a value
     * @param newValue name of the table from which to update a value
     * @throws SQLException
     */
    public void changeValue(String rowName, String colName, String tableName, String newValue) throws SQLException {
        if (!validTable(tableName) || !validColumn(colName)) {
            throw new IllegalArgumentException("Invalid table name (" + tableName + ") or column name (" + colName + ")");
        }
        
        int id = getId(rowName, tableName);
        Statement stmt = conn.createStatement();
        String sql = 
          "UPDATE "+ tableName + " SET "
        +  colName + " = " + newValue + " "
        + "WHERE id = " + id + ";";
        stmt.execute(sql);

        stmt.close();
    }

    /**
     * removes the row with the specified name from the specified table
     * @param rowName the name of the row to remove
     * @param tableName the table from which to remove a row
     * @throws SQLException
     */
    public void removeRow(String rowName, String tableName) throws SQLException {
        if (!validTable(tableName)) {
            throw new IllegalArgumentException("Invalid table name (" + tableName + ")");
        }

        int id = getId(rowName, tableName);
        Statement stmt = conn.createStatement();
        String sql = 
          "DELETE FROM " + tableName + " "
        + "WHERE id = " + id + ";";
        stmt.execute(sql);

        stmt.close();
    }

    /**
     * checks if the provided data is in the given table
     * @param colName name of the column to check
     * @param dataName name of data to check existence of
     * @param tableName table to check
     * @return true if data is in table, false otherwise
     * @throws SQLException
     */
    public boolean dataExists(String colName, String tableName, String dataName) throws SQLException {
        if (!validTable(tableName) || !validColumn(colName)) {
            throw new IllegalArgumentException("Invalid table name (" + tableName + ") or column name (" + colName + ").");
        }

        Statement stmt = conn.createStatement();
        String sql = 
          "SELECT " + dataName + " IN "
        + "  (SELECT " + colName + " FROM " + tableName + ");"; 
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        String s = rs.getString(1);

        stmt.close();
        rs.close();
        return (s.equals("TRUE"));
    }

    /**
     * adds row to the 'people' table
     * @param name - name of the person to add
     * @return id of the person added
     * @throws SQLException 
     */
    public void addPerson(String name) throws SQLException {
        if (dataExists("name", "people", "\'" + name + "\'")) {
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
        if (dataExists("name", "orders", "\'" + name + "\'")) {
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
        if (dataExists("name", "group_orders",  "\'" + name + "\'")) {
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

    /**
     * stores the given name as the person next up to pay for a group order
     * @param name name of the person to store (as an id)
     * @throws SQLException
     */
    public void setToPay(String name) throws SQLException {
        int personId = getId(name, "people");
        Statement stmt = conn.createStatement();
        String sql = "UPDATE stored_vars SET person_to_pay = " + personId + ";";
        stmt.execute(sql);
        stmt.close();   
    }

    /**
     * gets the stored person next up to pay for a group order
     * @return name of the person to pay
     * @throws SQLException
     */
    public String getToPay() throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = 
          "SELECT p.name "
        + "FROM people AS p "
        + "JOIN stored_vars AS sv ON sv.person_to_pay = p.id;";
        ResultSet rs = stmt.executeQuery(sql);

        String out = null;
        if (rs.next()) {
            out = rs.getString(1);
        }
        
        rs.close();
        stmt.close();
        return out;
    }

    /**
     * retrieves a row from the provided table, returning it as a TopicItem
     * @param tableName name of the table to query
     * @param name name of the item to get
     * @return the row containing 'name' as its name, as a TopicItem
     * @throws SQLException
     */
    public TopicItem getItem(String tableName, String name) throws SQLException {
        if (tableName.equals("people")) {
            return getPeopleItem(name);
        } else if (tableName.equals("orders")) {
            return getOrdersItem(name);
        } else if (tableName.equals("group_orders")) {
            return getGroupOrdersItem(name);
        }
        throw new IllegalArgumentException("invalid table name " + tableName + " for getting an item");
    }

    /**
     * retrieves a row from the 'people' table as a TopicItem
     * @param name name of the row to query
     * @return a row from 'people' as a TopicItem
     * @throws SQLException
     */
    private TopicItem getPeopleItem(String name) throws SQLException {
        int id = getId(name, "people");
        Statement stmt = conn.createStatement();
        String sql = 
          "SELECT name, bought, paid FROM people "
        + "WHERE id = " + id + ";";
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        List<String> values = new ArrayList<String>(); 
        values.add(rs.getString(1));
        values.add(rs.getString(2));
        values.add(rs.getString(3));

        stmt.close();
        rs.close();
        return new PersonItem(values);
    }

    /**
     * retrieves a row from the 'orders' table as a TopicItem
     * @param name name of the row to query
     * @return a row from 'orders' as a TopicItem
     * @throws SQLException
     */
    private TopicItem getOrdersItem(String name) throws SQLException {
        int id = getId(name, "orders");
        Statement stmt = conn.createStatement();
        String sql = 
          "SELECT name, price FROM orders "
        + "WHERE id = " + id + ";";
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        List<String> values = new ArrayList<String>(); 
        values.add(rs.getString(1));
        values.add(rs.getString(2));

        stmt.close();
        rs.close();
        return new OrderItem(values);
    }

    /**
     * retrieves a row from the 'group orders' table as a TopicItem
     * @param name name of the row to query
     * @return a row from 'group orders' as a TopicItem
     * @throws SQLException
     */
    private TopicItem getGroupOrdersItem(String name) throws SQLException {
        int id = getId(name, "group_orders");
        Statement stmt = conn.createStatement();
        String sql = 
          "SELECT name FROM group_orders "
        + "WHERE id = " + id + ";";
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        List<String> values = new ArrayList<String>(); 
        values.add(rs.getString(1));

        stmt.close();
        rs.close();
        return new GroupOrderItem(values);
    }

    /**
     * returns an entire table as a list of TopicItems
     * @param tableName the name of the table to query
     * @return all rows from the table as a list of TopicItems
     * @throws SQLException
     */
    public List<TopicItem> getTable(String tableName) throws SQLException {
        if (tableName.equals("people")) {
            return getPeopleTable();
        } else if (tableName.equals("orders")) {
            return getOrdersTable();
        } else if (tableName.equals("group_orders")) {
            return getGroupOrdersTable();
        }
        throw new IllegalArgumentException("invalid table name: " + tableName);
    }

    /**
     * retrieves relevant data from all rows of 'orders' as a list of TopicItems
     * @return all rows of 'orders' as a list of TopicItems
     * @throws SQLException
     */
    public List<TopicItem> getOrdersTable() throws SQLException {
        List<TopicItem> out = new ArrayList<TopicItem>();

        Statement stmt = conn.createStatement();
        String sql = "SELECT name, price FROM orders;";
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            List<String> row = new ArrayList<String>();
            row.add(rs.getString(1));
            row.add(rs.getString(2));
            out.add(new OrderItem(row));
        }

        rs.close();
        stmt.close();
        return out;
    }

    /**
     * retrieves relevant data from all rows of 'people' as a list of TopicItems
     * @return all rows of 'people' as a list of TopicItems
     * @throws SQLException
     */
    public List<TopicItem> getPeopleTable() throws SQLException {
        List<TopicItem> out = new ArrayList<TopicItem>();

        Statement stmt = conn.createStatement();
        String sql = "SELECT name, bought, paid FROM people;";
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            List<String> row = new ArrayList<String>();
            row.add(rs.getString(1));
            row.add(rs.getString(2));
            row.add(rs.getString(3));
            out.add(new PersonItem(row));
        }

        rs.close();
        stmt.close();
        return out;
    }

    /**
     * retrieves relevant data from all rows of 'group orders' as a list of TopicItems
     * @return all rows of 'group orders' as a list of TopicItems
     * @throws SQLException
     */
    public List<TopicItem> getGroupOrdersTable() throws SQLException {
        List<TopicItem> out = new ArrayList<TopicItem>();
        for (String name : getColumn("name", "group_orders")) {
            List<String> row = new ArrayList<String>();
            row.add(name);
            out.add(new GroupOrderItem(row));
        }

        return out;
    }

    /**
     * clears all tables of all rows (except stored_vars)
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
     * checks if the provided table name refers to a valid table in the database
     * @param tableName the provided table name
     * @return true if tableName is an accessible table, false otherwise
     */
    private boolean validTable(String tableName) {
        return accessibleTables.contains(tableName);
    }

    /**
     * checks if the provided column name refers to a valid column in the database
     * @param tableName the provided column name
     * @return true if colName is an accessible table, false otherwise
     */
    private boolean validColumn(String colName) {
        return accessibleColumns.contains(colName);
    }

    /**
     * gets the id of the data with 'name' in 'tableName'
     * @param name name of the data to get the id for
     * @param tableName table to check
     * @return id as an integer
     * @throws SQLException
     */
    private int getId(String name, String tableName) throws SQLException {
        if (!dataExists("name", tableName, "\'" + name + "\'")) {
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
        InputStreamReader isr = new InputStreamReader(getClass().getResourceAsStream(filepath));
        RunScript.execute(conn, isr);
        isr.close();
    }
}
