package wind.newwindalarm;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.mikephil.charting.charts.LineChart;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;

import wind.newwindalarm.chart.HistoryChart;

public class SpotDetailFragment extends Fragment {

    private LineChart mWindChart;
    private LineChart mTrendChart;
    private LineChart mTemperatureChart;
    private long spotID;
    private ImageView mWebcamImageView;
    private MeteoStationData meteoData;

    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 5;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void setMeteoData(MeteoStationData data) {
        meteoData = data;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        MenuItem mm = menu.findItem(R.id.options_refresh);
        if (mm != null) {

            menu.findItem(R.id.options_refresh).setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.options_refresh:
                // Do Fragment menu item stuff here
                refreshData();
                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View v;
        v = inflater.inflate(R.layout.fragment_spotdetail, container, false);
        mWebcamImageView = (ImageView) v.findViewById(R.id.imageView7);


        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.tablayout);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) v.findViewById(R.id.pager);
        //FragmentManager fm = getActivity().ge();

        MainActivity ma = (MainActivity) getActivity();

        mPagerAdapter = new ScreenSlidePagerAdapter(ma.getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        tabLayout.setupWithViewPager(mPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.webcamicon);
        tabLayout.getTabAt(1).setIcon(R.drawable.graphicon);




        // Updating the action bar title
        String txt = MainActivity.getSpotName(spotID);// + */""+spotID;
        if (txt != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(txt);
            mWindChart = (LineChart) v.findViewById(R.id.chart);
            //refreshData();
        }

        spotID = getArguments().getLong("spotID");
        String str = getArguments().getString("meteodata");

        if (str != null) {
            try {
                JSONObject json = new JSONObject(str);
                meteoData = new MeteoStationData(json);
                //refreshData();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            //getLastData(spotID);
        }
        return v;
    }

    private void refreshData() {

        new DownloadImageTask(mWebcamImageView).execute(meteoData.webcamurl);
        getHistoryData(spotID);
    }

    public void onBackPressed() {
        // super.onBackPressed();
        // myFragment.onBackPressed();
    }

    public void getHistoryData(final long spot) {

        HistoryChart hc = new HistoryChart(getActivity(), mWindChart, mTrendChart, mTemperatureChart);

        new requestMeteoDataTask(getActivity(), hc, requestMeteoDataTask.REQUEST_HISTORYMETEODATA).execute("" + spot);

    }


    public void getLastData(long spot) {


        new requestMeteoDataTask(getActivity(), new AsyncRequestMeteoDataResponse() {

            @Override
            public void processFinish(List<Object> list, boolean error, String errorMessage) {

                if (error) {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

                    alertDialogBuilder.setTitle("Errore");
                    alertDialogBuilder
                            .setMessage(errorMessage)
                            .setCancelable(false);
                    alertDialogBuilder
                            .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, just close
                                    // the dialog box and do nothing
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog;
                    alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                } else {

                    meteoData = null;
                    for (int i = 0; i < list.size(); i++) {

                        meteoData = (MeteoStationData) list.get(i);
                        if (meteoData.spotID == spotID)
                            break;
                    }
                    if (meteoData == null)
                        return;


                    //DownloadImageTask downloadImageTask = (DownloadImageTask) new DownloadImageTask(mWebcamImageView)
                    //        .execute(md.webcamurl);
                    refreshData();


                }

            }

            @Override
            public void processFinishHistory(List<Object> list, boolean error, String errorMessage) {

            }

            @Override
            public void processFinishSpotList(List<Object> list, boolean error, String errorMessage) {

            }
        }, requestMeteoDataTask.REQUEST_LASTMETEODATA).execute("" + spot);
    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {

            if (result == null)
                return;
            int bmWidth = result.getWidth();
            int bmHeight = result.getHeight();

            View parent = (View) mWebcamImageView.getParent();
            int ivWidth = parent.getWidth();
            //int ivWidth = bmImage.getWidth();
            int new_width = ivWidth;

            if (ivWidth > 0) {
                int new_height = (int) Math.floor((double) bmHeight * ((double) new_width / (double) bmWidth));
                Bitmap newbitMap = Bitmap.createScaledBitmap(result, new_width, new_height, true);
                bmImage.setImageBitmap(newbitMap);
            }
        }

    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        private static final int NUM_PAGES = 2;

        public ScreenSlidePagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {

            if (position == 0 ) {

                WebcamFragment webcamFragment = new WebcamFragment();
                Bundle data = new Bundle();
                data.putLong("spotID", spotID);
                if (meteoData != null)
                    data.putString("meteodata", meteoData.toJson());
                webcamFragment.setArguments(data);
                return  webcamFragment;

            }  else if (position == 1 ) {

                ChartFragment graphFragment = new ChartFragment();
                Bundle data = new Bundle();
                data.putLong("spotID", spotID);
                if (meteoData != null)
                    data.putString("meteodata", meteoData.toJson());
                graphFragment.setArguments(data);
                return  graphFragment;

            }  else{
                return new ScreenSlidePageFragment();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String titolo = "titolo " + position;
            if (position == 0 ) {

                return  "Webcam";

            }  else if (position == 1 ) {

                return  "Grafico";

            }
            return titolo;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
