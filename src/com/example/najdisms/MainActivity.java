package com.example.najdisms;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class MainActivity extends Activity {
	public static final String nastavitve = "settings";
	public static String php_url = "";
	public static String version = "0.2";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		SharedPreferences settings = getSharedPreferences(nastavitve, 0);
		boolean firstrun = settings.getBoolean("firstrun", true);
		php_url = settings.getString("php_url", "http://w3.lokacijainfo.si:8085/sms/testnajdi.php");
		if (firstrun == true) {
			loginDialog();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void loginDialog() {
		LayoutInflater inflater = getLayoutInflater();
		Builder dialogmaker = new AlertDialog.Builder(this);
		dialogmaker.setTitle("Najdi.si vpis");
	    View okn = inflater.inflate(R.layout.login_prompt, null);
	    dialogmaker.setView(okn);
	    final  EditText user = (EditText) okn.findViewById(R.id.username);
	    final  EditText pass = (EditText) okn.findViewById(R.id.password);
	    dialogmaker.setPositiveButton("Vpis", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	  	      SharedPreferences settings = getSharedPreferences(nastavitve, 0);
		      SharedPreferences.Editor editor = settings.edit();
		      editor.putBoolean("firstrun", false);
		      editor.putString("user", user.getText().toString());
		      editor.putString("pass", pass.getText().toString());
		      editor.commit();
	        }
	     })
	     .show();
	}
	
	public void settingsDialog() {
		LayoutInflater inflater = getLayoutInflater();
		Builder dialogmaker = new AlertDialog.Builder(this);
		dialogmaker.setTitle("Nastavitve");
	    View okn = inflater.inflate(R.layout.nastavitve, null);
	    dialogmaker.setView(okn);
	    final  EditText URL = (EditText) okn.findViewById(R.id.PHP_url);
	    URL.setText(php_url);
	    dialogmaker.setPositiveButton("Shrani", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	  	      SharedPreferences settings = getSharedPreferences(nastavitve, 0);
		      SharedPreferences.Editor editor = settings.edit();
		      editor.putString("php_url", URL.getText().toString());
		      php_url = URL.getText().toString();
		      editor.commit();
	        }
	     })
	     .show();
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.zamenjaj:
	        	loginDialog();
	            return true;
	        case R.id.nastavitve:
	        	settingsDialog();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public void poslji(View v) {
		SharedPreferences settings = getSharedPreferences(nastavitve, 0);
        EditText prejemniki = (EditText) findViewById(R.id.prejemniki);
        EditText sporocilo = (EditText) findViewById(R.id.sporocilo);
        String phone = prejemniki.getText().toString();
        String msg = sporocilo.getText().toString();
        String user = settings.getString("user", "");
        String pass = settings.getString("pass", "");
        postData(user, pass, msg, phone);
	}
	
	public void postData(String user, String pass, String msg, String phone ) {
		AsyncHttpResponseHandler odzivator = new AsyncHttpResponseHandler() {
		     @Override
		     public void onSuccess(String response) {
		         kruhek(response);
		     }
		};
        RequestParams params = new RequestParams();
		params.put("user", user);
		params.put("pass", pass);
		params.put("phone", phone);
		params.put("msg", msg);
		AsyncHttpPost.post(php_url, params, odzivator);
	}
	
	public void versionCheck(View v) {
		AsyncHttpResponseHandler odzivator = new AsyncHttpResponseHandler() {
		     @Override
		     public void onSuccess(String response) {
		        	if (!response.equals(MainActivity.version)) {
		        		kruhek("na voljo so posodobitve");
		        	} else {
		        		kruhek("Ne skrbi, imaš posodobljeno");        		
		        	}
		     }
		};
		AsyncHttpPost.get("http://users.volja.net/topsecret/najdisms_version", null, odzivator);
	}
	
	public void kruhek(String besedilo){
		Toast.makeText(this, besedilo, Toast.LENGTH_SHORT).show();
	}

}
