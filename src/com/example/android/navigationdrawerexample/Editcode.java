package com.example.android.navigationdrawerexample;

import static android.provider.BaseColumns._ID;
import static com.example.android.navigationdrawerexample.DatabaseConstants.CODE_LINK;
import static com.example.android.navigationdrawerexample.DatabaseConstants.CODE_CONTENT;
import static com.example.android.navigationdrawerexample.DatabaseConstants.TABLE_NAME;
import static com.example.android.navigationdrawerexample.DatabaseConstants.CODE_TITLE;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Editcode extends Activity {
	private String iid;
	private EditText titleView;
	private EditText codeView;
	private EditText linkView;
	
    private SQLiteDatabase db;
	
	private ActionBar actionBar;
	private Button saveButton, deleteButton;
	
	private String title, code , link;
	
	
	
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		iid = intent.getExtras().getString("ID");
		setContentView(R.layout.editcode);
		
		
        
        titleView = (EditText) findViewById(R.id.editTitle);
        codeView = (EditText) findViewById(R.id.editCode);
        linkView = (EditText) findViewById(R.id.editLink);
        
        db = MainActivity.dbhelper.getReadableDatabase();
		Cursor cursor = db.rawQuery(
	               "SELECT " + CODE_TITLE + ", " + CODE_CONTENT  + ", " + CODE_LINK +
	               " FROM " + TABLE_NAME + 
	               " WHERE _ID=?", new String[]{iid});
		 int titleColumn = cursor.getColumnIndex(CODE_TITLE);
         int codeColumn = cursor.getColumnIndex(CODE_CONTENT);
         int id = cursor.getColumnIndex(_ID);
         int linkcolun = cursor.getColumnIndex(CODE_LINK);
		while (cursor.moveToNext()) {
		    title = cursor.getString(titleColumn);
		    code = cursor.getString(codeColumn);
		    
		    link=cursor.getString(linkcolun);
		}
		
		titleView.setText(title);
		codeView.setText(code);
		linkView.setText(link);
		
		deleteButton = (Button) findViewById(R.id.btnCancel);
		deleteButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 deleteEvent(iid);
		         codelist.refresh();
		         finish();
		         overridePendingTransition(R.anim.right_in, R.anim.left_out);
			}
		});
		saveButton = (Button) findViewById(R.id.btnConfirm);
		saveButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(updatecode()){
					
					codelist.refresh();
					finish();
					overridePendingTransition(R.anim.right_in, R.anim.left_out);
				}
			}
		});
		
	}
	
	 private boolean updatecode(){
			
	        //SQLiteDatabase db2 = MainActivity.dbhelper.getWritableDatabase();
	        ContentValues values = new ContentValues();
	        
	        String title = titleView.getText().toString();
			if(title.equals("")) {	
				Toast.makeText(this, "Please input the key name", Toast.LENGTH_SHORT).show();
				return false;
			}
			
			String codeString = codeView.getText().toString();
			if(codeString.length()> MainActivity.codeLength){
				Toast.makeText(this, "Code should be less than "+ Integer.toString(MainActivity.codeLength) +" letter", Toast.LENGTH_SHORT).show();
				return false;
			}
		    if(codeString.equals("")) {	
					Toast.makeText(this, "Please input the key password", Toast.LENGTH_SHORT).show();
					return false;
				}
		    if(calculate_weight(codeString) != MainActivity.hamming_weight){
		    	Toast.makeText(this, "The Hamming weight of code must be " + Integer.toString(MainActivity.hamming_weight), Toast.LENGTH_SHORT).show();
				return false;
		    }
		    
		    if(!validate_code(codeString)){
		    	Toast.makeText(this, "The code word can not has char other than 0 or 1 " , Toast.LENGTH_SHORT).show();
				return false;
		    }
		    String linkString = linkView.getText().toString();
			
		    if(!linkString.contains("http")){
		    	Toast.makeText(this, "The link must have http header ", Toast.LENGTH_SHORT).show();
				return false;
		    }
	        values.put(CODE_TITLE, title);
	        values.put(CODE_CONTENT, codeString);
	        values.put(CODE_LINK, linkString);
	       
	        db.update(TABLE_NAME, values, "_ID=" +iid, null);
	        return true;
	    }
	 
	 private int calculate_weight(String input){
    	 int result = 0;
    	 for(int i = 0 ; i < input.length();i++){
    		 int m = (input.charAt(i) == '1') ? 1 : 0;
    		 result +=m;
    	 }
    	 return result;
    	 
     }
     
     private boolean validate_code(String input){
    	 for(int i = 0; i < input.length(); i++){
    		 if(input.charAt(i)!='1' && input.charAt(i) != '0') return false;
    	 }
    	 return true;
     }
     private static void deleteEvent(String id) {
 		SQLiteDatabase db = MainActivity.dbhelper.getWritableDatabase();
         db.delete(TABLE_NAME, _ID + "=" + id, null);
 	}
}
