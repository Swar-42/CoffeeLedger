package packages.coffeeLedger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import packages.input.*;
import packages.topics.*;

public class CommandUserInterface {

    private static CoffeeLedger db = CoffeeLedger.getInstance();

    public static void mainMenu() {
        List<String> options = new ArrayList<String>();
        options.add("Who should pay next?");
        options.add("Process a group order");
        options.add("Add/Edit " + TopicType.PERSON.plural());
        options.add("Add/Edit " + TopicType.ORDER.plural());
        options.add("Add/Edit " + TopicType.GROUP_ORDER.plural());
        OptionSelect menu = new OptionSelect("Welcome to CoffeeLedger! Select an option below:", options, "Exit");
        int selection = menu.prompt();

        TopicMenu subMenu;
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
                subMenu = new PersonMenu();
                subMenu.mainMenu();
                System.out.println();
                break;
            case 4:
                System.out.println();
                subMenu = new OrderMenu();
                subMenu.mainMenu();
                System.out.println();
                break;
            case 5:
                System.out.println();
                subMenu = new GroupOrderMenu();
                subMenu.mainMenu();
                System.out.println();
                break;
            case 0:
                System.out.println("Have a good day!");
                return;
        }

        mainMenu();
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


    public static void main(String[] args) {
        CoffeeLedger db = CoffeeLedger.getInstance();
        try {
            db.clear();

            db.addPerson("John");
            db.addPerson("Mike");
            db.addPerson("Chris");

            // db.printPeople();
            db.addOrder("Large iced coffee", 4.00);
            db.addOrder("Small hot coffee", 2.25);
            // db.printOrders();

            Map<String, String> groupOrder = new HashMap<String, String>();
            groupOrder.put("John", "Large iced coffee");
            groupOrder.put("Chris", "Small hot coffee");
            groupOrder.put("Mike", "Large iced coffee");
            db.addGroupOrder("Order 1", groupOrder);
            // db.printGroupOrders();
            // System.out.println("details:");
            // db.printGroupOrderDetails();

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
