package packages.topics;
import java.util.List;

public class OrderList extends TopicList {

    public OrderList(List<TopicItem> list) {
        super(list);
    }

    @Override
    protected void setTopicType() {
        type = TopicType.ORDER;
    }
    
}
