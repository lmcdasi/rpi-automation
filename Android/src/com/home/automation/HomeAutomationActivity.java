package com.home.automation;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.XMLReader;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;

public class HomeAutomationActivity extends Activity {
	private interface Constants {
		int ENABLE_BLUETOOTH = 2292;
		String LOG = "com.home.automation.activity";
		String[] SERVER_NAME = {"rpione"};
		String[] SERVER_ADDRESS = {"00:02:72:3F:E9:E4"};
	}
	
	private ConnectionTask connTask = null;
	private BluetoothAdapter mBluetoothAdapter = null;
	private BroadcastReceiver mReceiver = null;
	private PlaceholderFragment activityPlaceholder = null;
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( (requestCode == Constants.ENABLE_BLUETOOTH) && (resultCode != RESULT_OK) ){
    		Log.e(Constants.LOG, "Bluetooth disabled");
    	}
		
		Log.d(Constants.LOG, "onActivityResult: " + requestCode + "  " + resultCode);
    	super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_automation);
	
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if ((mBluetoothAdapter != null) && (!mBluetoothAdapter.isEnabled())) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, Constants.ENABLE_BLUETOOTH);
        } else {
        	if (mBluetoothAdapter == null) {
        		Log.e(Constants.LOG, "onCreate - failed to detect bluetooth adapter");
        	} else {
        		Log.d(Constants.LOG, "onCreate - detected bluetooth device && isEnabled == true");
        	}
        }
        
        final HomeAutomationActivity mainAct = this;
        mReceiver = new BroadcastReceiver() {
        	@Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    
                    Log.d(Constants.LOG, "Found device: [" + device.getName() + "] at address: [" + device.getAddress() + "]");
                    for(int i = 0; i< Constants.SERVER_NAME.length; i++){
                    	if(device.getName().equals(Constants.SERVER_NAME[i]) && device.getAddress().equals(Constants.SERVER_ADDRESS[i])){
                    		mBluetoothAdapter.cancelDiscovery();
                    		
                    		Log.d(Constants.LOG, "BroadcastReceiver - executing connection task.");
                    		connTask = new ConnectionTask(device, mainAct);
                    		connTask.execute();
                    	}
                    }	
                } else if(BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                	final Switch onOffSwitch = (Switch) activityPlaceholder.getView().findViewById(R.id.garage_door);
                	if(onOffSwitch.isEnabled()) onOffSwitch.setEnabled(false);
                	
                	if(connTask != null) {
                		connTask.closeConnection();
                		connTask = null;
                	}
                }
            }
        };
        
        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
        mBluetoothAdapter.startDiscovery();
        
		if (savedInstanceState == null) {
			activityPlaceholder = new PlaceholderFragment();
			
			getFragmentManager().beginTransaction()
					.add(R.id.container, activityPlaceholder, "test").commit();
		} else {
			activityPlaceholder = (PlaceholderFragment) getFragmentManager().findFragmentById(R.id.container);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home_automation, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
	
	@Override
	public void onStart() {
		Log.i(Constants.LOG, "onStart called");
		super.onStart();
	}
	
	@Override
	public void onDestroy() {
		Log.i(Constants.LOG, "onDestroy called");
		super.onDestroy();
		
		if(connTask != null) connTask.closeConnection();
		
		unregisterReceiver(mReceiver);
	}

    /**
     * Sets the bluetooth inputstream to read response from server
     * 
     * @param out the bluetooth inputstream
     */
	public void setInputStream(InputStream in) {
		if (activityPlaceholder != null)
			activityPlaceholder.setInputStream(in);
	}
	
    /**
     * Sets the bluetooth outputstream to write to server
     * 
     * @param out the bluetooth outputstream
     */
    public void setOutputStream(OutputStream out){
    	if (activityPlaceholder != null)
    		activityPlaceholder.setOutputStream(out);
    }
}
