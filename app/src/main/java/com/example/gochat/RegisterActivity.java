package com.example.gochat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private MaterialEditText _username,_email,_password;
    private Button tbn_register;

    private FirebaseAuth auth;
    private DatabaseReference reference;
      private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        _username=(MaterialEditText)findViewById(R.id.username);
        _email=(MaterialEditText)findViewById(R.id.email);
        _password=(MaterialEditText)findViewById(R.id.password);

        reference=FirebaseDatabase.getInstance().getReference().child("Users");
        auth= FirebaseAuth.getInstance();


    }

    //creating a UserWith email and password

    public void registerButtonClicked(View view)
    {
        final String email=_email.getText().toString().trim();
        final String password=_password.getText().toString().trim();
        final String username=_username.getText().toString().trim();

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(username))
        {

                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful())
                        {



                            String user_id = auth.getCurrentUser().getUid();

                            DatabaseReference newUser = reference.child(user_id);


                            newUser.child("id" ).setValue(user_id);
                            newUser.child("username").setValue( username);
                            newUser.child("imageURL").setValue("default");
                            newUser.child("status").setValue("offline");
                            newUser.child("search").setValue(username.toLowerCase());

                            Intent intent=new Intent(RegisterActivity.this,Main2Activity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                        }


                    }
                });


        }
        else
        {
            Toast.makeText(RegisterActivity.this,"All field are required !!",Toast.LENGTH_SHORT).show();
        }

    }


}
