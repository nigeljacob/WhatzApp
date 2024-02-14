package com.nnjtrading.whatzapp;

import androidx.annotation.NonNull;

import java.util.Arrays;

public class Message {
    private String Message, sentUserUid, sentDate, sentTime, messageType, RecieverUid, senderPhoneNumber, RecieverPhoneNumber, messageStatus, key;
    private boolean Seen;

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getSentUserUid() {
        return sentUserUid;
    }

    public void setSentUserUid(String sentUserUid) {
        this.sentUserUid = sentUserUid;
    }

    public String getSentDate() {
        return sentDate;
    }

    public void setSentDate(String sentDate) {
        this.sentDate = sentDate;
    }

    public String getSentTime() {
        return sentTime;
    }

    public void setSentTime(String sentTime) {
        this.sentTime = sentTime;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public boolean isSeen() {
        return Seen;
    }

    public void setSeen(boolean seen) {
        Seen = seen;
    }

    public String getRecieverUid() {
        return RecieverUid;
    }

    public void setRecieverUid(String recieverUid) {
        RecieverUid = recieverUid;
    }

    public String getSenderPhoneNumber() {
        return senderPhoneNumber;
    }

    public void setSenderPhoneNumber(String senderPhoneNumber) {
        this.senderPhoneNumber = senderPhoneNumber;
    }

    public String getRecieverPhoneNumber() {
        return RecieverPhoneNumber;
    }

    public void setRecieverPhoneNumber(String recieverPhoneNumber) {
        RecieverPhoneNumber = recieverPhoneNumber;
    }

    public String getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(String messageStatus) {
        this.messageStatus = messageStatus;
    }

    public String getKey() {
        return key;
    }

    private String[] DecryptList = new String[]{
        "#", "3", "q", "F", "@", "8", "X", "$", "Y", "J", "b", "T", "m", "g", "E", "h", "1", "v", "s", "R", "n", "A", "p", "P", "L",
                "M", "y", "j", "W", "c", "a", "o", "2", "0", "f", "Z", "7", "H", "N", "l", "D", "G", "S", "O", "V", "x", "w", "i", "K", "5",
                "t", "e", "4", "d", "6", "I", "U", "B", "C", "Q", "k", "r", "£"
    };
    private String[] encryptList = new String[]{
        "A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z",
                "a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z",
                "1","2","3","4","5","6","7","8","9","0", " "};

    @Override
    public String toString() {
        return "Message{" +
                "Message='" + Message + '\'' +
                ", sentUserUid='" + sentUserUid + '\'' +
                ", sentDate='" + sentDate + '\'' +
                ", sentTime='" + sentTime + '\'' +
                ", messageType='" + messageType + '\'' +
                ", RecieverUid='" + RecieverUid + '\'' +
                ", senderPhoneNumber='" + senderPhoneNumber + '\'' +
                ", RecieverPhoneNumber='" + RecieverPhoneNumber + '\'' +
                ", messageStatus='" + messageStatus + '\'' +
                ", key='" + key + '\'' +
                ", Seen=" + Seen +
                ", DecryptList=" + Arrays.toString(DecryptList) +
                ", encryptList=" + Arrays.toString(encryptList) +
                '}';
    }

    @NonNull
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public Message(String message, String sentUserUid, String sentDate, String sentTime, String messageType, String recieverUid, String senderPhoneNumber, String recieverPhoneNumber, String messageStatus, String key, boolean seen) {
        Message = message;
        this.sentUserUid = sentUserUid;
        this.sentDate = sentDate;
        this.sentTime = sentTime;
        this.messageType = messageType;
        RecieverUid = recieverUid;
        this.senderPhoneNumber = senderPhoneNumber;
        RecieverPhoneNumber = recieverPhoneNumber;
        this.messageStatus = messageStatus;
        this.key = key;
        Seen = seen;
    }

    public Message() {

    }

    public void encryptMessage() {
        StringBuilder encyptedMessage = new StringBuilder();
        for(int i = 0; i < this.Message.length(); i++) {
            String letter = String.valueOf(this.Message.charAt(i));
            int index = getIndex(letter, encryptList);
            if(index != -1) {
                encyptedMessage.append(DecryptList[index]);
            } else {
                encyptedMessage.append(letter);
            }
        }

        this.Message = encyptedMessage.toString();
    }

    public void decryptMessage() {
        StringBuilder decrptedMessage = new StringBuilder();
        for(int i = 0; i < this.Message.length(); i++) {
            String letter = String.valueOf(this.Message.charAt(i));
            int index = getIndex(letter, DecryptList);
            if(index != -1) {
                decrptedMessage.append(encryptList[index]);
            } else {
                decrptedMessage.append(letter);
            }
        }
        this.Message = decrptedMessage.toString();
    }

    public int getIndex(String letter, String[] list) {
        for(int i = 0; i < list.length; i++) {
            if(list[i].equals(letter)) {
                return i;
            }
        }
        return -1;
    }
}

