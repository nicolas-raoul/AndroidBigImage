package fr.free.nrw.androidbigimage;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;

public class AndroidBigImageView extends ImageView {
	private SizeCallBack callBack;
	private Handler handle;
	private Runnable cbkAction;
	private int width;
	private int height;
	
	public AndroidBigImageView(Context context) {
		super(context);
		cbkAction = new Runnable(){
			public void run(){
				if(callBack != null)
					callBack.onSizeChanged(width, height);
			}
		};
	}

	public AndroidBigImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		cbkAction = new Runnable(){
			public void run(){
				if(callBack != null)
					callBack.onSizeChanged(width, height);
			}
		};
	}

	public AndroidBigImageView(Context context, AttributeSet attrs, int defStyle) {
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