package com.supreme.supremedevs.schoolnotification.Fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.supreme.supremedevs.schoolnotification.Login.SetupActivity;
import com.supreme.supremedevs.schoolnotification.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class profile extends Fragment {
        private CircleImageView profile_image;
        private TextView profile_name;
        
        private FirebaseAuth mAuth;
        private FirebaseFirestore db;

        private String userId;
        private String postId;
        TextView email;
        private Uri mainImageURI = null;
    private RecyclerView post_list;
    FloatingActionButton add;
    Button edit;


        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                                 Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_profile, container, false);
            if(container!=null){
                container.removeAllViews();
            }


            profile_image = view.findViewById(R.id.profile_image);
            profile_name = view.findViewById(R.id.profile_name);
            edit = view.findViewById(R.id.edit);
            add = view.findViewById(R.id.add);
            email=view.findViewById(R.id.profile_email);

            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();
            userId = mAuth.getCurrentUser().getUid();
            setHasOptionsMenu(false);

            db.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            String name = task.getResult().getString("name");
                            String image = task.getResult().getString("image");
                            profile_name.setText(name);
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String profemail = user.getEmail();
                            email.setText(profemail);





                            mainImageURI = Uri.parse(image);

                            RequestOptions placeholderRequest = new RequestOptions();
                            placeholderRequest.placeholder(R.drawable.fb_holder);

                            Glide.with(getActivity()).setDefaultRequestOptions(placeholderRequest).load(image).into(profile_image);

                        }
                    } else {
                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(getActivity(), "Firestore Load Error: " + errorMessage, Toast.LENGTH_LONG).show();
                    }

                }
            });

            db = FirebaseFirestore.getInstance();
            final String user_id = mAuth.getCurrentUser().getUid();
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent settings = new Intent(getActivity(), SetupActivity.class);
                    settings.putExtra("User", user_id);
                    startActivity(settings);

                }
            });
            return view;
        }
    }



