package com.wordplat.ikvstockchart.drawing;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.wordplat.ikvstockchart.entry.Entry;
import com.wordplat.ikvstockchart.entry.EntrySet;
import com.wordplat.ikvstockchart.entry.SizeColor;
import com.wordplat.ikvstockchart.render.AbstractRender;

/**
 * Created by yilong on 2018/4/16.
 */

public class MALongDrawing implements IDrawing{
    private Paint ma10Paint;
    private Paint ma20Paint;
    private Paint ma50Paint;

    private final RectF candleRect = new RectF(); // K 线图显示区域
    private AbstractRender render;

    // 计算 MA(5, 10, 20) 线条坐标用的
    private float[] ma10Buffer = new float[4];
    private float[] ma20Buffer = new float[4];
    private float[] ma50Buffer = new float[4];

    @Override
    public void onInit(RectF contentRect, AbstractRender render) {
        this.render = render;
        final SizeColor sizeColor = render.getSizeColor();

        if (ma10Paint == null) {
            ma10Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            ma10Paint.setStyle(Paint.Style.STROKE);
        }

        if (ma20Paint == null) {
            ma20Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            ma20Paint.setStyle(Paint.Style.STROKE);
        }

        if (ma50Paint == null) {
            ma50Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            ma50Paint.setStyle(Paint.Style.STROKE);
        }

        ma10Paint.setStrokeWidth(sizeColor.getMaLineSize());
        ma20Paint.setStrokeWidth(sizeColor.getMaLineSize());
        ma50Paint.setStrokeWidth(sizeColor.getMaLineSize());

        ma10Paint.setColor(sizeColor.getMa5Color());
        ma20Paint.setColor(sizeColor.getMa10Color());
        ma50Paint.setColor(sizeColor.getMa20Color());

        candleRect.set(contentRect);
    }

    @Override
    public void computePoint(int minIndex, int maxIndex, int currentIndex) {
        final int count = (maxIndex - minIndex) * 4;
        if (ma10Buffer.length < count) {
            ma10Buffer = new float[count];
            ma20Buffer = new float[count];
            ma50Buffer = new float[count];
        }

        final EntrySet entrySet = render.getEntrySet();
        final Entry entry = entrySet.getEntryList().get(currentIndex);
        final int i = currentIndex - minIndex;

        if (currentIndex < maxIndex - 1) {
            ma10Buffer[i * 4 + 0] = currentIndex + 0.5f;
            ma10Buffer[i * 4 + 1] = entry.getMa10();
            ma10Buffer[i * 4 + 2] = currentIndex + 1 + 0.5f;
            ma10Buffer[i * 4 + 3] = entrySet.getEntryList().get(currentIndex + 1).getMa10();

            ma20Buffer[i * 4 + 0] = ma10Buffer[i * 4 + 0];
            ma20Buffer[i * 4 + 1] = entry.getMa20();
            ma20Buffer[i * 4 + 2] = ma10Buffer[i * 4 + 2];
            ma20Buffer[i * 4 + 3] = entrySet.getEntryList().get(currentIndex + 1).getMa20();

            ma50Buffer[i * 4 + 0] = ma10Buffer[i * 4 + 0];
            ma50Buffer[i * 4 + 1] = entry.getMa50();
            ma50Buffer[i * 4 + 2] = ma10Buffer[i * 4 + 2];
            ma50Buffer[i * 4 + 3] = entrySet.getEntryList().get(currentIndex + 1).getMa50();
        }
    }

    @Override
    public void onComputeOver(Canvas canvas, int minIndex, int maxIndex, float minY, float maxY) {
        canvas.save();
        canvas.clipRect(candleRect);

        render.mapPoints(ma10Buffer);
        render.mapPoints(ma20Buffer);
        render.mapPoints(ma50Buffer);

        final int count = (maxIndex - minIndex) * 4;

        // 使用 drawLines 方法比依次调用 drawLine 方法要快
        canvas.drawLines(ma10Buffer, 0, count, ma10Paint);
        canvas.drawLines(ma20Buffer, 0, count, ma20Paint);
        canvas.drawLines(ma50Buffer, 0, count, ma50Paint);

        canvas.restore();
    }

    @Override
    public void onDrawOver(Canvas canvas) {

    }
}
