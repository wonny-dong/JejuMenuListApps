package com.dongwon.menulist.task;

import android.content.Context;
import com.dongwon.menulist.R;
import com.dongwon.menulist.type.DayMenuJson;
import com.dongwon.menulist.type.FileInfoJson;
import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class HttpRequest {
    private Context context;
    private OkHttpClient okHttpClient;

    public HttpRequest(Context context){
        this.context = context;
        okHttpClient = new OkHttpClient();
    }

    public FileInfoJson getFileInfo() throws IOException, IllegalAccessException {
        return getJsonObject(context.getString(R.string.update_url) + context.getString(R.string.info_file), FileInfoJson.class);
    }

    public DayMenuJson[] getDayMenu(String fileName) throws IOException, IllegalAccessException {
        return getJsonObject(context.getString(R.string.update_url) + "menuList/" + fileName, DayMenuJson[].class);
    }

    public void getNewApk(File savePath, String apkName) throws IOException, IllegalAccessException {
        Request request = new Request.Builder()
                .url(context.getString(R.string.update_url) + "apk/" + apkName)
                .build();
        Response response = okHttpClient.newCall(request).execute();

        if(response.isSuccessful() == false){
            throw new IllegalAccessException("http error code : " + response.code());
        }

        if(savePath.exists()) {
            savePath.deleteOnExit();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(savePath);
        fileOutputStream.write(response.body().bytes());
        fileOutputStream.close();
    }

    private <T> T getJsonObject(String url, Class<T> returnObject) throws IOException, IllegalAccessException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        if(response.isSuccessful()){
            return new Gson().fromJson(response.body().string(), returnObject);
        }
        throw new IllegalAccessException("http error code : " + response.code());
    }
}
