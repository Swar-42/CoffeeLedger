import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CoffeeLedger {




    public static void main(String[] args) {
        Database db = Database.getInstance();
        try {
            db.clear();

            db.addPerson("John");
            db.addPerson("Mike");
            db.addPerson("Chris");

            db.printPeople();
            db.addOrder("Large iced coffee", 4.00);
            db.addOrder("Small hot coffee", 2.25);
            db.printOrders();

            Map<String, String> groupOrder = new HashMap<String, String>();
            groupOrder.put("John", "Large iced coffee");
            groupOrder.put("Chris", "Small hot coffee");
            groupOrder.put("Mike", "Large iced coffee");
            db.addGroupOrder("Order 1", groupOrder);
            db.printGroupOrders();
            System.out.println("details:");
            db.printGroupOrderDetails();

            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
