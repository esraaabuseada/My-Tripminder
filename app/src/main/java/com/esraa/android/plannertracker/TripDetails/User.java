package com.esraa.android.plannertracker.TripDetails;

public class User {

    private String userId;
    private String userName;
    private String userEmail;
    private String userPassword;



    public User() {
    }




    public User(String userId, String userEmail, String userPassword) {
        this.userId = userId;

        this.userEmail = userEmail;
        this.userPassword = userPassword;
    }

    public void setUserId (String userId){
        this.userId = userId;
    }

    public String getUserId () {
        return userId;

    }

    public void setUserName (String userName){
        this.userName = userName;
    }

    public void setUserEmail (String userEmail)
    {
        this.userEmail = userEmail;
    }

    public void setUserPassword (String userPassword){
        this.userPassword = userPassword;
    }
    public String getUserName ()
    {
        return userName;
    }

    public String getUserEmail () {
        return userEmail;
    }

    public String getUserPassword () {
        return userPassword;
    }

}
