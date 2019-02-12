package com.bdtravelblog.www.travelhub.DataModel;

import java.io.Serializable;

/**
 * Created by SAMSUNG on 2/23/2018.
 */

public class Comment implements Serializable {
    String comment;
    String profilePicDownloadUrl;
    String time;
    String username;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getProfilePicDownloadUrl() {
        return profilePicDownloadUrl;
    }

    public void setProfilePicDownloadUrl(String profilePicDownloadUrl) {
        this.profilePicDownloadUrl = profilePicDownloadUrl;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
