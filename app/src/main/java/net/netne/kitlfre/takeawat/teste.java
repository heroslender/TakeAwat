package net.netne.kitlfre.takeawat;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by Bruno Martins
 */

public class teste extends AppCompatActivity {
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teste);
    }

    public void click2(View view){
        startService(new Intent(getBaseContext(), BackgroudService.class));

        //raise_notification();
    }

    protected void raise_notification() {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nm = (NotificationManager) getSystemService(ns);

        Intent notificationIntent = new Intent(Intent.ACTION_VIEW );
        notificationIntent.setDataAndType(
                Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/Download/TakeAwat.apk"),
                "application/vnd.android.package-archive");
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.icon);
        builder.setTicker("TakeAwat update");
        builder.setContentTitle("TakeAwat");
        builder.setContentText("Update available!\nSelect to install");
        builder.setContentIntent(contentIntent);
        builder.setWhen(System.currentTimeMillis());
        builder.setAutoCancel(true);
        builder.setOngoing(false);

        nm.notify(0xBEEE, builder.build());
    }

    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(teste.this);
        dialogBuilder.setMessage("Deseja sair?");
        dialogBuilder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
                System.exit(0);
            }
        });
        dialogBuilder.setNegativeButton("Não", null);
        dialogBuilder.show();
    }
}