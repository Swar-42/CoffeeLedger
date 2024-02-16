package coffeeLedger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import input.*;
import topics.*;

/**
 * A Command-Line Interface for interacting with the CoffeeLedger database.
 */
public class CommandUserInterface {

    private static CoffeeLedger db = CoffeeLedger.getInstance();

    /**
     * The main entry point for the CoffeeLedger program
     */
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

    /*
     * Command-line interface for processing a group order, including loading a saved group order or making a new one.
     * Provides suggestions on who should pay for the group order
     */
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

    /**
     * command line interface for determining who should pay for the next group order (without information about that upcoming order)
     */
    public static void toPayMenu() {
        String personToPay = toPayPrompt();
        personToPay = confirmToPay(personToPay);
        if (personToPay == null) return;
        saveToPay(personToPay);
    }

    /**
     * calls on the database to update the bought values for all people involved in the group order
     * @param groupOrderName name of the group order which is used to update bought values
     */
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

    /**
     * sets the person_to_pay stored variable in the database as the person with the most debt.
     */
    private static void updateToPay() {
        try {
            db.setToPay(db.mostDebt());
        } catch (SQLException e) {
            System.err.println("Unable to update who should pay.");
            e.printStackTrace();
        }
    }

    /**
     * charges provided person by adding cost to their 'paid' value
     * @param name name of the person to charge
     * @param cost amount to charge the person for
     */
    private static void chargePerson(String name, double cost) {
        try {
            db.addPaid(name, cost);
            System.out.println(String.format("%s has been charged $%,.2f", name, cost));
        } catch (SQLException e) {
            System.err.println("Unable to charge " + name + " $" + cost );
            e.printStackTrace();
        }
    }

    /**
     * prompts the user with information about who should pay next
     * @return name of the person suggested to pay next for a group order
     */
    private static String toPayPrompt() {
        String personToPay = mostDebt();
        double debt = getDebt(personToPay);
        
        System.out.println(String.format("%s should pay next! (Owes: $%,.2f)", personToPay, debt));
        return personToPay;
    }

    /**
     * prompts the user to confirm if the potential payer will pay for the next group order
     * @param potentialPayer person to potentially next pay for a group order
     * @return name of the person who user has decided should pay next. null if user is undecided.
     */
    private static String confirmToPay(String potentialPayer) {
        BooleanSelect payPrompt = new BooleanSelect(String.format("Will %s pay for the next order? (Debt: $%,.2f)", potentialPayer, getDebt(potentialPayer)));
        boolean chargePerson = payPrompt.prompt();
        if (chargePerson) {
            return potentialPayer;
        }
        return null;
    }

    /**
     * saves the given person as the next to pay for a group order in the database
     * @param name name of the person to save
     */
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

    /**
     * retrieves the person with the most debt from the database
     * @return name of the person with the most debt
     */
    private static String mostDebt() {
        try {
            return db.mostDebt();
        } catch (SQLException e) {
            System.err.println("Unable to calculate person with most debt.");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * gets the person to pay for the next group order according the the stored value in the database
     * @return
     */
    private static String getToPay() {
        try {
            return db.getToPay();
        } catch (SQLException e) {
            System.err.println("Unable to calculate person to pay.");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * gets the total debt of the provided person as a double (negative if they are not in debt)
     * @param name name of the person to query
     * @return the person's total debt
     */
    private static double getDebt(String name) {
        try {
            return db.getDebt(name);
        } catch (SQLException e) {
            System.err.println("SQL error: unable to calculate debt");
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * gets the total cost of the given group order as a double
     * @param groupOrderName name of the group order to query
     * @return the total cost of the group order
     */
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
        try {
            CoffeeLedger.getInstance().close();
        } catch (SQLException e) {
            System.err.println("Unable to close database connection");
            e.printStackTrace();
        }
    }
}
