package com.ddaysimple.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {

    private TextView titleView;
    private TextView ddayView;
    private TextView dateView;

    private SharedPreferences prefs;
    private Calendar targetDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences("dday", MODE_PRIVATE);

        targetDate = Calendar.getInstance();
        targetDate.set(
                prefs.getInt("year", 2026),
                prefs.getInt("month", 10),
                prefs.getInt("day", 12)
        );

        createScreen();
        updateScreen();
    }

    private void createScreen() {

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(30, 30, 30, 30);
        layout.setBackgroundColor(0xFFFFFFFF);

        titleView = new TextView(this);
        titleView.setTextSize(30);
        titleView.setTextColor(0xFF000000);
        titleView.setGravity(Gravity.CENTER);
        titleView.setPadding(10, 20, 10, 40);

        ddayView = new TextView(this);
        ddayView.setTextSize(72);
        ddayView.setTextColor(0xFF000000);
        ddayView.setGravity(Gravity.CENTER);
        ddayView.setPadding(10, 30, 10, 30);

        dateView = new TextView(this);
        dateView.setTextSize(24);
        dateView.setTextColor(0xFF000000);
        dateView.setGravity(Gravity.CENTER);
        dateView.setPadding(10, 30, 10, 30);

        TextView setting = new TextView(this);
        setting.setText("날짜 설정");
        setting.setTextSize(22);
        setting.setTextColor(0xFF000000);
        setting.setGravity(Gravity.CENTER);
        setting.setPadding(40, 30, 40, 30);

        layout.addView(titleView);
        layout.addView(ddayView);
        layout.addView(dateView);
        layout.addView(setting);

        setContentView(layout);

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate();
            }
        });

        titleView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                changeTitle();
                return true;
            }
        });
    }

    private void selectDate() {

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(
                            DatePicker view,
                            int year,
                            int month,
                            int day) {

                        targetDate.set(year, month, day);

                        prefs.edit()
                                .putInt("year", year)
                                .putInt("month", month)
                                .putInt("day", day)
                                .apply();

                        updateScreen();
                    }
                },
                targetDate.get(Calendar.YEAR),
                targetDate.get(Calendar.MONTH),
                targetDate.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    private void changeTitle() {

        final EditText input = new EditText(this);
        input.setText(prefs.getString("title", "D-Day"));

        new AlertDialog.Builder(this)
                .setTitle("제목 변경")
                .setView(input)
                .setPositiveButton("저장",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialog,
                                    int which) {

                                String title =
                                        input.getText().toString().trim();

                                if (title.length() == 0) {
                                    title = "D-Day";
                                }

                                prefs.edit()
                                        .putString("title", title)
                                        .apply();

                                updateScreen();
                            }
                        })
                .setNegativeButton("취소", null)
                .show();
    }

    private void updateScreen() {

        titleView.setText(
                prefs.getString("title", "D-Day")
        );

        Calendar today = Calendar.getInstance();

        resetTime(today);
        resetTime(targetDate);

        long difference =
                targetDate.getTimeInMillis()
                        - today.getTimeInMillis();

        long days =
                TimeUnit.MILLISECONDS.toDays(difference);

        if (days > 0) {
            ddayView.setText("D-" + days);
        } else if (days == 0) {
            ddayView.setText("D-DAY");
        } else {
            ddayView.setText("D+" + Math.abs(days));
        }

        SimpleDateFormat format =
                new SimpleDateFormat(
                        "yyyy. MM. dd.",
                        Locale.KOREA
                );

        dateView.setText(
                format.format(targetDate.getTime())
        );
    }

    private void resetTime(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ddayView != null) {
            updateScreen();
        }
    }
}
