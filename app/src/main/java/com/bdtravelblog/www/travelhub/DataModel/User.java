package com.bdtravelblog.www.travelhub.DataModel;

import java.util.List;



public class User {
    String uid, name, country, address, phn, proPic, coverPic, level, email, oneLineAbout, about;
    String totalLove,totalLovedPost;
    int totalPost, totalPhoto,totalBuddy,points;
    List<String> userPosts, interestList, travelledList,photos; //lovedPostList

    //List<MessageReceiver> receiver = new ArrayList<>();

    public User() {
    }

    public User(String uid, String name, String country, String address, String phn, String proPic, String coverPic, String level, String email, String oneLineAbout, String about, String totalLove, String totalLovedPost, int totalPost, int totalPhoto, int totalBuddy, int points, List<String> userPosts, List<String> interestList, List<String> travelledList, List<String> photos) {
        this.uid = uid;
        this.name = name;
        this.country = country;
        this.address = address;
        this.phn = phn;
        this.proPic = proPic;
        this.coverPic = coverPic;
        this.level = level;
        this.email = email;
        this.oneLineAbout = oneLineAbout;
        this.about = about;
        this.totalLove = totalLove;
        this.totalLovedPost = totalLovedPost;
        this.totalPost = totalPost;
        this.totalPhoto = totalPhoto;
        this.totalBuddy = totalBuddy;
        this.points = points;
        this.userPosts = userPosts;
        this.interestList = interestList;
        this.travelledList = travelledList;
        this.photos = photos;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhn() {
        return phn;
    }

    public void setPhn(String phn) {
        this.phn = phn;
    }

    public String getProPic() {
        return proPic;
    }

    public void setProPic(String proPic) {
        this.proPic = proPic;
    }

    public String getCoverPic() {
        return coverPic;
    }

    public void setCoverPic(String coverPic) {
        this.coverPic = coverPic;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getTotalPost() {
        return totalPost;
    }

    public void setTotalPost(int totalPost) {
        this.totalPost = totalPost;
    }

    public int getTotalPhoto() {
        return totalPhoto;
    }

    public void setTotalPhoto(int totalPhoto) {
        this.totalPhoto = totalPhoto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getTotalBuddy() {
        return totalBuddy;
    }

    public void setTotalBuddy(int totalBuddy) {
        this.totalBuddy = totalBuddy;
    }

    public String getTotalLove() {
        return totalLove;
    }

    public void setTotalLove(String totalLove) {
        this.totalLove = totalLove;
    }

//    public List<String> getLovedPostList() {
//        return lovedPostList;
//    }
//
//    public void setLovedPostList(List<String> lovedPostList) {
//        this.lovedPostList = lovedPostList;
//    }

    public List<String> getUserPosts() {
        return userPosts;
    }

    public void setUserPosts(List<String> userPosts) {
        this.userPosts = userPosts;
    }

    public String getOneLineAbout() {
        return oneLineAbout;
    }

    public void setOneLineAbout(String oneLineAbout) {
        this.oneLineAbout = oneLineAbout;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getTotalLovedPost() {
        return totalLovedPost;
    }

    public void setTotalLovedPost(String totalLovedPost) {
        this.totalLovedPost = totalLovedPost;
    }

    public List<String> getInterestList() {
        return interestList;
    }

    public void setInterestList(List<String> interestList) {
        this.interestList = interestList;
    }

    public List<String> getTravelledList() {
        return travelledList;
    }

    public void setTravelledList(List<String> travelledList) {
        this.travelledList = travelledList;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
