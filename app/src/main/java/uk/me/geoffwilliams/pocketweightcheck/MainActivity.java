/*
 * pocketweightcheck -- simple android app to track of your weight
 * Copyright (C) 2014 Geoff Williams <geoff@geoffwilliams.me.uk>
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package uk.me.geoffwilliams.pocketweightcheck;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.androidannotations.annotations.sharedpreferences.SharedPref;
import uk.me.geoffwilliams.pocketweightcheck.dao.DaoHelper;
import uk.me.geoffwilliams.pocketweightcheck.dao.DaoHelperImpl;
import uk.me.geoffwilliams.pocketweightcheck.dao.RecordWeight;


@EActivity(R.layout.activity_main)
public class MainActivity extends FragmentActivity implements DataChangeListener {

    private final static String TAG = "pocketweightcheck";
    
    @Bean(DaoHelperImpl.class)
    DaoHelper daoHelper;

    @ViewById(R.id.graphLayout)
    LinearLayout graphLayout;
    
    @ViewById(R.id.statsLayout)
    LinearLayout statsLayout;
    
    @ViewById(R.id.noDataLayout)
    LinearLayout noDataLayout;
    
    private WeightEntryDialog weightEntryDialog;
    
    @Bean
    GraphController graphController;
    
    @ViewById(R.id.minWeightMessage)
    TextView minWeightMessage;
    
    @ViewById(R.id.maxWeightMessage)
    TextView maxWeightMessage;
    
    @StringRes(R.string.on)
    String on;
    
    @StringRes(R.string.msg_min_weight)
    String msgMinWeight;
    
    @StringRes(R.string.msg_max_weight)
    String msgMaxWeight;
    
    // must be wired after loading or causes error in emulator
    private java.text.DateFormat df = null;
    
    @Pref
    uk.me.geoffwilliams.pocketweightcheck.Prefs_ preferences;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "starting!");
        df = DateFormat.getDateFormat(this);
        weightEntryDialog = new WeightEntryDialog_();
        
        Log.d(TAG, "dateformatter was set");

        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         Intent intent;
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.enterWeightItem:
                showWeightEntryDialog();
                return true;
            case R.id.importExportItem:
                // not supported yet
                return true;
            case R.id.viewItem:
                intent = new Intent(this, ViewDataActivity_.class);
                startActivity(intent);
                return true;
            case R.id.settingsItem:

                  // Display the fragment as the main content.
                 getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new PreferencesFragment())
                        .commit();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    private void toggleStats(boolean show) {
        statsLayout.setVisibility( (show) ? View.VISIBLE: View.GONE);
        noDataLayout.setVisibility((show) ? View.GONE: View.VISIBLE);        
    }
    
    public void showWeightEntryDialog() {   
        Log.d(TAG, "show weight entry dialog...");
        weightEntryDialog.show(getSupportFragmentManager(), TAG);
        Log.d(TAG, "..control returned to main thread");
        onDataChanged();
    }
    
    /**
     * Accessor for unit testing
     * @return 
     */
    /* package */ WeightEntryDialog getWeightEntryDialog() {
        return weightEntryDialog;
    }
    
    @AfterViews
    /* package */ void afterViews() {
        daoHelper.registerListener(this);
        onDataChanged();
        if (Settings.isPromptForDataEntry()) {
            showWeightEntryDialog();
        }
    }
    
    private String getExtremaString(String intro, RecordWeight recordWeight) {
        assert intro != null : "intro cannot be null";
        assert recordWeight != null : "recordWeight cannot be null";
        assert on != null : "on cannot be null";
        assert df != null : "df cannot be null";
        Log.d(TAG, recordWeight.toString());
        return String.format(
                "%s  %.2f %s %s", 
                intro, 
                recordWeight.getValue(), 
                on, 
                df.format(recordWeight.getSampleTime()));
    }
    
    private void refreshExtrema() {
        RecordWeight minWeight = daoHelper.getMinWeight();
        RecordWeight maxWeight = daoHelper.getMaxWeight();
        
        if (minWeight != null && maxWeight != null) {
            String minMessageString = getExtremaString(msgMinWeight, minWeight);
            String maxMessageString = getExtremaString(msgMaxWeight, maxWeight);
            minWeightMessage.setText(minMessageString);
            maxWeightMessage.setText(maxMessageString);
        } else {
            Log.e(TAG, "recordweights were null in non-empty database");  
        }
        

    }
    
    @Override
    public void onDataChanged() {
        Log.d(TAG, "onDataChange fired");
        if (Settings.isRefreshUi()) {
            if (Settings.isLoadData() && 
                    daoHelper.getWeightCount() >= Settings.getGraphMinDataPoints()) {
                toggleStats(true);
                graphController.setContext(this);
                graphController.updateGraph(daoHelper.getWeightByDateAsc());
                graphLayout.removeAllViews();
                graphLayout.addView(graphController.getChart());
                
                refreshExtrema();
            } else {
                toggleStats(false);
            }
        }
    }
}

