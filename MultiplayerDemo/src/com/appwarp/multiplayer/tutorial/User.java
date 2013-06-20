package com.appwarp.multiplayer.tutorial;

import org.andengine.entity.sprite.Sprite;

public class User {
	
	private String name;
	private float x;
	private float y;
	private Sprite sprite;
	
	public User(float x, float y, Sprite sprite){
		this.x = x;
		this.y = y;
		this.sprite = sprite;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public float getX() {
		return x;
	}
	public void setX(float x) {
		this.x = x;
	}
	public float getY() {
		return y;
	}
	public void setY(float y) {
		this.y = y;
	}
	public Sprite getSprite(){
		return sprite;
	}
	
}
