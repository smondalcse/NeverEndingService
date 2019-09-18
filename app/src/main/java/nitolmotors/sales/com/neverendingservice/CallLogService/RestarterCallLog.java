package nitolmotors.sales.com.neverendingservice.CallLogService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import nitolmotors.sales.com.neverendingservice.LocationService.ServiceLocation;

public class RestarterCallLog extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Broadcast Listened", "Service CallLog tried to stop");
        Toast.makeText(context, "Service CallLog restarted", Toast.LENGTH_SHORT).show();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, ServiceCallLog.class));
        } else {
            context.startService(new Intent(context, ServiceCallLog.class));
        }
    }
}