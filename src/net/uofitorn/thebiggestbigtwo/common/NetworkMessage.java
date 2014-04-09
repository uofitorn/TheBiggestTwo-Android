package net.uofitorn.thebiggestbigtwo.common;

import java.io.Serializable;

public class NetworkMessage implements Serializable {
	
	public static final int HAND = 0;
	public static final int PLAY = 1;
	public static final int LOST = 2;
	public static final int WON = 3;
	public static final int TURN_UP = 4;
	public static final int STARTING = 5;
	
	int message;
	int playerNumber;
	int playersTurn;
	Hand hand;
	Play play;
	
	public int getPlayerNumber() {
		return playerNumber;
	}

	public void setPlayersTurn(int playersTurn) {
		this.playersTurn = playersTurn;
	}
	
	public int getPlayersTurn() {
		return playersTurn;
	}
	
	public void setPlayerNumber(int playerNumber) {
		this.playerNumber = playerNumber;
	}
	
	public Hand getHand() {
		return hand;
	}

	public void setHand(Hand hand) {
		this.hand = hand;
	}

	public Play getPlay() {
		return play;
	}

	public void setPlay(Play play) {
		this.play = play;
	}
	
	public int getMessage() {
		return message;
	}

	public void setMessage(int message) {
		this.message = message;
	}

	public NetworkMessage(int message) {
		this.message = message;
	}
}
