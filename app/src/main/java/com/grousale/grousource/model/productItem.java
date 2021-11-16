package com.grousale.grousource.model;

public class productItem {

    String itemID,timeIN,timeOut,shelf;

    public productItem(String itemID, String timeIN, String timeOut, String shelf) {
        this.itemID = itemID;
        this.timeIN = timeIN;
        this.timeOut = timeOut;
        this.shelf = shelf;
    }

    public productItem() {
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getTimeIN() {
        return timeIN;
    }

    public void setTimeIN(String timeIN) {
        this.timeIN = timeIN;
    }

    public String getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(String timeOut) {
        this.timeOut = timeOut;
    }

    public String getShelf() {
        return shelf;
    }

    public void setShelf(String shelf) {
        this.shelf = shelf;
    }
}
