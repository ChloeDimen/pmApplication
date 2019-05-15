package com.pminstall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BCRUpgradeApk extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        //一个新版本的应用安装到设备，替换之前已经存在的版本
        if (intent.getAction().equals("android.intent.action.PACKAGE_REPLACED")){
            Toast.makeText(context,"升级了一个安装包",Toast.LENGTH_SHORT).show();
            Intent intent2 = new Intent(context, MainActivity.class);
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent2);
        }
    }
}
