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

package com.wordplat.ikvstockchart.drawing;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.wordplat.ikvstockchart.entry.Entry;
import com.wordplat.ikvstockchart.entry.EntrySet;
import com.wordplat.ikvstockchart.entry.SizeColor;
import com.wordplat.ikvstockchart.render.AbstractRender;

/**
 * <p>MADrawing</p>
 * <p>Date: 2017/3/9</p>
 *
 * @author afon
 */

public class MADrawing implements IDrawing {

    private Paint ma5Paint;
    private Paint ma10Paint;
    private Paint ma20Paint;

    private final RectF candleRect = new RectF(); // K 线图显示区域
    private AbstractRender render;

    // 计算 MA(5, 10, 20) 线条坐标用的
    private float[] ma5Buffer = new float[4];
    private float[] ma10Buffer = new float[4];
    private float[] ma20Buffer = new float[4];
    private float[] lineBuffer;
    private float[] fuLineBuffer = new float[10];
    private float[] lineBuffer2;

    @Override
    public void onInit(RectF contentRect, AbstractRender render) {
        this.render = render;
        final SizeColor sizeColor = render.getSizeColor();

        if (ma5Paint == null) {
            ma5Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            ma5Paint.setStyle(Paint.Style.STROKE);
        }

        if (ma10Paint == null) {
            ma10Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            ma10Paint.setStyle(Paint.Style.STROKE);
        }

        if (ma20Paint == null) {
            ma20Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            ma20Paint.setStyle(Paint.Style.STROKE);
        }

        ma5Paint.setStrokeWidth(sizeColor.getMaLineSize());
        ma10Paint.setStrokeWidth(sizeColor.getMaLineSize());
        ma20Paint.setStrokeWidth(sizeColor.getMaLineSize());

        ma5Paint.setColor(sizeColor.getMa5Color());
        ma10Paint.setColor(sizeColor.getMa10Color());
        ma20Paint.setColor(sizeColor.getMa20Color());

        candleRect.set(contentRect);

        lineBuffer = new float[2];
        lineBuffer2 = new float[2];
    }

    @Override
    public void computePoint(int minIndex, int maxIndex, int currentIndex) {
        final int count = (maxIndex - minIndex) * 4;//currentIndex:86;minIndex:86;maxIndex:136。当前界面的左右点位置。
        if (ma5Buffer.length < count) {//数据点的index，坐标需要4倍的数据点数。
            ma5Buffer = new float[count];
            ma10Buffer = new float[count];
            ma20Buffer = new float[count];
            fuLineBuffer = new float[count];//建立坐标点的buffer
        }

        final EntrySet entrySet = render.getEntrySet();
        final Entry entry = entrySet.getEntryList().get(currentIndex);
        final int i = currentIndex - minIndex;//i = 0初始时。因为current = min。从0-左右点差


        if (entry.getOpen() > entry.getClose()) {//下降时
            lineBuffer[0] = entry.getOpen();
            lineBuffer[1] = entry.getClose();
        } else {
            lineBuffer[0] = entry.getClose();
            lineBuffer[1] = entry.getOpen();//lineBuffer得到1是低位，0是高位。单个Entry点来说。
        }
//        render.mapPoints(lineBuffer);



        //current一直变化，直到最右边时。
        if (currentIndex < maxIndex - 1) {
            ma5Buffer[i * 4 + 0] = currentIndex + 0.5f;//第一个点的x坐标
            ma5Buffer[i * 4 + 1] = entry.getMa5();//第一个点的y坐标
            ma5Buffer[i * 4 + 2] = currentIndex + 1 + 0.5f;//第二个点的x坐标
            ma5Buffer[i * 4 + 3] = entrySet.getEntryList().get(currentIndex + 1).getMa5();//第二个点的y坐标。

            ma10Buffer[i * 4 + 0] = ma5Buffer[i * 4 + 0];
            ma10Buffer[i * 4 + 1] = entry.getMa10();
            ma10Buffer[i * 4 + 2] = ma5Buffer[i * 4 + 2];
            ma10Buffer[i * 4 + 3] = entrySet.getEntryList().get(currentIndex + 1).getMa10();

            ma20Buffer[i * 4 + 0] = ma5Buffer[i * 4 + 0];
            ma20Buffer[i * 4 + 1] = entry.getMa20();
            ma20Buffer[i * 4 + 2] = ma5Buffer[i * 4 + 2];
            ma20Buffer[i * 4 + 3] = entrySet.getEntryList().get(currentIndex + 1).getMa20();



            final Entry entry2 = entrySet.getEntryList().get(currentIndex+1);

            if (entry2.getOpen() > entry2.getClose()) {
                lineBuffer2[0] = entry2.getOpen();
                lineBuffer2[1] = entry2.getClose();
            } else {
                lineBuffer2[0] = entry2.getClose();
                lineBuffer2[1] = entry2.getOpen();//0是大的，1是小的。
            }
            fuLineBuffer[i * 4 + 0] = currentIndex + 0.5f;
            fuLineBuffer[i * 4 + 1] = entry.getClose();//第一个点y坐标是，
            fuLineBuffer[i * 4 + 2] = currentIndex + 1 + 0.5f;
            fuLineBuffer[i * 4 + 3] = entry2.getClose();//第二个点y坐标也是大的那个。而不管是open还是close。
        }
    }

    @Override
    public void onComputeOver(Canvas canvas, int minIndex, int maxIndex, float minY, float maxY) {
        canvas.save();
        canvas.clipRect(candleRect);

        render.mapPoints(ma5Buffer);
        render.mapPoints(ma10Buffer);
        render.mapPoints(ma20Buffer);
        render.mapPoints(fuLineBuffer);

        final int count = (maxIndex - minIndex) * 4;

        // 使用 drawLines 方法比依次调用 drawLine 方法要快
//        canvas.drawLines(ma5Buffer, 0,count,ma5Paint);
//        canvas.drawLines(ma10Buffer, 0, count, ma10Paint);
//        canvas.drawLines(ma20Buffer, 0, count, ma20Paint);
        canvas.drawLines(fuLineBuffer,0,count,ma10Paint);

        canvas.restore();
    }

    @Override
    public void onDrawOver(Canvas canvas) {

    }
}
