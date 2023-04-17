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
	 * ADDING A CONNECTION HANDLER
	 * Initializing a TCP connection socket through which the application can receive a packet
	 * with information about the spots 
	 * 
	 */

	private static int NUM_ZONES;
	private static int SPOTS_PER_ZONE;
	private int spotID;
	private int zoneID;
	private int port;

	//private boolean dataStored=false;
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
			//System.out.println("ServerSocket initialized in parkingServer constructor");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public interface DataStoredListener {
		void onDataStoredChanged(boolean newValue);
	}

	public void run() {
		System.out.println("Server started on port "+port);
		//System.out.println("Server Bound : "+serverSocket.isBound()+" to port : "+serverSocket.getLocalPort());
		while (true) {
			// Using a try-with-resources block in order to ensure proper resource closing
			try(    // Wait for a client to connect
					Socket socket = serverSocket.accept();
					// Create input and output streams for the socket
					BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					PrintWriter output = new PrintWriter(socket.getOutputStream(), true)){
				System.out.println("Client connected: " + socket.getInetAddress().getHostAddress());
				// Send a greeting message to the client
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

				// Read message from the client
				while(true) {
					String message = input.readLine().toUpperCase();
					if(message != null) {
						//calling ValidateString to filter messages and only receive good data, e.g. A12, B8, C10
						if(validateString(message, output)) {
							System.out.println("Spot info acquired successfully.");
							dataStored = !dataStored;
						}
					}
					else continue;

					//close connection on "EXIT" message
					if(message.equals("EXIT")) {
						// close the connection socket, execute run() method so that 
						System.out.println("Connection exited by client.");
						//stop();
						socket.close();
						run();
						//break;
					}
				}

			}
			catch (IOException e) {
				//System.err.println("Error while starting the server: " + e.getMessage());				
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
		if(str.length() > upperLimit && str.length() < lowerLimit) return false;
		else return true;
	}

	public boolean isZoneValid(String str) {
		/*
		 * Creating a new array that will hold only the letters of the zones currently in use by the system.
		 */
		char[] usedZones = new char[NUM_ZONES];
		System.arraycopy(zoneLetter, 0, usedZones, 0, NUM_ZONES);
		String temp = new String(usedZones);
		if(temp.indexOf(Character.toUpperCase(str.charAt(0))) != -1) {
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
				//checking length to be less than 4
				if( !checkLength(message, 4, 2) ) {
					System.out.println("Inappropriate message length.");
					output.println("Inappropriate message length.");
				}
				else {
					//Message has proper format, and is between 2 and 4 characters
					String numberPart = extractNumber(message);
					if (!numberPart.isEmpty()) {
						int number = Integer.parseInt(numberPart);
						if(isSpotValid(number) && isZoneValid(message)) {
							System.out.println("Valid spot is "+Character.toUpperCase(message.charAt(0)) + number);
							output.println("Valid spot is "+Character.toUpperCase(message.charAt(0)) + number+", registered");
							//spotID = number;
							//System.out.println("Zone ID : "+zoneID);
							//System.out.println("Spot ID : "+spotID);
							//System.out.println("dataStored value was: "+dataStored);
							//System.out.println("dataStored value changed to "+!dataStored);
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
	//Socket socket, BufferedReader input, PrintWriter output
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
