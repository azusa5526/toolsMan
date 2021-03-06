package com.example.lai.toolsman.Post;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.lai.toolsman.R;
import com.example.lai.toolsman.SearchUser.Users;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Water extends AppCompatActivity {
    private Toolbar toolbar;
    private FloatingActionButton addPostBtn;
    private RecyclerView mList;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mUsersDatebase;
    //private DatabaseReference mDatabaseUser;
    private boolean mProcessLike = false;
    private DatabaseReference mDatabaseLike;
    String AccountName;
    String image;
    String currentUser;//這行


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water);
        Intent GetName = getIntent();
        AccountName = GetName.getStringExtra("AccountName");
        mAuth = FirebaseAuth.getInstance();


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("ArticleWater");
        mUsersDatebase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatebase.keepSynced(true);
        //mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("UsersWater");
        mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("LikesWater");

        //mDatabaseUser.keepSynced(true);
        mDatabase.keepSynced(true);
        mDatabaseLike.keepSynced(true);

        mList = findViewById(R.id.list);
        mList.setHasFixedSize(true);
        mList.setLayoutManager(new LinearLayoutManager(this));

        addPostBtn = (FloatingActionButton) findViewById(R.id.add_post_btn);
        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newPostIntent = new Intent(Water.this, PostWater.class);
                newPostIntent.putExtra("AccountName",AccountName);
                startActivity(newPostIntent);
                onStop();
            }
        });
    }



    @Override
    protected void onStart() {
        super.onStart();

            FirebaseRecyclerAdapter<ArticleWater, ArticleViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ArticleWater, ArticleViewHolder>(
                ArticleWater.class,
                R.layout.article_water,
                ArticleViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(final ArticleViewHolder viewHolder, final ArticleWater model, int position) {
                final String post_key = getRef(position).getKey();
                final String listUserId = getRef(position).getKey();
                viewHolder.setLikeBtn(post_key);
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImage(getApplicationContext(), model.getImage());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setProfile(model.getProfile());

                viewHolder.setDate(model.getDate());
                //User data will be retrieved here...

               /* mUsersDatebase.child(listUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String userThumb = dataSnapshot.child("thumbImage").getValue().toString();
                        viewHolder.setImage(thumbImage);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                })*/

                //String userThumb = mUsersDatebase.child("thumbImage").getKey().toString();
                //viewHolder.setImage(userThumb);


                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent singleArticleIntent = new Intent(Water.this, SingleArticleWater.class);
                        singleArticleIntent.putExtra("article_id", post_key);

                        startActivity(singleArticleIntent);
                    }
                }

                );

                //Like Feature
                viewHolder.mLikeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mProcessLike = true;

                        mDatabaseLike.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (mProcessLike) {
                                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {
                                        mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                        mProcessLike = false;

                                    } else {
                                        mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("Random");
                                        mProcessLike = false;

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }
        };

        mList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class ArticleViewHolder extends RecyclerView.ViewHolder{

        View mView;
        ImageButton mLikeBtn;
        DatabaseReference mDatabaseLike;
        FirebaseAuth mAuth;
        ImageView mProfile;

        public ArticleViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            mLikeBtn =  mView.findViewById(R.id.LikeBtn);
            mProfile = (ImageView) mView.findViewById(R.id.profile);
            mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("LikesWater");
            mAuth = FirebaseAuth.getInstance();
            mDatabaseLike.keepSynced(true);
        }

        public void setLikeBtn(final String post_key){
            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){
                        mLikeBtn.setImageResource(R.mipmap.action_like_blue);
                    } else {
                        mLikeBtn.setImageResource(R.mipmap.action_like_gray);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void setTitle(String title){
            TextView post_title = (TextView) mView.findViewById(R.id.post_title);
            post_title.setText(title);
        }

        public  void  setDesc(String desc){
            TextView post_desc = (TextView) mView.findViewById(R.id.post_desc);
            post_desc.setText(desc);
        }

        public void setImage(Context ctx, String image) {
            ImageView post_image = mView.findViewById(R.id.post_image);
            Picasso.get().load(image).into(post_image);
        }

        public void setUsername(String email) {
            TextView post_email = mView.findViewById(R.id.postemail);
            post_email.setText(email);
        }

        public void setProfile(String profile) {
            ImageView user_profile = mView.findViewById(R.id.profile);
            Picasso.get().load(profile).placeholder(R.drawable.defaultavatar).into(user_profile);
        }

        public  void  setDate(String date){
            TextView post_date = (TextView) mView.findViewById(R.id.date);
            post_date.setText(date);
        }
    }




}
