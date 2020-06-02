package it.unimib.disco.gruppoade.gamenow.utils;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;


public class FullscreenHelper {

    /**
     * Class responsible for changing the view from full screen to non-full screen and vice versa.
     *
     * @author Pierfrancesco Soffritti
     */

    private static final String TAG = "FullscreenHelper";

        private Activity context;
        private View[] views;

        /**
         * @param context
         * @param views to hide/show
         */
        public FullscreenHelper(Activity context, View... views) {
            this.context = context;
            this.views = views;
        }

        /**
         * call this method to enter full screen
         */
        public void enterFullScreen() {
            View decorView = context.getWindow().getDecorView();
            Log.d(TAG, "enterFullScreen: Views " + views.length);



            hideSystemUi(decorView);

            for(View view : views) {
                Log.d(TAG, "enterFullScreen: View " + view);
                view.setVisibility(View.GONE);
                view.invalidate();
            }
        }

        /**
         * call this method to exit full screen
         */
        public void exitFullScreen() {
            View decorView = context.getWindow().getDecorView();

            showSystemUi(decorView);

            for(View view : views) {
                view.setVisibility(View.VISIBLE);
                view.invalidate();
            }
        }

        private void hideSystemUi(View mDecorView) {
            mDecorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

        private void showSystemUi(View mDecorView) {
            mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }

}
