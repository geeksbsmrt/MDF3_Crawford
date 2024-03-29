package com.example.adamcrawford.socialite.events;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Author:  Adam Crawford
 * Project: Socialite
 * Package: com.example.adamcrawford.socialite.events
 * File:    EventConstructor
 * Purpose: TODO Minimum 2 sentence description
 */
public class EventConstructor {
    public String eventName;
    public String eventTime;
    public String eventLocation;
    public String eventCity;
    public String eventURL;

    public EventConstructor(JSONObject event){
        try {
            this.eventName = event.getJSONObject("event").getString("title");
            this.eventTime = event.getJSONObject("event").getString("start_date");
            this.eventLocation = event.getJSONObject("event").getJSONObject("venue").getString("address");
            this.eventCity = event.getJSONObject("event").getJSONObject("venue").getString("city");
            this.eventURL = event.getJSONObject("event").getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
