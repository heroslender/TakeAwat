package net.netne.kitlfre.takeawat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.widget.ProgressBar;
import android.widget.TextView;

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
public class updater {
    ProgressBar progressBar;
    TextView title;
    TextView percent;
    TextView percentOf;
    ProgressDialog mProgressDialog;
    String fileName;
    private Activity activity;

    public  updater(){}

    public updater(Activity act, TextView title1, ProgressBar progressBar1, TextView percent1, TextView percentOf1){
        activity = act;
        progressBar = progressBar1;
        title = title1;
        percent = percent1;
        percentOf = percentOf1;
        percentOf.setGravity(Gravity.END);
    }

    public void verificaUpdates(final GetVersaoCallback versaoCallback){
        try {
            new DownloadFileAsync(versaoCallback).execute();
        } catch (Exception erro) {
            Log.e("Erro", erro.getMessage());
        }
    }

    class DownloadFileAsync extends AsyncTask<String, String, String> {
        GetVersaoCallback versaoCallback;

        public DownloadFileAsync(GetVersaoCallback versaoCallback) {
            this.versaoCallback = versaoCallback;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setProgress(0);
            title.setText("A ligar ao servidor...");
        }

        @Override
        protected String doInBackground(String... arg0) {
            int count;
            String link = "";
            try {
                URL url = new URL("http://takeawat.tk/" + "versao.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                // set the timeout in milliseconds until a connection is established
                // the default value is zero, that means the timeout is not used
                conn.setConnectTimeout(3000);
                // set the default socket timeout (SO_TIMEOUT) in milliseconds
                // which is the timeout for waiting for data
                conn.setReadTimeout(3000);
                String result;

                result = convertStreamToString(conn.getInputStream());
                Log.e("Recebido", " " + result);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        title.setText("A procurar atualizações...");
                    }
                });
                JSONObject jObject = new JSONObject(result);

                if (jObject.length() != 0) {
                    link = jObject.getString("download");
                    String versao = jObject.getString("versao");
                    Log.e("ServerRequests", " " + link + " " + versao);

                    Double versaoAtual = Double.valueOf(activity.getResources().getString(R.string.versao));
                    Double versaoNova = Double.valueOf(versao);
                    if (versaoNova<=versaoAtual){
                        return "nenhuma";
                    }
                }
            } catch (Exception e) {
                Log.e("Exception", "Erro[" + e.getMessage() + "] ");
                e.printStackTrace();
                return null;
            }
            try {
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

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        title.setText("A efetuar o download...");
                    }
                });
                fileName = "TakeAwat.apk";

                File outputFile = new File(file, fileName);

                FileOutputStream fos = new FileOutputStream(outputFile);

                InputStream is = c.getInputStream();

                byte[] buffer = new byte[16384];
                long total = 0;
                while ((count = is.read(buffer)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    // fos.write(buffer, 0, len1);
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
                return "tem";

            } catch (IOException e) {
                Log.e("Erro", "Error: " + e);
                return null;
            }
        }

        protected void onProgressUpdate(String... progress) {
            Log.w("ANDRO_ASYNC", progress[0]);
            progressBar.setProgress(Integer.valueOf(progress[0]));
            percent.setText(progress[0]+"%");
            percentOf.setText(progress[0]+"/100");
        }

        protected void onPostExecute(String unused) {
            List<String> retorno = new ArrayList<>();
            retorno.add(unused);
            versaoCallback.done(retorno);
        }
    }

    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
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
            mProgressDialog = new ProgressDialog(updater.this);

            // mProgressDialog.setIcon(R.drawable.icon);
            mProgressDialog.setIcon(R.drawable.ic_home);
            mProgressDialog.setTitle("A ligar ao servidor... ");

            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(true);
            mProgressDialog.show();*/
    //mProgressDialog.setTitle("Downloading... ");
    //mProgressDialog.setProgress(Integer.parseInt(progress[0]));
    //mProgressDialog.dismiss();
}
