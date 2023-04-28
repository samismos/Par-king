package Parking;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;

import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
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

	static int NUM_ZONES = 8; // between 1-26 for alphabetical constraints
	static int SPOTS_PER_ZONE = 10; // spots per parking zone, for optimal performance keep it less or equal to 32, but you can experiment with much higher values
	boolean closeApp;

	//Shell Dimensions
	int SHELL_WIDTH = 1450;
	int SHELL_HEIGHT = 700;

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
		createContents(display, NUM_ZONES, SPOTS_PER_ZONE);
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
	protected void createContents(Display display, int NUM_ZONES, int SPOTS_PER_ZONE) throws IOException {

		parkingLot lot = new parkingLot(NUM_ZONES, SPOTS_PER_ZONE); 

		//X_BUTTON_INCREMENT is the HORIZONTAL distance between two parking spots in the same zone, e.g. A1 and A2
		int X_BUTTON_INCREMENT = 100;

		//Y_BUTTON_INCREMENT is the VERTICAL distance between two parking spots in the same zone, e.g A1 and A3
		int Y_BUTTON_DECREMENT = 40;

		//X_ZONE_INCREMENT is the HORIZONTAL distance between TWO PARKING ZONES
		int X_ZONE_INCREMENT = 300;

		//Y_ZONE_INCREMENT is the VERTICAL DISTANCE BETWEEN TWO PARKING ZONES
		int Y_ZONE_INCREMENT = (SPOTS_PER_ZONE/2)*60;

		//ZONE_HEIGHT 
		int ZONE_HEIGHT = Y_BUTTON_DECREMENT * ((SPOTS_PER_ZONE + 1) / 2);

		shlParkingApplication = new Shell(SWT.SHELL_TRIM & ~ SWT.RESIZE);
		shlParkingApplication.setSize(958, 632);
		shlParkingApplication.setBackground(SWTResourceManager.getColor(73, 104, 175));
		shlParkingApplication.setText("Par👑King - Parking Management System");

		// Server initialization in separate thread to not interrupt UI thread
		parkingServer server = new parkingServer(6709, NUM_ZONES, SPOTS_PER_ZONE);
		Thread serverThread = new Thread(server);
		serverThread.start();

		// Logo label
		Label lblWelcomeToOur = new Label(shlParkingApplication, SWT.NONE);
		lblWelcomeToOur.setBackground(SWTResourceManager.getColor(73, 104, 175));
		lblWelcomeToOur.setAlignment(SWT.CENTER);
		lblWelcomeToOur.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblWelcomeToOur.setFont(SWTResourceManager.getFont("Gill Sans Ultra Bold", 17, SWT.NORMAL));
		lblWelcomeToOur.setBounds(10, 10, 90, 49);
		lblWelcomeToOur.setText("PAR ");
		Label lblCrown = new Label(shlParkingApplication, SWT.NONE);
		lblCrown.setBackground(SWTResourceManager.getColor(73, 104, 175));
		lblCrown.setAlignment(SWT.CENTER);
		lblCrown.setForeground(SWTResourceManager.getColor(207,181,59));
		lblCrown.setFont(SWTResourceManager.getFont("Gill Sans Ultra Bold", 17, SWT.NORMAL));
		lblCrown.setBounds(100, 7, 40, 49);
		lblCrown.setText("👑");
		Label lblKing = new Label(shlParkingApplication, SWT.NONE);
		lblKing.setBackground(SWTResourceManager.getColor(73, 104, 175));
		lblKing.setAlignment(SWT.CENTER);
		lblKing.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblKing.setFont(SWTResourceManager.getFont("Gill Sans Ultra Bold", 17, SWT.NORMAL));
		lblKing.setBounds(133, 10, 120, 49);
		lblKing.setText(" KING");

		// Text which displays capacity at all times, initialized here and changed on all click events on the parking spots
		Label lblCapacity = new Label(shlParkingApplication, SWT.NONE);
		lblCapacity.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblCapacity.setBackground(SWTResourceManager.getColor(73, 104, 175));
		lblCapacity.setFont(SWTResourceManager.getFont("Gill Sans Ultra Bold", 12, SWT.NORMAL));
		lblCapacity.setBounds(481, 20, 581, 29);

		lot.updateCurrentCapacity(lblCapacity); // calling Lot function to update the content of the label based on current capacity


		/*
		 * NETWORK CONNECTION HANDLER	
		 * Creates a listener for the isDataStored() method of the parkingServer
		 * Ran in separate thread so as to not interrupt GUI interactivity
		 */
		DataStoredListener listener = new DataStoredListener(server, lot, display, lblCapacity);
		Thread listenerThread = new Thread(listener);
		listenerThread.start();

		closeApp = true; // closeApp parameter used to distinguish window disposal from reset button and setting changes

		shlParkingApplication.addDisposeListener(new DisposeListener(){ // Adding a listener to close resources properly when window is disposed
			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				try {
					server.stop(); //close server resources
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(closeApp == true) {
					System.out.println("Application exited successfully.");
					System.exit(0);
				}
			}
		});

		//creating the scrolled Composite which will display our parking lot
		ScrolledComposite scrolledComp = new ScrolledComposite(shlParkingApplication, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComp.setAlwaysShowScrollBars(false);
		scrolledComp.setExpandHorizontal(true);
		scrolledComp.setExpandVertical(true);

		//Adapting GUI to smaller monitors if necessary
		DisplayMode monitor = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
		boolean monitorIsSmall = monitor.getWidth() < 1450;
		int scrollHeight, zonesInRow, selectButtonX = 1000, selectButtonY = 85;
		if(monitorIsSmall) {
			shlParkingApplication.setSize(1150, 720);
			zonesInRow = 3;
			scrollHeight = (((NUM_ZONES-1) / zonesInRow) * ( ZONE_HEIGHT + Y_ZONE_INCREMENT));  //generating height based on No of Zones and Spots per zone, making scroll field "responsive" instead of fixed size
			scrolledComp.setBounds(10, 60, 950, 600);	
			scrolledComp.setMinSize(800 , scrollHeight);
			selectButtonX = 1000;
			selectButtonY = 200;
			lblCapacity.setBounds(431, 20, 581, 29);

		}
		else {
			shlParkingApplication.setSize(SHELL_WIDTH,SHELL_HEIGHT);
			zonesInRow = 4;
			scrollHeight = (((NUM_ZONES-1) / zonesInRow) * ( ZONE_HEIGHT + Y_ZONE_INCREMENT));  
			scrolledComp.setBounds(10, 60, 1280, 550);
			scrolledComp.setMinSize(1200 , scrollHeight);
			selectButtonX = 1313;
			selectButtonY = 200;
		}

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
		 * For every iteration, the X and Y of the button change so that they create two columns of buttons. 
		 * When we move on to the next zone, the "buttonY" is reset to the startingY, and the startingX changes accordingly.
		 * 
		 * When we create 4 zones, the startingX is reset and the startingY is modified to display the next row of zones below the first one.
		 */
		int buttonX=0, buttonY=0;
		buttonY=startingY;

		for(int i=0; i < NUM_ZONES; i++) {
			if(i % zonesInRow == 0 && i != 0) {
				startingX = 60; // resetting X to starting X
				startingY = startingY + Y_ZONE_INCREMENT; // setting the startingY lower to create the second row below the first one
				buttonY=startingY;
			}
			for(int j=0; j < SPOTS_PER_ZONE; j++) {
				//creation of the parking spot buttons 
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
				//setting button color
				if(lot.getZone(i).getSpot(j).isAvailable()) {
					btnA.setBackground(SWTResourceManager.getColor(30, 255, 30));
				}
				else {
					btnA.setBackground(SWTResourceManager.getColor(255,30,30));
				}
				btnA.setBounds(buttonX, buttonY, 90, 30);
				btnA.setText(lot.getZone(i).getZoneLetter()+" "+(j+1)); // button text is ZoneID + SpotID
				Label horizontalSeparator = new Label(content, SWT.SEPARATOR | SWT.HORIZONTAL);
				horizontalSeparator.setText("HorizontalSeparator");
				horizontalSeparator.setBounds(buttonX-3, buttonY+33, 98, 3);

				final int innerI = i;
				final int innerJ = j;

				btnA.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseDown(MouseEvent e) {
						//changing status (and color) on click, refreshing capacity meter
						lot.getZone(innerI).getSpot(innerJ).setStatus(!lot.getZone(innerI).getSpot(innerJ).isAvailable());
						lot.updateCurrentCapacity(lblCapacity);
					}
				});
				lot.getZone(i).getSpot(j).setButton(btnA);
			}
			if(SPOTS_PER_ZONE%2 != 0) {
				Label verticalSeparator = new Label(content, SWT.SEPARATOR | SWT.VERTICAL);
				verticalSeparator.setText("VerticalSeparator");
				verticalSeparator.setBounds(buttonX+93, buttonY-5, 5, ZONE_HEIGHT);
				buttonY = startingY;
				startingX = startingX + X_ZONE_INCREMENT;
			}
			else {
				Label verticalSeparator = new Label(content, SWT.SEPARATOR | SWT.VERTICAL);
				verticalSeparator.setText("VerticalSeparator");
				verticalSeparator.setBounds(buttonX-8, buttonY-5, 5, ZONE_HEIGHT);
				buttonY = startingY;
				startingX = startingX + X_ZONE_INCREMENT;
			}
		}

		//RESET : Button which clears all occupied spots
		Button btnClearAll = new Button(shlParkingApplication, SWT.NONE);
		btnClearAll.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));
		btnClearAll.setBounds(selectButtonX, selectButtonY, 90, 30);
		btnClearAll.setText("RESET");
		btnClearAll.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if(MessageDialog.openConfirm(shlParkingApplication, "Reset Confirm", "Are you sure you want to reset this program?")) {
					closeApp = false;
					//If user clicks OK, re-run application 
					try {
						server.stop();
					} catch (IOException e2) {
						e2.printStackTrace();
					}
					shlParkingApplication.close();
					try {
						createContents(display, NUM_ZONES, SPOTS_PER_ZONE);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					shlParkingApplication.open();
					shlParkingApplication.layout();
				}	
			}
		});

		//SELECT ZONE : Button selecting a zone, then setting all selected spots to free or occupied.		
		Button btnSelectZone = new Button(shlParkingApplication, SWT.NONE);
		btnSelectZone.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));
		btnSelectZone.setBounds(selectButtonX - 13, selectButtonY + 80, 120, 30);
		btnSelectZone.setText("SELECT ZONE");
		btnSelectZone.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				ZoneSelectionDialog dialog = new ZoneSelectionDialog(shlParkingApplication, lot); // Calling a dialog that returns a zoneID and status

				if(dialog.open() == Window.OK) { 
					lot.getZone(dialog.getZoneID()).setZoneStatus(dialog.getStatus());
				}
				lot.updateCurrentCapacity(lblCapacity); // Updating the capacity meter	
			}	
		});

		Button btnLotSettings = new Button(shlParkingApplication, SWT.NONE);
		btnLotSettings.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));
		btnLotSettings.setBounds(selectButtonX - 15, selectButtonY + 160, 125, 30);
		btnLotSettings.setText("LOT SETTINGS");
		btnLotSettings.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {

				LotSettingsDialog dialog = new LotSettingsDialog(shlParkingApplication); // Calling a dialog that returns num_zones and spots_per_zone 

				if(dialog.open() == Window.OK) {
					//If user clicks OK, re-run application with new parameters
					closeApp = false;
					try {
						server.stop();
					} catch (IOException e2) {
						e2.printStackTrace();
					}
					shlParkingApplication.close();
					try {
						createContents(display , dialog.getZoneNumber(), dialog.getSpotsPerZone());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					shlParkingApplication.open();
					shlParkingApplication.layout();
				}
			}	

		});
	}
}