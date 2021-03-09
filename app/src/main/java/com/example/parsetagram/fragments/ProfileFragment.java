package com.example.parsetagram.fragments;

        import android.os.Bundle;

        import androidx.fragment.app.Fragment;

        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;

        import com.example.parsetagram.Post;
        import com.example.parsetagram.R;
        import com.parse.FindCallback;
        import com.parse.ParseException;
        import com.parse.ParseQuery;
        import com.parse.ParseUser;

        import java.util.List;

public class ProfileFragment extends HomeFragment {
        private static String TAG = "HomeFragment";

        @Override
        protected void queryPosts() {
                ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
                query.include(Post.KEY_USER);
                query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
                query.setLimit(20);
                query.addDescendingOrder(Post.KEY_CREATED_KEY);
                query.findInBackground(new FindCallback<Post>() {
                        @Override
                        public void done(List<Post> posts, ParseException e) {
                                if(e != null) {
                                        Log.e(TAG, "Issue with getting posts", e);
                                }else {
                                        allPosts.addAll(posts);
                                        adapter.notifyDataSetChanged();
                                }
                        }
                });
        }
}
