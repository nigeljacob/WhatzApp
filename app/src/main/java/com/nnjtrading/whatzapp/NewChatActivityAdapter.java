package com.nnjtrading.whatzapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class NewChatActivityAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private final Context context;
    private final List<User> availableUsers;
    private final List<String> picasso;
    private Uri imageUri;
    private DatabaseReference MessageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    public static ArrayList<Message> messages;
    public static MessagesAdapter messagesAdapter;
    private List<ArrayList<Message>> allMessages;

    public NewChatActivityAdapter(Context context, List<User> dataList, List<String> piccaso, List<ArrayList<Message>> allMessages) {
        this.context = context;
        this.availableUsers = dataList;
        this.picasso = piccaso;
        this.allMessages = allMessages;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.new_user_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        User user = availableUsers.get(position);
        holder.name.setText(user.getName());
        holder.slogan.setText(user.getSlogan());

        try{
            if(user.getUri() != "" || user.getUid() != null) {
                Glide.with(context).load(user.getUri()).centerCrop().thumbnail(0.05f)
                        .into(holder.imageView);
                holder.imageView.setVisibility(View.VISIBLE);
                holder.placeHolder.setVisibility(View.INVISIBLE);
            }


        } catch (Exception e) {

        }

        getMessages(user.getNumber());

        holder.newChatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("tapped");
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("Name", user.getName());
                intent.putExtra("type", "newChat");
                intent.putExtra("uid", user.getUid());
                intent.putExtra("url", user.getUri());
                intent.putExtra("position", String.valueOf(position));
                System.out.println(picasso);
                intent.putExtra("PhoneNumber", user.getNumber());
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return availableUsers.size();
    }

    private void getMessages(String phoneNumber) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        messages = new ArrayList<>();

        messagesAdapter = new MessagesAdapter(context, messages);

        MessageReference = FirebaseDatabase.getInstance().getReference("Conversations");
        MessageReference.child(firebaseUser.getPhoneNumber()).child(phoneNumber).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                int i = 0;
                for(DataSnapshot itemsnapShot: snapshot.getChildren()) {
                    Message message = itemsnapShot.getValue(Message.class);
                    message.decryptMessage();
                    messages.add(message);
                }

                messagesAdapter.notifyDataSetChanged();
                MessagesAdapter.date.clear();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}



class MyViewHolder extends RecyclerView.ViewHolder {

    ImageView imageView, placeHolder;
    TextView name, slogan;
    LinearLayout newChatLayout;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.proifleImage);
        name = itemView.findViewById(R.id.name);
        slogan = itemView.findViewById(R.id.slogan);
        placeHolder = itemView.findViewById(R.id.camera);
        newChatLayout = itemView.findViewById(R.id.newChatLayout);

    }
}
