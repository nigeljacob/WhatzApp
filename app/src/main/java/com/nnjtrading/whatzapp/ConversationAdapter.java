package com.nnjtrading.whatzapp;

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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationViewHolder> {

    private Context context;
    private ArrayList<Message> conversations;
    private ArrayList<String> uids = new ArrayList<>();
    private FirebaseUser firebaseUser;
    public static ArrayList<Message> messages;
    public static ValueEventListener valueEventListener;
    private DatabaseReference databaseReference, userReference;
    public static MessagesAdapter messagesAdapter;
    private int count;
    private ArrayList<User> Founduser = new ArrayList<>();
    private boolean available = false;
    private String URL, UID;

    public ConversationAdapter(Context context, ArrayList<Message> conversations) {
        this.context = context;
        this.conversations = conversations;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.conversation_user_layout, parent, false);
        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        Message message = conversations.get(position);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        count = 0;

        databaseReference = FirebaseDatabase.getInstance().getReference("Conversations");
        userReference = FirebaseDatabase.getInstance().getReference("Users");

        holder.notSeenCountLayout.setVisibility(View.INVISIBLE);

        if(message.getSentUserUid().equals(firebaseUser.getUid())) {
            holder.messageStatus.setVisibility(View.VISIBLE);

            for(User user: MainActivity.availableUsers){
                if(user.getUid().equals(message.getRecieverUid())){
                    Founduser.add(user);
                    available = true;
                    break;
                }
            }

            UID = message.getRecieverUid();

            holder.Message.setText(message.getMessage());
            if(message.getMessageStatus().equals("sent")){
                holder.messageStatus.setImageResource(R.drawable.double_tick_gray_com);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(50, 50);
                holder.messageStatus.setLayoutParams(layoutParams);
                ViewGroup.MarginLayoutParams marginParams1 = (ViewGroup.MarginLayoutParams) holder.messageStatus.getLayoutParams();
                marginParams1.setMargins(20,0,0,0);
            }

            if(message.isSeen()){
                holder.messageStatus.setImageResource(R.drawable.double_tick_blue_com);
            }


        } else if(message.getRecieverUid().equals(firebaseUser.getUid())){
            holder.messageStatus.setVisibility(View.GONE);

            for(User user: MainActivity.availableUsers){
                if(user.getUid().equals(message.getSentUserUid())){
                    System.out.println("found");
                    Founduser.add(user);
                    available = true;
                    break;
                }
            }

            UID = message.getSentUserUid();

            holder.Message.setText(message.getMessage());


        }

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();


        String date = getDate();
        if(date.equals(message.getSentDate())) {
            String[] time = message.getSentTime().split(":");
            holder.dateTime.setText(time[0] + ":" + time[1]);
        } else {
            holder.dateTime.setText(message.getSentDate());
        }

        if(!available) {
            userReference.child(UID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);

                    if(user.getName() != null) {
                        holder.Name.setText(user.getNumber());
                    }

                    storageReference.child("ProfilePics/" + user.getUid() + "/profilePic.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            URL = String.valueOf(uri);
                            if(URL != null) {
                                Glide.with(context).load(URL).centerCrop().thumbnail(0.05f).into(holder.profileImage);
                                holder.profileImage.setVisibility(View.VISIBLE);
                                holder.placeHolder.setVisibility(View.INVISIBLE);
                            }
                        }
                    });

                    ArrayList<Message> messages1 = MainActivity.messagesHashMap.get(user.getNumber());

                    for(Message message: messages1) {
                        if(!message.isSeen() && message.getRecieverUid().equals(firebaseUser.getUid())){
                            count++;
                        }
                    }

                    holder.conversationLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            count = 0;
                            Intent intent = new Intent(context, ChatActivity.class);
                            intent.putExtra("Name", user.getNumber());
                            intent.putExtra("type", "old");
                            intent.putExtra("uid", user.getUid());
                            intent.putExtra("url", URL);
                            intent.putExtra("PhoneNumber", user.getNumber());
                            System.out.println(user.getNumber());
                            context.startActivity(intent);
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        } else {

            User user = Founduser.get(0);

            if(user.getName() != null) {
                holder.Name.setText(user.getName());
            }

            if(user.getUri() != null) {
                Glide.with(context).load(user.getUri()).centerCrop().thumbnail(0.05f).into(holder.profileImage);
                holder.profileImage.setVisibility(View.VISIBLE);
                holder.placeHolder.setVisibility(View.INVISIBLE);
            }

            ArrayList<Message> messages1 = MainActivity.messagesHashMap.get(user.getNumber());

            for(Message message1: messages1) {
                if(!message1.isSeen() && message1.getRecieverUid().equals(firebaseUser.getUid())){
                    count++;
                }
            }

            holder.conversationLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    count = 0;
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("Name", user.getName());
                    intent.putExtra("type", "old");
                    intent.putExtra("uid", user.getUid());
                    intent.putExtra("url", user.getUri());
                    intent.putExtra("PhoneNumber", user.getNumber());
                    System.out.println(user.getNumber());
                    context.startActivity(intent);
                }
            });
        }

        Founduser.clear();
        if(count > 0) {
            holder.notSeenCountLayout.setVisibility(View.VISIBLE);
            holder.notSeenCount.setText(String.valueOf(count));
        } else {
            holder.notSeenCountLayout.setVisibility(View.INVISIBLE);
        }


    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    private String getDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();

        return formatter.format(date);
    }

}

class ConversationViewHolder extends RecyclerView.ViewHolder {

    LinearLayout conversationLayout;
    ImageView profileImage, placeHolder, messageStatus;
    TextView Name, Message, notSeenCount, dateTime;
    CardView notSeenCountLayout;


    public ConversationViewHolder(@NonNull View itemView) {
        super(itemView);

        conversationLayout = itemView.findViewById(R.id.conversationLayout);
        profileImage = itemView.findViewById(R.id.proifleImage);
        placeHolder = itemView.findViewById(R.id.camera);
        messageStatus = itemView.findViewById(R.id.messageStatus);
        Name = itemView.findViewById(R.id.name);
        Message = itemView.findViewById(R.id.message);
        notSeenCount = itemView.findViewById(R.id.NotSceenCount);
        dateTime = itemView.findViewById(R.id.DateTime);
        notSeenCountLayout = itemView.findViewById(R.id.notSeenCountLayout);


    }
}
