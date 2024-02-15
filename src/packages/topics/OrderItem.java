package packages.topics;
import java.util.ArrayList;
import java.util.List;

import packages.input.Input;

public class OrderItem extends TopicItem {

    public OrderItem(List<String> values) {
        super(values);
    }

    @Override
    public String rowFormat() {
        return String.format("%-30s : $%s", values.get(0), values.get(1));
    }

    @Override
    protected List<String> getValueInput(int index, String prompt) {
        switch(index) {
            case 0: // name
                return super.getValueInput(index, prompt);
            case 1: // price
                List<String> out = new ArrayList<String>();
                String input = String.format("%,.2f", Input.getPosDouble(prompt));
                out.add(input);
                out.add(input);
                return out;
            default:
                throw new IllegalArgumentException("Index " + index + " not valid for OrderItems");
        }
    }

    @Override
    public String toString() {
        return String.format("%s : $%s", values.get(0), values.get(1));
    }

    @Override
    protected void setColNames() {
        colNames.add("name");
        colNames.add("price");
    }

    @Override
    protected void setTopicType() {
        type = TopicType.ORDER;
    }
    
}
