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
import android.util.Log;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.robolectric.Robolectric;

import uk.me.geoffwilliams.pocketweightcheck.dao.ArchivedWeight;
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
    private Activity mainActivity;
    private int sampleCount = 0;
    private MockDataChangeListener mockDataChangeListener;
    private float height = 1.85f;
    private float targetWeight = 80.1f;
    private Bmi_ bmi;
    private Prefs_ prefs;
    private static final String TAG = "pwc.DAOTest";

    
    public DAOTest() {
        // disable autoload for testing
        Settings.setRefreshUi(false);
        mainActivity = Robolectric.buildActivity(MainActivity_.class).create().get();
        daoHelper = new DaoHelperImpl(mainActivity);
        cs = daoHelper.getConnectionSource();
        bmi = Bmi_.getInstance_(mainActivity);
        daoHelper.setBmi(bmi);
    }

    @Before
    public void setUp() {
        mockDataChangeListener = new MockDataChangeListener();
        daoHelper.registerListener(mockDataChangeListener);
        
        
        // height needs to be consistent in DAO and test class
        prefs = new Prefs_(mainActivity);
        prefs.height().put(Float.toString(height));
        prefs.targetWeight().put(Float.toString(targetWeight));
        PrefsWrapper prefsWrapper = new PrefsWrapper();
        prefsWrapper.setPrefs(prefs);
        daoHelper.setPrefsWrapper(prefsWrapper);

        // restore defaults
        Settings.setMaxSampleAge(30);
        Settings.setMaxDetailAge(60);
    }
    /**
     * Delete all test data between tests
     */
    @After
    public void cleanUp() throws SQLException {
        TableUtils.clearTable(cs, Weight.class);
        TableUtils.clearTable(cs, RecordWeight.class);
    }

    /**
     * Sample data - increasing in weight
     */
    private void insertSampleData() {
        insertSampleData(1);
    }

    private void insertSampleData(float increment, float count) {
        // insert a bunch of samples
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(Settings.getOldestAllowable());

        for (int i = 0; i <= count; i++) {
            Double value = MAX_SAMPLE_WEIGHT + ((i+1) * increment);
            value = Math.max(MIN_SAMPLE_WEIGHT, value);
            Weight weight = new Weight(cal.getTime(), value);
            daoHelper.create(weight);
            Log.d(TAG,"Added sample data:" + weight);
            sampleCount++;
            cal.add(Calendar.DAY_OF_YEAR, + 1);
        }
    }
    
    private void insertSampleData(float increment) {
        // reduce the amount of samples to make the tests run quicker
        Settings.setMaxSampleAge(3);
        insertSampleData(increment, Settings.getMaxSampleAge());
    }

    @Test
    public void testDuplicateFail() {
        Weight sample = new Weight(new Date(), 88.8d);

        // 1st should succeed
        daoHelper.create(sample);
        assertTrue(mockDataChangeListener.isUpdated());
        mockDataChangeListener.setUpdated(false);

        // duplicate should fail
        try {
            daoHelper.create(sample);
            fail("duplicate record was inserted!");
        } catch (RuntimeException e) {
            // pass :)
            assertFalse(mockDataChangeListener.isUpdated());
        }
    }

    @Test
    public void testInsertSucceed() {
        Weight sample = new Weight(new Date(), 88.8d);

        // 1st should succeed
        daoHelper.create(sample);
        assertTrue(mockDataChangeListener.isUpdated());
    }

    @Test
    public void testDeleteSucceed() {
        Weight sample = new Weight(new Date(), 88.8d);

        // insert record
        daoHelper.create(sample);

        // delete record
        assertEquals("1 record should have been deleted", 1,
                daoHelper.delete(sample));
        assertTrue(mockDataChangeListener.isUpdated());
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
        assertTrue(mockDataChangeListener.isUpdated());
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
        // date (older records are now averaged and archived :)
        Date oldestEntry = weights.get(0).getSampleTime();
        Date oldestAllowable = Settings.archiveAfter();
        assertTrue(
                oldestEntry.equals(oldestAllowable)
                || oldestEntry.after(oldestAllowable));
        assertTrue(mockDataChangeListener.isUpdated());
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
        assertTrue(mockDataChangeListener.isUpdated());

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
        assertTrue(mockDataChangeListener.isUpdated());
        
        // check the min was saved
        RecordWeight recordWeight = daoHelper.getMinWeight();
        assertEquals(recordWeight.getSampleTime(), minimum.getSampleTime());
        assertEquals(recordWeight.getValue(), minimum.getWeight());
    }
    
    @Test
    public void testWeightCountEmpty() {
        daoHelper.deleteAllData();
        assertTrue(mockDataChangeListener.isUpdated());
        assertEquals(0, daoHelper.getWeightCount());
    }
    
    @Test
    public void testWeightCountInitial() {
        assertEquals(sampleCount, daoHelper.getWeightCount());
    }
    
    @Test
    public void testWeightCountIncrease() {
        assertFalse(mockDataChangeListener.isUpdated());
        assertEquals(sampleCount, daoHelper.getWeightCount());
        daoHelper.create(new Weight(new Date(), 88.8d));
        assertTrue(mockDataChangeListener.isUpdated());
        assertEquals(sampleCount + 1, daoHelper.getWeightCount());
    }
    
    @Test
    public void testTrendCalculated() {
        // delete all data then enter the same weight a few times
        // trend weight should then be equal to the entered weight
        double testWeight = 88.8d;
        daoHelper.deleteAllData();
        daoHelper.create(new Weight(new Date(), testWeight));
        daoHelper.create(new Weight(new Date(), testWeight));
        daoHelper.create(new Weight(new Date(), testWeight));
        List<Weight> weights = daoHelper.getWeightByDateAsc();
        for (Weight weight : weights) {
            assertEquals(testWeight, weight.getTrend().doubleValue(), 0);
        }
    }
    
    @Test
    public void testMinWeightRead() {
        Weight minWeight = new Weight(new Date(), 0d);
        daoHelper.create(minWeight);
        RecordWeight recordWeight = daoHelper.getMinWeight();
        assertNotNull(recordWeight);
        assertEquals(minWeight.getWeight(), recordWeight.getValue(), 0);
        assertEquals(minWeight.getSampleTime(), recordWeight.getSampleTime());
    }
    
    @Test
    public void testMaxWeightRead() {
        Weight maxWeight = new Weight(new Date(), 999d);
        daoHelper.create(maxWeight);
        RecordWeight recordWeight = daoHelper.getMaxWeight();
        assertNotNull(recordWeight);
        assertEquals(maxWeight.getWeight(), recordWeight.getValue(), 0);
        assertEquals(maxWeight.getSampleTime(), recordWeight.getSampleTime());
    }
    
    @Test
    public void testLatestWeight() {
        // check that the weights returned from getLatestWeight() and the last
        // entry in the list of weights in ascending order match
        insertSampleData();
        List<Weight> weights = daoHelper.getWeightByDateAsc();
        assertFalse(weights.isEmpty());
        Weight weightsLast = weights.get(weights.size() - 1);
 
        Weight latestWeight = daoHelper.getLatestWeight();
        
        assertEquals(weightsLast.getWeight(), latestWeight.getWeight(), 0);
        assertTrue(weightsLast.getSampleTime()
                .equals(latestWeight.getSampleTime()));
        
        
    }
    
    @Test
    public void testBmi() {
        
        // test BMI before loading data
        assertNull(daoHelper.getBmi());
      
        // check BMI calculation matches the correct weight...
        insertSampleData();
        Double daoBmi = daoHelper.getBmi();
        assertNotNull(daoBmi);
        
        Weight latestWeight = daoHelper.getLatestWeight();
        assertNotNull(latestWeight);
        double latestBmi = bmi.calculateBmi(latestWeight.getWeight(), height);
        assertTrue(latestBmi > 0);
        
        // check independent calculations match
        assertEquals(latestBmi, daoBmi, 0);  
    }

    @Test
    public void testBmiNoHeight() {        
        // test BMI returns null when no height set...
        insertSampleData();
        prefs.height().put("0");
        Double daoBmi = daoHelper.getBmi();
        assertNull(daoBmi);
    }
        
    @Test
    public void testTrendDiverge() {
        insertSampleData(1);
        assertEquals(Trend.TREND_DIVERGING, daoHelper.getTrend());
    }
    
    @Test
    public void testTrendConverge() {
        insertSampleData(-1);
        assertEquals(Trend.TREND_CONVERGING, daoHelper.getTrend());
    }
    
    @Test
    public void testTrendStable() {
        insertSampleData(0.01f);
        assertEquals(Trend.TREND_STABLE, daoHelper.getTrend());
    }
    
    
    @Test
    public void testTrendNoTarget() {
        // test trend returns Trend.TREND_ERROR when no target weight set
        insertSampleData();
        prefs.targetWeight().put("0");
        assertEquals(Trend.TREND_ERROR, daoHelper.getTrend());
    }


    @Test
    public void testAverageWeightCalculation() {
        // test date and weight are calculated as mean
        SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd");
        List<Weight> input = new ArrayList<Weight>();
        try {
            input.add(new Weight(dateParser.parse("2015-01-30"), 100.0d));
            input.add(new Weight(dateParser.parse("2015-01-1"), 50.0d));

            ArchivedWeight output = daoHelper.averageData(input);
            assertEquals((double) 75.0d, (double) output.getWeight(), 0.1d);
            assertEquals("2015-01-15", dateParser.format(output.getSampleTime()));
        } catch (ParseException e) {
            fail("can't parse date for tests - error in test logic");
        }

    }

    @Test
    public void testOldDataArchived() {
        // when we have >= 60 days worth data, take the last 30 days worth, average it and delete
        // the source data
        // tweak settings to allow old the data to be accepted
        Settings.setMaxSampleAge(6);
        Settings.setMaxDetailAge(6);
        insertSampleData(1, 6);

        // back to normal settings
        Settings.setMaxDetailAge(3);

        // insert a new record to trigger archival process
        insertSampleData(1,1);

        // there should now be data in the archived_weight table so do a query and count it
        assertTrue(daoHelper.getArchivedWeightCount() > 0);
    }

    @Test
    public void testOldDataRead() {
        // tweak settings to allow old the data to be accepted
        Settings.setMaxSampleAge(6);
        Settings.setMaxDetailAge(6);
        insertSampleData(1, 6);

        // back to normal settings
        Settings.setMaxDetailAge(3);

        // insert a new record to trigger archival process
        insertSampleData(1,1);

        // check the archived weights come back when we ask for weights
        List<Weight> weights = daoHelper.getWeightByDateAsc();

        // last entry should be an instance of ArchivedWeight
        assertTrue(weights.get(weights.size() - 1).isArchived());
    }

    @Test
    public void testEmptyDataAveraged() {
        // test that passing an empty list or null to be averaged returns null
        assertNull(daoHelper.averageData(new ArrayList<Weight>()));
        assertNull(daoHelper.averageData(null));
    }

    @Test
    public void testArchivedWeightTypeConversion() {
        // check all fields are copied correctly into new object
        ArchivedWeight aw = new ArchivedWeight();
        double sampleWeight = 88.0d;
        Date sampleTime = new Date();
        aw.setWeight(sampleWeight);
        aw.setSampleTime(sampleTime);

        Weight w = aw.toWeightObject();
        assertEquals(sampleWeight, w.getWeight(), 0);
        assertEquals(sampleTime.getTime(), w.getSampleTime().getTime());
    }
}
