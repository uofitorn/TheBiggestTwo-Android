package net.uofitorn.thebiggesttwo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.net.Socket;
import java.net.UnknownHostException;

import net.uofitorn.thebiggestbigtwo.common.NetworkMessage;
import net.uofitorn.thebiggestbigtwo.common.Play;
import android.util.Log;

public class ClientThread extends Thread {

	private static final String TAG = "ClientThread";
	BigTwoGame bigTwoGame;
	Socket socket;
	ObjectOutputStream out;
	ObjectInputStream in;
	NetworkMessage message;
	
	public ClientThread(BigTwoGame bigTwoGame) {
		this.bigTwoGame = bigTwoGame;
	}
	
	public void run() {
		try {
			socket = new Socket("uofitorn.net", 4444);
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			while ((message = (NetworkMessage) in.readObject()) != null) {
				Log.d(TAG, "Received message from server: " + message.getMessage());
			    bigTwoGame.receivedMessage(message);
			}
		} catch (OptionalDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void makePlay(Play play) {
		Log.d(TAG, "Sending play message to server in client thread.");
		NetworkMessage m = new NetworkMessage(NetworkMessage.PLAY);
		m.setPlay(play);
		try {
			out.writeObject(m);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
