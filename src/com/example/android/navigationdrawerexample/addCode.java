package com.example.android.navigationdrawerexample;


import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import static com.example.android.navigationdrawerexample.DatabaseConstants.TABLE_NAME;
import static com.example.android.navigationdrawerexample.DatabaseConstants.CODE_TITLE;
import static com.example.android.navigationdrawerexample.DatabaseConstants.CODE_LINK;
import static com.example.android.navigationdrawerexample.DatabaseConstants.CODE_CONTENT;

public class addCode extends Activity{
	private Button confirmButton;
	private Button cancelButton;
	private EditText titleEditText;
	private EditText codeEditText;
	private EditText descriptionEditText;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.add_code);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        setTitle("   Add code");
        
        
        titleEditText = (EditText) findViewById(R.id.editTitle);
        codeEditText = (EditText) findViewById(R.id.editCode);
        descriptionEditText = (EditText) findViewById(R.id.editLink);
       
        confirmButton = (Button) findViewById(R.id.btnConfirm);
        confirmButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(add_code()){
				    codelist.refresh();
					finish();	
					overridePendingTransition(R.anim.left_in, R.anim.right_out);
				}
				
			}
		});
        cancelButton = (Button) findViewById(R.id.btnCancel);
        cancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.left_in, R.anim.right_out);
			}
		});
       
      }
     private boolean add_code(){
		
        SQLiteDatabase db = MainActivity.dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        String title = titleEditText.getText().toString();
		if(title.equals("")) {	
			Toast.makeText(this, "Please input the key name", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		String codeString = codeEditText.getText().toString();
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
	    	Toast.makeText(this, "The code word can not has char other than 0 or 1 ", Toast.LENGTH_SHORT).show();
			return false;
	    }
	    String linkString = descriptionEditText.getText().toString();
		
	    if(!linkString.contains("http")){
	    	Toast.makeText(this, "The link must have http header ", Toast.LENGTH_SHORT).show();
			return false;
	    }
        values.put(CODE_TITLE, title);
        values.put(CODE_CONTENT, codeString);
        values.put(CODE_LINK, linkString);
       
        db.insert(TABLE_NAME, null, values);
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
}
