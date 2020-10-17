package com.seclab.nmaping;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.seclab.nmaping.base.BaseActivity;
import com.seclab.nmaping.content.MyApplication;
import com.seclab.nmaping.nmapinstall.NmapBinaryInstaller;
import com.seclab.nmaping.utils.IpUtils;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.net.SocketException;
import java.util.ArrayList;

public class MainActivity extends BaseActivity {

    private AppBarConfiguration mAppBarConfiguration;



    String DEBUG_TAG = "myTag";

    String DEFAULT_SHARED_PREFERENCES = "mySharedPrefs";
    String firstStartPref = "firstStart";

    public static File appBinHome;
    String NMAP_COMMAND = "./nmap ";

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化nmap
        boolean firstInstall = true;
        appBinHome = getDir("bin", Context.MODE_PRIVATE);

        SharedPreferences mySharedPreferences = getSharedPreferences(DEFAULT_SHARED_PREFERENCES, MODE_PRIVATE);
        firstInstall = mySharedPreferences.getBoolean(firstStartPref, true);
        if(firstInstall) {
            NmapBinaryInstaller installer = new NmapBinaryInstaller(getApplicationContext());
            installer.installResources();
            Log.d(DEBUG_TAG, "Installing binaries");


            // TODO: Write some test code to see if the binaries are placed correctly and have the right permissions!
            mySharedPreferences.edit().putBoolean(firstStartPref, false).apply();
        }



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Snackbar.make(view, IpUtils.getInnerIPSet(MyApplication.getmContText()), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_hostscan, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);







    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_settings_level:

//        .setPositiveButton(resources.getString(R.string.ok)) { dialog, which ->
//                // Respond to positive button press
//            }
//            // Single-choice items (initialized with checked item)
//        .setSingleChoiceItems(singleItems, checkedItem) { dialog, which ->
//                // Respond to item chosen
//            }
//        .show()



                break;

        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}