package ar.com.tzulberti.archerytraining.fragments.tournament;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ar.com.tzulberti.archerytraining.MainActivity;
import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.helper.TournamentHelper;
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
    private Button previousSerieButton;
    private Button nextSerieButton;

    private float targetCenterX = -1;
    private float targetCenterY = -1;
    private float imageScale = -1;
    private float pointWidth = -1;
    private Bitmap imageBitmap;
    private List<Coordinate> coordinates;
    private Paint currentImpactPaint;
    private Paint finalImpactPaint;

    private TournamentSerie tournamentSerie;


    public static ViewSerieInformationFragment createInstance(TournamentSerie tournamentSerie) {
        ViewSerieInformationFragment res = new ViewSerieInformationFragment();
        res.tournamentSerie = tournamentSerie;
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

        this.nextSerieButton = (Button) view.findViewById(R.id.btn_serie_next);
        this.previousSerieButton = (Button) view.findViewById(R.id.btn_serie_previous);
        this.nextSerieButton.setEnabled(false);
        this.previousSerieButton.setEnabled(false);

        ViewTreeObserver vto = this.targetImageView.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                targetImageView.getViewTreeObserver().removeOnPreDrawListener(this);
                initializeValues();
                return true;
            }
        });

        return view;
    }

    protected void initializeValues() {
        this.imageScale = Math.min(this.targetImageView.getWidth(), this.targetImageView.getHeight()) / IMAGE_WIDTH;
        this.targetCenterX = this.targetImageView.getWidth() / (2 * this.imageScale);
        this.targetCenterY = this.targetImageView.getHeight() / (2 * this.imageScale);
        this.pointWidth = Math.min(this.targetCenterX, this.targetCenterY) / 10;


        BitmapFactory.Options myOptions = new BitmapFactory.Options();
        myOptions.inScaled = false;
        myOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// important

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.complete_archery_target, myOptions);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);

        this.imageBitmap = Bitmap.createBitmap(bitmap);

        int arrowIndex = 0;
        for (TournamentSerieArrow serieArrow : this.tournamentSerie.arrows) {
            this.addTargetImpact(serieArrow.xPosition, serieArrow.yPosition, true, true, arrowIndex);
            arrowIndex += 1;
        }

        if (this.tournamentSerie.arrows.size() == TournamentConfiguration.MAX_ARROW_PER_SERIES) {
            this.nextSerieButton.setEnabled(true);
            this.previousSerieButton.setEnabled(this.tournamentSerie.index > 1);
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (this.tournamentSerie.arrows.size() == TournamentConfiguration.MAX_ARROW_PER_SERIES) {
            // already got the max number of arrows so there is nothing specific to do
            return false;
        }

        int eventAction = event.getAction();
        boolean isFinal = false;
        if (eventAction == MotionEvent.ACTION_DOWN || eventAction == MotionEvent.ACTION_MOVE || eventAction == MotionEvent.ACTION_UP) {
            Coordinate currentImpact = new Coordinate(event.getX() / this.imageScale, event.getY() / this.imageScale);
            isFinal = (eventAction == MotionEvent.ACTION_UP);
            this.addTargetImpact(currentImpact.x, currentImpact.y, isFinal, false, this.tournamentSerie.arrows.size());
            if (isFinal) {
                this.coordinates.add(currentImpact);
            }
        }

        if (isFinal && this.tournamentSerie.arrows.size() == TournamentConfiguration.MAX_ARROW_PER_SERIES) {
            // enable the buttons to save the current serie
            if (this.tournamentSerie.index > 1) {
                this.previousSerieButton.setEnabled(true);
            }
            System.err.println("Entro en el ouTouch con el maximo cantidad de flechas");
            this.nextSerieButton.setEnabled(true);

            // update the series information after updating the arrows by it's score
            // so it can be showed on that order
            Collections.sort(this.tournamentSerie.arrows, new Comparator<TournamentSerieArrow>() {

                @Override
                public int compare(TournamentSerieArrow o1, TournamentSerieArrow o2) {
                    return Integer.compare(o1.score, o2.score) * -1;
                }
            });
            this.tournamentDAO.saveTournamentSerieInformation(this.tournamentSerie);
        }

        return false;
    }


    private void addTargetImpact(float x, float y, boolean isFinal, boolean showingExisting, int arrowIndex) {

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


        TextView scoreText = this.currentScoreText[arrowIndex];

        scoreText.getBackground().setColorFilter(new PorterDuffColorFilter(TournamentHelper.getBackground(score), PorterDuff.Mode.SRC_IN));
        scoreText.setText(TournamentHelper.getUserScore(score));
        scoreText.setTextColor(TournamentHelper.getFontColor(score));

        if (isFinal && ! showingExisting) {
            TournamentSerieArrow serieArrow = new TournamentSerieArrow();
            serieArrow.xPosition = x;
            serieArrow.yPosition = y;
            serieArrow.score = score;
            this.tournamentSerie.totalScore += score;
            this.tournamentSerie.arrows.add(serieArrow);
        }
    }


    @Override
    public void handleClick(View v) {
        final MainActivity activity = (MainActivity) getActivity();

        if (v.getId() == R.id.btn_serie_previous || v.getId() == R.id.btn_serie_next) {
            TournamentSerie transitionSerie = null;
            if (v.getId() == R.id.btn_serie_previous) {
                // -2 is required because the first index is 1.
                transitionSerie = this.tournamentSerie.tournament.series.get(this.tournamentSerie.index - 2);
            } else {
                // same here... there isn't any need to add +1 because the serie already starts at 1
                if (this.tournamentSerie.tournament.series.size() > this.tournamentSerie.index) {
                    transitionSerie = this.tournamentSerie.tournament.series.get(this.tournamentSerie.index);
                } else {
                    // creating a new serie for the tournament
                    transitionSerie = this.tournamentDAO.createNewSerie(this.tournamentSerie.tournament);
                }
            }

            ViewSerieInformationFragment practiceTestingFragment = ViewSerieInformationFragment.createInstance(transitionSerie);
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, practiceTestingFragment)
                    .commit();
        } else {
            throw new RuntimeException("Unknown click button");
        }


    }

    @Override
    public boolean onLongClick(View v) {
        System.out.println("On long click");
        return false;
    }
}
