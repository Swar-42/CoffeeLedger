package packages.topics;
import java.util.List;

public class OrderList extends TopicList {

    @Override
    protected void setTopicType() {
        type = TopicType.ORDER;
    }

    @Override
    protected TopicMenu makeTopicMenu() {
        return new OrderMenu();
    }
    
}
