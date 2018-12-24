package edu.guet.roader.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Road
{
    @SerializedName("type")
    public String type;
    @SerializedName("name")
    public String name;
    @SerializedName("width")
    public long width;
    @SerializedName("points")
    public List<List<List<Double>>> points;
}
