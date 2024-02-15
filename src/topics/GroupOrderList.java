package topics;

public class GroupOrderList extends TopicList {

    @Override
    protected void setTopicType() {
        type = TopicType.GROUP_ORDER;
    }

    @Override
    protected TopicMenu makeTopicMenu() {
        return new GroupOrderMenu();
    }
}
