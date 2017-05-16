package ar.com.tzulberti.archerytraining.fragments.practice;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;





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
    private TextView currentScoreText;

    private float targetCenterX = -1;
    private float targetCenterY = -1;
    private float imageScale = -1;
    private float pointWidth = -1;

    private static final float IMAGE_WIDTH = 512;
    private static final float ARROW_IMPACT_RADIUS = 5;

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

        this.currentScoreText = (TextView) view.findViewById(R.id.current_score);

        return view;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (this.targetCenterX == -1) {
            this.imageScale = Math.min(this.targetImageView.getWidth(), this.targetImageView.getHeight()) / IMAGE_WIDTH;
            this.targetCenterX = this.targetImageView.getWidth() / (2 * this.imageScale);
            this.targetCenterY = this.targetImageView.getHeight() / (2 * this.imageScale);
            this.pointWidth = Math.min(this.targetCenterX, this.targetCenterY) / 10;
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

        int eventAction = event.getAction();
        if (eventAction == MotionEvent.ACTION_DOWN || eventAction == MotionEvent.ACTION_MOVE || eventAction == MotionEvent.ACTION_UP) {
            Coordinate currentImpact = new Coordinate(event.getX() / this.imageScale, event.getY() / this.imageScale);
            boolean isFinal = (eventAction == MotionEvent.ACTION_UP);
            this.addTargetImpact(currentImpact.x, currentImpact.y, isFinal);
            if (isFinal) {
                this.coordinates.add(currentImpact);
            }

        }
        return false;
    }

    private void currentScore(float x, float y, boolean isFinal) {
        double distance = Math.sqrt(Math.pow(x - this.targetCenterX, 2) + Math.pow(y + Y_PADDING - this.targetCenterY, 2));
        this.currentScoreText.setText(String.valueOf(10 - Math.floor(distance / this.pointWidth)));
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
        canvas.drawCircle(x, y + Y_PADDING, ARROW_IMPACT_RADIUS, paint);
        this.currentScore(x, y, isFinal);
        this.targetImageView.setAdjustViewBounds(true);
        this.targetImageView.setImageBitmap(mutableBitmap);
    }

    @Override
    public void handleClick(View v) {
    }

    @Override
    public boolean onLongClick(View v) {
        System.out.println("On long click");
        return false;
    }
}
