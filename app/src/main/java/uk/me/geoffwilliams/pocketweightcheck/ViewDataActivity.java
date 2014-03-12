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

import android.graphics.Color;
import android.os.Bundle;
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
import uk.me.geoffwilliams.pocketweightcheck.Settings;


/**
 *
 * @author geoff
 */
@EActivity(R.layout.activity_view_data)
public class ViewDataActivity extends FragmentActivity {
    
    @ViewById(R.id.weightTableLayout)
    TableLayout layout;
    
     /**
     * Suggest a class to implement the interface.  During testing this still
     * happens but we replace the object with a mock instance before starting 
     * the tests
     */
    @Bean(DaoHelperImpl.class)
    DaoHelper daoHelper;
    
    private java.text.DateFormat df;
    private static final String TAG = "pocketweightcheck.ViewDataActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //To change body of generated methods, choose Tools | Templates.
        
        Log.d(TAG, "inside onCreate()");
        // locale formatted date
        df = DateFormat.getDateFormat(this);
        if (Settings.isProduction()) {
            loadData();
        }
    }
    
    /*package*/ void loadData() {
        
        TextView dateTextView;
        TextView weightTextView;
        ImageButton deleteButton;
        List<Weight> weights = daoHelper.getWeightByDateDesc();

        
        for (final Weight weight : weights) {
            final TableRow row = new TableRow(this);
            
            // FIXME deprecated...
            row.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
            
            dateTextView = new TextView(this);
            weightTextView = new TextView(this);
            String datePretty = df.format(weight.getSampleTime());
            
            dateTextView.setText(datePretty);
            dateTextView.setPadding(2,2,2,2);
                          
            weightTextView.setText(String.valueOf(weight.getWeight()));
            weightTextView.setPadding(2,2,2,2);
            
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
            layout.addView(row);
        }
    }

}
