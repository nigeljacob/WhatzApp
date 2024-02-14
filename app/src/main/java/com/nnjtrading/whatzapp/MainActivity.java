package com.nnjtrading.whatzapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
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
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private Button signOut;
    public static NewChatActivityAdapter newChatActivityAdapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewGroup viewGroup;
    public static ArrayList<User> users, availableUsers;
    private ArrayList<PhoneContact> contactsList;
    private DatabaseReference databaseReference, conversationReference;
    public static ArrayList<String> picassos;
    private ConstraintLayout splashScreen;
    private Window window;
    public static ConversationAdapter conversationAdapter;
    private ArrayList<Message> conversations;
    private ValueEventListener valueEventListener, keyListener;
    private ArrayList<String> PhoneNumbers;
    public static ArrayList<ArrayList<Message>> allMessages, conversationAllMesssages;
    public static HashMap<String, ArrayList<Message>> messagesHashMap;
    private boolean available = false;

    private boolean darkMode = false;

    private static final String[] PROJECTION = new String[] {
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 21) {
            window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            int nightModeFlags =
                    this.getResources().getConfiguration().uiMode &
                            Configuration.UI_MODE_NIGHT_MASK;
            switch (nightModeFlags) {
                case Configuration.UI_MODE_NIGHT_YES:
                    window.setStatusBarColor(getResources().getColor(R.color.primaryDark));
                    window.setNavigationBarColor(getResources().getColor(R.color.primaryDark));
                    darkMode = true;
                    break;

                case Configuration.UI_MODE_NIGHT_NO:
                    window.setStatusBarColor(getResources().getColor(R.color.white));
                    window.setNavigationBarColor(getResources().getColor(R.color.white));
                    break;
            }
        }

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        if(currentUser == null) {
            Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        Intent intent = getIntent();

        String type = intent.getStringExtra("type");

        viewPager = findViewById(R.id.ViewPager);
        addTabs(viewPager);
        tabLayout = findViewById(R.id.tab);
        tabLayout.setupWithViewPager(viewPager);
        splashScreen = findViewById(R.id.splashScreen);
        viewGroup = findViewById(R.id.main);

        signOut = findViewById(R.id.signOut);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        if(type == null) {

            users = new ArrayList<>();
            availableUsers = new ArrayList<>();
            contactsList = new ArrayList<>();
            picassos = new ArrayList<>();
            conversations = new ArrayList<>();
            PhoneNumbers = new ArrayList<>();

            newChatActivityAdapter = new NewChatActivityAdapter(MainActivity.this, availableUsers, picassos, allMessages);

            MyAsyncTask myAsyncTask = new MyAsyncTask();
            myAsyncTask.execute();

            // Create a new Handler
            Handler handler = new Handler();

            // Create a Runnable that calls the doSomething() method
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    TransitionManager.beginDelayedTransition(viewGroup);
                    splashScreen.setVisibility(View.GONE);
                    if(darkMode) {
                        window.setStatusBarColor(getResources().getColor(R.color.Gray));
                    } else {
                        window.setStatusBarColor(getResources().getColor(R.color.primary));
                    }
                }
            };

            // Post the Runnable with a delay
            handler.postDelayed(runnable, 3000);

        } else if(type.equals("firstTime")) {
            splashScreen.setVisibility(View.GONE);
            if(darkMode) {
                window.setStatusBarColor(getResources().getColor(R.color.Gray));
            } else {
                window.setStatusBarColor(getResources().getColor(R.color.primary));
            }
            newChatActivityAdapter = ProfileSetupActivity.newChatActivityAdapter;
        }

        conversationAdapter = new ConversationAdapter(MainActivity.this, conversations);


        conversationReference = FirebaseDatabase.getInstance().getReference("Conversations");


        keyListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               for(DataSnapshot itemSnapshot: snapshot.getChildren()) {
                   getMessages(itemSnapshot.getKey());
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        conversationReference.child(currentUser.getPhoneNumber()).addValueEventListener(keyListener);


        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void addTabs(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new fragment_chats(), "Chats");
        adapter.addFrag(new fragment_updates(), "Updates");
        adapter.addFrag(new fragment_calls(), "Calls");
        viewPager.setAdapter(adapter);
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 100);
        } else {
            getPhoneContacts();
        }
    }

    private void getPhoneContacts() {
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, null);
        if (cursor != null) {
            try {
                final int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                final int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                String name, number;
                while (cursor.moveToNext()) {
                    name = cursor.getString(nameIndex);
                    number = cursor.getString(numberIndex);
                    String newNumber = "";
                    for(int i = 0; i < number.length(); i++) {
                        if(String.valueOf(number.charAt(i)).equals(" ") || String.valueOf(number.charAt(i)).equals("(") || String.valueOf(number.charAt(i)).equals(")")) {
                            continue;
                        }
                        newNumber = newNumber + String.valueOf(number.charAt(i));
                    }
                    PhoneContact contact = new PhoneContact(name, newNumber);
                    contactsList.add(contact);
                }
            } finally {
                cursor.close();
            }
        }
    }

    public void findUsers() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                availableUsers.clear();
                for(DataSnapshot itemSnapshot: snapshot.getChildren()) {
                    User user = itemSnapshot.getValue(User.class);
                    if(!user.getUid().equals(currentUser.getUid())) {
                        users.add(user);
                    }
                }

                for (int i = 0; i < users.size(); i++) {
                    User user = users.get(i);
                    String number = user.getNumber();
                    for(int j = 0; j < contactsList.size(); j++) {
                        PhoneContact contact = contactsList.get(j);
                        String userNumber = contact.getNumber();
                        if(number.startsWith("0")) {
                            if(userNumber.contains(number.substring(4)) && userNumber.endsWith(number.substring(number.length() - 1))) {
                                user.setName(contact.getName());
                                PhoneNumbers.add(userNumber);
                                try{
                                    getURI(user.getUid(), user);
                                    getMessages(user.getNumber());
                                } catch (Exception e) {
                                    picassos.add(null);
                                }
                                availableUsers.add(user);
                                break;
                            }
                        } else {
                            if(userNumber.contains(number.substring(3)) && userNumber.endsWith(number.substring(number.length() - 1))) {
                                user.setName(contact.getName());
                                PhoneNumbers.add(userNumber);
                                try{
                                    getURI(user.getUid(), user);
                                    getMessages(user.getNumber());
                                } catch (Exception e) {
                                    picassos.add(null);
                                }
                                availableUsers.add(user);
                                break;
                            }
                        }
                    }

                }

                newChatActivityAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getURI(String uid, User user){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("ProfilePics/" + uid + "/profilePic.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                user.setUri(String.valueOf(uri));
                picassos.add(String.valueOf(uri));
                conversationAdapter.notifyDataSetChanged();
                newChatActivityAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                picassos.add(null);
            }
        });
    }

    private void getMessages(String phoneNumber)  {
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        ArrayList<Message> UserMessages = new ArrayList<>();
        messagesHashMap = new HashMap<>();
        conversations.clear();
        available = false;

        conversationReference = FirebaseDatabase.getInstance().getReference("Conversations");
        conversationReference.child(currentUser.getPhoneNumber()).child(phoneNumber).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserMessages.clear();
                for(DataSnapshot itemsnapShot: snapshot.getChildren()) {
                    Message message = itemsnapShot.getValue(Message.class);
                    message.decryptMessage();
                    UserMessages.add(message);
                }

                messagesHashMap.put(phoneNumber, UserMessages);
                try{
                    for(Message message: conversations) {
                        if(message.getKey().equals(UserMessages.get(UserMessages.size() - 1).getKey())){
                            available = true;
                            break;
                        }
                    }
                    if(!available) {
                        conversations.add(UserMessages.get(UserMessages.size() - 1));
                        conversationAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    System.out.println("error");
                }
                MessagesAdapter.date.clear();
                System.out.println(conversations);
                try {
                    ChatActivity.messagesAdapter.notifyDataSetChanged();
                } catch (Exception e) {

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getPhoneContacts();
        } else {
            Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
        }

    }

    private class MyAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            checkPermission();
            findUsers();
            return "Background task completed!";
        }

        @Override
        protected void onPostExecute(String result) {
            conversationAdapter.notifyDataSetChanged();
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);

        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}