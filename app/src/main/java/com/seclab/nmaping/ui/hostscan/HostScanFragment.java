package com.seclab.nmaping.ui.hostscan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.google.android.material.snackbar.Snackbar;
import com.seclab.nmaping.R;
import com.seclab.nmaping.ScanResActivity;
import com.seclab.nmaping.adapter.ScanResAdapter;
import com.seclab.nmaping.bean.ScanBean;
import com.seclab.nmaping.content.MyApplication;
import com.seclab.nmaping.utils.CommandRunner;
import com.seclab.nmaping.utils.IpUtils;
import com.seclab.nmaping.utils.NmapFormat;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class HostScanFragment extends Fragment {

    private ScanResAdapter scanResAdapter = new ScanResAdapter(R.layout.scan_res_item);
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Activity mainActivity;
    public static File appBinHome;
    String NMAP_COMMAND = "./nmap ";
    String DEFAULT_SHARED_PREFERENCES = "mySharedPrefs";
    private ArrayList<ScanBean> scaResList = new ArrayList<>();

    private int scanLevel = 4 + 1;
    private int scanNum = 0;
    private String[] scanNumList = new String[]{
            "20",
            "30",
            "50",
            "80",
            "100"
    };
    private SharedPreferences mySharedPreferences;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_hostscan, container, false);




        mainActivity = getActivity();
        assert mainActivity != null;
        appBinHome = mainActivity.getDir("bin", MODE_PRIVATE);

        recyclerView = root.findViewById(R.id.rv_scan_res);
        swipeRefreshLayout = root.findViewById(R.id.swiperefresh);

        mySharedPreferences = mainActivity.getSharedPreferences(DEFAULT_SHARED_PREFERENCES, MODE_PRIVATE);

        scanNum = mySharedPreferences.getInt("scanNum",0);


        recyclerView.setLayoutManager(new LinearLayoutManager(MyApplication.getmContText()));
        recyclerView.setAdapter(scanResAdapter);

        scanResAdapter.addChildClickViewIds(R.id.bt_lookup);
        scanResAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                TextView textView = (TextView) adapter.getViewByPosition(position,R.id.tv_scanTitle);
//                Toast.makeText(MyApplication.getmContText(), textView.getText(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MyApplication.getmContText(),ScanResActivity.class);
                intent.putExtra("IP",textView.getText());
                startActivity(intent);
//                startActivity(new Intent(MyApplication.getmContText(), ScanResActivity.class));
            }
        });


        //处理下拉刷新事件
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    if (!IpUtils.innerIP(IpUtils.getLocalIPAddress()) || IpUtils.getIpBinary(MyApplication.getmContText()) == 0){
                        swipeRefreshLayout.setRefreshing(false);
                        Snackbar.make(getActivity().findViewById(R.id.fab), "您所在的网络暂时不支持哦!", Snackbar.LENGTH_LONG).show();
                        return;
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }
                Snackbar.make(getActivity().findViewById(R.id.fab), "正在进行主机发现,请等待...", Snackbar.LENGTH_LONG).show();
                for (int i = 0; i < scaResList.size(); i++){
                    scanResAdapter.remove(scaResList.get(i));
                }
                recyclerView.refreshDrawableState();
                scaResList.clear();
                initData();
            }
        });



        try {
            if (IpUtils.innerIP(IpUtils.getLocalIPAddress()) && IpUtils.getIpBinary(MyApplication.getmContText()) != 0){
                Snackbar.make(getActivity().findViewById(R.id.fab), "正在进行主机发现,请等待...", Snackbar.LENGTH_LONG).show();
                initData();
            } else {
                //在这里显示公网ip的逻辑
                Snackbar.make(getActivity().findViewById(R.id.fab), "您所在的网络暂时不支持哦!", Snackbar.LENGTH_LONG).show();
            }

        } catch (SocketException e) {
            e.printStackTrace();
        }




        return root;
    }

    public void initData(){
        scanLevel = mySharedPreferences.getInt("scanLevel",4) + 1;
        try {

            new AsyncCommandExecutor().execute(NMAP_COMMAND + "-T "+ scanLevel +" -sP -oG - "+ IpUtils.getInnerIPSet(MyApplication.getmContText()));
        } catch (SocketException e) {
            e.printStackTrace();
        }


    }



    public class AsyncCommandExecutor extends AsyncTask<String, Void, Void> {

        public String returnOutput;
//        private ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            swipeRefreshLayout.setRefreshing(true);

            scaResList.clear();
            recyclerView.refreshDrawableState();

//            scanResAdapter.addData(scaResList);
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

            for (int i = 1; i < NmapFormat.NmapResFormat(returnOutput).size(); i++){
                scaResList.add(new ScanBean(temp.get(i).toString()));
            }
            scanResAdapter.addData(scaResList);
            swipeRefreshLayout.setRefreshing(false);
//            Toast.makeText(MyApplication.getmContText(), NmapFormat.NmapResFormat(returnOutput).toString(), Toast.LENGTH_LONG).show();
        }
    }
}
