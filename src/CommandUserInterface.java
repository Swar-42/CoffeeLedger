import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class CommandUserInterface {

    private static CoffeeLedger db = CoffeeLedger.getInstance();

    public static void mainMenu() {
        List<String> options = new ArrayList<String>();
        options.add("Who should pay next?");
        options.add("Process a group order");
        options.add("Add/Edit " + CoffeeTopic.PERSON.plural());
        options.add("Add/Edit " + CoffeeTopic.ORDER.plural());
        options.add("Add/Edit " + CoffeeTopic.GROUP_ORDER.plural());
        OptionSelect menu = new OptionSelect("Welcome to CoffeeLedger! Select an option below:", options, "Exit");
        int selection = menu.prompt();

        switch (selection) {
            case 1:
                System.out.println();
                promptNextPayer();
                System.out.println();
                break;
            case 2:
                System.out.println();
                processGroupOrder();
                System.out.println();
                break;
            case 3:
                System.out.println();
                peopleMenu();
                System.out.println();
                break;
            case 4:
                System.out.println();
                ordersMenu();
                System.out.println();
                break;
            case 5:
                System.out.println();
                groupOrdersMenu();
                System.out.println();
                break;
            case 0:
                System.out.println("Have a good day!");
                return;
        }

        mainMenu();
    }

    private static void groupOrdersMenu() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'editGroupOrders'");
    }

    private static void ordersMenu() {
        int choice = topicBaseChoice(CoffeeTopic.ORDER);
        switch (choice) {
            case 1:
                System.out.println();
                addOrder();
                System.out.println();
                break;
            case 2:
                System.out.println();
                editOrders();
                System.out.println();
                break;
            case 0:
                return;
        }

        ordersMenu();
    }

    private static void editOrders() {
        // display the orders
        List<String> orderNames;
        List<String> orderPrices;
        try {
            orderNames = db.getOrderNames();
            orderPrices = db.getOrderPrices();
        } catch (SQLException e) {
            System.err.println("SQL Error: unable to get orders information");
            e.printStackTrace();
            return;
        }
        List<String> allOrders = new ArrayList<String>();
        for (int i = 0; i < orderNames.size(); i++) {
            allOrders.add(String.format("%-30s : $%s", orderNames.get(i), orderPrices.get(i)));
        }
        OptionSelect orderSelect = new OptionSelect("Select an item from the above to edit.", allOrders, "Back");
        orderSelect.displayTitleAbove(false);
        int choice = orderSelect.prompt();

        if (choice == 0) return;
        // edit prompt
        System.out.println();
        editOrder(orderNames.get(choice-1), orderPrices.get(choice-1));
        System.out.println();

        editOrders();
    }

    private static void editOrder(String name, String price) {
        List<String> editSelection = new ArrayList<String>();
        editSelection.add("Edit name");
        editSelection.add("Edit price");
        editSelection.add("Delete order");
        OptionSelect editSelect = new OptionSelect(String.format("Selected: %s : $%s", name, price), editSelection, "Back");
        int choice = editSelect.prompt();

        switch (choice) {
            case 1:
                name = editOrderName(name);
                break;
            case 2:
                price = editOrderPrice(name, price);
                break;
            case 3:
                Boolean deleted = deleteOrder(name);
                if (deleted) {
                    return;
                }
                break;
            case 0:
                return;
        }

        System.out.println();
        editOrder(name, price);
    }

    private static Boolean deleteOrder(String name) {
        BooleanSelect confirm = new BooleanSelect("Are you sure you want to delete the order \"" + name + "\"?");
        Boolean doDelete = confirm.prompt();
        if (doDelete) {
            try {
                db.removeOrder(name);
                System.out.println("Removed order \"" + name + "\".");
                return true;
            } catch (SQLException e) {
                System.err.println("Unable to delete order " + name);
                e.printStackTrace();
            }
        }
        return false;
    }

    private static String editOrderName(String name) {
        String newName = Input.getString("Enter the new name for \"" + name + "\": ");
        String prevName = name;
        try {
            db.changeOrderName(name, newName);
            name = newName;
        } catch (SQLException e) {
            System.err.println("Unable to change name of order \"" + name + "\"");
            e.printStackTrace();
            return name;
        }
        System.out.println("Changed the name of order from \"" + prevName + "\" to \"" + newName + "\"");
        return newName;
    }

    private static String editOrderPrice(String name, String price) {
        double newPrice = Input.getDouble("Enter the new price for \"" + name + "\": ");
        String prevPrice = price;
        String priceStr;
        try {
            db.changeOrderPrice(name, newPrice);
            priceStr = String.format("%,.2f", newPrice);
        } catch (SQLException e) {
            System.err.println("Unable to change price of order \"" + name + "\"");
            e.printStackTrace();
            return prevPrice;
        }
        System.out.println("Changed the price of order \"" + name + "\" from $" + prevPrice + " to $" + priceStr);
        return "" + newPrice;
    }

    private static void addOrder() {
        String name = Input.getString("Enter the name for the new individual order: ");
        try {
            if (db.orderExists(name)) {
                BooleanSelect overwritePrompt = new BooleanSelect("Order \"" + name + "\" already exists. Overwrite the existing order?");
                boolean doOverwrite = overwritePrompt.prompt();
                if (doOverwrite) {
                    editOrderPrice(name, "" + db.getOrderPrice(name));
                    return;
                }
                System.out.println("The order \"" + name + "\" will not be changed.");
                return;
            }
        } catch (SQLException e) {
            System.err.println("Unable to check existence of order \"" + name + "\"");
            e.printStackTrace();
            return;
        }
        double price = Input.getDouble("Enter the price for order \"" + name + "\": ");
        try {
            db.addOrder(name, price);
            System.out.println(String.format("Order \"%s\" : $%,.2f added.", name, price));
        } catch (SQLException e) {
            System.err.println("Unable to add order \"" + name + "\" with price " + String.format("%,.2f", price));
            e.printStackTrace();
            return;
        }
    }

    private static void peopleMenu() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'editPeople'");
    }

    private static void processGroupOrder() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'processGroupOrder'");
    }

    private static void promptNextPayer() {
        String personToPay;
        double debt;
        try {
            personToPay = db.mostDebt();
            debt = db.getDebt(personToPay);
        } catch (SQLException e) {
            System.err.println("SQL error: unable to calculate debt");
            e.printStackTrace();
            return;
        }
        
        System.out.println(String.format("%s should pay next! (Owes: $%,.2f)", personToPay, debt));
        BooleanSelect savePrompt = new BooleanSelect("Will " + personToPay + " pay for the next order? (save this information?)");
        Boolean savePerson = savePrompt.prompt();
        if (savePerson) {
            try {
                db.setToPay(personToPay);
            } catch (SQLException e) {
                System.err.println("SQL error: unable to set person_to_pay variable");
                e.printStackTrace();
                return;
            }
            System.out.println(personToPay + " saved as person to pay for the next order.");
        } 
    }

    private static int topicBaseChoice(CoffeeTopic topic) {
        List<String> options = new ArrayList<String>();
        options.add("Add new " + topic);
        options.add("Edit/View existing " + topic.plural());
        OptionSelect optionSelect = new OptionSelect("What would you like to do?", options, "Back");
        return optionSelect.prompt();
    }


    public static void main(String[] args) {
        CoffeeLedger db = CoffeeLedger.getInstance();
        try {
            // db.clear();

            // db.addPerson("John");
            // db.addPerson("Mike");
            // db.addPerson("Chris");

            // // db.printPeople();
            // db.addOrder("Large iced coffee", 4.00);
            // db.addOrder("Small hot coffee", 2.25);
            // // db.printOrders();

            // Map<String, String> groupOrder = new HashMap<String, String>();
            // groupOrder.put("John", "Large iced coffee");
            // groupOrder.put("Chris", "Small hot coffee");
            // groupOrder.put("Mike", "Large iced coffee");
            // db.addGroupOrder("Order 1", groupOrder);
            // // db.printGroupOrders();
            // // System.out.println("details:");
            // // db.printGroupOrderDetails();

            // Map<String, Double> orderPrices = db.getGroupOrder("Order 1");
            // for (String person : orderPrices.keySet()) {
            //     System.out.println(String.format("%-10s : $%,.2f", person, orderPrices.get(person)));
            // }

            CommandUserInterface.mainMenu();

            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
