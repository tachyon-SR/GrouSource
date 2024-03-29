package com.grousale.grousource.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.grousale.grousource.LocationActivity;
import com.grousale.grousource.R;
import com.grousale.grousource.databinding.FragmentAddBinding;
import com.grousale.grousource.utility.Constants;
import com.grousale.grousource.utility.CustomPrefManager;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;


public class AddFragment extends Fragment {

    FragmentAddBinding binding;
    String productName, productPrice, phone, address, landmark, shopname;
    EditText productNameET, productPriceET, phoneET, addressET, landmarkET, shopNameET;
    ImageView imageView;
    Button submit,detect,available, notAvailable;
    private String downloadUrl="", storageDIR, finalText;
    private ProgressDialog progressDialog;
    private Uri uri;
    private  int availability=1;
    CustomPrefManager customPrefManager;

    public AddFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       customPrefManager = new CustomPrefManager(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentAddBinding.inflate(inflater);
        productNameET = binding.productName;
        productPriceET = binding.productPrice;
        phoneET = binding.productPhone;
        addressET = binding.productAddress;
        landmarkET = binding.productLandmark;
        submit = binding.productSubmit;
        shopNameET = binding.productShopName;
        imageView = binding.productImage;
        detect = binding.productDetect;
        available = binding.availableButton;
        notAvailable = binding.notAvailableButton;

        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LocationActivity.class);
                startActivity(intent);
            }
        });

        available.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                available.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#14ff14")));
                notAvailable.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                availability=1;
            }
        });

        notAvailable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                available.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                notAvailable.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF3131")));
                availability=0;
            }
        });

        customPrefManager.putString(Constants.LATITUDE,"Not provided");
        customPrefManager.putString(Constants.LONGITUDE,"Not provided");


        progressDialog = new ProgressDialog(getContext());

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValues()){
                    if(downloadUrl.equals("")){
                        Toast.makeText(getContext(), "Please Upload image", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    addToFireStore();
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean b = checkValues();
                if(b) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/");
                    startActivityForResult(intent, 104);
                }
            }
        });

        return binding.getRoot();
    }

    private void addToFireStore() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> product = new HashMap<>();
        product.put(Constants.KEY_PRODUCT_NAME,productName);
        product.put(Constants.KEY_PRODUCT_PRICE,productPrice);
        product.put(Constants.KEY_SHOP_ADDRESS,address);
        product.put(Constants.KEY_SHOP_LANDMARK,landmark);
        product.put(Constants.KEY_SHOP_NAME,shopname);
        product.put(Constants.KEY_SHOP_PHONE,phone);
        product.put(Constants.KEY_PRODUCT_IMAGE_URL,downloadUrl);
        product.put(Constants.LATITUDE,customPrefManager.getString(Constants.LATITUDE));
        product.put(Constants.LONGITUDE,customPrefManager.getString(Constants.LONGITUDE));
        product.put(Constants.KEY_AVAILABILITY,availability);

        database.collection(Constants.KEY_FIELDPRODUCT_DB)
                .document(productName+phone)
                .set(product)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getContext(), "Product Added Successfully!", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getContext(), "Something went wrong. Try Again.", Toast.LENGTH_SHORT).show();
                            Log.d("Add",task.getException().toString());
                        }
                    }
                });



    }

    private boolean checkValues() {
        productName = productNameET.getText().toString().trim();
        productPrice = productPriceET.getText().toString().trim();
        phone = phoneET.getText().toString().trim();
        address = addressET.getText().toString().trim();
        landmark = landmarkET.getText().toString().trim();
        shopname = shopNameET.getText().toString().trim();

        if(TextUtils.isEmpty(productName)){
            Toast.makeText(getContext(), "Name needed" , Toast.LENGTH_SHORT).show();
            return  false;
        }

        else if(TextUtils.isEmpty(shopname) && availability==1){
            Toast.makeText(getContext(), "Shop name needed", Toast.LENGTH_SHORT).show();
            return  false;
        }

        else if(TextUtils.isEmpty(address) && availability==1){
            Toast.makeText(getContext(), "Address needed", Toast.LENGTH_SHORT).show();
            return  false;
        }

        else if(TextUtils.isEmpty(landmark) && availability==1){
            Toast.makeText(getContext(), "Landmark needed", Toast.LENGTH_SHORT).show();
            return  false;
        }

        else if(TextUtils.isEmpty(String.valueOf(productPrice)) && availability==1){
            Toast.makeText(getContext(), "Price needed", Toast.LENGTH_SHORT).show();
            return  false;
        }

        else if(TextUtils.isEmpty(String.valueOf(phone)) && availability==1){
            Toast.makeText(getContext(), "Phone needed", Toast.LENGTH_SHORT).show();
            return  false;
        }

        else if (customPrefManager.getString(Constants.LONGITUDE)=="Not Provided"){
            Toast.makeText(getContext(), "Detect Location", Toast.LENGTH_SHORT).show();
            return  false;
        }

        else return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        

        if (resultCode == Activity.RESULT_OK && requestCode == 104) {
            uri = data.getData();
            String s = uri.toString();


            if (s.startsWith("content://")) {
                Cursor cursor = null;
                try {
                    cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        finalText = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        if (finalText != null) {
                            uploadImageToStorage();
                        }
                    }
                } finally {
                    assert cursor != null;
                    cursor.close();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadImageToStorage() {

        StorageReference mStorage;
        storageDIR = productName+phone;
        mStorage = FirebaseStorage.getInstance().getReference(Constants.KEY_FIELDPRODUCT_DB).child(storageDIR);
        progressDialog.show();
        StorageReference mReference = mStorage.child(finalText);
        int scaleDivider = 4;
        try {
            //Reducing the file size
            Bitmap fileBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
            int scaleWidth = fileBitmap.getWidth() / scaleDivider;
            int scaleHeight = fileBitmap.getHeight() / scaleDivider;
            byte[] downloadImageBytes = getDownsizedImageBytes(fileBitmap, scaleWidth, scaleHeight);

            mReference.putBytes(downloadImageBytes).addOnSuccessListener(
                    taskSnapshot -> {
                        mReference.getDownloadUrl().addOnCompleteListener(
                                task -> {
                                    downloadUrl = task.getResult().toString();
                                    Glide.with(this).load(downloadUrl).into(imageView);
                                }
                        );
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Successfully Uploaded", Toast.LENGTH_LONG)
                                .show();

                    }
            ).addOnProgressListener(taskSnapshot -> {
                long progress = (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressDialog.setTitle("Uploading");
                progressDialog.setMessage(progress + " % Uploaded");
            });

        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error Occur: $e", Toast.LENGTH_SHORT).show();
        }

    }

    private byte[] getDownsizedImageBytes(Bitmap fileBitmap, int scaleWidth, int scaleHeight) {
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(fileBitmap, scaleWidth, scaleHeight, true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }
}