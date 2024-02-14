package com.nnjtrading.whatzapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProfileSetupActivity extends AppCompatActivity {

    private CardView profilePic;
    private ImageView proifleImage, camera;
    private TextView title;
    private static final int IMAGEPICK_GALLERY_REQUEST = 300;
    private Uri ImageUri;
    private boolean photoAvailable, removedPicture = false, photoLoaded = false;
    private Button doneButton;
    private EditText Name;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private ProgressBar progressBar, progressBar2;
    public static ArrayList<User> users, availableUsers;
    public static ArrayList<PhoneContact> contactsList;
    public static NewChatActivityAdapter newChatActivityAdapter;
    public static ArrayList<String> picassos;
    private ArrayList<ArrayList<Message>> allMessages;

    private static final String[] PROJECTION = new String[] {
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            int nightModeFlags =
                    this.getResources().getConfiguration().uiMode &
                            Configuration.UI_MODE_NIGHT_MASK;
            switch (nightModeFlags) {
                case Configuration.UI_MODE_NIGHT_YES:
                    window.setStatusBarColor(getResources().getColor(R.color.primaryDark));
                    window.setNavigationBarColor(getResources().getColor(R.color.primaryDark));
                    break;

                case Configuration.UI_MODE_NIGHT_NO:
                    window.setStatusBarColor(getResources().getColor(R.color.white));
                    window.setNavigationBarColor(getResources().getColor(R.color.white));
                    break;
            }
        }

        profilePic = findViewById(R.id.profilePic);
        proifleImage = findViewById(R.id.proifleImage);
        camera = findViewById(R.id.camera);
        title = findViewById(R.id.text);
        doneButton = findViewById(R.id.doneButton);
        Name = findViewById(R.id.Name);
        progressBar = findViewById(R.id.progressBar);
        progressBar2 = findViewById(R.id.progressBar2);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        users = new ArrayList<>();
        availableUsers = new ArrayList<>();
        contactsList = new ArrayList<>();
        picassos = new ArrayList<>();

        newChatActivityAdapter = new NewChatActivityAdapter(this, availableUsers, picassos, allMessages);

        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute();

        doneButton.setEnabled(false);

        Intent intent = getIntent();
        String type = intent.getStringExtra("type");

        if(type.equals("Old")) {
            progressBar2.setVisibility(View.VISIBLE);
            Name.setText(currentUser.getDisplayName());
            doneButton.setEnabled(true);

            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            storageReference.child("ProfilePics/" + currentUser.getUid() + "/profilePic.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    camera.setVisibility(View.GONE);
                    proifleImage.setVisibility(View.VISIBLE);
                    title.setVisibility(View.INVISIBLE);
                    Picasso.get()
                            .load(uri)
                            .centerCrop()
                            .resize(700, 700)
                            .rotate(90)
                            .into(proifleImage);
                    progressBar2.setVisibility(View.INVISIBLE);
                    photoLoaded = true;
                }
            });
        }

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(photoAvailable){
                    String options[] = {"Remove Image", "Change Image"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileSetupActivity.this);
                    builder.setTitle("Choose an Option");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(which == 0) {
                                proifleImage.setVisibility(View.INVISIBLE);
                                camera.setVisibility(View.VISIBLE);
                                title.setVisibility(View.VISIBLE);
                                photoAvailable = false;
                                removedPicture = true;
                            } else if(which == 1) {
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(intent, IMAGEPICK_GALLERY_REQUEST);
                            }
                        }
                    });
                    builder.create().show();
                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, IMAGEPICK_GALLERY_REQUEST);
                }
            }
        });

        Name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(Name.getText().length() > 0) {
                    doneButton.setEnabled(true);
                } else {
                    doneButton.setEnabled(false);
                }
            }
        });



        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInUserProfile();
            }
        });

    }

    private void SignInUserProfile() {
        progressBar.setVisibility(View.VISIBLE);
        doneButton.setVisibility(View.INVISIBLE);
        final String name = Name.getText().toString();

        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();


        currentUser.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    User user = new User(name, String.valueOf(currentUser.getPhoneNumber()), "Hey! I'm on WhatzApp", "Online", currentUser.getUid());
                    databaseReference = FirebaseDatabase.getInstance().getReference("Users");

                    databaseReference.child(currentUser.getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                if(photoAvailable) {
                                    firebaseStorage = FirebaseStorage.getInstance();
                                    StorageReference storageReference = firebaseStorage.getReference();
                                    storageReference.child("ProfilePics/" + currentUser.getUid() + "/profilePic.jpg").putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if(!task.isSuccessful()) {
                                                Toast.makeText(ProfileSetupActivity.this, "Unable to upload Profile Pic", Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.INVISIBLE);
                                                doneButton.setVisibility(View.VISIBLE);
                                                return;
                                            }
                                        }
                                    });
                                }

                                if(removedPicture && photoLoaded) {
                                    firebaseStorage = FirebaseStorage.getInstance();
                                    StorageReference storageReference = firebaseStorage.getReference();
                                    storageReference.child("ProfilePics/" + currentUser.getUid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (!task.isSuccessful()) {
                                                Toast.makeText(ProfileSetupActivity.this, "Unable to delete Profile Pic", Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.INVISIBLE);
                                                doneButton.setVisibility(View.VISIBLE);
                                                return;
                                            }
                                        }
                                    });
                                }

                                Intent intent = new Intent(ProfileSetupActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("type", "firstTime");
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                finish();
                            } else {
                                Toast.makeText(ProfileSetupActivity.this, "Unable to setup user", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                                doneButton.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGEPICK_GALLERY_REQUEST && resultCode == RESULT_OK && data != null & data.getData() != null) {
            ImageUri = data.getData();
            camera.setVisibility(View.GONE);
            proifleImage.setVisibility(View.VISIBLE);
            title.setVisibility(View.INVISIBLE);
            Picasso.get()
                    .load(ImageUri)
                    .centerCrop()
                    .resize(800, 800)
                    .rotate(90)
                    .into(proifleImage);
            photoAvailable = true;

        }

    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(ProfileSetupActivity.this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ProfileSetupActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 100);
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
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
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
                                try{
                                    getURI(user.getUid());
                                } catch (Exception e) {
                                    picassos.add(null);
                                }
                                if(!availableUsers.contains(user)){
                                    availableUsers.add(user);
                                }
                                break;
                            }
                        } else if(userNumber.contains(number.substring(3)) && userNumber.endsWith(number.substring(number.length() - 1))) {
                            user.setName(contact.getName());
                            try{
                                getURI(user.getUid());
                            } catch (Exception e) {
                                picassos.add(null);
                            }
                            if(!availableUsers.contains(user)){
                                availableUsers.add(user);
                            }
                            break;

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

    private void getURI(String uid){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("ProfilePics/" + uid + "/profilePic.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                picassos.add(String.valueOf(uri));
                newChatActivityAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                picassos.add(null);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getPhoneContacts();
        } else {
            Toast.makeText(ProfileSetupActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
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
        }
    }

}