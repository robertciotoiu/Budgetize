package com.example.robi.budgetize.ui.adapters.gridlistview;

public class AvailableBank {
    private int position;
    private String bankName;
    private String bankImg;

    public AvailableBank(String bankName, int position, String bankImg) {
        this.bankImg = bankImg;
        this.bankName = bankName;
        this.position = position;
    }

    public String getBankImg() {
        return bankImg;
    }

    public void setBankImg(String bankImg) {
        this.bankImg = bankImg;
    }

    public String getPositionText() {
        return Integer.toString(position);
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}
