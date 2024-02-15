package packages.topics;

import java.sql.SQLException;

import packages.input.Input;

public class OrderMenu extends TopicMenu {

    @Override
    protected void setTopicType() {
        type = TopicType.ORDER;
    }

    @Override
    protected void addItem(String name) {
        double price = Input.getPosDouble("Enter the price for order \"" + name + "\": ");
        try {
            db.addOrder(name, price);
            System.out.println(String.format("Order \"%s\" : $%,.2f added.", name, price));
        } catch (SQLException e) {
            System.err.println("Unable to add order \"" + name + "\" with price " + String.format("%,.2f", price));
            e.printStackTrace();
            return;
        }
    }
    
}
