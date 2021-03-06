package org.edx.mobile.view.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;

import org.edx.mobile.R;

public class CropOverlay extends View {

    private Paint borderPaint;
    private Paint backgroundPaint;

    final Path circleSelectionPath = new Path();
    final RectF mRectF = new RectF();

    public CropOverlay(Context context) {
        this(context, null);
    }

    public CropOverlay(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropOverlay(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        borderPaint = new Paint();
        borderPaint.setColor(Color.WHITE);
        borderPaint.setStrokeWidth(3 * context.getResources().getDisplayMetrics().density);
        borderPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.argb(200, 0, 0, 0));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw background with transparent circular hole
        final float radius = Math.min((float)canvas.getWidth(), canvas.getHeight()) / 2 - getResources().getDimension(R.dimen.widget_margin);
        mRectF.set((float)canvas.getWidth() / 2 - radius, (float)canvas.getHeight() / 2 - radius, (float)canvas.getWidth() / 2 + radius, (float)canvas.getHeight() / 2 + radius);
        circleSelectionPath.reset();
        circleSelectionPath.addOval(mRectF, Path.Direction.CW);
        canvas.clipPath(circleSelectionPath, Region.Op.XOR);
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), backgroundPaint);
        canvas.restore();

        // Draw circle border
        canvas.drawCircle((float)canvas.getWidth() / 2, (float)canvas.getHeight() / 2, radius, borderPaint);
    }
}
