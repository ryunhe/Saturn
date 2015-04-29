package io.knows.saturn.widget;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.widget.DatePicker;

import java.util.Calendar;

import timber.log.Timber;

/**
 * Created by ryun on 15-4-29.
 */
public class DatePickerDialogWithMaxMinRange extends DatePickerDialog {
    private int maxYear = 0;
    private int maxMonth = 0;
    private int maxDay = 0;

    private int minYear = 0;
    private int minMonth = 0;
    private int minDay = 0;
    private Calendar minDateCalendar, maxDateCalendar, calendar;

    private boolean fired = false;

    public DatePickerDialogWithMaxMinRange(Context context, OnDateSetListener callBack, Calendar minCalendar, Calendar maxCalendar, Calendar currentCalendar) {
        super(context, callBack, currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH));

        minDateCalendar = (Calendar) minCalendar.clone();
        maxDateCalendar = (Calendar) maxCalendar.clone();
        calendar = (Calendar) currentCalendar.clone();

        this.minDay = minDateCalendar.get(Calendar.DAY_OF_MONTH);
        this.minMonth = minDateCalendar.get(Calendar.MONTH);
        this.minYear = minDateCalendar.get(Calendar.YEAR);
        this.maxDay = maxDateCalendar.get(Calendar.DAY_OF_MONTH);
        this.maxMonth = maxDateCalendar.get(Calendar.MONTH);
        this.maxYear = maxDateCalendar.get(Calendar.YEAR);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                fired = false;
                this.getDatePicker().setMaxDate(maxDateCalendar.getTime().getTime());
                this.getDatePicker().setMinDate(minDateCalendar.getTime().getTime());
            } else {
                fired = true;
            }
        } catch (Throwable p_e) {
            /* Have suppressed the exception
             * Time not between max and min date range
             * The exception comes when the selected date is few milliseconds more or less than min/max date in devices of android 3.0 and above
             */
            Timber.d("maxDateCalendar: " + maxDateCalendar.getTime().getTime());
            Timber.d("minDateCalendar: " + minDateCalendar.getTime().getTime());
            Timber.d("calendar: " + calendar.getTime().getTime());
            p_e.printStackTrace();
        }
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        super.onDateChanged(view, year, monthOfYear, dayOfMonth);

        if (fired) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            if (calendar.after(maxDateCalendar)) {
                fired = true;
                if (maxMonth > 11) {
                    maxMonth = 11;
                }
                view.updateDate(maxYear, maxMonth, maxDay);
                Timber.d("In onDateChanged() method: fired==>" + fired);
            } else if (calendar.before(minDateCalendar)) {
                fired = true;
                if (minMonth > 11) {
                    minMonth = 11;
                }
                view.updateDate(minYear, minMonth, minDay);
                Timber.d("In onDateChanged() method: fired==>" + fired);
            } else {
                fired = true;
                view.updateDate(year, monthOfYear, dayOfMonth);
                Timber.d("In onDateChanged() method: fired==>" + fired);
            }
        } else {
            Timber.d("In onDateChanged() method: fired==>" + fired);
        }
    }
}
