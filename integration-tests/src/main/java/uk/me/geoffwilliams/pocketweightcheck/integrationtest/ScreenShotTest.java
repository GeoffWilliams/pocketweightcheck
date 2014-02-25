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
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;
import uk.me.geoffwilliams.pocketweightcheck.MainActivity;

import com.robotium.solo.Solo;

/**
 *
 * @author geoff
 */
public class ScreenShotTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private final static String TAG = "ScreenShotTest";
    
    private Solo solo;

    public ScreenShotTest() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation());
    }

    @Override
    protected void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    @LargeTest
    public void testScreenShot() throws Exception {
        driveMainActivity();

    }


    private void driveMainActivity() throws InterruptedException {
        Activity activity = startActivitySync(MainActivity.class);
        Thread.sleep(1000);
        Screenshots.poseForScreenshot();
        Thread.sleep(1000);
        Screenshots.poseForScreenshot();

        activity.finish();
    }


    private <T extends Activity> T startActivitySync(Class<T> clazz) {
        Intent intent = new Intent(getInstrumentation().getTargetContext(), clazz);
        intent.setFlags(intent.getFlags() | FLAG_ACTIVITY_NEW_TASK);
        return (T) getInstrumentation().startActivitySync(intent);
    }

}
