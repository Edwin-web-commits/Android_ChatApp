package com.example.gochat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gochat.Adapter.MessageAdapter;
import com.example.gochat.Fragments.APIService;
import com.example.gochat.Notifications.Client;
import com.example.gochat.Notifications.Data;
import com.example.gochat.Notifications.MyResponse;
import com.example.gochat.Notifications.Sender;
import com.example.gochat.Notifications.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {

    private CircleImageView profile_image;
    private TextView username;

    private FirebaseUser fuser;
    private DatabaseReference reference;
    private DatabaseReference chatRef;


    private ImageButton btn_send;
    private EditText text_send;


    private MessageAdapter messageAdapter;
    private List<Chat> mChat;
    private RecyclerView recyclerView;


    Intent intent;
    private String userid;

    private  ValueEventListener seenListener;


    APIService apiService;
    boolean notify =false ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        btn_send=(ImageButton)findViewById(R.id.btn_send);
        text_send=(EditText)findViewById(R.id.text_send);   //typed message

        recyclerView=(RecyclerView) findViewById(R.id.Recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MessageActivity.this,Main2Activity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        apiService= Client.getClient("https://fcm.googleapis.com/").create(APIService.class);




        profile_image=findViewById(R.id.profile_image);
        username=findViewById(R.id.Username);

        intent=getIntent();
         userid=intent.getStringExtra("userid");  //id of the selected user

        fuser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Users").child(userid);

        chatRef=FirebaseDatabase.getInstance().getReference().child("Chats");

        //retrieving data from database corresponding to the userid

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);

                username.setText(user.getUsername());

                if ("default".equals(user.getImageURL())) {


                    Picasso.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);


                }
                else {

                    profile_image.setImageResource(R.mipmap.ic_launcher);
                }

                readMessage(fuser.getUid(),userid,user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



      seenMessage(userid);

    }


    public void seenMessage(final String userid)
    {

        reference = FirebaseDatabase.getInstance().getReference().child("Chats");
        seenListener=reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot: dataSnapshot.getChildren())
                {
                    Chat chat=snapshot.getValue(Chat.class);

                    assert chat != null;
                    if(fuser.getUid().equals(chat.getReceiver()) && userid.equals(chat.getSender()))
                    {
                        HashMap<String,Object>hashMap=new HashMap<>();
                        hashMap.put("isseen",true);
                        snapshot.getRef().updateChildren(hashMap);


                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //This function sends a typed message to the database with the current user's id and receiver's id

    public void sendMessage(View view)
    {

         String message=text_send.getText().toString().trim(); // text_send message stored in the database as message


        if(!TextUtils.isEmpty(message))
        {
           DatabaseReference newMessage=chatRef.push();

           newMessage.child("sender").setValue(fuser.getUid());
           newMessage.child("receiver").setValue(userid);
           newMessage.child("message").setValue(message);  // text_send message stored in the database as message
            newMessage.child("isseen").setValue(false);


            //add the id of the user that the current user has messages with into the database child named ChatList,under the id of the current user

            final DatabaseReference chatRef=FirebaseDatabase.getInstance().getReference().child("ChatList").child(fuser.getUid()).child(userid);

            chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.exists())
                    {
                        chatRef.child("id").setValue(userid);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


             final String msg=message ;

             reference=FirebaseDatabase.getInstance().getReference().child("Users").child(fuser.getUid());
             reference.addValueEventListener(new ValueEventListener() {
                 @Override
                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                     User user=dataSnapshot.getValue(User.class);

                     if(notify) {
                         sendNotification(userid, user.getUsername(), msg);
                     }

                     notify=false ;
                 }

                 @Override
                 public void onCancelled(@NonNull DatabaseError databaseError) {

                 }
             });

        }
        else
        {
            Toast.makeText(MessageActivity.this,"You cannot send empty message",Toast.LENGTH_SHORT).show();
        }
        text_send.setText("");



        notify=true ;



    }

    private void sendNotification(String receiver, final String username, final String message)
    {
        DatabaseReference tokens= FirebaseDatabase.getInstance().getReference().child("Tokens");
        Query query=tokens.orderByKey().equalTo(receiver);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot: dataSnapshot.getChildren())
                {
                    Token token=snapshot.getValue(Token.class);
                    Data data= new Data(fuser.getUid(), R.mipmap.ic_launcher,username+": "+message,"New Message",userid);

                    Sender sender=new Sender(data, token.getToken());

                    apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if(response.code()== 200)
                            {
                                if(response.body().success != 1) {
                                    Toast.makeText(MessageActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void readMessage( final String myid, final String userid, final String imageurl)
    {
        mChat=new ArrayList<>();

        reference=FirebaseDatabase.getInstance().getReference().child("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mChat.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Chat chat=snapshot.getValue(Chat.class);

                    assert chat != null;
                    if(myid.equals(chat.getReceiver()) && userid.equals(chat.getSender()) || userid.equals(chat.getReceiver()) && myid.equals(chat.getSender())){

                        mChat.add(chat);
                    }


                   messageAdapter= new MessageAdapter(MessageActivity.this,mChat,imageurl);
                    recyclerView.setAdapter(messageAdapter);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //user status function
    public void status(String status)
    {
        reference=FirebaseDatabase.getInstance().getReference().child("Users").child(fuser.getUid());

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

        reference.removeEventListener(seenListener);
        status("offline");
    }

}
