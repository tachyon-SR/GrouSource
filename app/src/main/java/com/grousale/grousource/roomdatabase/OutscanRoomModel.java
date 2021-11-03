package com.grousale.grousource.roomdatabase;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//@Entity((tableName = "realtimeData"))
public class OutscanRoomModel implements Serializable {

    @NonNull @PrimaryKey
            String trackingID;
    String dateTime, remarks, platform;
    List<String> sku = new ArrayList<>();
    int quantity;

    public OutscanRoomModel() {
    }
}
