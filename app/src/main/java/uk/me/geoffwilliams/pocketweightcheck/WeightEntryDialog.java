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

import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;

import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

/**
 *
 * @author geoff
 */
@EFragment(R.layout.weight_entry)
public class WeightEntryDialog extends DialogFragment {
    
    private final static String TAG = "pocketweightcheck.WeightEntryDialog";

    public final static int MAX_ALLOWED_WEIGHT = 140;
    public final static int MIN_ALLOWED_WEIGHT = 40;

    private float enteredWeight = 0;

    @ViewById(R.id.dateTakenMessage)
    TextView dateTakenMessage;

    @ViewById(R.id.timeTakenMessage)
    TextView timeTakenMessage;

    @ViewById(R.id.weightEntryEditText)
    EditText weightEntryEditText;

//    @ViewById(R.id.okButton)
//    Button okButton;
    @StringRes(R.string.msg_saved)
    String msgSaved;

    @StringRes(R.string.msg_too_light)
    String msgTooLight;

    @StringRes(R.string.msg_too_heavy)
    String msgTooHeavy;

    @StringRes(R.string.msg_invalid)
    String msgInvalid;


    @Bean
    DateUtils dateUtils;
//
//    private Provider<Context> contextProvider;
//
//    @Inject
//    public WeightEntryDialog(Provider<Context> contextProvider) {
//        this.contextProvider = contextProvider;
//    }
    
    @Click void cancelButton() {
        dismiss();
    }

    @Click
    public void okButton() {
        CharSequence message;
        try {
            enteredWeight = Float.parseFloat(weightEntryEditText.getText().toString());

            if (enteredWeight < MIN_ALLOWED_WEIGHT) {
                message = msgTooLight;
            } else if (enteredWeight > MAX_ALLOWED_WEIGHT) {
                message = msgTooHeavy;
            } else {
                message = msgSaved;
            }
        } catch (NumberFormatException e) {
            message = msgInvalid;
        }
        Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
        toast.show();
    }

//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        updateEntryDateMessage();
//    }

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

}