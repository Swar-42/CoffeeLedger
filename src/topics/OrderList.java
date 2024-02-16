package topics;

/**
 * Describes a TopicList of the ORDER TopicType
 */
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
