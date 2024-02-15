package topics;
import java.util.ArrayList;
import java.util.List;

import input.OptionSelect;

public class GroupOrderItem extends TopicItem {
    
    public GroupOrderItem(List<String> values) {
        super(values);
    }

    @Override
    public TopicItem mainMenu() {
        List<String> options = new ArrayList<String>();
        options.add("Edit name");
        // options.add("Add/Edit individual orders");
        options.add("Delete group order");
        OptionSelect select = new OptionSelect("Selected: " + this.toString(), options, "Back");
        int choice = select.prompt();

        switch (choice) {
            case 1:
                System.out.println();
                editColumn(0);
                break;
            // case 2:
            //     System.out.println();
            //     // add/edit individual orders
            //     break;
            case 2:
                System.out.println();
                boolean deleted = deleteItem();
                if (deleted) return null;
            case 0:
                return this;
        }

        System.out.println();
        return mainMenu();
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