package com.example.gochat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.gochat.Fragments.ChatFragment;
import com.example.gochat.Fragments.ProfileFragment;
import com.example.gochat.Fragments.UsersFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Main2Activity extends AppCompatActivity {

    private CircleImageView profile_image;
    private TextView username;
    private DatabaseReference reference;
    private FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);




        profile_image=(CircleImageView)findViewById(R.id.profile_image);
        username=(TextView)findViewById(R.id.Username);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());


        //extracting user information from the database using its class

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user =dataSnapshot.getValue(User.class);

                username.setText(user.getUsername());


               if(user.getImageURL().equals("default"))
               {
                   profile_image.setImageResource(R.mipmap.ic_launcher);
               }
               else
               {


                   Picasso.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
               }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        TabLayout tabLayout= findViewById(R.id.tab_layout);
        ViewPager viewPager=findViewById(R.id.view_pager);

        //setting fragments to the viewPager and their titles

        ViewpagerAdapter viewpagerAdapter=new ViewpagerAdapter(getSupportFragmentManager());

        viewpagerAdapter.addFragment(new ChatFragment(),"Chats");
        viewpagerAdapter.addFragment(new UsersFragment(),"Users");
        viewpagerAdapter.addFragment(new ProfileFragment(),"Profile");

        viewPager.setAdapter(viewpagerAdapter);

        tabLayout.setupWithViewPager(viewPager);  //setting the titles on the tablayout


    }

    //creating viewpager Adapter

    class ViewpagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;


        public ViewpagerAdapter(@NonNull FragmentManager fm) {

            super(fm);

            this.fragments=new ArrayList<>();
            this.titles=new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public  void addFragment(Fragment fragment, String title)
        {
            fragments.add(fragment);
            titles.add(title);
        }

        //Ctrl + o

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {

            return titles.get(position);
        }
    }
    //user status function

    public void status(String status)
    {
        reference=FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("status",status);

        reference.updateChildren(hashMap);



    }

    @Override
    protected void onResume() {
        super.onResume();

        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();

        status("offline");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId())
        {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent =new Intent(Main2Activity.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
              return  true;
        }
        return false;
    }
}
