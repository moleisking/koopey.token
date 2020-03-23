package com.koopey.view;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;

import com.koopey.R;
import com.koopey.model.Image;

/**
 * Created by Scott on 29/03/2017.
 */
public class ImageReadFragment extends Fragment /*implements View.OnTouchListener*/  {

    private final String LOG_HEADER = "IMAGE:FRAGMENT";
    private Image image;
    private ImageView img;
    private static final float MIN_ZOOM = 1f,MAX_ZOOM = 1f;
    private ScaleGestureDetector scaleGestureDetector;

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        this.img = (ImageView)getActivity().findViewById(R.id.img);
        this.scaleGestureDetector = new ScaleGestureDetector (this.getActivity() , new MyScaleListener());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.label_image));
        ((MainActivity) getActivity()).hideKeyboard();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)    {
        super.onCreate(savedInstanceState);
        if(getActivity().getIntent().hasExtra("image") && ((Image) getActivity().getIntent().getSerializableExtra("image") != null)) {
            this.image = (Image) getActivity().getIntent().getSerializableExtra("image");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)    {
        return inflater.inflate(R.layout.fragment_image_read, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(this.image != null) {
            try {
                img.setImageBitmap(this.image.getBitmap());
            } catch (Exception ex) {
                Log.d(LOG_HEADER + ":IMG:ER", ex.getMessage());
            }
        }
    }

   // @Override
  //  public boolean onTouch(View v, MotionEvent event)
  //  {
   //     scaleGestureDetector.onTouchEvent(event);

       /* ImageView view = (ImageView) v;
        view.setScaleType(ImageView.ScaleType.MATRIX);
        float scale;

        switch (event.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:   // first finger down only
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                Log.d(TAG, "mode=DRAG"); // write to LogCat
                mode = DRAG;
                break;

            case MotionEvent.ACTION_UP: // first finger lifted

            case MotionEvent.ACTION_POINTER_UP: // second finger lifted

                mode = NONE;
                Log.d(TAG, "mode=NONE");
                break;

            case MotionEvent.ACTION_POINTER_DOWN: // first and second finger down

                oldDist = spacing(event);
                Log.d(TAG, "oldDist=" + oldDist);
                if (oldDist > 5f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                    Log.d(TAG, "mode=ZOOM");
                }
                break;

            case MotionEvent.ACTION_MOVE:

                if (mode == DRAG)
                {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y); // create the transformation in the matrix  of points
                }
                else if (mode == ZOOM)
                {
                    // pinch zooming
                    float newDist = spacing(event);
                    Log.d(TAG, "newDist=" + newDist);
                    if (newDist > 5f)
                    {
                        matrix.set(savedMatrix);
                        scale = newDist / oldDist; // setting the scaling of the
                        // matrix...if scale > 1 means
                        // zoom in...if scale < 1 means
                        // zoom out
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }

        view.setImageMatrix(matrix); // display the transformation on screen */

   //     return true; // indicate event was handled
   // }

    private class MyScaleListener extends
            ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            if (scaleFactor > 1) {
                Log.d(LOG_HEADER + ":MSL", "Zooming Out");
            } else {
                Log.d(LOG_HEADER + ":MSL", "Zooming In");
            }
            return true;
        }
    }
}
