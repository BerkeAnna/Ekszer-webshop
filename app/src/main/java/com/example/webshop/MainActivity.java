package com.example.webshop;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.app.AlarmManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final String PREF_KEY = MainActivity.class.getPackage().toString();
    private static final int SECRET_KEY = 99;

    EditText userNameEditText;
    EditText passwordEditText;

    private SharedPreferences preferences;
    private FirebaseAuth mAuth;


    private AlarmManager mAlarm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userNameEditText = findViewById(R.id.edittext_username);
        passwordEditText = findViewById(R.id.edittext_password);
        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
//        random Async Task
        Button button = findViewById(R.id.anonym_button);
        new RandomAsyncTask(button).execute();


//        Random. Async Loader
        getSupportLoaderManager().restartLoader(0, null, this);
        Log.i(LOG_TAG, "onCreate");
    }

    public void bej(View view) {
        String userName = userNameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        Log.i(LOG_TAG, "Bejelentkezett: " + userName + ", jelszo: " + password );

        mAuth.signInWithEmailAndPassword(userName, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(LOG_TAG, "User login successfully");
                    shopping();
                }else{
                    Log.d(LOG_TAG, "User not login");
                    Toast.makeText(MainActivity.this, "Usert not login:" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    public void shopping(){
        Intent intent = new Intent(this, ShopListActivity.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
    }

    public void loginAsGuest(View view){
        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(LOG_TAG, "Anonym user login successfully");
                    shopping();
                }else{
                    Log.d(LOG_TAG, "Anonym user not login");
                    Toast.makeText(MainActivity.this, "Usert not login:" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    public void registration(View view) {
        Intent intent = new Intent(this, RegistrationActivity.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userName",userNameEditText.getText().toString() );
        editor.putString("password", passwordEditText.getText().toString() );
        editor.apply();
        Log.i(LOG_TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(LOG_TAG, "onRestart");
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        return new RandomAsyncLoader(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        Button button = findViewById(R.id.anonym_button);
        button.setText(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}