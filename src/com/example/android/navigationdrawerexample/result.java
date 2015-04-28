package com.example.android.navigationdrawerexample;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class result extends Activity{
	private Button confirmButton;
	private Button cancelButton;
	private TextView resultTextView;
	private String number;
	private String url;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.result);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        setTitle("   result");
        Intent intent = getIntent();
        number=intent.getExtras().getString("result"); 
        resultTextView = (TextView) findViewById(R.id.etResult);
        resultTextView.setText("");
        int index =codelist.codeArrayList.indexOf(number);
        url = codelist.linkArrayList.get(index);
        resultTextView.setText(codelist.titleArrayList.get(index));
        confirmButton = (Button) findViewById(R.id.btnConfirm);
        confirmButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(codelist.codeArrayList.contains(number)){
				goToUrl(url);
				finish();
				}
			}
		});
        cancelButton = (Button) findViewById(R.id.btnCancel);
        cancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
       
      }
	private void goToUrl (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }
}
