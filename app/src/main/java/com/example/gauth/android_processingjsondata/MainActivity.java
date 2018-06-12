package com.example.gauth.android_processingjsondata;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

// the API we are calling is from the following website  https://openweathermap.org/current
public class MainActivity extends AppCompatActivity {
    EditText city;
    EditText country;
    TextView main;
    TextView description;
    TextView temperature;
    TextView pressure;
    TextView humidity;
    TextView tempMin;
    TextView tempMax;
    public void checkWeather(View view)
{
    Log.i("City is",city.getText().toString());
    Log.i("Country is",country.getText().toString());
    DownloadTask task=new DownloadTask();
    try {//http://samples.openweathermap.org/data/2.5/weather?q=London,uk&appid=b6907d289e10d714a6e88b30761fae22
        //363f7eae85e077fd34db9231f77abcbc this is our appkey which we got from website
     task.execute("http://api.openweathermap.org/data/2.5/weather?q="+city.getText().toString()+","+country.getText().toString()+"&appid=363f7eae85e077fd34db9231f77abcbc").get();
} catch (InterruptedException e) {

    e.printStackTrace(); //prints all information of the error
} catch (ExecutionException e) {

    e.printStackTrace();
}

}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       //enter data
        city=(EditText)findViewById(R.id.city);
        country=(EditText)findViewById(R.id.country);
      //display data
        main=(TextView)findViewById(R.id.main);
        description=(TextView)findViewById(R.id.description);
        temperature=(TextView)findViewById(R.id.temperature);
        pressure=(TextView)findViewById(R.id.pressure);
        humidity=(TextView)findViewById(R.id.humidity);
        tempMin=(TextView)findViewById(R.id.tempMin);
        tempMax=(TextView)findViewById(R.id.tempMax);
    }
public class DownloadTask extends AsyncTask<String,Void,String>
{
    @Override
    protected String doInBackground(String... urls) {

        String result = "";
        URL url;//This a special type of string which should be in the specific URL format and type URL
        HttpURLConnection urlConnection = null; //This is bit like a browser
        try {
            url = new URL(urls[0]); //The try catch is here because there can be an exception if url is not correct
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = urlConnection.getInputStream(); //this is for holding the incomming data
            InputStreamReader reader = new InputStreamReader(in); //this is for reading data
            int data = reader.read(); //this keeps track of current data we are currently on
            while (data != -1)  //the dat will be read and once done it becomes -1
            {

                char current = (char) data; //this will convert the value data is pointing to a charectar
                result += current;
                data = reader.read();
            }
           Log.i("Complete Data",result);
            return result; //result back to task to be printed out
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed";
        }
    }
    //Rather than passing the result back to main thread and main thread work with it we can use the below method
    //to do the following.
    //The doInBackground method cannot actually work with UI hence we were passing it to main thread.
    //But the below onPostExecute method can work with UI
    @Override
    protected  void onPostExecute(String result)
    {
        super.onPostExecute(result);
        try{
            JSONObject jsonObject=new JSONObject(result);//CONVERTS STRING TO JSON DATA
            //to get the weather part from the JSON Object by giving the key weather to obtain values
            String weatherInfo=jsonObject.getString("weather");
            JSONArray arrWeather=new JSONArray(weatherInfo);

            for(int i=0;i<arrWeather.length();i++)  //To get JSON Object subparts by looping through them
            {
             JSONObject jsonPart=arrWeather.getJSONObject(i);
                //to get the weather part from the JSON Object by giving the key main and description to obtain values
             Log.i("main",jsonPart.getString("main"));  //for testing purpose
              main.setText(jsonPart.getString("main"));
              description.setText(jsonPart.getString("description"));
            }
                String baseInfo=jsonObject.getString("main");
                JSONObject baseJason=new JSONObject(baseInfo);
                Log.i("Temperature",baseJason.getString("temp"));  //for testing purpose
            temperature.setText(baseJason.getString("temp"));
            pressure.setText(baseJason.getString("pressure"));
            humidity.setText(baseJason.getString("humidity"));
            tempMin.setText(baseJason.getString("temp_min"));
            tempMax.setText(baseJason.getString("temp_max"));




        }  //TRY CATCH IS ESSENTIAL HERE SINCE THE STRING GIVEN INPUT MIGHT BE MALFORMED
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}
}
