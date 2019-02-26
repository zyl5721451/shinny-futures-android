package com.shinnytech.futures.model.livem;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by yilong on 2017/12/28.
 */

public class DayFragment extends KLineFragment{
    private String code;
    private String startData;
    private String endData;
    private String step;
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        code = bundle.getString("code");
        startData = bundle.getString("startdata");
        endData = bundle.getString("enddata");
        step = bundle.getString("step");

        initUI();
    }
}
