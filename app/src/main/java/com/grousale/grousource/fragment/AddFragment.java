package com.grousale.grousource.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.grousale.grousource.R;
import com.grousale.grousource.databinding.FragmentAddBinding;


public class AddFragment extends Fragment {

    FragmentAddBinding binding;
    String productName, productPrice, phone, address, landmark;
    EditText productNameET, productPriceET, phoneET, addressET, landmarkET;
    Button submit,detect;

    public AddFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValues()){

                }
            }
        });






        return binding.getRoot();
    }

    private boolean checkValues() {
        productName = productNameET.getText().toString().trim();
        productPrice = productPriceET.getText().toString().trim();
        phone = phoneET.getText().toString().trim();
        address = addressET.getText().toString().trim();
        landmark = landmarkET.getText().toString().trim();
        return true;
    }
}