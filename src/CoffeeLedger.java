import java.io.IOException;
import java.sql.SQLException;

public class CoffeeLedger {




    public static void main(String[] args) {
        Database db = new Database();
        try {
            // db.addPerson("John");
            // db.addPerson("Mike");
            // db.addPerson("Chris");

            db.printPeople();
            // db.addOrder("Large iced coffee", 4.00);
            // db.addOrder("small hot coffee", 2.25);
            db.printOrders();

            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
