package com.dongwon.menulist.service;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;
import com.dongwon.menulist.R;
import com.dongwon.menulist.database.Values;
import com.dongwon.menulist.task.HttpRequest;
import com.dongwon.menulist.type.FileInfoJson;
import com.dongwon.menulist.util.HashHelper;
import com.dongwon.menulist.util.TrackHelper;

import java.io.File;
import java.io.FileInputStream;

public class ApkUpdateService extends Service{
    private ApkUpdateTask task;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(task == null || task.getStatus() == AsyncTask.Status.FINISHED){
            task = new ApkUpdateTask();
            task.execute();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class ApkUpdateTask extends AsyncTask<Void,Void,Boolean>{
        private File savePath;
        public ApkUpdateTask() {
            savePath = new File(Environment.getExternalStorageDirectory(), "/download/newMenuList.apk");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try{
                HttpRequest request = new HttpRequest(getBaseContext());
                FileInfoJson fileInfoJson = request.getFileInfo();
                boolean wantUpdateApk = HashHelper.md5Hex(new FileInputStream(getPackageCodePath())).equals(fileInfoJson.getApkHash()) == false;
                if(wantUpdateApk){
                    publishProgress();
                    request.getNewApk(savePath, fileInfoJson.getApkFileName());
                    try {
                        TrackHelper.sendEvent("Product", "Update", getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
                    } catch (PackageManager.NameNotFoundException e) {
                        TrackHelper.sendException(e);
                    }
                    return true;
                }else{
                    Values.getInstance().put(Values.BoolType.isNewApkUpdate, false);
                }
            }catch(Exception e){
                TrackHelper.sendException(e);
            }
            return false;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            Toast.makeText(getBaseContext(), R.string.setting_product_apk_download, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean){
                Toast.makeText(getBaseContext(), R.string.setting_product_apk_install, Toast.LENGTH_SHORT).show();
                installApk(savePath);
            }else{
                Toast.makeText(getBaseContext(), R.string.setting_product_not_found_update, Toast.LENGTH_SHORT).show();
            }
            stopSelf();
        }

        private void installApk(File path){
            Uri uri = Uri.fromFile(path);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            startActivity(intent);
        }
    }
}
