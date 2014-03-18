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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Date;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowHandler;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.tester.android.view.TestMenuItem;
import uk.me.geoffwilliams.pocketweightcheck.dao.DaoHelper;
import uk.me.geoffwilliams.pocketweightcheck.dao.MockDaoHelper;
import uk.me.geoffwilliams.pocketweightcheck.dao.Weight;

public class MainActivityTest extends TestSupport {
    private static final String TAG = "pocketweightcheck.MainActivityTest";
    LinearLayout statsLayout;
    LinearLayout noDataLayout;
    TextView minWeightMessage;
    TextView maxWeightMessage;
    DaoHelper daoHelper = new MockDaoHelper();
    MainActivity_ mainActivity;
    int VISIBLE = View.VISIBLE;
    int INVISIBLE = View.GONE;
    
    @Before
    public void setUp() throws Exception {
        // turn off automatic actions first...
        Settings.setPromptForDataEntry(false);
        Settings.setLoadData(false);
        // needs fixing after previous tests have run..
        Settings.setRefreshUi(true);
        
        mainActivity = (MainActivity_) Robolectric.buildActivity(MainActivity_.class)
                .create()
                .start()
                .resume()
                .visible()
                .get();
        assertNotNull(mainActivity);
        
        mainActivity.daoHelper = daoHelper;
        
        // UI components...
        statsLayout = (LinearLayout) mainActivity.findViewById(R.id.statsLayout);
        noDataLayout = (LinearLayout) mainActivity.findViewById(R.id.noDataLayout);
        minWeightMessage = (TextView) mainActivity.findViewById(R.id.minWeightMessage);
        maxWeightMessage = (TextView) mainActivity.findViewById(R.id.maxWeightMessage);          
        
        assertNotNull(statsLayout);
        assertNotNull(noDataLayout);
        assertNotNull(minWeightMessage);
        assertNotNull(maxWeightMessage);
        
        Settings.setLoadData(true);
        mainActivity.onDataChanged();

    }
    
    @Test
    public void testNoDataNoGraph() {
        daoHelper.deleteAllData();
        
        // should be no data until at least 2 points loaded
        for (int i = 0 ; i < Settings.getGraphMinDataPoints() ; i++) {
            mainActivity.onDataChanged();
            // 2x runs - 0 entries, 1 entries
            Log.d(TAG, "data size: " + daoHelper.getWeightCount());
            assertEquals(INVISIBLE, statsLayout.getVisibility());
            assertEquals(VISIBLE, noDataLayout.getVisibility());
            daoHelper.create(new Weight(new Date(), 88.9d));
        }
        
        // the last call to daoHelper.create() should trigger displaying the
        // graph...
        mainActivity.onDataChanged();
        assertEquals(VISIBLE, statsLayout.getVisibility());
        assertEquals(INVISIBLE, noDataLayout.getVisibility());

    }
    
    @Test
    public void testDataGraph() {
        assertEquals(VISIBLE, statsLayout.getVisibility());
        assertEquals(INVISIBLE, noDataLayout.getVisibility());
    }
    
    @Test
    public void testMinMaxWeightMessage() {        
        double minWeight = daoHelper.getMinWeight().getValue();
        double maxWeight = daoHelper.getMaxWeight().getValue();
        
        // check the number is contained in each message
        assertNotNull(minWeightMessage.getText());
        assertNotNull(maxWeightMessage.getText());
        assertTrue(minWeightMessage.getText().toString()
                .contains(String.valueOf(minWeight)));
        assertTrue(maxWeightMessage.getText().toString()
                .contains(String.valueOf(maxWeight)));
    }
    
    //
    // menu tests
    //
    @Test
    public void testEnterWeightMenuItem() {

        
        WeightEntryDialog dialog = mainActivity.getWeightEntryDialog();
        assertFalse(dialog.isVisible());            
        
        // manually invoke the callback...
        mainActivity.onOptionsItemSelected(createMenuItemInstance(R.id.enterWeightItem));
        
        
        // check the dialog is displaying
        ShadowHandler.idleMainLooper();
        assertTrue(dialog.isVisible());
           
    }
    
    @Test
    public void testViewDataMenuItem() {
        ShadowIntent intent = createShadowIntent(mainActivity, R.id.viewItem);
        
        assertEquals(
                intent.getComponent().getClassName(), 
                ViewDataActivity_.class.getName());       
        
    }
    
}
