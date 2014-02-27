/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.me.geoffwilliams.pocketweightcheck;

import android.app.Activity;
import android.widget.Button;
import android.widget.EditText;
import java.util.Calendar;
import java.util.Date;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowToast;
import static uk.me.geoffwilliams.pocketweightcheck.WeightEntryDialogTest.startFragment;

/**
 *
 * @author geoff
 */
@RunWith(RobolectricTestRunner.class)
public class DatePickerFragmentTest {
    
    protected Activity activity = Robolectric.buildActivity(MainActivity.class).create().get();
    
    @Before
    public void setUp() {
//        WeightEntryDialog_ weightEntryDialog = new WeightEntryDialog_();
//        startFragment(weightEntryDialog);
//        
//        weightEntryEditText = (EditText) weightEntryDialog.findViewById(R.id.weightEntryEditText);
//        okButton = (Button) weightEntryDialog.findViewById(R.id.okButton);
//        
//        assertNotNull(weightEntryDialog);
    }

    
    private String getResourceString(int id) {
        return activity.getResources().getString(id);
    }
    
    private void enterDate(int y, int m, int d) {
        DatePickerFragment fragment = new DatePickerFragment();
    }

    @Test
    public void testDateTooOldFail() throws Exception {

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, -DateUtils.MAX_SAMPLE_DATE);
        enterDate(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        );

        assertEquals(getResourceString(R.string.msg_too_old), ShadowToast.getTextOfLatestToast());

    }

    @Test
    public void testDateFutureFail() throws Exception {

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        enterDate(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) - 30,
                cal.get(Calendar.DAY_OF_MONTH)
        );

        assertEquals(getResourceString(R.string.msg_future), ShadowToast.getTextOfLatestToast());

    }
}
