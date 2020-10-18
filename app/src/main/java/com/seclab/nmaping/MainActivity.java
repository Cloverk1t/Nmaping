package com.seclab.nmaping;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
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
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.lang.reflect.Method;
import java.net.SocketException;
import java.util.ArrayList;

public class MainActivity extends BaseActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;


    String DEBUG_TAG = "myTag";

    String DEFAULT_SHARED_PREFERENCES = "mySharedPrefs";
    String firstStartPref = "firstStart";

    public static File appBinHome;
    String NMAP_COMMAND = "./nmap ";
    private SharedPreferences mySharedPreferences;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化nmap
        boolean firstInstall = true;
        appBinHome = getDir("bin", Context.MODE_PRIVATE);

        mySharedPreferences = getSharedPreferences(DEFAULT_SHARED_PREFERENCES, MODE_PRIVATE);
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

                Snackbar.make(view, IpUtils.getIpBinary(MyApplication.getmContText()) == 0 ? "true" :  "false", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_hostscan, R.id.nav_slideshow ,R.id.nav_advanced)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);







    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==android.R.id.home){
            drawer.openDrawer(Gravity.LEFT);
        }

        switch (item.getItemId()){
            case R.id.action_settings_level:
                new MaterialAlertDialogBuilder(MainActivity.this,R.style.myDialog)
                        .setTitle("设置扫描等级")
                        .setPositiveButton("Ok",null)
                        .setSingleChoiceItems(new String[]{
                                "1(最慢,使用前要想好)",
                                "2",
                                "3(Nmap默认,均衡选项)",
                                "4",
                                "5(最快,程序默认,精度低)"
                        }, mySharedPreferences.getInt("scanLevel", 4), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mySharedPreferences.edit().putInt("scanLevel",which).apply();

                            }
                        })
                        .show();
                break;
            case R.id.action_settings_number:
                new MaterialAlertDialogBuilder(MainActivity.this,R.style.myDialog)
                        .setTitle("设置扫描端口数量")
                        .setPositiveButton("Ok",null)
                        .setSingleChoiceItems(new String[]{
                                "20",
                                "30",
                                "50",
                                "80",
                                "100"
                        }, mySharedPreferences.getInt("scanNum", 0), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mySharedPreferences.edit().putInt("scanNum",which).apply();
                            }
                        })
                        .show();
                break;
        }


        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}