package net.uofitorn.thebiggestbigtwo.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class Hand implements Serializable {

		private ArrayList<Card> cards = new ArrayList<Card>();
		private boolean goesFirst = false;

		public Hand() {
		}
		
		public boolean goesFirst() {
			return goesFirst;
		}
		
		public ArrayList<Card> getCards() {
			return cards;
		}
		
		public void sort() {
			Collections.sort(cards);
		}
		
		public Card getCard(int index) {
			return cards.get(index);
		}
		/*
		public void removeCard(int i) {
			cards.set(i, null);
		}
		
		public void removeCard(Card card) {
			for (int i = 0; i < cards.size(); i++) {
				Card tmp = cards.get(i);
				if (tmp != null && tmp.getRank() == card.getRank() && tmp.getSuit() == card.getSuit()) {
					cards.set(i,  null);
				}
			}
		} */
		
		public void removeCard(int i) {
			cards.remove(i);
		}
		
		public void removeCard(Card c) {
			cards.remove(c);
		}
		
		public void addCard(Card card) {
			cards.add(card);
		}
		
		public void setGoesFirst(boolean goesFirst) {
			this.goesFirst = goesFirst;
		}
		
		public Card getLowest() {
			Collections.sort(cards);
			return cards.get(0);
		}
}
