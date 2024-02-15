package packages.topics;

import java.sql.SQLException;

public class PersonMenu extends TopicMenu {

    @Override
    protected void setTopicType() {
        type = TopicType.PERSON;
    }

    @Override
    protected void addItem(String name) {
        try {
            db.addPerson(name);
            System.out.println(String.format("Person \"%s\" added", name));
        } catch (SQLException e) {
            System.err.println("Unable to add person \"" + name + "\"");
            e.printStackTrace();
            return;
        }
    }
    
}
