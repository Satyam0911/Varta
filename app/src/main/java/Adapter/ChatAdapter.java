package Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Models.MessageModel;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter{
    private static final long MAX_FILE_SIZE = 1111;

    private void downloadAndDisplayFile(String fileUrl, View fileView) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(fileUrl);

        // Choose a download method based on your needs:
        // Download file contents as bytes
    
    storageRef.getBytes(MAX_FILE_SIZE)
        .addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Display file content based on its type (e.g., PDF viewer)
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle download failure (e.g., Toast message)
            }
        });
    

        // Download file to a temporary location (consider permissions)
    /*
    File localFile = ...; // Create temporary file*/
        File localFile = null;
        storageRef.getFile(localFile)
        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Access file using localFile.getAbsolutePath() and display it
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle download failure
            }
        });
    
    }

    ArrayList<MessageModel> messageModels;
    Context context;
    String recId;

    public ChatAdapter(ArrayList<MessageModel> messageModels, Context context, String recId) {

        this.messageModels = messageModels;
        this.context = context;
        this.recId = recId;
    }

    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE = 2;

    public ChatAdapter(ArrayList<MessageModel> messageModels, Context context) {
        this.messageModels = messageModels;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == SENDER_VIEW_TYPE){
            View view = LayoutInflater.from(context).inflate(R.layout.samplesender,parent,false);
            return new SenderViewHold(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.samplereciever,parent,false);
            return new ReceiverViewHold(view);
        }
    }

    @Override
    public int getItemViewType(int position) {

        if (messageModels.get(position).getuId().equals(FirebaseAuth.getInstance().getUid())){
            return SENDER_VIEW_TYPE;
        }
        else{
            return RECEIVER_VIEW_TYPE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MessageModel messageModel = messageModels.get(position);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete")
                        .setMessage("Are you sure want to delete")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                String sender = FirebaseAuth.getInstance().getUid()+recId;
                                database.getReference().child("chats").child(sender)
                                        .child(messageModel.getMessageId())
                                        .removeValue();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                return false;
            }
        });
        if (holder.getClass() == SenderViewHold.class){
            ((SenderViewHold)holder).senderMsg.setText(messageModel.getMessage());
            if (messageModel.getMessage().equals("photo")){
                ((SenderViewHold) holder).sendimg.setVisibility(View.VISIBLE);
                holder.itemView.findViewById(R.id.sendertext).setVisibility(View.GONE);
                Picasso.get().load(messageModel.getImageUri()).placeholder(R.drawable.image).into(((SenderViewHold) holder).sendimg);
            }
            if (messageModel.getMessage().equals("file")){
                ((SenderViewHold) holder).sfile.setVisibility(View.VISIBLE);
                holder.itemView.findViewById(R.id.sendertext).setVisibility(View.GONE);
                downloadAndDisplayFile(messageModel.getDocUri(), ((SenderViewHold) holder).sfile);
            }
        }
        else {
            ((ReceiverViewHold)holder).receiverMsg.setText(messageModel.getMessage());
            if (messageModel.getMessage().equals("photo")){
                ((ReceiverViewHold) holder).receiveimg.setVisibility(View.VISIBLE);
                holder.itemView.findViewById(R.id.reciverText).setVisibility(View.GONE);
                ImageView sendimg = holder.itemView.findViewById(R.id.receiveImg);
                Picasso.get().load(messageModel.getImageUri()).placeholder(R.drawable.image).into(((ReceiverViewHold) holder).receiveimg);
            }
        }
    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    public class ReceiverViewHold extends RecyclerView.ViewHolder{

        TextView receiverMsg , receiverTime;
        ImageView receiveimg;
        public ReceiverViewHold(@NonNull View itemView) {
            super(itemView);
            receiverMsg = itemView.findViewById(R.id.reciverText);
            receiverTime = itemView.findViewById(R.id.recivertm);
            receiveimg = itemView.findViewById(R.id.receiveImg);
        }
    }

    public class SenderViewHold extends RecyclerView.ViewHolder{

        TextView senderMsg,senderTime;
        ImageView sendimg;
        View sfile;
        public SenderViewHold(@NonNull View itemView) {
            super(itemView);
            sfile = itemView.findViewById(R.id.filedoc);
            senderMsg = itemView.findViewById(R.id.sendertext);
            senderTime = itemView.findViewById(R.id.sendertime);
            sendimg = itemView.findViewById(R.id.sendImg);
        }

    }

}
