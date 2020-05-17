package com.example.robi.budgetize.data.remotedatabase.entities.transaction;

public class Details {
    private String type;
    private String description;
    private String posted;
    private String completed;
    private NewBalance new_balance;
    private Value value;

    public Details(String type, String description, String posted, String completed, NewBalance new_balance, Value value) {
        this.type = type;
        this.description = description;
        this.posted = posted;
        this.completed = completed;
        this.new_balance = new_balance;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPosted() {
        return posted;
    }

    public void setPosted(String posted) {
        this.posted = posted;
    }

    public String getCompleted() {
        return completed;
    }

    public void setCompleted(String completed) {
        this.completed = completed;
    }

    public NewBalance getNew_balance() {
        return new_balance;
    }

    public void setNew_balance(NewBalance new_balance) {
        this.new_balance = new_balance;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Details{" +
                "type=" + type +
                ", description='" + description + '\'' +
                ", posted='" + posted + '\'' +
                ", completed='" + completed + '\'' +
                ", new_balance=" + new_balance +
                ", value=" + value +
                '}';
    }
}
