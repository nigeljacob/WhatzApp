package com.nnjtrading.whatzapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class ChatActivity extends AppCompatActivity {

    private ConstraintLayout chatArea;
    private ViewGroup viewGroup;
    private ImageView profileImage, camera, placeholder, attach, sendButton, micButton;
    private TextView name, status;
    private EditText messageInput;
    private RequestBuilder<Drawable> PicassoImage;
    private DatabaseReference databaseReference, MessageReference;
    private String[] encryptList, DecryptList;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ArrayList<Message> messages;
    private String Name, UID, type, url, phoneNumber;
    public static MessagesAdapter messagesAdapter;
    private RecyclerView recyclerView;
    private NestedScrollView nestedScrollView;
    private ValueEventListener valueEventListener;
    public static String position;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        encryptList = new String[]{
                "#", "3", "q", "F", "@", "8", "X", "$", "Y", "J", "b", "T", "m", "g", "E", "h", "1", "v", "s", "R", "n", "A", "p", "P", "L",
                "M", "y", "j", "W", "c", "a", "o", "2", "0", "f", "Z", "7", "H", "N", "l", "D", "G", "S", "O", "V", "x", "w", "i", "K", "5",
                "t", "e", "4", "d", "6", "I", "U", "B", "C", "Q", "k", "r", "£"
        };
        DecryptList = new String[]{
                "A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z",
                "a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z",
                "1","2","3","4","5","6","7","8","9","0", " "};


        Intent intent = getIntent();

        Name = intent.getStringExtra("Name");
        type = intent.getStringExtra("type");
        UID = intent.getStringExtra("uid");
        url = intent.getStringExtra("url");
        phoneNumber = intent.getStringExtra("PhoneNumber");
        position = intent.getStringExtra("position");
        System.out.println(phoneNumber);


        chatArea = findViewById(R.id.chatArea);
        viewGroup = findViewById(R.id.chatActivity);
        profileImage = findViewById(R.id.ChatproifleImage);
        placeholder = findViewById(R.id.camera);
        camera = findViewById(R.id.openCamera);
        attach = findViewById(R.id.attachFile);
        name = findViewById(R.id.name);
        status = findViewById(R.id.status);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.send);
        micButton = findViewById(R.id.mic);
        recyclerView = findViewById(R.id.chatRecycleView);

        nestedScrollView = findViewById(R.id.nestedView);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        name.setText(Name);

        if(type.equals("newChat")) {
            messageInput.requestFocus();
        }

        messages = MainActivity.messagesHashMap.get(phoneNumber);
        messagesAdapter = new MessagesAdapter(ChatActivity.this, messages);

        recyclerView.setAdapter(messagesAdapter);

        nestedScrollView.smoothScrollTo(0, 0);

        MessageReference = FirebaseDatabase.getInstance().getReference("Conversations");

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot itemsnapshot: snapshot.getChildren()) {
                    Message message = itemsnapshot.getValue(Message.class);
                    if(!message.isSeen() && message.getRecieverUid().equals(firebaseUser.getUid()) && !message.getSentUserUid().equals(firebaseUser.getUid())) {

                        String key = message.getKey();
                        message.setSeen(true);

                        // Create a new Handler
                        Handler handler = new Handler();

                        // Create a Runnable that calls the doSomething() method
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                MessageReference.child(firebaseUser.getPhoneNumber()).child(phoneNumber).child(key).child("seen").setValue(true);
                                MessageReference.child(phoneNumber).child(firebaseUser.getPhoneNumber()).child(key).child("seen").setValue(true);
                            }
                        };
                        // Post the Runnable with a delay
                        handler.postDelayed(runnable, 200);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };



        if(url != null) {
            profileImage.setVisibility(View.VISIBLE);
            Glide.with(ChatActivity.this).load(url).centerCrop().thumbnail(0.05f)
                    .into(profileImage);
            placeholder.setVisibility(View.INVISIBLE);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            int nightModeFlags =
                    this.getResources().getConfiguration().uiMode &
                            Configuration.UI_MODE_NIGHT_MASK;
            switch (nightModeFlags) {
                case Configuration.UI_MODE_NIGHT_YES:
                    window.setStatusBarColor(getResources().getColor(R.color.Gray));
                    window.setNavigationBarColor(getResources().getColor(R.color.primaryDark));
                    chatArea.setAlpha(0.05f);
                    break;

                case Configuration.UI_MODE_NIGHT_NO:
                    window.setStatusBarColor(getResources().getColor(R.color.primary));
                    window.setNavigationBarColor(getResources().getColor(R.color.white));
                    chatArea.setAlpha(0.8f);
                    break;
            }
        }

        recyclerView.setNestedScrollingEnabled(false);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        databaseReference.child(UID).child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String Status = snapshot.getValue(String.class);
                status.setText(Status);
                status.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        messageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TransitionManager.beginDelayedTransition(viewGroup, new Slide().setDuration(100));
                if(messageInput.getText().length() > 0) {
                    camera.setVisibility(View.GONE);
                    attach.setVisibility(View.GONE);
                    sendButton.setVisibility(View.VISIBLE);
                    micButton.setVisibility(View.GONE);

                } else {
                    camera.setVisibility(View.VISIBLE);
                    attach.setVisibility(View.VISIBLE);
                    sendButton.setVisibility(View.GONE);
                    micButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sendButton.getVisibility() == View.VISIBLE && micButton.getVisibility() == View.GONE && messageInput.getText().length() > 0) {
                    sendMessage("textMessage");
                }
            }
        });

    }

    private void sendMessage(String type) {
        final String Message = messageInput.getText().toString();
        final String[] Datetime = getDateTime().split(" ");
        final String Date = Datetime[0];
        final String Time = Datetime[1];
        final String senderUID = firebaseUser.getUid();
        final String messageType = type;

        String key = MessageReference.child(firebaseUser.getPhoneNumber()).child(phoneNumber).push().getKey();

        Message newMessage = new Message(Message, senderUID, Date, Time, messageType, UID, firebaseUser.getPhoneNumber(), phoneNumber, "waiting", key, false);
        newMessage.encryptMessage();
        Message tempMessage = new Message(Message, senderUID, Date, Time, messageType, UID, firebaseUser.getPhoneNumber(), phoneNumber, "waiting", key, false);
        messages.add(tempMessage);
        messagesAdapter.notifyItemInserted(messages.size() - 1);
        nestedScrollView.post(new Runnable() {
            @Override
            public void run() {
                nestedScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
        messageInput.getText().clear();
        messageInput.requestFocus();


        MessageReference.child(firebaseUser.getPhoneNumber()).child(phoneNumber).child(key).setValue(newMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    messages.clear();
                    MessageReference.child(phoneNumber).child(firebaseUser.getPhoneNumber()).child(key).setValue(newMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                // Create a new Handler
                                Handler handler = new Handler();

                                // Create a Runnable that calls the doSomething() method
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        MessageReference.child(firebaseUser.getPhoneNumber()).child(phoneNumber).child(key).child("messageStatus").setValue("sent");
                                        MessageReference.child(phoneNumber).child(firebaseUser.getPhoneNumber()).child(key).child("messageStatus").setValue("sent");
                                    }
                                };
                                // Post the Runnable with a delay
                                handler.postDelayed(runnable, 200);
                            }
                        }
                    });
                }
            }
        });

    }

    private String getDateTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();

        return formatter.format(date);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MessageReference.child(firebaseUser.getPhoneNumber()).child(phoneNumber).addValueEventListener(valueEventListener);
        nestedScrollView.post(new Runnable() {
            @Override
            public void run() {
                nestedScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        MessageReference.child(firebaseUser.getPhoneNumber()).child(phoneNumber).removeEventListener(valueEventListener);
    }
}