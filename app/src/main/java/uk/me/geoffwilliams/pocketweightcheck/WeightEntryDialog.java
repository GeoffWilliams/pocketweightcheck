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

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Date;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;

import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;
import uk.me.geoffwilliams.pocketweightcheck.dao.DaoHelper;
import uk.me.geoffwilliams.pocketweightcheck.dao.DaoHelperImpl;
import uk.me.geoffwilliams.pocketweightcheck.dao.Weight;

/**
 *
 * @author geoff
 */
@EFragment(R.layout.weight_entry)
public class WeightEntryDialog extends DialogFragment {
    
    private final static String TAG = "pwc.WeightEntryDialog";

    private double enteredWeight = 0;

    @ViewById(R.id.dateTakenMessage)
    TextView dateTakenMessage;

    @ViewById(R.id.timeTakenMessage)
    TextView timeTakenMessage;

    @ViewById(R.id.weightEntryEditText)
    EditText weightEntryEditText;

//    @ViewById(R.id.okButton)
//    Button okButton;
    @StringRes
    String msgSaved;

    @StringRes
    String msgTooLight;

    @StringRes
    String msgTooHeavy;

    @StringRes
    String msgInvalid;
    
    @StringRes
    String msgError;
    
    @StringRes
    String weightEntryDialogTitle;


    @Bean
    DateUtils dateUtils;
    
    /**
     * Suggest a class to implement the interface.  During testing this still
     * happens but we replace the object with a mock instance before starting 
     * the tests
     */
    @Bean(DaoHelperImpl.class)
    DaoHelper daoHelper;
        
    private void save() {
        Log.d(TAG, "saving weight...");
        Weight weight = new Weight(dateUtils.getDate(), enteredWeight);
        daoHelper.create(weight);
        Log.d(TAG, "...weight saved!");
    }
    
    @Click void cancelButton() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();       // dismiss();
    }

    @Click
    public void okButton() {
        CharSequence message;
        try {
            enteredWeight = Float.parseFloat(weightEntryEditText.getText().toString());

            if (enteredWeight < Settings.getMinAllowedWeight()) {
                message = msgTooLight;
            } else if (enteredWeight > Settings.getMaxAllowedWeight()) {
                message = msgTooHeavy;
            } else {
                try {
                    save();
                    message = msgSaved;
                    //dismiss();
                    getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
                } catch (RuntimeException e) {
                    Log.e(TAG,"error saving weight",e);
                    message = msgError;
                }
            }
        } catch (NumberFormatException e) {
            message = msgInvalid;
        }
        Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Click
    public void dateTakenMessage(View v) {
        Log.d(TAG, "showTimePickerDialog");
        DialogFragment df = new DatePickerFragment_();
        df.show(getFragmentManager(), "timePicker");
    }

    @Click
    public void timeTakenMessage(View v) {
        Log.d(TAG, "showDatePickerDialog");
        DialogFragment df = new TimePickerFragment_();
        df.show(getFragmentManager(), "datePicker");
    }

    @AfterViews
    void updateEntryDateMessage() {
        Log.d(TAG, "inside updateEntryDateMessage()");
        Log.d(TAG, dateTakenMessage.toString());
        Log.d(TAG, dateUtils.toString());
        Log.d(TAG, dateUtils.getDate().toString());
        dateTakenMessage.setText(
                DateFormat.getDateFormat(getActivity())
                .format(dateUtils.getDate()));
        timeTakenMessage.setText(
                DateFormat.getTimeFormat(getActivity())
                .format(dateUtils.getDate()));
    }
    
    @Override   
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle(weightEntryDialogTitle);
        
        // reset the current date
        dateUtils.setDate(new Date());
        return dialog; 
    }
    
    /**
     * Test support
     * @return 
     */
    public DateUtils getDateUtils() {
        return dateUtils;
    }
}
