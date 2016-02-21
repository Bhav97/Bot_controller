package com.getwrecked.botcontrol;

/*
 * needs fixes.
 * todo: LEARN JAVA.
 */

import android.os.Bundle;
import android.app.Activity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;


public class BaseActivity extends Activity {
    RelativeLayout layout_joystick;
    TextView textView1, textView2, textView3, textView4, textView5, Console;
    JoyStickClass js;
    Toast mLastToast;
    Button button2;
    Button button3;
    SeekBar speed;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    private BluetoothSocket btSocket = null;
    private BufferedReader btBufferedReader = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static final String TAG = null;



    private class ConnectBT extends AsyncTask<Void, Void, Void> {

        //receive the address from previous activity
        Intent newint = getIntent();
        String address = newint.getStringExtra(Devicelist.EXTRA_ADDRESS);
        InputStream aStream = null;
        InputStreamReader aReader = null;


        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(BaseActivity.this, "Connecting...", "May the force be with you...");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                showToast(getString(R.string.error3), true);
                finish();
            }
            else
            {
                showToast(getString(R.string.connected), true);
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }

    private void Disconnect()
    {
        if (btSocket!=null) //If the btSocket is busy
        {
            try
            {
                btSocket.close(); //close connection
            } catch (IOException e) {
                showToast(getString(R.string.error6), false);
            }
        }
        finish(); //return to the first layout
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        new ConnectBT().execute();

    }

        //// TODO: 2/14/16  add console for messages from arduino
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        speed = (SeekBar)findViewById(R.id.seekBar);
        button2 = (Button)findViewById(R.id.button2);
        button3 = (Button)findViewById(R.id.button3);

        textView1 = (TextView)findViewById(R.id.textView1);
        textView2 = (TextView)findViewById(R.id.textView2);
        textView3 = (TextView)findViewById(R.id.textView3);
        textView4 = (TextView)findViewById(R.id.textView4);
        textView5 = (TextView)findViewById(R.id.textView5);
        Console = (TextView) findViewById(R.id.Console);
        btBufferedReader = new BufferedReader(aReader)
        layout_joystick = (RelativeLayout)findViewById(R.id.layout_joystick);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Disconnect();
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                release();
            }
        });

        js = new JoyStickClass(getApplicationContext()
                , layout_joystick, R.drawable.image_button_2);
        js.setStickSize(150, 150);
        js.setLayoutSize(500, 500);
        js.setLayoutAlpha(150);
        js.setStickAlpha(100);
        js.setOffset(90);
        js.setMinimumDistance(50);

        layout_joystick.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                js.drawStick(arg1);
                if(arg1.getAction() == MotionEvent.ACTION_DOWN
                        || arg1.getAction() == MotionEvent.ACTION_MOVE) {
                    textView1.setText("X : " + String.valueOf(js.getX()));
                    textView2.setText("Y : " + String.valueOf(js.getY()));
                    textView3.setText("Angle : " + String.valueOf(js.getAngle()));
                    textView4.setText("Distance : " + String.valueOf(js.getDistance()));

                    int direction = js.get8Direction();
                    if(direction == JoyStickClass.STICK_UP) {
                        textView5.setText(R.string.up);
                        forward();
                    } else if(direction == JoyStickClass.STICK_UPRIGHT) {
                        textView5.setText(R.string.upright);
                        forwardright();
                    } else if(direction == JoyStickClass.STICK_RIGHT) {
                        textView5.setText(R.string.right);
                        right();
                    } else if(direction == JoyStickClass.STICK_DOWNRIGHT) {
                        textView5.setText(R.string.downright);
                        backwardright();
                    } else if(direction == JoyStickClass.STICK_DOWN) {
                        textView5.setText(R.string.down);
                        backward();
                    } else if(direction == JoyStickClass.STICK_DOWNLEFT) {
                        textView5.setText(R.string.downleft);
                        backwardleft();
                    } else if(direction == JoyStickClass.STICK_LEFT) {
                        textView5.setText(R.string.left);
                        left();
                    } else if(direction == JoyStickClass.STICK_UPLEFT) {
                        textView5.setText(R.string.upleft);
                        forwardleft();
                    } else if(direction == JoyStickClass.STICK_NONE) {
                        textView5.setText(R.string.center);
                        release();
                    }
                    else {
                        release();
                    }
                } else if(arg1.getAction() == MotionEvent.ACTION_UP) {
                    textView1.setText(R.string.x);
                    textView2.setText(R.string.y);
                    textView3.setText(R.string.angle);
                    textView4.setText(R.string.distance);
                    textView5.setText(R.string.direction);
                }
                return true;
            }
        });
        //// TODO: 2/14/16 add speed seekbar
        /*speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser==true)
                {
                    try
                    {
                        btSocket.getOutputStream().write(String.valueOf(progress).getBytes());
                    }
                    catch (IOException e)
                    {

                    }
                }
            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });*/
    }

    void forward() {
        try
        {
            btSocket.getOutputStream().write("1".getBytes());
        }
        catch (IOException e)
        {
            showToast(getString(R.string.error4), false);
        }
    }

    void forwardright() {
        try
        {
            btSocket.getOutputStream().write("2".getBytes());
        }
        catch (IOException e)
        {
            showToast(getString(R.string.error4), false);
        }
    }

    void forwardleft(){
        try
        {
            btSocket.getOutputStream().write("3".getBytes());
        }
        catch (IOException e)
        {
            showToast(getString(R.string.error4), false);
        }
    }

    void backwardright(){
        try
        {
            btSocket.getOutputStream().write("4".getBytes());
        }
        catch (IOException e)
        {
            showToast(getString(R.string.error4), false);
        }
    }

    void backward(){
        try
        {
            btSocket.getOutputStream().write("5".getBytes());
        }
        catch (IOException e)
        {
            showToast(getString(R.string.error4), false);
        }
    }

    void backwardleft(){
        try
        {
            btSocket.getOutputStream().write("6".getBytes());
        }
        catch (IOException e)
        {
            showToast(getString(R.string.error4), false);
        }
    }

    void release(){
        try
        {
            btSocket.getOutputStream().write("7".getBytes());
        }
        catch (IOException e)
        {
            showToast(getString(R.string.error4), false);
        }
    }

    void left(){
        try
        {
            btSocket.getOutputStream().write("8".getBytes());
        }
        catch (IOException e)
        {
            showToast(getString(R.string.error4), false);
        }
    }

    void right() {
        try
        {
            btSocket.getOutputStream().write("9".getBytes());
            Console.setText(getText(R.id.Console) + btBufferedReader.readLine());
        }
        catch (IOException e)
        {
            showToast(getString(R.string.error4), false);
        }
    }

    //get rid of stale toast before making fresh
    void showToast(String text, boolean longDuration) {
        if (mLastToast != null) mLastToast.cancel();
        mLastToast = Toast.makeText(BaseActivity.this, text, longDuration ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        mLastToast.setGravity(Gravity.BOTTOM, 0, 0);
        mLastToast.show();
    }
}