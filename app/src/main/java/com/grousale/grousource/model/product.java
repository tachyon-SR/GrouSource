package com.grousale.grousource.model;

public class product {

    String productName, shopName, shopAddress, shopLandmark, shopPhone, productImageUrl, productPrice;

    public product() {
    }

    public product(String productName, String shopName, String shopAddress, String shopLandmark, String shopPhone, String productImageUrl, String productPrice) {
        this.productName = productName;
        this.shopName = shopName;
        this.shopAddress = shopAddress;
        this.shopLandmark = shopLandmark;
        this.shopPhone = shopPhone;
        this.productImageUrl = productImageUrl;
        this.productPrice = productPrice;
    }

    public String getproductImageUrl() {
        return productImageUrl;
    }

    public void setproductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public String getShopLandmark() {
        return shopLandmark;
    }

    public void setShopLandmark(String shopLandmark) {
        this.shopLandmark = shopLandmark;
    }

    public String getShopPhone() {
        return shopPhone;
    }

    public void setShopPhone(String shopPhone) {
        this.shopPhone = shopPhone;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }
}
