package com.seclab.nmaping.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.seclab.nmaping.R;
import com.seclab.nmaping.content.MyApplication;
import com.seclab.nmaping.utils.IpUtils;

import org.w3c.dom.Text;

import java.net.SocketException;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        homeViewModel =
//                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView text_network = root.findViewById(R.id.text_network);
        final TextView text_security = root.findViewById(R.id.text_security);
        final TextView text_bssid = root.findViewById(R.id.text_bssid);
        final TextView text_dhcp = root.findViewById(R.id.text_dhcp);
        final TextView text_ipAddress = root.findViewById(R.id.text_ip);
        final TextView text_netmask = root.findViewById(R.id.text_netmask);
        final TextView text_gateway = root.findViewById(R.id.text_gateway);
        final TextView text_dns1 = root.findViewById(R.id.text_dns1);
        final TextView text_dns2 = root.findViewById(R.id.text_dns2);
        final Button button_scan = root.findViewById(R.id.btnScan);
        final Button button_settings = root.findViewById(R.id.btnSettings);
        final View cardNetworkUnable = root.findViewById(R.id.card_network_unable);
        final TextView tvNetworkError = root.findViewById(R.id.tv_network_error);

        try {
            if (!IpUtils.innerIP(IpUtils.getLocalIPAddress()) || IpUtils.getIpBinary(MyApplication.getmContText()) == 0){
                cardNetworkUnable.setVisibility(View.VISIBLE);
            }
            if (IpUtils.isBigNet(IpUtils.getLocalIPAddress())){
                cardNetworkUnable.setVisibility(View.VISIBLE);
                tvNetworkError.setText("您正处于172网段,主机发现会非常耗时！");
            }
            //添加172网段提示

        } catch (SocketException e) {
            e.printStackTrace();
        }

        button_scan.setOnClickListener(v -> {
            try {
                if (!IpUtils.innerIP(IpUtils.getLocalIPAddress()) || IpUtils.getIpBinary(MyApplication.getmContText()) == 0) {
                    Snackbar.make(getActivity().findViewById(R.id.fab), "不支持的网络类型!", Snackbar.LENGTH_LONG)
                            .setAction("打开wifi设置", v1 -> {
                                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                            })
                            .show();
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Navigation.findNavController(getView()).navigate(R.id.action_nav_home_to_nav_hostscan3);
        });
        button_settings.setOnClickListener(v -> {
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        });
        try {
            @SuppressLint("WrongConstant") WifiManager mWifiManger = (WifiManager) MyApplication.getmContText().getSystemService("wifi");
            DhcpInfo dhcpInfo = mWifiManger.getDhcpInfo();
            text_network.setText(mWifiManger.getConnectionInfo().getSSID().toString());
            text_security.setText("WPA2");
            text_bssid.setText(mWifiManger.getConnectionInfo().getBSSID().toString());
            text_dhcp.setText(intToIp(dhcpInfo.serverAddress));
            text_ipAddress.setText(intToIp(dhcpInfo.ipAddress));
            text_netmask.setText(intToIp(dhcpInfo.netmask));
            text_gateway.setText(intToIp(dhcpInfo.gateway));
            text_dns1.setText(intToIp(dhcpInfo.dns1));
            text_dns2.setText(intToIp(dhcpInfo.dns2));
        } catch (Exception e){
            e.printStackTrace();
        }

        return root;
    }

    public String intToIp(int paramInt) {
        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "."
                + (0xFF & paramInt >> 24);
    }
}

