package com.example.tasteless.svangur;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.LruCache;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;


public class CustomGrid extends BaseAdapter {

    private Context mContext;
    private final String[] text;
    private final String[] imageURL;
    private LayoutInflater inflater;

    public CustomGrid(Context mContext, String[] text, String[] image) {
        this.mContext = mContext;
        this.imageURL = image;
        this.text = text;
        this.inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return text.length;
    }

    @Override
    public Object getItem(int position) {
        return text[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.grid_single, parent, false);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.grid_text);
        textView.setText(text[position] + " km");

        ImageView imageView = (ImageView)convertView.findViewById(R.id.grid_image);
        if(imageURL[position].trim().equals("001")) {
            Picasso.with(this.mContext)
                        .load("http://www.bk.com/sites/default/files/VeggieBurger_thumb.png")
                        .into(imageView);
            }else {
            Picasso.with(this.mContext)
                        .load(imageURL[position].trim())
                        .into(imageView);
            }
        return convertView;
    }
}







//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        View grid;
//        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
////        Log.d("IED", "count: " + getCount());
//
//        if (convertView == null) {
//            Log.d("IED", "Counter: " + counter);
//            counter += 1;
//
//
//            grid = new View(mContext);
//            grid = inflater.inflate(R.layout.grid_single, null);
//            TextView textView = (TextView) grid.findViewById(R.id.grid_text);
//            ImageView imageView = (ImageView)grid.findViewById(R.id.grid_image);
//            textView.setText(web[position]);
////            imageView.setImageResource(Imageid[position]);
//
//            if(Imageid[counter].trim().equals("001")) {
//                Picasso.with(this.mContext)
//                        .load("https://www.google.is/url?sa=i&rct=j&q=&esrc=s&source=images&cd=&cad=rja&uact=8&ved=0ahUKEwjpwPD3xZTKAhVDwBQKHQLLAVEQjRwIBw&url=http%3A%2F%2Feplcr.com%2Fwrite-hamburger%2F&psig=AFQjCNEdAz3hnYFfwIAMgb1pJpRj1baJsA&ust=1452147714838074")
//                        .into(imageView);
////                Log.d("IED", "Equals: 001");
//            }else {
////                Log.d("IED", "Position: " + position);
//                Picasso.with(this.mContext)
//                        .load(Imageid[counter].trim())
//                        .into(imageView);
////                Log.d("IED", Imageid[position]);
////                Log.d("IED", "here");
//            }
//
////            Log.d("IED", "Test");
//        } else {
////            Log.d("IED", "Test1");
//            grid = (View) convertView;
//        }
//
//        return grid;
//    }
