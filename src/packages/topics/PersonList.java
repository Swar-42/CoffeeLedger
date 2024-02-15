package packages.topics;

public class PersonList extends TopicList {

    @Override
    protected void setTopicType() {
        type = TopicType.PERSON;
    }

    @Override
    protected TopicMenu makeTopicMenu() {
        return new PersonMenu();
    }
}
