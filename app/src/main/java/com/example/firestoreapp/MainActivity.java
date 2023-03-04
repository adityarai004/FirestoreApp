package com.example.firestoreapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private EditText nameET,emailET;
    TextView text;
    private Button saveBtn,readBtn,deleteBtn;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private DocumentReference empRef = db.collection("Users").document("Employees");

    CollectionReference collectionReference = db.collection("Users");
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
//                use SaveDataOnFireStore when need to store data in the same document
//                SaveDataOnFireStore();
                // use SaveDataToNewDocument for creating new document and adding new data
                SaveDataToNewDocument();
            }
        });

        //Reading data
        text = findViewById(R.id.text);
        readBtn = findViewById(R.id.readBtn);;

        readBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ReadData();
                GetAllDocumentsInCollection();
            }
        });

        deleteBtn = findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteData(emailET.getText().toString());
            }
        });


    }

    private void GetAllDocumentsInCollection() {
        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            String data = "";
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot snapshots : queryDocumentSnapshots ){
//                    Log.v("TAG", snapshots.getString(KEY_NAME));
//                    retrieving into objects
                    Employee employee = snapshots.toObject(Employee.class);
                    data += "Name : " + employee.getName() + " Email : " + employee.getEmail() + "\n";

                }
                text.setText(data);
            }
        });
    }

    private void SaveDataToNewDocument() {
        String name = nameET.getText().toString();
        String email = emailET.getText().toString();

        Employee employee = new Employee(name,email);
        collectionReference.add(employee);
    }

    private void DeleteData(String key) {
        //both the method works but differently
//        empRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void unused) {
//                Toast.makeText(MainActivity.this, "Data deleted", Toast.LENGTH_SHORT).show();
//            }
//        });

        //deleting value according to name
        empRef.update(KEY_NAME, FieldValue.delete());

        //deleting value according to Email
        empRef.update(KEY_EMAIL,FieldValue.delete());
    }

    //Reading simple data from firestore (Retrieving data)
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

    ////Saving simple data on firestore (Creating data)
    private void SaveDataOnFireStore(){
        String name = nameET.getText().toString().trim();
        String email = emailET.getText().toString().trim();
        Employee e1 = new Employee();
        e1.setName(name);
        e1.setEmail(email);


// this hashmap method works too but we will work on POJO too
//        HashMap<String, Object> data = new HashMap<>();
//        data.put(KEY_NAME,name);
//        data.put(KEY_EMAIL, email);


        db.collection("Users").document("Employees").set(e1).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Name : " + name + "  and email : " + email + " added to firestore." , Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Listening to snapshot changes
    @Override
    protected void onStart() {
        super.onStart();
        GetAllDocumentsInCollection();
        empRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Toast.makeText(MainActivity.this, "Error Found", Toast.LENGTH_SHORT).show();
                }
                if(value != null && value.exists()){

                    //Getting  data (via custom object)
                    Employee employee = value.toObject(Employee.class);

//                    String name = value.get(KEY_NAME).toString();
//                    String email = value.get(KEY_EMAIL).toString();

                    text.setText("username : " + employee.getName() + "\nEmail : " + employee.getEmail());
                }
            }
        });
    }

    //Creating multiple documents and retrieving multiple documents

}