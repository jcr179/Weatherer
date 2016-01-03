package jrebanal.weatherer;

// http://stackoverflow.com/questions/9605913/how-to-parse-json-in-android
// how to parse json in android

// forecast.io api key: be5c0f9b08e52e3f2da12e30ce8d3242

// v0.0001: (initial commit, first master) Got correct temperature to display in current city A text view.

// v0.001: New branch.

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import org.apache.http.HttpEntity;           //had to add a line to the gradle app build script for http stuff to import
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

public class MainActivity extends Activity implements OnItemSelectedListener {
private double lat1, lon1, lat2, lon2;
    private String conditions1, conditions2;
    private String url = "https://api.forecast.io/forecast/"; // ... APIKEY/LATITUDE,LONGITUDE"
    private String apikey = "be5c0f9b08e52e3f2da12e30ce8d3242";
    private String apicall1, apicall2;
    private double temp, apparentTemp, temp2, apparentTemp2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Spinner element
        Spinner spinner = (Spinner) findViewById(R.id.spCities);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> spElements = new ArrayList<String>();
        spElements.add("Mississauga");
        spElements.add("Brampton");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spElements);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        // 'city 1' default: Mississauga
        setLat1(43.5890452);  //latitude: degrees north of the equator
        setLon1(-79.6441198); //longitude: degrees east of the prime meridian

        // 'city 2' default: Toronto
        setLat2(43.653226);
        setLon2(-79.3831843);

        apicall1 = (url + apikey + "/" + Double.toString(lat1) + "," + Double.toString(lon1)); //debug, city 1.
        apicall2 = (url + apikey + "/" + Double.toString(lat2) + "," + Double.toString(lon2)); //debug, city 2.

        new WeatherAsync().execute(apicall1, apicall2); //following lines up to sbar() call are to find data
                                                        //immediately upon launching app

        TextView currentTemp = (TextView)findViewById(R.id.tvNowCityA);
        String s = Double.toString(getTemp());
        String trim = s.substring(0, Math.min(s.length(), 5)); //second argument of substring() - change the 5 to
                                                               //however many characters from string s you want up to
        currentTemp.setText(trim + "°C");

        TextView currentTemp2 = (TextView)findViewById(R.id.tvNowCityB);
        String s2 = Double.toString(getTemp2());
        String trim2 = s2.substring(0, Math.min(s2.length(), 5));
        currentTemp2.setText(trim2 + "°C");


        sbar(); // initialize seekbar listener for skToday

    }

    public void sbar() {
        final SeekBar seekbar = (SeekBar)findViewById(R.id.skToday);
        final TextView tvTodayCityA = (TextView)findViewById(R.id.tvTodayCityA);
        final TextView tvTodayCityB = (TextView)findViewById(R.id.tvTodayCityB);
        final TextView tvTodayConditionsA = (TextView)findViewById(R.id.tvTodayConditionsA);
        final TextView tvTodayConditionsB = (TextView)findViewById(R.id.tvTodayConditionsB);

        final SeekBar seekbar2 = (SeekBar)findViewById(R.id.skTomorrow);
        final TextView tvTomorrowCityA = (TextView)findViewById(R.id.tvTomorrowCityA);
        final TextView tvTomorrowCityB = (TextView)findViewById(R.id.tvTomorrowCityB);
        final TextView tvTomorrowConditionsA = (TextView)findViewById(R.id.tvTomorrowConditionsA);
        final TextView tvTomorrowConditionsB = (TextView)findViewById(R.id.tvTomorrowConditionsB);

        final HourlyWeather hourly = new HourlyWeather();
        seekbar.setMax(17); // from 0 to 17, there are 18 beats for 18 hour coverage: 6am to midnight
        seekbar2.setMax(17);

        // tv.setText("" + seekbar.getProgress() + "/" + seekbar.getMax());

        seekbar.setOnSeekBarChangeListener( //today
                new SeekBar.OnSeekBarChangeListener() {
                    int progress_value;


                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                        for (int i = 0; i < 18; i++) {
                            if (progress == i) {
                                String s = Double.toString(hourly.getTemps1()[i]);
                                String trim = s.substring(0, Math.min(s.length(), 5));
                                String t = Double.toString(hourly.getAtemps1()[i]);
                                String trimt = t.substring(0, Math.min(t.length(), 5));

                                tvTodayCityA.setText(trim + "°C / " + trimt + "°C");
                                tvTodayConditionsA.setText(hourly.getTodayConditions1()[i]);

                                String s2 = Double.toString(hourly.getTemps2()[i]);
                                String trim2 = s2.substring(0, Math.min(s2.length(), 5));
                                String t2 = Double.toString(hourly.getAtemps2()[i]);
                                String trimt2 = t2.substring(0, Math.min(t2.length(), 5));

                                tvTodayCityB.setText(trim2 + "°C / " + trimt2 + "°C");
                                tvTodayConditionsB.setText(hourly.getTodayConditions2()[i]);

                                DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"); // you define this format
                                Date today = Calendar.getInstance().getTime();
                                String dateString = df.format(today);

                                int hour = Integer.parseInt(dateString.substring(11, 13)) + i;

                                String todayTime = Integer.toString(hour) + "00 hours";

                                hourly.setTvTimeToday(todayTime);
                                TextView tvTodayTime = (TextView)findViewById(R.id.tvTodayTime);
                                tvTodayTime.setText(hourly.getTvTimeToday());
                            }
                        }

                        //tv.setText("" + progress + "/" + seekbar.getMax());
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                     //   tv.setText("" + progress_value + "/" + seekbar.getMax());

                    }
        }
        );

        seekbar2.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int progress_value;


                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        for (int i = 0; i < 18; i++) {
                            if (progress == i) {
                                String s = Double.toString(hourly.getTempsTom1()[i]);
                                String trim = s.substring(0, Math.min(s.length(), 5));
                                String t = Double.toString(hourly.getAtempsTom1()[i]);
                                String trimt = t.substring(0, Math.min(t.length(), 5));

                                tvTomorrowCityA.setText(trim + "°C / " + trimt + "°C");
                                tvTomorrowConditionsA.setText(hourly.getTomorrowConditions1()[i]);

                                String s2 = Double.toString(hourly.getTempsTom2()[i]);
                                String trim2 = s2.substring(0, Math.min(s2.length(), 5));
                                String t2 = Double.toString(hourly.getAtempsTom2()[i]);
                                String trimt2 = t2.substring(0, Math.min(t2.length(), 5));

                                tvTomorrowCityB.setText(trim2 + "°C / " + trimt2 + "°C");
                                tvTomorrowConditionsB.setText(hourly.getTomorrowConditions2()[i]);

                                DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"); // you define this format
                                Date today = Calendar.getInstance().getTime();
                                String dateString = df.format(today);

                                int hour = Integer.parseInt(dateString.substring(11, 13)) + i;

                                String tomorrowTime = Integer.toString(hour) + "00 hours tomorrow";

                                hourly.setTvTimeTomorrow(tomorrowTime);
                                TextView tvTomorrowTime = (TextView)findViewById(R.id.tvTomorrowTime);

                                tvTomorrowTime.setText(hourly.getTvTimeTomorrow());
                            }
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        //   tv.setText("" + progress_value + "/" + seekbar.getMax());

                    }
                }
        );
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();


        if (item.equals("Mississauga")) {
            setLat1(43.5890452);  //latitude: degrees north of the equator
            setLon1(-79.6441198); //longitude: degrees east of the prime meridian
            apicall1 = (url + apikey + "/" + Double.toString(lat1) + "," + Double.toString(lon1));

            new WeatherAsync().execute(apicall1, apicall2);

            TextView currentTemp = (TextView)findViewById(R.id.tvNowCityA);
            String s = Double.toString(getTemp());
            String trim = s.substring(0, Math.min(s.length(), 5));
            String t = Double.toString(getApparentTemp());
            String trimt = t.substring(0, Math.min(t.length(), 5));
            currentTemp.setText(trim + "°C / " + trimt + "°C");

            TextView currentConditions = (TextView)findViewById(R.id.tvNowConditionsA);
            currentConditions.setText(conditions1);

            TextView currentTemp2 = (TextView)findViewById(R.id.tvNowCityB);
            String s2 = Double.toString(getTemp2());
            String trim2 = s2.substring(0, Math.min(s2.length(), 5));
            String t2 = Double.toString(getApparentTemp2());
            String trimt2 = t2.substring(0, Math.min(t2.length(), 5));
            currentTemp2.setText(trim2 + "°C / " + trimt2 + "°C");

            TextView currentConditions2 = (TextView)findViewById(R.id.tvNowConditionsB);
            currentConditions2.setText(conditions2);

// Create an instance of SimpleDateFormat used for formatting
// the string representation of date (month/day/year)
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"); // you define this format

// Get the date today using Calendar object.
            Date today = Calendar.getInstance().getTime();
// Using DateFormat format method we can create a string
// representation of a date with the defined format.
            String dateString = df.format(today);

            Log.d("Weatherer", "[DATE] Date right now: " + dateString); //debug
            TextView tvNowDate = (TextView)findViewById(R.id.tvNowDate);
            tvNowDate.setText(dateString);





        }

        else if (item.equals("Brampton")) {
            setLat1(43.685271);
            setLon1(-79.759924); //negative values mean west of the prime meridian
            apicall1 = (url + apikey + "/" + Double.toString(lat1) + "," + Double.toString(lon1));

            new WeatherAsync().execute(apicall1, apicall2);

            TextView currentTemp = (TextView)findViewById(R.id.tvNowCityA);
            String s = Double.toString(getTemp());
            String trim = s.substring(0, Math.min(s.length(), 5));
            String t = Double.toString(getApparentTemp());
            String trimt = t.substring(0, Math.min(t.length(), 5));
            currentTemp.setText(trim + "°C / " + trimt + "°C");

            TextView currentConditions = (TextView)findViewById(R.id.tvNowConditionsA);
            currentConditions.setText(conditions1);

            TextView currentTemp2 = (TextView)findViewById(R.id.tvNowCityB);
            String s2 = Double.toString(getTemp2());
            String trim2 = s2.substring(0, Math.min(s2.length(), 5));
            String t2 = Double.toString(getApparentTemp2());
            String trimt2 = t2.substring(0, Math.min(t2.length(), 5));
            currentTemp2.setText(trim2 + "°C / " + trimt2 + "°C");

            TextView currentConditions2 = (TextView)findViewById(R.id.tvNowConditionsB);
            currentConditions2.setText(conditions2);

// Create an instance of SimpleDateFormat used for formatting
// the string representation of date (month/day/year)
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"); // you define this format

// Get the date today using Calendar object.
            Date today = Calendar.getInstance().getTime();
// Using DateFormat format method we can create a string
// representation of a date with the defined format.
            String dateString = df.format(today);

            Log.d("Weatherer", "[DATE] Date right now: " + dateString); //debug
            TextView tvNowDate = (TextView)findViewById(R.id.tvNowDate);
            tvNowDate.setText(dateString);

            /*
            HourlyWeather hourly = new HourlyWeather();
            TextView tvTodayTime = (TextView)findViewById(R.id.tvTodayTime);
            TextView tvTomorrowTime = (TextView)findViewById(R.id.tvTomorrowTime);

            tvTodayTime.setText(hourly.getTvTimeToday());
            tvTomorrowTime.setText(hourly.getTvTimeTomorrow());
            */

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

    }

    public double getLat1() {
        return lat1;
    }

    public void setLat1(double lat1) {
        this.lat1 = lat1;
    }

    public double getLon1() {
        return lon1;
    }

    public void setLon1(double lon1) {
        this.lon1 = lon1;
    }

    public double getLat2() {
        return lat2;
    }

    public void setLat2(double lat2) {
        this.lat2 = lat2;
    }

    public double getLon2() {
        return lon2;
    }

    public void setLon2(double lon2) {
        this.lon2 = lon2;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public double getTemp() {
        return temp;
    }

    public void setApparentTemp(double apparentTemp) {
        this.apparentTemp = apparentTemp;
    }

    public double getApparentTemp() {
        return apparentTemp;
    }

    public void setTemp2(double temp2) {
        this.temp2 = temp2;
    }

    public double getTemp2() {
        return temp2;
    }

    public void setApparentTemp2(double apparentTemp2) {
        this.apparentTemp2 = apparentTemp2;
    }

    public double getApparentTemp2() {
        return apparentTemp2;
    }

    public void setConditions1(String c) {
        this.conditions1 = c;
    }

    public String getConditions1() {
        return conditions1;
    }

    public void setConditions2(String c) {
        this.conditions2 = c;
    }

    public String getConditions2() {
        return conditions2;
    }

    public class WeatherAsync extends AsyncTask<String, Void, Boolean>{ //parameter, progress, result

        @Override
        protected Boolean doInBackground(String[] params) { // params here is the parameter passed to execute()
                                                             // at the onCreate() method's bottom, the api call url

            try {
                HttpClient client = new DefaultHttpClient();
                HttpClient client2 = new DefaultHttpClient();
                Log.w("Weatherer", "Parameter passed to execute(): " + params[0]);
                URI site = new URI(apicall1);
                URI site2 = new URI(apicall2);
                HttpGet request = new HttpGet();
                HttpGet request2 = new HttpGet();
                request.setURI(site);
                request2.setURI(site2);
                HttpResponse response = client.execute(request); //might throw an exception. This holds the
                                                              //result of what you get when posting params[0].

                HttpResponse response2 = client2.execute(request2);

                int status = response.getStatusLine().getStatusCode();
                int status2 = response2.getStatusLine().getStatusCode();

                Log.d("Weatherer", "[CITY A] Obtained status: " + Integer.toString(status));
                Log.d("Weatherer", "[CITY B] Obtained status: " + Integer.toString(status2));

                if ( (status == 200) && (status2 == 200)) { // if we got something successfully
                    HttpEntity entity = response.getEntity();
                    HttpEntity entity2 = response2.getEntity();
                    String data = EntityUtils.toString(entity); //data variable is to be parsed
                    String data2 = EntityUtils.toString(entity2);
                    Log.d("Weatherer", "[CITY A] ##### GOT STRING #####\n" + data);
                    Log.d("Weatherer", "[CITY B] ##### GOT STRING #####\n" + data2);

                    JSONObject jObj = new JSONObject(data);     //jObj must refer to the jsonobject "hourly"
                                                                //to get hourly data.

                    JSONObject jObj2 = new JSONObject(data2);

                    JSONObject jCurrently = jObj.getJSONObject("currently"); // there is a "currently" array in api call that holds
                                                                 // the hourly info we want, within the "hourly" object

                    JSONObject jCurrently2 = jObj2.getJSONObject("currently");

                    setTemp(toCelsius(jCurrently.getDouble("temperature")));
                    setApparentTemp(toCelsius(jCurrently.getDouble("apparentTemperature")));
                    setConditions1(jCurrently.getString("summary"));
                    setTemp2(toCelsius(jCurrently2.getDouble("temperature")));
                    setApparentTemp2(toCelsius(jCurrently2.getDouble("apparentTemperature")));
                    setConditions2(jCurrently2.getString("summary"));


                    HourlyWeather hourly = new HourlyWeather();

                    jObj = jObj.getJSONObject("hourly");
                    jObj2 = jObj2.getJSONObject("hourly");
                    JSONArray hourlyData = jObj.getJSONArray("data");   // fill static HourlyWeather arrays with
                    JSONArray hourlyData2 = jObj2.getJSONArray("data"); // hourly data
                    for (int i = 0; i < 18; i++) {
                        JSONObject j = hourlyData.getJSONObject(i);
                        JSONObject j2 = hourlyData2.getJSONObject(i);
                        hourly.setTemps1(toCelsius(j.getDouble("temperature")), i);
                        hourly.setAtemps1(toCelsius(j.getDouble("apparentTemperature")), i);
                        hourly.setTodayConditions1(j.getString("summary"), i);
                        hourly.setTemps2(toCelsius(j2.getDouble("temperature")), i);
                        hourly.setAtemps2(toCelsius(j2.getDouble("apparentTemperature")), i);
                        hourly.setTodayConditions2(j2.getString("summary"), i);

                        JSONObject jTom = hourlyData.getJSONObject(i + 24); // get the object 24 hours ahead
                        JSONObject jTom2 = hourlyData2.getJSONObject(i + 24);
                        hourly.setTempsTom1(toCelsius(jTom.getDouble("temperature")), i);
                        hourly.setAtempsTom1(toCelsius(jTom.getDouble("apparentTemperature")), i);
                        hourly.setTomorrowConditions1(jTom.getString("summary"), i);
                        hourly.setTempsTom2(toCelsius(jTom2.getDouble("temperature")), i);
                        hourly.setAtempsTom2(toCelsius(jTom2.getDouble("apparentTemperature")), i);
                        hourly.setTomorrowConditions2(jTom2.getString("summary"), i);


                    }



                    return true;


                }




            }

            catch (Exception ex) {
                ex.printStackTrace();

                return false;
            }
            Log.d("Weatherer", "Status code was not 200"); //the way to print debug messages to Android Monitor/Logcat
            return false; //if we get to this point, an exception is not called...
        }

        protected void onPostExecute(Boolean success) {
            if (success) {
                //Toast toast = Toast.makeText(MainActivity.this, "Success!", Toast.LENGTH_SHORT);
                //toast.show();
            }

            else {
                Toast toast = Toast.makeText(MainActivity.this, "Something went wrong!", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    public double toCelsius(double temp) {
        return ( (temp - 32.0) * (5.0 / 9.0) );
    }

}
