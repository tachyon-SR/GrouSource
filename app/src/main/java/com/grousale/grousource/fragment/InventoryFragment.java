package com.grousale.grousource.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.grousale.grousource.R;

import com.blikoon.qrcodescanner.QrCodeActivity;
import com.grousale.grousource.activity.AdminActivity;
import com.grousale.grousource.activity.BarCodeScannerActivity;
import com.grousale.grousource.adapters.HorizontalRecyclerView;
import com.grousale.grousource.databinding.FragmentInventoryBinding;
import com.grousale.grousource.utility.Constants;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.jetbrains.annotations.NotNull;

import java.text.CharacterIterator;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class InventoryFragment extends Fragment {


    Button button;
    String clickedButton,scanType,quantity="1",remarks,trkID,returnType;
    List<String> skuID = new ArrayList<>();
    private static final int REQUEST_CODE_QR_SCAN = 101;
    public static final int SELECT_PICTURES =102;
    public static final int SCAN_SKU =103;
    public static final int SCAN_TRKID =104;
    ArrayList<Uri> uri = new ArrayList<>();
    RecyclerView recyclerView;
    HorizontalRecyclerView adapter;
    Button imageChoose,scanSKU,scanID,inScan,outScan,submit,goodReturn,badReturn,fraudReturn,repairReturn,replaceReturn;
    FragmentInventoryBinding binding;
    ImageView amazon,flipkart,meesho,maizic;
    TextView grousale,store,customer,newProduct;
    EditText quantityET,remarksET;
    View[] buttons,returnButtons ;



    public InventoryFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static InventoryFragment newInstance(String param1, String param2) {
        InventoryFragment fragment = new InventoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentInventoryBinding.inflate(inflater);


        imageChoose = binding.chooseImage;
        recyclerView = binding.imageRecyclerView;
        scanSKU=binding.sku;
        scanID=binding.trackingID;
        amazon=binding.amazon;
        flipkart=binding.flipkart;
        meesho=binding.meesho;
        maizic=binding.maizic;
        grousale=binding.grousale;
        store=binding.store;
        customer=binding.customer;
        inScan=binding.inScan;
        outScan=binding.outScan;
        newProduct = binding.newProduct;
        goodReturn = binding.goodReturn;
        badReturn = binding.badReturn;
        fraudReturn = binding.fraudReturn;
        repairReturn = binding.repairReturn;
        replaceReturn = binding.replaceReturn;

        buttons = new View[]{amazon, flipkart, meesho, maizic, grousale, store, customer,newProduct};
        returnButtons = new View[]{goodReturn,badReturn,fraudReturn,repairReturn,replaceReturn};

        amazon.setOnClickListener(view -> {enableButton(amazon);clickedButton="Amazon"; });
        flipkart.setOnClickListener(view -> {enableButton(flipkart);clickedButton="Flipkart";});
        meesho.setOnClickListener(view -> {enableButton(meesho);clickedButton="Meesho";});
        maizic.setOnClickListener(view -> {enableButton(maizic);clickedButton="Maizic";});
        grousale.setOnClickListener(view -> {enableButton(grousale);clickedButton="Grousale";});
        store.setOnClickListener(view -> {enableButton(store);clickedButton="Store";});
        customer.setOnClickListener(view -> {enableButton(customer);clickedButton="Customer";});
        newProduct.setOnClickListener(view -> {enableButton(newProduct);clickedButton="newProduct";binding.returnLL.setVisibility(View.GONE);});


        goodReturn.setOnClickListener(view -> {
            enableReturnButton(goodReturn);returnType="good";
            imageChoose.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);});
        badReturn.setOnClickListener(view -> {
            enableReturnButton(badReturn);returnType="bad";
            imageChoose.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);});
        fraudReturn.setOnClickListener(view -> {
            enableReturnButton(fraudReturn);returnType="fraud";
            imageChoose.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);});
        replaceReturn.setOnClickListener(view -> {
            enableReturnButton(fraudReturn);returnType="replace";
            imageChoose.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);});
        repairReturn.setOnClickListener(view -> {
            enableReturnButton(fraudReturn);returnType="repair";
            imageChoose.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);});

        inScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inScan.setAlpha(1F);
                outScan.setAlpha(0.3F);
                scanType = "In";
                newProduct.setVisibility(View.VISIBLE);
                binding.returnLL.setVisibility(View.VISIBLE);
            }
        });

        outScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                outScan.setAlpha(1F);
                inScan.setAlpha(0.3F);
                scanType = "Out";
                binding.returnLL.setVisibility(View.GONE);
                imageChoose.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                newProduct.setVisibility(View.GONE);

            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));


        imageChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURES);             }
        });
        scanID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPermission()) {
                    Intent intent = new Intent(getActivity(), BarCodeScannerActivity.class);
                    startActivityForResult(intent, SCAN_TRKID);
                }
            }
        });
        scanSKU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPermission()) {
                    Intent intent = new Intent(getActivity(), BarCodeScannerActivity.class);
                    startActivityForResult(intent, SCAN_SKU);
                }
            }
        });

        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(clickedButton==null){
                    Toast.makeText(getContext(), "Please chose a platform", Toast.LENGTH_SHORT).show();
                    return;
                }
                remarks = binding.remarks.getText().toString().trim();
                quantity = binding.quantity.getText().toString().trim();
                addToFirestore();
            }
        });

        return binding.getRoot();

    }

    private void addToFirestore() {
        String db = null,documentId;
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> product = new HashMap<>();
        product.put(Constants.KEY_SKU,skuID);
        product.put(Constants.KEY_TRACKINGID,trkID);
        product.put(Constants.KEY_QUANTITY, quantity);
        product.put(Constants.KEY_PLATFORM,clickedButton);
        documentId=trkID;

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        product.put(Constants.KEY_DATETIME,formatter.format(date));

        if(scanType.equals("In")){
           db = Constants.KEY_INSCANDB;
            product.put(Constants.KEY_RETURNTYPE,returnType);
           if(clickedButton.equals("newProduct")) {
               db=Constants.KEY_PRODUCT_DB;
               documentId=skuID.get(0);
               if(skuID.get(0) == null){
                   Toast.makeText(getActivity(), "Please scan sku", Toast.LENGTH_SHORT).show();
                   return;
               }
               Toast.makeText(getActivity(), "ID"+ documentId, Toast.LENGTH_SHORT).show();
               product.remove(Constants.KEY_RETURNTYPE);
               product.remove(Constants.KEY_PLATFORM);
               product.remove(Constants.KEY_TRACKINGID);
           }
        }
        else db = Constants.KEY_OUTSCANDB;
        if(trkID == null){
            Toast.makeText(getActivity(), "Please scan trackingID", Toast.LENGTH_SHORT).show();
            return;
        }
        database.collection(db)
                .document(documentId)
                .set(product)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getContext(), "Adding to database", Toast.LENGTH_SHORT).show();
                            addtoProductDB();
                            reset();
                            }

                        else{
                            Toast.makeText(getContext(), "Something went wrong. Try Again.", Toast.LENGTH_SHORT).show();
                            Log.d("Add",task.getException().toString());
                        }
                    }
                });
    }

    private void addtoProductDB() {

    }

    private void reset() {
        binding.skuText.setText("Scan SKU");
        binding.trackingIDtext.setText("Scan Tracking ID");
        binding.skuText.setTextColor(Color.parseColor("#FF0000"));
        binding.trackingIDtext.setTextColor(Color.parseColor("#FF0000"));
        uri.clear();
        skuID.clear();
        trkID=null;
    }

    private boolean checkPermission() {
        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            Dexter.withContext(getContext())
                    .withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
                @Override
                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                }

                @Override
                public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    if (permissionDeniedResponse.isPermanentlyDenied()) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", getActivity().getPackageName(), null));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }

                    Toast.makeText(getContext(), "Permission is required for QR Code scanning", Toast.LENGTH_SHORT).show();
                    onStop();

                }

                @Override
                public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();

                }
            }).onSameThread()
                    .check();
        }

        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED)
        {
            Dexter.withContext(getContext())
                    .withPermission(Manifest.permission.VIBRATE).withListener(new PermissionListener() {
                @Override
                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                }

                @Override
                public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    if (permissionDeniedResponse.isPermanentlyDenied()) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", getActivity().getPackageName(), null));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }

                    Toast.makeText(getContext(), "Permission is required for QR Code scanning", Toast.LENGTH_SHORT).show();
                    onStop();

                }

                @Override
                public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();

                }
            }).onSameThread()
                    .check();
        }

        return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SELECT_PICTURES) {
            if(resultCode == Activity.RESULT_OK) {
                if(data.getClipData() != null) {
                    int count = data.getClipData().getItemCount(); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
                    for(int i = 0; i < count; i++) {
                        uri.add(data.getClipData().getItemAt(i).getUri());
                        Log.d("Images", uri.get(i).toString());
                        //do something with the image (save it to some directory or whatever you need to do with it here)
                    }
                    adapter = new HorizontalRecyclerView(uri);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            } else if(data.getData() != null) {
                String imagePath = data.getData().getPath();
                //do something with the image (save it to some directory or whatever you need to do with it here)
            }
        }

        if(requestCode == SCAN_SKU) {
            if(resultCode == Activity.RESULT_OK) {
                if(data.getExtras() != null) {
                    String skuresult = data.getExtras().getString("SCAN_RESULT");
                    skuID.add(skuresult);
                    StringBuilder builder = new StringBuilder();
                    for (String skuitem : skuID) {
                        builder.append(skuitem + "\n");
                    }
                    binding.skuText.setText(builder.toString());
                    binding.skuText.setTextColor(Color.parseColor("#00FF00"));

                }
            } else  {
                Toast.makeText(getActivity(), "No result from scanning, try again", Toast.LENGTH_SHORT).show();
                //do something with the image (save it to some directory or whatever you need to do with it here)
            }
        }

        if(requestCode == SCAN_TRKID) {
            if(resultCode == Activity.RESULT_OK) {
                if(data.getExtras() != null) {
                    trkID = data.getExtras().getString("SCAN_RESULT");
                    binding.trackingIDtext.setText(trkID);
                    binding.trackingIDtext.setTextColor(Color.parseColor("#00FF00"));

                }
            } else  {
                Toast.makeText(getActivity(), "No result from scanning,try again", Toast.LENGTH_SHORT).show();
                //do something with the image (save it to some directory or whatever you need to do with it here)
            }
        }
    }



    public void enableButton(View buttonName){
        for (View view : buttons) {
            if (view.equals(buttonName)) {
                view.setAlpha(1F);
            }
            else view.setAlpha(0.3F);
        }
    }

    public void enableReturnButton(View buttonName){
        for (View view : returnButtons) {
            if (view.equals(buttonName)) {
                view.setAlpha(1F);
            }
            else view.setAlpha(0.3F);
        }
    }


}