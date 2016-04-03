package com.ccc.tasteless.svangur;

import java.util.Random;

/**
 * Created by Tasteless on 15.2.2016.
 */
public class Utility {

    // Remove last character from a String
    public static String removeLastChar(String str) {
        if(str.equals("")) return str;
        return str.substring(0,str.length()-2
        );
    }

    public static int randInt(int min, int max) {

        // Usually this can be a field rather than a method variable
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

}
