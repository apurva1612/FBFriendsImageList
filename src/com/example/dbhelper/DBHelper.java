package com.example.dbhelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{
	
	public SQLiteDatabase DB;
	public String DBPath;
	public static String DBName = "Facebook_Freind";
	public static final int version = '1';
	public static Context currentContext;
	public static String tableName = "Freind_List";
	public String ID = "id", NAME = "name";
	public ContentValues values = new ContentValues();
	public DBHelper(Context context) {
		
		super(context, DBName, null, version);
		currentContext = context;
		DBPath = "/data/data/" + context.getPackageName() + "/databases/";
		boolean checkDB = checkDbExists();

/*		try {
			String myPath = DBPath + DBName;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,	SQLiteDatabase.OPEN_READONLY);

		} catch (SQLiteException e) {
		}
*/
		if (!checkDB) {
			createDatabase();
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	private void createDatabase() {
			DB = currentContext.openOrCreateDatabase(DBName, 0, null);
			DB.execSQL("CREATE TABLE IF NOT EXISTS " + tableName +	" (ID VARCHAR, NAME VARCHAR);");
	}
	
	public void insert(ContentValues values) {
		openDataBase();
	    
	    DB.insert(tableName, null, values);
	}
	
    public void openDataBase() throws SQLException{
        String myPath = DBPath + DBName;
    	DB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }
 
	private boolean checkDbExists() {
		SQLiteDatabase checkDB = null;

		try {
			String myPath = DBPath + DBName;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,	SQLiteDatabase.OPEN_READWRITE);

		} catch (SQLiteException e) {
			// database does't exist yet.
		}
		if (checkDB != null) {
			checkDB.close();
		}
		return checkDB != null ? true : false;
	}
}
