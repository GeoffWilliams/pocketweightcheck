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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import uk.me.geoffwilliams.pocketweightcheck.Bmi;
import uk.me.geoffwilliams.pocketweightcheck.Settings;
import uk.me.geoffwilliams.pocketweightcheck.DataChangeListener;
import uk.me.geoffwilliams.pocketweightcheck.Bmi_;
import uk.me.geoffwilliams.pocketweightcheck.PrefsWrapper;
import uk.me.geoffwilliams.pocketweightcheck.Trend;

/**
 *
 * @author geoff
 */
@EBean(scope = EBean.Scope.Singleton)
public class DaoHelperImpl extends OrmLiteSqliteOpenHelper implements DaoHelper {

    // name of the database file for your application -- change to something appropriate for your app
    private static final String DATABASE_NAME = "pocketweightcheck.db";
    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "pwc.DaoHelper";
    private List<DataChangeListener> dataChangeListeners = new ArrayList<DataChangeListener>();
 
    private RuntimeExceptionDao<Weight, Integer> weightDao = null;
    private RuntimeExceptionDao<ArchivedWeight, Integer> archivedWeightDao = null;
    private RuntimeExceptionDao<RecordWeight, Integer> recordWeightDao = null;
    private Trend trend = new Trend();
    
    @Bean
    PrefsWrapper prefs;
    
    @Bean
    Bmi bmi;
    
    public DaoHelperImpl(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        weightDao = getWeightDao();
        recordWeightDao = getRecordWeightDao();
        archivedWeightDao = getArchivedWeightDao();
    }


    @Override
    public void onCreate(SQLiteDatabase sqld, ConnectionSource cs) {
        try {
            Log.i(TAG, "creating initial database schema");
            TableUtils.createTable(connectionSource, Weight.class);
            TableUtils.createTable(connectionSource, RecordWeight.class);
            TableUtils.createTable(connectionSource, ArchivedWeight.class);
        } catch (java.sql.SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqld, ConnectionSource cs, int i, int i1) {
        try{
            TableUtils.createTableIfNotExists(connectionSource, ArchivedWeight.class);
        } catch (java.sql.SQLException e) {
            throw new RuntimeException(e);
        }
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

    private RuntimeExceptionDao<ArchivedWeight, Integer> getArchivedWeightDao() {
        if (archivedWeightDao == null) {
            archivedWeightDao = getRuntimeExceptionDao(ArchivedWeight.class);
        }
        return archivedWeightDao;
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

    /**
     * Average (mean) a list of weights into a single ArchivedWeight object
     *
     *
     * @param weights
     * @return calculated average date and weight or null if passed in list empty or null
     */
    public ArchivedWeight averageData(List<Weight> weights) {
        ArchivedWeight average;
        if (weights != null && weights.size() > 0) {
            double weightSum = 0.0d;
            long dateSum = 0l;
            average = new ArchivedWeight();

            for (Weight w : weights) {
                weightSum += w.getWeight();
                dateSum += w.getSampleTime().getTime();
            }
            average.setWeight(weightSum/weights.size());
            average.setSampleTime(new Date(dateSum/weights.size()));
        } else {
            average = null;
        }
        return average;
    }

    
    private void archiveOldData() {
        // average the old data
        Log.d(TAG, String.format("inside archiveOldData(), cutoff date %s",
                Settings.archiveAfter().toString()));
        QueryBuilder<Weight, Integer> queryBuilder = weightDao.queryBuilder();
        queryBuilder.orderBy(Weight.COL_SAMPLE_TIME, true);
        Where<Weight, Integer> where = queryBuilder.where();
        try {
            where.lt(Weight.COL_SAMPLE_TIME, Settings.archiveAfter());
            List<Weight> weights = queryBuilder.query();
            if (! weights.isEmpty()) {
                Log.d(TAG, String.format("archiving %d weights", weights.size()));
                ArchivedWeight archive = averageData(weights);
                archivedWeightDao.create(archive);

                // then delete it
                DeleteBuilder<Weight, Integer> deleteBuilder = weightDao.deleteBuilder();
                where = deleteBuilder.where();

                where.lt(Weight.COL_SAMPLE_TIME, Settings.archiveAfter());
                int deleted = deleteBuilder.delete();
                Log.d(TAG, String.format("Records deleted: %d", deleted));
            }
        } catch (SQLException e) {
            throw new RuntimeException("wrapped SQLException", e);
        }
    }

    @Override
    public void create(final Weight weight) {
        try {
            TransactionManager.callInTransaction(connectionSource,
                new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {

                        // insert the new data
                        weightDao.create(weight);
                        
                        // remove any data that is too old
                        archiveOldData();

                        // cleanup old data
                        // save the min weight (if needed)
                        updateMin(weight);

                        // save the max weight (if needed)
                        updateMax(weight);
                        
                        // because its Void not void!!!
                        return null;
                    }
                });
            dataChanged();
        } catch (SQLException e) {
            throw new RuntimeException("wrapped SqlException", e);
        }
    }

    @Override
    public int delete(Weight weight) {
        int count;
        if (weight.isArchived()) {
            count = archivedWeightDao.delete(new ArchivedWeight(weight));
        } else {
            count = weightDao.delete(weight);
        }
        dataChanged();
        return count;
    }

    @Override
    public RecordWeight getMaxWeight() {
        List<RecordWeight> res = recordWeightDao.queryForEq(
                RecordWeight.COL_KEY, RecordWeight.KEY_MAX_WEIGHT);
        if (res.isEmpty()) {
            throw new RuntimeException("No data for " + RecordWeight.KEY_MAX_WEIGHT);
        }
        return res.get(0);
    }

    @Override
    public RecordWeight getMinWeight() {
        List<RecordWeight> res = recordWeightDao.queryForEq(
                RecordWeight.COL_KEY, RecordWeight.KEY_MIN_WEIGHT);
        if (res.isEmpty()) {
            throw new RuntimeException("No data for " + RecordWeight.KEY_MIN_WEIGHT);
        }
        return res.get(0);
    }

    private List<Weight> getArchivedWeights(boolean ascending) {
        List<ArchivedWeight> archivedWeights;
        List<Weight> weights = new ArrayList<Weight>();
        QueryBuilder<ArchivedWeight, Integer> queryBuilder = archivedWeightDao.queryBuilder();
        queryBuilder.orderBy(Weight.COL_SAMPLE_TIME, ascending);

        try {
            archivedWeights = queryBuilder.query();
            for (ArchivedWeight archivedWeight : archivedWeights) {
                weights.add(archivedWeight.toWeightObject());
            }
        } catch (SQLException e) {
            throw new RuntimeException("wrapped SQLException", e);
        }

        return weights;
    }


    private List<Weight> getWeights(boolean ascending) {
        return getWeights(ascending, true);
    }

    /**
     *
     * @param ascending true to order dates oldest to newest, false to order dates newest to oldest
     * @param getArchived
     * @return
     */
    private List<Weight> getWeights(boolean ascending, boolean getArchived) {
        List<Weight> weights;
        QueryBuilder<Weight, Integer> queryBuilder = weightDao.queryBuilder();
        queryBuilder.orderBy(Weight.COL_SAMPLE_TIME, ascending);

        try {
            weights = queryBuilder.query();

            // get the archived weights too
            if (getArchived) {
                // ascending order (oldest first), descending order (oldest last)
                int pos = ascending ? 0 : weights.size();

                weights.addAll(pos, getArchivedWeights(ascending));
            }

        } catch (SQLException e) {
            throw new RuntimeException("wrapped SQLException", e);
        }
        calculateTrend(weights);
        return weights;
    }
    
    private void calculateTrend(List<Weight> weights) {
        double previousTrend = -1;
        for (Weight weight : weights) {
            if (previousTrend < 0) {
                weight.setTrend(weight.getWeight());
            } else {
                double trend = previousTrend + 
                        (Settings.getSmoothingConstant() * 
                        (weight.getWeight() - previousTrend));
                weight.setTrend(trend);
            }
            previousTrend = weight.getTrend();
        }
    }
    
    
    @Override
    public List<Weight> getWeightByDateAsc() {
        return getWeights(true);
    }
    @Override
    public List<Weight> getWeightByDateDesc() {
        return getWeights(false);
    }

    @Override
    public void deleteAllData() {
        try {
            TableUtils.clearTable(connectionSource, Weight.class);
            TableUtils.clearTable(connectionSource, RecordWeight.class);
            TableUtils.clearTable(connectionSource, ArchivedWeight.class);
            dataChanged();
    
        } catch (SQLException e) {
            throw new RuntimeException("Wrapped exception", e);
        }
    }

    @Override
    public long getWeightCount() {
        return weightDao.countOf();
    }

    @Override
    public long getArchivedWeightCount() {
        return archivedWeightDao.countOf();
    }

    @Override
    public void registerListener(DataChangeListener listener) {
        if (! dataChangeListeners.contains(listener)) {
            dataChangeListeners.add(listener);
        }
    }
    
    private void dataChanged() {
        for (DataChangeListener dataChangeListener : dataChangeListeners) {
            dataChangeListener.onDataChanged();
        }
    }

    @Override
    public Weight getLatestWeight() {
        List<Weight> weights;
        QueryBuilder<Weight, Integer> queryBuilder = weightDao.queryBuilder();
        queryBuilder.orderBy(Weight.COL_SAMPLE_TIME, false);
        queryBuilder.limit(1l);
        Weight latestWeight = null;
        try {
            weights = queryBuilder.query();
            if (weights.size() == 1) {
                latestWeight = weights.get(0);
            }
        } catch (SQLException e) {
            throw new RuntimeException("wrapped SQLException", e);
        }
        return latestWeight;
    }

    @Override
    public Double getBmi() {
        Double bmiValue;
        
        // if unset in prefs will be primitive zero...
        float height = prefs.getHeight();
        Weight latestWeight = getLatestWeight();
        if (latestWeight == null || height == 0) {
            bmiValue = null;
        } else {
            bmiValue = bmi.calculateBmi(latestWeight.getWeight(), height);
        }
        return bmiValue;
    }

    @Override
    public void setPrefsWrapper(PrefsWrapper prefs) {
        this.prefs = prefs;
    }

    @Override
    public void setBmi(Bmi_ bmi) {
        this.bmi = bmi;
    }

    @Override
    public int getTrend() {
        return trend.calculateTrend(prefs.getTargetWeight(), getWeightByDateAsc());
    }
}
