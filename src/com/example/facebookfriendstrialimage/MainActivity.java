package com.example.facebookfriendstrialimage;

import org.json.JSONArray;
import org.json.JSONObject;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.example.dbhelper.DBHelper;
import com.example.facebookfriendstrial.R;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.android.Facebook;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.UserInfoChangedCallback;
public class MainActivity extends Activity {

	private LoginButton loginBtn;
	private TextView userName;
	private UiLifecycleHelper uiHelper;
	Facebook facebook;
	private static DBHelper dbHelper;
	@SuppressWarnings("unused")
	private static SQLiteDatabase newDB;
	private Button friendsListBtn;
	ListView listView;
    
	public Context getContext(){
		return getApplicationContext();
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbHelper = new DBHelper(getApplicationContext());
		uiHelper = new UiLifecycleHelper(this, statusCallback);
		uiHelper.onCreate(savedInstanceState);

		setContentView(R.layout.activity_facebook);

		friendsListBtn = (Button) findViewById(R.id.friends_list);
		friendsListBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, SecondActivity.class);
				startActivity(intent);
			}
		});
		
		userName = (TextView) findViewById(R.id.user_name);
		loginBtn = (LoginButton) findViewById(R.id.fb_login_button);
		loginBtn.setUserInfoChangedCallback(new UserInfoChangedCallback() {
			@Override
			public void onUserInfoFetched(GraphUser user) {
				if (user != null) {
					userName.setText("Hello, " + user.getName());
				} else {
					userName.setText("You are not logged in");
				}
			}
		});
	}

	private Session.StatusCallback statusCallback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			if (state.isOpened()) {
				Log.d("FacebookSampleActivity", "Facebook session opened");
			} else if (state.isClosed()) {
				Log.d("FacebookSampleActivity", "Facebook session closed");
				session.closeAndClearTokenInformation();
			}
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
		
		Session session = Session.getActiveSession();
		if (session.isOpened()) {
			
			new Request(   session,  "/me/friends",   null,   HttpMethod.GET,   new Request.Callback() {
		        public void onCompleted(Response response) {
		            // handle the result 
		        	try {
			                	parseUserFromResponse(response);
		            	} catch ( Throwable t ) {
		                System.err.println( t );
		            }
		        }
		    }    ).executeAsync();
		}		
	}
	
	
	public static final void parseUserFromResponse( Response response ) {
		String ID = "id", NAME = "name", id, name;
		ContentValues values = new ContentValues();
		try {
	        GraphObject go  = response.getGraphObject();
	        JSONObject  jso = go.getInnerJSONObject();
	        JSONArray   arr = jso.getJSONArray( "data" );
			
			newDB = dbHelper.getWritableDatabase();
			
	        for ( int i = 0; i < ( arr.length() ); i++ ){
	        	
	            JSONObject json_obj = arr.getJSONObject( i );
	            id     = json_obj.getString("id");
	            name   = json_obj.getString("name");
	            values.put(ID, id);  
	    	    values.put(NAME, name); 
	            dbHelper.insert(values);
	        }
	    } catch ( Throwable t ) {
	        t.printStackTrace();
	    }
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onSaveInstanceState(Bundle savedState) {
		super.onSaveInstanceState(savedState);
		uiHelper.onSaveInstanceState(savedState);
	}
}