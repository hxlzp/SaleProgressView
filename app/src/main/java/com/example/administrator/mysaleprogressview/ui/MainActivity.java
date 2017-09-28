package com.example.administrator.mysaleprogressview.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;

import com.example.administrator.mysaleprogressview.R;
import com.example.administrator.mysaleprogressview.view.MySaleProgressView;

public class MainActivity extends AppCompatActivity {
    private MySaleProgressView mySaleProgressView;
    private SeekBar seekBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setListener();
    }
    private void setListener() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mySaleProgressView.setTotalAndCurrentCount(seekBar.getMax(),progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initView() {
        mySaleProgressView = (MySaleProgressView) findViewById(R.id.sale_progress);
        seekBar = (SeekBar) findViewById(R.id.seek_bar);
    }
}

