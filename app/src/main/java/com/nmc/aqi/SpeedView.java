package com.nmc.aqi;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.nmc.speedviewlib.components.Indicators.NormalIndicator;

public class SpeedView extends Speedometer {

    private Path markPath = new Path();
    private Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            speedometerPaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            markPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF speedometerRect = new RectF();

    public SpeedView(Context context) {
        this(context,null);
    }

    public SpeedView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SpeedView(Context context,AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initAttributeSet(context, attrs);
    }

    @Override
    protected void defaultGaugeValues() {
    }

    @Override
    protected void defaultSpeedometerValues() {
        super.setIndicator(new NormalIndicator(getContext()));
        super.setBackgroundCircleColor(0);
    }

    private void init() {
        speedometerPaint.setStyle(Paint.Style.STROKE);
        markPaint.setStyle(Paint.Style.STROKE);
        circlePaint.setColor(0xFF444444);
    }

    private void initAttributeSet(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SpeedView,0,0);

        circlePaint.setColor(a.getColor(R.styleable.SpeedView_sv_centerCircleColor, circlePaint.getColor()));
        a.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        updateBackgroundBitmap();
    }

    private void initDraw() {
        speedometerPaint.setStrokeWidth(getSpeedometerWidth());
        markPaint.setColor(getMarkColor());
    }

    @Override
    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);

        drawSpeedUnitText(canvas);

        drawIndicator(canvas);
        canvas.drawCircle(getSize() *.5f,getSize() *.5f,getWidthPa()/12f, circlePaint);

        drawNotes(canvas);
    }

    @Override
    protected void updateBackgroundBitmap() {
        Canvas c = createBackgroundBitmapCanvas();
        initDraw();

        float markH = getViewSizePa()/28f;
        markPath.reset();
        markPath.moveTo(getSize() *.5f, getPadding());
        markPath.lineTo(getSize() *.5f, markH + getPadding());
        markPaint.setStrokeWidth(markH/3f);

        float risk = getSpeedometerWidth() *.5f + getPadding();
        speedometerRect.set(risk, risk, getSize() -risk,getSize() -risk);

        speedometerPaint.setColor(getSevereLevelColor());
        c.drawArc(speedometerRect, getStartDegree(),
                (getEndDegree()- getStartDegree()),false, speedometerPaint);
        speedometerPaint.setColor(getVeryPoorLevelColor());
        c.drawArc(speedometerRect, getStartDegree(),
                (getEndDegree()- getStartDegree())*getVeryPoorLevelOffset(),false, speedometerPaint);
        speedometerPaint.setColor(getPoorLevelColor());
        c.drawArc(speedometerRect, getStartDegree(),
                (getEndDegree()- getStartDegree())*getPoorLevelOffset(),false, speedometerPaint);
        speedometerPaint.setColor(getModerateLevelColor());
        c.drawArc(speedometerRect, getStartDegree(),
                (getEndDegree() -getStartDegree())*getModerateLevelOffset(),false, speedometerPaint);
        speedometerPaint.setColor(getSatisfactoryLevelColor());
        c.drawArc(speedometerRect, getStartDegree(),
                (getEndDegree() -getStartDegree())*getSatisfactoryLevelOffset(),false, speedometerPaint);
        speedometerPaint.setColor(getGoodLevelColor());
        c.drawArc(speedometerRect, getStartDegree(),
                (getEndDegree() -getStartDegree())*getGoodLevelOffset(),false, speedometerPaint);

        c.save();
        c.rotate(90f + getStartDegree(), getSize() *.5f, getSize() *.5f);
        float everyDegree = (getEndDegree() - getStartDegree()) * .111f;
        for (float i = getStartDegree(); i < getEndDegree()-(2f*everyDegree); i+=everyDegree) {
            c.rotate(everyDegree, getSize() *.5f, getSize() *.5f);
            c.drawPath(markPath, markPaint);
        }
        c.restore();
        if (getTickNumber() > 0)
            drawTicks(c);
        else
            drawDefMinMaxSpeedPosition(c);
    }

    public int getCenterCircleColor() {
        return circlePaint.getColor();
    }

    public void setCenterCircleColor(int centerCircleColor) {
        circlePaint.setColor(centerCircleColor);
        if (!isAttachedToWindow())
            return;
        invalidate();
    }
}
