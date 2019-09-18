package nitolmotors.sales.com.neverendingservice;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import nitolmotors.sales.com.neverendingservice.CallLogService.ServiceCallLog;
import nitolmotors.sales.com.neverendingservice.LocationService.ServiceLocation;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    Intent mServiceIntent_location;
    private ServiceLocation mMyService_location;

    Intent mServiceIntent_calllog;
    private ServiceCallLog mMyService_calllog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start the location Service
        mMyService_location = new ServiceLocation();
        mServiceIntent_location = new Intent(this, mMyService_location.getClass());
        if (!isMyLocationServiceRunning(mMyService_location.getClass())) {
            startService(mServiceIntent_location);
        }

        // Start the Call log service
        mMyService_calllog = new ServiceCallLog();
        mServiceIntent_calllog = new Intent(this, mMyService_calllog.getClass());
        if (!isMyCallLogServiceRunning(mMyService_calllog.getClass())) {
            startService(mServiceIntent_calllog);
        }
    }

    private boolean isMyLocationServiceRunning(Class<?> serviceClass) {
        Log.i(TAG, "isMyServiceRunning: Location");
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("Location Service status", "Running");
                return true;
            }
        }
        Log.i ("Location Service status", "Not running");
        return false;
    }

    private boolean isMyCallLogServiceRunning(Class<?> serviceClass) {
        Log.i(TAG, "isMyServiceRunning: calllog");
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("Calllog Service status", "Running");
                return true;
            }
        }
        Log.i ("Calllog Service status", "Not running");
        return false;
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy: MainActivity");
        stopService(mServiceIntent_location);
        stopService(mServiceIntent_calllog);
        super.onDestroy();
    }

}