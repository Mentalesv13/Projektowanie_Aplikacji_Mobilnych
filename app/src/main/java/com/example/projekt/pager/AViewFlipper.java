package com.example.projekt.pager;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ViewFlipper;

public class AViewFlipper extends ViewFlipper
{

    Paint paint = new Paint();

    public AViewFlipper(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    protected void dispatchDraw(Canvas canvas)
    {
        super.dispatchDraw(canvas);
        int width = getWidth();

        float margin = 2;
        float radius = 13.5f;
        float cx = width / 2 - ((radius + margin) * 2 * getChildCount() / 2);
        float cy = getHeight() - 50;

        canvas.save();

        for (int i = 0; i < getChildCount(); i++)
        {
            if (i == getDisplayedChild())
            {

                paint.setColor(Color.WHITE);
                canvas.drawCircle(cx, cy, radius+2, paint);
                paint.setColor(Color.rgb(216,28,92));
                canvas.drawCircle(cx, cy, radius, paint);

            } else
            {
                paint.setColor(Color.WHITE);
                canvas.drawCircle(cx, cy, radius+2, paint);
                paint.setColor(Color.GRAY);
                canvas.drawCircle(cx, cy, radius, paint);
            }
            cx += 2.75 * (radius + margin);
        }
        canvas.restore();
    }

}