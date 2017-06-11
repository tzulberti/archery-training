package ar.com.tzulberti.archerytraining.fragments.tournament;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;


import com.edmodo.rangebar.RangeBar;

import org.w3c.dom.Text;

import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.model.tournament.Tournament;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerie;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerieArrow;

/**
 * Created by tzulberti on 5/25/17.
 */

public class ViewAllTournamentTargetArrowFragment extends BaseTournamentFragment {

    private Tournament tournament;

    private ImageView targetImageView;
    private RangeBar rangeBar;
    private TextView seriesShowingText;

    private Bitmap imageBitmap;

    private Paint finalImpactPaint;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.cleanState(container);
        View view = inflater.inflate(R.layout.tournament_view_tournament_series_on_target, container, false);
        this.targetImageView = (ImageView) view.findViewById(R.id.photo_view);
        this.rangeBar = (RangeBar) view.findViewById(R.id.tournament_series_rangebar);
        this.seriesShowingText = (TextView) view.findViewById(R.id.tournament_series_showing);

        this.setObjects();
        this.tournament = this.getTournamentArgument();

        this.finalImpactPaint = new Paint();
        this.finalImpactPaint.setAntiAlias(true);
        this.finalImpactPaint.setColor(Color.LTGRAY);

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
        BitmapFactory.Options myOptions = new BitmapFactory.Options();
        myOptions.inScaled = false;
        myOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// important

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.complete_archery_target, myOptions);

        this.rangeBar.setTickCount(this.tournament.series.size());
        this.imageBitmap = Bitmap.createBitmap(bitmap);

        //this.showSeries(1, this.tournament.series.size());
        this.rangeBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onIndexChangeListener(RangeBar rangeBar, int start, int end) {
                showSeries(start +1 , end +1);
            }
        });
        this.showSeries(1, this.tournament.series.size());
    }

    private void showSeries(int minSerie, int maxSerie) {
        Bitmap mutableBitmap = this.imageBitmap.copy(Bitmap.Config.ARGB_8888, true);

        for (TournamentSerie serie : this.tournament.series) {
            if (serie.index < minSerie) {
                continue;
            }

            if (serie.index > maxSerie) {
                continue;
            }

            for (TournamentSerieArrow serieArrow : serie.arrows) {
                this.addTargetImpact(serieArrow.xPosition, serieArrow.yPosition, mutableBitmap);
            }
        }

        this.seriesShowingText.setText(getString(R.string.tournament_serie_showing_series, minSerie, maxSerie));
    }

    private void addTargetImpact(float x, float y, Bitmap mutableBitmap) {
        Canvas canvas = new Canvas(mutableBitmap);
        canvas.drawCircle(x, y, ViewSerieInformationFragment.ARROW_IMPACT_RADIUS, this.finalImpactPaint);

        this.targetImageView.setAdjustViewBounds(true);
        this.targetImageView.setImageBitmap(mutableBitmap);
    }

    @Override
    public void handleClick(View v) {
    }
}

