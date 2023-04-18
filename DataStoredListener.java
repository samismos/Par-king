package Parking;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class DataStoredListener implements Runnable {
	private parkingServer server;
	private parkingLot lot;
	private Display display;
	private Label lblCapacity;

	public DataStoredListener(parkingServer server, parkingLot lot, Display display, Label lblCapacity) {
		this.server = server;
		this.lot = lot;
		this.display = display;
		this.lblCapacity = lblCapacity;
	}

	public void onDataStoredChanged() {
		//UI thread to visually update the parking buttons
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				// changes spot status to the opposite
				boolean previousStatus = lot.getZone(server.getZoneID()).getSpot(server.getSpotID()).isAvailable();
				lot.getZone(server.getZoneID()).getSpot(server.getSpotID()).setStatus(!previousStatus);
				lot.updateCurrentCapacity(lblCapacity);
			}
		});
	}

	@Override
	public void run() {
		
		AtomicBoolean previousValue = new AtomicBoolean(server.isDataStored()); // set a boolean value and listen for changes in that value

		ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(); // use a ScheduledExecutorService to schedule the task at fixed intervals
		executorService.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {				
				boolean currentValue = server.isDataStored(); // read new value of isDataStored()
				
				if (currentValue != previousValue.get()) { // if value of isDataStored has CHANGED, update relevant spot
					onDataStoredChanged();
					previousValue.set(currentValue);
				}
			}
		}, 0, 1000, TimeUnit.MILLISECONDS); // Schedule the task to run every 1 second

		// Wait for the executor service to terminate
		while (!executorService.isTerminated()) {
			while (!Thread.interrupted()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					System.err.println("DataStoredListener Thread was interrupted while sleeping");
					e.printStackTrace();
				}
			}
		}
	}
}
