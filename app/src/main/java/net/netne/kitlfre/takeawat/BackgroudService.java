package net.netne.kitlfre.takeawat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Bruno Martins
 */

public class BackgroudService extends Service {
    testes alarm = new testes();
    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        alarm.SetAlarm(this);
        return START_STICKY;
    }

    @Override
    public void onStart(Intent intent, int startId)
    {
        alarm.SetAlarm(this);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}