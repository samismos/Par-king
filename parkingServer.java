package Parking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class parkingServer implements Runnable {
	/*
	 * Initializing a TCP connection socket through which the application can receive a packet
	 * with information about the spots 
	 */

	private static int NUM_ZONES;
	private static int SPOTS_PER_ZONE;
	private int spotID;
	private int zoneID;
	private int port;
	private boolean dataStored=false;
	private List<DataStoredListener> listeners = new ArrayList<>();
	private ServerSocket serverSocket;
	private char[] zoneLetter = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};


	public parkingServer(int port, int zones , int spots) {
		this.port = port;
		NUM_ZONES = zones;
		SPOTS_PER_ZONE = spots;
		try {
			this.serverSocket = new ServerSocket(port); 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public interface DataStoredListener {
		void onDataStoredChanged(boolean newValue);
	}

	public void run() {
		System.out.println("Server started on port "+port);
		while (true) {
			try(    // Using a try-with-resources block in order to ensure proper resource closing
					Socket socket = serverSocket.accept(); // Wait for a client to connect
					// Initialize input and output streams for the socket
					BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					PrintWriter output = new PrintWriter(socket.getOutputStream(), true)){
				System.out.println("Client connected: " + socket.getInetAddress().getHostAddress());
				
				// Send welcome message to the client with information about the parking lot
				String greeting = "Client IP address is: " +socket.getInetAddress().getHostAddress();
				String zones = "Zones: "+zoneLetter[0]+"-"+zoneLetter[NUM_ZONES-1];
				String spots = "Spots per zone: 1-"+SPOTS_PER_ZONE;
				String instructions = "Enter data to change status of parking spot. FORMAT: [ZoneLetter][SpotNumber] ,e.g. A10, C8, D22";
				String exit ="Type EXIT to exit the connection.";
				output.println(greeting);
				output.println(zones);
				output.println(spots);
				output.println(instructions);
				output.println(exit);

				
				while(true) {
					String message = input.readLine().toUpperCase(); // Read message from the client and capitalize
					if(message != null) {
						if(validateString(message, output)) { // Calling ValidateString to filter messages and only receive good data, e.g. A12, B8, C10
							System.out.println("Spot info acquired successfully.");
							dataStored = !dataStored; // Changing value of dataStored to trigger the DataStoredListener
						}
					}
					else continue;

					
					if(message.equals("EXIT")) { // Close connection on "EXIT" message
						 
						System.out.println("Connection exited by client.");
						socket.close(); // close the client connection socket
						run(); // execute run to keep listening for new client connections
					}
				}

			}
			catch (IOException e) {				
				break;
			}
			
		}

	}


	public boolean isDataStored() {
		return dataStored;
	}

	public void setDataStored(boolean dataStored) {
		if (this.dataStored != dataStored) {
			this.dataStored = dataStored;
			notifyDataStoredChanged(dataStored);
		}
	}

	private void notifyDataStoredChanged(boolean newValue) {
		for (DataStoredListener listener : listeners) {
			listener.onDataStoredChanged(newValue);
		}
	}
	public void addDataStoredListener(DataStoredListener listener) {
		listeners.add(listener);
	}
	public void removeDataStoredListener(DataStoredListener listener) {
		listeners.remove(listener);
	}

	public static String extractNumber(String input) {
		String numberPart = "";
		numberPart = input.substring(1);
		return numberPart;
	}

	public boolean checkFormat(String input) {


		// Using regex to match a letter as the first character and numbers for the rest
		if(input.matches("[a-zA-Z]\\d*")) return true;
		else return false;
	}

	public boolean checkLength(String str, int upperLimit, int lowerLimit) {
		if(str.length() > upperLimit || str.length() < lowerLimit) return false;
		else return true;
	}

	public boolean isZoneValid(String str) {
		 // Creating a new array that will hold only the letters of the zones currently in use by the system.
		char[] usedZones = new char[NUM_ZONES];
		System.arraycopy(zoneLetter, 0, usedZones, 0, NUM_ZONES);
		String temp = new String(usedZones);
		if(temp.indexOf(Character.toUpperCase(str.charAt(0))) != -1) { // if index of letter in usedZones array is -1, it is NOT an element of the array
			zoneID = temp.indexOf(Character.toUpperCase(str.charAt(0)));
			return true;
		}
		else return false;
	}

	public boolean isSpotValid(int b) {
		if( b > 0 && b <= SPOTS_PER_ZONE) {
			spotID = b-1;
			return true;
		}
		else return false;
	}
	public boolean validateString(String message, PrintWriter output) {
		if(message != null) {

			if(checkFormat(message)) {
				//checking length to be between 2 and 4 characters
				if( !checkLength(message, 4, 2) ) {
					System.out.println("Inappropriate message length.");
					output.println("Inappropriate message length.");
				}
				else {
					// since message has proper length and format, extract number value (spotID) and validate zone and spot IDs
					String numberPart = extractNumber(message);
					if (!numberPart.isEmpty()) {
						int number = Integer.parseInt(numberPart);
						if(isSpotValid(number) && isZoneValid(message)) {
							System.out.println("Valid spot is "+Character.toUpperCase(message.charAt(0)) + number);
							output.println("Valid spot is "+Character.toUpperCase(message.charAt(0)) + number+", registered");
							return true;
						}
						else {
							System.out.println("No valid spot found in the message.");
							output.println("No valid spot found in the message.");
						}
					} 
				}
			}
			else {
				System.out.println("Invalid message format.");
				output.println("Invalid message format for message : "+message);
			}
			System.out.println("Received from client: " + message+"\n");
		}
		return false;
	}
	
	public void stop() throws IOException {
		if (serverSocket != null && !serverSocket.isClosed()) {
			serverSocket.close();
			System.out.println("Server stopped.");	
		}
	}
	
	public int getSpotID() {
		return spotID;
	}

	public int getZoneID() {
		return zoneID;
	}
}
