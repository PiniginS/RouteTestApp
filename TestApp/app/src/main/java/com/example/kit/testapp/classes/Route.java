package com.example.kit.testapp.classes;


import android.os.Environment;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class Route {

    List<LatLng> routePoints = new LinkedList<LatLng>();
    JSONObject json;

    public Route (String url) {
        downloadJson(url);//download json from url
        makeJsonObject();//make JSONobject from file
        createRoutePoints();//parce jsone, create route points
    }

    private void downloadJson(String url){
        try {
            URL u = new URL(url);
            InputStream stream = u.openStream();

            DataInputStream dis = new DataInputStream(stream);

            byte[] buffer = new byte[1024];
            int length;

            FileOutputStream fos = new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/" + "data/route.txt"));
            while ((length = dis.read(buffer))>0) {
                fos.write(buffer, 0, length);
            }
            fos.close();
        } catch (MalformedURLException mue) {
            Log.e("SYNC getUpdate", "malformed url error", mue);
        } catch (IOException ioe) {
            Log.e("SYNC getUpdate", "io error", ioe);
        } catch (SecurityException se) {
            Log.e("SYNC getUpdate", "security error", se);
        }
    }

    private void makeJsonObject(){
        try {
            InputStream is = new FileInputStream(new File(Environment.getExternalStorageDirectory() + "/" + "data/route.txt"));
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new JSONObject(new String(buffer, "UTF-8"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void createRoutePoints() {
        try {
            JSONArray points = json.getJSONArray("coords");
            for (int i=0;i<points.length();i++) {
                routePoints.add(new LatLng((Double)points.getJSONObject(i).get("la"),(Double)points.getJSONObject(i).get("lo")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getJson ()
    {
        return json;
    }

    public List<LatLng> getRoutePoints ()
    {
        return routePoints;
    }

}
