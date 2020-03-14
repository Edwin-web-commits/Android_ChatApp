package com.example.gochat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class LoginActivity extends AppCompatActivity {

    private MaterialEditText _email,_password;
    private Button tbn_login;

    private FirebaseAuth auth;
    private DatabaseReference reference;
    private FirebaseUser firebaseUser;

    private TextView forgot_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        _email=(MaterialEditText)findViewById(R.id.email);
        _password=(MaterialEditText)findViewById(R.id.password);

        reference= FirebaseDatabase.getInstance().getReference().child("Users");
        auth= FirebaseAuth.getInstance();

        forgot_password=(TextView)findViewById(R.id.forgot_password);

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resetIntent=new Intent(LoginActivity.this,ResetPasswordActivity.class);
                startActivity(resetIntent);
            }
        });


    }
    public void loginButtonClicked(View view) {
        String email = _email.getText().toString().trim();
        String password = _password.getText().toString().trim();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {
                        checkIfUserExists();
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this,"Authentication failed ",Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }
        else {
            Toast.makeText(LoginActivity.this, "All fields are required ", Toast.LENGTH_SHORT).show();
        }
    }


    public void checkIfUserExists()
    {
        final String user_id=auth.getCurrentUser().getUid();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(user_id))
                {
                    Intent intent=new Intent(LoginActivity.this,Main2Activity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    //finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
