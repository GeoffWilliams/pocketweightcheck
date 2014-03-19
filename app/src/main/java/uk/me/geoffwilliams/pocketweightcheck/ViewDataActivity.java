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
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.util.List;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import uk.me.geoffwilliams.pocketweightcheck.dao.DaoHelper;
import uk.me.geoffwilliams.pocketweightcheck.dao.DaoHelperImpl;
import uk.me.geoffwilliams.pocketweightcheck.dao.Weight;
import android.view.View;
import android.util.Log;
import android.view.Gravity;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.res.StringRes;
import uk.me.geoffwilliams.pocketweightcheck.Settings;

/**
 *
 * @author geoff
 */
@EActivity(R.layout.activity_view_data)
public class ViewDataActivity extends FragmentActivity implements DataChangeListener {

    @ViewById(R.id.weightTableLayout)
    TableLayout layout;

    /**
     * Suggest a class to implement the interface. During testing this still
     * happens but we replace the object with a mock instance before starting
     * the tests
     */
    @Bean(DaoHelperImpl.class)
    DaoHelper daoHelper;

    @StringRes
    String msgNoData;

    @StringRes
    String dateLabel;

    @StringRes
    String weightLabel;

    private static final String TAG = "pocketweightcheck.ViewDataActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //To change body of generated methods, choose Tools | Templates.

        Log.d(TAG, "inside onCreate()");
    }
    
    private TableRow addRow() {
        TableRow row = new TableRow(this);
        layout.addView(row);
        return row;
    }

    private void tableHeaders() {
        TableRow row = addRow();

        // date
        TextView tv = new TextView(this);
        tv.setText(dateLabel);
        tv.setTypeface(null, Typeface.BOLD);
        row.addView(tv);

        // weight
        tv = new TextView(this);
        tv.setText(weightLabel);
        tv.setTypeface(null, Typeface.BOLD);
        row.addView(tv);
    }

    @AfterViews
    @Override
    public void onDataChanged() {
        daoHelper.registerListener(this);
        if (Settings.isLoadData()) {
            Log.d(TAG, "loading data...");

            // delete any existing display
            layout.removeAllViews();

            List<Weight> weights = daoHelper.getWeightByDateDesc();

            if (weights.isEmpty()) {
                TableRow row = addRow();
                TextView tv = new TextView(this);
                tv.setText(msgNoData);
                row.addView(tv);
            } else {
                //  headers
                tableHeaders();

                // data
                TextView dateTextView;
                TextView weightTextView;
                ImageButton deleteButton;

                for (final Weight weight : weights) {
                    final TableRow row = addRow();

                    row.setLayoutParams(new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
                    row.setGravity(Gravity.CENTER_VERTICAL);
                    
                    dateTextView = new TextView(this);
                    weightTextView = new TextView(this);

                    dateTextView.setText(
                            TextFormatter.formatDate(this, weight.getSampleTime()));
                    dateTextView.setPadding(2, 2, 2, 2);

                    weightTextView.setText(
                            TextFormatter.formatDouble(weight.getWeight()));
                    weightTextView.setPadding(2, 2, 2, 2);

                    // delete row icon
                    deleteButton = new ImageButton(this);
                    deleteButton.setImageResource(R.drawable.delete);
                    deleteButton.setBackgroundColor(Color.BLACK);
                    deleteButton.setOnClickListener(
                            new View.OnClickListener() {
                                private final Weight targetWeight = weight;
                                private final View targetRow = row;

                                @Override
                                public void onClick(View v) {
                                    daoHelper.delete(targetWeight);
                                    layout.removeView(targetRow);
                                }
                            });

                    // attach the row to gui
                    row.addView(dateTextView);
                    row.addView(weightTextView);
                    row.addView(deleteButton);

                    Log.d(TAG, "added row...");
                }
            }
            Log.d(TAG, "...done loading data!");
        }
    }

}
