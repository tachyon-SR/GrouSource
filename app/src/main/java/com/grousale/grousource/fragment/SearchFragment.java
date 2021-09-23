package com.grousale.grousource.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.grousale.grousource.R;
import com.grousale.grousource.model.product;
import com.grousale.grousource.utility.Constants;
import com.grousale.grousource.viewHolder.productViewHolder;

import org.jetbrains.annotations.NotNull;


public class SearchFragment extends Fragment {

    FirebaseFirestore db ;
    CollectionReference productRef;
    Query query;
    String searchQuery;
    EditText search;
    Button searchButton;
    RecyclerView recyclerView;
    FirestoreRecyclerAdapter<product, productViewHolder> adapter;


    public SearchFragment() {
    }


    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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

        View v = inflater.inflate(R.layout.fragment_search, container, false);

        search = v.findViewById(R.id.searchBar);
        searchButton = v.findViewById(R.id.searchButton);
        recyclerView = v.findViewById(R.id.productRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchQuery = search.getText().toString().trim();
                showData(searchQuery);
            }
        });

        db = FirebaseFirestore.getInstance();
        productRef = db.collection(Constants.KEY_PRODUCT_DB);





        return  v;
    }

    private void showData(String searchQuery) {
        query = productRef.whereGreaterThanOrEqualTo("productName",searchQuery);




        FirestoreRecyclerOptions<product> options =
                new FirestoreRecyclerOptions.Builder<product>()
                        .setQuery(query,product.class).build();


        adapter = new FirestoreRecyclerAdapter<product, productViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull productViewHolder holder, int position, @NonNull @NotNull product model) {

                holder.productName.setText(model.getProductName());
                holder.shopAddress.setText(model.getShopAddress());
                holder.shopName.setText(model.getShopName());
                Log.d("Image",model.getproductImageUrl());
                Glide.with(getActivity()).load(model.getproductImageUrl()).into(holder.productImage);
            }

            @NonNull
            @NotNull
            @Override
            public productViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item_product, parent, false);
                productViewHolder holder = new productViewHolder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();


    }

    @Override
    public void onStop() {
        super.onStop();
    }
}