package com.grousale.grousource.retrofit;

import com.grousale.grousource.model.items;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface ApiInterface {
    @GET("/rest/V1/products?searchCriteria")
    Call<items> getAllItems(@Header("Authorization") String token);
}
