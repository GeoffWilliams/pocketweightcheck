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
package uk.me.geoffwilliams.pocketweightcheck.dao;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import android.util.Log;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;
import uk.me.geoffwilliams.pocketweightcheck.DateUtils;
//import uk.me.geoffwilliams.pocketweightcheck.R;

/**
 *
 * @author geoff
 */
public class DaoHelper extends OrmLiteSqliteOpenHelper {

    // name of the database file for your application -- change to something appropriate for your app
    private static final String DATABASE_NAME = "pocketweightcheck.db";
    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "pocketweightcheck.DaoHelper";

    private RuntimeExceptionDao<Weight, Integer> weightDao = null;
    private RuntimeExceptionDao<RecordWeight, Integer> recordWeightDao = null;

    public DaoHelper(Context context) {
        this(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DaoHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion) {
        super(context, databaseName, factory, databaseVersion);
        weightDao = getWeightDao();
        recordWeightDao = getRecordWeightDao();
    }

    @Override
    public void onCreate(SQLiteDatabase sqld, ConnectionSource cs) {
        try {
            Log.i(TAG, "creating initial database schema");
            TableUtils.createTable(connectionSource, Weight.class);
            TableUtils.createTable(connectionSource, RecordWeight.class);
        } catch (java.sql.SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqld, ConnectionSource cs, int i, int i1) {

    }

    private RuntimeExceptionDao<Weight, Integer> getWeightDao() {
        if (weightDao == null) {
            weightDao = getRuntimeExceptionDao(Weight.class);
        }
        return weightDao;
    }

    private RuntimeExceptionDao<RecordWeight, Integer> getRecordWeightDao() {
        if (recordWeightDao == null) {
            recordWeightDao = getRuntimeExceptionDao(RecordWeight.class);
        }
        return recordWeightDao;
    }

    private void updateExtrema(RecordWeight recordWeight, Weight weight) {
        recordWeight.setValue(weight.getWeight());
        recordWeight.setSampleTime(weight.getSampleTime());
        recordWeightDao.update(recordWeight);
    }

    private void updateMin(Weight weight) {
        String keyValue = RecordWeight.KEY_MIN_WEIGHT;
        List<RecordWeight> extrema
                = recordWeightDao.queryForEq(RecordWeight.COL_KEY, keyValue);
        if (extrema.isEmpty()) {
            // no data yet so this is a new extrema
            recordWeightDao.create(new RecordWeight(
                    keyValue, weight.getSampleTime(), weight.getWeight()));
        } else {
            RecordWeight recordWeight = extrema.get(0);
            if (weight.getWeight() < recordWeight.getValue()) {
                updateExtrema(recordWeight, weight);
            }
        }
    }

    private void updateMax(Weight weight) {
        String keyValue = RecordWeight.KEY_MAX_WEIGHT;
        List<RecordWeight> extrema
                = recordWeightDao.queryForEq(RecordWeight.COL_KEY, keyValue);
        if (extrema.isEmpty()) {
            // no data yet so this is a new extrema
            recordWeightDao.create(new RecordWeight(
                    keyValue, weight.getSampleTime(), weight.getWeight()));
        } else {
            RecordWeight recordWeight = extrema.get(0);
            if (weight.getWeight() > recordWeight.getValue()) {
                updateExtrema(recordWeight, weight);
            }
        }
    }
    
    private void deleteOldData() {
        DeleteBuilder<Weight, Integer> deleteBuilder = weightDao.deleteBuilder();
        Where<Weight, Integer> where = deleteBuilder.where();
        try {
            where.lt(Weight.COL_SAMPLE_TIME, new DateUtils().getOldestAllowable());
            deleteBuilder.delete();
        } catch (SQLException e) {
            throw new RuntimeException("wrapped SQLException", e);
        }
    }

    public void create(final Weight weight) {
        try {
            TransactionManager.callInTransaction(connectionSource,
                new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {

                        // insert the new data
                        weightDao.create(weight);
                        
                        // remove any data that is too old
                        deleteOldData();

                        // cleanup old data
                        // save the min weight (if needed)
                        updateMin(weight);

                        // save the max weight (if needed)
                        updateMax(weight);

                        // because its Void not void!!!
                        return null;
                    }
                });
        } catch (SQLException e) {
            throw new RuntimeException("wrapped SqlException", e);
        }
    }

    public int delete(Weight weight) {
        return weightDao.delete(weight);
    }

    public RecordWeight getMaxWeight() {
        List<RecordWeight> res = recordWeightDao.queryForEq(
                RecordWeight.COL_KEY, RecordWeight.KEY_MAX_WEIGHT);
        if (res.isEmpty()) {
            throw new RuntimeException("No data for " + RecordWeight.KEY_MAX_WEIGHT);
        }
        return res.get(0);
    }

    public RecordWeight getMinWeight() {
        List<RecordWeight> res = recordWeightDao.queryForEq(
                RecordWeight.COL_KEY, RecordWeight.KEY_MIN_WEIGHT);
        if (res.isEmpty()) {
            throw new RuntimeException("No data for " + RecordWeight.KEY_MIN_WEIGHT);
        }
        return res.get(0);
    }
    
    private List<Weight> getWeights(boolean ascending) {
        List<Weight> weights;
        QueryBuilder<Weight, Integer> queryBuilder = weightDao.queryBuilder();
        queryBuilder.orderBy(Weight.COL_SAMPLE_TIME, ascending);
        try {
            weights = queryBuilder.query();
        } catch (SQLException e) {
            throw new RuntimeException("wrapped SQLException", e);
        }
        return weights;
    }
    
    
    public List<Weight> getWeightByDateAsc() {
        return getWeights(true);
    }
    public List<Weight> getWeightByDateDesc() {
        return getWeights(false);
    }
}
