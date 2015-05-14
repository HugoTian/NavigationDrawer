/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.navigationdrawerexample;

import static android.provider.BaseColumns._ID;
import static com.example.android.navigationdrawerexample.DatabaseConstants.CODE_CONTENT;
import static com.example.android.navigationdrawerexample.DatabaseConstants.CODE_LINK;
import static com.example.android.navigationdrawerexample.DatabaseConstants.CODE_TITLE;
import static com.example.android.navigationdrawerexample.DatabaseConstants.TABLE_NAME;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity  implements PictureCallback, SurfaceHolder.Callback{
	//initialization
	public static int width,height;
    
	// drawer general layout
	private int itemselection = 0;
	private int previous_selection = 0;
	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] funtionTitles;

    
    //camera taking variable

	  private static Camera mCamera = Camera.open();     
	  private ImageView mCameraImage;
	  private SurfaceView mCameraPreview;
	  private static Camera.Parameters camParams  ;
	  private int minTime,maxTime;
	  private int exp;
	  private int Iso = 800;
	  private boolean mIsCapturing;
	  public static final String EXTRA_CAMERA_DATA = "camera_data";
	  private byte[] mCameraData;
	  private static final String KEY_IS_CAPTURING = "is_capturing";
	  private View maskView;
	  private float mDist;
	  private static Bitmap mBitmap;
	  private static Bitmap bmpGrayscale;
	  
	 // button configuration
	  private ImageButton mButton;
	  private OnClickListener mImageButtonClickListener = new OnClickListener() {
		    @Override
		    public void onClick(View v) {
		        switch (itemselection) {
		        case 0:
		        	break;
		        case 1:
		        	  detectingTextView.setVisibility(View.INVISIBLE);
					  rand_detection();
		        
					break;
		        case 2:
		        	  if(detecting) {
		        		  detecting = false;
		        	  }else{
					    auto_detection();
		        	  }
					break;
		        case 3:
		        	  onSave();
		        	 break;
		        case 4:
		        	  if(taken){
		        		  mCameraImage.setVisibility(View.GONE);
		 				  mCameraPreview.setVisibility(View.VISIBLE);
		        		  mCameraImage.setImageResource(0);
		        		  taken = false;
		        	  }
		        	  else onOpen();
		        	 break;
				default:
				
					break;
				}
		      
		    
		    }
		  };
	//encode and decode variable
    private Boolean find = false;
    private int numberOfdetect = 20;
    //public static String[] codewordString ={"101010010101","010101010101","110110010100","110100110010"};
    private static final int SELECT_PICTURE = 1;
    private String selectedImagePath;
    private Boolean taken = false;
    private Boolean detecting = false;
    public static int codeLength = 12;
	public static int hamming_weight = 6;
	private TextView detectingTextView;
    private int detectionNUmber =0;
	private static SQLiteDatabase db;
	 // listView for list of keys
	 
	 
    //database 
    public static DatabaseHelper dbhelper = null;
    public  static ArrayList<String> idList = new ArrayList<String>();
	 public  static ArrayList<String> titleList = new ArrayList<String>();
	 public  static ArrayList<String> codeList = new ArrayList<String>();
	 public  static ArrayList<String> linkList = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbhelper = new DatabaseHelper(this);
        mTitle = mDrawerTitle = getTitle();
        funtionTitles = getResources().getStringArray(R.array.function_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, funtionTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }
        
        
        /*initialize parameter*/
        Display display = getWindowManager().getDefaultDisplay();
	    Point size = new Point();
	    display.getSize(size);
	    width = size.x;
	    height = size.y;
	    
	    detectingTextView = (TextView) findViewById(R.id.textDect);
	    
	    mCameraPreview = (SurfaceView) findViewById(R.id.preview_view);
	    final SurfaceHolder surfaceHolder = mCameraPreview.getHolder();
	    surfaceHolder.addCallback(this);
	    surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	    
	    //camera activity
	    mIsCapturing= true;
	    mCameraImage = (ImageView) findViewById(R.id.content);
	    mCameraImage.setVisibility(View.INVISIBLE);
	    
	    if (mCamera==null)
	    {
	    	try {
	    		mCamera = Camera.open();
	    		camParams =  mCamera.getParameters() ;
			} catch (Exception e) {
				// TODO: handle exception
			}
	    	
	    	
	    }
	    Log.d("check2",Boolean.toString(mCamera!=null));
	    camParams =  mCamera.getParameters() ;
	    minTime = camParams.getMinExposureCompensation();
	    maxTime = camParams.getMinExposureCompensation();
	    exp = minTime;
	    camParams.set("iso", Integer.toString(Iso));
        camParams.setAutoExposureLock(false);
        camParams.set("shutter-spped", 1000);
        
        camParams.setExposureCompensation(exp);
        camParams.setPictureSize(640, 480);
        
        mCamera.setParameters(camParams);
	    try {
			mCamera.setPreviewDisplay(mCameraPreview.getHolder());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    mCamera.setDisplayOrientation(90);
	    
	    //Button configuration
	    mButton = (ImageButton) findViewById(R.id.imageButton);
	    mButton.setOnClickListener(mImageButtonClickListener);
	    
	    //mask view
	    maskView = (View) findViewById(R.id.maskView);
	    
	    // Database
	    
	    db = dbhelper.getReadableDatabase();
	    Cursor c =db.rawQuery("SELECT * FROM " + TABLE_NAME , null);
	    
	    int titleColumn = 0,codeColumn = 0,id = 0,linkcolumn = 0,i = 0;
	    if(c!=null){
	        titleColumn = c.getColumnIndex(CODE_TITLE);
            codeColumn = c.getColumnIndex(CODE_CONTENT);
            id = c.getColumnIndex(_ID);
            linkcolumn = c.getColumnIndex(CODE_LINK);
            i = 0;
	    }
        
        titleList.clear();
        codeList.clear();
        idList.clear();
        linkList.clear();
        // Check if our result was valid.
        
        if (c.getCount() > 0 && c!=null) {
         // Loop through all Results
       	 c.moveToFirst();
         do {
          String titleString = c.getString(titleColumn);
          String codeString = c.getString(codeColumn);
          String idString =c.getString(id);
          String linkString = c.getString(linkcolumn);
          
          titleList.add(i, titleString);
          codeList.add(i,codeString);
          idList.add(i,idString);
          linkList.add(i, linkString);
          i++;
         }while(c.moveToNext());
       }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
        case R.id.action_websearch:
            // create intent to perform web search for this planet
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
            // catch event that there's no activity to handle intent
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
            }
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        
		itemselection = position;
		if(itemselection==0 && previous_selection !=0){
			mCameraImage.setImageResource(0);
        	maskView.setVisibility(View.GONE);
        	mButton.setImageResource(R.drawable.ic_action_camera);
		}
        if(itemselection==1 ){
        	//mCameraImage.setImageResource(0);
        	detectingTextView.setVisibility(View.INVISIBLE);
        	maskView.setVisibility(View.GONE);
        	//mCameraPreview.setVisibility(View.VISIBLE);
        	mButton.setImageResource(R.drawable.ic_action_camera);
        } 
        if(itemselection==2 ){
        	mCameraImage.setImageResource(0);
        	detectingTextView.setVisibility(View.INVISIBLE);
        	maskView.setVisibility(View.VISIBLE);
        	//mCameraPreview.setVisibility(View.VISIBLE);
        	mButton.setImageResource(R.drawable.ic_action_camera);
        } 
        if(itemselection==3) {
        	mCameraImage.setImageResource(0);
        	maskView.setVisibility(View.GONE);
        	detectingTextView.setVisibility(View.INVISIBLE);
        	mCameraPreview.setVisibility(View.VISIBLE);
        	mButton.setImageResource(R.drawable.ic_action_save);	
        }
        if(itemselection==4) {
        	mCameraImage.setImageResource(0);
        	maskView.setVisibility(View.GONE);
        	detectingTextView.setVisibility(View.INVISIBLE);
        	mCameraPreview.setVisibility(View.VISIBLE);
        	mButton.setImageResource(R.drawable.ic_action_new);
        }
        if(itemselection ==5)
        {
        	detectingTextView.setVisibility(View.INVISIBLE);
        	Intent eventIntent = new Intent(this, codelist.class);
 	        startActivity(eventIntent);
        }
        if(itemselection==6)
        {
        	detectingTextView.setVisibility(View.INVISIBLE);
        	Intent eventIntent = new Intent(this, setting.class);
 	        startActivity(eventIntent);
        }
        
        if(itemselection ==7){
        	 detectingTextView.setVisibility(View.INVISIBLE);
        	 goToUrl("http://en.wikipedia.org/wiki/Visible_light_communication");
        }
        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle("LightSay® by T.Zhang");
        mDrawerLayout.closeDrawer(mDrawerList);
        previous_selection = itemselection;
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

   

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		if (mCamera != null) {
		      try {
		        mCamera.setPreviewDisplay(holder);
		        if (mIsCapturing) {
		          mCamera.startPreview();
		        }
		      } catch (IOException e) {
		        Toast.makeText(MainActivity.this, "Unable to start camera preview.", Toast.LENGTH_LONG).show();
		      }
		    }
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		// TODO Auto-generated method stub
		mCameraData = data;
		switch (itemselection) {
		case 0:
			
			break;
        case 1:
        	
			  try {
				rand_detection_post();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
        case 2:
			   try {
				auto_detection_post();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		default:
			break;
		}
	}
	
	  @Override
	  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == SELECT_PICTURE){
            if(resultCode ==RESULT_OK){
            	 taken= true;
            	 Uri selectedImageUri = data.getData();
                 selectedImagePath = getPath(selectedImageUri);
                 File imageFile = new File(selectedImagePath);
 				 if(imageFile.exists()){
 					Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
 				
 					
 					Matrix matrix = new Matrix();

 					matrix.postRotate(0);

 					mBitmap = Bitmap.createBitmap(bitmap , 0, 0, bitmap.getWidth(),bitmap.getHeight(), matrix, true);
 					mCameraImage.setVisibility(View.VISIBLE);
 					mCameraPreview.setVisibility(View.INVISIBLE);
 					mCameraImage.setImageBitmap(mBitmap);
 				} 
            }
	    }
	  }
	
	@Override
	protected void onResume() {
	    super.onResume();
	    
	    if (mCamera == null) {
	      try {
	        mCamera = Camera.open();
	    	camParams.set("iso", "800");
         	camParams.setAutoExposureLock(false);
         	camParams.set("shutter-spped", 1000);
         	int minTime = camParams.getMinExposureCompensation();
         	
         	camParams.setExposureCompensation(minTime);
         	camParams.setPictureSize(640, 480);
         	mCamera.setParameters(camParams);
	        mCamera.setPreviewDisplay(mCameraPreview.getHolder());
	        mCamera.setDisplayOrientation(90);
	        if (mIsCapturing) {
	          mCamera.startPreview();
	        }
	      } catch (Exception e) {
	        Toast.makeText(MainActivity.this, "Unable to open camera.", Toast.LENGTH_LONG)
	        .show();
	      }
	    }
	  }
	@Override
	protected void onPause() {
	    super.onPause();
	    
	    if (mCamera != null) {
	      mCamera.release();
	      mCamera = null;
	    }
	  }
	  
	  @Override
	public void onDestroy() {
		    super.onDestroy();
		    if (mCamera != null) {
			      mCamera.release();
			      mCamera = null;
			 }
	  }
	
	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
	   super.onSaveInstanceState(savedInstanceState);
	    
	    savedInstanceState.putBoolean(KEY_IS_CAPTURING, mIsCapturing);
	}
	  
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
	    super.onRestoreInstanceState(savedInstanceState);
	    
	    mIsCapturing = savedInstanceState.getBoolean(KEY_IS_CAPTURING, mCameraData == null);
	    
	  }
	 
	 @Override
	 public boolean onTouchEvent(MotionEvent event) {
	      // Get the pointer ID
	     
	      int action = event.getAction();


	      if (event.getPointerCount() > 1) {
	          // handle multi-touch events
	          if (action == MotionEvent.ACTION_POINTER_DOWN) {
	              mDist = getFingerSpacing(event);
	          } else if (action == MotionEvent.ACTION_MOVE && camParams.isZoomSupported()) {
	              mCamera.cancelAutoFocus();
	              handleZoom(event, camParams);
	          }
	      } else {
	          // handle single touch events
	          if (action == MotionEvent.ACTION_UP) {
	              handleFocus(event, camParams);
	          }
	      }
	      return true;
	  }

	  private void handleZoom(MotionEvent event, Camera.Parameters params) {
	      int maxZoom = params.getMaxZoom();
	      int zoom = params.getZoom();
	      float newDist = getFingerSpacing(event);
	      if (newDist > mDist) {
	          //zoom in
	          if (zoom < maxZoom)
	              zoom++;
	      } else if (newDist < mDist) {
	          //zoom out
	          if (zoom > 0)
	              zoom--;
	      }
	      mDist = newDist;
	      params.setZoom(zoom);
	      mCamera.setParameters(params);
	  }

	  public void handleFocus(MotionEvent event, Camera.Parameters params) {
	      int pointerId = event.getPointerId(0);
	      int pointerIndex = event.findPointerIndex(pointerId);
	      event.getX(pointerIndex);
	      event.getY(pointerIndex);

	      List<String> supportedFocusModes = params.getSupportedFocusModes();
	      if (supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
	          mCamera.autoFocus(new Camera.AutoFocusCallback() {
	              @Override
	              public void onAutoFocus(boolean b, Camera camera) {
	                  // currently set to auto-focus on single touch
	              }
	          });
	      }
	  }

	  /** Determine the space between the first two fingers */
	  private float getFingerSpacing(MotionEvent event) {
	      // ...
	      float x = event.getX(0) - event.getX(1);
	      float y = event.getY(0) - event.getY(1);
	      return FloatMath.sqrt(x * x + y * y);
	  }
	 
	  //Auto detection
	  private void auto_detection(){
		//set out maskview;
		detecting = true;
		mCameraPreview.setVisibility(View.VISIBLE);
		maskView.setVisibility(View.VISIBLE);
		 try {
			  mCamera.takePicture(null, null, this);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	  @SuppressLint("ShowToast")
	private void auto_detection_post() throws InterruptedException{
			//set out maskview;
		  detectingTextView.setVisibility(View.INVISIBLE);
		  if(!detecting) return;
		  String tmpString = new String();
			maskView.setVisibility(View.VISIBLE);
			if (mCameraData != null) {
		    	  mBitmap = BitmapFactory.decodeByteArray(mCameraData, 0, mCameraData.length);
		    	  Matrix matrix = new Matrix();
		          matrix.postRotate(90);
                  mBitmap = Bitmap.createBitmap(mBitmap , 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
		    	  tmpString=auto_decodeImage();
			  }
		      
		      
		      String finalResult = correlation_detection(tmpString);
		      if(codeList.size()==0){
		    	  Toast.makeText(MainActivity.this, "Data list is empty.", Toast.LENGTH_LONG);
		    	  return ;
		      }
		      for(int i = 0 ; i <codeList.size() ;i++){
		    	  if(finalResult.equals(codeList.get(i)))
		    		  find = true;
		      }
		      if(find){
		    	
		      Intent intent = new Intent(getBaseContext(), result.class);
		      intent.putExtra("result", finalResult);
		      startActivity(intent);
		      find =  false;
		     
		      overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		      
		    	//  int index = codeList.indexOf(finalResult);
		    	//  goToUrl(linkList.get(index));
		      }
		      else{
		    	  detectionNUmber++;
		    	  if(detectionNUmber==2){
		    		  detectingTextView.setVisibility(View.VISIBLE);
		    		  detectionNUmber=0;
		    		  return;
		    	  }
		    	  Thread.sleep(1000);
			     try {
				    mCamera.takePicture(null, null, this);
			      } catch (Exception e) {
				// TODO: handle exception
			      }
		      }
		}
	  
	  //Rand detection
	  private void rand_detection(){
		//set out maskview;
		maskView.setVisibility(View.GONE);
		 try {
			  mCamera.takePicture(null, null, this);
		} catch (Exception e) {
			// TODO: handle exception
		}
	  }
	  private void rand_detection_post() throws InterruptedException{
		  String result = new String();
		  
		  if (mCameraData != null) {
	    	  mBitmap = BitmapFactory.decodeByteArray(mCameraData, 0, mCameraData.length);
	    	  Matrix matrix = new Matrix();
	          matrix.postRotate(90);
              mBitmap = Bitmap.createBitmap(mBitmap , 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
              mCameraImage.setImageBitmap(mBitmap);
              
              
    		  result = random_detection();
              
		  }
		  //mCameraPreview.setVisibility(View.INVISIBLE);
		 
		  for(int i = 0 ; i <codeList.size() ;i++){
	    	  if(result.equals(codeList.get(i)))
	    		  find = true;
	      }
	      /*
	      Intent intent = new Intent(getBaseContext(), result.class);
	      intent.putExtra("result", result);
	      startActivity(intent);
	      find =  false;
	      
	      
	       */
		  if(find){
		  //int index = codeList.indexOf(result);
    	  //goToUrl(linkList.get(index));
			  Intent intent = new Intent(getBaseContext(), result.class);
		      intent.putExtra("result", result);
		      find =  false;
		      startActivity(intent);
		      
	      //overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		  }
		  else{
			  detectingTextView.setVisibility(View.VISIBLE);
		  }
	      
	  }
	  
	 // Help function
	  public static Bitmap toGrayscale(Bitmap bmpOriginal)
	  {        
	      int width, height;
	      height = bmpOriginal.getHeight();
	      width = bmpOriginal.getWidth();    

	      Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	      Canvas c = new Canvas(bmpGrayscale);
	      Paint paint = new Paint();
	      ColorMatrix cm = new ColorMatrix();
	      cm.setSaturation(0);
	      ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
	      paint.setColorFilter(f);
	      c.drawBitmap(bmpOriginal, 0, 0, paint);
	      return bmpGrayscale;
	  }
	  
	  private String auto_decodeImage(){
		  String result = new String();
		  int[] location = new int[2]; 
		  mCameraImage.getLocationOnScreen(location);
		   int imageX = location[0];
		   int imageY = location[1];
		   int imageW = mCameraPreview.getWidth();
		   int imageH = mCameraPreview.getHeight();
		   int imgWidth = mBitmap.getWidth();
		   int imgHeight = mBitmap.getHeight();
		   
		   bmpGrayscale = MainActivity.toGrayscale(mBitmap);
		   
		  int decodeXstart;
		  int decodeYstart;
		  
		  decodeXstart = (int) ((mask.x-mask.wid-imageX)/imageW*imgWidth);
		  decodeYstart = (int) ((mask.y-mask.hei-imageY)/imageH *imgHeight);
			
		  
		  int decodeXend ;
		  int decodeYend ;
		 
		  decodeXend =  (int) ((mask.x+mask.wid-imageX)/imageW*imgWidth);
			
		  
		  
		  decodeYend =(int) ((mask.y+mask.hei-imageY)/imageH *imgHeight);
		
		  
		  
		  // sum the pixels
		  imgWidth = decodeXend-decodeXstart;
		  int[] data = new int[imgWidth];
		  
		  
		  for (int i = decodeXstart ; i < decodeXend;i++){
			  int sum = 0;
			  for(int j = decodeYstart; j<decodeYend ; j++){
				  sum += (bmpGrayscale.getPixel(i,j) & 0xff);
			  }
			  data[i-decodeXstart] = sum;
		  }
		  
		  //find local maximum
		  
		  List<Integer> loc = new ArrayList<Integer>();
		  List<Integer> pks = new ArrayList<Integer>();
		  int mean = 0;
		  for (int j = imgWidth/4; j < imgWidth*3/4;j++){
			  mean +=data[j];
		  }
		  mean = mean / (imgWidth*3/4 - imgWidth/4);
		  for (int i = 1; i < imgWidth-1; i++){
				   if (data[i-1]<data[i] && data[i] > data[i+1] && data[i] > mean){
				       loc.add(i);
				       pks.add(data[i]);
		  			}
		  }
		  
		  
		  
		  List<Integer> minIdx = new ArrayList<Integer>();
		  List<Integer> minValue = new ArrayList<Integer>();
		  
		  for (int i = 1; i < imgWidth-1; i++){
				   if (data[i-1]>data[i] && data[i] <data[i+1] && data[i] <mean){
				       minIdx.add(i);
				       minValue.add(data[i]);
		  			}
		  }
		  
		  //get the threshold
		  List<Integer> middle = new ArrayList<Integer>();
		  
		  for (int i = 0 ; i < loc.get(0);i++){
		      middle.add(((pks.get(0)+minValue.get(0))/2));
		  }
		  for (int i = 0; i < loc.size()-1;i++){
		      for (int j = loc.get(i); j < loc.get(i+1);j++){
		          middle.add(((pks.get(i)+pks.get(i+1)+2*minValue.get(0))/4));
		  	  }
		  }
		  for (int i = loc.get(loc.size()-1); i < imgWidth ; i++ ){
		      middle.add(((pks.get(pks.size()-1)+minValue.get(0))/2));
		  }
		  
		  //  get the Binary data
		  int[] decode = new int[imgWidth];
		  for (int i = 0 ; i <imgWidth; i++){
			  if (data[i]>middle.get(i)) {
				  decode[i]=1;
			  }else {
				  decode[i]=0;
			  }
		  }
		  
		  
		  int gap = 1;
		  int mini = 100;
		  for (int i = 0;  i <imgWidth-1; i++){
		      if (decode[i]==1 && decode[i+1]==1)
		          gap = gap +1;
		      if (decode[i]==0 && decode[i+1]==0)
		          gap = gap +1 ;
		      if (decode[i] != decode[i+1]){
		          
		          if (gap < mini && gap >3){
		              mini = gap;
		      	   }
		          gap = 1 ;
		  		}
		  }
		 // String result = new String(" ");
		  result = " ";
		  int one = 0;
		  int zero = 0;
		  gap = mini;
		  
		  for (int i = 0 ; i <imgWidth ;i++){
				    if(decode[i]==1)
				        one = one +1;
				    else
				        zero = zero +1 ;
				    
				    
				    while(one >= gap){
				        zero = 0;
				        result+="1";
				        one = one -gap;
				        if (one < gap)
				            one = 0;
				        
		  			}
				    
				    
				    while(zero >= gap){
				        one = 0;
				        result+="0";
				        zero = zero -gap;
				        if (zero < gap )
				            zero = 0;
				        
				    }
				    
		  }
		  
		  for (int j = 0 ; j < decode.length;j++){
			  decode[j]=0;
		  }
		  
		  return result;
		  
	  }
	  
	  public static String correlation_detection(String result){
	    	
	    	if(result.length()<codeLength) return "noting detected";
	    	
	    	Log.d("raw result",result);
	    	
	    	for(int i = 0;i< result.length()-codeLength+1;i++){
				for(int j = 0 ; j < codeList.size(); j++){
					String detect = result.substring(i, i+codeLength);
					int corr = matrix_muti(detect, codeList.get(j));
					Log.d("detect",Integer.toString(corr));
					if(corr >= hamming_weight-1){
						int tmp =0;
						for(int l = 0 ; l < detect.length();l++){
							if(detect.charAt(l) != codeList.get(j).charAt(l))
								tmp++;
						}
						if(tmp<2)
						return codeList.get(j);
						
						
					}
				}
			}
	    	
	    	
	    	
	    	return "decode failure";
	    	
	    }
	  public static int matrix_muti(String a, String b){
	    	int result = 0;
			for(int i = 0 ; i < a.length();i++){
				int x,y;
				if (a.charAt(i) =='1') x =1;
				else x=0;
				
				if(b.charAt(i) =='1') y =1;
				else y=0;
				result+= x*y;
			}
	    	//result = result % 2;
	    	return result;
	    	
	    }
	  public static int randInt(int min, int max) {

	        // NOTE: Usually this should be a field rather than a method
	        // variable so that it is not re-seeded every call.
	        Random rand = new Random();

	        // nextInt is normally exclusive of the top value,
	        // so add 1 to make it inclusive
	        int randomNum = rand.nextInt((max - min) + 1) + min;

	        return randomNum;
	    }
	  private String random_detection() throws InterruptedException{
	    	int imageW = mCameraImage.getWidth();
			int imageH = mCameraImage.getHeight();
			int imgWidth = mBitmap.getWidth();
			int imgHeight = mBitmap.getHeight();
			
			int[] record = new int[codeLength];
			for(int k = 0 ; k< codeLength; k++){
				record[k] =0;
			}
	    	for(int i = 0 ; i<numberOfdetect;i++){
	    		int x = randInt(0, imageW-(int)(((float) mask.wid*2)/imgWidth*imageW));
	    		int y = randInt(0, imageH-(int)(((float) mask.hei*2)/imgHeight*imageH));
	    	   
	    	    String tmpString =decodeImage_rand(x, y);
	    		
	    		for(int j = 0 ; j < codeList.size() ;j++){
	    			if(tmpString.equals(codeList.get(j)))
	    				record[j]++;
	    		}
	    	}
	    	int maxIndex = 0;
	    	for (int i = 1; i < record.length; i++){
	    	   int newnumber = record[i];
	    	   if ((newnumber > record[maxIndex])){
	    	   maxIndex = i;
	    	  }
	    	}
	    	
	    	if(record[maxIndex] > 0){
	    	return codeList.get(maxIndex);
	    	}else{
	    		return "Nothing Find";
	    	}
	    }
	  private String decodeImage_rand(int x, int y){
			 
			 
		   int[] location = new int[2]; 
		   mCameraImage.getLocationOnScreen(location);
		   int imageW = mCameraImage.getWidth();
		   int imageH = mCameraImage.getHeight();
		   int imgWidth = mBitmap.getWidth();
		   int imgHeight = mBitmap.getHeight();
		   
		   bmpGrayscale = toGrayscale(mBitmap);
		   
	       int decodeXstart;
		   int decodeYstart;
		  
		   int decode_width = mask.wid*2;
		   int decode_height = mask.hei*2;
		   decodeXstart = (int )( ((float)x) /imageW * imgWidth);
		   if (decodeXstart>imgWidth-decode_width)
			  decodeXstart = imgWidth-decode_width;
		
		
		  decodeYstart = (int )( ((float)y) /imageH * imgHeight);
		  if (decodeYstart>imgHeight-decode_height)
			  decodeXstart = imgHeight-decode_height;
		
		  
		  int decodeXend  = decodeXstart + decode_width;
		  int decodeYend  = decodeYstart+ decode_height;
		  
		 
		  
		  
		  // sum the pixels
		  imgWidth = decodeXend-decodeXstart;
		  int[] data = new int[imgWidth];
		  
		  
		  for (int i = decodeXstart ; i < decodeXend;i++){
			  int sum = 0;
			  for(int j = decodeYstart; j<decodeYend ; j++){
				  sum += (bmpGrayscale.getPixel(i,j) & 0xff);
			  }
			  data[i-decodeXstart] = sum;
		  }
		  
		  //find local maximum
		  
		  List<Integer> loc = new ArrayList<Integer>();
		  List<Integer> pks = new ArrayList<Integer>();
		  int mean = 0;
		  for (int j = imgWidth/4; j < imgWidth*3/4;j++){
			  mean +=data[j];
		  }
		  mean = mean / (imgWidth*3/4 - imgWidth/4);
		  for (int i = 1; i < imgWidth-1; i++){
				   if (data[i-1]<data[i] && data[i] > data[i+1] && data[i] > mean){
				       loc.add(i);
				       pks.add(data[i]);
		  			}
		  }
		  
		  
		  
		  List<Integer> minIdx = new ArrayList<Integer>();
		  List<Integer> minValue = new ArrayList<Integer>();
		  
		  for (int i = 1; i < imgWidth-1; i++){
				   if (data[i-1]>data[i] && data[i] <data[i+1] && data[i] <mean){
				       minIdx.add(i);
				       minValue.add(data[i]);
		  			}
		  }
		  
		  //get the threshold
		  List<Integer> middle = new ArrayList<Integer>();
		  
		  for (int i = 0 ; i < loc.get(0);i++){
		      middle.add(((pks.get(0)+minValue.get(0))/2));
		  }
		  for (int i = 0; i < loc.size()-1;i++){
		      for (int j = loc.get(i); j < loc.get(i+1);j++){
		          middle.add(((pks.get(i)+pks.get(i+1)+2*minValue.get(0))/4));
		  	  }
		  }
		  for (int i = loc.get(loc.size()-1); i < imgWidth ; i++ ){
		      middle.add(((pks.get(pks.size()-1)+minValue.get(0))/2));
		  }
		  
		  //  get the Binary data
		  int[] decode = new int[imgWidth];
		  for (int i = 0 ; i <imgWidth; i++){
			  if (data[i]>middle.get(i)) {
				  decode[i]=1;
			  }else {
				  decode[i]=0;
			  }
		  }
		  
		  
		  int gap = 1;
		  int mini = 100;
		  for (int i = 0;  i <imgWidth-1; i++){
		      if (decode[i]==1 && decode[i+1]==1)
		          gap = gap +1;
		      if (decode[i]==0 && decode[i+1]==0)
		          gap = gap +1 ;
		      if (decode[i] != decode[i+1]){
		          
		          if (gap < mini && gap >3){
		              mini = gap;
		      	   }
		          gap = 1 ;
		  		}
		  }
		 // String result = new String(" ");
		  String tmp = "";
		  int one = 0;
		  int zero = 0;
		  gap = mini;
		  
		  for (int i = 0 ; i <imgWidth ;i++){
				    if(decode[i]==1)
				        one = one +1;
				    else
				        zero = zero +1 ;
				    
				    
				    while(one >= gap){
				        zero = 0;
				        tmp+="1";
				        one = one -gap;
				        if (one < gap)
				            one = 0;
				        
		  			}
				    
				    
				    while(zero >= gap){
				        one = 0;
				        tmp+="0";
				        zero = zero -gap;
				        if (zero < gap )
				            zero = 0;
				        
				    }
				    
		  }
		  
		  for (int j = 0 ; j < decode.length;j++){
			  decode[j]=0;
		  }
		  
		  
		   String tmp_result = correlation_detection(tmp);
		  
		   return tmp_result;
		    
		  
	  }
	  private void onSave() {
		  if(mCameraPreview.getVisibility()==View.VISIBLE) return;
	      File saveFile = openFileForImage();
	      if (saveFile != null) {
	        saveImageToFile(saveFile);
	      } else {
	        Toast.makeText(MainActivity.this, "Unable to open file for saving image.",
	        Toast.LENGTH_LONG).show();
	      }
	    }
	  private void onOpen() {

          // in onCreate or any event where your want the user to
          // select a file
          Intent intent = new Intent();
          intent.setType("image/*");
          intent.setAction(Intent.ACTION_GET_CONTENT);
          startActivityForResult(Intent.createChooser(intent,
                  "Select Picture"), SELECT_PICTURE);
			
		}
	  public String getPath(Uri uri) {
          // just some safety built in 
          if( uri == null ) {
              // TODO perform some logging or show user feedback
              return null;
          }
          // try to retrieve the image from the media store first
          // this will only work for images selected from gallery
          String[] projection = { MediaStore.Images.Media.DATA };
          Cursor cursor = managedQuery(uri, projection, null, null, null);
          if( cursor != null ){
              int column_index = cursor
              .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
              cursor.moveToFirst();
              return cursor.getString(column_index);
          }
          // this is our fallback here
          return uri.getPath();
  }
	  private File openFileForImage() {
		    File imageDirectory = null;
		    String storageState = Environment.getExternalStorageState();
		    if (storageState.equals(Environment.MEDIA_MOUNTED)) {
		      imageDirectory = new File(
		        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
		        "com.oreillyschool.android2.camera");
		      if (!imageDirectory.exists() && !imageDirectory.mkdirs()) {
		        imageDirectory = null;
		      } else {
		        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_mm_dd_hh_mm",
		          Locale.getDefault());
		    
		        return new File(imageDirectory.getPath() +
		          File.separator + "image_" +
		          dateFormat.format(new Date()) + ".png");
		      }
		    }
		    return null;
		  }
	  private void saveImageToFile(File file) {
		    if (mBitmap != null) {
		      FileOutputStream outStream = null;
		      try {
		        outStream = new FileOutputStream(file);
		        if (!mBitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)) {
		          Toast.makeText(MainActivity.this, "Unable to save image to file.",
		          Toast.LENGTH_LONG).show();
		        } else {
		          Toast.makeText(MainActivity.this, "Saved image to: " + file.getPath(),
		          Toast.LENGTH_LONG).show();
		        }
		        outStream.close();
		      } catch (Exception e) {
		        Toast.makeText(MainActivity.this, "Unable to save image to file.",
		        Toast.LENGTH_LONG).show();
		      }
		    }
		  }
	  private void goToUrl (String url) {
	        Uri uriUrl = Uri.parse(url);
	        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
	        startActivity(launchBrowser);
	    }
}