package ar.com.tzulberti.archerytraining.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by tzulberti on 4/21/17.
 */

public abstract class BaseClickableFragment extends Fragment implements View.OnClickListener {

    public abstract void handleClick(View v);

    protected void cleanState(ViewGroup container) {
        container.removeAllViews();
    }


    @Override
    public void onClick(View v) {
        this.handleClick(v);
    }
}
