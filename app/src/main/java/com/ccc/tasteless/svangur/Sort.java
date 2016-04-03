package com.ccc.tasteless.svangur;

import android.app.Activity;
import android.widget.CheckBox;
import android.widget.SeekBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Created by Tasteless on 7.1.2016.
 */
public class Sort {

    public Activity activity;

    public Sort(Activity _activity){

        this.activity = _activity;
    }

    public JSONArray sortByKm(JSONArray jsonArray) {

        SeekBar kmPlacholder = (SeekBar)this.activity.findViewById(R.id.seekbar);
        int km = kmPlacholder.getProgress()*1000;
        if(km == 11000) km = 20000;

        JSONArray test = new JSONArray();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {

                JSONObject item = (JSONObject)jsonArray.get(i);
                String s = item.getString("distance");

                int f = Integer.parseInt(s);
                if(f < km) {
                    test.put(jsonArray.getJSONObject(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return test;
    }

    public JSONArray shuffle(JSONArray array) throws JSONException {
        // Implementing Fisher–Yates shuffle
        Random rnd = new Random();
        for (int i = array.length() - 1; i >= 0; i--) {
            int j = rnd.nextInt(i + 1);
            // Simple swap
            Object object = array.get(j);
            array.put(j, array.get(i));
            array.put(i, object);
        }
        return array;
    }

    public static JSONArray sortByDistance(JSONArray jsonArray) {

        JSONArray sortedJsonArray = new JSONArray();
        List<JSONObject> jsonList = new ArrayList<JSONObject>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                jsonList.add(jsonArray.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        Collections.sort(jsonList, new Comparator<JSONObject>() {

            public int compare(JSONObject a, JSONObject b) {
                int valA = 0;
                int valB = 0;

                try {
                    valA = (int) a.get("distance");
                    valB = (int) b.get("distance");
                } catch (JSONException e) {
                    //do something
                }

                return (valA) - (valB);
            }
        });

        for (int i = 0; i < jsonArray.length(); i++) {
            sortedJsonArray.put(jsonList.get(i));
        }

        return sortedJsonArray;
    }

    public JSONArray sortByType(JSONArray jsonArray) {

        CheckBox checkbox_pizza = (CheckBox)activity.findViewById(R.id.checkbox_pizza);
        CheckBox checkbox_hamburger = (CheckBox)activity.findViewById(R.id.checkbox_hamburger);
        CheckBox checkbox_sushi = (CheckBox)activity.findViewById(R.id.checkbox_sushi);
        CheckBox checkbox_seafood = (CheckBox)activity.findViewById(R.id.checkbox_seafood);
        CheckBox checkbox_steak = (CheckBox)activity.findViewById(R.id.checkbox_steak);
        CheckBox checkbox_indian = (CheckBox)activity.findViewById(R.id.checkbox_indian);
        CheckBox checkbox_italian = (CheckBox)activity.findViewById(R.id.checkbox_italian);
        CheckBox checkbox_asian = (CheckBox)activity.findViewById(R.id.checkbox_asian);
        CheckBox checkbox_fastFood = (CheckBox)activity.findViewById(R.id.checkbox_fastFood);
        CheckBox checkbox_fancy = (CheckBox)activity.findViewById(R.id.checkbox_fancy);
        CheckBox checkbox_healthy = (CheckBox)activity.findViewById(R.id.checkbox_healthy);

        JSONArray outputArray = new JSONArray();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {

                JSONObject item = (JSONObject)jsonArray.get(i);

                if(checkbox_pizza.isActivated()) {
                    if("1".equals(item.getString("pizza"))) {
                        outputArray.put(jsonArray.getJSONObject(i));
                        continue;
                    }
                }

                if(checkbox_hamburger.isChecked()) {
                    if("1".equals(item.getString("hamburger"))) {
                        outputArray.put(jsonArray.getJSONObject(i));
                        continue;
                    }
                }

                if(checkbox_sushi.isChecked()) {
                    if("1".equals(item.getString("sushi"))) {
                    outputArray.put(jsonArray.getJSONObject(i));
                    continue;}
                }

                if(checkbox_seafood.isChecked()) {
                    if("1".equals(item.getString("seafood"))) {
                        outputArray.put(jsonArray.getJSONObject(i));
                        continue;}
                }

                if(checkbox_steak.isChecked()) {
                    if("1".equals(item.getString("steak"))) {
                        outputArray.put(jsonArray.getJSONObject(i));
                        continue;}
                }

                if(checkbox_indian.isChecked()) {
                    if("1".equals(item.getString("indian"))) {
                        outputArray.put(jsonArray.getJSONObject(i));
                        continue;}
                }

                if(checkbox_italian.isChecked()) {
                    if("1".equals(item.getString("italian"))) {
                        outputArray.put(jsonArray.getJSONObject(i));
                        continue;}
                }

                if(checkbox_asian.isChecked()) {
                    if("1".equals(item.getString("asian"))) {
                        outputArray.put(jsonArray.getJSONObject(i));
                        continue;}
                }

                if(checkbox_fastFood.isChecked()) {
                    if("1".equals(item.getString("fastfood"))) {
                        outputArray.put(jsonArray.getJSONObject(i));
                        continue;}
                }

                if(checkbox_fancy.isChecked()) {
                    if("1".equals(item.getString("fancy"))) {
                        outputArray.put(jsonArray.getJSONObject(i));
                        continue;}
                }

                if(checkbox_healthy.isChecked()) {
                    if("1".equals(item.getString("healthy"))) {
                        outputArray.put(jsonArray.getJSONObject(i));
                        continue;}
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return outputArray;
    }
}
