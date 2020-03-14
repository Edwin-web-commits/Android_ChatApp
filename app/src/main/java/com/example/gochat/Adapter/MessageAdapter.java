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
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT=0 ;
    public static final int MSG_TYPE_RIGHT=1 ;

    private android.content.Context mContext;
    private List<Chat> mChat;
    private String imageurl;

    private FirebaseUser fuser;

    public MessageAdapter(Context mContext, List<Chat> mChat,String imageurl){

        this.mContext=mContext;
        this.mChat=mChat;
        this.imageurl=imageurl;
    }


    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == MSG_TYPE_RIGHT) { //returned from getItemViewType() at the bottom

            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_rght, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
        else {



                View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item, parent, false);
                return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        Chat chat=mChat.get(position);

        holder.showmessage.setText(chat.getMessage());

        if("default".equals(imageurl))
        {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }else
        {
            Picasso.with(mContext).load(imageurl).into(holder.profile_image);

        }

        //Check for last message

        if(position ==mChat.size()-1)
        {
            if(chat.isIsseen())
            {
                holder.text_seen.setText("seen");
            }
            else{
                holder.text_seen.setText("Delivered");
            }

        }
        else
        {
            holder.text_seen.setVisibility(View.GONE);
        }

    }


    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView showmessage;
        public ImageView profile_image;
        public TextView  text_seen;


        public ViewHolder(View itemView)
        {
            super(itemView);


            showmessage =itemView.findViewById(R.id.show_message);
            profile_image=itemView.findViewById(R.id.profile_image);
           text_seen=itemView.findViewById(R.id.text_send);
        }
    }

 // if the id of the sender of the chat obeject at whatever position in the list is equals to the id of the current user,then return MSG_TYPE_RIGHt
    @Override
    public int getItemViewType(int position) {

        fuser= FirebaseAuth.getInstance().getCurrentUser();

        if(mChat.get(position).getSender().equals(fuser.getUid()))
        {
            return MSG_TYPE_RIGHT;
        }
        else{
            return MSG_TYPE_LEFT;
        }
    }
}
