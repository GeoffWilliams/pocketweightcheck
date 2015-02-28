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
@DatabaseTable(tableName = "weight")
public class Weight {
    
    public final static String COL_SAMPLE_TIME = "sample_time";
    public final static String COL_WEIGHT = "weight";
    
    @DatabaseField(id = true, columnName = COL_SAMPLE_TIME)
    protected Date sampleTime;
    
    @DatabaseField(canBeNull = false, columnName = COL_WEIGHT)
    protected Double weight;

    private boolean archived = false;

    private Double trend = null;
    
    public Weight() {}

    public Weight(Date date, Double weight) {
        this.sampleTime = date;
        this.weight = weight;
    }

    public Weight(Date date, Double weight, Double trend) {
        this.sampleTime = date;
        this.weight = weight;
        this.trend = trend;
    }
    
    public Date getSampleTime() {
        return sampleTime;
    }

    public void setSampleTime(Date sampleTime) {
        this.sampleTime = sampleTime;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getTrend() {
        return trend;
    }

    public void setTrend(Double trend) {
        this.trend = trend;
    }
 
    @Override
    public String toString() {
        return String.format(Locale.US, "Weight: {sampleTime:%s;weight:%.2f;trend:%.2f}",
                sampleTime, weight, trend);
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }
}
