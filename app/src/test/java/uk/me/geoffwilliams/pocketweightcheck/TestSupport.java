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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.tester.android.view.TestMenuItem;
import android.text.format.DateFormat;
import java.util.Date;

/**
 *
 * @author geoff
 */
@RunWith(RobolectricTestRunner.class)
public class TestSupport {

    //protected Activity activity;
    protected FragmentActivity fragmentActivity;
    protected FragmentManager fragmentManager;
    protected FragmentTransaction fragmentTransaction;
    
    protected String getResourceString(int id) {
        return Robolectric.application.getResources().getString(id);
    }
    
    @Before
    public void setUpLogging() {
        ShadowLog.stream = System.out;
    }

    public void startFragment(Fragment fragment) {
        fragmentActivity = Robolectric.buildActivity(FragmentActivity.class)
                .create()
                .start()
                .resume()
                .get();

        fragmentManager = fragmentActivity.getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(fragment, "tag");
        fragmentTransaction.commit();
    }
    
    protected MenuItem createMenuItemInstance(final int id) {
        return new TestMenuItem() {
            public int getItemId() {
                return id;
            }
        };
    }
         
    protected ShadowIntent createShadowIntent(Activity activity, int id) {
        
        activity.onOptionsItemSelected(createMenuItemInstance(id));

        ShadowActivity shadowActivity = Robolectric.shadowOf(activity);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        ShadowIntent shadowIntent = Robolectric.shadowOf(startedIntent);
        return shadowIntent;
    }

}
