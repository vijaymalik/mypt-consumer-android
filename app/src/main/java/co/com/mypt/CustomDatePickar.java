package co.com.mypt;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by ubuntu on 30/6/16.
 */
public class CustomDatePickar extends DatePicker implements
        DatePicker.OnDateChangedListener {
    Calendar setecteddateCal = Calendar.getInstance();
    private Calendar minCalender;
    private Calendar maxCalender;


    public CustomDatePickar(Context context) {
        super(context);
        setData();
    }

    public CustomDatePickar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setData();
    }

    public CustomDatePickar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setData();
    }

    public Calendar getselectedDate() {
        return setecteddateCal;
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
        // TODO Auto-generated method stub
        setecteddateCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        if (setecteddateCal.get(Calendar.DAY_OF_MONTH) == dayOfMonth) {

        } else {
//            if (timepicker != null) {
//                timepicker.setTominTime();
//            }
        }
    }

    public void setminandmaxDate(int min, int max) {
        minCalender = Calendar.getInstance();
        minCalender.add(Calendar.MINUTE, min);
        maxCalender = Calendar.getInstance();
        maxCalender.add(Calendar.MINUTE, max);

        try {
            setMaxDate(maxCalender.getTimeInMillis());
            setMinDate(minCalender.getTimeInMillis());
            //c.setTime(date);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public void setData() {

        minCalender = Calendar.getInstance();
        minCalender.add(Calendar.MINUTE, 30);

        maxCalender = Calendar.getInstance();
        maxCalender.add(Calendar.HOUR_OF_DAY, 24);

        init(minCalender.get(Calendar.YEAR), minCalender.get(Calendar.MONTH),
                minCalender.get(Calendar.DAY_OF_MONTH), this);
        try {


            setMaxDate(maxCalender.getTimeInMillis());
            setMinDate(minCalender.getTimeInMillis());
            //c.setTime(date);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
