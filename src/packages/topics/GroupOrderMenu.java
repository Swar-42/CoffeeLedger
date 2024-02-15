package packages.topics;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import packages.input.BooleanSelect;
import packages.input.Input;

public class GroupOrderMenu extends TopicMenu {

    @Override
    protected void setTopicType() {
        type = TopicType.GROUP_ORDER;
    }

    @Override
    protected void addItem(String name) {
        System.out.println();
        Map<String, String> orderMap = new HashMap<String, String>();
        BooleanSelect continuePrompt = new BooleanSelect("Add another person to the group order?");

        do {
            TopicList listSelect = new PersonList();
            TopicItem item = listSelect.getSelection("Choose a person above to add to the order.");
            if (item == null) break;
            String personName = item.getName();
            listSelect = new OrderList();
            item = listSelect.getSelection("What will " + personName + " order?");
            if (item == null) break;
            String orderName = item.getName();
            orderMap.put(personName, orderName);
        } while (continuePrompt.prompt());

        if (orderMap.isEmpty()) {
            System.out.println("Group Order \"" + name + "\" cancelled.");
            return;
        }

        try {
            db.addGroupOrder(name, orderMap);
            System.out.println(String.format("Group Order \"%s\" added.", name));
        } catch (SQLException e) {
            System.err.println("Unable to add Group Order \"" + name + "\"");
            e.printStackTrace();
            return;
        }
    }
    
}
