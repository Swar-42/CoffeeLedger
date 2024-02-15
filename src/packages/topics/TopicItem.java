package packages.topics;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import packages.coffeeLedger.CoffeeLedger;
import packages.input.BooleanSelect;
import packages.input.Input;
import packages.input.OptionSelect;

public abstract class TopicItem extends Topic {
    protected static List<String> colNames;
    protected static CoffeeLedger db = CoffeeLedger.getInstance();
    protected List<String> values;

    public TopicItem(List<String> values) {
        super();
        colNames = new ArrayList<String>();
        setColNames();
        this.values = values;
    }

    public TopicItem mainMenu() {
        List<String> editOptions = new ArrayList<String>();
        for (String name : colNames) {
            editOptions.add("Edit " + name);
        }
        editOptions.add("Delete " + type);
        OptionSelect editSelect = new OptionSelect("Selected: " + this.toString(), editOptions, "Back");
        int choice = editSelect.prompt();
        
        if (choice > 0 && choice <= values.size()) {
            System.out.println();
            editColumn(choice-1);
        } else if (choice == values.size() + 1) {
            System.out.println();
            boolean deleted = deleteItem();
            if (deleted) return null;
        } else {
            return this;
        }

        System.out.println();
        return mainMenu();
    }

    public String getName() {
        return values.get(0);
    }

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

    private boolean nameExists(String name) {
        try {
            return db.dataExists("name", type.tableName(), name);
        } catch (SQLException e) {
            System.err.println("Unable to detect existence of name " + name + " for " + type);
            e.printStackTrace();
        }
        return true;
    }

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

    public abstract String rowFormat();

    protected abstract void setColNames();
    
}
