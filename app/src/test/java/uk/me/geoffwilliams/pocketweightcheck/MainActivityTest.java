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

    LinearLayout graphLayout;
    LinearLayout noDataLayout;
    DaoHelper daoHelper = new MockDaoHelper();
    MainActivity_ mainActivity;
    
    @Before
    public void setUp() throws Exception {
        // turn off automatic actions first...
        Settings.setPromptForDataEntry(false);
        Settings.setLoadData(false);
        
        mainActivity = (MainActivity_) Robolectric.buildActivity(MainActivity_.class)
                .create()
                .start()
                .resume()
                .visible()
                .get();
        assertNotNull(mainActivity);
        
        mainActivity.daoHelper = daoHelper;
        
        graphLayout = (LinearLayout) mainActivity.findViewById(R.id.graphLayout);
        noDataLayout = (LinearLayout) mainActivity.findViewById(R.id.noDataLayout);
        
        Settings.setLoadData(true);
        mainActivity.loadData();

    }
    
    @Test
    public void testNoDataNoGraph() {
        daoHelper.deleteAllData();
        mainActivity.loadData();
        // should be no data until at least 2 points loaded
        for (int i = 0 ; i < 3 ; i++) {
            
            // 3x runs - 0 entries, 1 entries, 2 entries
            
            assertEquals(View.INVISIBLE, graphLayout.getVisibility());
            assertEquals(View.VISIBLE, noDataLayout.getVisibility());
            daoHelper.create(new Weight(new Date(), 88.9d));
        }
    }
    
    @Test
    public void testDataGraph() {
        assertEquals(View.VISIBLE, graphLayout.getVisibility());
        assertEquals(View.INVISIBLE, noDataLayout.getVisibility());
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
