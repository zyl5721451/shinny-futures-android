package com.shinnytech.futures.model.livem;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.LineData;
import com.shinnytech.futures.R;
import com.shinnytech.futures.application.BaseApplication;
import com.shinnytech.futures.constants.CommonConstants;
import com.shinnytech.futures.controller.fragment.BaseChartFragment;
import com.shinnytech.futures.controller.fragment.KlineFragment;
import com.shinnytech.futures.model.bean.eventbusbean.IdEvent;
import com.shinnytech.futures.model.bean.eventbusbean.KlineEvent;
import com.shinnytech.futures.model.bean.eventbusbean.SetUpEvent;
import com.shinnytech.futures.model.bean.futureinfobean.ChartEntity;
import com.shinnytech.futures.model.bean.futureinfobean.KlineEntity;
import com.shinnytech.futures.model.bean.searchinfobean.SearchEntity;
import com.shinnytech.futures.model.engine.LatestFileManager;
import com.shinnytech.futures.utils.LogUtils;
import com.wordplat.ikvstockchart.BaseFragment;
import com.wordplat.ikvstockchart.InteractiveKLineLayout;
import com.wordplat.ikvstockchart.KLineHandler;
import com.wordplat.ikvstockchart.StepData;
import com.wordplat.ikvstockchart.drawing.CandleLineDrawing;
import com.wordplat.ikvstockchart.entry.Entry;
import com.wordplat.ikvstockchart.entry.EntrySet;
import com.wordplat.ikvstockchart.entry.FIRST_ENTRY_TYPE;
import com.wordplat.ikvstockchart.entry.SizeColor;
import com.wordplat.ikvstockchart.render.KLineRender;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.channels.FileLock;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.shinnytech.futures.constants.CommonConstants.CHART_ID;
import static com.shinnytech.futures.constants.CommonConstants.VIEW_WIDTH;

/**
 * <p>KLineFragment</p>
 * <p>Date: 2017/4/5</p>
 *
 * @author afon
 */

public class KLineFragment extends BaseChartFragment {
    private static final String TAG = "KLineFragment";
    private SimpleDateFormat simpleDateFormathour = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    private SimpleDateFormat simpleDateFormatday = new SimpleDateFormat("yyyyMMdd");

   private InteractiveKLineLayout kLineLayout = null;
   private TextView MA_Text = null;
    private TextView StockIndex_Text = null;
    private TextView Volume_Text = null;
    private static final String STEP_TYPE_NUM  = "STEP_TYPE_NUM";
    private static final String STEP_TYPE_PER  = "STEP_TYPE_PER";
    private static String STOCK_CODE = "600030"; // 中信证券
    private static final int REQUEST_STOCK_FIRST = 1;
    private static final int REQUEST_STOCK_PREV = 2;
    private static final int REQUEST_STOCK_NEXT = 3;
    private static FIRST_ENTRY_TYPE sFirstEntry = FIRST_ENTRY_TYPE.UPING;
    private static float mScaleX = 0.0f;

    private Entry upingEntry;
    private Entry downingEntry;
    private Entry upEntry;
    private Entry downEntry;


    private EntrySet entrySet = new EntrySet();

    private String code;
    private String startData;
    private String endData;

    private EntrySet tempEntrySeet = new EntrySet();
    private Entry currentEntry;
    private EntrySet forEntrySet = new EntrySet();
    private String step;
    private int firstType;

    private EntrySet dayEntrySet = new EntrySet();
    private EntrySet weekEntrySet = new EntrySet();
    private String dayStep;
    private String weekStep;
    private String stepType;
    private boolean isCheckAccurance;
    private String name;
    private String monthName;
    private boolean isCandleLine = true;
    private CandleLineDrawing mCandleLineDrawing;
    public static String factor;
    public StepData mAnalysisData;
    /**
     * date: 2018/11/19
     * description: 最新价线
     */
    protected Map<String, LimitLine> mLatestLimitLines;


    /**
     * date: 7/9/17
     * description: X轴的显示格式，“年/月”--“2017/07”、“月/日”--“07/09”
     */
    private static final String FRAGMENT_XVALS_FORMAT = "fragment_format";

    /**
     * date: 2018/12/17
     * description: 页面类型
     */
    private static final String FRAGMENT_TYPE = "fragment_type";

    /**
     * date: 7/9/17
     * description: K线图类型--日线、小时线、5分钟线
     */
    private static final String FRAGMENT_KLINE_TYPE = "kline_type";
    private ChartEntity mChartEntity;
    private KlineEntity mKlineEntity;
    private int mBaseIndex;
    private int mLeftIndex;
    private int mRightIndex;
    private int mLastIndex;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kline,container,false);
        kLineLayout = view.findViewById(R.id.kLineLayout);
        MA_Text = view.findViewById(R.id.MA_Text);
        StockIndex_Text = view.findViewById(R.id.StockIndex_Text);
        Volume_Text = view.findViewById(R.id.Volume_Text);
        EventBus.getDefault().register(this);
        initData();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String mXValsFormat = getArguments().getString(FRAGMENT_XVALS_FORMAT);
        mFragmentType = getArguments().getString(FRAGMENT_TYPE);
        mKlineType = getArguments().getString(FRAGMENT_KLINE_TYPE);
        if (mXValsFormat != null)
            mSimpleDateFormat = new SimpleDateFormat(mXValsFormat, Locale.CHINA);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        code = bundle.getString("code");
        startData = bundle.getString("startdata");
        endData = bundle.getString("enddata");
        step = bundle.getString("step");
        firstType = bundle.getInt("firsttype");
        stepType = bundle.getString("steptype");
        isCheckAccurance  = bundle.getBoolean("accurance");
        name = bundle.getString("name");
        factor = bundle.getString("factor");
        isCandleLine = bundle.getBoolean("candleline",true);
        initFirstType();
        initUI();


        if(name == null) {
            name = "";
        }

    }

    @Override
    protected void refreshKline() {
        super.refreshKline();
        try {
            //开始加载数据
            if (mAnalysisData!=null&&entrySet.getEntryList()!=null&&!entrySet.getEntryList().isEmpty()) {
                String last_id_t = mKlineEntity.getLast_id();
                int last_index_t = Integer.parseInt(last_id_t);
                Map<String, KlineEntity.DataEntity> dataEntities = mKlineEntity.getData();

//                if (right_index_t == mRightIndex && left_index_t == mLeftIndex) {
//                    KlineEntity.DataEntity dataEntity = dataEntities.get(last_id_t);
//                    if (dataEntity == null) return;
//                    LogUtils.e("单个柱子刷新", false);
//                    candleData.removeEntry(mLastIndex - mBaseIndex, 0);
//                } else if (right_index_t > mRightIndex && left_index_t > mLeftIndex) {
//                    LogUtils.e("向后添加柱子", false);
//                } else if (left_index_t < mLeftIndex) {
//                    LogUtils.e("向前添加柱子", false);
//                }
            } else {
                LogUtils.e("K线图初始化", true);
                Map<String, KlineEntity> klineEntities = sDataManager.getRtnData().getKlines().get(instrument_id);
                mChartEntity = sDataManager.getRtnData().getCharts().get(CHART_ID);
                if (klineEntities == null || mChartEntity == null) return;
                String left_id_t = mChartEntity.getLeft_id();
                String right_id_t = mChartEntity.getRight_id();
                if (left_id_t == null || right_id_t == null) return;
                boolean mdhis_more_data = sDataManager.getRtnData().getMdhis_more_data();
                if ((left_id_t.equals("-1") && right_id_t.equals("-1")) || mdhis_more_data) return;
                String ins_list = mChartEntity.getState().get("ins_list");
                String duration = mChartEntity.getState().get("duration");
                if (ins_list == null || duration == null) return;
                if (!ins_list.equals(instrument_id) || !duration.equals(mKlineType)) return;
                mKlineEntity = klineEntities.get(mKlineType);
                if (mKlineEntity == null) return;


                mAnalysisData = new StepData();
//                Map<String, KlineEntity.DataEntity> tempEntities = mAnylysisData.getResultEntities(mKlineEntity.getData());
//                mKlineEntity.setData(tempEntities);
//                mKlineEntity.setLast_id(String.valueOf(tempEntities.size() - 1));
//                right_id_t = String.valueOf(tempEntities.size() - 1);


                String last_id_t = mKlineEntity.getLast_id();
                Map<String, KlineEntity.DataEntity> dataEntities = mKlineEntity.getData();
                if (last_id_t == null || "-1".equals(last_id_t) || dataEntities.isEmpty()) return;
                mBaseIndex = Integer.parseInt(left_id_t);
                mLeftIndex = Integer.parseInt(left_id_t);
                mRightIndex = Integer.parseInt(right_id_t);
                mLastIndex = Integer.parseInt(last_id_t);

                parseEntry(dataEntities);


                tempEntrySeet = mAnalysisData.getAnylysisData(name,entrySet,sFirstEntry);
                tempEntrySeet.computeStockIndex();
                kLineLayout.getKLineView().setEntrySet(tempEntrySeet);
                kLineLayout.getKLineView().notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parseEntry(Map<String, KlineEntity.DataEntity> dataEntities) {

        List<Map.Entry<String,KlineEntity.DataEntity>> list = new ArrayList<Map.Entry<String,KlineEntity.DataEntity>>(dataEntities.entrySet());
        Collections.sort(list,new Comparator<Map.Entry<String,KlineEntity.DataEntity>>() {

            @Override
            public int compare(Map.Entry<String, KlineEntity.DataEntity> o1, Map.Entry<String, KlineEntity.DataEntity> o2) {
                return Integer.parseInt(o1.getKey())>Integer.parseInt(o2.getKey())?1:-1;
            }
        });

        for(Map.Entry<String,KlineEntity.DataEntity> mapping:list){
            KlineEntity.DataEntity entity = mapping.getValue();
            float open = 0;
            try {
                open = Float.parseFloat(entity.getOpen());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            float high = 0;
            try {
                high = Float.parseFloat(entity.getHigh());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            float low = 0;
            try {
                low = Float.parseFloat(entity.getLow());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            float close = 0;
            try {
                close = Float.parseFloat(entity.getClose());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            long volume = 0;
            try {
                volume = Long.parseLong(entity.getVolume());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            long date = 0;

            try {
                date = Long.parseLong(entity.getDatetime());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            String datetime = "";
            if(mKlineType.equals(CommonConstants.KLINE_1_DAY)) {
                datetime = simpleDateFormatday.format(new Date(date/1000000));
            }else if(mKlineType.equals(CommonConstants.KLINE_1_HOUR)) {
                datetime = simpleDateFormathour.format(new Date(date/1000000));
            }
            entrySet.addEntry( new Entry(open,
                    high,
                    low,
                    close,
                    volume,
                    datetime));
        }
    }


    public String getMonthName() {
        return monthName;
    }

    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }

    public void setDayEntrySet(EntrySet set) {
        this.dayEntrySet = set;
    }
    public void setWeekEntrySet(EntrySet set) {
        this.weekEntrySet = set;
    }

    public void setDayStep(String dayStep) {
        this.dayStep = dayStep;
    }

    public void setWeekStep(String weekStep) {
        this.weekStep = weekStep;
    }
    public String getStepString() {
        return step;
    }

    private void initFirstType() {
        switch (firstType) {
            case 0:
                sFirstEntry = FIRST_ENTRY_TYPE.UPING;
                break;
            case 1:
                sFirstEntry = FIRST_ENTRY_TYPE.DONWING;
                break;
            case 2:
                sFirstEntry = FIRST_ENTRY_TYPE.NORMALUP;
                break;
            case 3:
                sFirstEntry = FIRST_ENTRY_TYPE.NORMALDOWN;
                break;
                default:
                    sFirstEntry = FIRST_ENTRY_TYPE.UPING;
                    break;
        }

    }

    private String getTrendName(FIRST_ENTRY_TYPE first_entry_type) {
        String name = "";
        switch (first_entry_type) {
            case DOWNINGSIGNAL:
                name = "降号";
                break;
            case UPINGSIGNAL:
                name = "升号";
                break;
            case NORMALDOWN:
                name = "自降";
                break;
            case NORMALUP:
                name = "自升";
                break;
            case LOWUP:
                name = "次升";
                break;
            case LOWDOWN:
                name = "次降";
                break;
            case DONWING:
                name = "下降";
                break;
            case UPING:
                name = "上升";
                break;
            default:
                break;

        }
        return name;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    protected void initUI() {
        if(TextUtils.isEmpty(monthName)) {
            MA_Text.setText(name+":"+code);
        }else {
            MA_Text.setText(monthName);
        }

        mCandleLineDrawing = new CandleLineDrawing();
        if(isCandleLine) {
            kLineLayout.addDrawing(mCandleLineDrawing);
        }



        kLineLayout.setKLineHandler(new KLineHandler() {
            @Override
            public void onHighlight(Entry entry, int entryIndex, float x, float y) {
                final SizeColor sizeColor = kLineLayout.getKLineView().getRender().getSizeColor();


                float ma1 = entry.getMa5();
                float ma2 = entry.getMa10();
                float ma3 = entry.getMa20();
//                if(DataManager.isLong) {
//                    ma1 = entry.getMa10();
//                    ma2 = entry.getMa20();
//                    ma3 = entry.getMa50();
//                }

                float factor  = 1.00f;
                String maString = String.format(getResources().getString(R.string.ma_highlight),entry.getHigh(),entry.getLow(),
                        entry.getOpen(),entry.getClose(),
                        getTrendName(entry.getEntry_type()),entry.getClose()+mAnalysisData.getStepByStepType(entry.getClose()),
                        entry.getClose()+mAnalysisData.getStepByStepTypeHalf(entry.getClose()),entry.getClose() - mAnalysisData.getStepByStepType(entry.getClose()),
                        entry.getClose() - mAnalysisData.getStepByStepTypeHalf(entry.getClose()),
                        entry.getMa5(),entry.getMa10(),entry.getRate(),entry.getHigh() - mAnalysisData.getStepByStepTypeHalf(entry.getClose()),
                        (entry.getHigh() - entry.getLow())/entry.getLow()*100,
                        ma1,ma2,ma3,
                        (entry.getClose()+mAnalysisData.getStepByStepType(entry.getClose())-mFactor)*factor,
                        (entry.getClose()+mAnalysisData.getStepByStepTypeHalf(entry.getClose())-mFactor)*factor,
                        (entry.getClose() - mAnalysisData.getStepByStepType(entry.getClose())-mFactor)*factor,
                        (entry.getClose() - mAnalysisData.getStepByStepTypeHalf(entry.getClose())-mFactor)*factor,
                        (entry.getClose() - mFactor)*factor);

                MA_Text.setText(getSpannableString(maString,
                        sizeColor.getMa5Color(),
                        sizeColor.getMa10Color(),
                        sizeColor.getMa20Color()));

                String volumeString = String.format("●Curr %1$.2f ●MA5 %2$.2f ●MA10 %3$.2f",
                        (double)entry.getVolume(),
                        entry.getVolumeMa5(),
                        entry.getVolumeMa10());

                Volume_Text.setText(getSpannableString(volumeString,
                        sizeColor.getMa5Color(),
                        sizeColor.getMa10Color(),
                        sizeColor.getMa20Color()));

                SpannableString spanString = new SpannableString("");
                if (kLineLayout.isShownMACD()) {
                    String str = String.format(getResources().getString(com.wordplat.ikvstockchart.R.string.macd_highlight),
                            entry.getDiff(),
                            entry.getDea(),
                            entry.getMacd());

                    spanString = getSpannableString(str,
                            sizeColor.getDiffLineColor(),
                            sizeColor.getDeaLineColor(),
                            sizeColor.getMacdHighlightTextColor());

                } else if (kLineLayout.isShownKDJ()) {
                    String str = String.format(getResources().getString(com.wordplat.ikvstockchart.R.string.kdj_highlight),
                            entry.getK(),
                            entry.getD(),
                            entry.getJ());

                    spanString = getSpannableString(str,
                            sizeColor.getKdjKLineColor(),
                            sizeColor.getKdjDLineColor(),
                            sizeColor.getKdjJLineColor());

                } else if (kLineLayout.isShownRSI()) {
                    String str = String.format(getResources().getString(com.wordplat.ikvstockchart.R.string.rsi_highlight),
                            entry.getRsi1(),
                            entry.getRsi2(),
                            entry.getRsi3());

                    spanString = getSpannableString(str,
                            sizeColor.getRsi1LineColor(),
                            sizeColor.getRsi2LineColor(),
                            sizeColor.getRsi3LineColor());

                } else if (kLineLayout.isShownBOLL()) {
                    String str = String.format(getResources().getString(com.wordplat.ikvstockchart.R.string.boll_highlight),
                            entry.getMb(),
                            entry.getUp(),
                            entry.getDn());

                    spanString = getSpannableString(str,
                            sizeColor.getBollMidLineColor(),
                            sizeColor.getBollUpperLineColor(),
                            sizeColor.getBollLowerLineColor());
                }
                StockIndex_Text.setText(spanString);
                if(isCandleLine&&!kLineLayout.hasDrawing(mCandleLineDrawing)) {
                    kLineLayout.addDrawing(mCandleLineDrawing);
                }
            }

            @Override
            public void onCancelHighlight() {
                String maString = getResources().getString(com.wordplat.ikvstockchart.R.string.ma_normal);
                if(TextUtils.isEmpty(monthName)) {
                    MA_Text.setText(name+":"+code);
                }else {
                    MA_Text.setText(monthName);
                }

                Volume_Text.setText("");

                String stockIndexString = "";
                if (kLineLayout.isShownMACD()) {
                    stockIndexString = getResources().getString(com.wordplat.ikvstockchart.R.string.macd_normal);
                } else if (kLineLayout.isShownKDJ()) {
                    stockIndexString = getResources().getString(com.wordplat.ikvstockchart.R.string.kdj_normal);
                } else if (kLineLayout.isShownRSI()) {
                    stockIndexString = getResources().getString(com.wordplat.ikvstockchart.R.string.rsi_normal);
                } else if (kLineLayout.isShownBOLL()) {
                    stockIndexString = getResources().getString(com.wordplat.ikvstockchart.R.string.boll_normal);
                }
                StockIndex_Text.setText(stockIndexString);
                if(isCandleLine) {
                    kLineLayout.removeDrawing(mCandleLineDrawing);
                }
            }

            @Override
            public void onSingleTap(MotionEvent e, float x, float y) {
                final KLineRender kLineRender = (KLineRender) kLineLayout.getKLineView().getRender();

                if (kLineRender.getKLineRect().contains(x, y)) {
//                    Toast.makeText(mActivity, "single tab [" + x + ", " + y + "]", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDoubleTap(MotionEvent e, float x, float y) {
                final KLineRender kLineRender = (KLineRender) kLineLayout.getKLineView().getRender();

                if (kLineRender.getKLineRect().contains(x, y)) {
                    kLineRender.zoomIn(x, y);
                }
            }

            @Override
            public void onLeftRefresh() {
//                presenter.loadPrev(REQUEST_STOCK_PREV, STOCK_CODE, kLineType);
            }

            @Override
            public void onRightRefresh() {
//                presenter.loadNext(REQUEST_STOCK_NEXT, STOCK_CODE, kLineType);
            }
        });
    }

    private SpannableString getSpannableString(String str, int partColor0, int partColor1, int partColor2) {
        String[] splitString = str.split("[●]");
        SpannableString spanString = new SpannableString(str);

        int pos0 = splitString[0].length();
        int pos1 = pos0 + splitString[1].length() + 1;
        int end = str.length();

        spanString.setSpan(new ForegroundColorSpan(partColor0),
                pos0, pos1, SpannableString.SPAN_EXCLUSIVE_INCLUSIVE);

        if (splitString.length > 2) {
            int pos2 = pos1 + splitString[2].length() + 1;

            spanString.setSpan(new ForegroundColorSpan(partColor1),
                    pos1, pos2, SpannableString.SPAN_EXCLUSIVE_INCLUSIVE);

            spanString.setSpan(new ForegroundColorSpan(partColor2),
                    pos2, end, SpannableString.SPAN_EXCLUSIVE_INCLUSIVE);
        } else {
            spanString.setSpan(new ForegroundColorSpan(partColor1),
                    pos1, end, SpannableString.SPAN_EXCLUSIVE_INCLUSIVE);
        }

        return spanString;
    }


    public EntrySet getCurrentEntrySet() {
        return entrySet;
    }

    public static float mFactor;

    @Override
    public void update() {
        super.update();
        if (mChart.getViewPortHandler().getScaleX() != mScaleX) {
            mChart.fitScreen();
//            mChart.zoom(mScaleX, 1.0f, mLastIndex, 0, YAxis.AxisDependency.LEFT);
        }
    }
    /**
     * date: 7/9/17
     * author: chenli
     * description: 创建页面实例
     */
    public static KLineFragment newInstance(String xValsFormat, String klineType, String fragmentType) {
        KLineFragment fragment = new KLineFragment();
        Bundle bundle = new Bundle();
        bundle.putString(FRAGMENT_XVALS_FORMAT, xValsFormat);
        bundle.putString(FRAGMENT_KLINE_TYPE, klineType);
        bundle.putString(FRAGMENT_TYPE, fragmentType);
        fragment.setArguments(bundle);
        return fragment;
    }


    /**
     * date: 2018/12/18
     * author: chenli
     * description: 不改页情况下k线周期更新
     */
    @Subscribe
    public void onEvent(KlineEvent klineEvent) {
        String fragmentType = klineEvent.getFragmentType();
        String klineType = klineEvent.getKlineType();
        if (mFragmentType.equals(fragmentType) && !mKlineType.equals(klineType)) {
            mKlineType = klineType;

            removeLatestLine();
            removeOrderLimitLines();
            removePositionLimitLines();
            xVals.clear();
            mChart.clear();
            mChart.fitScreen();

            if (BaseApplication.getWebSocketService() != null)
                BaseApplication.getWebSocketService().sendSetChartKline(instrument_id, VIEW_WIDTH, mKlineType);


            if (sDataManager.IS_LOGIN) {
                if (mIsPosition) addPositionLimitLines();
                if (mIsPending) addOrderLimitLines();
            }

        }
    }

    private void removeLatestLine() {
        if (!mLatestLimitLines.isEmpty()) {
            LimitLine limitLine = mLatestLimitLines.get("latest");
            mChart.getAxisLeft().removeLimitLine(limitLine);
            mLatestLimitLines.remove("latest");
        }
    }

    /**
     * date: 7/9/17
     * author: chenli
     * description: 接收自选合约列表弹出框以及持仓页传过来的合约代码，以便更新K线图
     */
    @Subscribe
    public void onEvent(IdEvent data) {
        String instrument_id_new = data.getInstrument_id();
        SearchEntity searchEntity = LatestFileManager.getSearchEntities().get(instrument_id_new);
        if (instrument_id.equals(instrument_id_new)) return;
        instrument_id = instrument_id_new;

        removeLatestLine();
        removeOrderLimitLines();
        removePositionLimitLines();
        xVals.clear();
        mChart.clear();
        mChart.fitScreen();

        if (BaseApplication.getWebSocketService() != null)
            BaseApplication.getWebSocketService().sendSetChartKline(instrument_id, VIEW_WIDTH, mKlineType);

        if (instrument_id.contains("KQ") && searchEntity != null)
            instrument_id_transaction = searchEntity.getUnderlying_symbol();
        else instrument_id_transaction = instrument_id;

        if (sDataManager.IS_LOGIN) {
            if (mIsPosition) addPositionLimitLines();
            if (mIsPending) addOrderLimitLines();
        }


    }

    /**
     * date: 7/9/17
     * author: chenli
     * description: 接收“设置”按钮最新的设置信息，以便根据用户要求显示持仓、挂单、均线
     */
    @Subscribe
    public void onEventMainThread(SetUpEvent data) {
        if (mIsPending != data.isPending()) {
            mIsPending = data.isPending();
            if (sDataManager.IS_LOGIN) {
                if (mIsPending) addOrderLimitLines();
                else removeOrderLimitLines();
            }
        }

        if (mIsPosition != data.isPosition()) {
            mIsPosition = data.isPosition();
            if (sDataManager.IS_LOGIN) {
                if (mIsPosition) addPositionLimitLines();
                else removePositionLimitLines();
            }
        }

        if (mIsAverage != data.isAverage()) {
            mIsAverage = data.isAverage();
            if (mIsAverage) {
//                mChart.getCombinedData().setData(mLineData);
                mChart.getLegend().setEnabled(true);
            } else {
                mChart.getCombinedData().setData(new LineData());
                mChart.getLegend().setEnabled(false);
            }
        }

        mChart.getCombinedData().notifyDataChanged();
        mChart.invalidate();
    }

}
