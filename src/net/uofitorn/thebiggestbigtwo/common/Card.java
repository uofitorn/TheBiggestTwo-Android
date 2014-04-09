package net.uofitorn.thebiggestbigtwo.common;

import java.io.Serializable;

public class Card implements Comparable<Card>, Serializable {

	private int rank, suit, ordinalNumber;

	private static String[] suits = {"Diamonds", "Clubs", "Hearts", "Spades"};
	private static String[] ranks = {"3", "4", "5", "6", "7", "8", "9", "10", "Jack",
									 "Queen", "King", "Ace", "2"};
	
	public static String rankAsString(int rank) {
		return ranks[rank];
	}

	public Card(int suit, int rank) {
		this.rank = rank;
		this.suit = suit;
		this.ordinalNumber = (rank * 4) + suit;
	}
	
	@Override
	public String toString() {
		return ranks[rank] + " of " + suits[suit];
	}
	
	public int getRank() {
		return rank;
	}

	public int getSuit() {
		return suit;
	}
	
	public int getOrdinalNumber() {
		return ordinalNumber;
	}
	
	public int compareTo(Card c) {
		if (this.ordinalNumber == c.getOrdinalNumber()) {
			return 0;
		} else if (this.ordinalNumber < c.getOrdinalNumber()) {
			return -1;
		} else {
			return 1;
		}
	}
}

