package ar.com.tzulberti.archerytraining.fragments.practice;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;

import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.fragments.BaseClickableFragment;

/**
 * Created by tzulberti on 4/25/17.
 */

public class PracticeTestingFragment extends BaseClickableFragment implements View.OnTouchListener, View.OnLongClickListener {

    private ImageView targetImageView;

    private int targetCenterX = -1;
    private int targetCenterY = -1;
    private float imageScale = -1;
    private int pointWidth = -1;

    private static final int IMAGE_WIDTH = 512;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.practice_testing, container, false);
        this.targetImageView = (ImageView) view.findViewById(R.id.photo_view);
        this.targetImageView.setOnTouchListener(this);
        this.targetImageView.setOnLongClickListener(this);

        return view;
    }

    @Override
    public void handleClick(View v) {

    }



    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (this.targetCenterX == -1) {
            this.targetCenterX = this.targetImageView.getWidth() / 2;
            this.targetCenterY = this.targetImageView.getHeight() / 2;
            this.pointWidth = Math.min(this.targetCenterX, this.targetCenterY) / 10;
            this.imageScale = Math.min(this.targetImageView.getWidth(), this.targetImageView.getHeight()) / IMAGE_WIDTH;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // when the user clicks on a point in the mouse
            //System.out.println(String.format("Start %s - %s", event.getX(), event.getY()));

            //System.out.println(String.format("%s - %s", this.targetImageView.getMaxHeight(), this.targetImageView.getMaxWidth()));
            //System.out.println(String.format("%s - %s", this.targetImageView.getX(), this.targetImageView.getY()));
            //System.out.println(String.format("%s - %s", this.targetImageView.getWidth(), this.targetImageView.getHeight()));
            //System.out.println(String.format("%s - %s", this.targetImageView.getMaxWidth(), this.targetImageView.getMaxHeight()));

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            // when the user starts moving
            this.currentScore(event.getX(), event.getY());
        } else if (event.getAction() == MotionEvent.ACTION_UP){
            // TODOS estos valores son 0
            //System.err.println(String.format("EMPEZANDO: %s - %s - %s", this.targetImageView.getX(), this.targetImageView.getLeft(), this.targetImageView.getTranslationX()));
            // Por alguna razon el resultado de esto es 1080 - 1536
            //System.err.println(String.format("EMPEZANDO2: %s - %s", this.targetImageView.getWidth(), this.targetImageView.getHeight()));
            //System.err.println(String.format("EMPEZANDO3: %s - %s", this.targetImageView.getMaxHeight(), this.targetImageView.getWidth()));
            //System.err.println(String.format("EMPEZANDO4: %s - %s", this.targetImageView.getMeasuredHeight(), this.targetImageView.getMeasuredWidth()));
            System.err.println(String.format("EMPEZANDO5: %s - %s", this.targetImageView.getScaleX(), this.targetImageView.getScaleY()));

            BitmapFactory.Options myOptions = new BitmapFactory.Options();
            //myOptions.inDither = true;
            myOptions.inScaled = false;
            myOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// important
            //myOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.complete_archery_target, myOptions);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.BLUE);

            Bitmap workingBitmap = Bitmap.createBitmap(bitmap);
            Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);

            Canvas canvas = new Canvas(mutableBitmap);
            //canvas.drawCircle(0, 0, 25, paint);
            /*
            for (int i = 0; i < 10; i++) {
                canvas.drawCircle(i * 100, i * 100, 25, paint);
            }
            */
            canvas.drawCircle(event.getX() / this.imageScale, event.getY() / this.imageScale, 25, paint);
            /*
            canvas.drawCircle(this.targetImageView.getWidth(), 0, 25, paint);
            canvas.drawCircle(this.targetImageView.getHeight(), 0, 25, paint);
            canvas.drawCircle(this.targetImageView.getHeight(), this.targetImageView.getWidth(), 25, paint);
            */

            //canvas.drawCircle(this.targetImageView.getTop(), this.targetImageView.getLeft(), 25, paint);
            /*
            canvas.drawCircle(200, 100, 25, paint);
            canvas.drawCircle(300, 200, 25, paint);
            canvas.drawCircle(400, 300, 25, paint);
            canvas.drawCircle(500, 400, 25, paint);
            canvas.drawCircle(600, 500, 25, paint);
            canvas.drawCircle(700, 600, 25, paint);
            canvas.drawCircle(800, 700, 25, paint);
            canvas.drawCircle(900, 800, 25, paint);
            */
            //canvas.drawCircle(event.getX() - this.targetImageView.getX(), event.getY() - this.targetImageView.getY(), 25, paint);
            //System.err.println(String.format("END %s - %s - %s - %s", event.getX(), event.getY(), event.getX() - this.targetImageView.getLeft(), event.getY() - this.targetImageView.getTop()));
            /**
            canvas.drawCircle(this.targetImageView.getTop(), this.targetImageView.getRight(), 25, paint);
            canvas.drawCircle(this.targetImageView.getBottom(), this.targetImageView.getRight(), 25, paint);
            canvas.drawCircle(this.targetImageView.getBottom(), this.targetImageView.getLeft(), 25, paint);
            */

            this.targetImageView.setAdjustViewBounds(true);
            this.targetImageView.setImageBitmap(mutableBitmap);


            /*
            // when the user finish moving
            Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            canvas.drawCircle(event.getX(), event.getY(), 80, paint);

            Drawable asd = this.targetImageView.getDrawable();
            ShapeDrawable sd = new ShapeDrawable(new OvalShape());
            asd.draw(sd);
            //this.targetImageView.setImageBitmap(bitmap);
            this.targetImageView.draw(canvas);
            */
            //System.out.println(String.format("END %s - %s", event.getX(), event.getY()));
        } else {
            //System.out.println(String.format("%s - %s - %s", event.getAction(), event.getX(), event.getY()));
        }
        return false;
    }

    private void currentScore(float x, float y) {
        double distance = Math.sqrt(Math.pow(x - this.targetCenterX, 2) + Math.pow(y - this.targetCenterY, 2));
        //System.out.println(String.format("Current score: %s", 10 - Math.floor(distance / this.pointWidth)));
    }

    @Override
    public boolean onLongClick(View v) {
        System.out.println("On long click");
        return false;
    }
}
