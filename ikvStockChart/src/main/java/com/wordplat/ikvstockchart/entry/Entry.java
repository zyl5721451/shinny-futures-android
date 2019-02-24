/*
 * Copyright (C) 2017 WordPlat Open Source Project
 *
 *      https://wordplat.com/InteractiveKLineView/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wordplat.ikvstockchart.entry;

/**
 * <p>Entry</p>
 * <p>Date: 2017/3/1</p>
 *
 * @author afon
 */

public class Entry implements Cloneable{

    // 初始需全部赋值的属性
    private float open; // 开盘价
    private float high; // 最高价
    private float low; // 最低价
    private float close; // 收盘价
    private long volume; // 量
    private String xLabel; // X 轴标签
    private FIRST_ENTRY_TYPE entry_type;
    private LINE_COLOR_TYPE line_type;
    private float rate;

    private int warning;

    // MA 指标的三个属性
    private float ma5;
    private float ma10;
    private float ma20;
    private float ma50;
    private float ma4;
    private float ma9;
    private float ma18;

    // 量的5日平均和10日平均
    private double volumeMa5;
    private double volumeMa10;

    // MACD 指标的三个属性
    private float dea;
    private float diff;
    private float macd;

    // KDJ 指标的三个属性
    private float k;
    private float d;
    private float j;

    // RSI 指标的三个属性
    private float rsi1;
    private float rsi2;
    private float rsi3;

    // BOLL 指标的三个属性
    private float up; // 上轨线
    private float mb; // 中轨线
    private float dn; // 下轨线

    /**
     * 自定义分时图用的数据
     *
     * @param close 收盘价
     * @param volume 量
     * @param xLabel X 轴标签
     */
    public Entry(float close, int volume, String xLabel) {
        this.open = 0;
        this.high = 0;
        this.low = 0;
        this.close = close;
        this.volume = volume;
        this.xLabel = xLabel;
    }

    @Override
    public Entry clone() throws CloneNotSupportedException {
        return (Entry) super.clone();
    }

    /**
     * 自定义 K 线图用的数据
     *
     * @param open 开盘价
     * @param high 最高价
     * @param low 最低价
     * @param close 收盘价
     * @param volume 量
     * @param xLabel X 轴标签
     */
    public Entry(float open, float high, float low, float close, long volume, String xLabel) {
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
        this.xLabel = xLabel;
    }

    public FIRST_ENTRY_TYPE getEntry_type() {
        return entry_type;
    }

    public void setEntry_type(FIRST_ENTRY_TYPE entry_type) {
        this.entry_type = entry_type;
    }

    public int getWarning() {
        return warning;
    }

    public void setWarning(int warning) {
        this.warning = warning;
    }

    public float getOpen() {
        return open;
    }

    public float getHigh() {
        return high;
    }

    public float getLow() {
        return low;
    }

    public float getClose() {
        return close;
    }

    public void setOpen(float open) {
        this.open = open;
    }

    public void setHigh(float high) {
        this.high = high;
    }

    public void setLow(float low) {
        this.low = low;
    }

    public void setClose(float close) {
        this.close = close;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }

    public long getVolume() {
        return volume;
    }

    public String getXLabel() {
        return xLabel;
    }

    public void setXLabel(String xLabel) {
        this.xLabel = xLabel;
    }

    public float getMa5() {
        return ma5;
    }

    public void setMa5(float ma5) {
        this.ma5 = ma5;
    }

    public float getMa10() {
        return ma10;
    }

    public void setMa10(float ma10) {
        this.ma10 = ma10;
    }

    public float getMa20() {
        return ma20;
    }

    public void setMa20(float ma20) {
        this.ma20 = ma20;
    }

    public double getVolumeMa5() {
        return volumeMa5;
    }

    public void setVolumeMa5(double volumeMa5) {
        this.volumeMa5 = volumeMa5;
    }

    public double getVolumeMa10() {
        return volumeMa10;
    }

    public void setVolumeMa10(double volumeMa10) {
        this.volumeMa10 = volumeMa10;
    }

    public float getDea() {
        return dea;
    }

    public void setDea(float dea) {
        this.dea = dea;
    }

    public float getDiff() {
        return diff;
    }

    public void setDiff(float diff) {
        this.diff = diff;
    }

    public float getMacd() {
        return macd;
    }

    public void setMacd(float macd) {
        this.macd = macd;
    }

    public float getK() {
        return k;
    }

    public void setK(float k) {
        this.k = k;
    }

    public float getD() {
        return d;
    }

    public void setD(float d) {
        this.d = d;
    }

    public float getJ() {
        return j;
    }

    public void setJ(float j) {
        this.j = j;
    }

    public float getRsi1() {
        return rsi1;
    }

    public void setRsi1(float rsi1) {
        this.rsi1 = rsi1;
    }

    public float getRsi2() {
        return rsi2;
    }

    public void setRsi2(float rsi2) {
        this.rsi2 = rsi2;
    }

    public float getRsi3() {
        return rsi3;
    }

    public void setRsi3(float rsi3) {
        this.rsi3 = rsi3;
    }

    public float getUp() {
        return up;
    }

    public void setUp(float up) {
        this.up = up;
    }

    public float getMb() {
        return mb;
    }

    public void setMb(float mb) {
        this.mb = mb;
    }

    public float getDn() {
        return dn;
    }

    public void setDn(float dn) {
        this.dn = dn;
    }

    public LINE_COLOR_TYPE getLine_type() {
        return line_type;
    }

    public void setLine_type(LINE_COLOR_TYPE line_type) {
        this.line_type = line_type;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public float getMa50() {
        return ma50;
    }

    public void setMa50(float ma50) {
        this.ma50 = ma50;
    }

    public float getMa4() {
        return ma4;
    }

    public void setMa4(float ma4) {
        this.ma4 = ma4;
    }

    public float getMa9() {
        return ma9;
    }

    public void setMa9(float ma9) {
        this.ma9 = ma9;
    }

    public float getMa18() {
        return ma18;
    }

    public void setMa18(float ma18) {
        this.ma18 = ma18;
    }

    @Override
    public String toString() {
        return "Entry{" +
                "open=" + open +
                ", high=" + high +
                ", low=" + low +
                ", close=" + close +
                '}';
    }
}
