package com.appwarp.multiplayer.tutorial.object;

import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.appwarp.multiplayer.tutorial.AndEngineTutorialActivity;

public class Player extends GameObject {

	private int fiXBaseVelocity = 100;
	private int baseVelocity = 100;
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	public Player(final float pX, final float pY, final TiledTextureRegion pTiledTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void move() {

		this.mPhysicsHandler.setVelocityX(baseVelocity);

		OutOfScreenX();
	}

	// ===========================================================
	// Methods
	// ===========================================================

	private void OutOfScreenX() {
		if (mX > AndEngineTutorialActivity.CAMERA_WIDTH) { // OutOfScreenX (right)
			baseVelocity = -fiXBaseVelocity;
		} else if (mX < 0) { // OutOfScreenX (left)
			baseVelocity = fiXBaseVelocity;
		}
	}
}
