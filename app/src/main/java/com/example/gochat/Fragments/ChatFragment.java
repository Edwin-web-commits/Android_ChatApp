package com.example.gochat.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.gochat.Adapter.userAdapter;
import com.example.gochat.Chat;
import com.example.gochat.Chatlist;
import com.example.gochat.Notifications.Token;
import com.example.gochat.R;
import com.example.gochat.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



// This Chat fragment is for Displaying all the users that you have chats(messages) with.

public class ChatFragment extends Fragment {

   private RecyclerView recyclerView;
   private userAdapter useradapter;
   private List<User> mUsers;

   private FirebaseUser fuser;
   private DatabaseReference reference;
   private List<Chatlist> userList ;

   private List<User> usersToBeAdded;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView=view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fuser= FirebaseAuth.getInstance().getCurrentUser();

        userList=new ArrayList<>();

     // get all items in the ChatList

     reference=FirebaseDatabase.getInstance().getReference().child("ChatList").child(fuser.getUid());
     reference.addValueEventListener(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

             userList.clear();
             for(DataSnapshot snapshot: dataSnapshot.getChildren())
             {
                 Chatlist chatlist=snapshot.getValue(Chatlist.class);

                 userList.add(chatlist);

                 chatList();


             }

         }

         @Override
         public void onCancelled(@NonNull DatabaseError databaseError) {

         }
     });


       updateToken(FirebaseInstanceId.getInstance().getToken());

        return view;

    }

    //update the token in the database under the id of the current user
    private void updateToken(String token)
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Tokens");
        Token token1=new Token(token);
        reference.child(fuser.getUid()).setValue(token1);
    }


     //This function is going to check the id of the user in the current iterator with the
    // id of the user in the current iterator in the userList ... and then add the user to the mUsers and then to the userAdapter.
    private void chatList() {

        mUsers=new ArrayList<>();

        reference=FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mUsers.clear();

                for(DataSnapshot snapshot: dataSnapshot.getChildren())
                {
                    User user=snapshot.getValue(User.class);

                    for(Chatlist chatlist: userList)
                    {
                        if(user.getId().equals(chatlist.getId()))
                        { //the user is a chat (has messaged the current user or the current user messaged him)
                            mUsers.add(user);  // add that user in the mUsers arrayList
                        }
                    }
                }
                useradapter=new userAdapter(getContext(),mUsers,true);
                recyclerView.setAdapter(useradapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
