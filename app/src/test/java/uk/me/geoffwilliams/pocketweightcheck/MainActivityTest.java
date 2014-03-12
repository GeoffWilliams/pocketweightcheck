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
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import uk.me.geoffwilliams.pocketweightcheck.dao.DaoHelper;
import uk.me.geoffwilliams.pocketweightcheck.dao.MockDaoHelper;

public class MainActivityTest extends TestSupport {

    LinearLayout graphLayout;
    LinearLayout noDataLayout;
    DaoHelper daoHelper = new MockDaoHelper();
    MainActivity_ mainActivity;
    
    @Before
    public void setUp() throws Exception {
        
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
        assertEquals(View.INVISIBLE, graphLayout.getVisibility());
        assertEquals(View.VISIBLE, noDataLayout.getVisibility());
    }
    
    @Test
    public void testDataGraph() {
        assertEquals(View.VISIBLE, graphLayout.getVisibility());
        assertEquals(View.INVISIBLE, noDataLayout.getVisibility());
    }
}
