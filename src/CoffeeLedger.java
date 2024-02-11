import java.io.IOException;
import java.sql.SQLException;

public class CoffeeLedger {


    public static void main(String[] args) {
        try {
            Database.load();
        } catch (ClassNotFoundException | SQLException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
