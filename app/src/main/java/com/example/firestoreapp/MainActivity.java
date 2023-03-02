package com.example.firestoreapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private EditText nameET,emailET;
    TextView text;
    private Button saveBtn,readBtn;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";

    DocumentReference empRef = db.collection("Users").document("Employees");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Writing data
        nameET = findViewById(R.id.nameET);
        emailET = findViewById(R.id.emailET);
        saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveDataOnFireStore();
            }
        });

        //Reading data
        text = findViewById(R.id.text);
        readBtn = findViewById(R.id.readBtn);;

        readBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReadData();
            }


        });
    }

    private void ReadData() {
        empRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    String name = documentSnapshot.get(KEY_NAME).toString();
                    String email = documentSnapshot.get(KEY_EMAIL).toString();
                    Log.i("TAG", "username : " + name + "Email : " + email);
                    text.setText("username : " + name + "Email : " + email);
                }
            }
        });
    }
    private void SaveDataOnFireStore(){
        String name = nameET.getText().toString().trim();
        String email = emailET.getText().toString().trim();

        HashMap<String, Object> data = new HashMap<>();
        data.put(KEY_NAME,name);
        data.put(KEY_EMAIL, email);


        db.collection("Users").document("Employees").set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Name : " + name + "  and email : " + email + " added to firestore." , Toast.LENGTH_SHORT).show();
            }
        });
    }
}