package Parking;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;

import java.io.IOException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class parkingAppWindow {
	//using global constants for zones in the lot, as well as spots per zone.
	static int NUM_ZONES = 8; //ALWAYS EQUAL TO OR LESS THAN 26 due to alphabetical constraints
	static int SPOTS_PER_ZONE = 10; //spots per parking zone, modify at will, no limits, however the top row might trim the top spots after VALUE=32;

	//initializing variables grouped for access and easier modification and experimentation 

	//X_BUTTON_INCREMENT is the HORIZONTAL distance between two parking spots in the same zone, e.g. A1 and A2
	int X_BUTTON_INCREMENT = 100;

	//Y_BUTTON_INCREMENT is the VERTICAL distance between two parking spots in the same zone, e.g A1 and A3
	int Y_BUTTON_DECREMENT = 40;

	//X_ZONE_INCREMENT is the HORIZONTAL distance between TWO PARKING ZONES
	int X_ZONE_INCREMENT = 300;

	//Y_ZONE_INCREMENT is the VERTICAL DISTANCE BETWEEN TWO PARKING ZONES
	int Y_ZONE_INCREMENT = (SPOTS_PER_ZONE/2)*60;

	//Shell Dimensions
	int SHELL_WIDTH = 1450;
	int SHELL_HEIGHT = 775;

	protected Shell shlParkingApplication;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			parkingAppWindow window = new parkingAppWindow();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	/**
	 * Open the window.
	 * @throws IOException 
	 */
	public void open() throws IOException {
		Display display = Display.getDefault();
		createContents(display);
		shlParkingApplication.open();
		shlParkingApplication.layout();
		while (!shlParkingApplication.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 * @throws IOException 
	 */
	protected void createContents(Display display) throws IOException {
		//MAIN METHOD, INITIALIZING PARKING LOT, ZONES AND SPOTS, SETTING STATUS TO AVAILABLE.
		parkingLot lot = new parkingLot(NUM_ZONES, SPOTS_PER_ZONE);
		shlParkingApplication = new Shell();
		shlParkingApplication.setBackground(SWTResourceManager.getColor(73, 104, 175));
		shlParkingApplication.setSize(SHELL_WIDTH,SHELL_HEIGHT);
		shlParkingApplication.setText("Parking Application");

		parkingServer server = new parkingServer(6709, NUM_ZONES, SPOTS_PER_ZONE);
		//server.init();
		Thread serverThread = new Thread(server);
		serverThread.start();



		//Logo label
		Label lblWelcomeToOur = new Label(shlParkingApplication, SWT.NONE);
		lblWelcomeToOur.setBackground(SWTResourceManager.getColor(73, 104, 175));
		lblWelcomeToOur.setAlignment(SWT.CENTER);
		lblWelcomeToOur.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblWelcomeToOur.setFont(SWTResourceManager.getFont("Gill Sans Ultra Bold", 17, SWT.NORMAL));

		lblWelcomeToOur.setBounds(10, 10, 314, 49);
		lblWelcomeToOur.setText("PAR 👑 KING ");

		//Text which displays capacity at all times, initialized here and changed on all click events on the parking spots
		Label lblCapacity = new Label(shlParkingApplication, SWT.NONE);
		lblCapacity.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblCapacity.setBackground(SWTResourceManager.getColor(73, 104, 175));
		lblCapacity.setFont(SWTResourceManager.getFont("Gill Sans Ultra Bold", 12, SWT.NORMAL));
		lblCapacity.setBounds(481, 20, 581, 29);
		//calling Lot function to update the content of the label based on current capacity
		lot.updateCurrentCapacity(lblCapacity);


		/*
		 * NETWORK CONNECTION HANDLER	
		 * Creates a listener for the isDataStored() method of the parkingServer
		 * Ran in separate thread so as to not interrupt GUI interactivity
		 */
		DataStoredListener listener = new DataStoredListener(server, lot, display, lblCapacity);
		Thread listenerThread = new Thread(listener);
		listenerThread.start();
		
		//Adding a listener to close server resources when window is disposed
		shlParkingApplication.addDisposeListener(new DisposeListener(){

			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				// TODO Auto-generated method stub
				try {
					System.out.println("Closed UI window.");
					//TimeUnit.SECONDS.sleep(1);
					server.stop();
					//listenerThread.interrupt();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


			}

		});


		//creating the scrolled Composite which will display our parking lot

		ScrolledComposite scrolledComp = new ScrolledComposite(shlParkingApplication, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComp.setAlwaysShowScrollBars(false);
		scrolledComp.setBounds(10, 60, 1280, 720);
		scrolledComp.setExpandHorizontal(true);
		scrolledComp.setExpandVertical(true);

		//generating height based on No of Zones and Spots per zone, making scroll field "responsive" instead of fixed size
		int scrollHeight = (((NUM_ZONES+1)/4)*(30+Y_BUTTON_DECREMENT)*((SPOTS_PER_ZONE+1)/2));
		scrolledComp.setMinSize(1200,scrollHeight);

		//child Composite as wrapper object to hold multiple buttons as required by scrolledComposite
		Composite content = new Composite(scrolledComp, SWT.NONE);
		content.setBackground(SWTResourceManager.getColor(192, 192, 192));
		scrolledComp.setContent(content);

		//setting initial X for zones
		int startingX = 60;
		//dynamically setting initial height for zone, allowing for proper display for any number of spots_per_zone
		int startingY = ((SPOTS_PER_ZONE+1)/2)*37;

		/*MAIN ALGORITHM
		 * The following algorithm uses a nested for-loop to generate parking zones of buttons which represent parking spots.
		 * For every iteration, the X and Y of the button change so that they create a nice formation. 
		 * When we move on to the next zone, the "buttonY" is reset to the startingY, and the startingX changes accordingly.
		 * 
		 * When we create 4 zones, the startingX is reset and the startingY is modified to display the next quarter of zones below the first one.
		 * 
		 */
		int buttonX=0, buttonY=0;
		char[] zoneLetter = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
		buttonY=startingY;

		for(int i=0; i < NUM_ZONES; i++) {
			if(i%4==0 && i!=0) {
				startingX = 60;
				startingY = startingY + Y_ZONE_INCREMENT;
				buttonY=startingY;

			}
			for(int j=0; j < SPOTS_PER_ZONE; j++) {
				Button btnA = new Button(content, SWT.NONE);
				if( j%2 == 0 ) {
					buttonX=startingX;
				}
				if( j%2 != 0 ) {
					buttonX=startingX + X_BUTTON_INCREMENT;
				}
				if(j%2 == 0 && j!=0) {
					buttonY = buttonY - Y_BUTTON_DECREMENT;
				}
				if(lot.getZone(i).getSpot(j).isAvailable()) {
					btnA.setBackground(SWTResourceManager.getColor(0, 255, 0));
				}
				else {
					btnA.setBackground(SWTResourceManager.getColor(255,0,0));
				}
				btnA.setBounds(buttonX, buttonY, 90, 30);
				btnA.setText(zoneLetter[i]+" "+ (j+1));


				final int innerI = i;
				final int innerJ = j;
				//change parkingSpot status on click
				btnA.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseDown(MouseEvent e) {
						//changing status and color on click, refreshing capacity meter
						lot.getZone(innerI).getSpot(innerJ).setStatus(!lot.getZone(innerI).getSpot(innerJ).isAvailable());
						lot.updateCurrentCapacity(lblCapacity);
					}
				});
				lot.getZone(i).getSpot(j).setButton(btnA);
			}//end of inner for

			startingX = startingX + X_ZONE_INCREMENT;
			buttonY = startingY;
		}

		//RESET BUTTON : Button which clears all occupied spots
		Button btnClearAll = new Button(shlParkingApplication, SWT.NONE);
		btnClearAll.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));
		btnClearAll.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if(MessageDialog.openConfirm(shlParkingApplication, "Reset Confirm", "Are you sure you want to reset this program?")) {
					//If user clicks OK, re-run application 
					try {
						server.stop();
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					shlParkingApplication.close();
					try {
						createContents(display);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					shlParkingApplication.open();
					shlParkingApplication.layout();

				}	
			}
		});

		btnClearAll.setBounds(1313, 85, 90, 30);
		btnClearAll.setText("RESET");

		//SELECT ZONE : Button selecting a zone, then setting all selected spots to free or occupied.		
		Button btnSelectZone = new Button(shlParkingApplication, SWT.NONE);
		btnSelectZone.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));
		btnSelectZone.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				//ACTION
				/*
				 * Calling a dialog that returns a zoneID and status
				 */
				ZoneSelectionDialog dialog = new ZoneSelectionDialog(shlParkingApplication, lot);

				if( dialog.open() == Window.OK) {
					//We use the returned values to call the setZoneStatus method and set the status for all the spots in the zone
					lot.getZone(dialog.getZoneID()).setZoneStatus(dialog.getStatus());
				}
				//Updating the capacity meter
				lot.updateCurrentCapacity(lblCapacity);	
			}	

		});
		btnSelectZone.setBounds(1300, 163, 120, 30);
		btnSelectZone.setText("SELECT ZONE");


		Button btnLotSettings = new Button(shlParkingApplication, SWT.NONE);
		btnLotSettings.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));
		btnLotSettings.setBounds(1298, 252, 125, 30);
		btnLotSettings.setText("LOT SETTINGS");
		btnLotSettings.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				//CALL DIALOG FOR ZONES AND SPOTS INPUT
			}	

		});


	}//createContents() END



}





