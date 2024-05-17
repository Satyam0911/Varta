package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.Models.Users;
import com.example.myapplication.databinding.ActivitySignInBinding;
import com.example.myapplication.databinding.ActivitySignUpBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class SignIn extends AppCompatActivity {
    ProgressDialog progressDialog;
    ActivitySignInBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("643225218443-s27obs5i5b99qqu8fele5todfh0eieek.apps.googleusercontent.com")
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInclient = GoogleSignIn.getClient(this,googleSignInOptions);
        //getActionBar().hide();

        progressDialog = new ProgressDialog(SignIn.this);
        progressDialog.setTitle("Login");
        progressDialog.setMessage("Login to your account");

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        binding.btnsignin2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.edtemail2.getText().toString() .isEmpty()){
                    binding.edtemail2.setError("Enter your Email");
                    return;
                }
                if (binding.edtpass2.getText().toString() .isEmpty()){
                    binding.edtpass2.setError("Enter your Password");
                    return;
                }

                progressDialog.show();
                auth.signInWithEmailAndPassword(binding.edtemail2.getText().toString(),binding.edtpass2.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss();
                                if (task.isSuccessful()){
                                    Intent intent = new Intent(SignIn.this, MainActivity.class);
                                    startActivity(intent);
                                }
                                else {
                                    Toast.makeText(SignIn.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        binding.btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignIn.this, SignUp.class);
                startActivity(intent);
            }
        });

        if(auth.getCurrentUser() != null){
            Intent intent = new Intent(SignIn.this, MainActivity.class);
            startActivity(intent);
        }
//        binding.btngoogle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent signInClient = googleSignInclient.getSignInIntent();
//                startActivity(signInClient);
//                startActivity( new Intent(SignIn.this,MainActivity.class));
//            }
//        });

    }
}