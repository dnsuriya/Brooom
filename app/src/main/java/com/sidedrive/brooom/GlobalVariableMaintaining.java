package com.sidedrive.brooom;

import android.app.Application;

/**
 * Created by wajira on 12/30/17.
 */

public class GlobalVariableMaintaining extends Application {
    private String phoneNumber;
    private String email;
    private String userType;
    private String userFirstName;
    private String userLastName;

    public String getPhoneNumber() {

        return phoneNumber;
    }

    public void setPhoneNumber(String number) {

        phoneNumber = number;

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }
}
