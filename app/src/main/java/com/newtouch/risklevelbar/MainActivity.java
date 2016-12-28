package com.newtouch.risklevelbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private RiskLevelBar mRiskLevelBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRiskLevelBar = (RiskLevelBar) findViewById(R.id.rl);
        mRiskLevelBar.setOnCheckedListener(new RiskLevelBar.OnCheckedListener() {
            @Override
            public void onCheckedChange(int i) {
                if (i != -1) {
                    Toast.makeText(MainActivity.this, "选中了" + (i + 1), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 跳转到5级
     * @param view
     */
    public void jump(View view) {
        mRiskLevelBar.setCurrentSelected(5);
    }

    /**
     * 获取当前选中的等级
     * @param view
     */
    public void getCurrent(View view){
        Toast.makeText(MainActivity.this, mRiskLevelBar.getCurrentSelected()+"级", Toast.LENGTH_SHORT).show();
    }
}
