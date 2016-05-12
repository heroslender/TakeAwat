package net.netne.kitlfre.takeawat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

/**
 * Created by Bruno Martins
 */

public class login extends AppCompatActivity {
    EditText email;
    EditText pass;
    UserLocalStore userLocalStore;
    boolean sessao = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        email=(EditText) findViewById(R.id.txt_login_cod_cli);
        pass=(EditText) findViewById(R.id.txt_login_pass);

        userLocalStore =new UserLocalStore(this);

        Switch toggle = (Switch) findViewById(R.id.mantem_sessao);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sessao = isChecked;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(authenticate()){
            User user= userLocalStore.getUser();
            email.setText(user.email);
        }
        Log.e("thaks", Environment.getExternalStorageDirectory().getPath());
        Log.e("path", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());
        Snackbar.make(findViewById(R.id.login), "Bem-Vindo", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(login.this);
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

    private boolean authenticate(){
        return userLocalStore.getUserLoggedIn();
    }


    public void btnLogin(View view) {
        try {
            if(email.getText().toString().trim().length()==0 || pass.getText().toString().trim().length()==0){
                throw new Exception("Os campos não estão preenchidos!");
            }
            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                String cod_cliente = email.getText().toString();
                String password = pass.getText().toString();

                User user = new User(cod_cliente, password);

                authenticate(user, false);
            }
            else {
                throw new Exception("Necessita de acesso a internet para utilizar a aplicação!");
            }
        }
        catch (Exception er){
            showError(er);
            er.printStackTrace();
        }
    }

    private void authenticate(final User user, final Boolean mantemLogado){
        try {
            ServerRequests serverRequests = new ServerRequests(this, true);
            serverRequests.fetchUserDataInBackground(user, new GetUserCallback() {
                @Override
                public void done(User returnUser) {
                    if (returnUser == null) {
                        MessageBox("Dados incorretos!");
                    } else {
                        logUserIn(returnUser, mantemLogado);
                    }
                }
            });
        } catch (Exception er){
            er.printStackTrace();
            Log.e("Exception: ", "Erro[" + er.getMessage() + "] ");
        }
    }

    private void logUserIn(User returnedUser, Boolean mantemLogado){
        userLocalStore.storeUserData(returnedUser);
        userLocalStore.setUserLoggedIn(true);
        if (mantemLogado)
            userLocalStore.setUserLoggedInSave(true);
        if (!mantemLogado)
            userLocalStore.setUserLoggedInSave(sessao);
        startActivity(new Intent(this, inicial.class));
        login.this.finish();
    }

    private void showError(Exception ero){
        AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(login.this);
        dialogBuilder.setMessage("Ocorreu um erro:\n" + ero.getMessage());
        dialogBuilder.setPositiveButton("ok", null);
        dialogBuilder.show();
    }

    private void MessageBox(String message){
        AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(login.this);
        dialogBuilder.setMessage(message);
        dialogBuilder.setPositiveButton("ok", null);
        dialogBuilder.show();
    }
}