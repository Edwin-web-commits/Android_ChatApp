package com.example.gochat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gochat.Chat;
import com.example.gochat.MessageActivity;
import com.example.gochat.R;
import com.example.gochat.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class userAdapter extends RecyclerView.Adapter<userAdapter.ViewHolder>{

    private android.content.Context mContext;
    private List<User> mUsers;
    private boolean isChat;   //is just a boolean variable to tell if the user is your chat or not

    private String theLastMessage;

    public userAdapter(Context mContext, List<User> mUsers,boolean isChat){

        this.mContext=mContext;
        this.mUsers=mUsers;
        this.isChat=isChat;
    }


     @NonNull
     @Override
     public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);
         return new userAdapter.ViewHolder(view);
     }

     @Override
     public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

       final User user=mUsers.get(position);
        holder.username.setText(user.getUsername());

        if("default".equals(user.getImageURL()))
        {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }
        else
        {
            Picasso.with(mContext).load(user.getImageURL()).into(holder.profile_image);
        }

        //check whether the user is online or not

        if(isChat) //if the user is your chat (you have a chat with the user)
        {
            if("online".equals(user.getStatus()))
            {
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            }
            else
            {
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.VISIBLE);
            }
        }
        else{

            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(mContext, MessageActivity.class);
                intent.putExtra("userid",user.getId());  //the id of the clicked user
                mContext.startActivity(intent);
            }
        });


        if(isChat)
        {
            lastMessage(user.getId(),holder.last_msg);

        }
        else
        {
            holder.last_msg.setVisibility(View.GONE);
        }


     }

     @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView username;
        public ImageView profile_image;
        public ImageView img_on;
        public ImageView img_off;
        public TextView last_msg;

        public ViewHolder(View itemView)
        {
            super(itemView);


            username =itemView.findViewById(R.id.username);
            profile_image=itemView.findViewById(R.id.profile_image);
            img_on=itemView.findViewById(R.id.img_on);
            img_off=itemView.findViewById(R.id.img_off);
            last_msg=itemView.findViewById(R.id.last_msg);
        }

    }

    //check for last message and set it to the thelastMessage textView
  public void lastMessage(final String userid, final TextView last_msg)
  {
      theLastMessage="default";
      final FirebaseUser fuser=FirebaseAuth.getInstance().getCurrentUser();
      DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Chats");

      reference.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

              for(DataSnapshot snapshot: dataSnapshot.getChildren())
              {
                  Chat chat=snapshot.getValue(Chat.class);

                  if(fuser.getUid().equals(chat.getReceiver()) && userid.equals(chat.getSender()) ||
                          fuser.getUid().equals(chat.getSender()) && userid.equals(chat.getReceiver()))
                  {
                      theLastMessage=chat.getMessage();
                  }


              }

             /* switch (theLastMessage)
              {
                  case "default":
                      last_msg.setText("No message");

                  default:
                      last_msg.setText(theLastMessage);

              }

              */
              if(theLastMessage=="default")
              {
                  last_msg.setText("No message");
              }
              else
              {
                  last_msg.setText(theLastMessage);
              }

              theLastMessage=("default");
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
      });


  }


}
