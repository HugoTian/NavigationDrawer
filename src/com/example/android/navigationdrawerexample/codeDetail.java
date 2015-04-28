package com.example.android.navigationdrawerexample;


import static android.provider.BaseColumns._ID;
import static com.example.android.navigationdrawerexample.DatabaseConstants.CODE_LINK;
import static com.example.android.navigationdrawerexample.DatabaseConstants.CODE_CONTENT;
import static com.example.android.navigationdrawerexample.DatabaseConstants.TABLE_NAME;
import static com.example.android.navigationdrawerexample.DatabaseConstants.CODE_TITLE;


import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

public class codeDetail extends Activity{
        private SQLiteDatabase db;
        private String id;
    	private TextView titleView, codeView ,linkTextView;
    	private String title, code ,link;
    	private Button editButton;
    	private Button cancelButton;
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
     
		Intent intent = getIntent();
		id = intent.getExtras().getString("ID");
		setContentView(R.layout.codedetail);
		
		titleView = (TextView) findViewById(R.id.editTitle);
		codeView = (TextView) findViewById(R.id.editCode);
		linkTextView = (TextView) findViewById(R.id.editLink);
		
		db = MainActivity.dbhelper.getReadableDatabase();
		Cursor cursor = db.rawQuery(
	               "SELECT " + CODE_TITLE + ", " + CODE_CONTENT  + ", " + CODE_LINK +
	               " FROM " + TABLE_NAME + 
	               " WHERE _ID=?", new String[]{id});
		while (cursor.moveToNext()) {
		    title = cursor.getString(0);
		    code = cursor.getString(1);
		    link = cursor.getString(2);
		}
		
		titleView.setText(title);
		codeView.setText(code);
		linkTextView.setText(link);
	    editButton = (Button) findViewById(R.id.btnConfirm);
	    editButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				editEvent();
			}
		});
	    
	    cancelButton = (Button) findViewById(R.id.btnCancel);
	    cancelButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cancal();
			}
		});
	}
	
	private void cancal(){
		this.finish();
		overridePendingTransition(R.anim.right_in, R.anim.left_out);
	}
	
	private void editEvent(){
		Intent intent = new Intent (this, Editcode.class);	
		intent.putExtra("ID", id);
		
		startActivity(intent);
		this.finish();
		overridePendingTransition(R.anim.right_in, R.anim.left_out);
	}
	static void deleteEvent(String id) {
		SQLiteDatabase db = MainActivity.dbhelper.getWritableDatabase();
        db.delete(TABLE_NAME, _ID + "=" + id, null);
	}
}
