package jrebanal.weatherer;

// http://stackoverflow.com/questions/9605913/how-to-parse-json-in-android
// how to parse json in android

// forecast.io api key: be5c0f9b08e52e3f2da12e30ce8d3242

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
    private String url = "https://api.forecast.io/forecast/"; // ... APIKEY/LATITUDE,LONGITUDE"
    private String apikey = "be5c0f9b08e52e3f2da12e30ce8d3242";
    private String apicall; //currently only relating to city 1 and mississauga.
    private double temp, apparentTemp;

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

        apicall = (url + apikey + "/" + Double.toString(lat1) + "," + Double.toString(lon1)); //debug, city 1.

        // new WeatherAsync().execute(url + successapikey + Double.toString(lat1) + Double.toString(lon1)); // city 1

        //new WeatherAsync().execute(url + apikey + Double.toString(lat2) + Double.toString(lon2)); // city 2

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();
        Log.d("Weatherer", "apicall: " + apicall); //debug. correct


        if (item.equals("Mississauga")) {
            //Toast.makeText(parent.getContext(), "mississauga!", Toast.LENGTH_LONG).show(); //debug
            setLat1(43.5890452);  //latitude: degrees north of the equator
            setLon1(-79.6441198); //longitude: degrees east of the prime meridian
            new WeatherAsync().execute(apicall); // city 1

            TextView currentTemp = (TextView)findViewById(R.id.tvNowCityA);
            currentTemp.setText(Double.toString(getTemp()));

        }

        else if (item.equals("Brampton")) {
            //Toast.makeText(parent.getContext(), "brampton!", Toast.LENGTH_LONG).show(); //debug
            setLat1(43.685271);
            setLon1(-79.759924); //negative values mean west of the prime meridian
            new WeatherAsync().execute(apicall); // city 1

            TextView currentTemp = (TextView)findViewById(R.id.tvNowCityA);
            currentTemp.setText(Double.toString(getTemp()));
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



    public class WeatherAsync extends AsyncTask<String, Void, Boolean>{ //parameter, progress, result

        @Override
        protected Boolean doInBackground(String[] params) { // params here is the parameter passed to execute()
                                                             // at the onCreate() method's bottom, the api call url

            try {
                HttpClient client = new DefaultHttpClient();
                Log.w("Weatherer", "Parameter passed to execute(): " + params[0]);
                URI site = new URI(apicall);
                HttpGet request = new HttpGet();
                request.setURI(site);
                HttpResponse response = client.execute(request); //might throw an exception. This holds the
                                                              //result of what you get when posting params[0].

                //dec 31 (1) - the HttpResponse line no longer throws an exception,
                //but it gets status code 404: page not found. The apicall passed to it is the real deal.

                //dec 31 (2) - this Http stuff is messed up. very inconsistent when testing with other websites
                //best to just learn another method/library to request, receive, and parse data.
                //TODO replace lines above for http client, post, response
                //TODO and re-do status check line, and everything up to the json stuff in the if statement below.



                //dec 31 - i think the HttpResponse line is throwing an exception. is there another library
                //that can hold the json data and handle requests/responses?

                //printStackTrace() in exception shows that it was because the app did not ask for internet permissions

                //after fixing this, an exception can be avoided but the status code is not 200



                int status = response.getStatusLine().getStatusCode();

                Log.d("Weatherer", "Obtained status: " + Integer.toString(status));

                if (status == 200) { // if we got something successfully
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity); //data variable is to be parsed
                    Log.d("Weatherer", "##### GOT STRING #####\n" + data);

                    JSONObject jObj = new JSONObject(data);     //jObj must refer to the jsonobject "hourly"
                                                                //to get hourly data.

                    JSONObject jCurrently = jObj.getJSONObject("currently"); // there is a "currently" array in api call that holds
                                                                 // the hourly info we want, within the "hourly" object

                    setTemp(jCurrently.getDouble("temperature"));
                    setApparentTemp(jCurrently.getDouble("apparentTemperature"));
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
                Toast toast = Toast.makeText(MainActivity.this, "Success!", Toast.LENGTH_SHORT);
                toast.show();
            }

            else {
                Toast toast = Toast.makeText(MainActivity.this, "Failure!", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

}
