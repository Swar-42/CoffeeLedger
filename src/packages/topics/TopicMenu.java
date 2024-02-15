package packages.topics;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import packages.coffeeLedger.CoffeeLedger;
import packages.input.OptionSelect;

public abstract class TopicMenu extends Topic {
    protected static CoffeeLedger db = CoffeeLedger.getInstance();
     
    public void mainMenu() {
        int choice = baseChoice();
        switch (choice) {
            case 1:
                System.out.println();
                // add item
                System.out.println();
                break;
            case 2:
                System.out.println();
                editList();
                System.out.println();
                break;
            case 0:
                return;
        }

        mainMenu();
    }

    private void editList() {
        List<TopicItem> itemList;
        try {
            itemList = db.getTable(type.tableName());
        } catch (SQLException e) {
            System.err.println("SQL Error: unable to get " + type.plural() + " information");
            e.printStackTrace();
            return;
        }
        boolean doRepeat = listSetup(itemList);

        if (doRepeat) editList();
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
}
