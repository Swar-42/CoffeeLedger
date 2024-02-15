package topics;
public abstract class Topic {
    protected TopicType type;

    public Topic() {
        setTopicType();
    }

    protected abstract void setTopicType();
}
