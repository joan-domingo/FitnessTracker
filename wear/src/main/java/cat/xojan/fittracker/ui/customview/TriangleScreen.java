package cat.xojan.fittracker.ui.customview;

import android.content.Context;
import android.content.Intent;
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

import com.google.android.gms.fitness.FitnessActivities;

import cat.xojan.fittracker.R;
import cat.xojan.fittracker.ui.activity.WorkoutActivity;

public class TriangleScreen extends View {

    private Region mRegionWalking;
    private Region mRegionRunning;
    private Region mRegionBiking;
    private Region mClip;

    private Point mP1;
    private Point mP2;
    private Point mP3;

    private float x1;
    private float y1;

    private Bitmap mRunningBitmap;
    private Bitmap mCyclingBitmap;
    private Bitmap mWalkingBitmap;

    public TriangleScreen(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        Resources res = getResources();
        mRegionWalking = new Region();
        mRegionRunning = new Region();
        mRegionBiking = new Region();
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

        //triangle up
        mP1.set(0, getHeight());
        mP2.set(mP1.x + getWidth(), mP1.y);
        mP3.set(mP1.x + (getWidth() / 2), mP1.y - getHeight() / 2);
        path = getTrianglePath(mP1, mP2, mP3);
        mRegionRunning.setPath(path, mClip);
        canvas.drawPath(path, getPaintShape(getResources()
                .getColor(R.color.garnet_icon)));

        x = (getWidth() / 2) - mRunningBitmap.getWidth()/2;
        y = 3 * (getHeight() /4) - mRunningBitmap.getHeight()/3;
        canvas.drawBitmap(mRunningBitmap, x, y, null);

        //triangle right
        mP1.set(0, 0);
        mP2.set(mP1.x, mP1.y + getHeight());
        mP3.set(mP1.x + getWidth() / 2, mP1.y + getHeight() / 2);
        path = getTrianglePath(mP1, mP2, mP3);
        mRegionWalking.setPath(path, mClip);
        canvas.drawPath(getTrianglePath(mP1, mP2, mP3), getPaintShape(getResources()
                .getColor(R.color.orange_icon)));

        x = (float) ((getWidth() / 4) - mWalkingBitmap.getWidth()/1.5);
        y = (getHeight()/2) - mWalkingBitmap.getHeight()/2;
        canvas.drawBitmap(mWalkingBitmap, x, y, null);

        //triangle left
        mP1.set(getWidth(), 0);
        mP2.set(mP1.x, mP1.y + getHeight());
        mP3.set(mP1.x - getWidth() / 2, mP1.y + getHeight() / 2);
        path = getTrianglePath(mP1, mP2, mP3);
        mRegionBiking.setPath(path, mClip);
        canvas.drawPath(getTrianglePath(mP1, mP2, mP3), getPaintShape(getResources()
                .getColor(R.color.green_icon)));

        x = 3 * (getWidth() / 4) - mCyclingBitmap.getWidth()/3;
        y = (getHeight()/2) - mCyclingBitmap.getHeight()/2;
        canvas.drawBitmap(mCyclingBitmap, x, y, null);
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
                x1 = event.getX();
                y1 = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                if (x1 == event.getX() && y1 == event.getY()) {
                    if (mRegionRunning.contains(Math.round(event.getX()), Math.round(event.getY()))) {
                        startActivity(FitnessActivities.RUNNING);
                    } else if (mRegionWalking.contains(Math.round(event.getX()), Math.round(event.getY()))) {
                        startActivity(FitnessActivities.WALKING);
                    } else if (mRegionBiking.contains(Math.round(event.getX()), Math.round(event.getY()))) {
                        startActivity(FitnessActivities.BIKING);
                    }
                }
                break;
        }
        return true;
    }

    private void startActivity(String activityType) {
        Intent i = new Intent(getContext(), WorkoutActivity.class);
        i.putExtra("EXTRA_ACTIVITY_TYPE", activityType);
        getContext().startActivity(i);
    }
}
