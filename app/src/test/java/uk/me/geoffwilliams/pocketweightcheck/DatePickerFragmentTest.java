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
import java.util.Locale;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.util.ActivityController;
import org.robolectric.util.FragmentTestUtil;

/**
 *
 * @author geoff
 */
public class DatePickerFragmentTest extends TestSupport {
    private final static String TAG = "pwc.DatePickerFragmentT";
    private DatePickerFragment_ fragment;
    
    @Before
    public void setUp() {
        Locale.setDefault(Locale.US);

        Settings.setLoadData(false);
        // restore defaults - prevent cross-test contamination
        Settings.setMaxSampleAge(30);
        Settings.setMaxDetailAge(60);


        fragmentActivity = Robolectric.buildActivity(MainActivity_.class)
                .create()
                .start()
                .resume()
                .visible()
                .get();

        fragment = new DatePickerFragment_();

        fragmentManager = fragmentActivity.getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.mainActivity, fragment);
        fragmentTransaction.commit();

        assertNotNull(fragment);

        FragmentTestUtil.startFragment(fragment);
        assertNotNull(fragment.getActivity());


    }

    @After
    public void tearDown() {
        fragmentActivity.getSupportFragmentManager().beginTransaction().remove(fragment).commit();
    }

    @Test
    public void testDateTooOldFail() throws Exception {

        // work out a date that is too old
        Calendar cal = GregorianCalendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, - Settings.getMaxSampleAge() - 1);

        Log.d(TAG, "cal date " + cal.getTime().toString());
        
        assertNotNull(fragment);

        // send for processing
        ActivityController.of(fragment.getActivity()).visible();
        fragment.onDateSet(null,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));

        assertEquals(getResourceString(R.string.msgTooOld), ShadowToast.getTextOfLatestToast());
        assertFalse(fragment.isVisible());
    }

    @Test
    public void testDateFutureFail() throws Exception {
        Calendar cal = GregorianCalendar.getInstance();
        // need date a whole day in the future as we only
        // send year+month+day for processing... 
        cal.add(Calendar.DAY_OF_YEAR, 1);

        Log.d(TAG,"cal date " + cal.getTime().toString());

        // send for processing
        ActivityController.of(fragment.getActivity()).visible();
        fragment.onDateSet(null,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));

        
        assertEquals(getResourceString(R.string.msgFuture), ShadowToast.getTextOfLatestToast());
        assertFalse(fragment.isVisible());
    }
    
    @Test
    public void testDateAccepted() throws Exception {
        Calendar cal = GregorianCalendar.getInstance();
   
        Log.d(TAG,"cal date " + cal.getTime().toString());

        // send for processing
        ActivityController.of(fragment.getActivity()).visible();
        fragment.onDateSet(null, 
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));

        // check no toast message shown (date accepted)
        assertNull(ShadowToast.getTextOfLatestToast());
        assertFalse(fragment.isVisible());
    }
}
