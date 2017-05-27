package ar.com.tzulberti.archerytraining.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ar.com.tzulberti.archerytraining.R;

/**
 * Fragment shown when the user opens the application
 *
 * Created by tzulberti on 5/24/17.
 */
public class MainFragment extends BaseClickableFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.cleanState(container);
        View view = inflater.inflate(R.layout.content_main, container, false);

        return view;
    }

    @Override
    public void handleClick(View v) {

    }
}
