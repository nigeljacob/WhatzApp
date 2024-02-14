package com.nnjtrading.whatzapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.ArrayList;

public class fragment_chats extends Fragment {

    private ImageButton newChat;
    private RecyclerView recyclerView;
    private ArrayList<Message> conversations;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_chats, container, false);

        newChat = v.findViewById(R.id.newChatsButton);
        recyclerView = v.findViewById(R.id.conversationsRecycleView);

        conversations = new ArrayList<>();

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);

        ConversationAdapter conversationAdapter = MainActivity.conversationAdapter;
        recyclerView.setAdapter(conversationAdapter);

        conversationAdapter.notifyDataSetChanged();


        newChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewChatActivity.class);
                startActivity(intent);
            }
        });

        // Inflate the layout for this fragment
        return v;
    }
}