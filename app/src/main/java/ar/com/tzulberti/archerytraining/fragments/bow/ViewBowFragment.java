package ar.com.tzulberti.archerytraining.fragments.bow;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import ar.com.tzulberti.archerytraining.MainActivity;
import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.fragments.common.AbstractTableDataFragment;


/**
 * Created by tzulberti on 6/12/17.
 */

public class ViewBowFragment extends BaseBowFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.cleanState(container);
        this.setObjects();
        View view = inflater.inflate(R.layout.bow_view_details, container, false);
        return view;
    }

    @Override
    public void handleClick(View v) {
    }
}
