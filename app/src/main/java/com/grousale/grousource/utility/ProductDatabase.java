package com.grousale.grousource.utility;

import com.grousale.grousource.model.products;

import java.util.List;

public class ProductDatabase {

    public static List<products> productsList;

    public static List<products> getProductsList() {
        return productsList;
    }

    public static void setProductsList(List<products> productsList) {
        ProductDatabase.productsList = productsList;
    }
}
