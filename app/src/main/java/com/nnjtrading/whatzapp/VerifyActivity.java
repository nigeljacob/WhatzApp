package com.nnjtrading.whatzapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.nnjtrading.whatzapp.R;

public class VerifyActivity extends AppCompatActivity {

    private EditText VerificationInput;
    private Button verifyButton;
    private TextView changeNumber;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        Intent intent = getIntent();

        String PhoneNumber = intent.getStringExtra("phoneNumber");
        String verificationID = intent.getStringExtra("verificationID");

        System.out.println(PhoneNumber);

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

        VerificationInput = findViewById(R.id.VerificationInput);
        verifyButton = findViewById(R.id.verifyButton);
        progressBar = findViewById(R.id.progressBar);
        changeNumber = findViewById(R.id.changeNumber);
        firebaseAuth = FirebaseAuth.getInstance();
        verifyButton.setEnabled(false);

        VerificationInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(VerificationInput.getText().length() != 6) {
                    verifyButton.setEnabled(false);
                } else {
                    verifyButton.setEnabled(true);
                }
            }
        });

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setView(R.layout.verification_loadin);
        builder.setCancelable(false);

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = builder.show();
                dialog.show();

                final String VerificationCode = VerificationInput.getText().toString();

                PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationID, VerificationCode);
                firebaseAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(VerifyActivity.this, "Verification Successful", Toast.LENGTH_SHORT).show();
                            String UserType = "";
                            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                            if(currentUser.getDisplayName() == null) {
                                UserType = "New";
                            } else {
                                UserType = "Old";
                            }
                            Intent intent = new Intent(VerifyActivity.this, ProfileSetupActivity.class);
                            intent.putExtra("type", UserType);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            finish();
                        } else {
                            System.out.println("eror");
                            dialog.dismiss();
                        }
                    }
                });

            }
        });

        changeNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}