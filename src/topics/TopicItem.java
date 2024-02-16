package topics;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import coffeeLedger.CoffeeLedger;
import input.BooleanSelect;
import input.Input;
import input.OptionSelect;

/**
 * Describes a single row of data from the accessible tables in the database
 * Includes options for modifying/deleting this data from the database
 */
public abstract class TopicItem extends Topic {
    
    protected static CoffeeLedger db = CoffeeLedger.getInstance();
    // the labels for each value (corresponds with database)
    protected static List<String> colNames;
    // the row of values from the database
    protected List<String> values;

    public TopicItem(List<String> values) {
        super();
        colNames = new ArrayList<String>();
        setColNames();
        this.values = values;
    }

    /**
     * Command-line main point of entry for modifying this TopicItem.
     * modification options include editing of any value as well as deletion of the TopicItem.
     * @param canDelete true if user has permission to delete this topicItem at this time.
     * @return this TopicItem, or null if this TopicItem has been deleted from the database.
     */
    public TopicItem mainMenu(boolean canDelete) {
        List<String> editOptions = new ArrayList<String>();
        for (String name : colNames) {
            editOptions.add("Edit " + name);
        }
        if (canDelete) {
            editOptions.add("Delete " + type);
        }
        OptionSelect editSelect = new OptionSelect("Selected: " + this.toString(), editOptions, "Back");
        int choice = editSelect.prompt();
        
        if (choice > 0 && choice <= values.size()) {
            System.out.println();
            editColumn(choice-1);
        } else if (choice == values.size() + 1 && canDelete) {
            System.out.println();
            boolean deleted = deleteItem();
            if (deleted) return null;
        } else {
            return this;
        }

        System.out.println();
        return mainMenu(canDelete);
    }

    /**
     * returns the unique principal label for the data
     * @return name of the TopicItem
     */
    public String getName() {
        return values.get(0);
    }

    /**
     * CLI for editing a specific value of the TopicItem
     * @param index index of the label whose value we are editing.
     */
    protected void editColumn(int index) {
        String name = values.get(0);
        String colName = colNames.get(index);
        List<String> newValue = getValueInput(index, "Enter the new " + colName + " for \"" + name + "\": ");
        String prevValue = values.get(index);

        if (index == 0 && nameExists(newValue.get(0))) { // names should be unique
            System.out.println("Invalid name. \"" + newValue.get(1) + "\" already exists.");
            editColumn(index);
            return;
        }
        
        try {
            db.changeValue(name, colName, type.tableName(), newValue.get(0));
            values.set(index, newValue.get(1));
            System.out.println("Changed the " + colName + " of " + type + " from " + prevValue + " to " + newValue.get(1));
        } catch (SQLException e) {
            System.err.println("Unable to change " + colName + " of " + type + " from " + prevValue + " to " + newValue.get(1));
            e.printStackTrace();
        }
    }

    /**
     * checks if the given name already exists in the database
     * @param name name to check
     * @return true if the name is already in the database, false otherwise
     */
    private boolean nameExists(String name) {
        try {
            return db.dataExists("name", type.tableName(), name);
        } catch (SQLException e) {
            System.err.println("Unable to detect existence of name " + name + " for " + type);
            e.printStackTrace();
        }
        return true;
    }

    /**
     * CLI for deletion of this TopicItem from the database
     * @return true if the data was deleted, false otherwise
     */
    protected boolean deleteItem() {
        BooleanSelect confirm = new BooleanSelect("Are you sure you want to delete the " + type + " \"" + values.get(0) + "\"?");
        Boolean doDelete = confirm.prompt();
        if (doDelete) {
            try {
                db.removeRow(values.get(0), type.tableName());
                System.out.println("Removed order \"" + values.get(0) + "\".");
                return true;
            } catch (SQLException e) {
                System.err.println("Unable to delete order " + values.get(0));
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return values.get(0);
    }

    /**
     * gets the user input and returns it in SQL-friendly and java-friendly formats
     * @param index column index whose value we are changing
     * @param prompt message to prompt the user for input
     * @return a 2-item list. First index - SQL-friendly output. Second-index - java-friendly output.
     *         e.g. SQL reads strings as 'string'.
     */
    protected List<String> getValueInput(int index, String prompt) {
        String input = Input.getString(prompt);
        List<String> out = new ArrayList<String>();
        out.add("\'" + input + "\'");
        out.add(input);
        return out;
    }

    /**
     * the display format for enumerating a row of the values inside this TopicItem
     * @return
     */
    public abstract String rowFormat();

    /**
     * sets the label for each value (corresponds with database column names)
     */
    protected abstract void setColNames();
    
}
