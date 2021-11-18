package com.grousale.grousource.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.grousale.grousource.databinding.ActivityAdminBinding;
import com.grousale.grousource.model.productItem;
import com.grousale.grousource.model.products;
import com.grousale.grousource.roomdatabase.RoomDao;
import com.grousale.grousource.utility.Constants;
import com.grousale.grousource.utility.ProductDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.AndroidSchedulers;

public class AdminActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    FirebaseFirestore db;
    String key,pass,prodID,remarks,quantity,shelf;
    List<productItem> itemIDList = new ArrayList<>();
    String TAG = "AdminActivity";
    ActivityAdminBinding binding;
    public static final int SCAN_ITEMID =103;
    public static final int SCAN_PRODID =104;
    Spinner spinner;
    List<products> prodList = new ArrayList<>();
    RoomDao roomDao;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        spinner = binding.spinnerSearch;

        this.setTitle("Admin Activity");
        prodList = ProductDatabase.getProductsList();

        binding.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Search", "Button Clicked");
                String searchQuery = binding.searchName.getText().toString().trim();
                Log.d("Search", "Query"+searchQuery);
                setSpinnervalues(searchQuery);
            }
        });

        binding.productID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPermission()) {
                    if(!itemIDList.isEmpty()){
                        Toast.makeText(AdminActivity.this, "Cannot change Product ID with SKU present", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Intent intent = new Intent(AdminActivity.this, BarCodeScannerActivity.class);
                        startActivityForResult(intent, SCAN_PRODID);
                    }
                }
            }
        });

        binding.itemID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPermission()){
                    Intent intent = new Intent(AdminActivity.this, BarCodeScannerActivity.class);
                    startActivityForResult(intent, SCAN_ITEMID);
                }
            }
        });

        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemIDList.isEmpty()){
                    Toast.makeText(AdminActivity.this, "Please scan at least one item", Toast.LENGTH_SHORT).show();
                    return;
                }
                remarks = binding.remarks.getText().toString().trim();
                quantity = binding.quantity.getText().toString().trim();
                shelf = binding.shelf.getText().toString().trim();
                addToFirestore();
                reset();
            }
        });



    }

    private void setSpinnervalues(String searchQuery) {

        Flowable<List<String>> listFlowableMyDeals = roomDao.searchResults(searchQuery);
        final List<String>[] prodList = new List[0];
        Disposable disposable = listFlowableMyDeals
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    prodList[0] = list;
                });

        compositeDisposable.add(disposable);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, prodList[0]);
        spinner.setAdapter(adapter);
    }

    private void addToFirestore() {

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> product = new HashMap<>();
        product.put(Constants.KEY_SKU,prodID);
        product.put(Constants.KEY_QUANTITY, quantity); //TODO To be changed to add everytime
        product.put(Constants.KEY_REMARKS,remarks);
        product.put(Constants.KEY_ITEMS,itemIDList);

            String db=Constants.KEY_PRODUCT_DB;
                if(prodID == null){
                    Toast.makeText(AdminActivity.this, "Please scan sku", Toast.LENGTH_SHORT).show();
                    return;
                }

        database.collection(db)
                .document(prodID)
                .set(product)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(AdminActivity.this, "Product Added Successfully!", Toast.LENGTH_SHORT).show();
                            reset();
                        }
                        else{
                            Toast.makeText(AdminActivity.this , "Something went wrong. Try Again.", Toast.LENGTH_SHORT).show();
                            Log.d("Add",task.getException().toString());
                        }
                    }
                });
    }

    private void reset() {
        binding.itemIDText.setText("Scan ID");
        binding.productIDtext.setText("Scan Product ID");
        binding.itemIDText.setTextColor(Color.parseColor("#FF0000"));
        binding.productIDtext.setTextColor(Color.parseColor("#FF0000"));
        itemIDList.clear();
        prodID=null;
        remarks=null;
        shelf = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SCAN_ITEMID) {
            if(resultCode == Activity.RESULT_OK) {
                if(data.getExtras() != null) {
                    productItem item = new productItem();
                    item.setItemID(data.getExtras().getString("SCAN_RESULT"));
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = new Date();
                    item.setTimeIN(formatter.format(date));
                    itemIDList.add(item);
                    StringBuilder builder = new StringBuilder();
                    for (productItem Iditem : itemIDList) {
                        builder.append(Iditem.getItemID() + "\n");
                    }
                    binding.itemIDText.setText(builder.toString());
                    binding.itemIDText.setTextColor(Color.parseColor("#00FF00"));
                    binding.quantity.setText(String.valueOf(itemIDList.size()));

                }
            } else  {
                Toast.makeText(AdminActivity.this, "No result from scanning, try again", Toast.LENGTH_SHORT).show();
                //do something with the image (save it to some directory or whatever you need to do with it here)
            }
        }


        if(requestCode == SCAN_PRODID) {
            if(resultCode == Activity.RESULT_OK) {
                if(data.getExtras() != null) {
                    prodID = data.getExtras().getString("SCAN_RESULT");
                    binding.productIDtext.setText(prodID);
                    binding.productIDtext.setTextColor(Color.parseColor("#00FF00"));

                }
            } else  {
                Toast.makeText(AdminActivity.this, "No result from scanning,try again", Toast.LENGTH_SHORT).show();
                //do something with the image (save it to some directory or whatever you need to do with it here)
            }
        }
    }

    private boolean checkPermission() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            Dexter.withContext(this)
                    .withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
                @Override
                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                }

                @Override
                public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    if (permissionDeniedResponse.isPermanentlyDenied()) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", getPackageName(), null));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }

                    Toast.makeText(AdminActivity.this , "Permission is required for QR Code scanning", Toast.LENGTH_SHORT).show();
                    onStop();

                }

                @Override
                public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();

                }
            }).onSameThread()
                    .check();
        }

        if(ActivityCompat.checkSelfPermission(AdminActivity.this, Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED)
        {
            Dexter.withContext(AdminActivity.this)
                    .withPermission(Manifest.permission.VIBRATE).withListener(new PermissionListener() {
                @Override
                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                }

                @Override
                public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    if (permissionDeniedResponse.isPermanentlyDenied()) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", getPackageName(), null));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }

                    Toast.makeText(AdminActivity.this, "Permission is required for QR Code scanning", Toast.LENGTH_SHORT).show();
                    onStop();

                }

                @Override
                public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();

                }
            }).onSameThread()
                    .check();
        }

        return ActivityCompat.checkSelfPermission(AdminActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(AdminActivity.this, Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String choice = (String) adapterView.getItemAtPosition(i);
        Toast.makeText(this, choice, Toast.LENGTH_SHORT).show();
        for (i = 0; i < prodList.size(); i++) {
            if (prodList.get(i).getName().equals(choice)) {
                prodID=prodList.get(i).getSku();
                binding.productIDtext.setText(prodID);

            }

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
