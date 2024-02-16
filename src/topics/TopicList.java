package topics;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import coffeeLedger.CoffeeLedger;
import input.OptionSelect;

/**
 * Describes a list of TopicItem (data from an entire accessible table)
 * Includes options for selection of a specific TopicItem
 */
public abstract class TopicList extends Topic {
    protected List<TopicItem> list;

    public TopicList() {
        super();
        CoffeeLedger db = CoffeeLedger.getInstance();
        try {
            list = db.getTable(type.tableName());
        } catch (SQLException e) {
            System.err.println("SQL Error: unable to get " + type.plural() + " information");
            e.printStackTrace();
            list = new ArrayList<TopicItem>();
        }
    }

    /**
     * prompts the user with the list of TopicItems and gets their selection.
     * @param prompt text to prompt the user with
     * @param addItem true if there should also be an option to add and select new TopicItem, false if not.
     * @return the TopicItem that the user has selected. null if they decided against selection.
     */
    public TopicItem getSelection(String prompt, boolean addItem) {
        if (list.isEmpty()) {
            System.out.println("** No saved " + type.plural() + " **");
        }

        List<String> rowDisplay = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            rowDisplay.add(list.get(i).rowFormat());
        }
        if (addItem) { // extra option for adding an item
            rowDisplay.add("Add new " + type);
        }
        OptionSelect itemSelect = new OptionSelect(prompt, rowDisplay, "Back");
        itemSelect.displayTitleAbove(false);
        int choice = itemSelect.prompt();

        if (choice == 0) {
            System.out.println();
            return null;
        } else if (choice == list.size()+1 && addItem) { // add and select a new TopicItem
            TopicMenu addMenu = makeTopicMenu();
            System.out.println();
            TopicItem newItem = addMenu.addItemMenu(false);
            list.add(newItem);
        }

        System.out.println();
        return list.get(choice-1);
    }

    /**
     * creates the corresponding concrete TopicMenu class
     * @return the corresponding concrete TopicMenu class
     */
    protected abstract TopicMenu makeTopicMenu();
}
