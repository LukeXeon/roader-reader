package edu.guet.roader;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import edu.guet.roader.model.Line;
import edu.guet.roader.model.Point;
import edu.guet.roader.model.RawRoad;
import edu.guet.roader.model.Road;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class RoadReader
{
    private static class Roads
    {
        @SerializedName("roads")
        private List<RawRoad> rawRoads;
    }

    private static final String APP_KEY = "7c269c7b934c4e25a6d2acccfc58c785";

    private static final String BASE_URL = "http://restapi.amap.com/v3/road/roadname";

    private static final String code = "029";

    private static final Set<String> ROAD_NAMES = new HashSet<String>()
    {
        {
            add("二环北路西段");
            add("二环北路东段");
            add("二环南路东段");
            add("二环南路西段");
            add("西二环路");
            add("丰庆路");
            add("大庆路");
            add("太乙路");
            add("太华南路");
            add("东二环路");
            add("星火路");
            add("太白北路");
            add("环城东路北段");
            add("环城东路南段");
            add("环城南路东段");
            add("环城南路西段");
            add("环城西路北段");
            add("咸宁西路");
            add("咸宁中路");
            add("朱宏路");
            add("大兴东路");
            add("长乐西路");
            add("长乐中路");
            add("长乐东路");
            add("未央路");
            add("长安北路");
            add("长安中路");
            add("长安南路");
            add("东关正街");
            add("北关正街");
            add("西关正街");
            add("兴庆路");
            add("兴庆西路");
            add("公园南路");
            add("永松路");
            add("电子正街");
            add("电子二路");
            add("轻松路");
            add("雁塔西路");
            add("雁塔一路");
            add("雁塔二路");
            add("小寨西路");
            add("小寨东路");
            add("雁南东路");
            add("上亿路");
            add("雁翔路");
            add("幸福南路");
            add("幸福北路");
            add("长营东路");
            add("长营西路");
            add("公园北路");
            add("韩森路");
            add("雁南西路");
            add("雁南一路");
            add("雁南二路");
            add("慈恩路");
            add("芙蓉东路");
            add("芙蓉西路");
            add("南关正街");
            add("南大街");
            add("北大街");
            add("西延路");
            add("曲江路");
            add("咸宁东路");

        }
    };

    private final OkHttpClient client;

    private final Gson gson = new Gson();

    private final List<Road> roads = new ArrayList<>();

    private static void test(Class<?> tClass)
    {

    }

    private static final List<Point> points;

    static
    {
        points = new ArrayList<>();
        points.add(new Point(108.92309, 34.279936));
        points.add(new Point(109.008833, 34.278608));
        points.add(new Point(109.009348, 34.207309));
        points.add(new Point(108.921859, 34.204946));
    }

    public static void main(String[] args) throws Exception
    {
        Gson gson = new Gson();
        RoadReader roadReader = new RoadReader();
        for (String key : ROAD_NAMES)
        {
            roadReader.load(code, key);
        }
        List<Line> lines = new ArrayList<>();
        Thread.sleep(15000);
        for (Road road : roadReader.roads)
        {
            for (List<List<Double>> subLine : road.points)
            {
                if (subLine.size() == 1)
                {
                    throw new Exception(subLine.toString());
                }
                for (int i = 0; i < subLine.size() - 1; i++)
                {
                    List<Double> rawPoint = subLine.get(i);
                    Point point1 = new Point(rawPoint.get(0), rawPoint.get(1));
                    rawPoint = subLine.get(i + 1);
                    Point point2 = new Point(rawPoint.get(0), rawPoint.get(1));
                    if (isInPolygon(point1) || isInPolygon(point2))
                    {
                        Line line = new Line(road.name, point1, point2);
                        lines.add(line);
                    } else
                    {
                        System.out.println(road.name);
                        System.out.println(gson.toJson(point1));
                        System.out.println(gson.toJson(point2));
                    }
                }
            }
        }
        System.out.println(lines.size());
        System.out.println();
        System.out.println(gson.toJson(lines));

    }

    @Override
    public String toString()
    {
        return gson.toJson(roads);
    }

    private RoadReader()
    {
        client = new OkHttpClient.Builder()
                .callTimeout(5, TimeUnit.SECONDS)
                .build();

    }


    public static final Set<String> strings = new HashSet<>();

    public void load(final String citycode, final String keywords)
    {
        StringBuilder builder = new StringBuilder();
        builder.append(BASE_URL).append('?')
                .append("city=").append(citycode)
                .append('&')
                .append("key=").append(APP_KEY)
                .append('&')
                .append("keywords=").append(keywords);
        String url = builder.toString();
        System.out.println(url);
        Request request = new Request.Builder().url(url)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                System.out.println("error: " + keywords);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                assert response.body() != null;
                String json = response.body().string();
                List<RawRoad> rawRoads
                        = gson.fromJson(json, Roads.class).rawRoads;
                if (rawRoads != null && rawRoads.size() != 0)
                {
                    System.out.println("out: " + keywords);
                    for (RawRoad rawRoad : rawRoads)
                    {
                        if (keywords.equals(rawRoad.name))
                        {
                            strings.add(rawRoad.name);
                            Road road = new Road();
                            road.name = rawRoad.name;
                            road.type = rawRoad.type;
                            road.width = rawRoad.width;
                            road.points = new ArrayList<>();
                            for (String rawPoints : rawRoad.polylines)
                            {
                                List<List<Double>> line = new ArrayList<>();
                                for (String point2 : rawPoints.split(";"))
                                {
                                    List<Double> locates = new ArrayList<>();
                                    for (String point : point2.split(","))
                                    {
                                        locates.add(Double.valueOf(point));
                                    }
                                    line.add(locates);
                                }
                                road.points.add(line);
                            }
                            synchronized (RoadReader.this)
                            {
                                roads.add(road);
                            }
                        }
                    }
                } else
                {
                    System.out.println("empty: " + keywords);
                }
            }
        });
    }


    public static boolean isInPolygon(Point lpoint)
    {
        // 将要判断的横纵坐标组成一个点
        Point2D.Double point = new Point2D.Double(lpoint.x, lpoint.y);
        // 将区域各顶点的横纵坐标放到一个点集合里面
        List<Point2D.Double> pointList = new ArrayList<>();
        double polygonPoint_x = 0.0, polygonPoint_y = 0.0;
        for (int i = 0; i < points.size(); i++)
        {
            polygonPoint_x = points.get(i).x;
            polygonPoint_y = points.get(i).y;
            Point2D.Double polygonPoint = new Point2D.Double(polygonPoint_x, polygonPoint_y);
            pointList.add(polygonPoint);
        }
        return check(point, pointList);
    }

    /**
     * 一个点是否在多边形内
     *
     * @param point   要判断的点的横纵坐标
     * @param polygon 组成的顶点坐标集合
     * @return
     */
    private static boolean check(Point2D.Double point, List<Point2D.Double> polygon)
    {
        java.awt.geom.GeneralPath peneralPath = new java.awt.geom.GeneralPath();

        Point2D.Double first = polygon.get(0);
        // 通过移动到指定坐标（以双精度指定），将一个点添加到路径中
        peneralPath.moveTo(first.x, first.y);
        polygon.remove(0);
        for (Point2D.Double d : polygon)
        {
            // 通过绘制一条从当前坐标到新指定坐标（以双精度指定）的直线，将一个点添加到路径中。
            peneralPath.lineTo(d.x, d.y);
        }
        // 将几何多边形封闭
        peneralPath.lineTo(first.x, first.y);
        peneralPath.closePath();
        // 测试指定的 Point2D 是否在 Shape 的边界内。
        return peneralPath.contains(point);
    }

}
