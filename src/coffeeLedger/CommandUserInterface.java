package coffeeLedger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import input.*;
import topics.*;

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
                toPayMenu();
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

    public static void processGroupOrder() {
        String potentialPayer = getToPay();
        String personToPay = null;
        if (potentialPayer != null) {
            personToPay = confirmToPay(potentialPayer);
        }
        BooleanSelect savedSelect = new BooleanSelect("Use a saved group order? ");
        boolean useSavedOrder = savedSelect.prompt();
        System.out.println();
        TopicItem groupOrder = null;
        if (useSavedOrder) {
            TopicList groupOrderList = new GroupOrderList();
            groupOrder = groupOrderList.getSelection("Select an above group order to process.", false);
        }
        while (groupOrder == null) {
            TopicMenu groupOrderMenu = new GroupOrderMenu();
            groupOrder = groupOrderMenu.addItemMenu();
        }
        System.out.println("Processing group order \"" + groupOrder.getName() + "\".");
        updateBought(groupOrder.getName());
        double cost = calcCost(groupOrder.getName());
        System.out.println(String.format("Group order %s costs $%,.2f.", groupOrder.getName(), cost));
        if (personToPay == null) {
            potentialPayer = toPayPrompt();
            personToPay = confirmToPay(potentialPayer);
            while (personToPay == null) {
                TopicList personSelect = new PersonList();
                TopicItem selectedPerson = personSelect.getSelection("Select who will pay from the above.", true);
                if (selectedPerson != null) {
                    System.out.println();
                    personToPay = selectedPerson.getName();
                }
            }
        }
        System.out.println(String.format("%s will be charged $%,.2f for the group order.", personToPay, cost));
        chargePerson(personToPay, cost);
        updateToPay();
    }

    public static void toPayMenu() {
        String personToPay = toPayPrompt();
        personToPay = confirmToPay(personToPay);
        if (personToPay == null) return;
        saveToPay(personToPay);
    }

    private static void updateBought(String groupOrderName) {
        try {
            db.updateBought(groupOrderName);
            updateToPay();
            System.out.println("Paid amounts for group order \"" + groupOrderName + "\" updated.");
        } catch (SQLException e) {
            System.err.println("Unable to update paid amounts for group order \"" + groupOrderName + "\"");
            e.printStackTrace();
        }
    }

    private static void updateToPay() {
        try {
            db.setToPay(db.mostDebt());
        } catch (SQLException e) {
            System.err.println("Unable to update who should pay.");
            e.printStackTrace();
        }
    }

    private static void chargePerson(String name, double cost) {
        try {
            db.addPaid(name, cost);
            System.out.println(String.format("%s has been charged $%,.2f", name, cost));
        } catch (SQLException e) {
            System.err.println("Unable to charge " + name + " $" + cost );
            e.printStackTrace();
        }
    }

    private static String toPayPrompt() {
        String personToPay = mostDebt();
        double debt = getDebt(personToPay);
        
        System.out.println(String.format("%s should pay next! (Owes: $%,.2f)", personToPay, debt));
        return personToPay;
    }

    private static String confirmToPay(String potentialPayer) {
        BooleanSelect payPrompt = new BooleanSelect(String.format("Will %s pay for the next order? (Debt: $%,.2f)", potentialPayer, getDebt(potentialPayer)));
        boolean chargePerson = payPrompt.prompt();
        if (chargePerson) {
            return potentialPayer;
        }
        return null;
}

    private static void saveToPay(String name) {
        try {
            db.setToPay(name);
        } catch (SQLException e) {
            System.err.println("SQL error: unable to set person_to_pay variable");
            e.printStackTrace();
            return;
        }
        System.out.println(name + " saved as person to pay for the next order.");
    }

    private static String mostDebt() {
        try {
            return db.mostDebt();
        } catch (SQLException e) {
            System.err.println("Unable to calculate person with most debt.");
            e.printStackTrace();
            return null;
        }
    }

    private static String getToPay() {
        try {
            return db.getToPay();
        } catch (SQLException e) {
            System.err.println("Unable to calculate person to pay.");
            e.printStackTrace();
            return null;
        }
    }

    private static double getDebt(String name) {
        try {
            return db.getDebt(name);
        } catch (SQLException e) {
            System.err.println("SQL error: unable to calculate debt");
            e.printStackTrace();
            return 0;
        }
    }

    private static double calcCost(String groupOrderName) {
        try {
            return db.getCost(groupOrderName);
        } catch (SQLException e) {
            System.err.println("Unable to calculate group order " + groupOrderName + " cost");
            e.printStackTrace();
            return 0;
        }
    }


    public static void main(String[] args) {
        
        mainMenu();
    }
}
