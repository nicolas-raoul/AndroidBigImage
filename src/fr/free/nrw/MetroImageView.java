package com.jbdubois.metro;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.jbdubois.metro.SizeCallBack;

public class MetroImageView extends ImageView {
	SizeCallBack callBack;
	Handler handle;
	Runnable cbkAction;
	int width, height;
	
	
	public MetroImageView(Context context) {
		super(context);
		cbkAction = new Runnable(){
			public void run(){
				if(callBack != null)
					callBack.onSizeChanged(width, height);
			}
		};
	}

	public MetroImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		cbkAction = new Runnable(){
			public void run(){
				if(callBack != null)
					callBack.onSizeChanged(width, height);
			}
		};
	}

	public MetroImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		cbkAction = new Runnable(){
			public void run(){
				if(callBack != null)
					callBack.onSizeChanged(width, height);
			}
		};
	}
	
	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh){
		super.onSizeChanged(w, h, oldw, oldh);
		width = w;
		height = h;
		if(handle != null)
			handle.post(cbkAction);
	}
	
	public void setCallBack(SizeCallBack cbk){
		callBack = cbk;
	}
	
	public void setHandle(Handler h){
		handle = h;
	}

}
