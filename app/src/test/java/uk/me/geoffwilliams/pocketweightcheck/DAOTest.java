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
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;
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
import org.junit.Before;
import org.robolectric.Robolectric;
import uk.me.geoffwilliams.pocketweightcheck.dao.DaoHelper;
import uk.me.geoffwilliams.pocketweightcheck.dao.RecordWeight;
import uk.me.geoffwilliams.pocketweightcheck.dao.Weight;

/**
 *
 * @author geoff
 */
public class DAOTest extends TestSupport {

    Activity activity = Robolectric.buildActivity(MainActivity.class).create().get();
    //RuntimeExceptionDao<Weight, Integer> weightDao;
    //RuntimeExceptionDao<RecordWeight, Integer> recordWeightDao;
    ConnectionSource cs;
    DaoHelper daoHelper;

    @Before
    public void setUp() {
        daoHelper = new DaoHelper(activity);
        cs = daoHelper.getConnectionSource();
//        weightDao = daoHelper.getWeightDao();
//        recordWeightDao = daoHelper.getRecordWeightDao();
    }

    /**
     * Delete all test data between tests
     */
    @After
    public void cleanUp() throws SQLException {
        TableUtils.clearTable(cs, Weight.class);
        TableUtils.clearTable(cs, RecordWeight.class);
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
    public void testSelectByDateAsc() {
//        QueryBuilder<Weight, Integer> queryBuilder = weightDao.queryBuilder();
//        OrderBy<Weight, Integer> orderBy = queryBuilder.orderBy("", true)
//        Where<Account, String> where = queryBuilder.where();
//        SelectArg selectArg = new SelectArg();
//// define our query as 'name = ?'
//        where.eq(Account.NAME_FIELD_NAME, selectArg);
//// prepare it so it is ready for later query or iterator calls
//        PreparedQuery<Account> preparedQuery = queryBuilder.prepare();
        fail();
    }

    @Test
    public void testSelectByDateDesc() {
        fail();
    }

    @Test
    public void testInsertRemovesOldData() {

        // insert a bunch of samples
        for (int i = 0; i < DateUtils.MAX_SAMPLE_DATE + 10; i++) {
            Calendar cal = GregorianCalendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_YEAR, -1);

            Weight weight = new Weight(cal.getTime(), 90.1d);
            daoHelper.create(weight);
        }

        // check there is nothing too old saved
    }

    @Test
    public void testMaxWeightSaved() {
        Double maxWeight = 100d;
        Date date;
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(new Date());
        date = cal.getTime();
        // insert a bunch of samples - 1st one is the max
        for (int i = 0; i < DateUtils.MAX_SAMPLE_DATE + 10; i++) {
            if (i > 0) {
                cal.add(Calendar.DAY_OF_YEAR, -1);
            }

            Weight weight = new Weight(cal.getTime(), new Double(maxWeight - i));
            daoHelper.create(weight);
        }

        // check the max was saved
        RecordWeight recordWeight = daoHelper.getMaxWeight();
        assertEquals(recordWeight.getSampleTime(), date);
        assertEquals(recordWeight.getValue(), maxWeight);
    }

    @Test
    public void testMinWeightSaved() {
        Double minWeight = 60d;
        Date date;
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(new Date());
        date = cal.getTime();
        // insert a bunch of samples - 1st one is the max
        for (int i = 0; i < DateUtils.MAX_SAMPLE_DATE + 10; i++) {
            if (i > 0) {
                cal.add(Calendar.DAY_OF_YEAR, -1);
            }

            Weight weight = new Weight(cal.getTime(), new Double(minWeight + i));
            daoHelper.create(weight);
        }

        // check the min was saved
        RecordWeight recordWeight = daoHelper.getMinWeight();
        assertEquals(recordWeight.getSampleTime(), date);
        assertEquals(recordWeight.getValue(), minWeight);
    }
}
