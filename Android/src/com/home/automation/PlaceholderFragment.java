package com.home.automation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.home.automation.common.cmds.GarageDoor;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class PlaceholderFragment extends Fragment {
	private interface Constants {
		String LOG = "com.home.automation.placeholderfragment";
	}
	 
	private InputStream inStream = null;
	private OutputStream outStream = null;
	
	public PlaceholderFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(Constants.LOG, "onCreateView called");
				
		View rootView = inflater.inflate(R.layout.fragment_home_automation,
				container, false);
		
        final Switch onOffSwitch = (Switch) rootView.findViewById(R.id.garage_door);
        onOffSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
        	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        		try {
        			if(outStream != null) {
        				outStream.write(GarageDoor.ACTIVATE_RELAY.getBytes());
        			} else {
        				Log.d(Constants.LOG, "Connection to garage door blt server was lost");
        			}
        		} catch (IOException e) {
					Log.d(Constants.LOG, "Got IOexception: " + e.getMessage() + " while when isChecked: " + isChecked);
					
					onOffSwitch.setChecked(!isChecked);
				}
        	}
        });
		
		return rootView;
	}
	
    /**
     * Sets the bluetooth inputstream to read response from server
     * 
     * @param out the bluetooth inputstream
     */
	public void setInputStream(InputStream in) {
		
		final Switch onOffSwitch = (Switch) this.getActivity().findViewById(R.id.garage_door);
		if(!onOffSwitch.isEnabled()) onOffSwitch.setEnabled(true);
		
		this.inStream = in;
	}
	
    /**
     * Sets the bluetooth outputstream to write to server
     * 
     * @param out the bluetooth outputstream
     */
    public void setOutputStream(OutputStream out){
    	this.outStream = out;
    }

}
