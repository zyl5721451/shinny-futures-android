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

public class MAShortDrawing implements IDrawing{
    private Paint ma4Paint;
    private Paint ma9Paint;
    private Paint ma18Paint;

    private final RectF candleRect = new RectF(); // K 线图显示区域
    private AbstractRender render;

    // 计算 MA(5, 10, 20) 线条坐标用的
    private float[] ma4Buffer = new float[4];
    private float[] ma9Buffer = new float[4];
    private float[] ma18Buffer = new float[4];

    @Override
    public void onInit(RectF contentRect, AbstractRender render) {
        this.render = render;
        final SizeColor sizeColor = render.getSizeColor();

        if (ma4Paint == null) {
            ma4Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            ma4Paint.setStyle(Paint.Style.STROKE);
        }

        if (ma9Paint == null) {
            ma9Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            ma9Paint.setStyle(Paint.Style.STROKE);
        }

        if (ma18Paint == null) {
            ma18Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            ma18Paint.setStyle(Paint.Style.STROKE);
        }

        ma4Paint.setStrokeWidth(sizeColor.getMaLineSize());
        ma9Paint.setStrokeWidth(sizeColor.getMaLineSize());
        ma18Paint.setStrokeWidth(sizeColor.getMaLineSize());

        ma4Paint.setColor(sizeColor.getMa5Color());
        ma9Paint.setColor(sizeColor.getMa10Color());
        ma18Paint.setColor(sizeColor.getMa20Color());

        candleRect.set(contentRect);
    }

    @Override
    public void computePoint(int minIndex, int maxIndex, int currentIndex) {
        final int count = (maxIndex - minIndex) * 4;
        if (ma4Buffer.length < count) {
            ma4Buffer = new float[count];
            ma9Buffer = new float[count];
            ma18Buffer = new float[count];
        }

        final EntrySet entrySet = render.getEntrySet();
        final Entry entry = entrySet.getEntryList().get(currentIndex);
        final int i = currentIndex - minIndex;

        if (currentIndex < maxIndex - 1) {
            ma4Buffer[i * 4 + 0] = currentIndex + 0.5f;
            ma4Buffer[i * 4 + 1] = entry.getMa4();
            ma4Buffer[i * 4 + 2] = currentIndex + 1 + 0.5f;
            ma4Buffer[i * 4 + 3] = entrySet.getEntryList().get(currentIndex + 1).getMa4();

            ma9Buffer[i * 4 + 0] = ma4Buffer[i * 4 + 0];
            ma9Buffer[i * 4 + 1] = entry.getMa9();
            ma9Buffer[i * 4 + 2] = ma4Buffer[i * 4 + 2];
            ma9Buffer[i * 4 + 3] = entrySet.getEntryList().get(currentIndex + 1).getMa9();

            ma18Buffer[i * 4 + 0] = ma4Buffer[i * 4 + 0];
            ma18Buffer[i * 4 + 1] = entry.getMa18();
            ma18Buffer[i * 4 + 2] = ma4Buffer[i * 4 + 2];
            ma18Buffer[i * 4 + 3] = entrySet.getEntryList().get(currentIndex + 1).getMa18();
        }
    }

    @Override
    public void onComputeOver(Canvas canvas, int minIndex, int maxIndex, float minY, float maxY) {
        canvas.save();
        canvas.clipRect(candleRect);

        render.mapPoints(ma4Buffer);
        render.mapPoints(ma9Buffer);
        render.mapPoints(ma18Buffer);

        final int count = (maxIndex - minIndex) * 4;

        // 使用 drawLines 方法比依次调用 drawLine 方法要快
        canvas.drawLines(ma4Buffer, 0, count, ma4Paint);
        canvas.drawLines(ma9Buffer, 0, count, ma9Paint);
        canvas.drawLines(ma18Buffer, 0, count, ma18Paint);

        canvas.restore();
    }

    @Override
    public void onDrawOver(Canvas canvas) {

    }
}
