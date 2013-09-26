package com.example.BitmapEffect;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import net.micode.fileexplorer.FileExplorerTabActivity;

/**
 * Created with IntelliJ IDEA.
 * User: Link
 * Date: 13-9-17
 * Time: PM2:24
 * To change this template use File | Settings | File Templates.
 */
public class HomeActivity extends Activity implements View.OnClickListener {

    ViewGroup mLayoutImageExplore,
            mLayoutFileExplore,
            mLayoutInternetExplore;
    ImageButton mIBtnSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_home);
        mLayoutImageExplore = (ViewGroup) findViewById(R.id.layout_image_explore);
        mLayoutFileExplore = (ViewGroup) findViewById(R.id.layout_file_explore);
        mLayoutInternetExplore = (ViewGroup) findViewById(R.id.layout_internet_explore);
        mIBtnSetting = (ImageButton) findViewById(R.id.ibtn_setting);

        mLayoutImageExplore.setOnClickListener(this);
        mLayoutInternetExplore.setOnClickListener(this);
        mLayoutFileExplore.setOnClickListener(this);

        mIBtnSetting.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //ToDo
        switch (v.getId()) {
            case R.id.layout_image_explore:
                MainActivity.show(this);
                break;
            case R.id.layout_internet_explore: {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse("http://www.baidu.com");
                intent.setData(content_url);
//                intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
                startActivity(intent);
            }
            break;
            case R.id.layout_file_explore: {
                Intent intent = new Intent(this, FileExplorerTabActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.ibtn_setting:
                SettingActivity.show(this);
                break;
        }
    }
}
