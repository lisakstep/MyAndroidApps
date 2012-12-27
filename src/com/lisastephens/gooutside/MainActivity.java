package com.lisastephens.gooutside;

import java.net.URI;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

	String myPrivateWundergroundID = "5eec8e9bd4306768";
	LocationManager locationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getLatLong();


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void updateWeather (String weatherInformation) throws Exception {

		JSONObject weatherJson = new JSONObject(weatherInformation); 
		JSONObject currentData = (JSONObject) weatherJson.get("current_observation");
		String currentTemp = currentData.get("temp_f").toString();
		String feelsLikeTemp = currentData.get("feelslike_f").toString();

		JSONObject displayLocation = (JSONObject) currentData.get("display_location");
		String locationName = displayLocation.get("full").toString();
		
		System.out.println("I think we are in" + locationName);

		TextView rawTemp = (TextView) findViewById(R.id.realTemp);
		rawTemp.setText(currentTemp);

		TextView processedTemp = (TextView) findViewById(R.id.feelsLike);
		processedTemp.setText(feelsLikeTemp);
	}

	public String getWeatherApiData (double latitude, double longitude) throws Exception {

		String apiUri = ("http://api.wunderground.com/api/" + myPrivateWundergroundID + "/conditions/q/" + latitude + "," + longitude + ".json");

		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet();
		request.setURI(new URI(apiUri));

		String content = client.execute(request, new BasicResponseHandler());

		return content;




	}

	public void getLatLong () {

		// Acquire a reference to the system Location Manager
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				// Called when a new location is found by the network location provider.
				String weatherString;
				try {
					weatherString = getWeatherApiData (location.getLatitude(),location.getLongitude());
					updateWeather (weatherString);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					// Remove the listener you previously added
					locationManager.removeUpdates(this);
				}
				
				
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {}

			public void onProviderEnabled(String provider) {}

			public void onProviderDisabled(String provider) {}
		};

		// Register the listener with the Location Manager to receive location updates
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);


	}

}
