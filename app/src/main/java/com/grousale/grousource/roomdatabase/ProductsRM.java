package com.grousale.grousource.roomdatabase;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "ProductsRDB")
public class ProductsRM {


    @PrimaryKey
    @NonNull
    String sku;
    String name,id;

    public ProductsRM() {
    }

    public ProductsRM(String sku, String name, String id) {
        this.sku = sku;
        this.name = name;
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
