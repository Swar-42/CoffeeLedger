package packages.topics;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import packages.coffeeLedger.CoffeeLedger;
import packages.input.BooleanSelect;
import packages.input.Input;
import packages.input.OptionSelect;

public abstract class TopicMenu extends Topic {
    protected static CoffeeLedger db = CoffeeLedger.getInstance();
     
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

    public TopicItem addItemMenu() {
        String name = Input.getString("Enter the name for the new " + type + ": ");
        // check for existence of 'name'
        try {
            if (db.dataExists("name", type.tableName(), "\'" + name + "\'")) {
                BooleanSelect overwritePrompt = new BooleanSelect(type + " \"" + name + "\" already exists. Edit this existing " + type + " instead?");
                boolean editExisting = overwritePrompt.prompt();
                if (editExisting) {
                    TopicItem item = db.getItem(type.tableName(), name);
                    System.out.println();
                    item.mainMenu();
                    return db.getItem(type.tableName(), name);
                }
                System.out.println("The " + type + " \"" + name + "\" will not be changed.");
                return db.getItem(type.tableName(), name);
            }
        } catch (SQLException e) {
            System.err.println("Unable to check existence of order \"" + name + "\"");
            e.printStackTrace();
            return null;
        }
        return addItem(name);
    }

    public void listMenu() {
        TopicList topicList = makeTopicList();
        TopicItem item = topicList.getSelection("Select an item from the above.");

        if (item == null) {
            return;
        }
        
        item.mainMenu();
        System.out.println();
        listMenu();
    }

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

    private int baseChoice() {
        List<String> options = new ArrayList<String>();
        options.add("Add new " + type);
        options.add("Edit/View existing " + type.plural());
        OptionSelect optionSelect = new OptionSelect("What would you like to do?", options, "Back");
        return optionSelect.prompt();
    }

    protected abstract TopicItem addItem(String name);
}
