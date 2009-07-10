package com.jbdubois.metro;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.view.View.OnClickListener;
import android.view.MotionEvent;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.jbdubois.metro.AnimationMetro;
import com.jbdubois.metro.AnimationCallBack;
import java.util.Timer;
import android.os.Handler;

public class metro extends Activity {
	MetroImageView 	metroImage;
	ImageButton	zoomInButton;
	ImageButton	zoomOutButton;
	Matrix 		metroMatrix;
	RectF 		sourceRect;
	RectF 		destinationRect;
	Bitmap		planBitmap;
	Timer		metroSchedule;
	AnimationMetro animation;
	Handler handle = new Handler();
	
	public static final int Menu1 = Menu.FIRST + 1;
    public static final int Menu2 = Menu.FIRST + 2;
    public static final int Menu3 = Menu.FIRST + 3;
	
    int		imageSizeX = 2047;
	int		imageSizeY = 2047;
	static float 	INITIAL_SCALE = (float)1;
	static float 	MAGNIFY_SCALE = (float)1.9;
	
	float	current_scale = INITIAL_SCALE;
	int		current_centerX = imageSizeX/2;
	int 	current_centerY = imageSizeY/2;
	int		current_drawable = R.drawable.metro;
	
	int		moveHistorySize;
	float	lastTwoXMoves[] = new float[2];
	float	lastTwoYMoves[] = new float[2];
	float	downPosX;
	float	downPosY;
	long	downTimer;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
        setContentView(R.layout.main);
        metroImage = (MetroImageView)findViewById(R.id.metroImage);
        zoomInButton = (ImageButton)findViewById(R.id.zoomIn);
        zoomOutButton = (ImageButton)findViewById(R.id.zoomOut);
               
        sourceRect = new RectF();
        destinationRect = new RectF();
        metroMatrix = new Matrix();
        
        if(savedInstanceState != null){
        	current_centerX = savedInstanceState.getInt("centerX");
        	current_centerY = savedInstanceState.getInt("centerY");
        	current_scale = savedInstanceState.getFloat("scale");
        	current_drawable = savedInstanceState.getInt("drawable");
        	imageSizeX = savedInstanceState.getInt("sizeX");
        	imageSizeY = savedInstanceState.getInt("sizeY");
        }
        
        metroSchedule = new Timer();
        animation = new AnimationMetro(handle, current_centerX, current_centerY, current_scale);
        
        metroImage.setHandle(handle);
        metroImage.setCallBack(sizecallback);
        
        animation.stopProcess();
        animation.setCallBack(animateMetro);
        metroSchedule.scheduleAtFixedRate(animation, 200, 30);
        
        metroImage.setOnTouchListener(metroListener);
        zoomInButton.setOnClickListener(metroZoomInListener);
        zoomOutButton.setOnClickListener(metroZoomOutListener);
        
        planBitmap = BitmapFactory.decodeResource(getResources(), current_drawable);
    	metroImage.setImageBitmap(planBitmap);
    	metroImage.getDrawable().setFilterBitmap(true);
    	metroImage.setImageMatrix(metroMatrix);
    }
    
    @Override
    public void onRestoreInstanceState(Bundle inState){
    	current_centerX = inState.getInt("centerX");
    	current_centerY = inState.getInt("centerY");
    	current_scale = inState.getFloat("scale");
    	current_drawable = inState.getInt("drawable");
    	imageSizeX = inState.getInt("sizeX");
    	imageSizeY = inState.getInt("sizeY");
    }
    
    @Override
    public void  onSaveInstanceState(Bundle outState){
    	super.onSaveInstanceState(outState);
    	outState.putInt("centerX", current_centerX);
    	outState.putInt("centerY", current_centerY);
    	outState.putFloat("scale", current_scale);
    	outState.putInt("drawable", current_drawable);
    	outState.putInt("sizeX", imageSizeX);
    	outState.putInt("sizeY", imageSizeY);
    }
    
    public void onDestroy()
    {    	
    	if(!planBitmap.isRecycled())
    		planBitmap.recycle();
    	
    	super.onDestroy();
    }
    
    /** hook into menu button for activity */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      populateMenu(menu);
      return super.onCreateOptionsMenu(menu);
    }
    /** when menu button option selected */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      return applyMenuChoice(item) || super.onOptionsItemSelected(item);
    }
    
    private OnTouchListener metroListener = new OnTouchListener() {
    	public boolean onTouch(View v, MotionEvent event) {
    		
    		if((event.getAction() == MotionEvent.ACTION_MOVE)){
    			
    			moveHistorySize++;
    			lastTwoXMoves[1] = lastTwoXMoves[0];
    			lastTwoXMoves[0] = event.getX();
    			lastTwoYMoves[1] = lastTwoYMoves[0];
    			lastTwoYMoves[0] = event.getY();
    			
    			if(moveHistorySize >= 2){
	    			current_centerX += (int)((lastTwoXMoves[1] - lastTwoXMoves[0]) * (imageSizeX / current_scale) / metroImage.getWidth());
	    			current_centerY += (int)((lastTwoYMoves[1] - lastTwoYMoves[0]) * (imageSizeY / current_scale) / metroImage.getHeight());
		    		
		    		updateDisplay();
    			}
    		}
    		else if(event.getAction() == MotionEvent.ACTION_DOWN){
    			animation.stopProcess();
    			lastTwoXMoves[0] = event.getX();
    			lastTwoYMoves[0] = event.getY();
    			downPosX = event.getX();
    			downPosY = event.getY();
    			downTimer = event.getEventTime();
    			moveHistorySize = 1;
    		}
    		else if((event.getAction() == MotionEvent.ACTION_UP) && (moveHistorySize >= 1)) {
    			
    			if(event.getEventTime() != downTimer){
	    			float speedX = (lastTwoXMoves[1] - lastTwoXMoves[0]) * (imageSizeX / current_scale) / metroImage.getWidth();
	    			float speedY = (lastTwoYMoves[1] - lastTwoYMoves[0]) * (imageSizeY / current_scale) / metroImage.getHeight();
	    			
	    			speedX /= event.getEventTime() - downTimer;
	    			speedY /= event.getEventTime() - downTimer;
	    			
	    			speedX *= 30;
	    			speedY *= 30;
	    			
	    			animation.setInfo(speedX, speedY, current_centerX, current_centerY);
    			}
    		}
    		
    		return true;
    	}
    };
    
    private OnClickListener metroZoomInListener = new OnClickListener() { 
    	public void onClick(View v) {
    		animation.stopProcess();
    		
    		if(current_scale <= 5){
    			animation.setScaleInfo(current_scale, current_scale * MAGNIFY_SCALE);
    		}
    		
    	}
    };
    
    private OnClickListener metroZoomOutListener = new OnClickListener() { 
    	public void onClick(View v) {
    		animation.stopProcess();
    		
    		if(current_scale >= MAGNIFY_SCALE * INITIAL_SCALE){
    			animation.setScaleInfo(current_scale, current_scale / MAGNIFY_SCALE);
    		}
    		else if((current_scale > INITIAL_SCALE)){
    			animation.setScaleInfo(current_scale, INITIAL_SCALE);
    		}
    	}
    };
    
    private AnimationCallBack animateMetro = new AnimationCallBack() {
    	public void onTimer(int centerX, int centerY, float scale){
			current_centerX = centerX;
			current_centerY = centerY;
			current_scale = scale;
			
			updateDisplay();
    	}
    };
    
    private SizeCallBack sizecallback = new SizeCallBack() {
    	public void onSizeChanged(int w, int h){
    		destinationRect.set((float)0, (float)0, (float)w, (float)h);
    		updateDisplay();
    	}
    };
    
    private void updateDisplay(){
    	calculateSourceRect(current_centerX, current_centerY, current_scale);
		metroMatrix.setRectToRect(sourceRect, destinationRect, Matrix.ScaleToFit.FILL);
		metroImage.setImageMatrix(metroMatrix);
    }
    
    private void calculateSourceRect(int centerX, int centerY, float scale){
    	int xSubValue;
    	int ySubValue;
    	
    	if(destinationRect.bottom >= destinationRect.right){
	    	ySubValue = (int)((imageSizeY/2) / scale);
	    	xSubValue = ySubValue;
	    	
	    	xSubValue = (int) (xSubValue * ((float)metroImage.getWidth() / (float)metroImage.getHeight()));
    	}else
    	{
    		xSubValue = (int)((imageSizeX/2) / scale);
	    	ySubValue = xSubValue;
	    	
	    	ySubValue = (int) (ySubValue * ((float)metroImage.getHeight() / (float)metroImage.getWidth()));
    	}
    	
    	if(centerX - xSubValue < 0) {
    		animation.stopProcess();
    		centerX = xSubValue;
    	}
    	if(centerY - ySubValue < 0) {
    		animation.stopProcess();
    		centerY = ySubValue;
    	}
    	if(centerX + xSubValue >= imageSizeX) {
    		animation.stopProcess();
    		centerX = imageSizeX - xSubValue - 1;
    	}
    	if(centerY + ySubValue >= imageSizeY) {
    		animation.stopProcess();
    		centerY = imageSizeY - ySubValue - 1;
    	}
    	
    	current_centerX = centerX;
    	current_centerY = centerY;
    	
    	sourceRect.set(centerX - xSubValue, centerY - ySubValue, centerX + xSubValue, centerY + ySubValue);
    }

    public void populateMenu(Menu menu) {
      menu.setQwertyMode(true);

      MenuItem item1 = menu.add(0, Menu1, 0, "Metro");
      item1.setAlphabeticShortcut('m');
      item1.setIcon(R.drawable.icon);

      MenuItem item2 = menu.add(0, Menu2, 0, "RER");
      item2.setAlphabeticShortcut('r');
      item2.setIcon(R.drawable.rerlogo);
      
      MenuItem item3 = menu.add(0, Menu3, 0, "Noctilien");
      item3.setAlphabeticShortcut('n');
      item3.setIcon(R.drawable.noctilienlogo);

    }
    
    public boolean applyMenuChoice(MenuItem item){
    	switch (item.getItemId()) {
    		case Menu1 : 
    			setNewDrawable(R.drawable.metro);
    			break;
    		case Menu2 : 
    			setNewDrawable(R.drawable.rer);
    			break;
    		case Menu3 : 
    			setNewDrawable(R.drawable.noctilien);
    			break;
    		default: break;
    	}

    	return true;
    }
    
    public void setNewDrawable(int resId){
    	current_drawable = resId;
    	planBitmap.recycle();
    	planBitmap = BitmapFactory.decodeResource(getResources(), resId);
    	metroImage.setImageBitmap(planBitmap);
    	metroImage.getDrawable().setFilterBitmap(true);
    	
    	current_scale = INITIAL_SCALE;
    	imageSizeX = planBitmap.getWidth();
    	imageSizeY = planBitmap.getHeight();
    	current_centerX = imageSizeX/2;
    	current_centerY = imageSizeY/2;
    	
    	animation.setInfo(0, 0, current_centerX, current_centerY);
    	animation.setScaleInfo(current_scale, current_scale);
    	
    	updateDisplay();
    }
}