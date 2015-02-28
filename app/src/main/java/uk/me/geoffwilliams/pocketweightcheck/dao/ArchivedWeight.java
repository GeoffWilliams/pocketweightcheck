package uk.me.geoffwilliams.pocketweightcheck.dao;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;
import java.util.Locale;

/**
 * Created by geoff on 22/02/15.
 */
@DatabaseTable(tableName = "archived_weight")
public class ArchivedWeight extends Weight {
//    public final static String COL_SAMPLE_TIME = "sample_time";
//    public final static String COL_WEIGHT = "weight";
//
//    @DatabaseField(id = true, columnName = COL_SAMPLE_TIME)
//    private Date sampleTime;
//
//    @DatabaseField(canBeNull = false, columnName = COL_WEIGHT)
//    private Double weight;
//
//    private Double trend = null;
//
//    public ArchivedWeight() {}
//
//    public ArchivedWeight(Date date, Double weight) {
//        this.sampleTime = date;
//        this.weight = weight;
//    }
//
//    public ArchivedWeight(Date date, Double weight, Double trend) {
//        this.sampleTime = date;
//        this.weight = weight;
//        this.trend = trend;
//    }
//
//    public Date getSampleTime() {
//        return sampleTime;
//    }
//
//    public void setSampleTime(Date sampleTime) {
//        this.sampleTime = sampleTime;
//    }
//
//    public Double getWeight() {
//        return weight;
//    }
//
//    public void setWeight(Double weight) {
//        this.weight = weight;
//    }
//
//    public Double getTrend() {
//        return trend;
//    }
//
//    public void setTrend(Double trend) {
//        this.trend = trend;
//    }
//
//    @Override
//    public String toString() {
//        return String.format(Locale.US, "Weight: {sampleTime:%s;weight:%.2f;trend:%.2f}",
//                sampleTime, weight, trend);
//    }
//
    public ArchivedWeight() {}

    public ArchivedWeight(Weight weight) {
        setArchived(true);
        setSampleTime(weight.getSampleTime());
        setWeight(weight.getWeight());
    }

    public Weight toWeightObject() {
        Weight w = new Weight(sampleTime, weight);
        w.setArchived(true);
        return w;
    }

}
