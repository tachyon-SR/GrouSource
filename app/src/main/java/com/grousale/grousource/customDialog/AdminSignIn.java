package com.grousale.grousource.customDialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.grousale.grousource.MainActivity;
import com.grousale.grousource.R;
import com.grousale.grousource.activity.AdminActivity;
import com.grousale.grousource.activity.EditItemActivity;

public class AdminSignIn extends Dialog  {

    Context mcontext;
    String optionSelected,key;
    FirebaseFirestore db;
    Button submit;
    EditText editText ;

    public AdminSignIn(@NonNull Context context,String option) {
        super(context);

        mcontext = context;
        optionSelected = option;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.admin_sign_in);

        submit = findViewById(R.id.passwordSubmit);
        editText = findViewById(R.id.passwordText);


        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Document").document("Collection");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        
                        key = document.get("FirestoreID").toString();
                    } else {
                        Toast.makeText(mcontext, "Some error with fetching", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mcontext, "Please try again later", Toast.LENGTH_SHORT).show();
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pass = editText.getText().toString().trim();
                if(pass.equals(key)){
                    Toast.makeText(mcontext, "Correct Password", Toast.LENGTH_SHORT).show();
                    taketoActivity();
                }
            }
        });
    }

    private void taketoActivity() {
        switch (optionSelected){
            case "AddNew": {
                Intent intent = new Intent(mcontext, AdminActivity.class);
                mcontext.startActivity(intent);
                dismiss();
                break;
            }
            case "EditExisting": {
                Intent intent = new Intent(mcontext, EditItemActivity.class);
                mcontext.startActivity(intent);
                dismiss();
                break;
            }
        }
    }


}
