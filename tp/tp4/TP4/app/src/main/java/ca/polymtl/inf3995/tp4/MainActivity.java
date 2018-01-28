package ca.polymtl.inf3995.tp4;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class MainActivity extends FragmentActivity implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
    private static final String TAG = "MainActivity";

    private static final int MIN_SWIPE = 150;


    private GestureDetectorCompat mDetector;

    FragmentManager fragmentManager = getSupportFragmentManager();

    RequestFragment frag1 = new RequestFragment();
    RequestFragment frag2 = new RequestFragment();
    RequestFragment frag3 = new RequestFragment();

    RequestFragment currentFrag;

    int state = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Log.d(TAG, "start");

        // Check whether the activity is using the layout version with
        // the fragment_container FrameLayout. If so, we must add the first fragment
        if (findViewById(R.id.activity_main) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }


            // Create an instance of ExampleFragment
            //HeadlinesFragment firstFragment = new HeadlinesFragment();


            // In case this activity was started with special instructions from an Intent,
            // pass the Intent's extras to the fragment as arguments
            frag1.setArguments(getIntent().getExtras());


            Bundle args = new Bundle();
            args.putString(RequestFragment.ARG_URL, "test1");
            frag1.setArguments(args);

            Bundle args2 = new Bundle();
            args2.putString(RequestFragment.ARG_URL, "test2");
            frag2.setArguments(args2);

            Bundle args3 = new Bundle();
            args3.putString(RequestFragment.ARG_URL, "test3");
            frag3.setArguments(args3);

            currentFrag = frag1;


            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.add(R.id.activity_main, frag1);
            fragmentTransaction.commit();


            mDetector = new GestureDetectorCompat(this, this);
            // Set the gesture detector as the double tap
            // listener.
            mDetector.setOnDoubleTapListener(this);


        }


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mDetector.onTouchEvent(event);
        // Be sure to call the superclass implementation
        return super.onTouchEvent(event);
    }


    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {


        if (e1.getY() > e2.getY() && e1.getY() - e2.getY() > MIN_SWIPE) {
            //Toast.makeText(getApplicationContext(), "swipe", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "swipe");

            state = (state + 1) % 3;

            if (state == 0) {
                currentFrag = frag1;
            } else if (state == 1) {
                currentFrag = frag2;
            } else if (state == 2) {
                currentFrag = frag3;
            }

            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.replace(R.id.activity_main, currentFrag);
            transaction.addToBackStack(null);
            transaction.commit();
        }

        return true;
    }


    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {


        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }


    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return false;
    }
}
