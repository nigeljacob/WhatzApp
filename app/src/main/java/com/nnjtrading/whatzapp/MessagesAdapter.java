package com.nnjtrading.whatzapp;

import android.content.Context;
import android.os.Handler;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MessagesAdapter extends RecyclerView.Adapter<MessageViewHolder> {

    private Context context;
    private ArrayList<Message> messages;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    public static ArrayList<String> date = new ArrayList<>();
    private String[] months = {"January", "February","March","April","May","June","July","August","September","October","November","December"};

    public MessagesAdapter(Context context, ArrayList<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.send_receieve_message_layout, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        if(message.getSentUserUid().equals(currentUser.getUid())) {
            holder.receivedMessagelayout.setVisibility(View.GONE);
            holder.sendMessage.setText(message.getMessage());
            String[] time = message.getSentTime().split(":");
            holder.sentTime.setText(time[0] + ":" + time[1]);
            holder.sentMessageLayout.setVisibility(View.VISIBLE);
            String messageStatus = message.getMessageStatus();
            if(messageStatus.equals("sent")){
                holder.sentStatus.setImageResource(R.drawable.double_tick_gray_com);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(50, 50);
                holder.sentStatus.setLayoutParams(layoutParams);
                ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) holder.sentStatus.getLayoutParams();
                marginParams.setMargins(0,20,14,-27);
            }

            if(message.isSeen()) {
                holder.sentStatus.setImageResource(R.drawable.double_tick_blue_com);
            }

        } else {
            holder.sentMessageLayout.setVisibility(View.GONE);
            holder.reveivedMessage.setText(message.getMessage());
            String[] time = message.getSentTime().split(":");
            holder.recievedTime.setText(time[0] + ":" + time[1]);
            holder.receivedMessagelayout.setVisibility(View.VISIBLE);
        }

        String dateText = message.getSentDate();
        if(!date.contains(dateText)) {
            String[] dateList = dateText.split("/");
            String[] dateTimeNow = getDateTime().split(" ");
            String[] dateToday = dateTimeNow[0].split("/");
            String DateToText = "";
            if(dateText.equals(dateTimeNow[0])) {
                DateToText = "Today";
            } else if(dateList[0].equals(String.valueOf(Integer.parseInt(dateToday[0]) - 1)) && dateList[1].equals(dateToday[1]) && dateList[2].equals(dateToday[2])) {
                DateToText = "Yesterday";
            } else {
                DateToText = dateList[0] + " " + months[Integer.parseInt(dateList[1]) - 1] + " " + dateList[2];
            }
            holder.date.setText(DateToText);
            holder.dateLayout.setVisibility(View.VISIBLE);
            date.add(dateText);
        } else {
            holder.dateLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private String getDateTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();

        return formatter.format(date);
    }
}



class MessageViewHolder extends RecyclerView.ViewHolder {
     LinearLayout sentMessageLayout, receivedMessagelayout;
     ConstraintLayout dateLayout;
     TextView sendMessage, sentTime, reveivedMessage, recievedTime, date;
     ImageView sentStatus;
    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
        // sent Layout
        sentMessageLayout = itemView.findViewById(R.id.sendMessageLayout);
        sendMessage = itemView.findViewById(R.id.sentmessageText);
        sentTime = itemView.findViewById(R.id.sentmessageTime);
        sentStatus = itemView.findViewById(R.id.messageStatus);

        // received Message
        receivedMessagelayout = itemView.findViewById(R.id.receivedMessageLayout);
        reveivedMessage = itemView.findViewById(R.id.reveivedMessageText);
        recievedTime = itemView.findViewById(R.id.recievedmessageTime);

        // date
        dateLayout = itemView.findViewById(R.id.messageDateLayout);
        date = itemView.findViewById(R.id.messageDate);

    }
}
