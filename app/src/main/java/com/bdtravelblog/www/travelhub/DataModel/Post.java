package com.bdtravelblog.www.travelhub.DataModel;

import android.net.Uri;

import java.io.Serializable;
import java.util.List;

/**
 * Created by SAMSUNG on 11/17/2017.
 */

public class Post implements Serializable {
    String username,time,postDetail,userId;
    String type;
    String key;
    String profilePicDownloadUrl;
    List<String> imageDownloadUrl;
    List<Comment> comments;
    String loveCount;
    String commentCount;
    String viewCount;

    public Post() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPostDetail() {
        return postDetail;
    }

    public void setPostDetail(String postDetail) {
        this.postDetail = postDetail;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProfilePicDownloadUrl() {
        return profilePicDownloadUrl;
    }

    public void setProfilePicDownloadUrl(String profilePicDownloadUrl) {
        this.profilePicDownloadUrl = profilePicDownloadUrl;
    }

    public List<String> getImageDownloadUrl() {
        return imageDownloadUrl;
    }

    public void setImageDownloadUrl(List<String> imageDownloadUrl) {
        this.imageDownloadUrl = imageDownloadUrl;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getLoveCount() {
        return loveCount;
    }

    public void setLoveCount(String loveCount) {
        this.loveCount = loveCount;
    }

    public String getViewCount() {
        return viewCount;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
