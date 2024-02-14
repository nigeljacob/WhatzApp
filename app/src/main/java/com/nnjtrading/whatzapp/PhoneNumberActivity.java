package com.nnjtrading.whatzapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.nnjtrading.whatzapp.R;

import java.util.concurrent.TimeUnit;

public class PhoneNumberActivity extends AppCompatActivity {

    private Spinner spinner;
    private String[] countries, countryCodeList;
    private EditText countryCode, phoneNumber;
    private Button nextButton;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number);

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

        spinner = findViewById(R.id.country);
        countryCode = findViewById(R.id.countryCode);
        phoneNumber = findViewById(R.id.PhoneNumberInput);
        nextButton = findViewById(R.id.nextButton);
        progressBar = findViewById(R.id.progressBar);
        firebaseAuth = FirebaseAuth.getInstance();

        nextButton.setEnabled(false);

        countries = new String[]{
                "Abkhazia", "Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra", "Angola", "Anguilla", "Antigua and Barbuda", "Argentina", "Armenia", "Aruba", "Ascension", "Australia", "Australian External Territories", "Austria", "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Barbuda", "Belarus", "Belgium", "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia", "Bosnia and Herzegovina", "Botswana", "Brazil", "British Indian Ocean Territory", "British Virgin Islands", "Brunei", "Bulgaria", "Burkina Faso", "Burundi", "Cambodia", "Cameroon", "Canada", "Cape Verde", "Cayman Islands", "Central African Republic", "Chad", "Chile", "China", "Christmas Island", "Cocos-Keeling Islands", "Colombia", "Comoros", "Congo", "Congo, Dem. Rep. of (Zaire)", "Cook Islands", "Costa Rica", "Croatia", "Cuba", "Curacao", "Cyprus", "Czech Republic", "Denmark", "Diego Garcia", "Djibouti", "Dominica", "Dominican Republic", "East Timor", "Easter Island", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia", "Ethiopia", "Falkland Islands", "Faroe Islands", "Fiji", "Finland", "France", "French Antilles", "French Guiana", "French Polynesia", "Gabon", "Gambia", "Georgia", "Germany", "Ghana", "Gibraltar", "Greece", "Greenland", "Grenada", "Guadeloupe", "Guam", "Guatemala", "Guinea", "Guinea-Bissau", "Guyana", "Haiti", "Honduras", "Hong Kong SAR China", "Hungary", "Iceland", "India", "Indonesia", "Iran", "Iraq", "Ireland", "Israel", "Italy", "Ivory Coast", "Jamaica", "Japan", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Kuwait", "Kyrgyzstan", "Laos", "Latvia", "Lebanon", "Lesotho", "Liberia", "Libya", "Liechtenstein", "Lithuania", "Luxembourg", "Macau SAR China", "Macedonia", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands", "Martinique", "Mauritania", "Mauritius", "Mayotte", "Mexico", "Micronesia", "Midway Island", "Moldova", "Monaco", "Mongolia", "Montenegro", "Montserrat", "Morocco", "Myanmar", "Namibia", "Nauru", "Nepal", "Netherlands", "Netherlands Antilles", "Nevis", "New Caledonia", "New Zealand", "Nicaragua", "Niger", "Nigeria", "Niue", "Norfolk Island", "North Korea", "Northern Mariana Islands", "Norway", "Oman", "Pakistan", "Palau", "Palestinian Territory", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Poland", "Portugal", "Puerto Rico", "Qatar", "Reunion", "Romania", "Russia", "Rwanda", "Samoa", "San Marino", "Saudi Arabia", "Senegal", "Serbia", "Seychelles", "Sierra Leone", "Singapore", "Slovakia", "Slovenia", "Solomon Islands", "South Africa", "South Georgia and the South Sandwich Islands", "South Korea", "Spain", "Sri Lanka", "Sudan", "Suriname", "Swaziland", "Sweden", "Switzerland", "Syria", "Taiwan", "Tajikistan", "Tanzania", "Thailand", "Timor Leste", "Togo", "Tokelau", "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan", "Turks and Caicos Islands", "Tuvalu", "U.S. Virgin Islands", "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom", "United States", "Uruguay", "Uzbekistan", "Vanuatu", "Venezuela", "Vietnam", "Wake Island", "Wallis and Futuna", "Yemen", "Zambia", "Zanzibar", "Zimbabwe"
        };
        countryCodeList = new String[]{
                "+7 840", "+93", "+355", "+213", "+1 684", "+376", "+244", "+1 264", "+1 268", "+54", "+374", "+297", "+247", "+61", "+672", "+43", "+994", "+1 242", "+973", "+880", "+1 246", "+1 268", "+375", "+32", "+501", "+229", "+1 441", "+975", "+591", "+387", "+267", "+55", "+246", "+1 284", "+673", "+359", "+226", "+257", "+855", "+237", "+1", "+238", "+ 345", "+236", "+235", "+56", "+86", "+61", "+61", "+57", "+269", "+242", "+243", "+682", "+506", "+385", "+53", "+599", "+537", "+420", "+45", "+246", "+253", "+1 767", "+1 809", "+670", "+56", "+593", "+20", "+503", "+240", "+291", "+372", "+251", "+500", "+298", "+679", "+358", "+33", "+596", "+594", "+689", "+241", "+220", "+995", "+49", "+233", "+350", "+30", "+299", "+1 473", "+590", "+1 671", "+502", "+224", "+245", "+595", "+509", "+504", "+852", "+36", "+354", "+91", "+62", "+98", "+964", "+353", "+972", "+39", "+225", "+1 876", "+81", "+962", "+7 7", "+254", "+686", "+965", "+996", "+856", "+371", "+961", "+266", "+231", "+218", "+423", "+370", "+352", "+853", "+389", "+261", "+265", "+60", "+960", "+223", "+356", "+692", "+596", "+222", "+230", "+262", "+52", "+691", "+1 808", "+373", "+377", "+976", "+382", "+1664", "+212", "+95", "+264", "+674", "+977", "+31", "+599", "+1 869", "+687", "+64", "+505", "+227", "+234", "+683", "+672", "+850", "+1 670", "+47", "+968", "+92", "+680", "+970", "+507", "+675", "+595", "+51", "+63", "+48", "+351", "+1 787", "+974", "+262", "+40", "+7", "+250", "+685", "+378", "+966", "+221", "+381", "+248", "+232", "+65", "+421", "+386", "+677", "+27", "+500", "+82", "+34", "+94", "+249", "+597", "+268", "+46", "+41", "+963", "+886", "+992", "+255", "+66", "+670", "+228", "+690", "+676", "+1 868", "+216", "+90", "+993", "+1 649", "+688", "+1 340", "+256", "+380", "+971", "+44", "+1", "+598", "+998", "+678", "+58", "+84", "+1 808", "+681", "+967", "+260", "+255", "+263"
        };

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, countries);

        spinner.setAdapter(dataAdapter);
        spinner.setSelection(198);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                countryCode.setText(countryCodeList[spinner.getSelectedItemPosition()]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        phoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(phoneNumber.getText().length() < 5) {
                    nextButton.setEnabled(false);
                } else {
                    nextButton.setEnabled(true);
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                nextButton.setVisibility(View.INVISIBLE);
                final String PhoneNumber = String.valueOf(countryCode.getText()) + String.valueOf(phoneNumber.getText());
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(PhoneNumberActivity.this);
                alertDialog.setMessage("We will be verifying the phone Number\n\n" + PhoneNumber + "\n\nis this OK? or would you like to edit the number?");
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                                        .setPhoneNumber(PhoneNumber)       // Phone number to verify
                                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                        .setActivity(PhoneNumberActivity.this)                 // (optional) Activity for callback binding
                                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                        .build();
                        PhoneAuthProvider.verifyPhoneNumber(options);
                    }

                    final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override
                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                        }

                        @Override
                        public void onVerificationFailed(@NonNull FirebaseException e) {
                            Toast.makeText(PhoneNumberActivity.this, "Failed to send verification Code", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                            nextButton.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onCodeSent(@NonNull String verificationId,
                                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
                            Toast.makeText(PhoneNumberActivity.this, "Verification code send Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(PhoneNumberActivity.this, VerifyActivity.class);
                            intent.putExtra("verificationID", verificationId);
                            intent.putExtra("phoneNumber", PhoneNumber);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                    };
                });
                alertDialog.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                alertDialog.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}