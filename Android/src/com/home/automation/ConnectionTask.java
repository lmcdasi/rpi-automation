package com.home.automation;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.ParcelUuid;
import android.util.Log;

public class ConnectionTask extends AsyncTask<Object, Integer, Long> {
	private BluetoothDevice dDevice;
	private BluetoothSocket mmSocket = null;
	private HomeAutomationActivity mainActivity;
	private UUID phBtUUID;
    private boolean connected = false;
    
	private interface Constants {
		String LOG = "com.home.automation.connectiontask";
	}
    
    public ConnectionTask(BluetoothDevice device, HomeAutomationActivity homeact) {
    	dDevice = device;
    	mainActivity = homeact;
    	
    	//Context mainContext = mainActivity.getBaseContext();
    	//String android_id = Secure.getString(mainContext.getContentResolver(), Secure.ANDROID_ID); 
    	//phBtUUID = UUID.nameUUIDFromBytes(android_id.getBytes());

    }
       
	@Override
	protected Long doInBackground(Object... params) {
		Log.i(Constants.LOG, "doInBackground called");
		
		try {
			Method getUuidsMethod = dDevice.getClass().getDeclaredMethod("getUuids");
		    ParcelUuid[] dDeviceUuid = (ParcelUuid[]) getUuidsMethod.invoke(dDevice);
		    phBtUUID = dDeviceUuid[0].getUuid();
	    	
			Log.d(Constants.LOG, "Creating RFCOMM socket unsing UUID: " + phBtUUID);
			mmSocket = dDevice.createRfcommSocketToServiceRecord(phBtUUID);
			//Method m = dDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
			//mmSocket = (BluetoothSocket) m.invoke(dDevice, phBtUUID);
			
			Log.d(Constants.LOG, "Trying to Connect usint UUID: " + phBtUUID);
			mmSocket.connect();
			Log.d(Constants.LOG, "Connected");
			
			connected = true;
		} catch (Exception e) {
			try {
				Log.e(Constants.LOG, "Exception connecting when using UUID: " + phBtUUID + " " + e.getMessage());
				if (mmSocket != null) mmSocket.close();
			} catch (IOException closeException) {
				Log.e(Constants.LOG, "Can't close socket with UUID: " + phBtUUID + " "
						+ closeException.getMessage());
			}

			connected = false;
		}
		
		return null;
	}

	@Override
    protected void onPostExecute(Long result) {
    	Log.d(Constants.LOG, "onPostExecute called");
    	
		try {
			if(connected){
				Log.d(Constants.LOG, "Connected to BLT. Creating out/in streams.");
				mainActivity.setOutputStream(mmSocket.getOutputStream());
				mainActivity.setInputStream(mmSocket.getInputStream());
			}
		} catch (IOException connectException) {
			try {
				Log.e(Constants.LOG, "Unable to set streams UUID: " + phBtUUID + " "
						+ connectException.getMessage());

				mmSocket.close();
			} catch (IOException closeException) {
				Log.e(Constants.LOG, "Unable to close socket UUID: " + phBtUUID + " "
						+ closeException.getMessage());
			}
		}
    }

	@Override
    protected void onProgressUpdate(Integer... progress) {
    	Log.d(Constants.LOG, "onProgressUpdate at: " + progress.toString());
    }
	
	protected void closeConnection() {
		Log.d(Constants.LOG, "closeConnection - called");
		
		try {
			mmSocket.close();
		} catch (IOException e) {
			Log.e(Constants.LOG, "Unable to close connection UUID: " + phBtUUID + " BLT stack in improper state - may require restart");
		}
	}
}
