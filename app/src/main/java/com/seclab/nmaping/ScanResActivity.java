package com.seclab.nmaping;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.seclab.nmaping.base.BaseActivity;
import com.seclab.nmaping.bean.ScanBean;
import com.seclab.nmaping.content.MyApplication;
import com.seclab.nmaping.ui.hostscan.HostScanFragment;
import com.seclab.nmaping.utils.CommandRunner;
import com.seclab.nmaping.utils.IpUtils;
import com.seclab.nmaping.utils.NmapFormat;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.util.List;

public class ScanResActivity extends BaseActivity {

    private TextView tvSingleHostRes;
    public static File appBinHome;
    String NMAP_COMMAND = "./nmap ";
    private View scanResProgress;
    private View scanResView;
    String DEFAULT_SHARED_PREFERENCES = "mySharedPrefs";
    private int scanNum = 0;
    private String[] scanNumList = new String[]{
            "20",
            "30",
            "50",
            "80",
            "100"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanres);



        tvSingleHostRes = findViewById(R.id.single_host_res);
        appBinHome = getDir("bin", Context.MODE_PRIVATE);
        scanResProgress = findViewById(R.id.scan_res_progress);
        scanResView = findViewById(R.id.scan_res_view);

        SharedPreferences mySharedPreferences = getSharedPreferences(DEFAULT_SHARED_PREFERENCES, MODE_PRIVATE);
        scanNum = mySharedPreferences.getInt("scanNum",0);


//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//添加默认的返回图标
//        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
//
//
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });




        Intent intent = getIntent();

//        tvSingleHostRes.setText(intent.getStringExtra("IP"));



        initData(intent.getStringExtra("IP"));




    }
    public void initData(String ip){

        new AsyncCommandExecutor().execute(NMAP_COMMAND + "-sT -n --top-ports " + scanNumList[scanNum] +" "+ ip);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return true;
    }


    public class AsyncCommandExecutor extends AsyncTask<String, Void, Void> {

        public String returnOutput;
//        private ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            scanResProgress.setVisibility(View.VISIBLE);
            scanResView.setVisibility(View.GONE);



            return;
        }
        @Override
        protected Void doInBackground(String... params) {
            try {
                this.returnOutput = CommandRunner.execCommand(params[0], HostScanFragment.appBinHome.getAbsoluteFile());
            } catch (IOException e) {
                this.returnOutput = "IOException while trying to scan!";
                e.printStackTrace();
            } catch (InterruptedException e) {
                this.returnOutput = "Nmap Scan Interrupted!";
            }

            return null;
        }
        @Override
        protected void onPostExecute(Void result) {

            scanResProgress.setVisibility(View.GONE);
            scanResView.setVisibility(View.VISIBLE);
            tvSingleHostRes.setText(returnOutput);



        }
    }



}
