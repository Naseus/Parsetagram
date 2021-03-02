package com.example.parsetagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText etUsername;
    private EditText etPassword;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick(login button)");
                String user = etUsername.getText().toString();
                String pass = etPassword.getText().toString();
                loginUser(user, pass);
            }
        });
    }

    private void loginUser(String username, String password){
        Log.i(TAG, "Attempting to login" + username);
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
               if(e != null) {
                   Log.e(TAG, "issue with login", e);
                   return;
               }
               toMainActivity();
            }
        });
    }

    private void toMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}