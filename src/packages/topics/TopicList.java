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

    public TopicItem getSelection(String prompt) {

        List<String> rowDisplay = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            rowDisplay.add(list.get(i).rowFormat());
        }
        OptionSelect itemSelect = new OptionSelect(prompt, rowDisplay, "Back");
        itemSelect.displayTitleAbove(false);
        int choice = itemSelect.prompt();

        if (choice == 0) {
            return null;
        }

        System.out.println();
        return list.get(choice-1);
    }
}
