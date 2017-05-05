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

import java.util.ArrayList;
import java.util.List;

import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.fragments.BaseClickableFragment;
import ar.com.tzulberti.archerytraining.model.Coordinate;

/**
 * Created by tzulberti on 4/25/17.
 */

public class PracticeTestingFragment extends BaseClickableFragment implements View.OnTouchListener, View.OnLongClickListener {

    private static final int Y_PADDING = -80;

    private ImageView targetImageView;

    private int targetCenterX = -1;
    private int targetCenterY = -1;
    private float imageScale = -1;
    private int pointWidth = -1;

    private static final int IMAGE_WIDTH = 512;

    private Bitmap imageBitmap;
    private List<Coordinate> coordinates;
    private Paint currentImpactPaint;
    private Paint finalImpactPaint;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.practice_testing, container, false);
        this.targetImageView = (ImageView) view.findViewById(R.id.photo_view);
        this.targetImageView.setOnTouchListener(this);
        this.targetImageView.setOnLongClickListener(this);

        this.coordinates = new ArrayList<Coordinate>();
        this.currentImpactPaint = new Paint();
        this.currentImpactPaint.setAntiAlias(true);
        this.currentImpactPaint.setColor(Color.MAGENTA);

        this.finalImpactPaint = new Paint();
        this.finalImpactPaint.setAntiAlias(true);
        this.finalImpactPaint.setColor(Color.LTGRAY);


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

        if (this.imageBitmap == null) {
            BitmapFactory.Options myOptions = new BitmapFactory.Options();
            myOptions.inScaled = false;
            myOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// important

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.complete_archery_target, myOptions);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.BLUE);

            this.imageBitmap = Bitmap.createBitmap(bitmap);
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
            Coordinate currentImpact = new Coordinate(event.getX() / this.imageScale, event.getY() / this.imageScale);
            this.addTargetImpact(currentImpact.x, currentImpact.y, false);

        } else if (event.getAction() == MotionEvent.ACTION_UP){
            Coordinate currentImpact = new Coordinate(event.getX() / this.imageScale, event.getY() / this.imageScale);
            this.coordinates.add(currentImpact);
            this.addTargetImpact(currentImpact.x, currentImpact.y, true);
        } else {
            //System.out.println(String.format("%s - %s - %s", event.getAction(), event.getX(), event.getY()));
        }
        return false;
    }

    private void currentScore(float x, float y) {
        double distance = Math.sqrt(Math.pow(x - this.targetCenterX, 2) + Math.pow(y - this.targetCenterY, 2));
        //System.out.println(String.format("Current score: %s", 10 - Math.floor(distance / this.pointWidth)));
    }

    private void addTargetImpact(float x, float y, boolean isFinal) {
        Bitmap mutableBitmap = this.imageBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Paint paint = this.finalImpactPaint;
        if (! isFinal) {
            paint = this.currentImpactPaint;
        } else {
            this.imageBitmap = mutableBitmap;
        }
        Canvas canvas = new Canvas(mutableBitmap);
        canvas.drawCircle(x, y + Y_PADDING, 10, paint);

        this.targetImageView.setAdjustViewBounds(true);
        this.targetImageView.setImageBitmap(mutableBitmap);
    }





    @Override
    public boolean onLongClick(View v) {
        System.out.println("On long click");
        return false;
    }
}
