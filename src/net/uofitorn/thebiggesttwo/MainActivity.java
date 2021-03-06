package net.uofitorn.thebiggesttwo;

import java.io.IOException;
import java.util.ArrayList;

import net.uofitorn.thebiggestbigtwo.common.Card;
import net.uofitorn.thebiggestbigtwo.common.Hand;
import net.uofitorn.thebiggestbigtwo.common.Play;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.font.FontUtils;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.bitmap.AssetBitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;

import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends BaseGameActivity {
    
	public static final String TAG = "MainActivity";

	private Camera camera;
	private Scene scene;
	
	private boolean connected = false;
	  
	BigTwoGame bigTwoGame;
	Handler handler;
	Font font;
	  
	private ITexture playButtonTexture;
	private ITexture playPressedButtonTexture;
	private ITextureRegion playButtonTextureRegion;
	private ITextureRegion playPressedButtonTextureRegion;
	private ITexture cardDeckTexture;
	
	private CardTextureMap cardTextureMap;
	CardViews cardViews;
	int[] cardXLocations = new int[13];
	  
	Sprite playButton;
	  
	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) {
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
	  
	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws IOException {
		this.cardDeckTexture = new AssetBitmapTexture(this.getTextureManager(), this.getAssets(), "bigtwodeck_small.png", TextureOptions.BILINEAR);
		this.cardDeckTexture.load();
		this.cardTextureMap = new CardTextureMap();
		for (int i = 0; i < 13; i++) {
			for (int j = 0; j < 4; j++) {
				Card c = new Card(j, i);
				int x = Constants.CARD_WIDTH * i;	
				int y = Constants.CARD_HEIGHT * j;
				final ITextureRegion cardTextureRegion = TextureRegionFactory.extractFromTexture(this.cardDeckTexture, x, y, Constants.CARD_WIDTH, Constants.CARD_HEIGHT);
				this.cardTextureMap.setTextureRegion(c, cardTextureRegion);				  
			}
		}
		  
		bigTwoGame = new BigTwoGame(handler);
		 
		// Must set playarea dimensions before creating cardviews because it relies on it
		bigTwoGame.setPlayAreaX(Constants.CAMERA_WIDTH / 2 - 200);
		bigTwoGame.setPlayAreaY((Constants.CAMERA_HEIGHT / 2 - 25) - 50);
		bigTwoGame.setPlayAreaWidth(400);
		bigTwoGame.setPlayAreaHeight(200); 
		 
		cardViews = new CardViews(bigTwoGame);		
		bigTwoGame.setCardViews(cardViews);
		 
		 
		this.font = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 8);
		this.font.load();
		 
		loadButtonTextures();
	    pOnCreateResourcesCallback.onCreateResourcesFinished();
	}
	  
	@Override
	 public EngineOptions onCreateEngineOptions() {
		 handler = new Handler() {
			 @Override
			 public void handleMessage(Message msg) {
				 switch (msg.what) {
				 	case Constants.UPDATE_VIEW:
				 		Log.d(TAG, "Update view message received");
				 		writeText();
					  	break;
				 	case Constants.RECEIVED_HAND:
				 		Log.d(TAG, "Received RECEIVED_HAND message.");
				 		setupHand();
				 		writeText();
				 		break;
				 	case Constants.RECEIVED_PLAY:
				 		processReceivedPlay();
				 		break;
				 }
				 super.handleMessage(msg);
			 }
		};
		final Camera camera = new Camera(0, 0, Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
        
		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR, 
	        new RatioResolutionPolicy(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT), camera);
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) {
	    scene = new Scene();
		scene.getBackground().setColor(Color.GREEN);
		scene.setTouchAreaBindingOnActionDownEnabled(true);
		 
		Rectangle myRectangle = new Rectangle(Constants.CAMERA_WIDTH / 2, Constants.CAMERA_HEIGHT / 2 - 25, 400, 100, this.getVertexBufferObjectManager()); 
		 
		// create sprites in cardview here
		playButton = new Sprite(Constants.CAMERA_WIDTH - 45, Constants.CAMERA_HEIGHT - 25, playButtonTextureRegion, this.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(final TouchEvent pAreaTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				switch(pAreaTouchEvent.getAction()) {
					case TouchEvent.ACTION_DOWN:
						if (!connected) {
							bigTwoGame.connectToServer();
				 			connected = true;
				 		} else {
				 			playPressed();				 			
				 		}
						break;
				}
				return true;
			}
		};
 

		this.scene.attachChild(playButton);
		this.scene.attachChild(myRectangle);
		this.scene.registerTouchArea(playButton);
		pOnCreateSceneCallback.onCreateSceneFinished(scene);
	}	 
	protected void loadButtonTextures() throws IOException {
		playButtonTexture = new AssetBitmapTexture(this.getTextureManager(), getAssets(), "play.png");
		playButtonTextureRegion = TextureRegionFactory.extractFromTexture(playButtonTexture);
		playButtonTexture.load();
		  
		playPressedButtonTexture = new AssetBitmapTexture(this.getTextureManager(), getAssets(), "play_pressed.png");
		playPressedButtonTextureRegion = TextureRegionFactory.extractFromTexture(playPressedButtonTexture);
		playPressedButtonTexture.load();
	}
	 
	protected void setupHand() {
		 
		Hand hand = bigTwoGame.getHand();
		//hand.sort();
		ArrayList<Card> cards = hand.getCards();
		int x = (Constants.CARD_WIDTH / 2) + 4, y = (Constants.CARD_HEIGHT / 2) + 5;
		int i = 0;
		 
		cardViews.computeXLocations(62, 13);
		for (Card c : cards) {
			ITextureRegion region = cardTextureMap.getTextureRegion(c);
			cardViews.createAddCardView(x, y, c, region, this.getVertexBufferObjectManager(), scene);		 
			x += 62;
			i++;
		}
	}
	 
	protected void processReceivedPlay() {
        	 
		Log.d(TAG, "Received RECEIVED_PLAY message");
 		ArrayList<Card> currentPlayCards = new ArrayList<Card>();
 		// topOfStack was set when play message was received, that prompted this 
 		// message to be received by view
 		Play currentPlay = bigTwoGame.getTopOfStack();
 		for (int i = 0; i < currentPlay.getCardsInPlay(); i++) {
 			currentPlayCards.add(currentPlay.getCard(i));
 		}
 		cardViews.setCurrentPlayCards(currentPlayCards);
 		cardViews.createCurrentPlayCardViews(cardTextureMap, MainActivity.this.getVertexBufferObjectManager(), scene);
 		writeText();
	}
	
	protected void writeText() {
        int widthOfText = (int) FontUtils.measureText(font, "You are player number " + bigTwoGame.getPlayerNumber());
		Text playerNumberText = new Text(widthOfText / 2, Constants.CAMERA_HEIGHT - 10, this.font, "You are player number " + bigTwoGame.getPlayerNumber(), 
	 				new TextOptions(HorizontalAlign.LEFT), this.getVertexBufferObjectManager());
		 
		widthOfText = (int) FontUtils.measureText(font, "It is player " + bigTwoGame.getPlayersTurn() + "'s turn");
		Text currentPlayerText = new Text(widthOfText / 2, Constants.CAMERA_HEIGHT - 20, this.font, "It is player " + bigTwoGame.getPlayersTurn() + "'s turn", 
	 				new TextOptions(HorizontalAlign.LEFT), this.getVertexBufferObjectManager());
		
		Play currentPlay = bigTwoGame.getTopOfStack();
		if (currentPlay != null) {
			Text topOfStackText = new Text(Constants.CAMERA_WIDTH / 2, bigTwoGame.getPlayAreaY() + bigTwoGame.getPlayAreaHeight() + 5, this.font,
 				currentPlay.getFriendlyName() + " with high card " + currentPlay.getHighestCard().toString(), 
 				new TextOptions(HorizontalAlign.LEFT), this.getVertexBufferObjectManager());
			this.scene.attachChild(topOfStackText);
		}
		this.scene.attachChild(playerNumberText);
		this.scene.attachChild(currentPlayerText);
	}
	
	protected void playPressed() {
		ArrayList<Card> toPlayCards = cardViews.getToPlayCards();
		Play play = new Play(toPlayCards);
		switch (bigTwoGame.makePlay(play)) {
			case Constants.OK_PLAY:
				Log.d(TAG, "Making play");
				MainActivity.this.toastOnUiThread("Play OK", Toast.LENGTH_SHORT);
				break;
			case Constants.NOT_LOWEST:
				MainActivity.this.toastOnUiThread("You must play the lowest play in your hand!", Toast.LENGTH_SHORT);
				break;
			case Constants.NOT_GREATER:
				MainActivity.this.toastOnUiThread("Your play does not beat the current play!", Toast.LENGTH_SHORT);
				break;
			case Constants.NOT_YOUR_TURN:
				MainActivity.this.toastOnUiThread("It isn't your turn!", Toast.LENGTH_SHORT);
				break;
			case Constants.CANT_PASS:
				MainActivity.this.toastOnUiThread("Can't pass if you're the first player!", Toast.LENGTH_SHORT);
				break;
			case Constants.INVALID_PLAY:
				MainActivity.this.toastOnUiThread("Not a valid play!", Toast.LENGTH_SHORT);
				break;
		}
	}
}
