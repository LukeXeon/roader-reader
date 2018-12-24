package edu.guet.roader.model;

import com.google.gson.annotations.SerializedName;

public class Point
{

    public Point(double x, double y)
    {
        this.x = x;
        this.y = y;
    }


    //¾­¶È
    @SerializedName("longitude")
    public double x;
    @SerializedName("latitude")
    public double y;
}