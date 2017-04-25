package ar.com.tzulberti.archerytraining.fragments;

import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by tzulberti on 4/21/17.
 */

public abstract class BaseClickableFragment extends Fragment implements View.OnClickListener {

    public abstract void handleClick(View v);

    @Override
    public void onClick(View v) {
        this.handleClick(v);
    }
}
