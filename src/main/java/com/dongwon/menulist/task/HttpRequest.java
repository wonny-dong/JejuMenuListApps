package com.dongwon.menulist.task;

import android.content.Context;
import com.dongwon.menulist.R;
import com.dongwon.menulist.type.DayMenuJson;
import com.dongwon.menulist.type.FileInfoJson;
import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Dongwon on 2015-04-26.
 */
public class HttpRequest {
    private Context context;
    private OkHttpClient okHttpClient;

    public HttpRequest(Context context){
        this.context = context;
        okHttpClient = new OkHttpClient();
    }

    public FileInfoJson getFileInfo() throws IOException {
        Request request = new Request.Builder()
                .url(context.getString(R.string.update_url) + context.getString(R.string.info_file))
                .build();
        Response response = okHttpClient.newCall(request).execute();
        return new Gson().fromJson(response.body().string(), FileInfoJson.class);
    }

    public DayMenuJson[] getDayMenu(String fileName) throws IOException {
        Request request = new Request.Builder()
                .url(context.getString(R.string.update_url) + fileName)
                .build();
        return new Gson().fromJson(okHttpClient.newCall(request).execute().body().string(), DayMenuJson[].class);
    }

    public void getNewApk(File savePath, String urlPath) throws IOException {
        Request request = new Request.Builder()
                .url(context.getString(R.string.update_url) +urlPath)
                .build();
        Response response = okHttpClient.newCall(request).execute();

        if(savePath.exists()) savePath.deleteOnExit();
        FileOutputStream fileOutputStream = new FileOutputStream(savePath);
        fileOutputStream.write(response.body().bytes());
        fileOutputStream.close();
    }
}
