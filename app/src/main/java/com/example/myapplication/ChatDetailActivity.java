package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Application;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import com.example.myapplication.Models.MessageModel;
import com.example.myapplication.Models.Users;
import com.example.myapplication.databinding.ActivityChatDetailBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import Adapter.ChatAdapter;

public class ChatDetailActivity extends AppCompatActivity {

    FirebaseDatabase database;
    FirebaseStorage storage;
    FirebaseAuth auth;
    TextView userName;
    ProgressDialog progressBar;

    ImageView send,menu,backArrow,sendImg,sendDoc,video,call;
    CircularImageView profileimg;
    RecyclerView chatrecycle;
    EditText edtMsg;
    String senderRoom;
    String senderId;
    String reciverId;
    String username;
    String profilePic;
    String receiverRoom;
    ChatAdapter chatAdapter;

 //   ActivityChatDetailBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  binding = ActivityChatDetailBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_chat_detail);

        send = findViewById(R.id.send);
        profileimg = findViewById(R.id.profilePic);
        menu = findViewById(R.id.menubtn);
        chatrecycle = findViewById(R.id.chatRecyclerview);
        edtMsg = findViewById(R.id.edtTextMsg);
        sendImg = findViewById(R.id.sendImg);
        sendDoc = findViewById(R.id.sendDoc);
        video = findViewById(R.id.videoCall);
        call = findViewById(R.id.callbtn);
        storage = FirebaseStorage.getInstance();

        progressBar = new ProgressDialog(ChatDetailActivity.this);
        progressBar.setMessage("Image is Loading");

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        senderId = auth.getUid();
        String reciverId = getIntent().getStringExtra("userId");
        String username = getIntent().getStringExtra("userName");
        String profilePic = getIntent().getStringExtra("profilePic");
        Users users = new Users();
        //Picasso.get().load(users.getProfilepic()).placeholder(R.drawable.image).into(profileimg);

        userName = findViewById(R.id.userName);
        userName.setText(username);
        Picasso.get().load(profilePic).placeholder(R.drawable.image).into(profileimg);

        backArrow = findViewById(R.id.imageView2);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatDetailActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        sendDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");   //*/* for all
                startActivityForResult(intent,25);
            }
        });


        final ArrayList<MessageModel> messageModels= new ArrayList<>();

        chatAdapter = new ChatAdapter(messageModels,this,reciverId);
        chatrecycle.setAdapter(chatAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        chatrecycle.setLayoutManager(layoutManager);

        senderRoom = senderId+reciverId;
        receiverRoom = reciverId+senderId;

        database.getReference().child("chats")
                .child(senderRoom)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                messageModels.clear();
                                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                                    MessageModel model = snapshot1.getValue(MessageModel.class);
                                    model.setMessageId(snapshot1.getKey());
                                    messageModels.add(model);
                                }
                                chatAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
        
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtMsg.getText().toString().isEmpty())
                {
                    edtMsg.setError("Enter something");
                    return;
                }
                String message = edtMsg.getText().toString();
                final MessageModel model = new MessageModel(senderId,message);
                model.setTimestamp(new Date().getTime());
                edtMsg.setText("");

                database.getReference().child("chats")
                        .child(senderRoom)
                        .push()
                        .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                database.getReference().child("chats")
                                        .child(receiverRoom)
                                        .push()
                                        .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                            }
                                        });
                            }
                        });
                chatAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 25 && data != null) {
            if (data.getData() != null){
                Uri selectedimg = data.getData();
                Calendar calendar = Calendar.getInstance();
               // progressBar.show();
                StorageReference reference = storage.getReference().child("chats").child(calendar.getTimeInMillis() + "");
                reference.putFile(selectedimg).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                   // progressBar.dismiss();
                                    String imgPath = uri.toString();
                                    String message = edtMsg.getText().toString();
                                    final MessageModel model = new MessageModel(senderId,message);
                                    model.setTimestamp(new Date().getTime());
                                    model.setMessage("photo");
                                    model.setImageUri(imgPath);
                                    edtMsg.setText("");

                                    database.getReference().child("chats")
                                            .child(senderRoom)
                                            .push()
                                            .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    database.getReference().child("chats")
                                                            .child(receiverRoom)
                                                            .push()
                                                            .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {

                                                                }
                                                            });
                                                }
                                            });
                                    chatAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });
            }
        }
        else if (requestCode == 11 && data != null) {
            Toast.makeText(ChatDetailActivity.this,"open camera",Toast.LENGTH_SHORT).show();
        }
    }

}