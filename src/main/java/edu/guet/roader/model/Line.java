package edu.guet.roader.model;

public class Line
{
    public Line(String name,Point start,Point end)
    {
        this.name = name;
        this.start = start;
        this.end = end;
    }
    public String name;
    public Point start;
    public Point end;
}
