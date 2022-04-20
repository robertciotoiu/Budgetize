package com.example.robi.budgetize.ui.gridlistview;

public class AvailableBank {
    private int position;
    private String bankName;
    private String bankImg;//ALSO BANK ID!

    public AvailableBank(String bankName, int position, String bankImg) {
        this.bankImg = bankImg;
        this.position = position;
        this.bankName = bankName;
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

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
