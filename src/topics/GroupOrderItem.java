package topics;
import java.util.ArrayList;
import java.util.List;

import input.OptionSelect;

/**
 * Describes a TopicItem of the GROUP_ORDER type and its functionality
 */
public class GroupOrderItem extends TopicItem {
    
    public GroupOrderItem(List<String> values) {
        super(values);
    }

    @Override
    public TopicItem mainMenu() {
        /*
         * Note: adding/editing individual orders within a group order is not currently implemented. 
         * Implementation of this functionality could involve creating another TopicType PERSON_ORDER
         * corresponding to a specific person's order within a group order.
         * With that TopicType, a corresponding menu, list, and item class would be extended from the abstract implementations.
         * Values for PERSON_ORDER could be retrieved from a temporary JOINed table with people.name and order.name.
         * Adding items would be inserting a new row into group_order_details according to the database (new database methods may need to be implemented).
         */

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