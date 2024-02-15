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
                editListMenu();
                System.out.println();
                break;
            case 0:
                return;
        }

        mainMenu();
    }

    public void addItemMenu() {
        String name = Input.getString("Enter the name for the new " + type + ": ");
        // check for existence of 'name'
        try {
            if (db.dataExists("name", type.tableName(), "\'" + name + "\'")) {
                BooleanSelect overwritePrompt = new BooleanSelect(type + " \"" + name + "\" already exists. Edit this existing " + type + " instead?");
                boolean editExisting = overwritePrompt.prompt();
                if (editExisting) {
                    TopicItem item = db.getItem(type.tableName(), name);
                    item.mainMenu();
                    return;
                }
                System.out.println("The " + type + " \"" + name + "\" will not be changed.");
                return;
            }
        } catch (SQLException e) {
            System.err.println("Unable to check existence of order \"" + name + "\"");
            e.printStackTrace();
            return;
        }
        addItem(name);
    }

    public void editListMenu() {
        List<TopicItem> itemList;
        try {
            itemList = db.getTable(type.tableName());
        } catch (SQLException e) {
            System.err.println("SQL Error: unable to get " + type.plural() + " information");
            e.printStackTrace();
            return;
        }
        boolean doRepeat = listSetup(itemList);

        if (doRepeat) editListMenu();
    }

    private boolean listSetup(List<TopicItem> itemList) {
        TopicList topicList;
        switch (type) {
            case PERSON:
                topicList = new PersonList(itemList);
                break;
            case ORDER:
                topicList = new OrderList(itemList);
                break;
            case GROUP_ORDER:
                topicList = new GroupOrderList(itemList);
                break;
            default:
                throw new IllegalStateException("topic type " + type + " is invalid");
        }
        return topicList.mainMenu();
    }

    private static int baseChoice() {
        List<String> options = new ArrayList<String>();
        options.add("Add new " + type);
        options.add("Edit/View existing " + type.plural());
        OptionSelect optionSelect = new OptionSelect("What would you like to do?", options, "Back");
        return optionSelect.prompt();
    }

    protected abstract void addItem(String name);
}
