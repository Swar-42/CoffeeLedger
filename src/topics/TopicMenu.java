package topics;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import coffeeLedger.CoffeeLedger;
import input.BooleanSelect;
import input.Input;
import input.OptionSelect;

/**
 * Describes a base menu CLI for interacting with a particular topic.
 */
public abstract class TopicMenu extends Topic {
    protected static CoffeeLedger db = CoffeeLedger.getInstance();
    
    /**
     * main point of entry
     */
    public void mainMenu() {
        int choice = baseChoice();
        switch (choice) {
            case 1:
                System.out.println();
                addItemMenu();
                System.out.println();
                break;
            case 2:
                System.out.println();
                listMenu();
                System.out.println();
                break;
            case 0:
                return;
        }

        mainMenu();
    }

    /**
     * main menu for adding a new TopicItem
     * @return the new TopicItem added. null if none added.
     */
    public TopicItem addItemMenu() {
        String name = Input.getString("Enter the name for the new " + type + ": ");
        // check for existence of 'name'
        try {
            if (db.dataExists("name", type.tableName(), "\'" + name + "\'")) {
                BooleanSelect overwritePrompt = new BooleanSelect(type + " \"" + name + "\" already exists. Edit this existing " + type + " instead?");
                boolean editExisting = overwritePrompt.prompt();
                if (editExisting) {
                    // go to existing item's menu and return that item.
                    TopicItem item = db.getItem(type.tableName(), name);
                    System.out.println();
                    return item.mainMenu();
                }
                // return existing item without going to its menu
                System.out.println("The " + type + " \"" + name + "\" will not be changed.");
                return db.getItem(type.tableName(), name);
            }
        } catch (SQLException e) {
            System.err.println("Unable to check existence of order \"" + name + "\"");
            e.printStackTrace();
            return null;
        }
        // Item does not exist, so we can safely add it
        return addItem(name);
    }

    /**
     * main menu for displaying a list of TopicItems using a TopicList
     */
    public void listMenu() {
        TopicList topicList = makeTopicList();
        TopicItem item = topicList.getSelection("Select an item from the above.", false);

        if (item == null) {
            return;
        }
        
        item.mainMenu();
        System.out.println();
        listMenu();
    }

    /**
     * makes a TopicList item of the corresponding TopicType
     * @return
     */
    public TopicList makeTopicList() {
        switch (type) {
            case PERSON:
                return new PersonList();
            case ORDER:
                return new OrderList();
            case GROUP_ORDER:
                return new GroupOrderList();
            default:
                throw new IllegalStateException("topic type " + type + " is invalid");
        }
    }

    /**
     * the base options of a TopicMenu
     * @return
     */
    private int baseChoice() {
        List<String> options = new ArrayList<String>();
        options.add("Add new " + type);
        options.add("Edit/View existing " + type.plural());
        OptionSelect optionSelect = new OptionSelect("What would you like to do?", options, "Back");
        return optionSelect.prompt();
    }

    /**
     * CLI for adding a TopicItem of the given name to the database according to a child class's specifications (differ for each TopicType).
     * @param name name of the TopicItem to add
     * @return
     */
    protected abstract TopicItem addItem(String name);
}
