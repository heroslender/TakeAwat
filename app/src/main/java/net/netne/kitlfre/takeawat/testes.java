package net.netne.kitlfre.takeawat;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bruno Martins
 */
public class testes extends BroadcastReceiver
{

    final long SEGUNDOS = 1000;
    final long MINUTOS = SEGUNDOS * 60;
    final long HORAS = MINUTOS * 60;
    final int id = 0xBEEE;
    public final String TEM_UPDATE = "TEM_UPDATE";
    public final String NAO_TEM_UPDATE = "NAO_TEM_UPDATE";
    public final String ERRO = "ERRO";
    public final String ERRO_DOWNLOAD = "ERRO_DOWNLOAD";
    public final String DOWNLOAD_COMPLETO = "DOWNLOAD_COMPLETO";
    UserLocalStore userLocalStore;
    Context context;

    @Override
    public void onReceive(final Context context, Intent intent)
    {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();
        this.context = context;
        userLocalStore = new UserLocalStore(context);
        if (userLocalStore.getCheckUpdate()) {
            new DownloadFileAsync(new GetVersaoCallback() {
                @Override
                public void done(List<String> versao) {
                    String ns = Context.NOTIFICATION_SERVICE;
                    NotificationManager nm = (NotificationManager) context.getSystemService(ns);
                    if (versao.get(0).equals(TEM_UPDATE)) {
                        //Intent notificationIntent = new Intent(Intent.ACTION_VIEW);
                        //notificationIntent.setClass(context, splash.class);
                        Intent intent = new Intent(context, splash.class);
                        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                        builder.setSmallIcon(R.drawable.icon);
                        builder.setTicker("TakeAwat atualização");
                        builder.setContentTitle("TakeAwat");
                        builder.setContentText("Está disponivel uma nova atualização!\nClique para instalar.");
                        builder.setContentIntent(contentIntent);
                        builder.setWhen(System.currentTimeMillis());
                        builder.setAutoCancel(true);
                        builder.setOngoing(false);
                        nm.notify(id, builder.build());
                    } else if (versao.get(0).equals(ERRO_DOWNLOAD)) {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                        builder.setSmallIcon(R.drawable.icon)
                                .setContentTitle("TakeAwat")
                                .setContentText("Ocurreu um erro ao efetuar o download.")
                                .setContentIntent(null)
                                .setAutoCancel(true)
                                .setOngoing(false);
                        nm.notify(id, builder.build());
                    } else if (versao.get(0).equals(DOWNLOAD_COMPLETO)) {/*
                        Intent install_intent = new Intent(Intent.ACTION_VIEW);
                        install_intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/myapp.apk")),"application/vnd.android.package-archive");
                        PendingIntent pending = PendingIntent.getActivity(context,0, install_intent, 0);*/
                        Intent notificationIntent = new Intent(Intent.ACTION_VIEW);
                        notificationIntent.setDataAndType(
                                Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/TakeAwat.apk")),
                                "application/vnd.android.package-archive");
                        //notificationIntent.setDataAndType(Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/Download/TakeAwat.apk"), "application/vnd.android.package-archive");
                        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                        builder.setSmallIcon(R.drawable.icon);
                        builder.setTicker("TakeAwat atualização");
                        builder.setContentTitle("TakeAwat");
                        builder.setContentText("Download completo!\nClique para instalar.");
                        builder.setContentIntent(contentIntent);
                        builder.setWhen(System.currentTimeMillis());
                        builder.setAutoCancel(true);
                        builder.setOngoing(false);
                        nm.notify(id, builder.build());
                    } else if (versao.get(0).equals(NAO_TEM_UPDATE)) {
                    } else if (versao.get(0).equals(ERRO)) {
                    }
                }
            }).execute();
        }
        wl.release();
    }

    public void SetAlarm(Context context)
    {
        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, testes.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), MINUTOS*1, pi); // Millisec * Second * Minute
    }

    public void CancelAlarm(Context context)
    {
        Intent intent = new Intent(context, testes.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    class DownloadFileAsync extends AsyncTask<String, String, String> {
        GetVersaoCallback versaoCallback;
        NotificationManager mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);

        public DownloadFileAsync(GetVersaoCallback versaoCallback) {
            this.versaoCallback = versaoCallback;
        }

        @Override
        protected String doInBackground(String... arg0) {
            int count;
            String link = "";
            try {
                URL url = new URL("http://takeawat.tk/versao.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(3000);
                String result;
                result = convertStreamToString(conn.getInputStream());
                Log.e("Recebido", " " + result);
                JSONObject jObject = new JSONObject(result);
                if (jObject.length() != 0) {
                    link = jObject.getString("download");
                    String versao = jObject.getString("versao");
                    Log.e("ServerRequests", " " + link + " " + versao);
                    Double versaoAtual = Double.valueOf(context.getResources().getString(R.string.versao));
                    Double versaoNova = Double.valueOf(versao);
                    if (versaoNova<=versaoAtual){
                        return NAO_TEM_UPDATE;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Exception", "Erro[" + e.getMessage() + "] ");
                return ERRO;
            }
            CancelAlarm(context);
            if (!(new UserLocalStore(context).getAutoUpdate())){
                return TEM_UPDATE;
            }

            try {
                mBuilder.setContentTitle("Atualização.")
                        .setTicker("TakeAwat atualização")
                        .setContentText("A conectar ao servidor...")
                        .setSmallIcon(R.drawable.icon)
                        .setAutoCancel(false)
                        .setOngoing(true)
                        .setProgress(0,0,true);
                mNotifyManager.notify(id, mBuilder.build());

                URL url = new URL(link);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.setDoOutput(true);
                c.connect();
                int lenghtOfFile = c.getContentLength();
                Log.e("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);
                String PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"";
                Log.e("PATH", "" + PATH);
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"");
                if(!file.exists()) {
                    file.mkdirs();
                }
                String fileName = "TakeAwat.apk";

                File outputFile = new File(file, fileName);

                FileOutputStream fos = new FileOutputStream(outputFile);

                InputStream is = c.getInputStream();
                mBuilder.setContentText("A efetuar o download...");
                mNotifyManager.notify(id, mBuilder.build());

                byte[] buffer = new byte[16384];
                long total = 0;
                while ((count = is.read(buffer)) != -1) {
                    total += count;
                    int progress = (int) ((total * 100) / lenghtOfFile);
                    Log.e("progresso: ", progress+"");
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    // fos.write(buffer, 0, len1);
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
                return DOWNLOAD_COMPLETO;

            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Erro", "Error: " + e);
                return ERRO_DOWNLOAD;
            }
        }

        protected void onProgressUpdate(String... progress) {
            mBuilder.setProgress(100, Integer.valueOf(progress[0]), false);
            mNotifyManager.notify(id, mBuilder.build());
        }

        protected void onPostExecute(String unused) {
            List<String> retorno = new ArrayList<>();
            retorno.add(unused);
            versaoCallback.done(retorno);
        }
    }

    private static String convertStreamToString(InputStream is) {
        Log.e("is", is+"");
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        Log.e("reader", reader+"");
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
                Log.e("line", line+"");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    /*
    private String CheckUpdates(){
        try {
            String link = "";
            URL url = new URL("http://takeawat.tk/versao.php");
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);

            new Thread(new Runnable() {
                public void run() {
                    try {
                        is = conn.getInputStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        fix = false;
                    }
                }
            }).start();

            while (fix){

            }

            result = convertStreamToString(is);
            Log.e("Recebido", " " + result);
            JSONObject jObject = new JSONObject(result);

            if (jObject.length() != 0) {
                link = jObject.getString("download");
                String versao = jObject.getString("versao");
                Log.e("ServerRequests", " " + link + " " + versao);

                Double versaoAtual = Double.valueOf(context.getResources().getString(R.string.versao));
                Double versaoNova = Double.valueOf(versao);
                if (versaoNova<=versaoAtual){
                    return null;
                }
            }
            return link;
        } catch (Exception e) {
            Log.e("Exception", "Erro[" + e.getMessage() + "] ");
            e.printStackTrace();
            return null;
        }
    }

    private String Download(String link){
        int count;
        try {
            NotificationManager mNotifyManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
            mBuilder.setContentTitle("TakeAwat atualização.")
                    .setContentText("A conectar ao servidor...")
                    .setSmallIcon(R.drawable.icon)
                    .setProgress(0,0,true);
            mNotifyManager.notify(id, mBuilder.build());

            URL url = new URL(link);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();
            int lenghtOfFile = c.getContentLength();
            Log.e("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);
            String PATH = Environment.getExternalStorageDirectory() + "/Download/";
            Log.e("PATH", "" + PATH);
            File file = new File(PATH);
            if(!file.exists()) {
                file.mkdirs();
            }

            mBuilder.setContentText("A efetuar o download...");

            String fileName = "TakeAwat.apk";

            File outputFile = new File(file, fileName);

            FileOutputStream fos = new FileOutputStream(outputFile);

            InputStream is = c.getInputStream();

            byte[] buffer = new byte[16384];
            long total = 0;
            while ((count = is.read(buffer)) != -1) {
                total += count;
                mBuilder.setProgress(100, (int) ((total * 100) / lenghtOfFile), false);
                mNotifyManager.notify(id, mBuilder.build());
                //publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                // fos.write(buffer, 0, len1);
                fos.write(buffer, 0, count);
            }
            fos.close();
            is.close();
            // When the loop is finished, updates the notification
            mBuilder.setContentText("Download complete")
                    // Removes the progress bar
                    .setProgress(0,0,true);
            mNotifyManager.notify(id, mBuilder.build());
            return "pronto";

        } catch (IOException e) {
            Log.e("Erro", "Error: " + e);
            return null;
        }
    }
*/
}
