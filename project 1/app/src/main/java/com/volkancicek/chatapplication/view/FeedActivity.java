package com.volkancicek.chatapplication.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.volkancicek.chatapplication.R;
import com.volkancicek.chatapplication.adapter.PostAdapter;
import com.volkancicek.chatapplication.databinding.ActivityFeedBinding;
import com.volkancicek.chatapplication.model.Post;

import java.util.ArrayList;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;
    private ActivityFeedBinding binding;
    ArrayList<Post> arrayListPost;
    PostAdapter postAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        arrayListPost = new ArrayList<>();
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        getData();
        binding.recylerView.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new PostAdapter(arrayListPost);
        binding.recylerView.setAdapter(postAdapter);
    }
    private void getData(){
        firebaseFirestore.collection("postData").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null)
                {
                    Toast.makeText(FeedActivity.this,error.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
                if(value != null)
                {
                    for(DocumentSnapshot documentSnapshot : value.getDocuments()){
                        Map<String, Object> data = documentSnapshot.getData();
                        String email = (String)data.get("useremail");
                        String comment = (String)data.get("comment");
                        String imageUrl = (String)data.get("imageUrl");

                        Post post = new Post(email,comment,imageUrl);
                        arrayListPost.add(post);
                    }
                    postAdapter.notifyDataSetChanged();
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options_menu,menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.addpost)
        {
            Intent intentAddPost = new Intent(FeedActivity.this,UploadActivity.class);
            startActivity(intentAddPost);
        }
        else if(item.getItemId() == R.id.signout){
            auth.signOut();

            Intent intentOut = new Intent(FeedActivity.this,MainActivity.class);
            startActivity(intentOut);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}