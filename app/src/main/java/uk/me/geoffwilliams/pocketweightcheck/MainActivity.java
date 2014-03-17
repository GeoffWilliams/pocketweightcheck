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
import android.view.Menu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import uk.me.geoffwilliams.pocketweightcheck.dao.DaoHelper;
import uk.me.geoffwilliams.pocketweightcheck.dao.DaoHelperImpl;


@EActivity(R.layout.activity_main)
public class MainActivity extends FragmentActivity implements DataChangeListener {

    private final static String TAG = "pocketweightcheck";
    
//    @Inject 
//    private Provider<FragmentManager> fragmentManagerProvider;
//    
    @Bean(DaoHelperImpl.class)
    DaoHelper daoHelper;

    @ViewById(R.id.graphLayout)
    LinearLayout graphLayout;
    
    @ViewById(R.id.noDataLayout)
    LinearLayout noDataLayout;
    
    private WeightEntryDialog weightEntryDialog;
    
    @Bean
    GraphController graphController;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "starting!");
        setContentView(R.layout.activity_main);
        weightEntryDialog = new WeightEntryDialog_();
        if (Settings.isPromptForDataEntry()) {
            showWeightEntryDialog();
        }
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

   
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    private void toggleGraph(boolean show) {
        graphLayout.setVisibility( (show) ? View.VISIBLE: View.INVISIBLE);
        noDataLayout.setVisibility((show) ? View.INVISIBLE: View.VISIBLE);
        
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
    /* package */ void afterInject() {
        daoHelper.registerListener(this);
        onDataChanged();
    }

    @Override
    public void onDataChanged() {
        if (Settings.isRefreshUi()) {
            if (Settings.isLoadData() && 
                    daoHelper.getWeightCount() >= Settings.getGraphMinDataPoints()) {
                toggleGraph(true);
                graphController.setContext(this);
                graphController.updateGraph(daoHelper.getWeightByDateAsc());
                graphLayout.removeAllViews();
                graphLayout.addView(graphController.getChart());
            } else {
                toggleGraph(false);
            }
        }
    }
}

