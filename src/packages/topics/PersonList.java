package packages.topics;
import java.util.List;

public class PersonList extends TopicList {
    
    public PersonList(List<TopicItem> list) {
        super(list);
    }

    @Override
    protected void setTopicType() {
        type = TopicType.PERSON;
    }
}
