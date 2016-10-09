package com.ppjun.downloadmore;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class MainActivity extends Activity implements View.OnClickListener {
    private ImageView mIcon;
    private ProgressBar mProgressBar;
    private Button mDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initListener();
    }

    private void initListener() {
        mDownload.setOnClickListener(this);
    }

    private void initViews() {
        mIcon = (ImageView) findViewById(R.id.app_icon);
        mProgressBar = (ProgressBar) findViewById(R.id.app_progress);
        mDownload = (Button) findViewById(R.id.app_download);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.app_download:
                bindService(new Intent(MainActivity.this,DownloadService.class),conn,BIND_AUTO_CREATE);

                break;
        }
    }

    private ServiceConnection conn=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DownloadService.MyBinder binder= (DownloadService.MyBinder) service;
            binder.download();
            DownloadService  mService=binder.getService();
            mService.setListener(new DownloadService.DownloadListener(){

                @Override
                public void onDownLoadFinish() {
                    unbindService(conn);
                }

                @Override
                public void onProgress(int progress) {
                    mProgressBar.setProgress(progress);
                    mDownload.setText(progress+"%");
                    if(progress==100){
                        mDownload.setText("打开");
                    }
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

}
