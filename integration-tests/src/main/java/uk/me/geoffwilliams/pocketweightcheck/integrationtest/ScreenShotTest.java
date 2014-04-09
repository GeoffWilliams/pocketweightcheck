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

package uk.me.geoffwilliams.pocketweightcheck.integrationtest;
import com.github.rtyley.android.screenshot.celebrity.Screenshots;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import android.app.Activity;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;
import android.view.KeyEvent;
import uk.me.geoffwilliams.pocketweightcheck.MainActivity_;

import com.robotium.solo.Solo;
import java.util.Calendar;
import java.util.GregorianCalendar;
import uk.me.geoffwilliams.pocketweightcheck.R;
import uk.me.geoffwilliams.pocketweightcheck.Settings;
import uk.me.geoffwilliams.pocketweightcheck.dao.DaoHelper;
import uk.me.geoffwilliams.pocketweightcheck.dao.DaoHelperImpl;
import uk.me.geoffwilliams.pocketweightcheck.dao.Weight;

/**
 *
 * @author geoff
 */
public class ScreenShotTest extends ActivityInstrumentationTestCase2<MainActivity_> {

    private final static String TAG = "ScreenShotTest";
    private final double MAX_SAMPLE_WEIGHT = 92.4f;
    private final double MIN_SAMPLE_WEIGHT = 40f;

    // yes my laptop really is that slow... :(
    private final int DELAY = 15000;
    
    private Solo solo;
    private DaoHelper daoHelper; 
    Activity activity;

    public ScreenShotTest() {
        super(MainActivity_.class);
    }

    @Override
    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation());
        activity = startActivitySync(MainActivity_.class);
        
        Settings.setRefreshUi(false);
        daoHelper = ((MainActivity_) activity).getDaoHelper();
        insertSampleData(-1.1f);
        Settings.setRefreshUi(true);
        
        // set a height and target weight
        PreferenceManager.getDefaultSharedPreferences(activity).edit()
                .putString("targetWeight", "70")
                .putString("height", "1.7")
                .commit();

        // restart activity to get refreshed data...
        solo.finishOpenedActivities();
        activity = startActivitySync(MainActivity_.class);
                
    }

    @Override
    protected void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
    
    private void insertSampleData(float increment) {
        // insert a bunch of samples
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(Settings.getOldestAllowable());

        
        for (int i = 0; i <= Settings.getMaxSampleAge(); i++) {
            Double value = MAX_SAMPLE_WEIGHT + ((i+1) * increment);
            value = Math.max(MIN_SAMPLE_WEIGHT, value);
            Weight weight = new Weight(cal.getTime(), value);
            daoHelper.create(weight);
            Log.d(TAG,"Added sample data:" + weight);
            cal.add(Calendar.DAY_OF_YEAR, + 1);
        }
    }

    
    @LargeTest
    public void testScreenShot() throws Exception {

        // initial render
        solo.sleep(DELAY);
        
        // weight entry dialog
        Screenshots.poseForScreenshot();
        solo.clickOnButton("Cancel");

        // graph + table
        solo.sleep(DELAY);
        Screenshots.poseForScreenshot();

        // view data
        solo.sleep(DELAY);
        solo.clickOnActionBarItem(R.id.viewItem);
        solo.sleep(DELAY);
        Screenshots.poseForScreenshot();
        
        activity.finish();
    }


    private <T extends Activity> T startActivitySync(Class<T> clazz) {
        Intent intent = new Intent(getInstrumentation().getTargetContext(), clazz);
        intent.setFlags(intent.getFlags() | FLAG_ACTIVITY_NEW_TASK);
        return (T) getInstrumentation().startActivitySync(intent);
    }

}
