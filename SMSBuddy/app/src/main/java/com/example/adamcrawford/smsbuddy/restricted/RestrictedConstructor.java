package com.example.adamcrawford.smsbuddy.restricted;

import java.io.Serializable;

/**
 * Author:  Adam Crawford
 * Project: SMSBuddy
 * Package: com.example.adamcrawford.smsbuddy.restricted
 * File:    RestrictedConstructor
 * Purpose: TODO Minimum 2 sentence description
 */
public class RestrictedConstructor implements Serializable {

    public String rName;

    public RestrictedConstructor(String iName) {
        this.rName = iName;
    }
}
