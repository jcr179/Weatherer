package jrebanal.weatherer;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

/**
 * Created by jrebanal on 1/1/16.
 */
public class HourlyWeather {
    final static private int n = 18; // number of hours ahead of certain time desired for forecast
    static private long timeNow;
    static private double[] temps1 = new double[n];
    static private double[] atemps1 = new double[n];
    static private double[] temps2 = new double[n];
    static private double[] atemps2 = new double[n];
    static private double[] tempsTom1 = new double[n];
    static private double[] atempsTom1 = new double[n];
    static private double[] tempsTom2 = new double[n];
    static private double[] atempsTom2 = new double[n];
    static private String[] todayConditions1 = new String[n];
    static private String[] todayConditions2 = new String[n];
    static private String[] tomorrowConditions1 = new String[n];
    static private String[] tomorrowConditions2 = new String[n];
    static private String tvTimeToday;
    static private String tvTimeTomorrow;

    public HourlyWeather() {
    }

    public static void setTimeNow(long timeNow) {
        HourlyWeather.timeNow = timeNow;
    }

    public static String getTvTimeToday() {
        return tvTimeToday;
    }

    public static void setTvTimeToday(String tvTimeToday) {
        HourlyWeather.tvTimeToday = tvTimeToday;
    }

    public static String getTvTimeTomorrow() {
        return tvTimeTomorrow;
    }

    public static void setTvTimeTomorrow(String tvTimeTomorrow) {
        HourlyWeather.tvTimeTomorrow = tvTimeTomorrow;
    }

    public static String[] getTodayConditions1() {
        return todayConditions1;
    }

    public static void setTodayConditions1(String a, int n) {
        HourlyWeather.todayConditions1[n] = a;
    }

    public static String[] getTodayConditions2() {
        return todayConditions2;
    }

    public static void setTodayConditions2(String a, int n) {
        HourlyWeather.todayConditions2[n] = a;
    }

    public static String[] getTomorrowConditions1() {
        return tomorrowConditions1;
    }

    public static void setTomorrowConditions1(String a, int n) {
        HourlyWeather.tomorrowConditions1[n] = a;
    }

    public static String[] getTomorrowConditions2() {
        return tomorrowConditions2;
    }

    public static void setTomorrowConditions2(String a, int n) {
        HourlyWeather.tomorrowConditions2[n] = a;
    }

    public static void setTemps1(double temps1, int n) {
        HourlyWeather.temps1[n] = temps1;
    }

    public static void setAtemps1(double atemps1, int n) {
        HourlyWeather.atemps1[n] = atemps1;
    }

    public static void setTemps2(double temps2, int n) {
        HourlyWeather.temps2[n] = temps2;
    }

    public static void setAtemps2(double atemps2, int n) {
        HourlyWeather.atemps2[n] = atemps2;
    }

    public static double[] getTempsTom1() {
        return tempsTom1;
    }

    public static void setTempsTom1(double tempsTom1, int n) {
        HourlyWeather.tempsTom1[n] = tempsTom1;
    }

    public static double[] getAtempsTom1() {
        return atempsTom1;
    }

    public static void setAtempsTom1(double atempsTom1, int n) {
        HourlyWeather.atempsTom1[n] = atempsTom1;
    }

    public static double[] getTempsTom2() {
        return tempsTom2;
    }

    public static void setTempsTom2(double tempsTom2, int n) {
        HourlyWeather.tempsTom2[n] = tempsTom2;
    }

    public static double[] getAtempsTom2() {
        return atempsTom2;
    }

    public static void setAtempsTom2(double atempsTom2, int n) {
        HourlyWeather.atempsTom2[n] = atempsTom2;
    }

    /*
    public double[] getValues() {
        double[] result = new double[18];



        return result;
    }
    */

    public long getTime() {
        return timeNow;
    }

    public double[] getTemps1() {
        return temps1;
    }

    public double[] getAtemps1() {
        return atemps1;
    }

    public double[] getTemps2() {
        return temps2;
    }

    public double[] getAtemps2() {
        return atemps2;
    }


}
