package com.wordplat.ikvstockchart;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.xutils.x;

public class BaseFragment extends Fragment {
    protected Activity mActivity; // 给子类用的
    private BroadcastReceiver mReceiver;
    private BroadcastReceiver mReceiver1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }



    /**
     * date: 6/1/18
     * author: chenli
     * description: 注册行情交易广播
     */
    private void registerBroaderCast() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String mDataString = intent.getStringExtra("msg");
//                refreshChart(mDataString);
            }
        };
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, new IntentFilter("com.shinnytech.futures.model.service.WebSocketService.MD_BROADCAST"));

    }
}