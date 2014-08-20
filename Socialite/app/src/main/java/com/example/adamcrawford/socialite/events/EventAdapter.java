package com.example.adamcrawford.socialite.events;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.adamcrawford.socialite.R;

import java.util.ArrayList;

/**
 * Author:  Adam Crawford
 * Project: Socialite
 * Package: com.example.adamcrawford.socialite.events
 * File:    EventAdapter
 * Purpose: TODO Minimum 2 sentence description
 */
public class EventAdapter extends ArrayAdapter<EventConstructor> {
    private Context context;
    private ArrayList<EventConstructor> objects;

    public EventAdapter(Context context, int resource, ArrayList<EventConstructor> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        EventConstructor event = objects.get(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_event, null);
            holder = new ViewHolder();
            holder.eventName = (TextView) convertView.findViewById(R.id.eventName);
            holder.eventTime = (TextView) convertView.findViewById(R.id.eventTime);
            holder.eventLocation = (TextView) convertView.findViewById(R.id.eventLocation);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.eventName.setText(event.eventName);
        holder.eventTime.setText(event.eventTime);
        holder.eventLocation.setText(event.eventLocation);

        return convertView;
    }

    static class ViewHolder {
        TextView eventName;
        TextView eventTime;
        TextView eventLocation;
    }
}
