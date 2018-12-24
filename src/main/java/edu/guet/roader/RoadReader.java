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
            add("������·����");
            add("������·����");
            add("������·����");
            add("������·����");
            add("������·");
            add("����·");
            add("����·");
            add("̫��·");
            add("̫����·");
            add("������·");
            add("�ǻ�·");
            add("̫�ױ�·");
            add("���Ƕ�·����");
            add("���Ƕ�·�϶�");
            add("������·����");
            add("������·����");
            add("������·����");
            add("������·");
            add("������·");
            add("���·");
            add("���˶�·");
            add("������·");
            add("������·");
            add("���ֶ�·");
            add("δ��·");
            add("������·");
            add("������·");
            add("������·");
            add("��������");
            add("��������");
            add("��������");
            add("����·");
            add("������·");
            add("��԰��·");
            add("����·");
            add("��������");
            add("���Ӷ�·");
            add("����·");
            add("������·");
            add("����һ·");
            add("������·");
            add("Сկ��·");
            add("Сկ��·");
            add("���϶�·");
            add("����·");
            add("����·");
            add("�Ҹ���·");
            add("�Ҹ���·");
            add("��Ӫ��·");
            add("��Ӫ��·");
            add("��԰��·");
            add("��ɭ·");
            add("������·");
            add("����һ·");
            add("���϶�·");
            add("�ȶ�·");
            add("ܽ�ض�·");
            add("ܽ����·");
            add("�Ϲ�����");
            add("�ϴ��");
            add("�����");
            add("����·");
            add("����·");
            add("������·");

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
        // ��Ҫ�жϵĺ����������һ����
        Point2D.Double point = new Point2D.Double(lpoint.x, lpoint.y);
        // �����������ĺ�������ŵ�һ���㼯������
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
     * һ�����Ƿ��ڶ������
     *
     * @param point   Ҫ�жϵĵ�ĺ�������
     * @param polygon ��ɵĶ������꼯��
     * @return
     */
    private static boolean check(Point2D.Double point, List<Point2D.Double> polygon)
    {
        java.awt.geom.GeneralPath peneralPath = new java.awt.geom.GeneralPath();

        Point2D.Double first = polygon.get(0);
        // ͨ���ƶ���ָ�����꣨��˫����ָ��������һ������ӵ�·����
        peneralPath.moveTo(first.x, first.y);
        polygon.remove(0);
        for (Point2D.Double d : polygon)
        {
            // ͨ������һ���ӵ�ǰ���굽��ָ�����꣨��˫����ָ������ֱ�ߣ���һ������ӵ�·���С�
            peneralPath.lineTo(d.x, d.y);
        }
        // �����ζ���η��
        peneralPath.lineTo(first.x, first.y);
        peneralPath.closePath();
        // ����ָ���� Point2D �Ƿ��� Shape �ı߽��ڡ�
        return peneralPath.contains(point);
    }

}
