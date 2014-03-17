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
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.robolectric.Robolectric;
import uk.me.geoffwilliams.pocketweightcheck.dao.DaoHelperImpl;
import uk.me.geoffwilliams.pocketweightcheck.dao.RecordWeight;
import uk.me.geoffwilliams.pocketweightcheck.dao.Weight;

/**
 *
 * @author geoff
 */
public class DAOTest extends TestSupport {

    private ConnectionSource cs;
    private DaoHelperImpl daoHelper;
    private static final double MAX_SAMPLE_WEIGHT = 111.1d;
    private static final double MIN_SAMPLE_WEIGHT = 66.6d;
    private Activity activity;
    private int sampleCount = 0;

    public DAOTest() {
        // disable autoload for testing
        Settings.setRefreshUi(false);
        activity = Robolectric.buildActivity(MainActivity.class).create().get();
        daoHelper = new DaoHelperImpl(activity);
        cs = daoHelper.getConnectionSource();
    }

    /**
     * Delete all test data between tests
     */
    @After
    public void cleanUp() throws SQLException {
        TableUtils.clearTable(cs, Weight.class);
        TableUtils.clearTable(cs, RecordWeight.class);
    }

    private void insertSampleData() {
        // insert a bunch of samples
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(new Date());
        
        // reduce the amount of samples to make the tests run quicker
        Settings.setMaxSampleAge(3);
        
        for (int i = 0; i < Settings.getMaxSampleAge() + 1; i++) {
            cal.add(Calendar.DAY_OF_YEAR, -1);
            Double value = MAX_SAMPLE_WEIGHT - i;
            value = Math.max(MIN_SAMPLE_WEIGHT, value);
            Weight weight = new Weight(cal.getTime(), value);
            daoHelper.create(weight);
            sampleCount++;
        }
    }

    @Test
    public void testDuplicateFail() {
        Weight sample = new Weight(new Date(), 88.8d);

        // 1st should succeed
        daoHelper.create(sample);

        // duplicate should fail
        try {
            daoHelper.create(sample);
            fail("duplicate record was inserted!");
        } catch (RuntimeException e) {
            // pass :)
        }
    }

    @Test
    public void testInsertSucceed() {
        Weight sample = new Weight(new Date(), 88.8d);

        // 1st should succeed
        daoHelper.create(sample);
    }

    @Test
    public void testDeleteSucceed() {
        Weight sample = new Weight(new Date(), 88.8d);

        // insert record
        daoHelper.create(sample);

        // delete record
        assertEquals("1 record should have been deleted", 1,
                daoHelper.delete(sample));
    }
    
    @Test
    public void testDeleteAllSucceed() {
        Weight sample = new Weight(new Date(), 88.8d);

        // insert record
        daoHelper.create(sample);
        
        // delete all data
        daoHelper.deleteAllData();
        // check all tables empty
        assertEquals("all weights must be deleted",
                0, daoHelper.getWeightByDateAsc().size());
    }

    @Test
    public void testSelectByDateAsc() {
        insertSampleData();
        List<Weight> weights = daoHelper.getWeightByDateAsc();
        assertNotNull(weights);
        assertTrue(!weights.isEmpty());

        // no need to test each date is stored in order - that would be testing
        // the ORM itself.  Just check first/last dates are in the correct order
        // indicating the ORDER BY clause has been applied properly
        Date firstEntry = weights.get(0).getSampleTime();
        Date lastEntry = weights.get(weights.size() - 1).getSampleTime();
        assertTrue(firstEntry.before(lastEntry));
    }

    @Test
    public void testSelectByDateDesc() {
        insertSampleData();
        List<Weight> weights = daoHelper.getWeightByDateDesc();
        assertNotNull(weights);
        assertTrue(!weights.isEmpty());

        Date firstEntry = weights.get(0).getSampleTime();
        Date lastEntry = weights.get(weights.size() - 1).getSampleTime();
        assertTrue(firstEntry.after(lastEntry));
    }

    @Test
    public void testInsertRemovesOldData() {
        // insert a bunch of test records.  Some should be pruned on entry by
        // the DAO
        insertSampleData();

        // Ask for all data and ensure there is nothing too old
        List<Weight> weights = daoHelper.getWeightByDateAsc();
        assertNotNull(weights);
        assertFalse(weights.isEmpty());

        // check that the earliest record is not before the oldest allowable
        // date
        Date oldestEntry = weights.get(0).getSampleTime();
        Date oldestAllowable = Settings.getOldestAllowable();
        assertTrue(
                oldestEntry.equals(oldestAllowable)
                || oldestEntry.after(oldestAllowable));
    }

    @Test
    public void testMaxWeightSaved() {
        // add a bunch of records...
        insertSampleData();
        
        // mix in our maximum weight as the earliest allowed...
        Weight maximum = new Weight(
                Settings.getOldestAllowable(), 
                Double.valueOf(MAX_SAMPLE_WEIGHT + 10)
        );
        daoHelper.create(maximum);

        // check the max was saved
        RecordWeight recordWeight = daoHelper.getMaxWeight();
        assertEquals(recordWeight.getSampleTime(), maximum.getSampleTime());
        assertEquals(recordWeight.getValue(), maximum.getWeight());
    }

    @Test
    public void testMinWeightSaved() {
        
        // insert a bunch of data
        insertSampleData();
        
        // mix in our minimum weight as the earliest allowed...
        Weight minimum = new Weight(
                Settings.getOldestAllowable(), 
                Double.valueOf(MIN_SAMPLE_WEIGHT - 10)
        );
        daoHelper.create(minimum);
        
        // check the min was saved
        RecordWeight recordWeight = daoHelper.getMinWeight();
        assertEquals(recordWeight.getSampleTime(), minimum.getSampleTime());
        assertEquals(recordWeight.getValue(), minimum.getWeight());
    }
    
    @Test
    public void testWeightCountEmpty() {
        daoHelper.deleteAllData();
        assertEquals(0, daoHelper.getWeightCount());
    }
    
    @Test
    public void testWeightCountInitial() {
        assertEquals(sampleCount, daoHelper.getWeightCount());
    }
    
    @Test
    public void testWeightCountIncrease() {
        assertEquals(sampleCount, daoHelper.getWeightCount());
        daoHelper.create(new Weight(new Date(), 88.8d));
        assertEquals(sampleCount + 1, daoHelper.getWeightCount());
    }
}
