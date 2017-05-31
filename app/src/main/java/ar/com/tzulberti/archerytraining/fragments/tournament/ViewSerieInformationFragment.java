package ar.com.tzulberti.archerytraining.fragments.tournament;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ar.com.tzulberti.archerytraining.MainActivity;
import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.helper.TournamentHelper;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentConfiguration;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerie;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerieArrow;

/**
 * Created by tzulberti on 4/25/17.
 */

public class ViewSerieInformationFragment extends BaseTournamentFragment implements View.OnTouchListener, View.OnLongClickListener {

    public static final float IMAGE_WIDTH = 512;
    public static final float ARROW_IMPACT_RADIUS = 5;

    private static final int Y_PADDING = -80;


    private ImageView targetImageView;
    private TextView[] currentScoreText;
    private TextView totalSerieScoreText;

    private Button previousSerieButton;
    private Button nextSerieButton;

    private float targetCenterX = -1;
    private float targetCenterY = -1;
    private float imageScale = -1;
    private float pointWidth = -1;
    private Bitmap imageBitmap;
    private Paint currentImpactPaint;
    private Paint finalImpactPaint;

    private boolean canGoBack;
    private TournamentSerie tournamentSerie;


    public static ViewSerieInformationFragment createInstance(TournamentSerie tournamentSerie) {
        ViewSerieInformationFragment res = new ViewSerieInformationFragment();
        res.tournamentSerie = tournamentSerie;
        res.canGoBack = false;
        return res;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.cleanState(container);
        View view = inflater.inflate(R.layout.tournament_view_serie_arrow, container, false);
        this.targetImageView = (ImageView) view.findViewById(R.id.photo_view);
        this.setObjects();
        this.targetImageView.setOnTouchListener(this);
        this.targetImageView.setOnLongClickListener(this);

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

        ((TextView) view.findViewById(R.id.serie_index)).setText(getString(R.string.tournament_serie_current_serie, this.tournamentSerie.index));
        ((TextView) view.findViewById(R.id.total_tournament_score)).setText(String.format("%s / %s", this.tournamentSerie.tournament.totalScore, TournamentConfiguration.MAX_SCORE_FOR_TOURNAMENT));

        this.totalSerieScoreText = (TextView) view.findViewById(R.id.total_serie_score);


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

        if (this.tournamentSerie.index == TournamentConfiguration.MAX_SERIES) {
            this.nextSerieButton.setText(getText(R.string.tournament_serie_end));
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
            isFinal = (eventAction == MotionEvent.ACTION_UP);
            this.addTargetImpact(event.getX() / this.imageScale, event.getY() / this.imageScale, isFinal, false, this.tournamentSerie.arrows.size());
        }

        if (isFinal && this.tournamentSerie.arrows.size() == TournamentConfiguration.MAX_ARROW_PER_SERIES) {
            // enable the buttons to save the current serie
            if (this.tournamentSerie.index > 1) {
                this.previousSerieButton.setEnabled(true);
            }
            this.nextSerieButton.setEnabled(true);

            // update the series information after updating the arrows by it's score
            // so it can be showed on that order
            Collections.sort(this.tournamentSerie.arrows, new Comparator<TournamentSerieArrow>() {

                @Override
                public int compare(TournamentSerieArrow o1, TournamentSerieArrow o2) {
                    int res = 0;
                    if (o1.isX && !o2.isX) {
                        res = 1;
                    } else if (!o1.isX && o2.isX) {
                        res = -1;
                    } else if (o1.score > o2.score) {
                        res = 1;
                    } else if (o1.score < o2.score) {
                        res = -1;
                    }
                    res = -1 * res;
                    return res;
                }
            });

            this.serieDataDAO.addSerieData(this.tournamentSerie.tournament.distance, this.tournamentSerie.arrows.size());
            this.tournamentDAO.saveTournamentSerieInformation(this.tournamentSerie);
        }

        return false;
    }


    private void addTargetImpact(float x, float y, boolean isFinal, boolean showingExisting, int arrowIndex) {

        Bitmap mutableBitmap = this.imageBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Paint paint = this.finalImpactPaint;
        if (isFinal) {
            this.imageBitmap = mutableBitmap;
        } else {
            paint = this.currentImpactPaint;
        }

        Canvas canvas = new Canvas(mutableBitmap);
        if (!showingExisting) {
            y = y + Y_PADDING;
        }
        canvas.drawCircle(x, y, ARROW_IMPACT_RADIUS, paint);
        double distance = Math.sqrt(Math.pow(x - this.targetCenterX, 2) + Math.pow(y - this.targetCenterY, 2));
        int score = (int) (10 - Math.floor(distance / this.pointWidth));
        if (score < 0) {
            score = 0;
        }
        boolean isX = (score == 10 && (distance / this.pointWidth) < 0.5);
        this.targetImageView.setAdjustViewBounds(true);
        this.targetImageView.setImageBitmap(mutableBitmap);


        TextView scoreText = this.currentScoreText[arrowIndex];

        scoreText.getBackground().setColorFilter(new PorterDuffColorFilter(TournamentHelper.getBackground(score), PorterDuff.Mode.SRC_IN));
        scoreText.setText(TournamentHelper.getUserScore(score, isX));
        scoreText.setTextColor(TournamentHelper.getFontColor(score));

        if (isFinal && !showingExisting) {
            TournamentSerieArrow serieArrow = new TournamentSerieArrow();
            serieArrow.xPosition = x;
            serieArrow.yPosition = y;
            serieArrow.score = score;
            serieArrow.isX = isX;
            this.tournamentSerie.totalScore += score;
            this.tournamentSerie.arrows.add(serieArrow);
        }

        if (isFinal) {
            this.totalSerieScoreText.setText(String.format("%s / %s", this.tournamentSerie.totalScore, TournamentConfiguration.MAX_SCORE_PER_SERIES));
        }
    }


    @Override
    public void handleClick(View v) {
        final MainActivity activity = (MainActivity) this.getActivity();

        if (v.getId() == R.id.btn_serie_previous || v.getId() == R.id.btn_serie_next) {
            TournamentSerie transitionSerie = null;
            if (v.getId() == R.id.btn_serie_previous) {
                // -2 is required because the first index is 1.
                transitionSerie = this.tournamentSerie.tournament.series.get(this.tournamentSerie.index - 2);
            } else {

                // same here... there isn't any need to add +1 because the serie already starts at 1
                if (this.tournamentSerie.index == TournamentConfiguration.MAX_SERIES) {
                    // return to the tournament view because all the series for the tournament have been loaded
                    Bundle bundle = new Bundle();
                    bundle.putLong("tournamentId", this.tournamentSerie.tournament.id);

                    ViewTournamentSeriesFragment tournamentSeriesFragment = new ViewTournamentSeriesFragment();
                    tournamentSeriesFragment.setArguments(bundle);

                    FragmentManager fragmentManager = activity.getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, tournamentSeriesFragment)
                            .commit();
                    return;

                } else if (this.tournamentSerie.tournament.series.size() > this.tournamentSerie.index) {
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
        return false;
    }

    @Override
    public boolean canGoBack() {
        if (this.tournamentSerie.arrows.size() == TournamentConfiguration.MAX_ARROW_PER_SERIES) {
            // if the serie is created with 6 arrows, then I dont have any issue
            // if the user go backs
            return true;
        }

        if (this.canGoBack) {
            // if the user already confirmed that he can go back, then it should do it
            return true;
        }

        final ViewSerieInformationFragment self = this;

        new AlertDialog.Builder(this.getContext())
                .setTitle(R.string.common_confirmation_dialog_title)
                .setMessage(R.string.tournament_view_serie_creating_back)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        self.canGoBack = true;
                        self.tournamentDAO.deleteSerie(self.tournamentSerie.id);
                        self.getActivity().onBackPressed();
                    }})
                .setNegativeButton(android.R.string.no, null)
                .show();

        // ask the user if he is sure to go back or not
        return false;
    }


}
