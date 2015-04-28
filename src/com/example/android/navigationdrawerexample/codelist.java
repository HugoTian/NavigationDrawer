package com.example.android.navigationdrawerexample;

import static android.provider.BaseColumns._ID;
import static com.example.android.navigationdrawerexample.DatabaseConstants.CODE_LINK;
import static com.example.android.navigationdrawerexample.DatabaseConstants.CODE_CONTENT;
import static com.example.android.navigationdrawerexample.DatabaseConstants.TABLE_NAME;
import static com.example.android.navigationdrawerexample.DatabaseConstants.CODE_TITLE;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class codelist extends Activity{
	 private static SQLiteDatabase db;
	 // listView for list of keys
	 private static ListView mListView;
	 public  static ArrayList<String> idArrayList = new ArrayList<String>();
	 public  static ArrayList<String> titleArrayList = new ArrayList<String>();
	 public  static ArrayList<String> codeArrayList = new ArrayList<String>();
	 public  static ArrayList<String> linkArrayList = new ArrayList<String>();
	 public static ArrayAdapter<String> adapter ;
	 
	 private Button addButton;
	 private Button cancelButton;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.codelist);
        
        
        
        mListView = (ListView) findViewById(R.id.listcode);
        db = MainActivity.dbhelper.getReadableDatabase();
	    Cursor c =db.rawQuery("SELECT * FROM " + TABLE_NAME , null);
	    
	    int titleColumn = 0,codeColumn = 0,id = 0,linkcolumn = 0,i = 0;
	    if(c!=null){
	        titleColumn = c.getColumnIndex(CODE_TITLE);
            codeColumn = c.getColumnIndex(CODE_CONTENT);
            id = c.getColumnIndex(_ID);
            linkcolumn = c.getColumnIndex(CODE_LINK);
            i = 0;
	    }
        
        titleArrayList.clear();
        codeArrayList.clear();
        idArrayList.clear();
        linkArrayList.clear();
        // Check if our result was valid.
        
        if (c.getCount() > 0 && c!=null) {
         // Loop through all Results
       	 c.moveToFirst();
         do {
          String titleString = c.getString(titleColumn);
          String codeString = c.getString(codeColumn);
          String idString =c.getString(id);
          String linkString = c.getString(linkcolumn);
          
          titleArrayList.add(i, titleString);
          codeArrayList.add(i,codeString);
          idArrayList.add(i,idString);
          linkArrayList.add(i, linkString);
          i++;
         }while(c.moveToNext());
         
         adapter = new ArrayAdapter<String>(this,
        			        android.R.layout.simple_list_item_1, titleArrayList);
       	 adapter.notifyDataSetChanged();
 			
       		  
          // Assign adapter to ListView
     	   mListView.setAdapter(adapter);
       }
        
       addButton = (Button) findViewById(R.id.btnConfirm);
       addButton.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			addevent();
		}
	   });
       
       cancelButton = (Button) findViewById(R.id.btnCancel);
       cancelButton.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			cancel();
		}
	   });
       mListView.setOnItemClickListener(new OnItemClickListener() {
     	   public void onItemClick(AdapterView<?> parent, View view,
     	     int position, long id) {
     	    
     		   view.setSelected(true);
     	       String keySelect =  (String) parent.getItemAtPosition(position);
     	       int i = titleArrayList.indexOf(keySelect);
               gotoEdit(idArrayList.get(i));
     	   
			   
     	   }
     	  });
     	    
     	   
	}
	 private void gotoEdit(String id){
 	     
		 Intent intent = new Intent (codelist.this, Editcode.class);	
			intent.putExtra("ID", id);
			
			startActivity(intent);
			overridePendingTransition(R.anim.right_in, R.anim.left_out);
	 }
	 private void addevent(){
		 Intent intent = new Intent(codelist.this, addCode.class);
		 startActivity(intent);
		 overridePendingTransition(R.anim.right_in, R.anim.left_out);
		 
	 }
	 
	 private void cancel(){
		 this.finish();
		 overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	 }
	 @Override
	 protected void onResume() {
		    super.onResume();
		    adapter = new ArrayAdapter<String>(this,
			        android.R.layout.simple_list_item_1, titleArrayList);
	      
	 }
	 public static void refresh(){
    	 mListView.setAdapter(null);
    	 db = MainActivity.dbhelper.getReadableDatabase();
    	 Cursor c =db.rawQuery("SELECT * FROM " + TABLE_NAME , null);
    	 int titleColumn = c.getColumnIndex(CODE_TITLE);
         int codeColumn = c.getColumnIndex(CODE_CONTENT);
         int id = c.getColumnIndex(_ID);
         int linkcolumn = c.getColumnIndex(CODE_LINK);
         int i = 0;
         
         titleArrayList.clear();
         codeArrayList.clear();
         idArrayList.clear();
         linkArrayList.clear();
         // Check if our result was valid.
         
         if (c.getCount() > 0 && c!=null) {
          // Loop through all Results
        	 c.moveToFirst();
          do {
           String titleString = c.getString(titleColumn);
           String codeString = c.getString(codeColumn);
           String idString =c.getString(id);
           String linkString = c.getString(linkcolumn);
           
           titleArrayList.add(i, titleString);
           codeArrayList.add(i,codeString);
           idArrayList.add(i,idString);
           linkArrayList.add(i, linkString);
           i++;
          }while(c.moveToNext());
          
          
          adapter.notifyDataSetChanged();
  			
        		  
           // Assign adapter to ListView
      	   mListView.setAdapter(adapter);
         }
	 }
}
