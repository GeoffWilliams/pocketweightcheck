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

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import java.util.Calendar;
import java.util.Date;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.util.FragmentTestUtil;
/**
 *
 * @author geoff
 */
public class TimePickerFragmentTest extends TestSupport {
    
    private final static String TAG = "pocketweightcheck.TimePickerFragmentTest";
    protected FragmentActivity activity;
    private TimePickerFragment_ fragment;
    private FragmentManager fragmentManager;
 
    @Before
    public void setUp() {      
        activity = Robolectric.buildActivity(MainActivity.class)
                .create()
                .start()
                .resume()
                .get();

        fragment = new TimePickerFragment_();
        
        fragmentManager = activity.getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.main_activity, fragment);
        fragmentTransaction.commit();

        assertNotNull(fragment);
        assertNotNull(fragment.getActivity());

        FragmentTestUtil.startFragment(fragment);
        assertNotNull(fragment);

    }
    
    @Test
    public void testTimeAccepted() throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        Log.d(TAG, "cal date " + cal.getTime().toString());

        // send for processing
        fragment.show(activity.getSupportFragmentManager(), "tag");
        fragment.onTimeSet(null, //new DatePicker(activity), 
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE));

        // check no toast message shown (date accepted)
        assertNull(ShadowToast.getTextOfLatestToast());

    }
}
