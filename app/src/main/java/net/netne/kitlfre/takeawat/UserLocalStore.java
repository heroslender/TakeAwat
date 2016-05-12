package net.netne.kitlfre.takeawat;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Bruno Martins
 */
public class UserLocalStore {

    public static final String SP_NAME = "userDetails";
    SharedPreferences userLocalDatabase;
    Context context;

    public UserLocalStore(Context context){
        userLocalDatabase=context.getSharedPreferences(SP_NAME, 0);
        this.context = context;
    }

    public void storeUserData(User user){
        SharedPreferences.Editor spEditor=userLocalDatabase.edit();
        spEditor.putString("email", user.email);
        spEditor.putString("password", user.password);
        spEditor.putString("nome", user.nome);
        spEditor.putString("morada", user.morada);
        spEditor.putString("telefone", user.telefone);
        spEditor.commit();
    }

    public User getUser(){
        String email = userLocalDatabase.getString("email", "");
        String password = userLocalDatabase.getString("password", "");
        String nome = userLocalDatabase.getString("nome", "");
        String morada = userLocalDatabase.getString("morada", "");
        String telefone = userLocalDatabase.getString("telefone", "");

        return new User(email, password, nome, telefone, morada);
    }

    public void setUserLoggedIn(boolean loggedIn){
        SharedPreferences.Editor spEditor=userLocalDatabase.edit();
        spEditor.putBoolean("LoggedIn", loggedIn);
        spEditor.commit();
    }

    public boolean getUserLoggedIn(){
        return userLocalDatabase.getBoolean("LoggedIn", false);
    }

    public void clearUserData(){
        SharedPreferences.Editor spEditor=userLocalDatabase.edit();
        spEditor.clear();
        spEditor.commit();
    }

    public void setUserLoggedInSave(boolean save) {
        SharedPreferences.Editor spEditor=userLocalDatabase.edit();
        spEditor.putBoolean("LoggedInSave", save);
        spEditor.commit();
    }

    public boolean getUserLoggedInSave(){
        return userLocalDatabase.getBoolean("LoggedInSave", false);
    }

    public void setCheckUpdate(boolean checkUpdate) {
        SharedPreferences.Editor spEditor=userLocalDatabase.edit();
        spEditor.putBoolean("CheckUpdate", checkUpdate);
        spEditor.commit();
    }

    public boolean getCheckUpdate(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getBoolean("CheckUpdate", false);
        //return userLocalDatabase.getBoolean("CheckUpdate", false);
    }

    public void setAutoUpdate(boolean autoUpdate) {
        SharedPreferences.Editor spEditor=userLocalDatabase.edit();
        spEditor.putBoolean("AutoUpdate", autoUpdate);
        spEditor.commit();
    }

    public boolean getAutoUpdate(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getBoolean("AutoUpdate", false);
        //return userLocalDatabase.getBoolean("AutoUpdate", false);
    }
}
