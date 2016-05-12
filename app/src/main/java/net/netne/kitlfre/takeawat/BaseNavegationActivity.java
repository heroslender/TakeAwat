package net.netne.kitlfre.takeawat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

/**
 * Created by Bruno Martins
 */
public abstract class BaseNavegationActivity  extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onStop() {
        super.onStop();
    }

    private Toolbar mActionBarToolbar;
    private DrawerLayout mDrawerLayout;
    protected NavigationView mNavigationView;
    protected UserLocalStore userLocalStore;

    private void logout(){
        User u = userLocalStore.getUser();
        boolean l = userLocalStore.getUserLoggedIn();
        userLocalStore.clearUserData();
        userLocalStore.setUserLoggedIn(l);
        if(l)
            userLocalStore.storeUserData(u);
        userLocalStore.setUserLoggedInSave(false);
    }

    protected boolean useToolbar() {
        return true;
    }

    protected boolean useDrawerToggle() {
        return true;
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        getActionBarToolbar();
        setupNavDrawer();
        View header = LayoutInflater.from(this).inflate(R.layout.header, null);
        mNavigationView.addHeaderView(header);
        TextView nome = (TextView) header.findViewById(R.id.nav_header_name);
        TextView email = (TextView) header.findViewById(R.id.nav_header_email);
        Log.e("nome:", " " + userLocalStore.getUser().nome);
        if (userLocalStore.getUser().nome != null && userLocalStore.getUser().nome != "")
           nome.setText(userLocalStore.getUser().nome);
        Log.e("email:", " " + userLocalStore.getUser().email);
        if (userLocalStore.getUser().email != null && userLocalStore.getUser().email != "")
             email.setText(userLocalStore.getUser().email);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userLocalStore = new UserLocalStore(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(this);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_inicio) {
            startActivity(new Intent(getBaseContext(), inicial.class));
        } if (id == R.id.nav_ementa) {
            startActivity(new Intent(getBaseContext(), ementa.class));
        } else if (id == R.id.nav_pedir) {
            startActivity(new Intent(getBaseContext(), pedir.class));
        } else if (id == R.id.nav_pedidos) {
        } else if (id == R.id.nav_acc) {
        } else if (id == R.id.nav_defenicoes) {
            startActivity(new Intent(getBaseContext(), defenicoes.class));
        } else if (id == R.id.nav_sair) {
            logout();
            startActivity(new Intent(getBaseContext(), login.class));
        }

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);
        return true;
    }

    protected Toolbar getActionBarToolbar() {
        if (mActionBarToolbar == null) {
            mActionBarToolbar = (Toolbar) findViewById(R.id.tool_bar);
            if (mActionBarToolbar != null) {
                // Depending on which version of Android you are on the Toolbar or the ActionBar may be
                // active so the a11y description is set here.
                mActionBarToolbar.setNavigationContentDescription(getResources()
                        .getString(R.string.navdrawer_description_a11y));
                //setSupportActionBar(mActionBarToolbar);
                if (useToolbar()) { setSupportActionBar(mActionBarToolbar);
                } else { mActionBarToolbar.setVisibility(View.GONE); }

            }
        }
        return mActionBarToolbar;
    }

    private void setupNavDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout == null) {
            return;
        }
        // use the hamburger menu
        if( useDrawerToggle()) {
            ActionBarDrawerToggle mToggle = new ActionBarDrawerToggle(
                    this, mDrawerLayout, mActionBarToolbar,
                    R.string.navigation_drawer_open,
                    R.string.navigation_drawer_close);
            mDrawerLayout.setDrawerListener(mToggle);
            mToggle.syncState();
        }
        else if(useToolbar() && getSupportActionBar() != null) {
            // Use home/back button instead
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(ContextCompat
                    .getDrawable(this, R.drawable.abc_ic_ab_back_mtrl_am_alpha));
        }
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    protected void closeNavDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }
}
