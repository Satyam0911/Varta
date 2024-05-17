package com.example.myapplication;
import com.example.myapplication.Models.Users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.databinding.ActivitySignUpBinding;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {
    ActivitySignUpBinding binding;
    private FirebaseAuth auth;
    EditText edtemail;
    EditText edtpass;
    private static final int RC_SIGN_IN = 123;
    EditText edtuser;
    TextView txtSin;
    Button btnsignup;
    Button bgoogle;
    FirebaseDatabase database;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
//        getSupportActionBar().hide();
        binding=ActivitySignUpBinding.inflate(getLayoutInflater());

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        bgoogle = findViewById(R.id.btngoogle);

        edtuser = findViewById(R.id.edtuserName);
        btnsignup = findViewById(R.id.btnsignup);
        edtemail = findViewById(R.id.edtemail);
        edtpass = findViewById(R.id.edtpass);
        txtSin = findViewById(R.id.txtSin);



        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("643225218443-s27obs5i5b99qqu8fele5todfh0eieek.apps.googleusercontent.com")
                .requestEmail()
                .build();
//        BeginSignInRequest signInRequest = BeginSignInRequest.builder()
//                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
//                        .setSupported(true)
//                        // Your server's client ID, not your Android client ID.
//                        .setServerClientId(getString("643225218443-s27obs5i5b99qqu8fele5todfh0eieek.apps.googleusercontent.com"))
//                        // Only show accounts previously used to sign in.
//                        .setFilterByAuthorizedAccounts(true)
//                        .build())
//                .build();

        GoogleSignInClient googleSignInclient = GoogleSignIn.getClient(this,googleSignInOptions);


        bgoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInClient = googleSignInclient.getSignInIntent();
                startActivity(signInClient);
                startActivity( new Intent(SignUp.this,MainActivity.class));
            }
        });

        progressDialog = new ProgressDialog(SignUp.this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("We're Creating your account");

        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                auth.createUserWithEmailAndPassword
                                (edtemail.getText().toString(),edtpass.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss();
                                if(task.isSuccessful()){
                                    Users user = new Users(edtuser.getText().toString(),edtemail.getText().toString(),edtpass.getText().toString());
                                    String id = task.getResult().getUser().getUid();
                                    database.getReference().child("Users").child(id).setValue(user);
                                    Toast.makeText(SignUp.this,"User created Successfully",Toast.LENGTH_SHORT).show();
                                    startActivity( new Intent(SignUp.this,MainActivity.class));
                                }
                                else {
                                    Toast.makeText(SignUp.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


            }
        });
        txtSin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this, SignIn.class);
                startActivity(intent);
            }
        });
    }

//    private void signInWithGoogle(GoogleSignInOptions gso) {
//        Intent signInIntent = new GoogleSignInIntentBuilder(GoogleSignInIntent.ACTION_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id)) // Replace with your web client ID
//                .fromGoogleSignInOptions(gso)
//                .build();
//        startActivityForResult(signInIntent, RC_SIGN_IN);
//    }
}