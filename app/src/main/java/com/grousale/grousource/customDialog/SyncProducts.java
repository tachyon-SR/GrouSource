package com.grousale.grousource.customDialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.grousale.grousource.databinding.DialogSyncBinding;
import com.grousale.grousource.model.items;
import com.grousale.grousource.model.products;
import com.grousale.grousource.retrofit.ApiInterface;
import com.grousale.grousource.retrofit.RetrofitClientInstance;
import com.grousale.grousource.roomdatabase.ProductsDB;
import com.grousale.grousource.roomdatabase.ProductsRM;
import com.grousale.grousource.roomdatabase.RoomDao;
import com.grousale.grousource.utility.Constants;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SyncProducts extends Dialog {

    DialogSyncBinding binding;
    Context mContext;
    List<products> productsList = new ArrayList<>();
    RoomDao roomDao;

    public SyncProducts(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DialogSyncBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ProductsDB productsDB = ProductsDB.getInstance(mContext);
        roomDao = productsDB.roomDao();

        binding.syncNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.syncNow.setVisibility(View.GONE);
                binding.syncProgressbar.setVisibility(View.VISIBLE);
                syncProducts();
            }
        });

    }

    private void syncProducts() {

        Retrofit retrofit2 = RetrofitClientInstance.getRetrofitInstance();
        ApiInterface jsonPlaceHolderApi = retrofit2.create(ApiInterface.class);
        String newkey= "Bearer g4wtlkubkd1mzcbfd3tu92z803tbn2qn";

        jsonPlaceHolderApi.getAllItems(newkey).enqueue(new Callback<items>() {

            @Override
            public void onResponse(Call<items> call, Response<items> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(mContext, "Code: "+response.code(), Toast.LENGTH_SHORT).show();
                    Log.d("Retrofit",response.code()+" ;"+response.message()+" ;"+response.errorBody());
                    return;
                }
                productsList = response.body().getProducts();
                addtoRoomDataBase(productsList);
                addtoFirebase(productsList);

                 }

            @Override
            public void onFailure(Call<items> call, Throwable t)
            {
                Toast.makeText(mContext, "Failed: "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("Retrofit",t.getMessage()+ " "+ t.getLocalizedMessage()+" "+t.getCause());
            }

        });
    }

    private void addtoRoomDataBase(List<products> productsList) {

        ProductsRM productsRM;

        for(int i=0;i<productsList.size();i++){
            productsRM = new ProductsRM(
                    productsList.get(i).getSku(),
                    productsList.get(i).getName(),
                    productsList.get(i).getId()
            );
            insertITemToRoom(productsRM);
        }

    }

    private void insertITemToRoom(ProductsRM productsRM) {
        Completable.fromAction(() ->
                roomDao.insert(productsRM)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d("TAG", "onComplete: Called");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("TAG", "onError: Called" + e.getMessage());
                    }
                });
    }

    private void addtoFirebase(List<products> products) {

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> product = new HashMap<>();
        for(int i=0;i<products.size();i++){
            product.put(products.get(i).getName(),products.get(i).getSku());
        }

        database.collection(Constants.KEY_PRODUCT_DB)
                .document("SKUName")
                .set(product)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(mContext, "Synced Successfully!", Toast.LENGTH_SHORT).show();
                            dismiss();
                        }
                        else{
                            Toast.makeText(mContext , "Something went wrong. Try Again.", Toast.LENGTH_SHORT).show();
                            Log.d("Add",task.getException().toString());
                        }
                    }
                });
    }
}
