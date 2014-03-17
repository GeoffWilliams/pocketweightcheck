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

import android.widget.TableLayout;
import android.widget.ImageButton;
import android.widget.TableRow;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.robolectric.Robolectric;
import org.robolectric.util.FragmentTestUtil;
import uk.me.geoffwilliams.pocketweightcheck.dao.MockDaoHelper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;
import org.robolectric.util.ActivityController;

/**
 *
 * @author geoff
 */
public class ViewDataActivityTest extends TestSupport {

    private final static String TAG = "pocketweightcheck.ViewDataActivityTest";
    private TableLayout layout;
    private static final int DELETE_BUTTON_POSITION = 2;
    private ViewDataActivity_ viewDataActivity;

    @Before
    public void setUp() {
        Settings.setLoadData(false);
        Settings.setRefreshUi(false);
        
        viewDataActivity = Robolectric.buildActivity(ViewDataActivity_.class)
                .create()
                .start()
                .resume()
                .visible()
                .get();

        assertNotNull(viewDataActivity);

        Log.d(TAG, "*** injected daoHelper ***");
        // inject mock DAO before the activity starts trying to load data...
        viewDataActivity.daoHelper = new MockDaoHelper();
        
        // enable loading data now the mock is in place
        Settings.setLoadData(true);

        layout = (TableLayout) viewDataActivity.findViewById(R.id.weightTableLayout);
        assertNotNull(layout);

        viewDataActivity.loadData();

    }

    @Test
    public void testEmpty() {
        // unload all data
        viewDataActivity.daoHelper.deleteAllData();
        
        // reload table
        viewDataActivity.loadData();
        
        // check we get the no data message
        TableRow row = (TableRow) layout.getChildAt(0);
        assertNotNull(row);
        TextView message = (TextView) row.getChildAt(0);
        assertNotNull(message);
        assertEquals(getResourceString(R.string.msg_no_data), message.getText());
    }
    
    /**
     * Check listing displayed ok
     */
    @Test
    public void testListing() {
        assertEquals("MockDaoHelper.SAMPLE_SIZE data rows + 1x header row must be displayed",
                MockDaoHelper.SAMPLE_SIZE + 1, layout.getChildCount());
    }

    /**
     * check listing handles a delete properly
     */
    @Test
    public void testDelete() {
        int rowCount = layout.getChildCount();
        assertEquals("initial sample data must be loaded",
                MockDaoHelper.SAMPLE_SIZE + 1, rowCount);
        // delete the second entry (I picked a random row)
        TableRow row = (TableRow) layout.getChildAt(2);

        // click the delete button belonging to the row...
        ImageButton deleteButton
                = (ImageButton) row.getChildAt(DELETE_BUTTON_POSITION);
        deleteButton.performClick();

        // check record was deleted and display updated
        assertEquals("record must be deleted",
                rowCount - 1, layout.getChildCount());
    }
}
