package com.example.parsetagram.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.parsetagram.MainActivity;
import com.example.parsetagram.Post;
import com.example.parsetagram.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

public class ComposeFragment extends Fragment {

    private EditText etDescription;
    private ImageButton ibtnTakePicture;
    private Button btnPost;
    private ImageView ivPost;
    private String TAG = "ComposeFragment";
    private File photoFile;
    private static final int CAPTURE_IMAGE_ACTIVITY_RESQUEST_CODE = 27;
    public String photoFileName = "photo.jpg";

    public ComposeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        etDescription = view.findViewById(R.id.etDescription);
        ibtnTakePicture = view.findViewById(R.id.ibtnTakePicture);
        btnPost = view.findViewById(R.id.btnPost);
        ivPost = view.findViewById(R.id.ivPost);

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = etDescription.getText().toString();
                ParseUser currentUser = ParseUser.getCurrentUser();
                if(photoFile == null || ivPost.getDrawable() == null)
                    Toast.makeText(getContext(), "There is no image", Toast.LENGTH_LONG).show();
                else if(description.isEmpty())
                    Toast.makeText(getContext(), "There is no description", Toast.LENGTH_LONG).show();
                else
                    savePost(description, currentUser, photoFile);
            }
        });

        queryPosts();

        ibtnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

    }
    private void savePost(String description, ParseUser currentUser, File photoFile) {
        Post post = new Post();
        post.setDescription(description);
        post.setImage(new ParseFile(photoFile));
        post.setUser(currentUser);
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null) {
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(getContext(), "Error while saving.", Toast.LENGTH_LONG).show();
                } else {
                    Log.i(TAG, "Post saved successfully");
                    Toast.makeText(getContext(), "Post saved successfully", Toast.LENGTH_LONG).show();
                    etDescription.setText("");
                    ivPost.setImageResource(0);
                }
            }
        });
    }

    private void launchCamera() {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri(photoFileName);
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
        i.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if(i.resolveActivity(getContext().getPackageManager()) != null)
            startActivityForResult(i, CAPTURE_IMAGE_ACTIVITY_RESQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_RESQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                ivPost.setImageBitmap(takenImage);
                Log.e(TAG, "Image changed");
            } else {
                Toast.makeText(getContext(), "failed to take photo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);
        if(!mediaStorageDir.exists() && !mediaStorageDir.mkdirs())
            Log.d(TAG, "failed to create directory");
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    private void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e != null)
                    Log.e(TAG, "Issue with getting posts", e);
                else
                    for(Post post: posts)
                        Log.i(TAG, "Post: " + post.getDescription()+ " username: " + post.getUser().getUsername());

            }
        });
    }
}