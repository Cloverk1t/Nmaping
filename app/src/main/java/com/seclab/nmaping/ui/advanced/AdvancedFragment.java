package com.seclab.nmaping.ui.advanced;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;
import com.seclab.nmaping.MainActivity;
import com.seclab.nmaping.R;
import com.seclab.nmaping.bean.ScanBean;
import com.seclab.nmaping.content.MyApplication;
import com.seclab.nmaping.ui.gallery.GalleryViewModel;
import com.seclab.nmaping.ui.hostscan.HostScanFragment;
import com.seclab.nmaping.utils.CommandRunner;
import com.seclab.nmaping.utils.NmapFormat;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AdvancedFragment extends Fragment {


    public static File appBinHome;
    String NMAP_COMMAND = "./nmap ";


    private TextView scanOutputTV;
    private TextInputLayout flagsET;
    private Button scanBT;

    private MainActivity activity;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_advanced, container, false);

        activity = (MainActivity) getActivity();

        appBinHome = MyApplication.getmContText().getDir("bin", Context.MODE_PRIVATE);
        scanOutputTV = root.findViewById(R.id.scan_output_TV);
        flagsET = root.findViewById(R.id.flags_ET);
        scanBT = root.findViewById(R.id.scan_BT);


        scanBT.setOnClickListener((v) ->{
            String f = flagsET.getEditText().getText().toString();
//            Log.d("Advanced Test", flagsET.getEditText().getText().toString());
            new AsyncCommandExecutor().execute(NMAP_COMMAND + f);

        });



        return root;
    }



    public class AsyncCommandExecutor extends AsyncTask<String, Void, Void> {

        public String returnOutput;
        private ProgressDialog progressDialog = new ProgressDialog(activity);

        @Override
        protected void onPreExecute() {
            this.progressDialog.setTitle("NMAP");
            this.progressDialog.setMessage("正在执行中...");
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
            return;
        }
        @Override
        protected Void doInBackground(String... params) {
            try {
                this.returnOutput = CommandRunner.execCommand(params[0], AdvancedFragment.appBinHome.getAbsoluteFile());
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

            scanOutputTV.setText(returnOutput);
            if(this.progressDialog.isShowing())
                this.progressDialog.dismiss();
        }
    }

}