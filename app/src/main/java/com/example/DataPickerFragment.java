package com.example;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import java.util.Calendar;

public class DataPickerFragment extends DialogFragment {

    public interface OnDateSetListener {
        void onDateSet(int year, int month, int day);
    }

    private OnDateSetListener listener;

    public static DataPickerFragment newInstance(OnDateSetListener listener) {
        DataPickerFragment fragment = new DataPickerFragment();
        fragment.setListener(listener);
        return fragment;
    }

    private void setListener(OnDateSetListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(requireActivity(), (DatePicker view, int year1, int month1, int day1) -> {
            if (listener != null) {
                listener.onDateSet(year1, month1, day1);
            }
        }, year, month, day);
    }
}