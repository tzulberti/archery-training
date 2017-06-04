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
import ar.com.tzulberti.archerytraining.dao.SerieDataDAO;
import ar.com.tzulberti.archerytraining.dao.TournamentDAO;
import ar.com.tzulberti.archerytraining.fragments.BaseClickableFragment;
import ar.com.tzulberti.archerytraining.fragments.common.AbstractSerieArrowsFragment;
import ar.com.tzulberti.archerytraining.helper.TournamentHelper;
import ar.com.tzulberti.archerytraining.model.base.ISerie;
import ar.com.tzulberti.archerytraining.model.tournament.Tournament;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentConfiguration;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerie;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerieArrow;

/**
 * Created by tzulberti on 4/25/17.
 */

public class ViewSerieInformationFragment extends AbstractSerieArrowsFragment {


    private TournamentDAO tournamentDAO;
    private SerieDataDAO serieDataDAO;

    @Override
    protected int getLayoutResource() {
        return R.layout.tournament_view_serie_arrow;
    }

    @Override
    protected void setAdditionalInformation(View view) {
        TournamentSerie tournamentSerie = (TournamentSerie) this.serie;
        ((TextView) view.findViewById(R.id.total_tournament_score)).setText(String.format("%s / %s", tournamentSerie.tournament.totalScore, TournamentConfiguration.MAX_SCORE_FOR_TOURNAMENT));
    }

    @Override
    protected void saveSerie() {
        TournamentSerie tournamentSerie = (TournamentSerie) this.serie;
        this.serieDataDAO.addSerieData(tournamentSerie.tournament.distance, tournamentSerie.arrows.size());
        this.tournamentDAO.saveTournamentSerieInformation(tournamentSerie);
    }

    @Override
    protected void deleteSerie() {
        TournamentSerie tournamentSerie = (TournamentSerie) this.serie;
        this.tournamentDAO.deleteSerie(tournamentSerie.id);
    }

    @Override
    protected void addArrowData(float x, float y, int score, boolean isX) {
        TournamentSerieArrow arrow = new TournamentSerieArrow();
        arrow.xPosition = x;
        arrow.yPosition = y;
        arrow.score = score;
        arrow.isX = isX;
        TournamentSerie tournamentSerie = (TournamentSerie) this.serie;
        tournamentSerie.arrows.add(arrow);
    }

    @Override
    protected void setDAOs() {
        MainActivity activity = (MainActivity) getActivity();
        this.tournamentDAO = activity.getTournamentDAO();
        this.serieDataDAO = activity.getSerieDAO();
    }

    @Override
    protected BaseClickableFragment getContainerDetailsFragment() {
        return new ViewTournamentSeriesFragment();
    }

    @Override
    protected AbstractSerieArrowsFragment getSerieDetailsFragment() {
        return new ViewSerieInformationFragment();
    }

    @Override
    protected ISerie createNewSerie() {
        return this.tournamentDAO.createNewSerie((Tournament) this.serie.getContainer());
    }

    @Override
    protected boolean canActivateButtons() {
        return this.serie.getArrows().size() == TournamentConfiguration.MAX_ARROW_PER_SERIES;
    }

    @Override
    protected boolean hasFinished() {
        return this.serie.getIndex() == TournamentConfiguration.MAX_SERIES;
    }
}
