package com.votors.myandroid;


import android.graphics.Color;
import android.graphics.Path;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;


public class MainActivity extends AppCompatActivity {
    protected static final String TAG = "myapp";
    private FrameLayout mFrame;
    Context contex = getApplication();
    public static final int NSHAPE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFrame = (FrameLayout) findViewById(R.id.frame);
        mFrame.setBackgroundColor(getResources().getColor(R.color.green));

        final MyView view = new MyView(this);
        view.setBackgroundColor(getResources().getColor(R.color.white));
        mFrame.addView(view);

        // Create and set on touch listener
        mFrame.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN: {

                        int pointerIndex = event.getActionIndex();

                        float x = event.getX(pointerIndex);
                        float y = event.getY(pointerIndex);
                        view.addPoin(new MyPoint(x,y));
                        view.invalidate();
                        break;
                    }

                    default:
                        Log.i(TAG, "unknow....." + event.toString());
                }

                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class MyView extends View {
        ArrayList<MyPoint> points = new ArrayList<MyPoint>();
        final private Paint mPaint = new Paint();
        final int NColor = 5;
        final int colors[] = {Color.GREEN, Color.BLUE,Color.CYAN,Color.MAGENTA,Color.DKGRAY};
        int colorPos = 0;
        int PoinSize = 0;

        public MyView(Context context) {
            super(context);
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mPaint.setStrokeWidth(5);
        }

        public void addPoin(MyPoint p) {
            if (points.size() >= NSHAPE*2) {
                clearPoint();
            }
            points.add(p);
        }
        public void clearPoint() {
            points.clear();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            ArrayList<MyPoint> crosses = new ArrayList<MyPoint>();
            mPaint.setStrokeWidth(5);

            if (points.size() > 0) {
                Path path = null;
                colorPos = 0;
                for (int i = 0; i<points.size(); i++) {
                    if ((i+1)%NSHAPE == 1) {
                        //first point of the shape.
                        path = new Path();
                        path.setFillType(Path.FillType.EVEN_ODD);
                        path.moveTo(points.get(i).x, points.get(i).y);
                    } else if ((i + 1) % NSHAPE == 0) {
                        // last point of the shape
                        mPaint.setColor(Color.BLACK);
                        canvas.drawLine(points.get(i - 1).x, points.get(i - 1).y, points.get(i).x, points.get(i).y, mPaint);
                        canvas.drawLine(points.get(i).x, points.get(i).y, points.get(i + 1 - NSHAPE).x, points.get(i + 1 - NSHAPE).y, mPaint);
                        path.lineTo(points.get(i).x, points.get(i).y);
                        path.lineTo(points.get(i+1-NSHAPE).x, points.get(i+1-NSHAPE).y);
                        path.close();
                        mPaint.setColor(colors[colorPos++ % NColor]);
                        canvas.drawPath(path, mPaint);
                    }  else {
                        // mid-point of the shape
                        mPaint.setColor(Color.BLACK);
                        canvas.drawLine(points.get(i - 1).x, points.get(i - 1).y, points.get(i).x, points.get(i).y, mPaint);
                        path.lineTo(points.get(i).x, points.get(i).y);
                    }
                }
            }

            if (points.size() >= NSHAPE*2) {
                // get all the cross points
                for (int i=0; i<points.size(); i+=NSHAPE) {
                    //for every shape
                    for (int j=i+NSHAPE; j<points.size(); j+=NSHAPE) {
                        // not enough point for a integrated shape, break
                        if (points.size() < j + NSHAPE) break;
                        //for every other shape
                        //find point in another shape, point in another shape is treated as crossing point
                        PointInTriangle(points.get(i),points.get(i+1),points.get(i+2),points.get(j),points.get(j+1),points.get(j+2),crosses);
                        PointInTriangle(points.get(j),points.get(j+1),points.get(j+2),points.get(i),points.get(i+1),points.get(i+2),crosses);

                        // find the intersection
                        for (int n=0; n<NSHAPE; n++) {
                            for (int m = 0; m < NSHAPE; m++) {
                                getLineCross(points.get(i+n), points.get(i+(n+1)%NSHAPE), points.get(j+m), points.get(j+(m+1)%NSHAPE), crosses);
                            }
                        }
                    }
                }

                for (MyPoint p: crosses) {
                    Log.i(TAG, "Cross: " + p.id + "\t" + p.p1.id +"," + p.p2.id +"," + p.p3.id +"," + p.p4.id +",");
                }

                // paint the area of cross point as red, order it first
                if (crosses.size() > 1) {
                    int cnt = 0;
                    Path path3 = new Path();
                    path3.setFillType(Path.FillType.EVEN_ODD);
                    mPaint.setColor(Color.YELLOW);
                    path3.moveTo(crosses.get(0).x, crosses.get(0).y);
                    canvas.drawCircle(crosses.get(0).x, crosses.get(0).y, ++cnt * PoinSize, mPaint);

                    MyPoint p;

                    MyPoint first = crosses.remove(0);
                    MyPoint curr = first;
                    MyPoint next = null;
                    while (crosses.size() > 0) {
                        next = null;
                        int i;
                        for (i = 0; i < crosses.size(); i++) {
                            MyPoint temp = crosses.get(i);
                            if ((temp.p1.equals(curr.p1) && temp.p2.equals(curr.p2))
                                || (temp.p1.equals(curr.p3) && temp.p2.equals(curr.p4))
                                || (temp.p3.equals(curr.p3) && temp.p4.equals(curr.p4))
                                || (temp.p3.equals(curr.p1) && temp.p4.equals(curr.p2))) {
                                path3.lineTo(temp.x, temp.y);
                                mPaint.setColor(Color.YELLOW);
                                canvas.drawCircle(crosses.get(i).x, crosses.get(i).y, ++cnt*PoinSize, mPaint);
                                next = temp;
                                break;
                            }
                        }
                        if (next == null) {
                            next = crosses.remove(0);
                            path3.lineTo(next.x, next.y);
                            mPaint.setColor(Color.BLACK);
                            canvas.drawCircle(next.x, next.y, ++cnt*PoinSize, mPaint);
                            Log.i(TAG, "* NOT find co-line point.");
                        } else {
                            next = crosses.remove(i);
                            Log.i(TAG, "* find co-line point.");
                        }
                        p = curr;
                        Log.i(TAG, "curr: " + p.toString() + "\t" + p.p1.id + "," + p.p2.id + "," + p.p3.id + "," + p.p4.id + ",");
                        p = next;
                        Log.i(TAG, "next: " + p.toString() + "\t" + p.p1.id + "," + p.p2.id + "," + p.p3.id + "," + p.p4.id + ",");
                        curr = next;
                    }
                    path3.lineTo(first.x, first.y);
                    path3.close();
                    mPaint.setColor(Color.RED);
                    canvas.drawPath(path3, mPaint);
                }
            }
        }

        MyPoint getLineCross(MyPoint p1, MyPoint p2, MyPoint p3, MyPoint p4, ArrayList<MyPoint> crosses) {
            float a1,b1,a2,b2;
            MyPoint inter;

            if (p2.x-p1.x == 0) {
                p2.x++;
            }
            if (p4.x-p3.x == 0) {
                p4.x++;
            }

            // see : http://www.mathsisfun.com/algebra/line-equation-2points.html
            // y = a*x + b
            a1 = (p2.y-p1.y)/(p2.x-p1.x);
            b1 = p1.y - a1*p1.x;
            a2 = (p4.y-p3.y)/(p4.x-p3.x);
            b2 = p3.y - a2*p3.x;
            if (a1 == a2) {
                return null;
            }

            float x = -(b1-b2)/(a1-a2);
            float y = a1 * x + b1;

            if (x > Math.min(p1.x,p2.x) && x < Math.max(p1.x,p2.x)
                    && y >= Math.min(p1.y,p2.y) && y <= Math.max(p1.y, p2.y)
                    && x >= Math.min(p3.x, p4.x) && x <= Math.max(p3.x, p4.x)
                    && y >= Math.min(p3.y,p4.y) && y <= Math.max(p3.y,p4.y)
                    ) {
                inter = new MyPoint(x,y);
                crosses.add(inter);
                Log.i(TAG, "intersection is " + (int)inter.x + "," + (int)inter.y);
                inter.setLines(p1,p2,p3,p4);
                return inter;
            } else {
                return null;
            }
        }

        float sign (MyPoint p1, MyPoint p2, MyPoint p3)
        {
            return (p1.x - p3.x) * (p2.y - p3.y) - (p2.x - p3.x) * (p1.y - p3.y);
        }

        boolean isPointInTriangle (MyPoint pt, MyPoint v1, MyPoint v2, MyPoint v3)
        {
            boolean b1, b2, b3;
            b1 = sign(pt, v1, v2) < 0.0f;
            b2 = sign(pt, v2, v3) < 0.0f;
            b3 = sign(pt, v3, v1) < 0.0f;
            return ((b1 == b2) && (b2 == b3));
        }
        void PointInTriangle(MyPoint p1, MyPoint p2, MyPoint p3, MyPoint v1, MyPoint v2, MyPoint v3, ArrayList<MyPoint> crosses) {
            if (isPointInTriangle(p1,v1,v2,v3)) {
                p1.setLines(p1,p2,p1,p3);
                crosses.add(p1);
            }
            if (isPointInTriangle(p2,v1,v2,v3)) {
                p2.setLines(p1,p2,p2,p3);
                crosses.add(p2);
            }
            if (isPointInTriangle(p3,v1,v2,v3)) {
                p3.setLines(p2,p3,p1,p3);
                crosses.add(p3);
            }
            return;
        }
    }
}

class MyPoint {
    float x = 0;
    float y = 0;
    public MyPoint p1;
    public MyPoint p2;
    public MyPoint p3;
    public MyPoint p4;
    static int cnt = 0;
    public int id = 0;

    @Override public String toString() {
        return id + ":("+x+"," +y +")";
    }

    public boolean equals(MyPoint that) {
        if (this.id == that.id) {
            return true;
        }
        return false;
    }

    public float getDistance(MyPoint that) {
        return (float)Math.sqrt( (this.x-that.x)*(this.x-that.x) + (this.y-that.y)*(this.y-that.y));
    }

    MyPoint(float x, float y) {
        this.x = x;
        this.y = y;
        id = ++cnt;
    }

    public void setLines(MyPoint p1, MyPoint p2, MyPoint p3, MyPoint p4) {
        // from left to right save the lines
        if (Math.min(p1.x,p2.x) < Math.min(p3.x,p4.x)) {
            if (p1.x < p2.x) {
                this.p1 = p1;
                this.p2 = p2;
            } else {
                this.p1 = p2;
                this.p2 = p1;
            }
            if (p3.x < p4.x) {
                this.p3 = p3;
                this.p4 = p4;
            } else {
                this.p3 = p4;
                this.p4 = p3;
            }
        } else {
            if (p3.x < p4.x) {
                this.p1 = p3;
                this.p2 = p4;
            } else {
                this.p1 = p4;
                this.p2 = p3;
            }
            if (p1.x < p2.x) {
                this.p3 = p1;
                this.p4 = p2;
            } else {
                this.p3 = p2;
                this.p4 = p1;
            }
        }
    }


}

