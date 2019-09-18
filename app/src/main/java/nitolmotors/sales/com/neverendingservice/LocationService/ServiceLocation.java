package nitolmotors.sales.com.neverendingservice.LocationService;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import nitolmotors.sales.com.neverendingservice.R;


public class ServiceLocation extends Service {
    private static final String TAG = "ServiceCallLog";

    public int counter = 0;

    private FusedLocationProviderClient mFusedLocationClient;
    Location location;

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate: ");
        super.onCreate();


        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        Log.i(TAG, "startMyOwnForeground: ");
        String NOTIFICATION_CHANNEL_ID = getString(R.string.notification_channel_location_ID);//"example.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: ");
        super.onStartCommand(intent, flags, startId);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

        startTimer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: ");

        super.onDestroy();
        stoptimertask();

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, RestarterLocation.class);
        this.sendBroadcast(broadcastIntent);
    }

    private Timer timer;
    private TimerTask timerTask;

    public void startTimer() {
        Log.i(TAG, "startTimer: ");

        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                Log.i("ServiceLocation: Count", "=========  " + (counter++));
                sendAndRequestResponse();
            }
        };
          timer.schedule(timerTask, 10000, 10000); //
       // timer.schedule(timerTask, 10000, 1000); //
    }

    public void stoptimertask() {
        Log.i(TAG, "stoptimertask: ");
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind: ");
        return null;
    }

    private void sendAndRequestResponse() {
        Log.d(TAG, "sendAndRequestResponse: Count"  + (counter++));
        getLastLocation();
        
        /*
        String url = "";
        if(location != null) {
            Log.i(TAG, "getLatitude: " + location.getLatitude());
            Log.i(TAG, "getLongitude: " + location.getLongitude());
            url = "http://209.222.99.106/~sonu/androidservice/ncalllog_test/saveData.php?data=" + counter
                    + "&lat=" + location.getLatitude() + "&lng=" + location.getLongitude();
        } else {
            url = "http://209.222.99.106/~sonu/androidservice/ncalllog_test/saveData.php?data=" + counter;
        }
        Log.d(TAG, "login_url: " + url);

        RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                Log.d(TAG, "onResponse:========>>>>>>> " + response.toString());
                //   Toast.makeText(getApplicationContext(), "Data Saved", Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
        mRequestQueue.add(jsonObjReq);
        jsonObjReq.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        */
    }

    private void getLastLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "getLastLocation: Permission not granted");
            return;
        }
        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Log.i(TAG, "onComplete: ");
                    location = task.getResult();
                } else {
                    Log.i(TAG, "onComplete: Failed");
                }
            }
        });

    }

}