package com.shinnytech.futures.model.livem;

import com.shinnytech.futures.model.bean.futureinfobean.KlineEntity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by yilong on 2018/2/8.
 */

public class AnalysisData {
    private float step;
    private Map<String, KlineEntity.DataEntity> resultEntities = new HashMap<>();
    private Map.Entry<String, KlineEntity.DataEntity> currentEntry;
    private Map.Entry<String, KlineEntity.DataEntity> upingEntry;
    private Map.Entry<String, KlineEntity.DataEntity> downingEntry;
    private Map.Entry<String, KlineEntity.DataEntity> upEntry;
    private Map.Entry<String, KlineEntity.DataEntity> downEntry;
    private float ajustFactor = 0.835f;


    public Map<String, KlineEntity.DataEntity> getResultEntities(Map<String, KlineEntity.DataEntity> entityMap) {
        step = Float.parseFloat(LocalDataManager.step);
        if (entityMap != null && !entityMap.isEmpty()) {
            Iterator<Map.Entry<String, KlineEntity.DataEntity>> iterator = entityMap.entrySet().iterator();
            currentEntry = iterator.next();
            currentEntry.getValue().setEntry_type(ENTRY_TYPE.UPING);
            iterator.remove();
            getUpStep(iterator);
        }
        return resultEntities;
    }

    public float getStepByStepType() {
        return step * ajustFactor;
    }


    public float getStepByStepTypeHalf() {
        return step / 2.0f;
    }

    private void getUpStep(Iterator<Map.Entry<String, KlineEntity.DataEntity>> iterator) {

    }

    private void resetTrend() {
        //趋势逆转时，把前面的调整阶段的关键点置为空
        upEntry = null;
        downEntry = null;
        upingEntry = null;
        downingEntry = null;
    }

}
