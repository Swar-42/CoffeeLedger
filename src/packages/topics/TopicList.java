package packages.topics;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import packages.coffeeLedger.CoffeeLedger;
import packages.input.OptionSelect;

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

    public TopicItem getSelection(String prompt, boolean addItem) {
        if (list.isEmpty()) {
            System.out.println("** No saved " + type.plural() + " **");
        }

        List<String> rowDisplay = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            rowDisplay.add(list.get(i).rowFormat());
        }
        if (addItem) {
            rowDisplay.add("Add new " + type);
        }
        OptionSelect itemSelect = new OptionSelect(prompt, rowDisplay, "Back");
        itemSelect.displayTitleAbove(false);
        int choice = itemSelect.prompt();

        if (choice == 0) {
            System.out.println();
            return null;
        } else if (choice == list.size()+1) {
            TopicMenu addMenu = makeTopicMenu();
            System.out.println();
            TopicItem newItem = addMenu.addItemMenu();
            list.add(newItem);
        }

        System.out.println();
        return list.get(choice-1);
    }

    protected abstract TopicMenu makeTopicMenu();
}
