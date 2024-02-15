package packages.topics;
import java.util.List;

public class GroupOrderList extends TopicList {
    
    public GroupOrderList(List<TopicItem> list) {
        super(list);
    }

    @Override
    protected void setTopicType() {
        type = TopicType.GROUP_ORDER;
    }
}
