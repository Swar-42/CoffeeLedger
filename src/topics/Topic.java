package topics;

/**
 * abstract class with a topic type that can be set by child classes.
 */
public abstract class Topic {
    protected TopicType type;

    public Topic() {
        setTopicType();
    }

    /**
     * set which enumeration 'type' will be set to for the child class.
     */
    protected abstract void setTopicType();
}
