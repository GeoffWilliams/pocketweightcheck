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

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author geoff
 */
@DatabaseTable(tableName = "record_weight")
public class RecordWeight {
    public final static String COL_KEY = "key";
    public final static String COL_VALUE = "value";
    public final static String COL_SAMPLE_TIME = "sample_time";
    
    public final static String KEY_MAX_WEIGHT = "max_weight";
    public final static String KEY_MIN_WEIGHT = "min_weight";
    
    @DatabaseField(id = true, columnName = COL_KEY)
    private String key;

    @DatabaseField(canBeNull = false, columnName = COL_VALUE)
    private Double value;
    
    @DatabaseField(canBeNull = false, columnName = COL_SAMPLE_TIME)
    private Date sampleTime;

    public RecordWeight() {}
    
    public RecordWeight(String key, Date sampleTime, Double value) {
        this.key = key;
        this.sampleTime = sampleTime;
        this.value = value;
    }
    
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Date getSampleTime() {
        return sampleTime;
    }

    public void setSampleTime(Date sampleTime) {
        this.sampleTime = sampleTime;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "RecordWeight:  {key: %s; value: %.2f sampleTime: %s",
                key, value, sampleTime);
    }
    
}

