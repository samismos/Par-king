package Parking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Client {
	public static void main(String[] args) {
		try {
			// Create a socket to connect to the server
			Socket socket = new Socket("localhost", 6709);

			// Get input and output streams from the socket
			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

			// Send a message to the server
			output.println("test message - expects (Invalid message format)");

			// Receive and print the server's initial messages
			for(int i=0 ; i < 5 ; i++) {
				String response = input.readLine();
				if(response != null) {
					System.out.println("Received from server: " + response);	
				}
			}

			Scanner sc = new Scanner(System.in);
			while(!socket.isClosed()) {

				String response = input.readLine();
				System.out.println(response);
				//continue;
				/*if(response.equals("SUCCESS")) {
					System.out.println("Closing connection...");
					break;
				}*/


				//if(response != null) continue;
				if (sc.hasNextLine()) {
					String message = sc.nextLine();
					output.println(message);
					if(message.toUpperCase().equals("EXIT")) {
						sc.close();
						break;
					}
					TimeUnit.MILLISECONDS.sleep(1);
					continue;
				} 
				else {
					System.err.println("No input found.");
				}
			}
			// Close the streams and socket
			try {
				System.out.println("Closed client resources successfully.");
				input.close();
				output.close();
				socket.close();
			} catch (IOException e) {
				System.err.println("Error closing resources: " + e.getMessage());
			}


		} catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}