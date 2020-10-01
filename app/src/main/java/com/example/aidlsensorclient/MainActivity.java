package com.example.aidlsensorclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aidlsensorserver.SensorAIDLInterface;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SensorAIDLInterface sensorInterface;
    TextView mTvResult,mBtnResult,mBtnBind;
    int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTvResult=(TextView)findViewById(R.id.tvResult);
        mBtnResult=(TextView)findViewById(R.id.btnResult);
        mBtnBind=(TextView)findViewById(R.id.btnBind);

        mBtnResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                  // i++;
                   mTvResult.setText(sensorInterface.getResult());
                } catch (Exception e) {
                    e.printStackTrace();
                    mTvResult.setText("Unable to connect server.");
                }
            }
        });

        mBtnBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent("com.example.aidlsensorserver.AIDL");
                bindService(convertImplicitIntentToExplicitIntent(intent,MainActivity.this),serviceConn,BIND_AUTO_CREATE);

            }
        });
    }

    private ServiceConnection serviceConn=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            sensorInterface= SensorAIDLInterface.Stub.asInterface(iBinder);
            Toast.makeText(MainActivity.this, "Connected to server!!!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            sensorInterface=null;
            Toast.makeText(MainActivity.this, "Disconnected from server!!!", Toast.LENGTH_SHORT).show();
        }
    };

    public static Intent convertImplicitIntentToExplicitIntent(Intent implicitIntent, Context context) {
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfoList = pm.queryIntentServices(implicitIntent, 0);

        if (resolveInfoList == null || resolveInfoList.size() != 1) {
            return null;
        }
        ResolveInfo serviceInfo = resolveInfoList.get(0);
        ComponentName component = new ComponentName(serviceInfo.serviceInfo.packageName, serviceInfo.serviceInfo.name);
        Intent explicitIntent = new Intent(implicitIntent);
        explicitIntent.setComponent(component);
        return explicitIntent;
    }
}