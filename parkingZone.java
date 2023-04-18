package Parking;

import org.eclipse.swt.widgets.Button;

public class parkingZone {
	private int zoneID;
	private int maxCapacity;
	private parkingSpot[] spots;
	private boolean status;
	private Button button;


	public parkingZone(int zoneID, int zoneCapacity) {
		this.zoneID = zoneID;
		maxCapacity = zoneCapacity;
		spots = new parkingSpot[zoneCapacity];
		for(int i=0; i < zoneCapacity; i++) {
			spots[i] = new parkingSpot(i, button);
		}
	}

	public int getZoneID() {
		return zoneID;
	}

	public int getCapacity() {
		return maxCapacity;
	}

	public void setCapacity(int Capacity) {
		this.maxCapacity = Capacity;
	}

	public int getZoneOccupiedSpots() {
		int counter = 0;
		for(int i=0; i < maxCapacity; i++) {
			if(spots[i].isAvailable() == false) {
				counter++;
			}
		}
		return counter;
	}

	public boolean isFull() {
		return status;
	}

	public parkingSpot getSpot(int index) {
		return spots[index];
	}

	public void setZoneStatus(boolean status) {
		this.status = status;
		for(int i=0; i < maxCapacity; i++) {
			spots[i].setStatus(status);
		}
	}

	public char getZoneLetter() {
		char[] zoneLetter = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
		return zoneLetter[zoneID];
	}
}
