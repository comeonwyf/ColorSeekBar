package com.example.administrator.colorseekbar;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        setColorSeekBar();
    }

    private void setColorSeekBar() {
        //可以滑动，渐变色
        ColorSeekBar colorSeekBar = findViewById(R.id.colorSeekBar);
        colorSeekBar.setColorByGadient(3, true,0,new int[]{ Color.WHITE,Color.RED},new int[]{Color.WHITE,Color.BLUE},new int[]{Color.RED,Color.BLACK});
        colorSeekBar.setOnStateChangeListener(new ColorSeekBar.OnStateChangeListener() {
            @Override
            public void OnStateChangeListener(float progress) {
                Log.e("print", "进度: "+progress );
                
            }

            @Override
            public void onStopTrackingTouch(float progress) {
                Log.e("print", "进度: "+progress );
            }
        });
        
        //可以滑动，不是渐变色
        ColorSeekBar colorSeekBar1 = findViewById(R.id.colorSeekBar1);
        colorSeekBar1.setColor(4,true,100/4*3, Color.RED,Color.BLACK,Color.BLUE,Color.YELLOW);
        
    }
}
