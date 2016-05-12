package net.netne.kitlfre.takeawat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;
import java.util.jar.JarFile;


/**
 * Created by Bruno Martins
 */
public class splash extends Activity {

    UserLocalStore userLocalStore;
    ProgressBar progressBar;
    TextView title;
    TextView percent;
    TextView percentOf;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        /*
        Define o layout
         */
        setContentView(R.layout.splash_screen);
        /*
        Inicializa a classe UserLocalStore
         */
        userLocalStore =new UserLocalStore(this);
        /*
        Cria variaveis para as textView e para a progressBar
         */
        progressBar = (ProgressBar) findViewById(R.id.progressBarSplash);
        title = (TextView)findViewById(R.id.txtSplashTitle);
        percent = (TextView)findViewById(R.id.txtSplashPercent);
        percentOf = (TextView)findViewById(R.id.txtSplashTotal);
        /*
        Alinha a TextView a direita
         */
        percentOf.setGravity(Gravity.END);
        /*
        Define os valores Pre-defenidos
         */
        title.setText("A carregar...");
        progressBar.setProgress(0);
        percent.setText("0%");
        percentOf.setText("0/100");
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*
        Ao iniciar execura o metodo StartSplash passados 1.5 segundos
         */
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                StartSplash();
            }
        }, 1500);
    }

    public void StartSplash(){
        /*
        Ao iniciar verifica se o telemovel esta ligado a internet
         */
        Toast.makeText(getBaseContext(), userLocalStore.getCheckUpdate()+" "+userLocalStore.getAutoUpdate(), Toast.LENGTH_LONG).show();
        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            /*
            Inicialisa a classe updater para verificar se existem atualizaçoes disponiveis
             */
            updater update = new updater(splash.this, title, progressBar, percent, percentOf);
            update.verificaUpdates(new GetVersaoCallback() {
                @Override
                public void done(List<String> versao) {
                    /*
                    quando acabar a verificação verifico se o resultado(variavel versao) é null
                    caso seja null é porque ocurreu um erro ou nao existiam atualizaçoes disponiveis
                     */
                    if (versao==null || versao.get(0) == null){
                        AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(splash.this);
                        dialogBuilder.setMessage("Ocurreu um erro ao atualizar!\nPor favor tente novamente mais tarde!");
                        dialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                                System.exit(0);
                            }
                        });
                        dialogBuilder.show();
                    } else if (versao.get(0).equalsIgnoreCase("tem")){
                        title.setText("A instalar...");
                        if (isApkCorrupted()){
                            title.setText("Ocurreu um erro! O ficheiro está corrompido.");
                        }
                        else {
                            installApp();
                        }
                    } else {
                        GuardaProdutos();
                    }
                }
            });
        }
        else{
            AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(splash.this);
            dialogBuilder.setMessage("Não foi possivel conectar ao servidor!\nDeseja tentar novamente?");
            dialogBuilder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent splashIntent = new Intent(splash.this,splash.class);
                    splash.this.startActivity(splashIntent);
                    splash.this.finish();
                }
            });
            dialogBuilder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                    System.exit(0);
                }
            });
            dialogBuilder.show();
        }
    }

    private boolean isApkCorrupted() {
        boolean corruptedApkFile = false;
        try {
            new JarFile(new File(Environment.getExternalStorageDirectory() + "/Download/" +
                    "TakeAwat.apk"));
        } catch (Exception ex) {
            corruptedApkFile = true;
        }
        return corruptedApkFile;
    }

    public void GuardaProdutos(){
        title.setText("A atualizar dados da aplicação...");
        progressBar.setProgress(50);
        percent.setText("50%");
        percentOf.setText("50/100");
        ServerRequests serverRequests = new ServerRequests(splash.this, false);
        serverRequests.fetchProdutosDataInBackground(new GetProdutoCallback() {
            @Override
            public void done(List<Produto> returnProduto) {
                progressBar.setProgress(75);
                percent.setText("75%");
                percentOf.setText("75/100");
                if (returnProduto != null) {
                    dataBaseLocal bdLocal = new dataBaseLocal(splash.this, null, null);
                    bdLocal.apagaTodosProdutos();
                    for (Produto p : returnProduto) {
                        bdLocal.adicionaProduto(p);
                    }
                    new Handler().postDelayed(new Runnable(){
                        @Override
                        public void run() {
                            Login();
                        }
                    }, 500);
                }
                else{
                    AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(splash.this);
                    dialogBuilder.setMessage("Não foi possivel conectar ao servidor!\nSe o erro persistir tente novamente mais tarde.\nDeseja tentar novamente?");
                    dialogBuilder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent splashIntent = new Intent(splash.this,splash.class);
                            splash.this.startActivity(splashIntent);
                            splash.this.finish();
                        }
                    });
                    dialogBuilder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                            System.exit(0);
                        }
                    });
                    dialogBuilder.show();
                }
            }
        });
    }

    public void Login(){
        startService(new Intent(getBaseContext(), BackgroudService.class));
        if (userLocalStore.getUserLoggedInSave()) {
            try {
                title.setText("A efetuar login...");
                progressBar.setProgress(85);
                percent.setText("85%");
                percentOf.setText("85/100");
                ServerRequests serverRequests = new ServerRequests(splash.this, false);
                serverRequests.fetchUserDataInBackground(new User(userLocalStore.getUser().email, userLocalStore.getUser().password), new GetUserCallback() {
                    @Override
                    public void done(User returnUser) {
                        if (returnUser == null) {
                            progressBar.setProgress(100);
                            percent.setText("100%");
                            percentOf.setText("100/100");
                            Intent loginIntent = new Intent(splash.this, login.class);
                            AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(splash.this);
                            dialogBuilder.setMessage("Dados incorretos!");
                            dialogBuilder.setPositiveButton("ok", null);
                            dialogBuilder.show();
                            startActivity(loginIntent);
                            splash.this.finish();
                        } else {
                            Intent inicialIntent = new Intent(splash.this, inicial.class);
                            userLocalStore.storeUserData(returnUser);
                            userLocalStore.setUserLoggedIn(true);
                            userLocalStore.setUserLoggedInSave(true);
                            startActivity(inicialIntent);
                            splash.this.finish();
                        }
                    }
                });
            } catch (Exception er){
                er.printStackTrace();
                Log.e("Exception: ", "Erro[ " + er.getMessage() + " ] ");
            }
        } else {
            startActivity(new Intent(splash.this, login.class));
            splash.this.finish();
        }
    }

    public void installApp() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/Download/" +
                "TakeAwat.apk")), "application/vnd.android.package-archive");
        startActivity(intent);
    }
}