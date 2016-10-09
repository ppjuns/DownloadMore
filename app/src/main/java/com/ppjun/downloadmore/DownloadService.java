package com.ppjun.downloadmore;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;

/**
 * Package :com.ppjun.downloadmore
 * Description :
 * Author :Rc3
 * Created at :2016/10/8 17:28.
 */

public class DownloadService extends Service {
    MyBinder mBinder = new MyBinder();
    DownloadListener listener;


    public void setListener(DownloadListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }

    public interface DownloadListener {
        void onDownLoadFinish();

        void onProgress(int progress);

    }

    public class MyBinder extends Binder {
        DownloadService getService() {
            return DownloadService.this;
        }


        public void download() {
            OkHttpUtils.get()
                    .url("http://shouji.360tpcdn.com/161008/0eb28d50ead4d292ff0b379ae92a422d/com.hotbody.fitzero_49.apk")
                    .build()

                    .execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(), "xxx.apk") {
                        @Override
                        public void inProgress(float progress) {
                            //Log.i("TAG", (int) (100 * progress) + "");
                            listener.onProgress((int) (100 * progress));
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(DownloadService.this);
                            builder.setContentTitle("火辣健身");
                            Intent intent = new Intent(DownloadService.this, MainActivity.class);
                            PendingIntent pendingIntent = PendingIntent.getActivity(DownloadService.this, 0, intent, 0);
                            builder.setContentIntent(pendingIntent);
                            builder.setContentInfo((int) (100 * progress) + "%");
                            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.hljs
                            ));

                            builder.setContentText("下载中");
                            builder.setSmallIcon(R.mipmap.ic_launcher);
                            builder.setProgress(100, (int) (100 * progress), false);
                            builder.setOngoing(true);
                            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            manager.notify(1, builder.build());
                            if ((int) (100 * progress) == 100) {
                                builder.setContentText("下载完成");
                                builder.setOngoing(false);
                                manager.notify(1, builder.build());

                            }
                        }

                        @Override
                        public void onError(Request request, Exception e) {

                        }

                        @Override
                        public void onResponse(File response) {
                            Log.i("TAg", response.getAbsolutePath());
                            listener.onDownLoadFinish();
                        }
                    });

        }
    }
}
