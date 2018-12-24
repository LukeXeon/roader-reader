package edu.guet.roader.model;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Field;
import java.util.List;

public class RawRoad
{
    @SerializedName("id")
    public String id;
    @SerializedName("name")
    public String name;
    @SerializedName("citycode")
    public String citycode;
    @SerializedName("width")
    public long width;
    @SerializedName("type")
    public String type;
    @SerializedName("center")
    public String center;
    @SerializedName("polylines")
    public List<String> polylines;

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        for (Field field : getClass().getDeclaredFields())
        {
            SerializedName serializedName = field.getAnnotation(SerializedName.class);
            if (serializedName != null)
            {
                field.setAccessible(true);
                try
                {
                    if (List.class.isAssignableFrom(field.getType()))
                    {
                        builder.append(serializedName.value())
                                .append('=')
                                .append('¡ý')
                                .append('\n');
                        for (Object object : (List) field.get(this))
                        {
                            builder.append('¡ú')
                                    .append(object)
                                    .append(';')
                                    .append('\n');
                        }
                    }
                    else
                    {
                        builder.append(serializedName.value())
                                .append('=')
                                .append(field.get(this))
                                .append(';')
                                .append('\n');
                    }
                } catch (IllegalAccessException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
    }
}
