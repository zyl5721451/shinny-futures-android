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

    private void getUpStep(Iterator<Map.Entry<String, KlineEntity.DataEntity>> iterator) {//自然上升
        while (iterator.hasNext()) {
            Map.Entry<String, KlineEntity.DataEntity> entry = iterator.next();//第一个点下降趋势
            KlineEntity.DataEntity dataEntity = entry.getValue();
            KlineEntity.DataEntity currentEntity = currentEntry.getValue();
            float dataClose = Float.parseFloat(dataEntity.getClose());
            float currentClose = Float.parseFloat(currentEntity.getClose());
            switch (currentEntity.getEntry_type()) {
                case DONWING://下降趋势
                    if (dataClose < currentClose) {//继续下降
                        dataEntity.setEntry_type(ENTRY_TYPE.DONWING);

                        currentEntry = entry;
                        resultEntities.put(currentEntry.getKey(), currentEntry.getValue());
                        iterator.remove();
                    } else if (dataClose >= currentClose + getStepByStepType()) {//上升6点
                        dataEntity.setEntry_type(ENTRY_TYPE.NORMALUP);
                        downingEntry = currentEntry;//记录下降趋势最低点
                        currentEntity.setLine_color_type(LINE_COLOR_TYPE.TYPE_DOWNING);

                        currentEntry = entry;
                        resultEntities.put(currentEntry.getKey(), currentEntry.getValue());
                        iterator.remove();
                    }
                    break;
                case UPING://上升趋势
                    if (dataClose > currentClose) {//继续上升
                        dataEntity.setEntry_type(ENTRY_TYPE.UPING);

                        currentEntry = entry;
                        resultEntities.put(currentEntry.getKey(), currentEntry.getValue());
                        iterator.remove();
                    } else if (dataClose <= currentClose - getStepByStepType()) {//下降6个点
                        dataEntity.setEntry_type(ENTRY_TYPE.NORMALDOWN);
                        upingEntry = currentEntry;//记录上升趋势最高点
                        currentEntity.setLine_color_type(LINE_COLOR_TYPE.TYPE_UPING);

                        currentEntry = entry;
                        resultEntities.put(currentEntry.getKey(), currentEntry.getValue());
                        iterator.remove();
                    }
                    break;
                case LOWDOWN://次级下降
                    if (dataClose < currentClose) {//继续下降
                        if (downingEntry != null && dataClose <= Float.parseFloat(downingEntry.getValue().getClose())) {//超过上一次下降趋势最低点，记录下降趋势
                            resetTrend();
                            dataEntity.setEntry_type(ENTRY_TYPE.DONWING);

                            currentEntry = entry;
                            resultEntities.put(currentEntry.getKey(), currentEntry.getValue());
                            iterator.remove();
                        } else if (upingEntry != null && downEntry != null && dataClose <= Float.parseFloat(downEntry.getValue().getClose()) - getStepByStepTypeHalf()) {
                            resetTrend();
                            dataEntity.setEntry_type(ENTRY_TYPE.DONWING);

                            currentEntry = entry;
                            resultEntities.put(currentEntry.getKey(), currentEntry.getValue());
                            iterator.remove();
                        } else if (downEntry != null && dataClose <= Float.parseFloat(downEntry.getValue().getClose())) {//超过上一次自然下降最低点，记录自然下降
                            //小于downEntry,并且3点以上，记入下降趋势，没有超过3点，记入自然下降
                            dataEntity.setEntry_type(ENTRY_TYPE.NORMALDOWN);

                            currentEntry = entry;
                            resultEntities.put(currentEntry.getKey(), currentEntry.getValue());
                            iterator.remove();
                        } else {//其它情况，还是次级下降
                            dataEntity.setEntry_type(ENTRY_TYPE.LOWDOWN);

                            currentEntry = entry;
                            resultEntities.put(currentEntry.getKey(), currentEntry.getValue());
                            iterator.remove();
                        }
                    } else if (dataClose >= currentClose && upingEntry != null && dataClose > Float.parseFloat(upingEntry.getValue().getClose())) {
                        resetTrend();
                        currentEntity.setLine_color_type(LINE_COLOR_TYPE.TYPE_LOW_DOWN);
                        dataEntity.setEntry_type(ENTRY_TYPE.UPING);

                        currentEntry = entry;
                        resultEntities.put(currentEntry.getKey(), currentEntry.getValue());
                        iterator.remove();
                    } else if (downingEntry != null && dataClose >= currentClose && upEntry != null && dataClose >= Float.parseFloat(upEntry.getValue().getClose()) + getStepByStepTypeHalf()) {//上升且超过上一个关键点3点及以上
                        resetTrend();
                        currentEntity.setLine_color_type(LINE_COLOR_TYPE.TYPE_LOW_DOWN);
                        dataEntity.setEntry_type(ENTRY_TYPE.UPING);

                        currentEntry = entry;
                        resultEntities.put(currentEntry.getKey(), currentEntry.getValue());
                        iterator.remove();
                    } else if (dataClose >= currentClose + getStepByStepType()) { //上升
                        currentEntity.setLine_color_type(LINE_COLOR_TYPE.TYPE_LOW_DOWN);
                        if (upEntry != null && dataClose > Float.parseFloat(upEntry.getValue().getClose())) {//超过自然上升最高点，记录自然上升
                            dataEntity.setEntry_type(ENTRY_TYPE.NORMALUP);

                            currentEntry = entry;
                            resultEntities.put(currentEntry.getKey(), currentEntry.getValue());
                            iterator.remove();
                        } else {//记入次级上升
                            dataEntity.setEntry_type(ENTRY_TYPE.LOWUP);

                            currentEntry = entry;
                            resultEntities.put(currentEntry.getKey(), currentEntry.getValue());
                            iterator.remove();
                        }
                    }
                    break;
                case LOWUP://次级上升
                    if (dataClose > currentClose) {//继续上升
                        if (upingEntry != null && dataClose >= Float.parseFloat(upingEntry.getValue().getClose())) {//超过上一次上升趋势最低点，记录上升趋势
                            resetTrend();
                            dataEntity.setEntry_type(ENTRY_TYPE.UPING);

                            currentEntry = entry;
                            resultEntities.put(currentEntry.getKey(), currentEntry.getValue());
                            iterator.remove();
                        } else if (downingEntry != null && upEntry != null && dataClose >= Float.parseFloat(upEntry.getValue().getClose()) + getStepByStepTypeHalf()) {
                            resetTrend();
                            dataEntity.setEntry_type(ENTRY_TYPE.UPING);

                            currentEntry = entry;
                            resultEntities.put(currentEntry.getKey(), currentEntry.getValue());
                            iterator.remove();
                        } else if (upEntry != null && dataClose >= Float.parseFloat(upEntry.getValue().getClose())) {//超过上一次自然上升最低点，记录自然上升
                            //超过upEntry，并且3点以上。记入上升趋势，没有超过3点，记入自然上升
                            dataEntity.setEntry_type(ENTRY_TYPE.NORMALUP);

                            currentEntry = entry;
                            resultEntities.put(currentEntry.getKey(), currentEntry.getValue());
                            iterator.remove();
                        } else {//其它情况，还是次级上升
                            dataEntity.setEntry_type(ENTRY_TYPE.LOWUP);

                            currentEntry = entry;
                            resultEntities.put(currentEntry.getKey(), currentEntry.getValue());
                            iterator.remove();
                        }
                    } else if (dataClose <= currentClose && downingEntry != null && dataClose < Float.parseFloat(downingEntry.getValue().getClose())) {
                        resetTrend();
                        currentEntity.setLine_color_type(LINE_COLOR_TYPE.TYPE_LOW_UP);
                        dataEntity.setEntry_type(ENTRY_TYPE.DONWING);

                        currentEntry = entry;
                        resultEntities.put(currentEntry.getKey(), currentEntry.getValue());
                        iterator.remove();
                    } else if (upingEntry != null && dataClose <= currentClose && downEntry != null && dataClose <= Float.parseFloat(downEntry.getValue().getClose()) - getStepByStepTypeHalf()) {//下降且超过上一个关键点3点及以上
                        resetTrend();
                        currentEntity.setLine_color_type(LINE_COLOR_TYPE.TYPE_LOW_UP);
                        dataEntity.setEntry_type(ENTRY_TYPE.DONWING);

                        currentEntry = entry;
                        resultEntities.put(currentEntry.getKey(), currentEntry.getValue());
                        iterator.remove();
                    } else if (dataClose <= currentClose - getStepByStepType()) { //下降
                        currentEntity.setLine_color_type(LINE_COLOR_TYPE.TYPE_LOW_UP);
                        if (downEntry != null && dataClose < Float.parseFloat(downEntry.getValue().getClose())) {//超过自然下降最高点，记录自然下降
                            dataEntity.setEntry_type(ENTRY_TYPE.NORMALDOWN);

                            currentEntry = entry;
                            resultEntities.put(currentEntry.getKey(), currentEntry.getValue());
                            iterator.remove();
                        } else {//记入次级下降
                            dataEntity.setEntry_type(ENTRY_TYPE.LOWDOWN);

                            currentEntry = entry;
                            resultEntities.put(currentEntry.getKey(), currentEntry.getValue());
                            iterator.remove();
                        }
                    }

                    break;
                case NORMALUP://自然上升
                    if (dataClose > currentClose) {//继续上升
                        if (upingEntry != null && dataClose > Float.parseFloat(upingEntry.getValue().getClose())) { //超过上升趋势，记录上升趋势
                            resetTrend();
                            dataEntity.setEntry_type(ENTRY_TYPE.UPING);

                            currentEntry = entry;
                            resultEntities.put(currentEntry.getKey(), currentEntry.getValue());
                            iterator.remove();
                        } else if (downingEntry != null && upEntry != null && dataClose > Float.parseFloat(upEntry.getValue().getClose()) + getStepByStepTypeHalf()) {//超过上一个自然上升的最高点，3点，则记入上升趋势
                            resetTrend();
                            dataEntity.setEntry_type(ENTRY_TYPE.UPING);

                            currentEntry = entry;
                            resultEntities.put(currentEntry.getKey(), currentEntry.getValue());
                            iterator.remove();
                        } else {//其它自然上升
                            dataEntity.setEntry_type(ENTRY_TYPE.NORMALUP);

                            currentEntry = entry;
                            resultEntities.put(currentEntry.getKey(), currentEntry.getValue());
                            iterator.remove();
                        }
                    } else if (dataClose <= currentClose && downingEntry != null && dataClose <= Float.parseFloat(downingEntry.getValue().getClose())) {
                        upEntry = currentEntry;//记录上一个自然上升最高点
                        currentEntity.setLine_color_type(LINE_COLOR_TYPE.TYPE_NORMAL_UP);
                        resetTrend();
                        dataEntity.setEntry_type(ENTRY_TYPE.DONWING);

                        currentEntry = entry;
                        resultEntities.put(currentEntry.getKey(), currentEntry.getValue());
                        iterator.remove();
                    } else if (upingEntry != null && dataClose <= currentClose && downEntry != null && dataClose <= Float.parseFloat(downEntry.getValue().getClose()) - getStepByStepTypeHalf()) {
                        upEntry = currentEntry;//记录上一个自然上升最高点
                        currentEntity.setLine_color_type(LINE_COLOR_TYPE.TYPE_NORMAL_UP);
                        resetTrend();
                        dataEntity.setEntry_type(ENTRY_TYPE.DONWING);

                        currentEntry = entry;
                        resultEntities.put(currentEntry.getKey(), currentEntry.getValue());
                        iterator.remove();
                    } else if (dataClose <= currentClose - getStepByStepType()) {//下降
                        upEntry = currentEntry;//记录上一个自然上升最高点
                        currentEntity.setLine_color_type(LINE_COLOR_TYPE.TYPE_NORMAL_UP);
                        if (downEntry != null && dataClose > Float.parseFloat(downEntry.getValue().getClose())) {
                            dataEntity.setEntry_type(ENTRY_TYPE.LOWDOWN);//次级下降

                            currentEntry = entry;
                            resultEntities.put(currentEntry.getKey(), currentEntry.getValue());
                            iterator.remove();
                        } else {
                            dataEntity.setEntry_type(ENTRY_TYPE.NORMALDOWN);//自然下降

                            currentEntry = entry;
                            resultEntities.put(currentEntry.getKey(), currentEntry.getValue());
                            iterator.remove();
                        }
                    }
                    break;
                case NORMALDOWN://自然下降
                    if (dataClose < currentClose) {//继续下降
                        if (downingEntry != null && dataClose < Float.parseFloat(downingEntry.getValue().getClose())) { //超过下降趋势，记录下降趋势
                            resetTrend();
                            dataEntity.setEntry_type(ENTRY_TYPE.DONWING);

                            currentEntry = entry;
                            resultEntities.put(currentEntry.getKey(), currentEntry.getValue());
                            iterator.remove();
                        } else if (upingEntry != null && downEntry != null && dataClose <= Float.parseFloat(downEntry.getValue().getClose()) - getStepByStepTypeHalf()) {//下降超过上一次自然下降的最低点3点以上，则记入下降趋势
                            //此条只有下降趋势为空时才会生效
                            resetTrend();
                            dataEntity.setEntry_type(ENTRY_TYPE.DONWING);

                            currentEntry = entry;
                            resultEntities.put(currentEntry.getKey(), currentEntry.getValue());
                            iterator.remove();
                        } else {//其它自然下降
                            dataEntity.setEntry_type(ENTRY_TYPE.NORMALDOWN);

                            currentEntry = entry;
                            resultEntities.put(currentEntry.getKey(), currentEntry.getValue());
                            iterator.remove();
                        }
                    } else if (dataClose >= currentClose && upingEntry != null && dataClose >= Float.parseFloat(upingEntry.getValue().getClose())) {
                        downEntry = currentEntry;//记录上一个自然下降最高点
                        currentEntity.setLine_color_type(LINE_COLOR_TYPE.TYPE_NORMAL_DOWN);
                        resetTrend();
                        dataEntity.setEntry_type(ENTRY_TYPE.UPING);

                        currentEntry = entry;
                        resultEntities.put(currentEntry.getKey(), currentEntry.getValue());
                        iterator.remove();
                    } else if (downingEntry != null && dataClose >= currentClose && upEntry != null && dataClose >= Float.parseFloat(upEntry.getValue().getClose()) + getStepByStepTypeHalf()) {
                        downEntry = currentEntry;//记录上一个自然下降最高点
                        currentEntity.setLine_color_type(LINE_COLOR_TYPE.TYPE_NORMAL_DOWN);
                        resetTrend();
                        dataEntity.setEntry_type(ENTRY_TYPE.UPING);

                        currentEntry = entry;
                        resultEntities.put(currentEntry.getKey(), currentEntry.getValue());
                        iterator.remove();
                    } else if (dataClose >= currentClose + getStepByStepType()) {//6点转向上升
                        downEntry = currentEntry;//记录上一个自然下降最高点
                        currentEntity.setLine_color_type(LINE_COLOR_TYPE.TYPE_NORMAL_DOWN);
                        if (upEntry != null && dataClose < Float.parseFloat(upEntry.getValue().getClose())) {//次级上升
                            dataEntity.setEntry_type(ENTRY_TYPE.LOWUP);

                            currentEntry = entry;
                            resultEntities.put(currentEntry.getKey(), currentEntry.getValue());
                            iterator.remove();
                        } else {
                            dataEntity.setEntry_type(ENTRY_TYPE.NORMALUP);//自然上升

                            currentEntry = entry;
                            resultEntities.put(currentEntry.getKey(), currentEntry.getValue());
                            iterator.remove();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void resetTrend() {
        //趋势逆转时，把前面的调整阶段的关键点置为空
        upEntry = null;
        downEntry = null;
        upingEntry = null;
        downingEntry = null;
    }

}
