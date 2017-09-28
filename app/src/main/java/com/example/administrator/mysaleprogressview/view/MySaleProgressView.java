package com.example.administrator.mysaleprogressview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.administrator.mysaleprogressview.R;
import com.example.administrator.mysaleprogressview.utils.ScreenUtil;

import java.text.DecimalFormat;

/**
 * Created by hxl on 2017/9/28 0028 at haichou.
 */

public class MySaleProgressView extends View {

    private int sideColor;
    private int textColor;
    private float sideWidth;
    private float textSize;
    private String nearOverText;
    private String overText;
    //商品总数
    private int totalCount ;
    //当前卖出数
    private int currentCount;
    //售出比例
    private float scale;
    /*view的宽高*/
    private int height;
    private int width;
    private PorterDuffXfermode porterDuffXfermode;
    /*基准线*/
    private float baseLineY;
    /*矩形*/
    private RectF bgRectF;
    /*边框画笔*/
    private Paint sidePaint;
    /*半径*/
    private int radius;
    /*进度条画笔*/
    private Paint srcPaint;
    /*背景*/
    private Bitmap bgBitmap;
    private Bitmap srcBitmap;
    /*前景*/
    private Bitmap fgSrcBitmap;
    /*文本*/
    private float nearOverTextWidth;
    private Paint textPaint;

    public MySaleProgressView(Context context) {
        this(context,null);
    }

    public MySaleProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MySaleProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        initPaint();
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MySaleProgressView);
        /*边框的颜色*/
        sideColor = typedArray.getColor(R.styleable.MySaleProgressView_sideColor,0xffff3c32);
        /*字体的颜色*/
        textColor = typedArray.getColor(R.styleable.MySaleProgressView_textColor, 0xffff3c32);
        /*边框的宽度*/
        sideWidth = typedArray.getDimension(R.styleable.MySaleProgressView_sideWidth,
                ScreenUtil.dp2px(context,2));
        /*字体的大小*/
        textSize = typedArray.getDimension(R.styleable.MySaleProgressView_textSize,
                ScreenUtil.dp2px(context,16));
        /*即将售罄*/
        nearOverText = typedArray.getString(R.styleable.MySaleProgressView_nearOverText);
        /*已售罄*/
        overText = typedArray.getString(R.styleable.MySaleProgressView_overText);
        typedArray.recycle();
    }

    private void initPaint() {
        /*边框画笔*/
        sidePaint = new Paint();
        sidePaint.setAntiAlias(true);
        sidePaint.setDither(true);
        sidePaint.setColor(sideColor);
        sidePaint.setStyle(Paint.Style.STROKE);
        sidePaint.setStrokeWidth(sideWidth);

        /*背景进度画笔*/
        srcPaint = new Paint();
        srcPaint.setAntiAlias(true);
        srcPaint.setDither(true);

        /*文本*/
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setDither(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(textSize);

        nearOverTextWidth = textPaint.measureText(nearOverText);

        porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        /*获得view的宽高*/
        height = getMeasuredHeight();
        width = getMeasuredWidth();
        /*设置圆角半径*/
        radius = height/2;
        /*设置圆角矩形*/
        if (bgRectF == null){
            bgRectF = new RectF(sideWidth,sideWidth,width-sideWidth,height-sideWidth);
        }
        /**
         * 绘制 text 的时候，起始点的坐标就是文字左下角 的 基准点，而不是左上角
         * 以baseline为基准，向上为负，向下为正。ascent为负数，descent为正数。
         */
        if (baseLineY == 0.0){
            Paint.FontMetricsInt fontMetricsInt = textPaint.getFontMetricsInt();
            baseLineY = height/2 - (fontMetricsInt.descent/2 + fontMetricsInt.ascent/2);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (totalCount == 0) {
            scale = 0.0f;
        }else {
            scale = Float.parseFloat(new DecimalFormat("0.00").format((float)currentCount/(float)totalCount));
        }
        /*绘制边框*/
        drawSide(canvas);
        /*绘制背景进度*/
        drawProgress(canvas);
        /*绘制前景*/
        drawFg(canvas);
        /*绘制文本*/
        drawText(canvas);
    }
    /*绘制文本*/
    private void drawText(Canvas canvas) {
        String scaleText = new DecimalFormat("#%").format(scale);
        String saleText = String.format("已抢%s件",currentCount);
        float scaleTextWidth = textPaint.measureText(scaleText);
        /*1.创建一个bitmap*/
        Bitmap textBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        /*2.装载在空的画布中*/
        Canvas textCanvas = new Canvas(textBitmap) ;
        textPaint.setColor(textColor);
        /*3.绘制dst*/
        if (scale <0.8f){
            textCanvas.drawText(saleText,ScreenUtil.dp2px(getContext(),10),baseLineY,textPaint);
            textCanvas.drawText(scaleText,width-scaleTextWidth-ScreenUtil.dp2px(getContext(),10),
                    baseLineY,textPaint);
        }else if(scale < 1.0f){
            textCanvas.drawText(nearOverText,width/2 - nearOverTextWidth/2,baseLineY,textPaint);
            textCanvas.drawText(scaleText,width-scaleTextWidth-ScreenUtil.dp2px(getContext(),10),
                    baseLineY,textPaint);
        }else {
            textCanvas.drawText(overText,width/2 - nearOverTextWidth/2,baseLineY,textPaint);
        }
        /*4.设置图形模式SRC_IN*/
        textPaint.setXfermode(porterDuffXfermode);
        /*5.绘制src*/
        textPaint.setColor(Color.WHITE);
        textCanvas.drawRoundRect(new RectF(sideWidth,sideWidth,(width-sideWidth)*scale,height-sideWidth),
                radius,radius,textPaint);
        canvas.drawBitmap(textBitmap,0,0,null);
        textPaint.setXfermode(null);
    }

    /*绘制前景*/
    private void drawFg(Canvas canvas) {
        if (scale == 0.0){
            return;
        }
        /*1.创建一个bitmap*/
        Bitmap fgBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        /*2.创建的bitmap装载画布上*/
        Canvas fgCanvas = new Canvas(fgBitmap) ;
        /*3.绘制dst*/
        fgCanvas.drawRoundRect(new RectF(sideWidth, sideWidth, (width - sideWidth) * scale, height - sideWidth),
                radius,radius,srcPaint);
        /*4.设置图形混合模式SRC_IN*/
        srcPaint.setXfermode(porterDuffXfermode);
        /*5.创建src*/
        if (fgSrcBitmap == null){
            fgSrcBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.fg);
        }
        /*6.绘制src*/
        fgCanvas.drawBitmap(fgSrcBitmap,null,bgRectF,srcPaint);

        canvas.drawBitmap(fgBitmap,0,0,null);
        srcPaint.setXfermode(null);
    }

    /*绘制背景进度*/
    private void drawProgress(Canvas canvas) {
        /*1.创建一个bitmap对象装载在一个新的画布上中*/
        bgBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        Canvas bgCanvas = new Canvas(bgBitmap) ;
        /*2.在画布上绘制一个矩形dst*/
        bgCanvas.drawRoundRect(bgRectF,radius,radius,srcPaint);
        /*3.为paint设置SRC_IN的图形混合模式，再绘制进度图形，之前的圆角矩形为dst，绘制的进度图形为src*/
        srcPaint.setXfermode(porterDuffXfermode);
        if (srcBitmap == null){
            srcBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.bg);
        }
        /*4.绘制src*/
        bgCanvas.drawBitmap(srcBitmap,null,bgRectF,srcPaint);
        //根据 SRC_IN 的规则，条纹图与圆角矩形重叠的部分保留，溢出的部分则不予显示
        canvas.drawBitmap(bgBitmap,0,0,null);
        //给 Paint  设置混合模式，绘制完后，如果还要绘制其他东西，则取消混合模式，即  setXfermode( null )。
        srcPaint.setXfermode(null);

    }

    /*绘制边框*/
    private void drawSide(Canvas canvas) {
        canvas.drawRoundRect(bgRectF,radius,radius,sidePaint);
    }

    public void setTotalAndCurrentCount(int totalCount,int currentCount){
        this.totalCount = totalCount;
        this.currentCount = currentCount;
        /*重绘*/
        postInvalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (bgBitmap!=null&&!bgBitmap.isRecycled()){
            bgBitmap.recycle();
            bgBitmap = null;
        }
        if (srcBitmap!=null&&!srcBitmap.isRecycled()){
            srcBitmap.recycle();
            srcBitmap = null;
        }
        if (fgSrcBitmap!=null&&!fgSrcBitmap.isRecycled()){
            fgSrcBitmap.recycle();
            fgSrcBitmap = null;
        }
    }
}

