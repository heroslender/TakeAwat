package net.netne.kitlfre.takeawat;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bruno Martins
 */
public class ServerRequests {
    ProgressDialog progressDialog;
    public static final int CONNECTION_TIMEOUT = 1000 * 15;
    public static final String SERVER_ADDRESS = "http://takeawat.tk/";
    public boolean UsaDialog = false;
    Context context;

    public ServerRequests(Context context, boolean usaDialog) {
        this.context = context;
        if(usaDialog) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setCancelable(false);
            progressDialog.setTitle("A Processar");
            progressDialog.setMessage("Por favor aguarde...");
        }
        UsaDialog = usaDialog;
    }

    public void storeUserDataInBackground(User user, GetUserCallback userCallback) {
        if (UsaDialog)
        progressDialog.show();
        new StoreUserDataAsyncTasck(user, userCallback).execute();
    }

    public void fetchUserDataInBackground(User user, GetUserCallback userCallback) {
        if (UsaDialog)
        progressDialog.show();
        new FetchUserDataAsyncTasck(user, userCallback).execute();
    }

    public void fetchProdutosDataInBackground(GetProdutoCallback userCallback) {
        new FetchProdutosDataAsyncTasck(userCallback).execute();
    }

    public class StoreUserDataAsyncTasck extends AsyncTask<Void, Void, Void> {
        User user;
        GetUserCallback userCallback;

        public StoreUserDataAsyncTasck(User user, GetUserCallback userCallback) {
            this.user = user;
            this.userCallback = userCallback;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("email", user.email));
            dataToSend.add(new BasicNameValuePair("pass", user.password));
            dataToSend.add(new BasicNameValuePair("nome", user.nome));
            dataToSend.add(new BasicNameValuePair("telefone", user.telefone));
            dataToSend.add(new BasicNameValuePair("morada", user.morada));

            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "Register.php");

            try {
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                client.execute(post);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (UsaDialog)
                progressDialog.dismiss();
            userCallback.done(null);
            super.onPostExecute(aVoid);
        }
    }

    public class FetchUserDataAsyncTasck extends AsyncTask<Void, Void, User> {
        User user;
        GetUserCallback userCallback;
        String email = "";
        String Pw = "";

        public FetchUserDataAsyncTasck(User user, GetUserCallback userCallback) {
            this.user = user;
            this.userCallback = userCallback;
            email = user.email;
            Pw = user.password;
        }

        @Override
        protected User doInBackground(Void... params) {
            User returnedUser = null;
            try {
                URL url = new URL(SERVER_ADDRESS + "Login.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("email", email)
                        .appendQueryParameter("password", "" + Pw);
                final String postParameters = builder.build().getEncodedQuery();
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(3000);
                conn.setRequestMethod("POST");
                conn.setFixedLengthStreamingMode(postParameters.getBytes().length);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                //send the POST out
                PrintWriter pw = new PrintWriter(conn.getOutputStream());
                pw.print(postParameters);
                pw.close();

                conn.connect();

                String result = convertStreamToString(conn.getInputStream());

                Log.i("JSON Parser", "Recebido" + result);
                JSONObject jObject = null;
                try {
                    jObject = new JSONObject(result);
                } catch (JSONException e) {
                    Log.e("JSON Parser", "Error parsing data [" + e.getMessage() + "] " + result);
                }

                if (jObject.length() == 0) {
                    returnedUser = null;
                } else {
                    String nome = jObject.getString("nome");
                    String telefone = jObject.getString("telefone");
                    String morada = jObject.getString("morada");

                    returnedUser = new User(email, Pw, nome, telefone, morada);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Exception", "Erro[" + e.getMessage() + "] " + email + " " + Pw);
            }
            return returnedUser;
        }

        @Override
        protected void onPostExecute(User returnedUser) {
            if (UsaDialog)
                progressDialog.dismiss();
            userCallback.done(returnedUser);
            super.onPostExecute(user);
        }
    }

    public class FetchProdutosDataAsyncTasck extends AsyncTask<Void, Void, List<Produto>> {
        List<Produto> produto;
        GetProdutoCallback produtoCallback;

        public FetchProdutosDataAsyncTasck(GetProdutoCallback produtoCallback) {
            this.produtoCallback = produtoCallback;
        }

        @Override
        protected List<Produto> doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();

            dataToSend.add(new BasicNameValuePair("produto", 1 + ""));

            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "Produtos.php");

            List<Produto> returnedProduto = null;
            try {
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                HttpResponse httpResponse = client.execute(post);

                HttpEntity entity = httpResponse.getEntity();

                InputStream is = null;
                StringBuilder sb = null;
                String result = null;

                is = entity.getContent();
                //convert response to string
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                    sb = new StringBuilder();
                    sb.append(reader.readLine() + "\n");
                    String line = "0";

                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();
                    result = sb.toString();
                } catch(Exception e){
                    Log.e("log_tag", "Error converting result " + e.toString());
                }
                Log.e("result", result);
                JSONArray jArray = new JSONArray(result);
                JSONObject json_data = null;
                returnedProduto = new ArrayList<>();
                for (int i = 0; i < jArray.length(); i++) {
                    json_data = jArray.getJSONObject(i);
                    Integer cod_produto = json_data.getInt("cod_produto");
                    String designacao = json_data.getString("designacao");
                    String preco = json_data.getString("preco");
                    String dia = json_data.getString("dia");
                    Log.e("Produto", "p:" + cod_produto + " " + designacao + " " + preco + " " + dia);
                    Produto produto = new Produto(cod_produto, designacao, preco, dia);
                    returnedProduto.add(produto);
                }
            } catch (Exception e) {

                e.printStackTrace();
                Log.e("Exception", "Erro[" + e.getMessage() + "] ");
            }
            return returnedProduto;
        }

        @Override
        protected void onPostExecute(List<Produto> returnedProduto) {
            produtoCallback.done(returnedProduto);
            super.onPostExecute(produto);
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
}
