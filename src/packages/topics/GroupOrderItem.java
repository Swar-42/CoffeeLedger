package packages.topics;
import java.util.List;

public class GroupOrderItem extends TopicItem {
    
    public GroupOrderItem(List<String> values) {
        super(values);
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