package com.example.gochat;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }
    public void loginActivityButtonClicked(View view)
    {
        Intent loginIntent=new Intent(MainActivity.this,LoginActivity.class);
        startActivity(loginIntent);
    }
    public void registerActivityButtonClicked(View view)
    {
        Intent registerIntent=new Intent(MainActivity.this,RegisterActivity.class);
        startActivity(registerIntent);
    }

    @Override
    protected void onStart() {

        super.onStart();

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        //check if the user logged in

        if(firebaseUser !=null){
            Intent intent=new Intent(MainActivity.this,Main2Activity.class);
            startActivity(intent);
        }


    }
}
