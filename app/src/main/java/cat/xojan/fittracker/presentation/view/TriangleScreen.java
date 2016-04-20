package cat.xojan.fittracker.presentation.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Region;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.fernandocejas.frodo.core.checks.Preconditions;

import cat.xojan.fittracker.R;
import cat.xojan.fittracker.domain.ActivityType;

public class TriangleScreen extends View {

    public interface FitnessActivityClickListener {
        void onClick(ActivityType activityType);
    }

    private Region mRegionWalking;
    private Region mRegionRunning;
    private Region mRegionBiking;
    private Region mRegionOther;
    private Region mClip;

    private Point mP1;
    private Point mP2;
    private Point mP3;

    private Bitmap mRunningBitmap;
    private Bitmap mCyclingBitmap;
    private Bitmap mWalkingBitmap;

    private boolean isRunningPressed;
    private boolean isOtherPressed;
    private boolean isWalkingPressed;
    private boolean isBikingPressed;

    FitnessActivityClickListener mListener;

    public TriangleScreen(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setFitnessActivityClickListener(FitnessActivityClickListener listener) {
        mListener = listener;
    }

    private void init() {
        Resources res = getResources();
        mRegionWalking = new Region();
        mRegionRunning = new Region();
        mRegionBiking = new Region();
        mRegionOther = new Region();
        mClip = new Region();
        mP1 = new Point();
        mP2 = new Point();
        mP3 = new Point();
        mRunningBitmap = BitmapFactory.decodeResource(res, R.drawable.ic_running30);
        mCyclingBitmap = BitmapFactory.decodeResource(res, R.drawable.ic_biking2);
        mWalkingBitmap = BitmapFactory.decodeResource(res, R.drawable.ic_walking3);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mClip.set(0, 0, getWidth(), getHeight());
        Path path;
        float x, y;
        int color;

        //triangle up
        mP1.set(0, getHeight());
        mP2.set(mP1.x + getWidth(), mP1.y);
        mP3.set(mP1.x + (getWidth() / 2), mP1.y - getHeight() / 2);
        path = getTrianglePath(mP1, mP2, mP3);
        mRegionOther.setPath(path, mClip);
        color = isOtherPressed ? R.color.red_dark : R.color.red;
        canvas.drawPath(path, getPaintShape(getResources()
                .getColor(color)));

        x = (getWidth() / 2) - mRunningBitmap.getWidth()/2;
        y = 3 * (getHeight() /4) - mRunningBitmap.getHeight()/3;
        canvas.drawBitmap(mRunningBitmap, x, y, null);

        //triangle right
        mP1.set(0, 0);
        mP2.set(mP1.x, mP1.y + getHeight());
        mP3.set(mP1.x + getWidth() / 2, mP1.y + getHeight() / 2);
        path = getTrianglePath(mP1, mP2, mP3);
        mRegionWalking.setPath(path, mClip);
        color = isWalkingPressed ? R.color.turquoise_dark : R.color.turquoise;
        canvas.drawPath(getTrianglePath(mP1, mP2, mP3), getPaintShape(getResources()
                .getColor(color)));

        x = (float) ((getWidth() / 4) - mWalkingBitmap.getWidth()/1.5);
        y = (getHeight()/2) - mWalkingBitmap.getHeight()/2;
        canvas.drawBitmap(mWalkingBitmap, x, y, null);

        //triangle left
        mP1.set(getWidth(), 0);
        mP2.set(mP1.x, mP1.y + getHeight());
        mP3.set(mP1.x - getWidth() / 2, mP1.y + getHeight() / 2);
        path = getTrianglePath(mP1, mP2, mP3);
        mRegionBiking.setPath(path, mClip);
        color = isBikingPressed ? R.color.brown_dark : R.color.brown;
        canvas.drawPath(getTrianglePath(mP1, mP2, mP3), getPaintShape(getResources()
                .getColor(color)));

        x = 3 * (getWidth() / 4) - mCyclingBitmap.getWidth()/3;
        y = (getHeight()/2) - mCyclingBitmap.getHeight()/2;
        canvas.drawBitmap(mCyclingBitmap, x, y, null);

        //triangle down
        mP1.set(0, 0);
        mP2.set(mP1.x + getWidth(), mP1.y);
        mP3.set(mP1.x + (getWidth() / 2), mP1.y + getHeight() / 2);
        path = getTrianglePath(mP1, mP2, mP3);
        mRegionRunning.setPath(path, mClip);
        color = isRunningPressed ? R.color.lime_dark : R.color.lime;
        canvas.drawPath(path, getPaintShape(getResources()
                .getColor(color)));

        x = (getWidth() / 2) - mRunningBitmap.getWidth()/2;
        y = (getHeight() /4) - mRunningBitmap.getHeight()/3;
        canvas.drawBitmap(mRunningBitmap, x, y, null);
    }

    private Path getTrianglePath(Point p1, Point p2, Point p3) {
        Path path = new Path();
        path.moveTo(p1.x, p1.y);
        path.lineTo(p2.x, p2.y);
        path.lineTo(p3.x, p3.y);
        return path;
    }

    private Paint getPaintShape(int color) {
        Paint paintShape = new Paint();
        paintShape.setStyle(Paint.Style.FILL);
        paintShape.setColor(color);
        paintShape.setTextSize(30);
        return paintShape;
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (mRegionRunning.contains(Math.round(event.getX()), Math.round(event.getY()))) {
                    isRunningPressed = true;
                } else if (mRegionWalking.contains(Math.round(event.getX()), Math.round(event.getY()))) {
                    isWalkingPressed = true;
                } else if (mRegionBiking.contains(Math.round(event.getX()), Math.round(event.getY()))) {
                    isBikingPressed = true;
                } else {
                    isOtherPressed = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                Preconditions.checkNotNull(mListener);
                if (mRegionRunning.contains(Math.round(event.getX()), Math.round(event.getY()))) {
                    isRunningPressed = false;
                    mListener.onClick(ActivityType.Running);
                } else if (mRegionWalking.contains(Math.round(event.getX()), Math.round(event.getY()))) {
                    isWalkingPressed = false;
                    mListener.onClick(ActivityType.Walking);
                } else if (mRegionBiking.contains(Math.round(event.getX()), Math.round(event.getY()))) {
                    isBikingPressed = false;
                    mListener.onClick(ActivityType.Biking);
                } else {
                    isOtherPressed = false;
                    mListener.onClick(ActivityType.Other);
                }
                break;
        }
        this.invalidate();
        return true;
    }
}
