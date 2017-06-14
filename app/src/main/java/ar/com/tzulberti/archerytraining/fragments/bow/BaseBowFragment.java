package ar.com.tzulberti.archerytraining.fragments.bow;

import ar.com.tzulberti.archerytraining.MainActivity;
import ar.com.tzulberti.archerytraining.dao.BowDAO;
import ar.com.tzulberti.archerytraining.fragments.BaseClickableFragment;

/**
 * Created by tzulberti on 6/12/17.
 */

public abstract class BaseBowFragment extends BaseClickableFragment {

    protected BowDAO bowDAO;

    public void setObjects() {
        MainActivity activity = (MainActivity) getActivity();
        this.bowDAO = activity.getBowDAO();
    }
}
