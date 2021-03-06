package com.example.marina.noobstacles;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {
    // LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;



    private List<Obstacle> obstacles ;
    private List<Obstacle> withObstacles1;


    private String URLLLL = "";
    private ArrayList<LatLng> markerPoints;
    private TextView textViewLatitude;
    private TextView textViewLongitude;
    private  RequestObstacles obstaclesss;
    private Button mProblemBtn;
    private Button mShowObstaclesButton;

    private Double lat;
    private Double lng;

//    protected LocationManager locationManager;
//    protected LocationListener locationListener;
//    protected Context context;
//    TextView txtLat;
//    String lat;
//    String provider;
//    protected String latitude, longitude;
//    protected boolean gps_enabled, network_enabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        obstaclesss = new RequestObstacles();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mMap = mapFragment.getMap();

        textViewLatitude = (TextView) findViewById(R.id.tv_latitude);
        textViewLongitude = (TextView) findViewById(R.id.tv_longitude);
        mShowObstaclesButton = (Button) findViewById(R.id.show_obstacles);
        mShowObstaclesButton.setOnClickListener(this);
        markerPoints = new ArrayList<LatLng>();

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(AppIndex.API).build();
        }


        mProblemBtn = (Button) findViewById(R.id.report_problem);
        mProblemBtn.setOnClickListener(this);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                Log.d("NONONONONO", obstaclesss.getNoObstacles().get(0).toString());
                // Already two locations
                if (markerPoints.size() > 0) {
                    markerPoints.clear();
                    mMap.clear();
                }

                // Adding new item to the ArrayList
                markerPoints.add(point);

                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(point);

                /**
                 * For the start location, the color of marker is GREEN and
                 * for the end location, the color of marker is RED.
                 */
                if (markerPoints.size() == 1) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                }/*else if(markerPoints.size()==2){
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    }*/


                // Add new marker to the Google Map Android API V2
                mMap.addMarker(options);
                Location userLocation = mMap.getMyLocation();
                LatLng myLocation = null;
                if (userLocation != null) {
                    myLocation = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
                }

                // Checks, whether start and end locations are captured
                if (markerPoints.size() >= 0) {
                    LatLng origin = myLocation;
                    LatLng dest = markerPoints.get(0);

                    // Getting URL to the Google Directions API
                    String url = getDirectionsUrl(origin, dest);

                    DownloadTask downloadTask = new DownloadTask();

                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url);
                }

            }
        });
//        txtLat = (TextView) findViewById(R.id.textview1);
//
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        enableMyLocation();
        Location userLocation = mMap.getMyLocation();
        LatLng myLocation = null;
        if (userLocation != null) {
            myLocation = new LatLng(userLocation.getLatitude(),
                    userLocation.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,
                    mMap.getMaxZoomLevel() - 5));
        }
    }

//    @Override
//    public void onLocationChanged(Location location) {
//        txtLat = (TextView) findViewById(R.id.textview1);
//        txtLat.setText("Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
//        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
//        mMap.addMarker(new MarkerOptions().position(currentLocation).title("You are here!"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
//    }
//
//    @Override
//    public void onProviderDisabled(String provider) {
//        Log.d("Latitude", "disable");
//    }
//
//    @Override
//    public void onProviderEnabled(String provider) {
//        Log.d("Latitude", "enable");
//    }
//
//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras) {
//        Log.d("Latitude", "status");
//    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("LOG", "Connected");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            textViewLatitude.setText(String.valueOf(mLastLocation.getLatitude()));
            lat = mLastLocation.getLatitude();

            textViewLongitude.setText(String.valueOf(mLastLocation.getLongitude()));
            lng = mLastLocation.getLongitude();

            LatLng point = new LatLng(lat, lng);
            markerPoints.add(point);
            LatLng currentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(currentLocation).title("You are here!"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));

            Log.d("Latitude", String.valueOf(mLastLocation.getLatitude()));
            Log.d("Longitude", String.valueOf(mLastLocation.getLongitude()));
        }
    }

    @Override
    protected void onStart() {
        Log.d("LOG", "Start");
        mGoogleApiClient.connect();
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.marina.noobstacles/http/host/path")
        );
        AppIndex.AppIndexApi.start(mGoogleApiClient, viewAction);
    }

    @Override
    protected void onStop() {
        Log.d("LOG", "Stop");
        mGoogleApiClient.disconnect();
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.marina.noobstacles/http/host/path")
        );
        AppIndex.AppIndexApi.end(mGoogleApiClient, viewAction);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        if (id == mProblemBtn.getId()) {
            Intent intent = new Intent(this, ReportForm.class);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            lat = mLastLocation.getLatitude();
            lng = mLastLocation.getLongitude();
            Log.d("cordination", lat + "    " + lng);
            intent.putExtra("Latitude", lat.toString());
            intent.putExtra("Longitude", lng.toString());
            startActivity(intent);
        } else if(id == mShowObstaclesButton.getId()) {
            showAllObstacles();
        }
    }

    private void enableMyLocation() {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED) {
                        // Access to the location has been granted to the app.
                                mMap.setMyLocationEnabled(true);
                    } else if (mMap != null) {
                        // Permission to access the location is missing.
                                // Show rationale and request permission
                                   }
    }
    @Override
    public boolean onMyLocationButtonClick() {
                return false;
    }


    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor+"&mode=walking";

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters ;


        return url;
    }
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        URLLLL = strUrl;
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exceptiondownloadingurl", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }



    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String>{

        public String getRightPath(List<Point> points, List<Obstacle> obstacles, List<Obstacle> with){
            String url = URLLLL;
            boolean isFirst = true;
            Log.d("EMPTY", points.size() + "");
            Log.d("EMPTY", obstacles.size() + "");
            Log.d("EMPTY", with.size() + "");
            if(points.size() != 0) {
                for (Point point : points) {
                    for (Obstacle o : obstacles) {
                        if (point.isProblem(o, point)) {
                            for (Obstacle ob : with)
                                if (point.isGoodPoint(ob, point)) {
                                    if (isFirst) {
                                        Log.d("LASTLOGAddgoodpoint", ob.toString());
                                        url += "&waypoints=optimize:true|" + ob.getLatitude() + "," + ob.getLongitude();
                                        isFirst = false;
                                    }
                                    url += "|" + ob.getLatitude() + "," + ob.getLongitude();
                                }
                        }
                    }

                }
            }
            return url;
        }
        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("LASTLOG", URLLLL);

                List<Point> notParsePath = parseJsonData(data);


                    obstacles = obstaclesss.getNoObstacles();
                    withObstacles1 = obstaclesss.getWthObstacles();
                    //String newURL = getRightPath(notParsePath, obstacles, withObstacles1);

                    data = downloadUrl(getRightPath(notParsePath, obstacles, withObstacles1));
                    Log.d("LASTLOG", URLLLL);

            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();
            parseJsonData(result);
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }

        private ArrayList<Point> parseJsonData(String gogleResponseStr) {
            ArrayList<Point> points = new ArrayList<>();
            Log.d("RADOEGEI","RADOEGEI");
            JSONObject json = null;
            try {
                json = new JSONObject(gogleResponseStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d("RADOEGEI","RADOEGEI");
            JSONArray routesJson = null;
            try {
//                stepsJson = json.getJSONArray("routes").getJSONArray(0).getJSONArray(2).getJSONArray(6);
                routesJson = json.getJSONArray("routes");
                Log.d("STEPS", routesJson.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < routesJson.length(); ++i) {
                JSONObject route = null;
                try {
                    route = routesJson.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("RADOEGEI","RADOEGEI");
                JSONArray legsJson = null;
                try {
                    legsJson = route.getJSONArray("legs");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for(int j = 0; j < legsJson.length(); ++j){
                    JSONObject leg = null;
                    try {
                         leg = legsJson.getJSONObject(j);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JSONArray stepJSON = null;
                    try {
                        stepJSON = leg.getJSONArray("steps");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for(int k = 0; k<stepJSON.length(); ++k){
                        JSONObject step = null;
                        try {
                            step = stepJSON.getJSONObject(k);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        JSONObject starLocation = null;
                        try {
                            starLocation = step.getJSONObject("start_location");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Point point = new Point();
                        double latitude = 0;
                         try {
                             latitude = Double.parseDouble(starLocation.getString("lat"));
                            point.setLat(latitude);
                         } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        double longitude = 0;
                        try {
                            longitude = Double.parseDouble(starLocation.getString("lng"));;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        point.setLng(longitude);
                        points.add(point);
                        Log.d("RADOEGEI", "RADOEGEI");
                    }
                }

            }
            Log.d("RADOEGEI", "RADOEGEI");
            return points;
        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> > {


        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(2);
                lineOptions.color(Color.RED);

            }

            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }

    public void showAllObstacles(){

        LatLng currentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        List<Obstacle> obstacles = obstaclesss.getAllGoodObstaclesInRange(currentLocation, new Double(0.01));
        for(Obstacle point : obstacles){
            currentLocation = new LatLng(point.getLatitude(),point.getLongitude());
            mMap.addMarker(new MarkerOptions().position(currentLocation).title("On this position we have any obstacles")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));


        }
        obstacles = obstaclesss.getAllBadObstaclesInRange(currentLocation, new Double(0.01));
        for(Obstacle point : obstacles){
            currentLocation = new LatLng(point.getLatitude(),point.getLongitude());
            mMap.addMarker(new MarkerOptions().position(currentLocation).title("On this position we have any obstacles")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
