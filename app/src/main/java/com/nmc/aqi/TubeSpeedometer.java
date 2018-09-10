package com.nmc.aqi;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.EmbossMaskFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;

public class TubeSpeedometer extends Speedometer {

    private Paint tubePaint = new Paint(Paint.ANTI_ALIAS_FLAG)
            ,tubeBacPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF speedometerRect = new RectF();

    private boolean withEffects3D = true;

    public TubeSpeedometer(Context context) {
        this(context, null);
    }

    public TubeSpeedometer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TubeSpeedometer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initAttributeSet(context, attrs);
    }

    @Override
    protected void defaultGaugeValues() {
    }

    @Override
    protected void defaultSpeedometerValues() {
        super.setBackgroundCircleColor(0);
        super.setGoodLevelColor(0xff00BCD4);
        super.setSatisfactoryLevelColor(0xffFFC107);
        super.setModerateLevelColor(0xffFFC107);
        super.setPoorLevelColor(0xffFFC107);
        super.setVeryPoorLevelColor(0xffFFC107);
        super.setSevereLevelColor(0xffF44336);
        super.setSpeedometerWidth(dpTOpx(40f));
    }

    private void init() {
        tubePaint.setStyle(Paint.Style.STROKE);
        tubeBacPaint.setStyle(Paint.Style.STROKE);
        tubeBacPaint.setColor(0xff757575);
        tubePaint.setColor(getGoodLevelColor());

        if (Build.VERSION.SDK_INT >= 19)
            setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    private void initAttributeSet(Context context, AttributeSet attrs) {
        if (attrs == null)
            return;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TubeSpeedometer, 0, 0);

        tubeBacPaint.setColor(a.getColor(R.styleable.TubeSpeedometer_sv_speedometerBackColor, tubeBacPaint.getColor()));
        withEffects3D = a.getBoolean(R.styleable.TubeSpeedometer_sv_withEffects3D, withEffects3D);
        a.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        updateEmboss();
        updateBackgroundBitmap();
    }

    private void updateEmboss() {
        if (isInEditMode())
            return;
        if (!withEffects3D) {
            tubePaint.setMaskFilter(null);
            tubeBacPaint.setMaskFilter(null);
            return;
        }
        EmbossMaskFilter embossMaskFilter = new EmbossMaskFilter(
                new float[] { .5f, 1f, 1f }, .6f, 3f, pxTOdp(getSpeedometerWidth())*.35f);
        tubePaint.setMaskFilter(embossMaskFilter);
        EmbossMaskFilter embossMaskFilterBac = new EmbossMaskFilter(
                new float[] { -.5f, -1f, 0f }, .6f, 1f, pxTOdp(getSpeedometerWidth())*.35f);
        tubeBacPaint.setMaskFilter(embossMaskFilterBac);
    }

    @Override
    protected void onSectionChangeEvent(byte oldSection, byte newSection) {
        super.onSectionChangeEvent(oldSection, newSection);
        if (newSection == GOOD_LEVEL_SECTION)
            tubePaint.setColor(getGoodLevelColor());
        else if (newSection == SATISFACTORY_LEVEL_SECTION)
            tubePaint.setColor(getSatisfactoryLevelColor());
        else if (newSection == MODERATE_LEVEL_SECTION)
            tubePaint.setColor(getModerateLevelColor());
        else if (newSection == POOR_LEVEL_SECTION)
            tubePaint.setColor(getPoorLevelColor());
        else if (newSection == VERY_POOR_LEVEL_SECTION)
            tubePaint.setColor(getVeryPoorLevelColor());
        else
            tubePaint.setColor(getSevereLevelColor());
    }

    private void initDraw() {
        tubePaint.setStrokeWidth(getSpeedometerWidth());
        tubeBacPaint.setStrokeWidth(getSpeedometerWidth());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initDraw();

        float sweepAngle = (getEndDegree() - getStartDegree())*getOffsetSpeed();
        canvas.drawArc(speedometerRect,  getStartDegree(), sweepAngle, false, tubePaint);

        drawSpeedUnitText(canvas);
        drawIndicator(canvas);
        drawNotes(canvas);
    }

    @Override
    protected void updateBackgroundBitmap() {
        Canvas c = createBackgroundBitmapCanvas();
        initDraw();

        float risk = getSpeedometerWidth() *.5f + getPadding();
        speedometerRect.set(risk, risk, getSize() -risk, getSize() -risk);

        c.drawArc(speedometerRect, getStartDegree(), getEndDegree()- getStartDegree(), false, tubeBacPaint);

        if (getTickNumber() > 0)
            drawTicks(c);
        else
            drawDefMinMaxSpeedPosition(c);
    }

    public int getSpeedometerColor() {
        return tubeBacPaint.getColor();
    }

    public void setSpeedometerColor(int speedometerColor) {
        updateBackgroundBitmap();
        invalidate();
    }

    public boolean isWithEffects3D() {
        return withEffects3D;
    }

    public void setWithEffects3D(boolean withEffects3D) {
        this.withEffects3D = withEffects3D;
        updateEmboss();
        updateBackgroundBitmap();
        invalidate();
    }
}
