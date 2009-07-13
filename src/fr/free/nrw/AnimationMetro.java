package com.jbdubois.metro;

import java.util.TimerTask;
import com.jbdubois.metro.AnimationCallBack;
import android.os.Handler;
import java.lang.Runnable;

public class AnimationMetro extends TimerTask {
	
	AnimationCallBack callBack;
	Handler handle;
	Runnable cbkAction;
	float speedX;
	float speedY;
	float currentCenterX, currentCenterY;
	float current_scale, target_scale;
	boolean runningMove;
	boolean runningScale;
	boolean scaleDirectionPlus;
	
	static float SCALE_STEP = (float)0.04;
	static float MAX_SPEED = 100;
	static float END_SPEED_THRESHOLD = (float)4;
	static float SPEED_STEP = (float)1.1;
	
	public AnimationMetro(Handler handle, int current_centerX, int current_centerY, float currentScale) {
		super();
		speedX = 0;
		speedY = 0;
		runningMove = false;
		runningScale = false;
		this.currentCenterX = current_centerX;
		this.currentCenterY = current_centerY;
		this.current_scale = currentScale;
		this.handle = handle;
		cbkAction = new Runnable(){
			public void run(){
				callBack.onTimer((int)currentCenterX, (int)currentCenterY, current_scale);
			}
		};
	}

	@Override
	public void run() {
		if(runningMove == true){
			float speedAbs = speedX * speedX + speedY * speedY;
			
			if(speedAbs >= (MAX_SPEED * MAX_SPEED)){
				speedX = (speedX/speedAbs) * MAX_SPEED * MAX_SPEED;
				speedY = (speedY/speedAbs) * MAX_SPEED * MAX_SPEED;
			}
			
			if(speedAbs <= END_SPEED_THRESHOLD){
				runningMove = false;
			}
			else {
				currentCenterX += speedX;
				currentCenterY += speedY;
				handle.post(cbkAction);
				speedX /= SPEED_STEP;
				speedY /= SPEED_STEP;
			}
		}
		
		if(runningScale == true){
			if(scaleDirectionPlus == true){
				current_scale += SCALE_STEP*current_scale;
				if(current_scale >= target_scale){
					runningScale = false;
					current_scale = target_scale;
				}
				handle.post(cbkAction);
			}
			else{
				current_scale -= SCALE_STEP*current_scale;
				if(current_scale <= target_scale){
					runningScale = false;
					current_scale = target_scale;
				}
				handle.post(cbkAction);
			}
		}
	}
	
	public void setCallBack(AnimationCallBack cbk){
		callBack = cbk;
	}
	
	public void setInfo(float speedX_, float speedY_, int centerX_, int centerY_){
		speedX = speedX_;
		speedY = speedY_;
		currentCenterX = centerX_;
		currentCenterY = centerY_;
		runningMove = true;
	}
	
	public void setScaleInfo(float current_scale, float target_scale){
		this.current_scale = current_scale;
		this.target_scale = target_scale;
		
		if(current_scale < target_scale){
			scaleDirectionPlus = true;
		}
		else{
			scaleDirectionPlus = false;
		}
		
		runningScale = true;
	}
	
	public void stopProcess(){
		runningMove = false;
	}

}
