package com.thiernombd.arduinobluetoothcommand;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static BluetoothAdapter mBluetoothAdapter = null;
    BluetoothDevice mBluetoothDevice = null;
    public static BluetoothSocket bSocket = null;
    private ServerBluetooth serverBluetooth = null;
    public static BluetoothHandler  bluetoothHandler = null;
    public static BluetoothHandler.ConnectedThread connectedThread = null;

    Boolean supportedBluetooth = null;
    Boolean enableBluetooth = null;

    EditText input_et = null;
    Button send_bt = null;
    TextView output_tv = null;

    RadioGroup sendOption_rg = null;
    RadioButton sendText_rb = null;
    RadioButton sendScrollText_rb = null;
    RadioButton changeColorBlue_rb = null;
    RadioButton changeColorRed_rb = null;



    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_CONNECTION_BT = 2;

    public static final int MESSAGE_READ = 0;
    public static final int MESSAGE_WRITE = 1;
    public static final int MESSAGE_TOAST = 2;

    private static final String SEND_TEXT = "d-";
    private static final String SEND_SCROLL_TEXT = "ds-";
    private static final String CHANGE_COLOR_BLUE = "b-";
    private static final String CHANGE_COLOR_RED = "r-";
    private String prefixToSend = SEND_TEXT;


    final private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            //Toast.makeText(MainActivity.this, "In the handle method", Toast.LENGTH_SHORT).show();
            if(msg.what == MESSAGE_READ){

                byte[] bytes = (byte[]) msg.obj;
                String newText = output_tv.getText().toString() + new String(bytes) + "\n";
                output_tv.setText(newText);

            }else if(msg.what == MESSAGE_TOAST){
                String msgToast = msg.getData().getString("toast");
                Toast.makeText(MainActivity.this, msgToast, Toast.LENGTH_SHORT).show();
            }else if(msg.what == BluetoothConnectionActivity.MESSAGE_CONNECTED){
                Toast.makeText(MainActivity.this, R.string.ms_connection_success, Toast.LENGTH_SHORT).show();
            }else if(msg.what == BluetoothConnectionActivity.MESSAGE_NOT_CONNECTED){
                Toast.makeText(MainActivity.this, R.string.ms_faild_to_connect, Toast.LENGTH_SHORT).show();
            }
            /*else if(msg.what == MESSAGE_WRITE){

                //String newText = output_tv.getText().toString() + new String(bytes) + "\n";
                //output_tv.setText(newText);

                byte[] bytes = (byte[]) msg.obj;
                String msgToast = new String(bytes);
                Toast.makeText(MainActivity.this, msgToast, Toast.LENGTH_SHORT).show();

            }*/
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.robotech));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        //.setAction("Action", null).show();
                Intent goToConnectionActivity = new Intent(MainActivity.this, BluetoothConnectionActivity.class);
                startActivityForResult(goToConnectionActivity, REQUEST_CONNECTION_BT);
            }
        });

        input_et = (EditText) findViewById(R.id.input_et);
        send_bt = (Button) findViewById(R.id.send_bt);
        output_tv = (TextView) findViewById(R.id.output_tv);

        sendText_rb = (RadioButton) findViewById(R.id.send_text_rb);
        sendScrollText_rb = (RadioButton) findViewById(R.id.send_scroll_text_rb);
        changeColorBlue_rb = (RadioButton) findViewById(R.id.change_color_blue_rb);
        changeColorRed_rb = (RadioButton) findViewById(R.id.change_color_red_rb);
        sendOption_rg = (RadioGroup) findViewById(R.id.send_option_rg);

        sendText_rb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefixToSend = SEND_TEXT;
                input_et.setEnabled(true);

                sendScrollText_rb.setChecked(false);
                changeColorBlue_rb.setChecked(false);
                changeColorRed_rb.setChecked(false);
            }
        });

        sendScrollText_rb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefixToSend = SEND_SCROLL_TEXT;
                input_et.setEnabled(true);

                sendText_rb.setChecked(false);
                changeColorBlue_rb.setChecked(false);
                changeColorRed_rb.setChecked(false);
            }
        });

        changeColorBlue_rb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefixToSend = CHANGE_COLOR_BLUE;
                input_et.setEnabled(false);

                sendScrollText_rb.setChecked(false);
                sendText_rb.setChecked(false);
                changeColorRed_rb.setChecked(false);
            }
        });

        changeColorRed_rb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefixToSend = CHANGE_COLOR_RED;
                input_et.setEnabled(false);

                sendScrollText_rb.setChecked(false);
                changeColorBlue_rb.setChecked(false);
                sendText_rb.setChecked(false);
            }
        });



        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter != null){
            supportedBluetooth = true;
            if(!mBluetoothAdapter.isEnabled()){
                Intent enabledBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enabledBtIntent, REQUEST_ENABLE_BT);
            }else enableBluetooth = true;
        }
        else{
            supportedBluetooth = false;
            Toast.makeText(MainActivity.this, getString(R.string.ms_Bluetooth_not_suppoted), Toast.LENGTH_LONG).show();
        }



        send_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(bSocket !=null){
                    if(input_et.isEnabled()){
                        String inputContent = input_et.getText().toString();
                        if(!inputContent.isEmpty()) {
                            inputContent = prefixToSend + inputContent;
                            connectedThread.write(inputContent.getBytes());

                            String newText = output_tv.getText().toString() + inputContent + "\n";
                            output_tv.setText(newText);

                        }else Toast.makeText(MainActivity.this, R.string.ms_enter_text_to_write, Toast.LENGTH_SHORT).show();
                    }else{
                        connectedThread.write(prefixToSend.getBytes());
                        String changedColor = (prefixToSend.equals(CHANGE_COLOR_BLUE)) ? " -> Couleur blue envoyée" : " -> Couleur rouge envoyée";
                        String newText = output_tv.getText().toString() + changedColor + "\n";
                        output_tv.setText(newText);

                    }
                }else Toast.makeText(MainActivity.this, R.string.ms_have_to_connect, Toast.LENGTH_SHORT).show();

            }
        });

        //////////////////////////////////////////////////////////////////////////////////

        ///////////////////////////////TO DO AT THE END OF THE ONCREATE////////////////////////////////
        bluetoothHandler = new BluetoothHandler(mHandler);
        serverBluetooth = new ServerBluetooth(mBluetoothAdapter, mHandler);
        Log.d("...........", "SERVEEEEEEEEEEEEEEEEUR EN DEMARRAGE..... ");
        serverBluetooth.start();

    }//End of onCreate

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_ENABLE_BT){
            if(resultCode == RESULT_OK){
                enableBluetooth = true;
                Toast.makeText(MainActivity.this, getString(R.string.ms_can_paire_bluetooth), Toast.LENGTH_LONG).show();
            }else {
                enableBluetooth = false;
                Toast.makeText(MainActivity.this, getString(R.string.ms_invite_enable_bluetooth), Toast.LENGTH_LONG).show();
            }
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(bSocket == null) serverBluetooth.cancel();

        if(connectedThread != null)connectedThread.cancel();
    }


}
