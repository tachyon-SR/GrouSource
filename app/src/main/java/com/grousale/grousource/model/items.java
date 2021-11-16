package com.grousale.grousource.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class items {
    @SerializedName("items")
    @Expose
    private List<products> products = null;

    public List<products> getProducts() {
        return products;
    }

    public void setProducts(List<products> products) {
        this.products = products;
    }
}
