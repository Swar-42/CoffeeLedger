import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class CommandUserInterface {

    private static CoffeeLedger db = CoffeeLedger.getInstance();
    private static Scanner scanner = new Scanner(System.in);

    public static void mainMenu() {
        List<String> options = new ArrayList<String>();
        options.add("Who should pay next?");
        options.add("Process a group order");
        options.add("Add/Edit " + CoffeeTopic.PERSON.plural());
        options.add("Add/Edit " + CoffeeTopic.ORDER.plural());
        options.add("Add/Edit " + CoffeeTopic.GROUP_ORDER.plural());
        OptionSelect menu = new OptionSelect(scanner, "Welcome to CoffeeLedger! Select an option below:", options, "Exit");
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
        OptionSelect orderSelect = new OptionSelect(scanner, "Select an item from the above to edit.", allOrders, "Back");
        orderSelect.displayTitleAbove(false);
        int choice = orderSelect.prompt();

        if (choice == 0) return;
        // edit prompt
        System.out.println();
        editOrder(orderNames.get(choice-1), orderPrices.get(choice-1));
        System.out.println();
    }

    private static void editOrder(String name, String price) {
        List<String> editSelection = new ArrayList<String>();
        editSelection.add("Edit name");
        editSelection.add("Edit price");
        OptionSelect editSelect = new OptionSelect(scanner, String.format("Selected: %-30s : $%s", name, price), editSelection, "Back");
        int choice = editSelect.prompt();

        switch (choice) {
            case 1:
            case 2:
            case 0:
                return;
        }
    }

    private static void addOrder() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addOrder'");
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
        BooleanSelect savePrompt = new BooleanSelect(scanner, "Will " + personToPay + " pay for the next order? (save this information?)");
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
        OptionSelect optionSelect = new OptionSelect(scanner, "What would you like to do?", options, "Back");
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
