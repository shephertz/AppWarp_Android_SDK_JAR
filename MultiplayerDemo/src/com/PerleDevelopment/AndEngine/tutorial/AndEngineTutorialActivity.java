package com.PerleDevelopment.AndEngine.tutorial;

import java.util.HashMap;
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

public class AndEngineTutorialActivity extends SimpleBaseGameActivity implements IOnSceneTouchListener{

	public static int CAMERA_WIDTH = 480;
	public static int CAMERA_HEIGHT = 800;

	private Camera mCamera;
	private Scene mMainScene;

	private BitmapTextureAtlas mBitmapTextureAtlas1;
	private BitmapTextureAtlas mBitmapTextureAtlas2;
	private BitmapTextureAtlas mBitmapTextureAtlas3;
	private BitmapTextureAtlas mBitmapTextureAtlas4;
	private TiledTextureRegion mPlayerTiledTextureRegion1;
	private TiledTextureRegion mPlayerTiledTextureRegion2;
	private TiledTextureRegion mPlayerTiledTextureRegion3;
	private TiledTextureRegion mPlayerTiledTextureRegion4;
	
	
	private RepeatingSpriteBackground mGrassBackground;
	
	private WarpClient theClient;
	private EventHandler eventHandler = new EventHandler(this);
	private Random ramdom = new Random();

	private HashMap<String, User> userMap = new HashMap<String, User>();
	
	private String roomId = "";
	
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
		this.mPlayerTiledTextureRegion1 = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas1, this, "monster1.png", 0, 0, 1, 1);
		this.mPlayerTiledTextureRegion2 = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas2, this, "monster2.png", 0, 0, 1, 1);
		this.mPlayerTiledTextureRegion3 = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas3, this, "monster3.png", 0, 0, 1, 1);
		this.mPlayerTiledTextureRegion4 = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas4, this, "monster4.png", 0, 0, 1, 1);
		this.mBitmapTextureAtlas1.load();
		this.mBitmapTextureAtlas2.load();
		this.mBitmapTextureAtlas3.load();
		this.mBitmapTextureAtlas4.load();
		Intent intent = getIntent();
		roomId = intent.getStringExtra("roomId");
		getRoomData(roomId);
	}

	@Override
	protected Scene onCreateScene() {
//		this.mEngine.registerUpdateHandler(new FPSLogger()); // logs the frame rate

		/* Create Scene and set background colour to (1, 1, 1) = white */
		this.mMainScene = new Scene();
		this.mMainScene.setBackground(mGrassBackground);

		/* Centre the player on the camera. */
		final float centerX = (CAMERA_WIDTH - this.mPlayerTiledTextureRegion1.getWidth()) / 2;
		final float centerY = (CAMERA_HEIGHT - this.mPlayerTiledTextureRegion1.getHeight()) / 2;
		this.mMainScene.setOnSceneTouchListener(this);
		return this.mMainScene;
	}

	
	
	private void getRoomData(String roomId){
		if(theClient!=null){
			theClient.addRoomRequestListener(eventHandler);
			theClient.addNotificationListener(eventHandler);
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
			object.put("X", xCord+"");
			object.put("Y", yCord+"");
			theClient.sendChat(object.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
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
			sendUpdateEvent(x, y);
			updateMove(Utils.userName, x, y);
		}
		return false;
	}
	public void updateMove(String userName, float x, float y){
		if(userMap.get(userName)!=null){
			Sprite sprite = userMap.get(userName).getSprite();
			sprite.registerEntityModifier(new MoveModifier(1, sprite.getX(), x, sprite.getY(), y));
		}
	}
	@Override
	public void onBackPressed() {
		if(theClient!=null){
			theClient.leaveRoom(roomId);
			handleLeave(Utils.userName);
			theClient.unsubscribeRoom(roomId);
			theClient.removeRoomRequestListener(eventHandler);
			theClient.removeNotificationListener(eventHandler);
			theClient.disconnect();
		}
		super.onBackPressed();
	}
	public void clearResources(){
		this.mBitmapTextureAtlas1.unload();
		this.mBitmapTextureAtlas2.unload();
		this.mBitmapTextureAtlas3.unload();
		this.mBitmapTextureAtlas4.unload();
		this.mMainScene.dispose();
	}
}