package ar.com.tzulberti.archerytraining.activities.common;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;

import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.helper.TournamentHelper;
import ar.com.tzulberti.archerytraining.model.base.AbstractArrow;
import ar.com.tzulberti.archerytraining.model.base.ISerie;
import ar.com.tzulberti.archerytraining.model.constrains.RoundConstraint;

/**
 * Show the target so the user can input were the arrows go and set/view the series
 * score
 *
 * Created by tzulberti on 6/3/17.
 */
public abstract class AbstractSerieArrowsActivity extends BaseArcheryTrainingActivity implements View.OnTouchListener, View.OnLongClickListener {

    public static final String SERIE_ARGUMENT_KEY = "serie";
    public static final String CONTAINER_ARGUMENT_KEY = "container";


    public static final float IMAGE_WIDTH = 512;
    public static final float ARROW_IMPACT_RADIUS = 5;

    private static final int Y_PADDING = -80;


    private ImageView targetImageView;
    private TextView[] currentScoreText;
    private TextView totalSerieScoreText;

    private Button previousSerieButton;
    private Button nextSerieButton;
    private Button arrowUndoButton;

    private float targetCenterX = -1;
    private float targetCenterY = -1;
    private float imageScale = -1;
    private float pointWidth = -1;
    private Bitmap imageBitmap;
    private Paint currentImpactPaint;
    private Paint finalImpactPaint;

    private boolean canGoBack;
    protected ISerie serie;


    /**
     * @return the id of the layout that is going to be shown
     */
    protected abstract int getLayoutResource();

    /**
     * Shows additional information on the view (for example, the tournament score, or the playoff
     * score)
     */
    protected abstract void setAdditionalInformation();

    /**
     * Saves the serie information using the different datos
     */
    protected abstract void saveSerie();

    /**
     * Deletes the current serie that the user is saving
     * <p>
     * This is used when the user goes back before finishing the current serie
     */
    protected abstract void deleteSerie();

    /**
     * Add an arrow to the current serie
     * <p>
     * This should also update the serie total score, and all those kind of values
     *
     * @param x the x coordinate of the impact
     * @param y the y coordinate of the impact
     * @param score the score given the coordinates
     * @param isX if it is an X
     */
    protected abstract void addArrowData(float x, float y, int score, boolean isX);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(this.getLayoutResource());
        this.serie = (ISerie)this.getIntent().getSerializableExtra(SERIE_ARGUMENT_KEY);

        this.createDAOs();
        this.targetImageView = (ImageView) this.findViewById(R.id.photo_view);
        this.targetImageView.setOnTouchListener(this);
        this.targetImageView.setOnLongClickListener(this);

        this.currentImpactPaint = new Paint();
        this.currentImpactPaint.setAntiAlias(true);
        this.currentImpactPaint.setColor(Color.MAGENTA);

        this.finalImpactPaint = new Paint();
        this.finalImpactPaint.setAntiAlias(true);
        this.finalImpactPaint.setColor(Color.LTGRAY);

        this.currentScoreText = new TextView[6];
        this.currentScoreText[0] = (TextView) this.findViewById(R.id.current_score1);
        this.currentScoreText[1] = (TextView) this.findViewById(R.id.current_score2);
        this.currentScoreText[2] = (TextView) this.findViewById(R.id.current_score3);
        this.currentScoreText[3] = (TextView) this.findViewById(R.id.current_score4);
        this.currentScoreText[4] = (TextView) this.findViewById(R.id.current_score5);
        this.currentScoreText[5] = (TextView) this.findViewById(R.id.current_score6);

        this.nextSerieButton = (Button) this.findViewById(R.id.btn_serie_next);
        this.previousSerieButton = (Button) this.findViewById(R.id.btn_serie_previous);
        this.arrowUndoButton = (Button) this.findViewById(R.id.btn_arrow_undo);
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

        ((TextView) this.findViewById(R.id.serie_index)).setText(getString(R.string.tournament_serie_current_serie, this.serie.getIndex()));
        this.setAdditionalInformation();

        this.totalSerieScoreText = (TextView) this.findViewById(R.id.total_serie_score);
    }

    protected void initializeValues() {
        this.imageScale = Math.min(this.targetImageView.getWidth(), this.targetImageView.getHeight()) / IMAGE_WIDTH;
        this.targetCenterX = this.targetImageView.getWidth() / (2 * this.imageScale);
        this.targetCenterY = this.targetImageView.getHeight() / (2 * this.imageScale);
        this.pointWidth = Math.min(this.targetCenterX, this.targetCenterY) / 10;


        this.showSeriesArrow();

        // if viewing an already existing serie, then disable the undo button since
        // the serie was already saved, or it is disabled because the serie has no
        // arrow information
        this.arrowUndoButton.setEnabled(false);

        if (this.canActivateButtons()) {
            this.nextSerieButton.setEnabled(true);
            this.previousSerieButton.setEnabled(this.serie.getIndex() > 1);
        }

        if (this.hasFinished()) {
            this.nextSerieButton.setText(getText(R.string.tournament_serie_end));
        }
    }

    /**
     * Take care of creating the correct bitmap and showing those on the target
     */
    protected void showSeriesArrow() {
        BitmapFactory.Options myOptions = new BitmapFactory.Options();
        myOptions.inScaled = false;
        myOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// important

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.complete_archery_target, myOptions);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);

        this.imageBitmap = Bitmap.createBitmap(bitmap);

        if (this.serie.getArrows().size() == 0) {
            // if there is no serie, then show the empty target. This is used
            // when undoing the first added arrow
            this.targetImageView.setAdjustViewBounds(true);
            this.targetImageView.setImageBitmap(this.imageBitmap);
            
        } else {
            int arrowIndex = 0;
            for (AbstractArrow serieArrow : this.serie.getArrows()) {
                this.addTargetImpact(serieArrow.xPosition, serieArrow.yPosition, true, true, arrowIndex);
                arrowIndex += 1;
            }
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int numberOfArrows = this.serie.getArrows().size();
        if (! this.canAddArrowImpact()) {
            // already got the max number of arrows so there is nothing specific to do
            return false;
        }

        int eventAction = event.getAction();
        boolean isFinal = false;
        if (eventAction == MotionEvent.ACTION_DOWN || eventAction == MotionEvent.ACTION_MOVE || eventAction == MotionEvent.ACTION_UP) {
            isFinal = (eventAction == MotionEvent.ACTION_UP);
            this.addTargetImpact(event.getX() / this.imageScale, event.getY() / this.imageScale, isFinal, false, numberOfArrows);
        }

        if (isFinal && this.canActivateButtons()) {
            this.activateButtons();
        }

        return false;
    }

    protected void activateButtons() {
        // enable the buttons to save the current serie
        if (this.serie.getIndex() > 1) {
            this.previousSerieButton.setEnabled(true);
        }
        this.nextSerieButton.setEnabled(true);

        // update the series information after updating the arrows by it's score
        // so it can be showed on that order
        Collections.sort(this.serie.getArrows(), new Comparator<AbstractArrow>() {

            @Override
            public int compare(AbstractArrow o1, AbstractArrow o2) {
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

        this.saveSerie();
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
            this.addArrowData(x, y, score, isX);
            this.serie.updateTotalScore(score);
        }

        if (isFinal) {
            this.totalSerieScoreText.setText(String.format("%s / %s", this.serie.getTotalScore(), this.getMaxSerieScore()));
            this.arrowUndoButton.setEnabled(true);
        }
    }

    /**
     * @return the max possible score for the current serie
     */
    protected int getMaxSerieScore() {
        RoundConstraint roundConstraint = this.serie.getContainer().getTournamentConstraint().getConstraintForSerie(this.serie.getIndex());
        return roundConstraint.getSerieMaxPossibleScore();
    }


    public void goToPreviousSerie(View v) {
        // -2 is required because the first index is 1.
        ISerie transitionSerie = this.serie.getContainer().getSeries().get(this.serie.getIndex() - 2);

        Intent intent = new Intent(this, this.getClass());
        intent.putExtra(AbstractSerieArrowsActivity.SERIE_ARGUMENT_KEY, transitionSerie);
        startActivity(intent);
    }


    public void undoLastArrow(View v) {
        TextView scoreText = this.currentScoreText[this.serie.getArrows().size() - 1];
        scoreText.setText("");
        scoreText.setBackground(this.getResources().getDrawable(R.drawable.rounded));
        scoreText.setBackgroundResource(R.drawable.rounded);

        // remove the last added arrow
        AbstractArrow arrowToRemove = this.serie.getArrows().get(this.serie.getArrows().size() - 1);
        this.serie.getArrows().remove(this.serie.getArrows().size() - 1);
        this.serie.updateTotalScore(0 - arrowToRemove.score);

        // disable the buttons if they were enabled
        this.previousSerieButton.setEnabled(false);
        this.nextSerieButton.setEnabled(false);

        // update the total score of the serie
        this.totalSerieScoreText.setText(String.format("%s / %s", this.serie.getTotalScore(), this.getMaxSerieScore()));

        // removed only arrow for the current serie so he can not undo anymore
        if (this.serie.getArrows().size() == 0) {
            this.arrowUndoButton.setEnabled(false);
        }

        // redraw all the arrows on the target to remove the last added arrow
        this.showSeriesArrow();
    }

    public void goToNextOrEnd(View v) {
        ISerie transitionSerie;

        if (this.hasFinished()) {
            Intent intent = new Intent(this, this.getContainerDetailsFragment());
            intent.putExtra(CONTAINER_ARGUMENT_KEY, this.serie.getContainer());
            startActivity(intent);
            return;

        } else if (this.serie.getContainer().getSeries().size() > this.serie.getIndex()) {
            // same here... there isn't any need to add +1 because the serie already starts at 1
            transitionSerie = this.serie.getContainer().getSeries().get(this.serie.getIndex());

        } else {
            // creating a new serie for the tournament
            transitionSerie = this.createNewSerie();
        }

        Intent intent = new Intent(this, this.getClass());
        intent.putExtra(AbstractSerieArrowsActivity.SERIE_ARGUMENT_KEY, transitionSerie);
        startActivity(intent);
    }

    /**
     * Used to shown the container details when the user alread finished loading
     * all the possible series
     */
    protected abstract Class<? extends AppCompatActivity> getContainerDetailsFragment();


    /**
     * Creates the new serie on the database
     *
     * @return the created serie
     */
    protected abstract ISerie createNewSerie();

    /**
     * @return if it can activate the next, and previous buttons
     */
    protected abstract boolean canActivateButtons();

    /**
     * @return if the user can continue adding arrows to the target
     */
    protected boolean canAddArrowImpact() {
        return ! this.canActivateButtons();
    }

    /**
     * @return if no more series can be loaded
     */
    protected abstract boolean hasFinished();


    @Override
    public boolean onLongClick(View v) {
        return false;
    }


    @Override
    public void onBackPressed() {
        if (this.canGoBack()) {
            super.onBackPressed();
        }
    }

    public boolean canGoBack() {
        if (this.canActivateButtons()) {
            // if the serie is created with 6 arrows, then it doesnt has any issue
            // if the user go back
            return true;
        }

        if (this.canGoBack) {
            // if the user already confirmed that he can go back, then it should do it
            return true;
        }

        final AbstractSerieArrowsActivity self = this;

        new AlertDialog.Builder(this)
                .setTitle(R.string.common_confirmation_dialog_title)
                .setMessage(R.string.tournament_view_serie_creating_back)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        self.canGoBack = true;
                        self.deleteSerie();
                        self.onBackPressed();
                    }})
                .setNegativeButton(android.R.string.no, null)
                .show();

        // ask the user if he is sure to go back or not
        return false;
    }

}
