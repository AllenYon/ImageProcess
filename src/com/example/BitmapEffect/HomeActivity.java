package com.example.BitmapEffect;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ToggleButton;
import net.micode.fileexplorer.FileExplorerTabActivity;

/**
 * Created with IntelliJ IDEA.
 * User: Link
 * Date: 13-9-17
 * Time: PM2:24
 * To change this template use File | Settings | File Templates.
 */
public class HomeActivity extends Activity implements View.OnClickListener {

//    ViewGroup mLayoutImageExplore,
//            mLayoutFileExplore,
//            mLayoutInternetExplore;
//    ImageButton mIBtnSetting;

    Button mBtnImage;
    Button mBtnFile;
    Button mBtnInternet;
    Button mBtnSetting;

    ToggleButton mTBtnWifi;
    ToggleButton mTBtnSDCard;
    ToggleButton mTBtnUSB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_home);
        findViewById(R.id.btn_image).setOnClickListener(this);
        findViewById(R.id.btn_file).setOnClickListener(this);
        findViewById(R.id.btn_internet).setOnClickListener(this);
        findViewById(R.id.btn_setting).setOnClickListener(this);

        mTBtnSDCard = (ToggleButton) findViewById(R.id.tb_sd);
        mTBtnUSB = (ToggleButton) findViewById(R.id.tb_usb);
        mTBtnWifi = (ToggleButton) findViewById(R.id.tb_wifi);


        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            mTBtnWifi.setChecked(true);
        } else {
            mTBtnWifi.setVisibility(View.INVISIBLE);
        }


        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            // sd card 可用
            mTBtnUSB.setChecked(true);
            mTBtnSDCard.setChecked(true);
        } else { // 当前不可用
            mTBtnUSB.setVisibility(View.INVISIBLE);
            mTBtnSDCard.setVisibility(View.INVISIBLE);
        }


    }

    @Override
    public void onClick(View v) {
        //ToDo
        switch (v.getId()) {
            case R.id.btn_image:
                MainActivity.show(this, MainActivity.Type.RealTime);
                break;
            case R.id.btn_internet: {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse("http://www.baidu.com");
                intent.setData(content_url);
//                intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
                startActivity(intent);
            }
            break;
            case R.id.btn_file: {
                Intent intent = new Intent(this, FileExplorerTabActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.btn_setting:
                SettingActivity.show(this);
                break;
        }
    }
}
