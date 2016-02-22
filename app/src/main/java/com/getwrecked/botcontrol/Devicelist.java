package com.getwrecked.botcontrol;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.content.Intent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

public class Devicelist extends Activity {

    public static String EXTRA_ADDRESS;
    ListView deviceList;
    Button choosepaired;
    TextView textView;
    Toast mLastToast;

    private BluetoothAdapter myBluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devicelist);

        deviceList = (ListView)findViewById(R.id.listView);
        choosepaired = (Button)findViewById(R.id.button);
        textView = (TextView)findViewById(R.id.textview1);

        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        if (myBluetooth == null) {
            showToast(getString(R.string.error1) , true);
            //good job.
            finish();
        }
        else {
            pairedDeviceList();
        }

        choosepaired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pairedDeviceList(); //boo yaah
            }
        });
    }

    private void pairedDeviceList() {
        Set<BluetoothDevice> pairedDevices = myBluetooth.getBondedDevices();
        List<String> list = new ArrayList<>();

        if (!myBluetooth.isEnabled()) {
            //Ask user to turn bluetooth on.
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon, 1);
        }
        else {
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice bt : pairedDevices)
                    list.add(bt.getName() + "\n" + bt.getAddress()); //list out name and address.
            } else {
                showToast(getString(R.string.error2), false);
            }
            final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
            deviceList.setAdapter(adapter);
            deviceList.setOnItemClickListener(myClickListener); //new method
        }

    }

    private AdapterView.OnItemClickListener myClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick (AdapterView av, View v, int arg2, long arg3) {
            // Get the device MAC address, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
            // Make an intent to start next activity.
            Intent changetime = new Intent(Devicelist.this, BaseActivity.class);
            //Change the activity.
            changetime.putExtra(Devicelist.EXTRA_ADDRESS, address); //this will be received at BaseActivity Activity
            startActivity(changetime);
        }
    };

    //get rid of stale toast before making fresh
    void showToast(String text, boolean longDuration) {
        if (mLastToast != null) mLastToast.cancel();
        mLastToast = Toast.makeText(Devicelist.this, text, longDuration ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        mLastToast.setGravity(Gravity.BOTTOM, 0, 0);
        mLastToast.show();
    }
}