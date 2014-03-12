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
import java.util.GregorianCalendar;
import java.util.List;

/**
 *
 * @author geoff
 */
public class MockDaoHelper implements DaoHelper {
    public final static int SAMPLE_SIZE = 30;
    public final static double SAMPLE_WEIGHT_INITIAL = 111.1d;
    private final static String TAG = "pocketweightcheck.MockDaoHelper";
    private List<Weight> sampleData = new ArrayList<Weight>();
    
    public MockDaoHelper() {
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public RecordWeight getMinWeight() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Weight> getWeightByDateAsc() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    
}
