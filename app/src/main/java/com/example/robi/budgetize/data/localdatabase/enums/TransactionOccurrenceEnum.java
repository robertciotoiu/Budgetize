package com.example.robi.budgetize.data.localdatabase.enums;

public enum TransactionOccurrenceEnum {
    Never("Never"),
    EveryDay("Every Day"),
//    Every2Days("Every 2 Days"),
//    EveryWorkingDay("Every Working Day"),
//    EveryWeek("Every Week"),
//    Every2Weeks("Every 2 Weeks"),
    EveryMonth("Every Month"),
//    Every2Months("Every 2 Months"),
//    Every3Month("Every 3 Months"),
//    Every6Months("Every 6 Months"),
    EveryYear("Every Year");

    private final String occurrence;

    public String getOccurrence()
    {
        return this.occurrence;
    }

    TransactionOccurrenceEnum(String occurrence)
    {
        this.occurrence = occurrence;
    }
}
