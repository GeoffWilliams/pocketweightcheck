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

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.j256.ormlite.support.ConnectionSource;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import uk.me.geoffwilliams.pocketweightcheck.DataChangeListener;

/**
 *
 * @author geoff
 */
public class MockDaoHelper implements DaoHelper {
    public final static int SAMPLE_SIZE = 4;
    public final static double SAMPLE_WEIGHT_INITIAL = 111.1d;
    private final static String TAG = "pocketweightcheck.MockDaoHelper";
    private List<Weight> sampleData = new ArrayList<Weight>();
    private List<DataChangeListener> dataChangeListeners = new ArrayList<DataChangeListener>();
    
    private void loadData() {
        Calendar cal = GregorianCalendar.getInstance();
        Weight weight;
        double currentWeight = SAMPLE_WEIGHT_INITIAL;
        for (int i = 0 ; i < SAMPLE_SIZE ; i++) {
            weight = new Weight(cal.getTime(), currentWeight);
            
            // change weight and time ready for next sample
            cal.add(Calendar.DAY_OF_YEAR, -1);
            currentWeight -= 1;
            
            sampleData.add(weight);
        }
        
    }
    
    
    public MockDaoHelper() {
        loadData();
    }
    
    @Override
    public void create(Weight weight) {
        sampleData.add(weight);
        Log.i(TAG, "MOCK saved weight {" + weight.getSampleTime().toString() 
                + "," + weight.getWeight() + "}");
    }

    @Override
    public int delete(Weight weight) {        
        int deleteCount;
        Log.i(TAG, "MOCK requested deleted weight {" + weight.getSampleTime().toString() 
                + "," + weight.getWeight() + "}");
        if (sampleData.remove(weight)) {
            deleteCount = 1;
        } else {
            deleteCount = 0;
        }
        return deleteCount;
    }

    @Override
    public RecordWeight getMaxWeight() {
        return new RecordWeight(RecordWeight.KEY_MAX_WEIGHT, new Date(), 999d);
    }

    @Override
    public RecordWeight getMinWeight() {
        return new RecordWeight(RecordWeight.KEY_MIN_WEIGHT, new Date(), 0d);
    }

    @Override
    public List<Weight> getWeightByDateAsc() {
        List<Weight> reversed = sampleData;
        Collections.reverse(reversed);
        return reversed;
    }

    /**
     * Make up 30 records of data and return them
     * @return 
     */
    @Override
    public List<Weight> getWeightByDateDesc() {
        return sampleData;
    }

    @Override
    public void onCreate(SQLiteDatabase sqld, ConnectionSource cs) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqld, ConnectionSource cs, int i, int i1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteAllData() {
         sampleData = new ArrayList<Weight>();
    }

    @Override
    public long getWeightCount() {
        return sampleData.size();
    }

    @Override
    public void registerListener(DataChangeListener listener) {
        dataChangeListeners.add(listener);
    }
    
}
