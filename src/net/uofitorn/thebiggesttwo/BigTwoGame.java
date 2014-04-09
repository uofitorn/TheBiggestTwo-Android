package net.uofitorn.thebiggesttwo;

import net.uofitorn.thebiggestbigtwo.common.Card;
import net.uofitorn.thebiggestbigtwo.common.Hand;
import net.uofitorn.thebiggestbigtwo.common.NetworkMessage;
import net.uofitorn.thebiggestbigtwo.common.Play;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class BigTwoGame {

	private static final String TAG = "BigTwoGame";
	
	private Hand hand;
	private Play currentPlay;
	private int playerNumber = -1;
	private int ownerOfCurrentPlay;
	private int currentPlayersTurn = -1;
	private ClientThread clientThread;
	private Handler handler;
	private int numberOfPlayers = 2;
	private int playAreaX;
	private int playAreaY;
	private int playAreaWidth;
	private int playAreaHeight;
	private CardViews cardViews;
	
	private static BigTwoGame INSTANCE;
	
	public BigTwoGame(Handler handler) {
		this.handler = handler;
	}	

	public Play getTopOfStack() {
		return currentPlay;
	}
	
	public Hand getHand() {
		return hand;
	}
	
	public int getPlayersTurn() {
		return currentPlayersTurn;
	}
	
	public void setPlayersTurn(int playersTurn) {
		this.currentPlayersTurn = playersTurn;
	}
	
	public int makePlay(Play play) {
		if (currentPlay == null) {
			// Can't pass if you're the first player.
			if (play.getTypeOfPlay() == Play.TypesOfPlays.PASS) {
				return Constants.CANT_PASS;
			}
			
			if (play.getTypeOfPlay() == Play.TypesOfPlays.INVALID) {
				return Constants.INVALID_PLAY;
			}
			
			boolean containsLowest = false;
			for (int i = 0; i < play.getCardsInPlay(); i++) {
				if (hand.getLowest().getOrdinalNumber() == play.getCard(i).getOrdinalNumber()) {
					containsLowest = true;
				}
			}
			
			if (currentPlayersTurn != playerNumber) {
				return Constants.NOT_YOUR_TURN;
			}
			
			if (!containsLowest) {
				return Constants.NOT_LOWEST;
			}
			
			currentPlay = play;
			for (int i = 0; i < play.getCardsInPlay(); i++) {
				hand.removeCard(play.getCard(i));
			}
			clientThread.makePlay(play);
			// TODO: 	Update the view so it reflects the missing cards.
			Message msg = handler.obtainMessage();
			msg.what = Constants.UPDATE_VIEW;
			handler.sendMessage(msg);
			return Constants.OK_PLAY;
		} else if (play.getTypeOfPlay() == Play.TypesOfPlays.PASS) {
			incrementPlayer();
			clientThread.makePlay(play);
			// Update the view so it reflects the new player
			Message msg = handler.obtainMessage();
			msg.what = Constants.UPDATE_VIEW;
			handler.sendMessage(msg);
			return Constants.OK_PLAY;
		} else if (currentPlay.compareTo(play) == 1) {
			currentPlay = play;
			for (int i = 0; i < play.getCardsInPlay(); i++) {
				hand.removeCard(play.getCard(i));
			}
			incrementPlayer();
			clientThread.makePlay(play);
			
			// Update the view so it reflects the missing cards.
			Message msg = handler.obtainMessage();
			msg.what = Constants.UPDATE_VIEW;
			handler.sendMessage(msg);
			return Constants.OK_PLAY;
		} else {
			return Constants.NOT_GREATER;
		}	
	}
	
	public void connectToServer() {
		clientThread = new ClientThread(this);
		clientThread.start();
	}
	
	public void receivedMessage(NetworkMessage message) {
		Message msg = handler.obtainMessage();
		switch (message.getMessage()) {
			case NetworkMessage.STARTING:
				this.playerNumber = message.getPlayerNumber();
				this.currentPlayersTurn = message.getPlayersTurn();
				msg.what = Constants.UPDATE_VIEW;
				handler.sendMessage(msg);
				Log.d(TAG, "Received starting message with player number: " + playerNumber);
				break;
			case NetworkMessage.HAND:
				msg.what = Constants.RECEIVED_HAND;
				this.hand = message.getHand();
				this.handler.sendMessage(msg);
				Log.d(TAG, "Received HAND message from server");
				break;
			case NetworkMessage.PLAY:
				incrementPlayer();
				msg.what = Constants.RECEIVED_PLAY;
				this.currentPlay = message.getPlay();
				this.handler.sendMessage(msg);
				break;
		}
	}

	public void setPlayerNumber(int playerNumber) {
		this.playerNumber = playerNumber;
	}
	
	public int getPlayerNumber() {
		return playerNumber;
	}
	
	public void incrementPlayer() {
		currentPlayersTurn++;
		if (currentPlayersTurn == numberOfPlayers) {
			currentPlayersTurn = 0;
		}
	}
	
	public Card getCard(int i) {
		if (hand != null) {
			Card card = hand.getCard(i);
			if (card != null) 
				return card;
			else 
				return null;
		}
		return null;
	}
	
	public int getPlayAreaX() {
		return playAreaX;
	}

	public void setPlayAreaX(int playAreaX) {
		this.playAreaX = playAreaX;
	}

	public int getPlayAreaY() {
		return playAreaY;
	}

	public void setPlayAreaY(int playAreaY) {
		this.playAreaY = playAreaY;
	}

	public int getPlayAreaWidth() {
		return playAreaWidth;
	}	

	public void setPlayAreaWidth(int playAreaWidth) {
		this.playAreaWidth = playAreaWidth;
	}

	public int getPlayAreaHeight() {
		return playAreaHeight;
	}

	public void setPlayAreaHeight(int playAreaHeight) {
		this.playAreaHeight = playAreaHeight;
	}

	public void setCardViews(CardViews cardViews) {
		this.cardViews = cardViews;
	}
}
