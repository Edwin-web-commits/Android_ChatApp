package com.example.gochat.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gochat.MainActivity;
import com.example.gochat.R;
import com.example.gochat.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

// profile
public class ProfileFragment extends Fragment {

    private CircleImageView image_profile;
    private TextView username;

    private DatabaseReference reference;
    private FirebaseUser fuser;

    private StorageReference storageReference;
    private static int IMAGE_REQUEST=1;
    private Uri uri=null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_profile, container, false);


        image_profile=view.findViewById(R.id.profile_image);
        username=view.findViewById(R.id.username);

        fuser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference().child("Users").child(fuser.getUid());

        storageReference= FirebaseStorage.getInstance().getReference("uploads");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user=dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());

                if(user.getImageURL().equals("default"))
                {
                    image_profile.setImageResource(R.mipmap.ic_launcher);
                }else
                {
                    Picasso.with(getContext()).load(user.getImageURL()).into(image_profile);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

       image_profile.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               upload();
           }
       });

        return view;

    }
 public void upload()
 {

     Intent intent=new Intent();
     intent.setType("image/*");
     intent.setAction(intent.ACTION_GET_CONTENT);
     startActivityForResult(intent,IMAGE_REQUEST);
 }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK) {
            uri = data.getData();
            image_profile.setImageURI(uri);


            uploadImage();
        }

    }

    public void uploadImage()
    {
        final ProgressDialog pd=new ProgressDialog(getContext());
        pd.setMessage("Uploading");
        pd.show();

        if(uri !=null) {

            final StorageReference filePath = storageReference.child("uploads").child(uri.getLastPathSegment());

            final UploadTask uploadTask = filePath.putFile(uri);

            // getting the DownloadUri of the image

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL


                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {


                @Override      //if task is completed and the DownloadUri is returned ,
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful()) {
                        final Uri downloadUri = task.getResult();


                       // final DatabaseReference newPost = reference.push();
                        reference=FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

                        reference.child("imageURL").setValue(downloadUri.toString());

                         pd.dismiss();


                    } else {
                        // Handle failures
                        // ...
                        ;
                        Toast.makeText(getContext(),"Failed",Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    pd.dismiss();

                }
            });
        }else
        {
            Toast.makeText(getContext(),"No image selected",Toast.LENGTH_SHORT).show();
        }


    }

}
