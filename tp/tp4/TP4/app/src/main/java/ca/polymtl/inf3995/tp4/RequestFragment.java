package ca.polymtl.inf3995.tp4;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class RequestFragment extends Fragment {
    private static final String TAG = "RequestFragment";

    static final String ARG_URL = "url";
    private String url;

    public RequestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param url Parameter 1.
     * @return A new instance of fragment RequestFragment.
     */
    public static RequestFragment newInstance(String url) {
        RequestFragment fragment = new RequestFragment();
        Bundle args = new Bundle();
        args.putString(ARG_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            url = getArguments().getString(ARG_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_request, container, false);
    }


    @Override
    public void onStart() {
        super.onStart();

        // During startup, check if there are arguments passed to the fragment.
        // onStart is a good place to do this because the layout has already been
        // applied to the fragment at this point so we can safely call the method
        // below that sets the article text.
        Bundle args = getArguments();
        if (args != null) {
            // Set article based on argument passed in
            updateButtonView(args.getString(ARG_URL));
        }
        // } else if (mCurrentPosition != -1) {
        // Set article based on saved instance state defined during onCreateView
        //   updateButtonView(mCurrentPosition);
        // }

    }


    public void updateButtonView(String url) {
        Button button2 = (Button) getActivity().findViewById(R.id.button2);
        button2.setText(url);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                Log.d(TAG, "button");

                Intent myIntent = new Intent(getActivity(), ResultActivity.class);
                myIntent.putExtra("key", "test"); //Optional parameters
                getActivity().startActivity(myIntent);

            }
        });

        //mCurrentPosition = position;

    }


}
