package com.example.android.navigationdrawerexample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class setting extends Activity{
	private Button confirmButton;
	private Button cancelButton;
	private EditText lengthEditText;
	private EditText weightEditText;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.setting);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        setTitle("  Setting");
        
        
        lengthEditText = (EditText) findViewById(R.id.editlength);
        weightEditText = (EditText) findViewById(R.id.editweight);
        
        lengthEditText.setText("12");
        weightEditText.setText("6");
        confirmButton = (Button) findViewById(R.id.btnConfirm);
        cancelButton = (Button) findViewById(R.id.btnCancel);
        
        confirmButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				  saveSetting();
			}
		});
        
        cancelButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MainActivity.codeLength =12;
				MainActivity.hamming_weight =6;
				finish();
				overridePendingTransition(R.anim.fadein, R.anim.fadeout);
			}
		});
	}
	
	private void saveSetting(){
		  MainActivity.codeLength = Integer.valueOf(lengthEditText.getText().toString());
		  MainActivity.hamming_weight = Integer.valueOf(weightEditText.getText().toString());
		  finish();
		  overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}
}
