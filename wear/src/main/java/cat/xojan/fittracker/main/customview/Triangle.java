package cat.xojan.fittracker.main.customview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import cat.xojan.fittracker.R;
import cat.xojan.fittracker.workout.StartActivity;

public class Triangle extends View {

    private static final String NAMESPACE = "http://schemas.android.com/apk/res-auto";
    private static final String DIRECTION = "direction";
    private static final String DIRECTION_UP = "up";
    private static final String DIRECTION_DOWN = "down";
    private static final String DIRECTION_RIGHT = "right";
    private static final String DIRECTION_LEFT = "left";

    private final String mTriangleDirection;
    private final Region mRegionWalking;
    private final Region mRegionRunning;
    private final Region mRegionBiking;

    public Triangle(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTriangleDirection = attrs.getAttributeValue(NAMESPACE, DIRECTION);
        mRegionWalking = new Region();
        mRegionRunning = new Region();
        mRegionBiking = new Region();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Region clip = new Region(0, 0, canvas.getWidth(), canvas.getHeight());
        Point p1, p2, p3;
        Path path;
        switch (mTriangleDirection) {
            case DIRECTION_DOWN:
                p1 = new Point(0, 0);
                p2 = new Point(p1.x + getWidth(), p1.y);
                p3 = new Point(p1.x + (getWidth() / 2), p1.y + getHeight() / 2);
                canvas.drawPath(getTrianglePath(p1, p2, p3), getPaintShape(Color.BLACK));
                break;
            case DIRECTION_UP:
                p1 = new Point(0, getHeight());
                p2 = new Point(p1.x + getWidth(), p1.y);
                p3 = new Point(p1.x + (getWidth() / 2), p1.y - getHeight() / 2);
                path = getTrianglePath(p1, p2, p3);
                mRegionRunning.setPath(path, clip);
                canvas.drawPath(path, getPaintShape(getResources()
                .getColor(R.color.garnet_icon)));
                break;
            case DIRECTION_RIGHT:
                p1 = new Point(0, 0);
                p2 = new Point(p1.x, p1.y + getHeight());
                p3 = new Point(p1.x + getWidth() / 2, p1.y + getHeight() / 2);
                path = getTrianglePath(p1, p2, p3);
                mRegionWalking.setPath(path, clip);
                canvas.drawPath(getTrianglePath(p1, p2, p3), getPaintShape(getResources()
                        .getColor(R.color.orange_icon)));
                break;
            case DIRECTION_LEFT:
                p1 = new Point(getWidth(), 0);
                p2 = new Point(p1.x, p1.y + getHeight());
                p3 = new Point(p1.x - getWidth() / 2, p1.y + getHeight() / 2);
                path = getTrianglePath(p1, p2, p3);
                mRegionBiking.setPath(path, clip);
                canvas.drawPath(getTrianglePath(p1, p2, p3), getPaintShape(getResources()
                .getColor(R.color.green_icon)));
                break;
        }
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
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            if (mRegionRunning.contains(Math.round(event.getX()), Math.round(event.getY()))) {
                startActivity("Running");
            } else if (mRegionWalking.contains(Math.round(event.getX()), Math.round(event.getY()))) {
                startActivity("Walking");
            } else if (mRegionBiking.contains(Math.round(event.getX()), Math.round(event.getY()))) {
                startActivity("Biking");
            }
        }

        return false;
    }

    private void startActivity(String activityType) {
        Intent i = new Intent(getContext(), StartActivity.class);
        i.putExtra("EXTRA_ACTIVITY_TYPE", activityType);
        getContext().startActivity(i);
    }
}
