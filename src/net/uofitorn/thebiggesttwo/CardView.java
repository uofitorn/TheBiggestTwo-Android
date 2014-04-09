package net.uofitorn.thebiggesttwo;

import net.uofitorn.thebiggestbigtwo.common.Card;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;

import android.util.Log;

public class CardView {
	public final static String TAG = "CardView";
	Sprite sprite;
	Card card;

	public void incrementPosition() {
		int tmp = sprite.getTag();
		sprite.setTag(tmp + 1);
		sprite.setX(sprite.getX() + 62);
	}
	
	public void decrementPosition() {
		int tmp = sprite.getTag();
		sprite.setTag(tmp - 1);
		sprite.setX(sprite.getX() - 62);
	}
	
	public void setTag(int tag) {
		this.sprite.setTag(tag);
	}
	
	public CardView(Card card) {
		this.card = card;
	}
	public Sprite getSprite() {
		return sprite;
	}
	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}
	public Card getCard() {
		return card;
	}
	public void setCard(Card card) {
		this.card = card;
	}
	
	public void setxPos(float xPos) {
		sprite.setX(xPos);
	}
	public void setyPos(float yPos) {
		sprite.setY(yPos);
	}
	public void attachToScene(Scene scene) {
		scene.attachChild(sprite);
	}
}
