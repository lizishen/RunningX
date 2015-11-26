package com.votors.floodfill;
import java.util.LinkedList;
import java.util.Queue;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {

    private RelativeLayout dashBoard;
    private MyView myView;
    public ImageView image;

    Button b_red, b_blue, b_green, b_orange, b_clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myView = new MyView(this);
        setContentView(R.layout.activity_main);
        findViewById(R.id.dashBoard);

        b_red = (Button) findViewById(R.id.b_red);
        b_blue = (Button) findViewById(R.id.b_blue);
        b_green = (Button) findViewById(R.id.b_green);
        b_orange = (Button) findViewById(R.id.b_orange);

        b_red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myView.changePaintColor(0xFFFF0000);
            }
        });

        b_blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myView.changePaintColor(0xFF0000FF);
            }
        });

        b_green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myView.changePaintColor(0xFF00FF00);
            }
        });

        b_orange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myView.changePaintColor(0xFFFF9900);
            }
        });

        dashBoard = (RelativeLayout) findViewById(R.id.dashBoard);
        dashBoard.addView(myView);

    }

    public class MyView extends View {

        private Paint paint;
        private Path path;
        public Bitmap mBitmap;
        public ProgressDialog pd;
        final Point p1 = new Point();
        public Canvas canvas;

        //Bitmap mutableBitmap ;
        public MyView(Context context) {

            super(context);

            this.paint = new Paint();
            this.paint.setAntiAlias(true);
            pd = new ProgressDialog(context);
            this.paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(5f);
            mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.forme).copy(Bitmap.Config.ARGB_8888, true);
            this.path = new Path();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            this.canvas = canvas;
            //this.paint.setColor(Color.RED);

            canvas.drawBitmap(mBitmap, 0, 0, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {

            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:

                    p1.x = (int) x;
                    p1.y = (int) y;
                    final int sourceColor = mBitmap.getPixel((int) x, (int) y);
                    final int targetColor = paint.getColor();
                    new TheTask(mBitmap, p1, sourceColor, targetColor).execute();
                    invalidate();
            }
            return true;
        }

        public void clear() {
            path.reset();
            invalidate();
        }

        public int getCurrentPaintColor() {
            return paint.getColor();
        }

        public void changePaintColor(int color){
            this.paint.setColor(color);
        }

        class TheTask extends AsyncTask<Void, Integer, Void> {

            Bitmap bmp;
            Point pt;
            int replacementColor, targetColor;

            public TheTask(Bitmap bm, Point p, int sc, int tc) {
                this.bmp = bm;
                this.pt = p;
                this.replacementColor = tc;
                this.targetColor = sc;
                pd.setMessage("Filling....");
                pd.show();
            }

            @Override
            protected void onPreExecute() {
                pd.show();

            }

            @Override
            protected void onProgressUpdate(Integer... values) {

            }

            @Override
            protected Void doInBackground(Void... params) {
                FloodFill f = new FloodFill();
                f.floodFill(bmp, pt, targetColor, replacementColor);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                pd.dismiss();
                invalidate();
            }
        }
    }

    // flood fill
    public class FloodFill {
        public int DIFF =100;
        boolean colorDiff(int c1,int c2) {
            int r1,g1,b1,r2,g2,b2;
            r1 = (c1 & 0xFF0000)>>16;
            g1 = (c1 & 0x00FF00)>>8;
            b1 = c1 & 0x0000FF;
            r2 = (c2 & 0xFF0000)>>16;
            g2 = (c2 & 0x00FF00)>>8;
            b2 = c2 & 0x0000FF;
            if ( Math.sqrt((r1-r2)*(r1-r2) + (g1-g2)*(g1-g2) + (b1-b2)*(b1-b2)) < DIFF) {
                return true;
            }

            return false;
        }
        public void floodFill(Bitmap image, Point node, int targetColor, int replacementColor) {

            int width = image.getWidth();
            int height = image.getHeight();
            int target = targetColor;
            int replacement = replacementColor;

            if (target != replacement) {
                Queue<Point> queue = new LinkedList<Point>();
                do {

                    int x = node.x;
                    int y = node.y;
                    while (x > 0 && colorDiff(image.getPixel(x - 1, y), target)) {
                        x--;
                    }

                    boolean spanUp = false;
                    boolean spanDown = false;
                    while (x < width && colorDiff(image.getPixel(x, y), target)) {
                        image.setPixel(x, y, replacement);
                        if (!spanUp && y > 0 && colorDiff(image.getPixel(x, y - 1), target)) {
                            queue.add(new Point(x, y - 1));
                            spanUp = true;
                        } else if (spanUp && y > 0 && !colorDiff(image.getPixel(x, y - 1), target)) {
                            spanUp = false;
                        }
                        if (!spanDown && y < height - 1 && colorDiff(image.getPixel(x, y + 1), target)) {
                            queue.add(new Point(x, y + 1));
                            spanDown = true;
                        } else if (spanDown && y < (height - 1) && !colorDiff(image.getPixel(x, y + 1), target)) {
                            spanDown = false;
                        }
                        x++;
                    }

                } while ((node = queue.poll()) != null);
            }
        }
    }
}