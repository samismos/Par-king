package Parking;

import org.eclipse.swt.widgets.Label;

public class parkingLot {

	private int numberOfZones;
	private parkingZone[] zones;
	private int maxCapacity;

	public parkingLot(int numZones, int numSpots) {
		numberOfZones = numZones;
		zones = new parkingZone[numZones];
		maxCapacity = numZones*numSpots;
		for(int i=0; i < numZones; i++) {
			zones[i] = new parkingZone(i, numSpots);
		}
	}	

	public int getMaxCapacity() {
		return maxCapacity;
	}
	
	public void setMaxCapacity(int maxCap) {
		maxCapacity = maxCap;
	}
	
	public void updateCurrentCapacity(Label lbl) {
		double sum=0;
		for(int i=0; i < numberOfZones; i++) {
			sum = sum + zones[i].getZoneOccupiedSpots();
		}
		lbl.setText("CURRENTLY OCCUPIED: "+(Math.round((sum*100)/maxCapacity*100.0)/100.0)+"% CAPACITY"); // limit capacity value to 2 decimal places 
	}

	public parkingZone getZone(int index) {
		return zones[index];
	}
	public int getNumberOfZones() {
		return numberOfZones;
	}
}