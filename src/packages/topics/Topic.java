package packages.topics;
public abstract class Topic {
    protected static TopicType type;

    public Topic() {
        setTopicType();
    }

    protected abstract void setTopicType();
}
