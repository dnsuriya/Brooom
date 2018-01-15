package com.sidedrive.brooom;

import android.app.Application;

/**
 * Created by wajira on 12/30/17.
 */

public class GlobalVariableMaintaining extends Application {
    private String phoneNumber;
    private String email;
    private String userType;

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
}
