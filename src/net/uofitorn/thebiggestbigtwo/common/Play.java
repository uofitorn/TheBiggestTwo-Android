package net.uofitorn.thebiggestbigtwo.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class Play implements Serializable {
	
	private static final String TAG = "Play";
	
	public enum TypesOfPlays {INVALID, PASS, SINGLE, PAIR, TRIPLE, STRAIGHT, FLUSH, FULL_HOUSE, 
								QUAD, STRAIGHT_FLUSH};	
								
	public String friendlyNames[] = {"Invalid Play", "PASS", "Single Card", "Pair", "Three of a Kind",
				"Straight", "Flush", "Full House", "Four of a Kind", "Straight Flush"};
		
	private Card highestCard;
	ArrayList<Card> cards;								
	private TypesOfPlays typeOfPlay;	
	
	public Play(ArrayList<Card> cards) {
		this.cards = cards;
		setTypeAndHighest();
	}
	
	public String getFriendlyName() {
		return friendlyNames[typeOfPlay.ordinal()];
	}
	
	public Card getCard(int i) {
		return cards.get(i);
	}
	
	public int compareTo(Play play) {
		//if play > this.play return -1
		if (this.getCardsInPlay() != play.getCardsInPlay()) {
			return -1;
		}
		if(this.typeOfPlay == play.getTypeOfPlay()) {
			if (this.highestCard.getOrdinalNumber() < play.getHighestCard().getOrdinalNumber()) {
				return 1;
			} else {
				return -1;
			}
		} else if (this.typeOfPlay.ordinal() < play.getTypeOfPlay().ordinal()) {
			return 1;
		} else {
			return -1;
		}
	}
	
	public TypesOfPlays getTypeOfPlay() {
		return typeOfPlay;
	}
	
	public Card getHighestCard() {
		return highestCard;
	}
	
	private boolean isStraight() {
		int bottomRank = cards.get(0).getRank();
		for (int i = 0; i < 5; i++) {
			if (cards.get(i).getRank() == bottomRank + i) {
				continue;
			} else {
				return false;
			}
		}	
		highestCard = new Card(cards.get(4).getSuit(), cards.get(4).getRank());
		return true;
	}
	
	private boolean isFlush() {
		int suit = cards.get(0).getSuit();
		for (Card c : cards) {
			if (suit != c.getSuit()) {
				return false;
			}
		}
		highestCard = new Card(suit, cards.get(4).getRank());
		return true;
	}

	private boolean isFullHouse() {
		boolean a1 = cards.get(0).getRank() == cards.get(1).getRank() &&
				 	 cards.get(1).getRank() == cards.get(2).getRank() &&
				 	 cards.get(3).getRank() == cards.get(4).getRank();
		boolean a2 = cards.get(0).getRank() == cards.get(1).getRank() &&
			 	 	 cards.get(2).getRank() == cards.get(3).getRank() &&
			 	 	 cards.get(3).getRank() == cards.get(4).getRank();
		if (a1) {
			highestCard = new Card(cards.get(0).getSuit(), cards.get(0).getRank());
		} else {
			highestCard = new Card(cards.get(4).getSuit(), cards.get(4).getRank());
		}
		return (a1 || a2);
	}

	private boolean isQuad() {
		boolean a1 = cards.get(0).getRank() == cards.get(1).getRank() &&
			 cards.get(1).getRank() == cards.get(2).getRank() &&
			 cards.get(2).getRank() == cards.get(3).getRank();
		boolean a2 = cards.get(1).getRank() == cards.get(2).getRank() &&
			 cards.get(2).getRank() == cards.get(3).getRank() &&
			 cards.get(3).getRank() == cards.get(4).getRank();
		if (a1) {
			highestCard = new Card(cards.get(0).getSuit(), cards.get(0).getRank());
		} else {
			highestCard = new Card(cards.get(4).getSuit(), cards.get(4).getRank());
		}
		return (a1 || a2);
	}

	private boolean isStraightFlush() {
		if(isStraight() && isFlush()) {
			highestCard = new Card(cards.get(4).getSuit(), cards.get(4).getRank());
			return true;
		}	
		return false;
	}

	private boolean isTriple() {
		if(cards.get(0).getRank() == cards.get(1).getRank() && 
				cards.get(1).getRank() == cards.get(2).getRank()) {
			highestCard = new Card(cards.get(2).getSuit(), cards.get(2).getRank());
			return true;
		}
		return false;
	}

	private boolean isPair() {
		if(cards.get(0).getRank() == cards.get(1).getRank()) {
			highestCard = new Card(cards.get(1).getSuit(), cards.get(1).getRank());
			return true;
		}
		return false;
	}
	
	public int getCardsInPlay() {
		return cards.size();
	}
	
	// private helper function to set the type of play
	// and the high rank and suit of the play.
	private void setTypeAndHighest() {
		if (cards == null) {
			typeOfPlay = TypesOfPlays.PASS;
			highestCard = null;
			return;
		}
		Collections.sort(cards);
		if (cards.size() == 1) {
			typeOfPlay = TypesOfPlays.SINGLE;
			highestCard = new Card(cards.get(0).getSuit(), cards.get(0).getRank());
		} else if (cards.size() == 2 && isPair()) {
			typeOfPlay = TypesOfPlays.PAIR;
		} else if (cards.size() == 3 && isTriple()) {
			typeOfPlay = TypesOfPlays.TRIPLE;
		} else if (cards.size() == 5) {
			if (isStraightFlush()) {
				typeOfPlay = TypesOfPlays.STRAIGHT_FLUSH;
			} else if (isQuad()) {
				typeOfPlay = TypesOfPlays.QUAD;
			} else if (isFullHouse()) {
				typeOfPlay = TypesOfPlays.FULL_HOUSE;
			} else if (isFlush()) {
				typeOfPlay = TypesOfPlays.FLUSH;
			} else if (isStraight()) {
				typeOfPlay = TypesOfPlays.STRAIGHT;
			} else {
				typeOfPlay = TypesOfPlays.INVALID;
			}
		} else {
			typeOfPlay = TypesOfPlays.INVALID;
		}
	}
	
}
