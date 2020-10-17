package com.seclab.nmaping.ui.hostscan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.google.android.material.snackbar.Snackbar;
import com.seclab.nmaping.MainActivity;
import com.seclab.nmaping.R;
import com.seclab.nmaping.ScanResActivity;
import com.seclab.nmaping.adapter.ScanResAdapter;
import com.seclab.nmaping.bean.ScanBean;
import com.seclab.nmaping.content.MyApplication;
import com.seclab.nmaping.ui.slideshow.SlideshowViewModel;
import com.seclab.nmaping.utils.CommandRunner;
import com.seclab.nmaping.utils.IpUtils;
import com.seclab.nmaping.utils.NmapFormat;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class HostScanFragment extends Fragment {

    private ScanResAdapter scanResAdapter = new ScanResAdapter(R.layout.scan_res_item);
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Activity mainActivity;
    public static File appBinHome;
    String NMAP_COMMAND = "./nmap ";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_hostscan, container, false);

        mainActivity = getActivity();
        assert mainActivity != null;
        appBinHome = mainActivity.getDir("bin", Context.MODE_PRIVATE);

        recyclerView = root.findViewById(R.id.rv_scan_res);
        swipeRefreshLayout = root.findViewById(R.id.swiperefresh);




        recyclerView.setLayoutManager(new LinearLayoutManager(MyApplication.getmContText()));
        recyclerView.setAdapter(scanResAdapter);

        scanResAdapter.addChildClickViewIds(R.id.bt_lookup);
        scanResAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                startActivity(new Intent(MyApplication.getmContText(), ScanResActivity.class));
            }
        });

        try {
            if (IpUtils.innerIP(IpUtils.getLocalIPAddress())){
                new AsyncCommandExecutor().execute(NMAP_COMMAND + "-T 5 -sP -oG - "+ IpUtils.getInnerIPSet(MyApplication.getmContText()));
            } else {
                //在这里显示公网ip的逻辑
                Toast.makeText(MyApplication.getmContText(), IpUtils.getLocalIPAddress()+ " is not private ip", Toast.LENGTH_LONG).show();
            }

        } catch (SocketException e) {
            e.printStackTrace();
        }




        return root;
    }

    public class AsyncCommandExecutor extends AsyncTask<String, Void, Void> {

        public String returnOutput;
//        private ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            swipeRefreshLayout.setRefreshing(true);
//            this.progressDialog.setTitle("NMAP");
//            this.progressDialog.setMessage("Scanning...");
//            this.progressDialog.setCancelable(false);
//            this.progressDialog.show();
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
            Log.d("DEBUG", returnOutput);
            List temp = NmapFormat.NmapResFormat(returnOutput);
            ArrayList<ScanBean> list = new ArrayList<>();
            for (int i = 1; i < NmapFormat.NmapResFormat(returnOutput).size(); i++){
                list.add(new ScanBean(temp.get(i).toString()));
            }
            scanResAdapter.addData(list);
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(MyApplication.getmContText(), NmapFormat.NmapResFormat(returnOutput).toString(), Toast.LENGTH_LONG).show();
        }
    }
}
