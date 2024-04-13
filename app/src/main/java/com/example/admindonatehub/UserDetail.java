package com.example.admindonatehub;


public class UserDetail {
    private String userName;
    private String userEmail;
    private double donationAmount;
    private String imageUrl;



    public UserDetail(String userName, String userEmail, double donationAmount, String imageUrl) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.donationAmount = donationAmount;
        this.imageUrl = imageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public double getDonationAmount() {
        return donationAmount;
    }

    public void setDonationAmount(double donationAmount) {
        this.donationAmount = donationAmount;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
