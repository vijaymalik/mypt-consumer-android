package co.com.mypt.BarChart;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.Log;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class RoundedBarWithTickCrossImageChart  extends BarChartRenderer {

    private Bitmap tickBitmap;
    private Bitmap crossBitmap;
    private ArrayList<String>goals;
    private ArrayList<String>dates;

    public RoundedBarWithTickCrossImageChart(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler,
                                             int startColor, int endColor, Bitmap tickImage,
                                             Bitmap crossImage, ArrayList<String>goals, ArrayList<String>dates) {
        super(chart, animator, viewPortHandler);
        this.startColor = startColor;
        this.endColor = endColor;
        this.goals = goals;
        this.dates = dates;

        gradientPaint = new Paint();
        gradientPaint.setStyle(Paint.Style.FILL);

        this.tickBitmap = Bitmap.createScaledBitmap(tickImage, 30, 30, true);
        this.crossBitmap = Bitmap.createScaledBitmap(crossImage, 30, 30, true);
    }

    private float mRadius=5f;
    private int startColor;
    private int endColor;
    private Paint gradientPaint;

    public void setmRadius(float mRadius) {
        this.mRadius = mRadius;
    }

    @Override
    protected void drawDataSet(Canvas c, IBarDataSet dataSet, int index) {

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        mShadowPaint.setColor(dataSet.getBarShadowColor());

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();



        if(mBarBuffers!=null){
            // initialize the buffer
            BarBuffer buffer = mBarBuffers[index];
            buffer.setPhases(phaseX, phaseY);
            buffer.setDataSet(index);
            buffer.setBarWidth(mChart.getBarData().getBarWidth());
            buffer.setInverted(mChart.isInverted(dataSet.getAxisDependency()));

            buffer.feed(dataSet);

            trans.pointValuesToPixel(buffer.buffer);

            // if multiple colors
            /*if (dataSet.getColors().size() > 1) {

                for (int j = 0; j < buffer.size(); j += 4) {

                    if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2]))
                        continue;

                    if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j]))
                        break;

                    if (mChart.isDrawBarShadowEnabled()) {
                        if (mRadius > 0)
                            c.drawRoundRect(new RectF(buffer.buffer[j], mViewPortHandler.contentTop(), buffer.buffer[j + 2], mViewPortHandler.contentBottom()), mRadius, mRadius, mShadowPaint);
                        else
                            c.drawRect(buffer.buffer[j], mViewPortHandler.contentTop(), buffer.buffer[j + 2], mViewPortHandler.contentBottom(), mShadowPaint);
                    }

                    // Set the color for the currently drawn value. If the index
                    // is
                    // out of bounds, reuse colors.
                    mRenderPaint.setColor(dataSet.getColor(j / 4));

                    LinearGradient shader = new LinearGradient(
                            buffer.buffer[j],   // x0
                            buffer.buffer[j + 1], // y0
                            buffer.buffer[j],   // x1
                            buffer.buffer[j + 3], // y1
                            startColor,         // top color
                            endColor,           // bottom color
                            Shader.TileMode.CLAMP
                    );
                    gradientPaint.setShader(shader);

                    if (mRadius > 0){

                        Path path = RoundedRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2] , buffer.buffer[j + 3] , 15,15, true, true, false, false);
                        c.drawPath(path,gradientPaint);
                    }
                    else
                        c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2], buffer.buffer[j + 3], gradientPaint);


                }
            } else {

                LinearGradient shader = new LinearGradient(
                        0f,   // x0
                        mViewPortHandler.contentBottom(), // y0
                        0f,   // x1
                        mViewPortHandler.contentTop(), // y1
                        startColor,         // top color
                        endColor,           // bottom color
                        Shader.TileMode.CLAMP
                );
                gradientPaint.setShader(shader);

                mRenderPaint.setColor(dataSet.getColor());

                for (int j = 0; j < buffer.size(); j += 4) {

                    if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2]))
                        continue;

                    if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j]))
                        break;

                    if (mChart.isDrawBarShadowEnabled()) {
                        if (mRadius > 0)
                            c.drawRoundRect(new RectF(buffer.buffer[j], mViewPortHandler.contentTop(),
                                    buffer.buffer[j + 2],
                                    mViewPortHandler.contentBottom()), mRadius, mRadius, mShadowPaint);
                        else
                            c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                                    buffer.buffer[j + 3], gradientPaint);
                    }

                    if (mRadius > 0){
                        Path path = RoundedRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2] , buffer.buffer[j + 3] , 15,15, true, true, false, false);
                        c.drawPath(path,gradientPaint);
                    }
                    else
                        c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                                buffer.buffer[j + 3], gradientPaint);
                }
            }*/

            LinearGradient shader = new LinearGradient(
                    0f,   // x0
                    mViewPortHandler.contentBottom(), // y0
                    0f,   // x1
                    mViewPortHandler.contentTop(), // y1
                    startColor,         // top color
                    endColor,           // bottom color
                    Shader.TileMode.CLAMP
            );

            mRenderPaint.setColor(dataSet.getColor());

            for (int j = 0; j < buffer.size(); j += 4) {

                if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2]))
                    continue;

                if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j]))
                    break;

                if(goals.get(j / 4).equalsIgnoreCase("true")){
                    gradientPaint.setShader(shader);
                }else {
                    gradientPaint.setShader(null);
                    gradientPaint.setColor(Color.parseColor("#31343A"));
                }

                if (mChart.isDrawBarShadowEnabled()) {
                    if (mRadius > 0)
                        c.drawRoundRect(new RectF(buffer.buffer[j], mViewPortHandler.contentTop(),
                                buffer.buffer[j + 2],
                                mViewPortHandler.contentBottom()), mRadius, mRadius, mShadowPaint);
                    else
                        c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                                buffer.buffer[j + 3], gradientPaint);
                }

                if (mRadius > 0){
                    Path path = RoundedRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2] , buffer.buffer[j + 3] , 15,15, true, true, false, false);
                    c.drawPath(path,gradientPaint);
                }
                else
                    c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                            buffer.buffer[j + 3], gradientPaint);
            }
        }

    }
    public static Path RoundedRect(
            float left, float top, float right, float bottom, float rx, float ry,
            boolean tl, boolean tr, boolean br, boolean bl
    ){
        Path path = new Path();
        if (rx < 0) rx = 0;
        if (ry < 0) ry = 0;
        float width = right - left;
        float height = bottom - top;
        if (rx > width / 2) rx = width / 2;
        if (ry > height / 2) ry = height / 2;
        float widthMinusCorners = (width - (2 * rx));
        float heightMinusCorners = (height - (2 * ry));

        path.moveTo(right, top + ry);
        if (tr)
            path.rQuadTo(0, -ry, -rx, -ry);//top-right corner
        else{
            path.rLineTo(0, -ry);
            path.rLineTo(-rx,0);
        }
        path.rLineTo(-widthMinusCorners, 0);
        if (tl)
            path.rQuadTo(-rx, 0, -rx, ry); //top-left corner
        else{
            path.rLineTo(-rx, 0);
            path.rLineTo(0,ry);
        }
        path.rLineTo(0, heightMinusCorners);

        if (bl)
            path.rQuadTo(0, ry, rx, ry);//bottom-left corner
        else{
            path.rLineTo(0, ry);
            path.rLineTo(rx,0);
        }

        path.rLineTo(widthMinusCorners, 0);
        if (br)
            path.rQuadTo(rx, 0, rx, -ry); //bottom-right corner
        else{
            path.rLineTo(rx,0);
            path.rLineTo(0, -ry);
        }

        path.rLineTo(0, -heightMinusCorners);

        path.close();//Given close, last lineto can be removed.

        return path;
    }

    @Override
    public void drawValues(Canvas c) {
        BarData barData = mChart.getBarData();
        if (barData == null) return;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        for (int i = 0; i < barData.getDataSetCount(); i++) {
            IBarDataSet dataSet = barData.getDataSetByIndex(i);
            if (!shouldDrawValues(dataSet)) continue;

            Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());
            BarBuffer buffer = mBarBuffers[i];

            for (int j = 0; j < buffer.buffer.length; j += 4) {
                float x = (buffer.buffer[j] + buffer.buffer[j + 2]) / 2;
                float y = buffer.buffer[j + 1] - 20f; // Positioning above bar

                // Determine whether to use tick or cross image
                BarEntry entry = dataSet.getEntryForIndex(j / 4);
                Date date;
                try {
                    date = inputFormat.parse(dates.get(j / 4));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                if (date != null) {
                    if(date.after(cal.getTime())){
                        Log.e("","");
                    }else {
                        Bitmap icon = goals.get(j / 4).equalsIgnoreCase("true") ? tickBitmap : crossBitmap; // Tick if value > 2000

                        // Draw tick/cross image (with circle) above the bar
                        c.drawBitmap(icon, x - (icon.getWidth() / 2), y - (icon.getHeight() / 2), null);
                    }
                }
            }
        }
    }

}