package gwind.windalarm.cardui;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;

import gwind.windalarm.data.MeteoStationData;
import gwind.windalarm.R;


public class InfoCardItem {

    private InfoCard card;
    InfoCardListener listener;

    public interface InfoCardListener {
        public void cardSelected();
    }

    public InfoCardItem(InfoCardListener ml, final Activity activity, LinearLayout container) {

        listener = ml;
        card = (InfoCard) activity.getLayoutInflater().inflate(R.layout.card_info, container, false);

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listener.cardSelected();
            }
        });

        card.init();
    }

    public void setTitle(String title) {
        card.setTitle(title);
    }
    public void setValue(String title) {
        card.setValue(title);
    }

    public void update(MeteoStationData data) {

   }
}