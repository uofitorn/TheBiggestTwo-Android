package net.uofitorn.thebiggesttwo;

import java.util.ArrayList;
import java.util.Iterator;

import net.uofitorn.thebiggestbigtwo.common.Card;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.util.Log;

public class CardViews {
	public static final String TAG = "CardViews";
	
	int[] xLocations;
	ArrayList<CardView> handViews;
	ArrayList<CardView> toPlayViews;
	BigTwoGame bigTwoGame;
	int width;

	private int spacing;
	
	public CardViews(BigTwoGame bigTwoGame) {
		xLocations = new int[13];
		handViews = new ArrayList<CardView>();
		this.bigTwoGame = bigTwoGame;
	}
	
	public void repositionCardView(int x, int y, int index) {
		CardView cardToMove = handViews.get(index);
		int i = 0;
		
		if (x > bigTwoGame.getPlayAreaX() && 
				x < bigTwoGame.getPlayAreaX() + bigTwoGame.getPlayAreaWidth() &&
				y > bigTwoGame.getPlayAreaY() && 
				y < bigTwoGame.getPlayAreaY() + bigTwoGame.getPlayAreaHeight()) {
			Log.d(TAG, "Moving card to toplayarea");
			handViews.remove(index);
			computeXLocations(0, 0, handViews.size());
			updateTags();
			updateSpritePositions();
			return;
		}
			
		for (CardView cardView : handViews) {
			Log.d(TAG, "X: " + x + " cardView.x: " + cardView.getSprite().getX() + " and i: " + i + " and handViews.size: " + handViews.size());
			if (x < cardView.getSprite().getX() && i == 0) {
				handViews.remove(index);
				i = -1;
				Log.d(TAG, "Moving card to leftmost position");
				break;
			} else if (x > cardView.getSprite().getX() && x <= cardView.getSprite().getX() + 62) {
				if (i + 1 == index) {
					Log.d(TAG, "SHOULD BE GETTING HERE");
					updateSpritePositions();
					return;
				}
				Log.d(TAG, "This is the most called case");
				handViews.remove(index);
				if (i > index) {
					i--;
				}
				break;
			} else if (x > cardView.getSprite().getX() + 62 && i == handViews.size() - 1) {
				Log.d(TAG, "Moving card to rightmost position");
				handViews.remove(index);
				i = handViews.size() - 1;
				break;
			}
			i++;
		}
				handViews.add(i + 1, cardToMove);
		updateTags();
		updateSpritePositions();	

	}
	
	public void updateSpritePositions() {
		for (int i = 0; i < handViews.size(); i++) {
			//Log.d(TAG, "Setting x, y position of: " + cardViewsList.get(i).getCard().toString());
			Log.d(TAG, "Setting x position for " + i + " to " + xLocations[i]);
			handViews.get(i).getSprite().setX(xLocations[i]);
			handViews.get(i).getSprite().setY((65/2) + 5);
		}
	}
	
	public void updateTags() {
		for (int i = 0; i < handViews.size(); i++) {
			handViews.get(i).setTag(i);
		}
	}

	public void createAddCardView(int x, int y, Card card, ITextureRegion cardTextureRegion, 
			VertexBufferObjectManager vbom, Scene scene) {
		
		xLocations[handViews.size()] = x;
		CardView cardView = new CardView(card);
		Sprite sprite = new Sprite(x, y, cardTextureRegion, vbom) {
			boolean mGrabbed = false;
	
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				switch(pSceneTouchEvent.getAction()) {
					case TouchEvent.ACTION_DOWN:
					    Log.d(TAG, "Moving card: " + this.getTag());
						this.setScale(1.7f);
						this.mGrabbed = true;
						break;
					case TouchEvent.ACTION_MOVE:
						if(this.mGrabbed) {
							this.setPosition(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
						}
						break;
					case TouchEvent.ACTION_UP:
						if(this.mGrabbed) {
							int x = (int) pSceneTouchEvent.getX();
							int y = (int) pSceneTouchEvent.getY();
							repositionCardView(x, y, this.getTag());
							this.mGrabbed = false;
							this.setScale(1.0f);
						}
						break;
				}
				return true;
			}
		};
		sprite.setTag(handViews.size());
		cardView.setSprite(sprite);
		cardView.attachToScene(scene);
		scene.registerTouchArea(sprite);
		handViews.add(cardView);
	}

	public void insertXLocations(int [] xLocations) {
		this.xLocations = xLocations;
	}
	
	public void computeXLocations(int width, int spacing, int positions) {
		if (width != 0) {
			this.width = width;
		}
		if (spacing != 0) {
			this.spacing = spacing;
		}
		
		int requiredLengthOverTwo = this.spacing * positions / 2;
		int startingX = (this.width / 2) - requiredLengthOverTwo;
		
		Log.d(TAG, "Computing new sizes for " + positions + " new cards.");
		for (int i = 0; i < positions; i++) {
			int tmp = (i * this.spacing) + startingX;
			Log.d(TAG, "Adding position : " + tmp + " and startingX is: " + startingX);
			xLocations[i] = (i * this.spacing) + startingX + 31;
		}
	}
}
