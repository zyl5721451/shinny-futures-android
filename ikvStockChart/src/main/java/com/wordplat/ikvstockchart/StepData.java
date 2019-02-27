package com.wordplat.ikvstockchart;

import android.text.TextUtils;
import android.util.Log;

import com.wordplat.ikvstockchart.entry.Entry;
import com.wordplat.ikvstockchart.entry.EntrySet;
import com.wordplat.ikvstockchart.entry.FIRST_ENTRY_TYPE;
import com.wordplat.ikvstockchart.entry.LINE_COLOR_TYPE;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by allenzhang on 2019/2/24.
 */
public class StepData {
    private float ajustFactor = 0.935f;
    private float step;

    public float getStepByStepType(float close) {
        float floatStep = step;
        return close*floatStep/100.0f;
    }

    public float getStepByStepTypeHalf(float close) {
        float floatStep = step/2.0f;
        return close*floatStep/100.0f;
    }


    public StepData() {
        this.step = Float.parseFloat(LocalDataManager.step);
    }

    private EntrySet tempEntrySeet = new EntrySet();
    private Entry currentEntry;
    private static FIRST_ENTRY_TYPE sFirstEntry = FIRST_ENTRY_TYPE.UPING;

    private Entry upingEntry;
    private Entry downingEntry;
    private Entry upEntry;
    private Entry downEntry;
    private EntrySet forEntrySet = new EntrySet();
    private boolean isCheckAccurance;
    private String name;

    public EntrySet getAnylysisData(String name, EntrySet entrySet, FIRST_ENTRY_TYPE first_entry_type) {
        step = Float.parseFloat(LocalDataManager.step);
        this.name = name;
        this.isCheckAccurance = false;
        this.sFirstEntry = first_entry_type;
        getStepEntrySet(entrySet);
        return tempEntrySeet;
    }



    private void getStepEntrySet(EntrySet entrySet) {
        if (entrySet != null && !entrySet.getEntryList().isEmpty()) {
            entrySet.getEntryList().get(0).setEntry_type(sFirstEntry);
            currentEntry = entrySet.getEntryList().get(0);
            tempEntrySeet.addEntry(currentEntry);
            forEntrySet.addEntries(entrySet.getEntryList());
            getUpStep();
//            getStep();
        }
    }







    private void getUpStep() {
        int initi = 0;
        int i = 1;
        for (; i < forEntrySet.getEntryList().size(); i++) {
            Entry entry = forEntrySet.getEntryList().get(i);
            switch (currentEntry.getEntry_type()) {
                case DONWING:
                    if(entry.getClose()<currentEntry.getClose()) {
                        entry.setEntry_type(FIRST_ENTRY_TYPE.DONWING);

                        getNextCircle(i, entry);
                        i = initi;
                    }else if(entry.getClose()>=currentEntry.getClose()+getStepByStepType(currentEntry.getClose())){
                        entry.setEntry_type(FIRST_ENTRY_TYPE.NORMALUP);
                        downingEntry = currentEntry;
                        currentEntry.setLine_type(LINE_COLOR_TYPE.TYPE_DOWNING);
                        getNextCircle(i, entry);
                        i = initi;
                    }
                    break;
                case UPING:
                    if(entry.getClose()>currentEntry.getClose()) {
                        entry.setEntry_type(FIRST_ENTRY_TYPE.UPING);
                        getNextCircle(i,entry);
                        i = initi;
                    }else if(entry.getClose()<=currentEntry.getClose()-getStepByStepType(currentEntry.getClose())) {
                        entry.setEntry_type(FIRST_ENTRY_TYPE.NORMALDOWN);
                        upingEntry = currentEntry;
                        currentEntry.setLine_type(LINE_COLOR_TYPE.TYPE_UPING);
                        getNextCircle(i,entry);
                        i = initi;
                    }
                    break;
                case LOWDOWN:
                    if(entry.getClose()<currentEntry.getClose()) {
                        if(downingEntry!=null&&entry.getClose()<=downingEntry.getClose()) {
                            resetTrend();
                            entry.setEntry_type(FIRST_ENTRY_TYPE.DONWING);
                            getNextCircle(i,entry);
                            i = initi;
                        } else if(upingEntry!=null&&downEntry!=null&&(isCheckAccurance?entry.getLow():entry.getClose())<=downEntry.getClose()-getStepByStepTypeHalf(downEntry.getClose())) {
                            resetTrend();
                            entry.setEntry_type(FIRST_ENTRY_TYPE.DONWING);
                            getNextCircle(i,entry);
                            i = initi;
                        }else if(downEntry!=null&&entry.getClose()<=downEntry.getClose()) {
                            entry.setEntry_type(FIRST_ENTRY_TYPE.NORMALDOWN);
                            getNextCircle(i,entry);
                            i = initi;
                        } else {
                            entry.setEntry_type(FIRST_ENTRY_TYPE.LOWDOWN);
                            getNextCircle(i,entry);
                            i = initi;
                        }
                    }else if(entry.getClose()>=currentEntry.getClose()&&upingEntry!=null&&entry.getClose()>upingEntry.getClose()) {
                        resetTrend();
                        currentEntry.setLine_type(LINE_COLOR_TYPE.TYPE_LOW_DOWN);
                        entry.setEntry_type(FIRST_ENTRY_TYPE.UPING);
                        getNextCircle(i,entry);
                        i = initi;
                    }else if(downingEntry!=null&&entry.getClose()>=currentEntry.getClose()&&upEntry!=null&&(isCheckAccurance?entry.getHigh():entry.getClose())>=upEntry.getClose() + getStepByStepTypeHalf(upEntry.getClose())) {//上升且超过上一个关键点3点及以上
                        resetTrend();
                        currentEntry.setLine_type(LINE_COLOR_TYPE.TYPE_LOW_DOWN);
                        entry.setEntry_type(FIRST_ENTRY_TYPE.UPING);
                        getNextCircle(i,entry);
                        i = initi;
                    }  else if(entry.getClose()>=currentEntry.getClose()+getStepByStepType(currentEntry.getClose())){
                        currentEntry.setLine_type(LINE_COLOR_TYPE.TYPE_LOW_DOWN);
                        if(upEntry!=null&&entry.getClose()>upEntry.getClose()) {
                            entry.setEntry_type(FIRST_ENTRY_TYPE.NORMALUP);
                            getNextCircle(i,entry);
                            i = initi;
                        }else {
                            entry.setEntry_type(FIRST_ENTRY_TYPE.LOWUP);
                            getNextCircle(i,entry);
                            i = initi;
                        }
                    }
                    break;
                case LOWUP:
                    if(entry.getClose()>currentEntry.getClose()) {
                        if(upingEntry!=null&&entry.getClose()>=upingEntry.getClose()) {
                            resetTrend();
                            entry.setEntry_type(FIRST_ENTRY_TYPE.UPING);
                            getNextCircle(i,entry);
                            i = initi;
                        } else if(downingEntry!=null&&upEntry!=null&&(isCheckAccurance?entry.getHigh():entry.getClose())>=upEntry.getClose()+getStepByStepTypeHalf(upEntry.getClose())) {
                            resetTrend();
                            entry.setEntry_type(FIRST_ENTRY_TYPE.UPING);
                            getNextCircle(i,entry);
                            i = initi;
                        } else if(upEntry!=null&&entry.getClose()>=upEntry.getClose()) {
                            entry.setEntry_type(FIRST_ENTRY_TYPE.NORMALUP);
                            getNextCircle(i,entry);
                            i = initi;
                        }else {
                            entry.setEntry_type(FIRST_ENTRY_TYPE.LOWUP);
                            getNextCircle(i,entry);
                            i = initi;
                        }
                    }else if(entry.getClose()<=currentEntry.getClose()&&downingEntry!=null&&entry.getClose()<downingEntry.getClose()) {
                        resetTrend();
                        currentEntry.setLine_type(LINE_COLOR_TYPE.TYPE_LOW_UP);
                        entry.setEntry_type(FIRST_ENTRY_TYPE.DONWING);
                        getNextCircle(i,entry);
                        i = initi;
                    } else if(upingEntry!=null&&entry.getClose()<=currentEntry.getClose()&&downEntry!=null&&(isCheckAccurance?entry.getLow():entry.getClose())<=downEntry.getClose() - getStepByStepTypeHalf(downEntry.getClose())) {//下降且超过上一个关键点3点及以上
                        resetTrend();
                        currentEntry.setLine_type(LINE_COLOR_TYPE.TYPE_LOW_UP);
                        entry.setEntry_type(FIRST_ENTRY_TYPE.DONWING);
                        getNextCircle(i,entry);
                        i = initi;
                    } else if(entry.getClose()<=currentEntry.getClose()-getStepByStepType(currentEntry.getClose())){
                        currentEntry.setLine_type(LINE_COLOR_TYPE.TYPE_LOW_UP);
                        if(downEntry!=null&&entry.getClose()<downEntry.getClose()) {
                            entry.setEntry_type(FIRST_ENTRY_TYPE.NORMALDOWN);
                            getNextCircle(i,entry);
                            i = initi;
                        }else {
                            entry.setEntry_type(FIRST_ENTRY_TYPE.LOWDOWN);
                            getNextCircle(i,entry);
                            i = initi;
                        }
                    }

                    break;
                case NORMALUP:
                    if(entry.getClose()>currentEntry.getClose()) {
                        if(upingEntry!=null&&entry.getClose()>upingEntry.getClose()) {
                            resetTrend();
                            entry.setEntry_type(FIRST_ENTRY_TYPE.UPING);
                            getNextCircle(i,entry);
                            i = initi;
                        }else if(downingEntry!=null&&upEntry!=null&&(isCheckAccurance?entry.getHigh():entry.getClose())>upEntry.getClose()+getStepByStepTypeHalf(upEntry.getClose())) {//超过上一个自然上升的最高点，3点，则记入上升趋势
                            resetTrend();
                            entry.setEntry_type(FIRST_ENTRY_TYPE.UPING);
                            getNextCircle(i,entry);
                            i = initi;
                        } else {
                            entry.setEntry_type(FIRST_ENTRY_TYPE.NORMALUP);
                            getNextCircle(i,entry);
                            i = initi;
                        }
                    }else if(entry.getClose()<=currentEntry.getClose()&&downingEntry!=null&&entry.getClose()<=downingEntry.getClose()) {
                        upEntry = currentEntry;
                        currentEntry.setLine_type(LINE_COLOR_TYPE.TYPE_NORMAL_UP);
                        resetTrend();
                        entry.setEntry_type(FIRST_ENTRY_TYPE.DONWING);
                        getNextCircle(i,entry);
                        i = initi;
                    }else if(upingEntry!=null&&entry.getClose()<=currentEntry.getClose()&&downEntry!=null&&(isCheckAccurance?entry.getLow():entry.getClose())<=downEntry.getClose()-getStepByStepTypeHalf(downEntry.getClose())) {
                        upEntry = currentEntry;
                        currentEntry.setLine_type(LINE_COLOR_TYPE.TYPE_NORMAL_UP);
                        resetTrend();
                        entry.setEntry_type(FIRST_ENTRY_TYPE.DONWING);
                        getNextCircle(i,entry);
                        i = initi;
                    }
                    else if(entry.getClose()<=currentEntry.getClose() - getStepByStepType(currentEntry.getClose())){
                        upEntry = currentEntry;
                        currentEntry.setLine_type(LINE_COLOR_TYPE.TYPE_NORMAL_UP);
                        if(downEntry!=null&&entry.getClose()>downEntry.getClose()) {
                            entry.setEntry_type(FIRST_ENTRY_TYPE.LOWDOWN);
                            getNextCircle(i,entry);
                            i = initi;
                        } else {
                            entry.setEntry_type(FIRST_ENTRY_TYPE.NORMALDOWN);
                            getNextCircle(i,entry);
                            i = initi;
                        }
                    }
                    break;
                case NORMALDOWN:
                    if(entry.getClose()<currentEntry.getClose()) {
                        if(downingEntry!=null&&entry.getClose()<downingEntry.getClose()) {
                            resetTrend();
                            entry.setEntry_type(FIRST_ENTRY_TYPE.DONWING);
                            getNextCircle(i,entry);
                            i = initi;
                        }else if(upingEntry!=null&&downEntry!=null&&(isCheckAccurance?entry.getLow():entry.getClose()) <=downEntry.getClose() - getStepByStepTypeHalf(downEntry.getClose())){//下降超过上一次自然下降的最低点3点以上，则记入下降趋势
                            resetTrend();
                            entry.setEntry_type(FIRST_ENTRY_TYPE.DONWING);
                            getNextCircle(i,entry);
                            i = initi;
                        } else {
                            entry.setEntry_type(FIRST_ENTRY_TYPE.NORMALDOWN);
                            getNextCircle(i,entry);
                            i = initi;
                        }
                    }else if(entry.getClose()>=currentEntry.getClose()&&upingEntry!=null&&entry.getClose()>=upingEntry.getClose()) {
                        downEntry = currentEntry;
                        currentEntry.setLine_type(LINE_COLOR_TYPE.TYPE_NORMAL_DOWN);
                        resetTrend();
                        entry.setEntry_type(FIRST_ENTRY_TYPE.UPING);
                        getNextCircle(i,entry);
                        i = initi;
                    }else if(downingEntry!=null&&entry.getClose()>=currentEntry.getClose()&&upEntry!=null&&(isCheckAccurance?entry.getHigh():entry.getClose())>=upEntry.getClose()+getStepByStepTypeHalf(upEntry.getClose())) {
                        downEntry = currentEntry;
                        currentEntry.setLine_type(LINE_COLOR_TYPE.TYPE_NORMAL_DOWN);
                        resetTrend();
                        entry.setEntry_type(FIRST_ENTRY_TYPE.UPING);
                        getNextCircle(i,entry);
                        i = initi;
                    }
                    else if(entry.getClose()>=currentEntry.getClose() + getStepByStepType(currentEntry.getClose())){
                        downEntry = currentEntry;
                        currentEntry.setLine_type(LINE_COLOR_TYPE.TYPE_NORMAL_DOWN);
                        if(upEntry!=null&&entry.getClose()<upEntry.getClose()) {
                            entry.setEntry_type(FIRST_ENTRY_TYPE.LOWUP);
                            getNextCircle(i,entry);
                            i = initi;
                        }else {
                            entry.setEntry_type(FIRST_ENTRY_TYPE.NORMALUP);
                            getNextCircle(i,entry);
                            i = initi;
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }


    private void resetTrend() {
        upEntry = null;
        downEntry = null;
        upingEntry = null;
        downingEntry = null;
    }

    private void getNextCircle(int i, Entry entry) {
        List<Entry> list;
        currentEntry = entry;
        tempEntrySeet.addEntry(currentEntry);
        if (i < forEntrySet.getEntryList().size() - 1) {
            list = forEntrySet.subEntry(i, forEntrySet.getEntryList().size());
        } else {
            list = new ArrayList<Entry>();
        }
        EntrySet entrySet1 = new EntrySet();
        entrySet1.addEntries(list);
        forEntrySet = entrySet1;
    }


}
