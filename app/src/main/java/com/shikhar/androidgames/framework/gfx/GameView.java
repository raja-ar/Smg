package com.shikhar.androidgames.framework.gfx;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.shikhar.androidgames.mario.screens.LevelCompleteScreen;

class GameView extends SurfaceView implements SurfaceHolder.Callback {
   
	class GameThread extends Thread {
        /*
         * State-tracking constants
         */
       public static final int STATE_PAUSE = 2;
        public static final int STATE_READY = 3;
        public static final int STATE_RUNNING = 4;
         
        private int mCanvasWidth;
        private int mCanvasHeight;

        private long mLastTime;
        
        private Handler mHandler;

        private int mMode;
        private boolean mRun = false;
        private SurfaceHolder mSurfaceHolder;

 
        public GameThread(SurfaceHolder surfaceHolder, Context context,
                Handler handler) {
            // get handles to some important objects
            mSurfaceHolder = surfaceHolder;
            mHandler = handler;
            mContext = context;
          }


        public void doStart() {
            synchronized (mSurfaceHolder) {
            	// Initialize game here!
 
            	mLastTime = System.currentTimeMillis() + 100;
                setState(STATE_RUNNING);
            }
        }


        public void pause() {
            synchronized (mSurfaceHolder) {
                if (mMode == STATE_RUNNING) 
                	setState(STATE_PAUSE);
            }
        }

        public void unpause() {
            // Move the real time clock up to now
            synchronized (mSurfaceHolder) {
                mLastTime = System.currentTimeMillis() + 100;
            }
            setState(STATE_RUNNING);
            /*
            if (!thread.isAlive() && mMode == STATE_READY){
            	thread.start();
            	thread.setRunning(true);
            	setState(STATE_RUNNING);
            }
            */

         }

 

        @Override
        public void run() {
        	Rect dstRect = new Rect();
            while (mRun) {
                Canvas canvas = null;
                try {
                	canvas = mSurfaceHolder.lockCanvas();
                    synchronized (mSurfaceHolder) {
                     	if (mMode == STATE_RUNNING) updateGame();
                  	}
                }
                catch (Exception e){
                	e.printStackTrace();
                }
                finally {
                    // do this in a finally so that if an exception is thrown
                    // during the above, we don't leave the Surface in an
                    // inconsistent state
                    if (canvas != null) {
                    	canvas.getClipBounds(dstRect);
            			
            			if (game.getCurrentScreen() instanceof LevelCompleteScreen){
            				canvas.drawBitmap(((LevelCompleteScreen)game.getCurrentScreen()).getBitmap(), null,dstRect,null);
            			}else{
            				canvas.drawBitmap(framebuffer, null, dstRect, null);
            			}
            			
            			//canvas.drawBitmap(framebuffer, null, dstRect, null);
            			if (game.isSwitchingScreen()){
            				switchTime=-25;
            				game.setSwitchScreen(false);
            			}
            			if (switchTime <= 25) {
            				if (switchTime < 0) {
            					canvas.drawARGB(250 + 10*switchTime, 0, 0, 0);
            				} else if (switchTime > 0) {
            					canvas.drawARGB(250-10*switchTime, 0, 0, 0);
            				} else if (switchTime == 0) {
            					game.setScreen(game.getNextScreen());
            					canvas.drawARGB(250 , 0, 0, 0);
             				}
            				switchTime+=1;
            			}else{
            				game.setScreenTransitionActive(false);
            			}
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }


        public void setRunning(boolean b) {
            mRun = b;
        }


        public void setState(int mode) {
            synchronized (mSurfaceHolder) {
                setState(mode, null);
            }
        }

        public void setState(int mode, CharSequence message) {
            synchronized (mSurfaceHolder) {
                mMode = mode;
            }
        }

        /* Callback invoked when the surface dimensions change. */
        public void setSurfaceSize(int width, int height) {
            // synchronized to make sure these all change atomically
            synchronized (mSurfaceHolder) {
                mCanvasWidth = width;
                mCanvasHeight = height;
            }
        }

  

        private void updateGame() {
        	//// <DoNotRemove>
            long now = System.nanoTime();
            // Do nothing if mLastTime is in the future.
            // This allows the game-start to delay the start of the physics
            // by 100ms or whatever.
          //  if (mLastTime > now) 
            //	return;
            float deltaTime = (now - mLastTime) /  1000000000.0f;
            if (deltaTime > 0.06){
	            deltaTime = (float)0.06;
	        }
            mLastTime = now;
            if (switchTime>25){
				game.getCurrentScreen().update(deltaTime);
			}
			game.getCurrentScreen().paint(deltaTime);


            //// </DoNotRemove>
            
            /*
             * Why use mLastTime, now and elapsed?
             * Well, because the frame rate isn't always constant, it could happen your normal frame rate is 25fps
             * then your char will walk at a steady pace, but when your frame rate drops to say 12fps, without elapsed
             * your character will only walk half as fast as at the 25fps frame rate. Elapsed lets you manage the slowdowns
             * and speedups!
             */
        }
    }

    private Context mContext;

    private GameThread thread;
    
    AndroidGame game;
    
	Bitmap framebuffer;
	
    int switchTime=26;
    public GameView(AndroidGame game, Bitmap framebuffer) {
        super(game);
        this.game = game;
		this.framebuffer = framebuffer;
        // register our interest in hearing about changes to our surface
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        // create thread only; it's started in surfaceCreated()
        thread = new GameThread(holder, game, new Handler() {
            @Override
            public void handleMessage(Message m) {
            	// Use for pushing back messages.
            }
        });

        setFocusable(true); // make sure we get key events
        // set up a new game
        thread.setState(GameThread.STATE_READY);   	
    }


    public GameThread getThread() {
        return thread;
    }

  

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (!hasWindowFocus)
        	thread.pause();
        else
        	thread.unpause();
    }

    
    /* Callback invoked when the surface dimensions change. */
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
        thread.setSurfaceSize(width, height);
    }

    /*
     * Callback invoked when the Surface has been created and is ready to be
     * used.
     */
    public void surfaceCreated(SurfaceHolder holder) {
        // start the thread here so that we don't busy-wait in run()
        // waiting for the surface to be created
      	//thread.setState(GameThread.STATE_READY);
      		thread = new GameThread(holder, game, new Handler() {
    	           @Override
    	           public void handleMessage(Message m) {
    	           	// Use for pushing back messages.
    	           }
    	        });
    		thread.setState(GameThread.STATE_RUNNING);
    		thread.setRunning(true);  
    		thread.start();
    }

    /*
     * Callback invoked when the Surface has been destroyed and must no longer
     * be touched. WARNING: after this method returns, the Surface/Canvas must
     * never be touched again!
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
        // we have to tell thread to shut down & wait for it to finish, or else
        // it might touch the Surface after we return and explode
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }
    
	public void setBuffer(Bitmap bmp){
		framebuffer=bmp;
	}
	
}
