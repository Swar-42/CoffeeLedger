package packages.topics;
import java.util.ArrayList;
import java.util.List;

import packages.input.OptionSelect;

public abstract class TopicList extends Topic {
    protected List<TopicItem> list;

    public TopicList(List<TopicItem> list) {
        super();
        this.list = list;
    }

    public boolean mainMenu() {
        List<String> rowDisplay = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            rowDisplay.add(list.get(i).rowFormat());
        }
        OptionSelect itemSelect = new OptionSelect("Select an item from the above to edit.", rowDisplay, "Back");
        int choice = itemSelect.prompt();

        if (choice == 0) {
            return false;
        }

        System.out.println();
        list.get(choice-1).mainMenu();
        System.out.println();
        return true;
    }
}
