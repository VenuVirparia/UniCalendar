package com.example.unicalendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class MonthYearPickerDialog extends DialogFragment {

    private DatePickerDialog.OnDateSetListener listener;

    public static MonthYearPickerDialog newInstance(int month, int year) {
        MonthYearPickerDialog fragment = new MonthYearPickerDialog();
        Bundle args = new Bundle();
        args.putInt("month", month);
        args.putInt("year", year);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View dialog = inflater.inflate(R.layout.dialog_month_year_picker, null);
        final NumberPicker monthPicker = dialog.findViewById(R.id.monthPicker);
        final NumberPicker yearPicker = dialog.findViewById(R.id.yearPicker);

        monthPicker.setMinValue(0);
        monthPicker.setMaxValue(11);
        monthPicker.setDisplayedValues(new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"});

        int year = getArguments() != null ? getArguments().getInt("year") : Calendar.getInstance().get(Calendar.YEAR);
        yearPicker.setMinValue(1900);
        yearPicker.setMaxValue(2100);
        yearPicker.setValue(year);

        builder.setView(dialog)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    if (listener != null) {
                        listener.onDateSet(null, yearPicker.getValue(), monthPicker.getValue(), 1);
                    }
                })
                .setNegativeButton("Cancel", null);

        return builder.create();
    }

    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }
}
