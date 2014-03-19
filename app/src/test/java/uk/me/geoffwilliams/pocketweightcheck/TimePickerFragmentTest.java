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

import android.util.Log;
import java.util.Calendar;
import java.util.GregorianCalendar;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.util.FragmentTestUtil;
/**
 *
 * @author geoff
 */
public class TimePickerFragmentTest extends TestSupport {
    
    private final static String TAG = "pocketweightcheck.TimePickerFragmentTest";
    private TimePickerFragment_ fragment;
 
    @Before
    public void setUp() {      
        Settings.setRefreshUi(false);
        
        fragmentActivity = Robolectric.buildActivity(MainActivity_.class)
                .create()
                .start()
                .resume()
                .visible()
                .get();

        fragment = new TimePickerFragment_();
        
        fragmentManager = fragmentActivity.getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.mainActivity, fragment);
        fragmentTransaction.commit();

        assertNotNull(fragment);
        assertNotNull(fragment.getActivity());

        FragmentTestUtil.startFragment(fragment);
        assertNotNull(fragment);

    }
    
    @Test
    public void testTimeAccepted() throws Exception {
        Calendar cal = GregorianCalendar.getInstance();
        
        Log.d(TAG, "cal date " + cal.getTime().toString());

        // send for processing
        fragment.show(fragmentActivity.getSupportFragmentManager(), "tag");
        fragment.onTimeSet(null, 
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE));

        // check no toast message shown (date accepted)
        assertNull(ShadowToast.getTextOfLatestToast());
        assertFalse(fragment.isVisible());

    }
    
    @Test
    public void testTimeFutureFail() throws Exception {
        Calendar cal = GregorianCalendar.getInstance();
        // need date a whole day in the future as we only
        // send year+month+day for processing... 
        cal.add(Calendar.MINUTE, 1);

        Log.d(TAG,"cal date " + cal.getTime().toString());

        // send for processing
        fragment.show(fragmentActivity.getSupportFragmentManager(), "tag");
        fragment.onTimeSet(null, 
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE));

        
        assertEquals(getResourceString(R.string.msgFuture), 
                ShadowToast.getTextOfLatestToast());
        assertFalse(fragment.isVisible());
    }    

    @Test
    public void testTimeTooOldFail() throws Exception {
        // since we are only setting the TIME in the dialog
        // we must first set the DATE so that decreasing by
        // one minute in TIME will cause rejection
        Calendar cal = GregorianCalendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, - Settings.getMaxSampleAge());
        Log.d(TAG, "Computed old DATE: " + cal.getTime().toString());
        
        // put the date inside the dateutils instance singleton and ensure its 
        // accepted.  this is equivalent to setting the date first using the
        // date picker
        DateUtils_ dateUtils = DateUtils_.getInstance_(fragmentActivity);
        assertTrue(dateUtils.setDate(cal.getTime()));
        
        // compute a time that is too old
        cal.add(Calendar.MINUTE, -5);
        Log.d(TAG, "Computed old TIME: " + cal.getTime().toString());
        
        Log.d(TAG,"cal date " + cal.getTime().toString());

        // send for processing
        fragment.show(fragmentActivity.getSupportFragmentManager(), "tag");
        fragment.onTimeSet(null, 
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE));

        
        assertEquals(getResourceString(R.string.msgTooOld), 
                ShadowToast.getTextOfLatestToast());
        assertFalse(fragment.isVisible());
    }    
}
