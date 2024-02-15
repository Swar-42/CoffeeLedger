package packages.topics;
import java.util.ArrayList;
import java.util.List;

import packages.input.OptionSelect;

public class GroupOrderItem extends TopicItem {
    
    public GroupOrderItem(List<String> values) {
        super(values);
    }

    @Override
    public void mainMenu() {
        List<String> options = new ArrayList<String>();
        options.add("Edit name");
        options.add("Add/Edit individual orders");
        options.add("Delete group order");
        OptionSelect select = new OptionSelect("Selected: " + this.toString(), options, "Back");
        int choice = select.prompt();

        switch (choice) {
            case 1:
                System.out.println();
                editColumn(0);
                break;
            case 2:
                System.out.println();
                // add/edit individual orders
                break;
            case 3:
                System.out.println();
                boolean deleted = deleteItem();
                if (deleted) return;
            case 0:
                return;
        }

        System.out.println();
        mainMenu();
    }

    @Override
    public String rowFormat() {
        return values.get(0);
    }

    @Override public String toString() {
        return values.get(0);
    }

    @Override
    protected void setColNames() {
        colNames.add("name");
    }


    @Override
    protected void setTopicType() {
        type = TopicType.GROUP_ORDER;
    }
    
}