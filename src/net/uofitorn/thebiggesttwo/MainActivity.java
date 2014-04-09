package net.uofitorn.thebiggesttwo;

import java.io.IOException;
import java.util.ArrayList;

import net.uofitorn.thebiggestbigtwo.common.Card;
import net.uofitorn.thebiggestbigtwo.common.Hand;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.bitmap.AssetBitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.adt.color.Color;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class MainActivity extends BaseGameActivity {

	public static final String TAG = "MainActivity";
	private static final int CAMERA_WIDTH = 800;  // 0.6
	private static final int CAMERA_HEIGHT = 480;
	private static final int CARD_WIDTH = 48;    // 71
	private static final int CARD_HEIGHT = 65;   // 96
	
	private Camera camera;
	private Scene scene;
	
	private boolean connected = false;
	  
	BigTwoGame bigTwoGame;
	Handler handler;
	  
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
				int x = CARD_WIDTH * i;	
				int y = CARD_HEIGHT * j;
				final ITextureRegion cardTextureRegion = TextureRegionFactory.extractFromTexture(this.cardDeckTexture, x, y, CARD_WIDTH, CARD_HEIGHT);
				this.cardTextureMap.setTextureRegion(c, cardTextureRegion);				  
			 }
		 }
		  
		 bigTwoGame = new BigTwoGame(handler);
		 cardViews = new CardViews(bigTwoGame);
		  
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
					  	break;
				 	case Constants.RECEIVED_HAND:
				 		Log.d(TAG, "Received RECEIVED_HAND message.");
				 		setupHand();
				 		break;
				 }
				 super.handleMessage(msg);
			 }
		 };
		 final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		 return new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR, 
	        new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
	 }

	 @Override
	 public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) {
		 scene = new Scene();
		 scene.getBackground().setColor(Color.GREEN);
		 scene.setTouchAreaBindingOnActionDownEnabled(true);
		 
		 Rectangle myRectangle = new Rectangle(CAMERA_WIDTH / 2, CAMERA_HEIGHT / 2 + 26, 400, 200, this.getVertexBufferObjectManager());
		 bigTwoGame.setPlayAreaX(CAMERA_WIDTH / 2 - 200);
		 bigTwoGame.setPlayAreaY((CAMERA_HEIGHT / 2 + 26) - 100);
		 bigTwoGame.setPlayAreaWidth(400);
		 bigTwoGame.setPlayAreaHeight(200);

		 // create sprites in cardview here
		 playButton = new Sprite(CAMERA_WIDTH - 45, CAMERA_HEIGHT - 25, playButtonTextureRegion, this.getVertexBufferObjectManager()) {
			 @Override
			 public boolean onAreaTouched(final TouchEvent pAreaTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				 switch(pAreaTouchEvent.getAction()) {
				 	case TouchEvent.ACTION_DOWN:
				 		Log.d(TAG, "Touch event detected.");
				 		if (!connected) {
				 			bigTwoGame.connectToServer();
				 			connected = true;
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
		 int x = (CARD_WIDTH / 2) + 4, y = (CARD_HEIGHT / 2) + 5;
		 int i = 0;
		 
		 cardViews.computeXLocations(CAMERA_WIDTH, 62, 13);
		 for (Card c : cards) {
			 //cardXLocations[i] = x;
			 ITextureRegion region = cardTextureMap.getTextureRegion(c);
			 cardViews.createAddCardView(x, y, c, region, this.getVertexBufferObjectManager(), scene);		 
			 x += 62;
			 i++;
		 }
	 }
}
