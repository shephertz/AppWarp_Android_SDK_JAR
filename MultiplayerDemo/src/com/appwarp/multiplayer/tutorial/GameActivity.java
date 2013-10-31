package com.appwarp.multiplayer.tutorial;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Random;

import org.andengine.engine.Engine.EngineLock;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.RepeatingSpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.source.AssetBitmapTextureAtlasSource;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.json.JSONObject;

import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;

import com.shephertz.app42.gaming.multiplayer.client.WarpClient;

public class GameActivity extends SimpleBaseGameActivity implements IOnSceneTouchListener{

	public static int CAMERA_WIDTH = 480;
	public static int CAMERA_HEIGHT = 800;

	private Camera mCamera;
	private Scene mMainScene;

	private BitmapTextureAtlas mBitmapTextureAtlas1;
	private BitmapTextureAtlas mBitmapTextureAtlas2;
	private BitmapTextureAtlas mBitmapTextureAtlas3;
	private BitmapTextureAtlas mBitmapTextureAtlas4;
	
	private BitmapTextureAtlas fruitBitmapTextureAtlas1;
	private BitmapTextureAtlas fruitBitmapTextureAtlas2;
	private BitmapTextureAtlas fruitBitmapTextureAtlas3;
	private BitmapTextureAtlas fruitBitmapTextureAtlas4;
	
	private BitmapTextureAtlas coinBitmapTextureAtlas;
	
	private TiledTextureRegion mPlayerTiledTextureRegion1;
	private TiledTextureRegion mPlayerTiledTextureRegion2;
	private TiledTextureRegion mPlayerTiledTextureRegion3;
	private TiledTextureRegion mPlayerTiledTextureRegion4;
	
	private TiledTextureRegion mFruitTiledTextureRegion1;
	private TiledTextureRegion mFruitTiledTextureRegion2;
	private TiledTextureRegion mFruitTiledTextureRegion3;
	private TiledTextureRegion mFruitTiledTextureRegion4;
	
	private TiledTextureRegion mCoinTiledTextureRegion;
	
	private RepeatingSpriteBackground mGrassBackground;
	
	private WarpClient theClient;
	private EventHandler eventHandler = new EventHandler(this);
	private Random ramdom = new Random();

	private HashMap<String, User> userMap = new HashMap<String, User>();
	
	private HashMap<String, Sprite> objectMap = new HashMap<String, Sprite>();
	
	private String roomId = "";
	
	private int selectedFruit=-1;
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		try{
			theClient = WarpClient.getInstance();
		}catch(Exception e){
			e.printStackTrace();
		}
		userMap.clear();
		final DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        CAMERA_WIDTH = displayMetrics.widthPixels;
        CAMERA_HEIGHT = displayMetrics.heightPixels;
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new FillResolutionPolicy(), this.mCamera);
	}

	@Override
	protected void onCreateResources() {
		/* Load all the textures this game needs. */
		
		this.mGrassBackground = new RepeatingSpriteBackground(CAMERA_WIDTH, CAMERA_HEIGHT, this.getTextureManager(), AssetBitmapTextureAtlasSource.create(this.getAssets(), "background_grass.png"), this.getVertexBufferObjectManager());
		this.mBitmapTextureAtlas1 = new BitmapTextureAtlas(this.getTextureManager(), 32, 32);
		this.mBitmapTextureAtlas2 = new BitmapTextureAtlas(this.getTextureManager(), 32, 32);
		this.mBitmapTextureAtlas3 = new BitmapTextureAtlas(this.getTextureManager(), 32, 32);
		this.mBitmapTextureAtlas4 = new BitmapTextureAtlas(this.getTextureManager(), 32, 32);
		
		this.fruitBitmapTextureAtlas1 = new BitmapTextureAtlas(this.getTextureManager(), 128, 128);
		this.fruitBitmapTextureAtlas2 = new BitmapTextureAtlas(this.getTextureManager(), 128, 128);
		this.fruitBitmapTextureAtlas3 = new BitmapTextureAtlas(this.getTextureManager(), 128, 128);
		this.fruitBitmapTextureAtlas4 = new BitmapTextureAtlas(this.getTextureManager(), 128, 128);
		
		this.coinBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 128, 128);
		
		this.mPlayerTiledTextureRegion1 = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas1, this, "monster1.png", 0, 0, 1, 1);
		this.mPlayerTiledTextureRegion2 = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas2, this, "monster2.png", 0, 0, 1, 1);
		this.mPlayerTiledTextureRegion3 = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas3, this, "monster3.png", 0, 0, 1, 1);
		this.mPlayerTiledTextureRegion4 = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas4, this, "monster4.png", 0, 0, 1, 1);
		
		this.mFruitTiledTextureRegion1 = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.fruitBitmapTextureAtlas1, this, "fruit_banana_100.png", 0, 0, 1, 1);
		this.mFruitTiledTextureRegion2 = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.fruitBitmapTextureAtlas2, this, "fruit_grape_100.png", 0, 0, 1, 1);
		this.mFruitTiledTextureRegion3 = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.fruitBitmapTextureAtlas3, this, "fruit_pineapple_100.png", 0, 0, 1, 1);
		this.mFruitTiledTextureRegion4 = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.fruitBitmapTextureAtlas4, this, "fruit_strawberry_100.png", 0, 0, 1, 1);
		
		this.mCoinTiledTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.coinBitmapTextureAtlas, this, "coin-shrimp-sprite.png", 0, 0, 1, 1);
		
		this.mBitmapTextureAtlas1.load();
		this.mBitmapTextureAtlas2.load();
		this.mBitmapTextureAtlas3.load();
		this.mBitmapTextureAtlas4.load();
		
		this.fruitBitmapTextureAtlas1.load();
		this.fruitBitmapTextureAtlas2.load();
		this.fruitBitmapTextureAtlas3.load();
		this.fruitBitmapTextureAtlas4.load();
		
		this.coinBitmapTextureAtlas.load();
		
		Intent intent = getIntent();
		roomId = intent.getStringExtra("roomId");
		init(roomId);
	}

	@Override
	protected Scene onCreateScene() {
//		this.mEngine.registerUpdateHandler(new FPSLogger()); // logs the frame rate

		/* Create Scene and set background colour to (1, 1, 1) = white */
		this.mMainScene = new Scene();
		this.mMainScene.setBackground(mGrassBackground);

		//Adding fruit here
		
		final Sprite banana = new Sprite(0, CAMERA_HEIGHT/2-50*2, mFruitTiledTextureRegion1, this.getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				selectedFruit = Constants.BananaId;
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		banana.setSize(50, 50);
		this.mMainScene.registerTouchArea(banana);
		this.mMainScene.attachChild(banana);
		final Sprite grape = new Sprite(0, CAMERA_HEIGHT/2-50, mFruitTiledTextureRegion2, this.getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				selectedFruit = Constants.GrapeId;
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		grape.setSize(50, 50);
		this.mMainScene.registerTouchArea(grape);
		this.mMainScene.attachChild(grape);
		final Sprite pineapple = new Sprite(0, CAMERA_HEIGHT/2, mFruitTiledTextureRegion3, this.getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				selectedFruit = Constants.PinaappleId;
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		pineapple.setSize(50, 50);
		this.mMainScene.registerTouchArea(pineapple);
		this.mMainScene.attachChild(pineapple);
		final Sprite strawary = new Sprite(0, CAMERA_HEIGHT/2+50, mFruitTiledTextureRegion4, this.getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				selectedFruit = Constants.StrawaryId;
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		strawary.setSize(50, 50);
		this.mMainScene.registerTouchArea(strawary);
		this.mMainScene.attachChild(strawary);
		
		// Adding coin here
		
		final Sprite coin1 = new Sprite(0, 0, mCoinTiledTextureRegion, this.getVertexBufferObjectManager());
		this.mMainScene.attachChild(coin1);
		final Sprite coin2 = new Sprite(CAMERA_WIDTH-mCoinTiledTextureRegion.getWidth(), 0, mCoinTiledTextureRegion, this.getVertexBufferObjectManager());
		this.mMainScene.attachChild(coin2);
		final Sprite coin3 = new Sprite(0, CAMERA_HEIGHT-mCoinTiledTextureRegion.getHeight(), mCoinTiledTextureRegion, this.getVertexBufferObjectManager());
		this.mMainScene.attachChild(coin3);
		final Sprite coin4 = new Sprite(CAMERA_WIDTH-mCoinTiledTextureRegion.getWidth(), CAMERA_HEIGHT-mCoinTiledTextureRegion.getHeight(), mCoinTiledTextureRegion, this.getVertexBufferObjectManager());
		this.mMainScene.attachChild(coin4);
		
		
		this.mMainScene.setOnSceneTouchListener(this);
		return this.mMainScene;
	}

	
	
	private void init(String roomId){
		if(theClient!=null){
			theClient.addRoomRequestListener(eventHandler);
			theClient.addNotificationListener(eventHandler);
			Log.d(this.getClass().toString(), "Room Id is: "+roomId);
			theClient.subscribeRoom(roomId);
			theClient.getLiveRoomInfo(roomId);
		}
	}
	public void addMorePlayer(boolean isMine, String userName){
		if(userMap.get(userName)!=null){// if already in room
			return;
		}
		Log.d("userNameGame", userName);
		char index =  userName.charAt(userName.length()-1);
		TiledTextureRegion tiledTextureRegion = null;
		if(index=='1'){
			tiledTextureRegion = mPlayerTiledTextureRegion1;
		}else if(index=='2'){
			tiledTextureRegion = mPlayerTiledTextureRegion2;
		}else if(index=='3'){
			tiledTextureRegion = mPlayerTiledTextureRegion3;
		}else if(index=='4'){
			tiledTextureRegion = mPlayerTiledTextureRegion4;
		}
		final Sprite face = new Sprite(ramdom.nextInt(CAMERA_WIDTH), ramdom.nextInt(CAMERA_HEIGHT), tiledTextureRegion, this.getVertexBufferObjectManager());
		face.setScale(1.5f);
		this.mMainScene.attachChild(face);
		User user = new User(face.getX(), face.getY(), face);
		userMap.put(userName, user);
		if(isMine){
			this.mMainScene.setOnSceneTouchListener(this);
		}
	}
	private void sendUpdateEvent(float xCord, float yCord){
		try{
			JSONObject object = new JSONObject();
			float perX = Utils.getPercentFromValue(xCord, CAMERA_WIDTH);
			float perY = Utils.getPercentFromValue(yCord, CAMERA_HEIGHT);
			object.put("X", perX);
			object.put("Y", perY);
			theClient.sendChat(object.toString());
		}catch(Exception e){
			Log.d("sendUpdateEvent", e.getMessage());
		}
	}
	
	private void updateProperty(String position, String objectType){
		HashMap<String, Object> table = new HashMap<String, Object>();
		table.put(position, objectType);
		theClient.updateRoomProperties(roomId, table, null);
	}
	
	public void handleLeave(String name) {
		
		if(name.length()>0 && userMap.get(name)!=null){
			Sprite sprite = userMap.get(name).getSprite();
			final EngineLock engineLock = this.mEngine.getEngineLock();
			engineLock.lock();
			this.mMainScene.detachChild(sprite);
			sprite.dispose();
			sprite = null;
			userMap.remove(name);
			engineLock.unlock();
		}
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		if(pSceneTouchEvent.isActionUp()){
			float x = pSceneTouchEvent.getX();
			float y = pSceneTouchEvent.getY();
			checkForFruitMove(x, y);
			sendUpdateEvent(x, y);
			updateMove(false, Utils.userName, x, y);
		}
		return false;
	}
	private void checkForFruitMove(float x, float y){
		if(selectedFruit!=-1){
			if(x>0 && x<mFruitTiledTextureRegion1.getWidth() && y>0 && y<mFruitTiledTextureRegion1.getWidth()){
				placeObject(selectedFruit, "topLeft", null, true);
				return;
			}else if(x>CAMERA_WIDTH-mFruitTiledTextureRegion2.getWidth() && x<CAMERA_WIDTH && y>0 && y<mFruitTiledTextureRegion2.getWidth()){
				placeObject(selectedFruit, "topRight", null, true);
				return;
			}else if(x>0 && x<mFruitTiledTextureRegion3.getWidth() && y>CAMERA_HEIGHT-mFruitTiledTextureRegion3.getHeight() && y<CAMERA_HEIGHT){
				placeObject(selectedFruit, "bottomLeft", null, true);
				return;
			}else if(x>CAMERA_WIDTH-mFruitTiledTextureRegion4.getWidth() && x<CAMERA_WIDTH && y>CAMERA_HEIGHT-mFruitTiledTextureRegion4.getHeight() && y<CAMERA_HEIGHT){
				placeObject(selectedFruit, "bottomRight", null, true);
				return;
			}
		}
	}
	
	public synchronized void placeObject(final int selectedObject, final String destination, final String userName, boolean updateProperty){
		Sprite sprite=null;
		float xDest = 0;
		float yDest = 0;
		if(destination.equals("topLeft")){
			xDest = 0;
			yDest = 0;
		}else if(destination.equals("topRight")){
			xDest = CAMERA_WIDTH-mCoinTiledTextureRegion.getWidth();
			yDest = 0;
		}else if(destination.equals("bottomLeft")){
			xDest = 0;
			yDest = CAMERA_HEIGHT-mCoinTiledTextureRegion.getHeight();
		}else if(destination.equals("bottomRight")){
			xDest = CAMERA_WIDTH-mCoinTiledTextureRegion.getWidth();
			yDest = CAMERA_HEIGHT-mCoinTiledTextureRegion.getHeight();
		}else{
			return;
		}
		if(selectedObject==1){
			sprite = new Sprite(0, CAMERA_HEIGHT/2-50*2, mFruitTiledTextureRegion1, this.getVertexBufferObjectManager());
		}else if(selectedObject==2){
			sprite = new Sprite(0, CAMERA_HEIGHT/2-50, mFruitTiledTextureRegion2, this.getVertexBufferObjectManager());
		}else if(selectedObject==3){
			sprite = new Sprite(0, CAMERA_HEIGHT/2, mFruitTiledTextureRegion3, this.getVertexBufferObjectManager());
		}else if(selectedObject==4){
			sprite = new Sprite(0, CAMERA_HEIGHT/2+50, mFruitTiledTextureRegion4, this.getVertexBufferObjectManager());
		}else{
			return;
		}
		sprite.setSize(50, 50);
		if(objectMap.get(destination)!=null){// remove previous sprite if exist
			Sprite objectSprite = objectMap.get(destination);
			final EngineLock engineLock = this.mEngine.getEngineLock();
			engineLock.lock();
			this.mMainScene.detachChild(objectSprite);
			objectSprite.dispose();
			objectSprite = null;
			objectMap.remove(objectSprite);
			engineLock.unlock();
		}
		objectMap.put(destination, sprite);
		this.mMainScene.attachChild(sprite);
		sprite.registerEntityModifier(new MoveModifier(1, sprite.getX(), xDest, sprite.getY(), yDest));
		if(updateProperty){
			updateProperty(destination, selectedObject+"");
		}
		selectedFruit = -1;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(userName!=null){
					Utils.showToastAlert(GameActivity.this, userName+" has changed "+destination+" object");
				}
			}
		});
	}
	
	public void updateMove(boolean isRemote, String userName, float xPer, float yPer){
		if(userMap.get(userName)!=null){
			if(isRemote){
				xPer = Utils.getValueFromPercent(xPer, CAMERA_WIDTH);
				yPer = Utils.getValueFromPercent(yPer, CAMERA_HEIGHT);
			}
			Sprite sprite = userMap.get(userName).getSprite();
			float deltaX = sprite.getX() - xPer;
			float deltaY = sprite.getY() - yPer;
			float distance = (float) Math.sqrt((deltaX*deltaX)+(deltaY*deltaY));
			float time = distance/Constants.MonsterSpeed;
			sprite.registerEntityModifier(new MoveModifier(time, sprite.getX(), xPer, sprite.getY(), yPer));
		}
	}
	
	@Override
	public void onBackPressed() {
		if(theClient!=null){
			handleLeave(Utils.userName);
			theClient.leaveRoom(roomId);
			theClient.unsubscribeRoom(roomId);
			theClient.removeRoomRequestListener(eventHandler);
			theClient.removeNotificationListener(eventHandler);
		}
		super.onBackPressed();
	}
	public void clearResources(){
		this.mBitmapTextureAtlas1.unload();
		this.mBitmapTextureAtlas2.unload();
		this.mBitmapTextureAtlas3.unload();
		this.mBitmapTextureAtlas4.unload();
		this.fruitBitmapTextureAtlas1.unload();
		this.fruitBitmapTextureAtlas2.unload();
		this.fruitBitmapTextureAtlas3.unload();
		this.fruitBitmapTextureAtlas4.unload();
		this.mMainScene.dispose();
	}
}