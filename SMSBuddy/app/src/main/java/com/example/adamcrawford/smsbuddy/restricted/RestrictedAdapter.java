package com.example.adamcrawford.smsbuddy.restricted;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.adamcrawford.smsbuddy.R;

import java.util.ArrayList;

/**
 * Author:  Adam Crawford
 * Project: SMSBuddy
 * Package: com.example.adamcrawford.smsbuddy.restricted
 * File:    RestrictedAdapter
 * Purpose: TODO Minimum 2 sentence description
 */
public class RestrictedAdapter extends ArrayAdapter<RestrictedConstructor>{

    private Context context;
    private ArrayList<RestrictedConstructor> objects;

    public RestrictedAdapter(Context context, int resource, ArrayList<RestrictedConstructor> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        RestrictedConstructor person = objects.get(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.restric_list, null);
            holder = new ViewHolder();
            holder.restrictedName = (TextView) convertView.findViewById(R.id.name);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.restrictedName.setText(person.rName);

        return convertView;
    }

    static class ViewHolder {
        TextView restrictedName;
    }
}
