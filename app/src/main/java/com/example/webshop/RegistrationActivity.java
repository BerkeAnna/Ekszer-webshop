package com.example.webshop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>  {

    private static final String PREF_KEY = MainActivity.class.getPackage().toString();
    private static final String LOG_TAG = RegistrationActivity.class.getName();

    private static final int SECRET_KEY = 99;

    EditText usernameEditText;
    EditText nameEditText;
    EditText passwordEditText;
    EditText emailEditText;

    private SharedPreferences preferences;
    private FirebaseAuth mAuth;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

//        Bundle bundle = getIntent().getExtras();
//        int secret_key = bundle.getInt("SECRET_KEY");
        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);


        if(secret_key != 99){
            finish();
        }

        usernameEditText = findViewById(R.id.usernameEdittext);
        nameEditText = findViewById(R.id.nameEdittext);
        passwordEditText = findViewById(R.id.passwordTextPassword);
        emailEditText = findViewById(R.id.editTextEmail);

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        String username = preferences.getString("userName", "");
        String password = preferences.getString("password", "");

        usernameEditText.setText(username);
        passwordEditText.setText(password);

        mAuth= FirebaseAuth.getInstance();

//          Ez a piros lépj vissza cansel a registréciónál
        TextView textView = findViewById(R.id.textView3);
        new SignupAsyncTask(textView).execute();

        getSupportLoaderManager().restartLoader(0, null, this);

        Log.i(LOG_TAG, "onCreate");
    }
    public void signup(View view) {
        String name = nameEditText.getText().toString();
        String userName = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String email = emailEditText.getText().toString();

        Log.i(LOG_TAG, "Regisztrált: " + userName + ",name: "+ name+ ", jelszo: " + password + ", email: "+ email);


        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(LOG_TAG, "User created successfully");
                    shopping();
                }else{
                    Log.d(LOG_TAG, "User not created");
                    Toast.makeText(RegistrationActivity.this, "Usert not created:" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void shopping(){
        Intent intent = new Intent(this, ShopListActivity.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
    }

    public void cancel(View view) {
        finish();
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
        return new SignupAsyncLoader(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        TextView textView = findViewById(R.id.textView3);
        textView.setText(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}