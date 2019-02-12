package com.bdtravelblog.www.travelhub.DataModel;

/**
 * Created by SAMSUNG on 11/16/2017.
 */

public class MessageReceiver {
    String email,message,time,userName,proPic;
    boolean read,sent;

    public MessageReceiver(String email, String message, String time, String userName, String proPic, boolean read, boolean sent) {
        this.email = email;
        this.message = message;
        this.time = time;
        this.userName = userName;
        this.proPic = proPic;
        this.read = read;
        this.sent = sent;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProPic() {
        return proPic;
    }

    public void setProPic(String proPic) {
        this.proPic = proPic;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }
}
