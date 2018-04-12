package ca.polymtl.inf3995.oronos.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.polymtl.inf3995.oronos.activities.OronosActivity;

public class TelemetryFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((OronosActivity) getActivity()).showToolbar();
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
