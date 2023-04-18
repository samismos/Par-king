package Parking;

import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.SWTResourceManager;

public class parkingSpot {
	private int spotID;
	private parkingZone zone;
	private boolean status;
	private Button button;

	public parkingSpot(int spotID, Button button){
		this.spotID = spotID;
		this.status = true; // declare spot as available by default
	}
	public int getSpotID() {
		return spotID;
	}
	
	public void setSpotID(int id) {
		this.spotID = id;
	}

	public parkingZone getZone() {
		return zone;
	}
	
	public void setZone(parkingZone zone) {
		this.zone = zone;
	}
	
	public void setButton(Button button) {
		this.button = button;
	}

	public boolean isAvailable() {
		return status;
	}
	
	public void setStatus(boolean status) {
		this.status = status;
		if(status == true) {
			// set color of button as GREEN when available or RED when occupied
			button.setBackground(SWTResourceManager.getColor(0, 255, 0));
		}
		else {
			button.setBackground(SWTResourceManager.getColor(255,0,0));
		}
	}
}