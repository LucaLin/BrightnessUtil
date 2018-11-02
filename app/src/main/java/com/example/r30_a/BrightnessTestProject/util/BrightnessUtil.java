package com.example.r30_a.BrightnessTestProject.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

/**
 * Created by lucalin on 2018/10/3.
 */

public class BrightnessUtil {

    private int originalBrightness;
    private boolean isAutoForOriginalBrightness = false;
    private boolean isMaxBrightness = false;
    private Context context;

    public BrightnessUtil(Context context){this.context = context;}

    //調整畫面亮度至最亮
    public void toMaxBrightness(){
        //API在23以上要先詢問可否自動調整
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(Settings.System.canWrite(context)){
                setMaxBrightness();
            }else {
                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                                              .setTitle("提示")
                                              .setMessage("請先開啟系統權限")
                                              .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                  @Override
                                                  public void onClick(DialogInterface dialog, int which) {
                                                      Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);//對手機設置進行修改
                                                      intent.setData(Uri.parse("package:"+context.getPackageName()));
                                                      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                      context.startActivity(intent);
                                                  }
                                              }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setCancelable(false);

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        }else {
            setMaxBrightness();
        }
    }

    private void setMaxBrightness() {

        //step1: 判斷亮度是否為自動, 是的話轉成手動調整模式
        try {
            isAutoForOriginalBrightness = Settings.System.getInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE) ==
                    Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;

            if(isAutoForOriginalBrightness){
                Settings.System.putInt(context.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            }

            //step2: 取得螢幕亮度
            originalBrightness = Settings.System.getInt(context.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS);

            //step3: 設定亮度範圍, 0是最暗，255最亮
            int brightness = 255;

            //step4: 設定螢幕亮度
            Settings.System.putInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS,brightness);
            isMaxBrightness = true;

        }catch (Settings.SettingNotFoundException e){
            e.printStackTrace();
        }
    }

    //回到原來裝置的亮度
    public void toOriginalBrightness(){
        //如果沒有調整過最大亮度，不做任何事
        if(!isMaxBrightness){
            return;
        }
        try {
            if(isAutoForOriginalBrightness){
                Settings.System.putInt(context.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
            }

            int brightness = originalBrightness;//置入原先取得的螢幕亮度

            Settings.System.putInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS,brightness);
            isMaxBrightness = false;
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
