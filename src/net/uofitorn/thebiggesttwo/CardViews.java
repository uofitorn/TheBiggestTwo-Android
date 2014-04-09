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
	int [] singleXLocation;
	int [] pairXLocations;
	int [] tripleXLocations;
	int [] fiverXLocations;
	int inPlayYLocation;
	ArrayList<CardView> handViews;
	ArrayList<CardView> toPlayViews;
	ArrayList<CardView> currentPlay;

	BigTwoGame bigTwoGame;


	private int spacing;

	private ArrayList<Card> currentPlayCards;
	
	public CardViews(BigTwoGame bigTwoGame) {
		xLocations = new int[13];
		singleXLocation = new int[1];
		pairXLocations = new int[2];
		tripleXLocations = new int[3];
		fiverXLocations = new int[5];
		handViews = new ArrayList<CardView>();
		toPlayViews = new ArrayList<CardView>();
		this.bigTwoGame = bigTwoGame;
		setInPlayLocations();
	}
	
	protected void setInPlayLocations() {
		inPlayYLocation = bigTwoGame.getPlayAreaY() + bigTwoGame.getPlayAreaHeight() - 55;
		
		singleXLocation[0] = Constants.CAMERA_WIDTH / 2;
		
		pairXLocations[0] = (Constants.CAMERA_WIDTH / 2) - (Constants.SPACE_BETWEEN_CARDS / 2) - (Constants.CARD_WIDTH / 2);
		pairXLocations[1] = Constants.CAMERA_WIDTH / 2 + (Constants.SPACE_BETWEEN_CARDS / 2) + (Constants.CARD_WIDTH / 2);
		
		tripleXLocations[0] = (Constants.CAMERA_WIDTH / 2) - (Constants.CARD_WIDTH / 2) - Constants.SPACE_BETWEEN_CARDS - (Constants.CARD_WIDTH / 2);
		tripleXLocations[1] = Constants.CAMERA_WIDTH / 2;
		tripleXLocations[2] = (Constants.CAMERA_WIDTH / 2) + (Constants.CARD_WIDTH / 2) + Constants.SPACE_BETWEEN_CARDS + (Constants.CARD_WIDTH / 2);
		
		fiverXLocations[0] = (Constants.CAMERA_WIDTH / 2) - (Constants.CARD_WIDTH / 2) - Constants.SPACE_BETWEEN_CARDS - (Constants.CARD_WIDTH / 2) - 62;
		fiverXLocations[1] = (Constants.CAMERA_WIDTH / 2) - (Constants.CARD_WIDTH / 2) - Constants.SPACE_BETWEEN_CARDS - (Constants.CARD_WIDTH / 2);
		fiverXLocations[2] = Constants.CAMERA_WIDTH / 2;
		fiverXLocations[3] = (Constants.CAMERA_WIDTH / 2) + (Constants.CARD_WIDTH / 2) + Constants.SPACE_BETWEEN_CARDS + (Constants.CARD_WIDTH / 2);
		fiverXLocations[4] = (Constants.CAMERA_WIDTH / 2) + (Constants.CARD_WIDTH / 2) + Constants.SPACE_BETWEEN_CARDS + (Constants.CARD_WIDTH / 2) + 62;;
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
			computeXLocations(0, handViews.size());
			toPlayViews.add(cardToMove);
			updateTags();
			updateSpritePositions();
			return;
		}
			
		for (CardView cardView : handViews) {
			Log.d(TAG, "X: " + x + " cardView.x: " + cardView.getSprite().getX() + " and i: " + i + " and handViews.size: " + handViews.size());
			if (x <= (int) cardView.getSprite().getX() && i == 0) {
				handViews.remove(index);
				i = -1;
				Log.d(TAG, "Moving card to leftmost position");
				break;
			} else if (x >= (int) cardView.getSprite().getX() && x < (int) cardView.getSprite().getX() + 62) {
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
		if (i >= 14) {
			Log.d(TAG, "SHOULD NOT BE GETTING HERE!");
		}		handViews.add(i + 1, cardToMove);
		updateTags();
		updateSpritePositions();	

	}
	
	public void updateSpritePositions() {
		for (int i = 0; i < handViews.size(); i++) {
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
	
	public void computeXLocations(int spacing, int positions) {
		if (spacing != 0) {
			this.spacing = spacing;
		}
		
		int requiredLengthOverTwo = this.spacing * positions / 2;
		int startingX = (Constants.CAMERA_WIDTH / 2) - requiredLengthOverTwo;
		
		for (int i = 0; i < positions; i++) {
			int tmp = (i * this.spacing) + startingX;
			xLocations[i] = (i * this.spacing) + startingX + 31;
		}
	}
	
	public ArrayList<Card> getToPlayCards() {
		ArrayList<Card> toPlayCards = new ArrayList<Card>();
		for (CardView cardView : toPlayViews) {
			toPlayCards.add(cardView.getCard());
		}
		return toPlayCards;
	}
	
	public ArrayList<CardView> getCurrentPlay() {
		return currentPlay;
	}

	public void setCurrentPlay(ArrayList<CardView> currentPlay) {
		this.currentPlay = currentPlay;
	}
	
	public void setCurrentPlayCards(ArrayList<Card> currentPlayCards) {
		this.currentPlayCards = currentPlayCards;
	}
	
	public void createCurrentPlayCardViews(CardTextureMap cardTextureMap, 
			VertexBufferObjectManager vbom, Scene scene) {
		currentPlay = new ArrayList<CardView>();
		
		int []tmp = null;
		int i = 0;
		switch (currentPlayCards.size()) {
			case 1:
				tmp = singleXLocation;
				break;
			case 2:
				tmp = pairXLocations;
				break;
			case 3:
				tmp = tripleXLocations;
				break;
			case 5:
				tmp = fiverXLocations;
				break;
		}

		for (Card card : currentPlayCards) {
			Log.d(TAG, "Setting x of new card to " + tmp[i] + " and y to " + inPlayYLocation);
			CardView cardView = new CardView(card);
			ITextureRegion region = cardTextureMap.getTextureRegion(card);	
			Sprite sprite = new Sprite(tmp[i], inPlayYLocation, region, vbom);
			cardView.setSprite(sprite);
			cardView.attachToScene(scene);
			currentPlay.add(cardView);
			i++;
		}

	}
}