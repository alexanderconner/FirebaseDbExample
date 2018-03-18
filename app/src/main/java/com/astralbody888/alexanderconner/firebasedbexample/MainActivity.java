package com.astralbody888.alexanderconner.firebasedbexample;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    final String TAG = "MainActivity";
    Button submitButton;
    Button findUserButton;
    EditText fnameText;
    EditText lnameText;
    EditText yobText;
    EditText findUserNameText;

    FirebaseFirestore firestoreDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        fnameText = (EditText) findViewById(R.id.FirstName_editText);
        lnameText = (EditText) findViewById(R.id.LastName_editText);
        yobText = (EditText) findViewById(R.id.YOB_editText);
        findUserNameText = (EditText) findViewById(R.id.findUser_editText);
        submitButton = (Button) findViewById(R.id.submit_button);
        findUserButton = (Button) findViewById(R.id.find_button);

        submitButton.setOnClickListener(submitInfoListener);
        findUserButton.setOnClickListener(findUserByName);

        firestoreDB = FirebaseFirestore.getInstance();

    }

    Button.OnClickListener submitInfoListener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            // Create a new user with a first and last name
            Map<String, Object> user = new HashMap<>();
            user.put("first", fnameText.getText().toString());
            user.put("last", lnameText.getText().toString());
            user.put("born", yobText.getText().toString());

            // Add a new document with a generated ID
            firestoreDB.collection("users")
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "Created user with ID: " + documentReference.getId());
                            Toast.makeText(MainActivity.this, "DocumentSnapshot added with ID: " + documentReference.getId(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });
        }
    };

    Button.OnClickListener findUserByName = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            Query userInfoQuery = firestoreDB.collection("users").whereEqualTo("first", findUserNameText.getText().toString());
            userInfoQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null  ) {
                            Log.d(TAG, "DocumentSnapshot data: " + querySnapshot.getDocuments());
                            Toast.makeText(MainActivity.this, "Year of birth: " +querySnapshot.getDocuments().get(0).get("born"), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "No such document");
                            Toast.makeText(MainActivity.this, "No such document", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }

                }
            });
        }
    };
}
