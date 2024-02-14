package com.nnjtrading.whatzapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;

public class NewChatActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    private  NewChatActivityAdapter adapter;
    private TextView contactsCount;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chat);

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
                    break;

                case Configuration.UI_MODE_NIGHT_NO:
                    window.setStatusBarColor(getResources().getColor(R.color.primary));
                    window.setNavigationBarColor(getResources().getColor(R.color.white));
                    break;
            }
        }

        recyclerView = findViewById(R.id.recycleView);
        contactsCount = findViewById(R.id.contactsCount);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = MainActivity.newChatActivityAdapter;
        recyclerView.setAdapter(adapter);
        contactsCount.setText(adapter.getItemCount() + " Contacts");
        adapter.notifyDataSetChanged();



    }

    @Override
    protected void onResume() {
        super.onResume();
        MessagesAdapter.date.clear();
    }
}