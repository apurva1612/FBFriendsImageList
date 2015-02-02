package com.example.facebookfriendstrialimage;

import java.util.ArrayList;
import com.example.dbhelper.DBHelper;
import com.example.facebookfriendstrial.R;
import com.example.imageView.LazyAdapter;
import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

public class SecondActivity extends ListActivity {
	private ArrayList<String> results = new ArrayList<String>();
	private String tableName = DBHelper.tableName;
	private SQLiteDatabase newDB;
	ImageView imageView;
	ListView list;
    LazyAdapter adapter;
    int count, i=0;
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        list=(ListView)findViewById(android.R.id.list);
        
        
        Button b=(Button)findViewById(android.R.id.button1);
        b.setOnClickListener(listener);
        
        
        openAndQueryDatabase();
        //displayResultList();
    }
    public OnClickListener listener=new OnClickListener(){
        @Override
        public void onClick(View arg0) {
            adapter.imageLoader.clearCache();
            adapter.notifyDataSetChanged();
        }
    };

	private void openAndQueryDatabase() {
		try {
			
			DBHelper dbHelper = new DBHelper(getApplicationContext());
			newDB = dbHelper.getWritableDatabase();
		
			
			Cursor c = newDB.rawQuery("SELECT distinct id, name FROM " + tableName , null);
			int count = c.getCount();
			String[] linkStrings = new String[count];
			
	    	if (c != null ) {
	    		int i=0;
	    		if  (c.moveToFirst()) {
	    			do {
	    				String id = c.getString(c.getColumnIndex("ID"));
	    				String name = c.getString(c.getColumnIndex("NAME"));
	    				results.add("Id: " + id + ", Name: " + name);
	    				linkStrings[i] = "https://graph.facebook.com/"+ id + "/picture?type=normal";
	    				i++;
	    			}while (c.moveToNext());
	    		}
	    	}
	        
	        adapter=new LazyAdapter(this, linkStrings, results);
	        list.setAdapter(adapter);

		} catch (SQLiteException se ) {
        	Log.e(getClass().getSimpleName(), "Could not create or Open the database");
        } finally {
        	if (newDB != null) 
        		newDB.execSQL("DELETE FROM " + tableName);
        		newDB.close();
        }
	}
}
