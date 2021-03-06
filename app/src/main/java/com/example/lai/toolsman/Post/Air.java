package com.example.lai.toolsman.Post;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lai.toolsman.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Air extends AppCompatActivity {
    private Toolbar toolbar;
    private FloatingActionButton addPostBtn;

    private RecyclerView mList;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUser;
    private boolean mProcessLike = false;
    private DatabaseReference mDatabaseLike;
    String AccountName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent GetName = getIntent();
        AccountName = GetName.getStringExtra("AccountName");

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("ArticleAir");
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("UsersAir");
        mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("LikesAir");
        mDatabaseUser.keepSynced(true);
        mDatabase.keepSynced(true);
        mDatabaseLike.keepSynced(true);

        mList = findViewById(R.id.list);
        mList.setHasFixedSize(true);
        mList.setLayoutManager(new LinearLayoutManager(this));

        addPostBtn = (FloatingActionButton) findViewById(R.id.add_post_btn);
        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newPostIntent = new Intent(Air.this, PostAir.class);
                newPostIntent.putExtra("AccountName",AccountName);
                startActivity(newPostIntent);
                onStop();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<ArticleAir,ArticleViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ArticleAir, ArticleViewHolder>(
                ArticleAir.class,
                R.layout.article_air,
                ArticleViewHolder.class,
                mDatabase
        ) {

            protected void populateViewHolder(ArticleViewHolder viewHolder, ArticleAir model, int position) {
                final String post_key = getRef(position).getKey();
                viewHolder.setLikeBtn(post_key);
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImage(getApplicationContext(), model.getImage());
                viewHolder.setUsername(model.getUsername());
                //User data will be retrieved here...

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent singleArticleIntent = new Intent(Air.this, SingleArticleAir.class);
                        singleArticleIntent.putExtra("article_id", post_key);
                        startActivity(singleArticleIntent);
                    }
                });
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
        public ArticleViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            mLikeBtn =  mView.findViewById(R.id.LikeBtn);
            mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("LikesAir");
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

        public  void setUsername(String username){
            TextView post_username = (TextView) mView.findViewById(R.id.username);
            post_username.setText(username);
        }

    }
}
