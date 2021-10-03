package com.grousale.grousource.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.grousale.grousource.R;
import com.grousale.grousource.databinding.ActivityAdminBinding;

public class AdminActivity extends AppCompatActivity {

    FirebaseFirestore db;
    String key,pass;
    String TAG = "Adminctivity";
    ActivityAdminBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        this.setTitle("Admin Activity");

        binding.submitPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pass = binding.password.getText().toString().trim();
                if(pass.equals(key)){
                    Toast.makeText(AdminActivity.this, "Correct Password", Toast.LENGTH_SHORT).show();
                    binding.submitPassword.setVisibility(View.GONE);
                    binding.password.setVisibility(View.GONE);
                }

                else Toast.makeText(AdminActivity.this, "Incorrect password.", Toast.LENGTH_SHORT).show();
            }
        });

        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Document").document("Collection");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        key = document.get("FirestoreID").toString();
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}