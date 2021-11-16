package com.grousale.grousource.customDialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.grousale.grousource.activity.AdminActivity;
import com.grousale.grousource.databinding.DialogSyncBinding;
import com.grousale.grousource.model.items;
import com.grousale.grousource.model.products;
import com.grousale.grousource.retrofit.ApiInterface;
import com.grousale.grousource.retrofit.RetrofitClientInstance;
import com.grousale.grousource.utility.Constants;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SyncProducts extends Dialog {

    DialogSyncBinding binding;
    Context mContext;

    public SyncProducts(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DialogSyncBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
                List<products> products = response.body().getProducts();
                addtoFirebase(products);

                 }

            @Override
            public void onFailure(Call<items> call, Throwable t)
            {
                Toast.makeText(mContext, "Failed: "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("Retrofit",t.getMessage()+ " "+ t.getLocalizedMessage()+" "+t.getCause());
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
