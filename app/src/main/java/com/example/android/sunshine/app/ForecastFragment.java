package com.example.android.sunshine.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by HyosokaPoipo on 4/12/2016.
 */
public class ForecastFragment extends Fragment {

    //Constructor
    public ForecastFragment()
    {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        //ini buat ngeload menu forecastfragment.xml yang kita buat sebelumnya
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionMenu itu buat ngehandle event2 menu
        setHasOptionsMenu(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            /**
             * disini kode buat action yang akan sunshine lakukan
             * ketika menu Refresh di pencet...hehehe...:D
             */
            FetchWeatherTask weatherTask = new FetchWeatherTask();
            weatherTask.execute();
                return true;
            }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

          // Create some dummy data for the ListView.  Here's a sample weekly forecast
            String[] data = {
                    "Mon 6/23 - Sunny - 31/17",
                    "Tue 6/24 - Foggy - 21/8",
                    "Wed 6/25 - Cloudy - 22/17",
                    "Thurs 6/26 - Rainy - 18/11",
                    "Fri 6/27 - Foggy - 21/10",
                    "Sat 6/28 - TRAPPED IN WEATHERSTATION - 23/18",
                    "Sun 6/29 - Sunny - 20/7"
            };
            List<String> weekForecast = new ArrayList<String>(Arrays.asList(data));

            ArrayAdapter<String> mForecastAdapter =
                    new ArrayAdapter<String>(
                            getActivity(), // The current context (this activity)
                            R.layout.list_item_forecast, // The name of the layout ID.
                            R.id.list_item_forecast_textview, // The ID of the textview to populate.
                            weekForecast);

            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
            listView.setAdapter(mForecastAdapter);


        return rootView;
    }



    public class FetchWeatherTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
              /*
            urlConnection dan reader nnti mw dipake dibagian finally, jadi deklarasinya
            diluar try-catch
             */
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // String untuk menampung respon yang berupa json format
            String forecastJsonStr = null;

            try {
                // Konfigurasi URL untuk OpenWeatherMap query
                // Parameter2nya dapat dilihat di halaman OWM's forecast :
                // http://openweathermap.org/API#forecast
                String baseUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7";
                String apiKey = "&APPID=" + BuildConfig.OPEN_WEATHER_MAP_API_KEY;
                URL url = new URL(baseUrl.concat(apiKey));

                // Membuat request ke OpenWeatherMap, kemudian membuka koneksi baru
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Nangkep response dari openweatherMap terus disiapin untuk jadi variabel string
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // klu g' dapat apa2, return null
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line; //ini buat nyimpen string pas looping
                while ((line = reader.readLine()) != null) {

                    //nambain perline
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    //klu buffernya kosong, berarti g' ada datanya kaan... :v
                    return null;
                }
                //Masukin response akhir yang udah berupa string ke forecastJsonStr
                forecastJsonStr = buffer.toString();
                Log.i("Data OpenWeatherMap",forecastJsonStr);

            } catch (IOException e) {
                Log.e("ForecastFragment", "Error ", e);
                //Klu ada error pas mau ngambil data dari weatherMap, berarti kita g' dapat respon apa2
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("ForecastFragment", "Error closing stream", e);
                    }
                }
            }
            return null;
        }
    }

}





















