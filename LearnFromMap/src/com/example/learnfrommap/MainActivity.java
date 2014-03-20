package com.example.learnfrommap;

import java.io.IOException;
import java.security.Provider;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends Activity {

	GoogleMap map;
	MapFragment mapFragment;
	Button check, start;
	TextView question;
	Location location;
	String[] questions, answers;
	static int number = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mapFragment = (MapFragment) getFragmentManager().findFragmentById(
				R.id.map);
		check = (Button) findViewById(R.id.check_button);
		start = (Button) findViewById(R.id.start_button);
		question = (TextView) findViewById(R.id.question);
		map = mapFragment.getMap();
		location = new Location("");
		CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(
				22.006891, 78.618164));
		CameraUpdate zoom = CameraUpdateFactory.zoomTo(4);
		questions = new String[5];
		questions[0] = "Ahmedabad";
		questions[1] = "Rajkot";
		questions[2] = "Which is the capital city of Gujarat";
		answers = new String[5];
		answers[0] = "Ahmedabad";
		answers[1] = "Rajkot";
		answers[2] = "Gandhinagar";
		map.moveCamera(center);
		map.animateCamera(zoom);
		map.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng arg0) {
				// TODO Auto-generated method stub
				map.clear();
				map.addMarker(new MarkerOptions().position(arg0)
						.draggable(true));
				location.setLatitude(arg0.latitude);
				location.setLongitude(arg0.longitude);
			}
		});
		start.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				check.setVisibility(View.VISIBLE);
				start.setVisibility(View.GONE);
				question.setVisibility(View.VISIBLE);
				question.setText(questions[number]);
			}
		});
		check.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (checkAnswer() == true) {
					number++;

				}

				question.setText(questions[number]);
			}
		});
	}

	private boolean checkAnswer() {
		GetAddressTask addressTask = new GetAddressTask(MainActivity.this);
		try {
			String ans = addressTask.execute(location).get();
			System.out.println(ans + "...............");
			if (number < 3) {
				if (ans.equals(answers[number])) {
					Toast.makeText(getApplicationContext(), "correct answer",
							Toast.LENGTH_SHORT).show();
					return true;
				} else {
					if (ans.equals("IO Exception trying to get address"))
						Toast.makeText(getApplicationContext(),
								"no internet connection", Toast.LENGTH_SHORT)
								.show();
					else
						Toast.makeText(getApplicationContext(), "wrong answer",
								Toast.LENGTH_SHORT).show();

					return false;
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	private class GetAddressTask extends AsyncTask<Location, Void, String> {
		Context mContext;

		public GetAddressTask(Context context) {
			super();
			mContext = context;
		}

		@Override
		protected String doInBackground(Location... params) {
			Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
			// Get the current location from the input parameter list
			Location loc = params[0];
			// Create a list to contain the result address
			List<Address> addresses = null;
			try {

				addresses = geocoder.getFromLocation(loc.getLatitude(),
						loc.getLongitude(), 1);
			} catch (IOException e1) {
				Log.e("LocationSampleActivity",
						"IO Exception in getFromLocation()");
				e1.printStackTrace();
				return ("IO Exception trying to get address");
			} catch (IllegalArgumentException e2) {
				// Error message to post in the log
				String errorString = "Illegal arguments "
						+ Double.toString(loc.getLatitude()) + " , "
						+ Double.toString(loc.getLongitude())
						+ " passed to address service";
				Log.e("LocationSampleActivity", errorString);
				e2.printStackTrace();
				return errorString;
			}
			// If the reverse geocode returned an address
			if (addresses != null && addresses.size() > 0) {
				// Get the first address
				Address address = addresses.get(0);
				if (address.getLocality() != null)
					return address.getLocality();
				return "No address found";
			} else {
				return "No address found";
			}
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			// question.setText(result);
		}
	}
}
