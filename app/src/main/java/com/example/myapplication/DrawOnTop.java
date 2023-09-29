package com.example.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.View;

public class DrawOnTop extends View {
    int screenCenterX = 0;
    int screenCenterY = 0;
    int radius = 50;

    public DrawOnTop(Context context, int screenCenterX, int screenCenterY, int radius) {
        super(context);
        this.screenCenterX = screenCenterX;
        this.screenCenterY = screenCenterY;
        this.radius = radius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(getResources().getColor(R.color.white));
        paint.setAlpha(130);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawCircle(screenCenterX, screenCenterY, radius, paint);
        super.onDraw(canvas);
    }
}