package packages.topics;
import java.util.ArrayList;
import java.util.List;

import packages.input.Input;

public class PersonItem extends TopicItem {

    public PersonItem(List<String> values) {
        super(values);
    }

    @Override protected List<String> getValueInput(int index, String prompt) {
        switch(index) {
            case 0: // name
                return super.getValueInput(index, prompt);
            case 1: // bought, paid
            case 2:
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
    public String rowFormat() {
        return String.format("%-30s (Bought: $%-5s) (Paid: $%-5s)", values.get(0), values.get(1), values.get(2));
    }
    
    @Override
    public String toString() {
        return String.format("%s (Bought: $%-5s) (Paid: $%-5s)", values.get(0), values.get(1), values.get(2));
    }

    @Override
    protected void setColNames() {
        colNames.add("name");
        colNames.add("bought");
        colNames.add("paid");
    }


    @Override
    protected void setTopicType() {
        type = TopicType.PERSON;
    }
    
}