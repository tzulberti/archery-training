package ar.com.tzulberti.archerytraining.seriefragments;

import android.support.v4.app.Fragment;


import ar.com.tzulberti.archerytraining.MainActivity;
import ar.com.tzulberti.archerytraining.dao.SerieDataDAO;
import ar.com.tzulberti.archerytraining.helper.BaseClickableFragment;


/**
 * Created by tzulberti on 4/21/17.
 */

public abstract class BaseFragment extends Fragment implements BaseClickableFragment {

    protected SerieDataDAO serieDataDAO;

    public void setObjects() {
        MainActivity activity = (MainActivity) getActivity();
        this.serieDataDAO = activity.getSerieDAO();
    }

}
