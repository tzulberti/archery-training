package ar.com.tzulberti.archerytraining.fragments.tournament;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.reflect.ConstructorUtils;

import java.util.ArrayList;
import java.util.List;

import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.model.Coordinate;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentConfiguration;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerie;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerieArrow;

/**
 * Created by tzulberti on 4/25/17.
 */

public class ViewSerieInformationFragment extends BaseTournamentFragment implements View.OnTouchListener, View.OnLongClickListener {

    private static final int Y_PADDING = -80;
    private static final float IMAGE_WIDTH = 512;
    private static final float ARROW_IMPACT_RADIUS = 5;
    private ImageView targetImageView;
    private TextView[] currentScoreText;
    private float targetCenterX = -1;
    private float targetCenterY = -1;
    private float imageScale = -1;
    private float pointWidth = -1;
    private Bitmap imageBitmap;
    private List<Coordinate> coordinates;
    private Paint currentImpactPaint;
    private Paint finalImpactPaint;

    private TournamentSerie tournamentSerie;
    private boolean creatingSerie;

    public static ViewSerieInformationFragment createInstance(TournamentSerie tournamentSerie, boolean creatingSerie) {
        ViewSerieInformationFragment res = new ViewSerieInformationFragment();
        res.tournamentSerie = tournamentSerie;
        res.creatingSerie = creatingSerie;
        return res;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tournament_view_serie_arrow, container, false);
        this.targetImageView = (ImageView) view.findViewById(R.id.photo_view);
        this.setObjects();
        this.targetImageView.setOnTouchListener(this);
        this.targetImageView.setOnLongClickListener(this);

        this.coordinates = new ArrayList<Coordinate>();
        this.currentImpactPaint = new Paint();
        this.currentImpactPaint.setAntiAlias(true);
        this.currentImpactPaint.setColor(Color.MAGENTA);

        this.finalImpactPaint = new Paint();
        this.finalImpactPaint.setAntiAlias(true);
        this.finalImpactPaint.setColor(Color.LTGRAY);

        this.currentScoreText = new TextView[6];
        this.currentScoreText[0] = (TextView) view.findViewById(R.id.current_score1);
        this.currentScoreText[1] = (TextView) view.findViewById(R.id.current_score2);
        this.currentScoreText[2] = (TextView) view.findViewById(R.id.current_score3);
        this.currentScoreText[3] = (TextView) view.findViewById(R.id.current_score4);
        this.currentScoreText[4] = (TextView) view.findViewById(R.id.current_score5);
        this.currentScoreText[5] = (TextView) view.findViewById(R.id.current_score6);

        for (TournamentSerieArrow serieArrow : this.tournamentSerie.arrows) {
            this.addTargetImpact(serieArrow.xPosition, serieArrow.yPosition, true);
        }

        return view;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (! this.creatingSerie || this.tournamentSerie.arrows.size() == TournamentConfiguration.MAX_ARROW_PER_SERIES) {
            // if the user is viewing an existing value, then he can not
            // set a new score value
            return false;
        }

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


    private void addTargetImpact(float x, float y, boolean isFinal) {
        Bitmap mutableBitmap = this.imageBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Paint paint = this.finalImpactPaint;
        if (!isFinal) {
            paint = this.currentImpactPaint;
        } else {
            this.imageBitmap = mutableBitmap;
        }
        Canvas canvas = new Canvas(mutableBitmap);
        canvas.drawCircle(x, y + Y_PADDING, ARROW_IMPACT_RADIUS, paint);
        double distance = Math.sqrt(Math.pow(x - this.targetCenterX, 2) + Math.pow(y + Y_PADDING - this.targetCenterY, 2));
        int score = (int) (10 - Math.floor(distance / this.pointWidth));
        if (score < 0) {
            score = 0;
        }
        this.targetImageView.setAdjustViewBounds(true);
        this.targetImageView.setImageBitmap(mutableBitmap);


        TextView scoreText = this.currentScoreText[this.tournamentSerie.arrows.size()];
        scoreText.getBackground().setColorFilter(new PorterDuffColorFilter(this.getColor(score), PorterDuff.Mode.SRC_IN));
        scoreText.setText(this.getUserScore(score));
        scoreText.setTextColor(this.getFontColor(score));

        if (isFinal) {
            TournamentSerieArrow serieArrow = new TournamentSerieArrow();
            serieArrow.xPosition = x;
            serieArrow.yPosition = y;
            serieArrow.score = score;
            this.tournamentSerie.totalScore += score;
            this.tournamentSerie.arrows.add(serieArrow);
        }
    }


    public String getUserScore(int score) {
        if (score == 0) {
          return " M ";
        } else if (score < 10) {
            return "  " + String.valueOf(score) + " ";
        } else {
            return String.valueOf(score) + " ";
        }
    }

    public int getColor(int score) {
        int res = 0;
        switch (score) {
            case 10 : case 9:
                        res = Color.YELLOW;
                        break;
            case 8 : case 7:
                        res = Color.RED;
                        break;
            case 6 : case 5:
                        res = Color.BLUE;
                        break;
            case 4 : case 3:
                        res = Color.BLACK;
                        break;
            case 2 : case 1:
                        res = Color.WHITE;
                        break;
            case 0:
                        res = Color.BLACK;
                        break;
        }
        return res;
    }

    public int getFontColor(int score) {
        if (score == 0 || score == 3 || score == 4) {
            return Color.WHITE;
        } else {
            return Color.BLACK;
        }


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
