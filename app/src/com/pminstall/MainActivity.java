package com.pminstall;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import com.udroid.content.pm.ApplicationManager;
import com.udroid.content.pm.OnFinishObserver;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText filePathEd;
    ApplicationManager am;
    static String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        filePathEd = (EditText) findViewById(R.id.filePath);
        filePathEd.setText("/sdcard/Download/demo.apk");
        Button del = (Button) findViewById(R.id.delapk);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //String filePath = filePathEd.getText().toString().trim();
                    // install(filePath);
                    //am.uninstallApplication(filePathEd.getText().toString(), false);
                    /**
                     *
                     * @param packageName uninstall the app packageName
                     * @param delFlags  {@link #DELETE_KEEP_DATA}
                     * @throws IllegalArgumentException
                     * @throws IllegalAccessException
                     * @throws InvocationTargetException
                     */
                    am.deletePackage(filePathEd.getText().toString(), ApplicationManager.DELETE_KEEP_DATA);
                    Log.d("aa", "buttonClick: " );
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.d("aa", "buttonClick: " + e.toString());
                }
            }
        });

        Button install = (Button) findViewById(R.id.install);
        install.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String filePath = filePathEd.getText().toString().trim();
                try {
                    //String filePath = filePathEd.getText().toString().trim();
                    // install(filePath);
                    /**
                     *
                     * @param context
                     * @param apkFile the apk file exist path
                     * @param installFlags {@link #INSTALL_REPLACE_EXISTING}
                     * @throws IllegalArgumentException
                     * @throws IllegalAccessException
                     * @throws InvocationTargetException
                     */
                    am.installPackage(MainActivity.this, filePath, ApplicationManager.INSTALL_REPLACE_EXISTING);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.d("aa", "buttonClick: " + e.toString());
                }


            }
        });
        try {
            am = new ApplicationManager(this);
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        am.setOnInstalledPackaged(new OnFinishObserver() {
            public void packageInstalled(String packageName, int returnCode) {
                final String pkgName = packageName;
                if (returnCode == ApplicationManager.INSTALL_SUCCEEDED) {
                    Log.d("aa", "Install succeeded");
                   // doStartApplicationWithPackageName(packageName);//启动自己的时候，会有问题


                    MainActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            Toast.makeText(MainActivity.this, "Install succeeded" + pkgName, Toast.LENGTH_SHORT).show();
                            filePathEd.setText(pkgName);
                        }
                    });

                } else {
                    final int retCode =returnCode;
                    MainActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            Toast.makeText(MainActivity.this, "Install failed"+ retCode, Toast.LENGTH_SHORT).show();
                        }
                    });

                    Log.d("aa", "Install failed: " + returnCode);
                }
            }

            @Override
            public void packageDeleted(String packageName, int returnCode) {
                // TODO Auto-generated method stub
                final String pkgName = packageName;
                Log.d("aa", "delete succeeded=====================================================" + packageName);
                if (returnCode == ApplicationManager.DELETE_SUCCEEDED) {
                    Log.d("aa", "delete succeeded");
                    MainActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            Toast.makeText(MainActivity.this, "delete succeeded" + pkgName, Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    final int retCode =returnCode;
                    MainActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            Toast.makeText(MainActivity.this, "delete failed"+ retCode, Toast.LENGTH_SHORT).show();
                        }
                    });

                    Log.d("aa", "delete failed: " + returnCode);
                }
            }
        });

    }
    //打开app
    private void doStartApplicationWithPackageName(String packagename) {

        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等

        PackageInfo packageinfo = null;
        try {

            packageinfo = getPackageManager().getPackageInfo(packagename, 0);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (packageinfo == null) {

            return;
        }

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();

        if (resolveinfo != null) {
            // packagename = 参数packname

            String packageName = resolveinfo.activityInfo.packageName;

            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;

            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);

            intent.setComponent(cn);

            startActivity(intent);

        }
    }
}
