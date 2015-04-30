package mike.huji;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends Activity {

	private static final String TAG = null;
	private static final int REQUEST_RESULT = 0;
	protected static final int PICKFILE_RESULT_CODE = 1;
    private Button addButton;
    private Context context;
    private ListView listview;
    private List<String> fileNamesList = new ArrayList<String>();
    private SharedPreferences sharedPref;
    private StableArrayAdapter adapter;
	
	/**
	 * Part 1: Full Activity life cycle 
	 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * The activity is being created.
         * 1. Open threads
         * 2. Initialize a Loader
         */
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Check if need to open welcome screen
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPref.getBoolean("isShowWelcome", true)) {  //else continue run this MainActivity...
        	Intent intent = new Intent(this, ScreenSlidePagerActivity.class);
        	startActivity(intent);       	
        }
        if (sharedPref.getInt("array_size", 0) == 0) {
        	Editor edit = sharedPref.edit();
        	edit.putInt("array_size", 0);
        	edit.commit();
        }
        //UI
        context = getApplicationContext();
        addButton = (Button) findViewById(R.id.add_file);
        //populate the list
        PopulateList();
        listview = (ListView) findViewById(R.id.listview);  
        adapter = new StableArrayAdapter(this, android.R.layout.simple_list_item_1, fileNamesList);
    	listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
        		final String item = (String) parent.getItemAtPosition(position);
        		String uriPath = sharedPref.getString(item, null);
    			Editor edit = sharedPref.edit();
    			edit.putString("file_to_open", uriPath);
    			edit.commit();
    			Intent intent = new Intent(context, DisplayActivity.class);
    			startActivity(intent);
        	}
        });
		//Handling add new file
        addButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("*/*");
				startActivityForResult(intent, PICKFILE_RESULT_CODE);		   
	        }
	    });
    }
    
    //populate the list
    private void PopulateList() {
        fileNamesList.clear();
        int size = sharedPref.getInt("array_size", 0);
        for (int i=0; i<size; i++) {
        	fileNamesList.add((String)sharedPref.getString("array_" + i, null));
        }
    }
    
    private class StableArrayAdapter extends ArrayAdapter<String> {
    	HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();
        public StableArrayAdapter(Context context, int textViewResourceId, List<String> objects) {
        	super(context, textViewResourceId, objects);
        	for (int i = 0; i < objects.size(); ++i) {
        		mIdMap.put(objects.get(i), i);
        	}
        }
        @Override
        public long getItemId(int position) {
        	String item = getItem(position);
        	return mIdMap.get(item);
        }
        @Override
        public boolean hasStableIds() {
        	return true;
        }
        
        public void clearAndSetHashMap(List<String> objects) {
        	mIdMap.clear();
        	for (int i = 0; i < objects.size(); ++i) {
        		mIdMap.put(objects.get(i), i);
        	}
        }
	}
    
    @Override
    protected void onStart() {
    	/**
    	 * The activity is about to become visible.
    	 * 1. Register a BroadcastReceiver
    	 */
        super.onStart();
    }
    
    @Override
    protected void onResume() {
    	/**
    	 * The activity has become visible (it is now "resumed").
    	 * 1. Lightweight code
    	 */
        super.onResume();
        PopulateList();
        adapter.clearAndSetHashMap(fileNamesList);
        adapter.notifyDataSetChanged();
    }
    
    @Override
    protected void onPause() {
    	/**
    	 * Another activity is taking focus (this activity is about to be "paused").
    	 * 1. Lightweight code
    	 * 2. The last method that's guaranteed to be called before the process can be killed,
    	 * 	  Therefore here make crucial cleanup & backup, but not too much (1 above).
    	 * 3. 
    	 */
        super.onPause();
    }
    
    @Override
    protected void onStop() {
        /**
         * The activity is no longer visible (it is now "stopped").
         * 1. Unregister a BroadcastReceiver
         */
        super.onStop();
    }
    
    @Override
    protected void onDestroy() {
    	/**
    	 * The activity (Activity object!) is about to be destroyed.
    	 * 1. Destroy threads
    	 */
        super.onDestroy();
    }
    
    @Override
    protected void onSaveInstanceState (Bundle outState) {
    	/**
    	 * Called before onStop() and possibly before onPause() when `accidently`
    	 * the activity destroyed. This is a way to save state. 
    	 * Then, if the system kills application process and the user navigates 
    	 * back to your activity, the system recreates the activity and passes 
    	 * the Bundle to both onCreate() and onRestoreInstanceState().
    	 */
    	super.onSaveInstanceState(outState);
    }
    
    //Result from place picker
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == PICKFILE_RESULT_CODE) {
    		String displayName = null;
    		String uriString = null;
    		if (resultCode == RESULT_OK) {
    			// Get the Uri of the selected file
                Uri uri = data.getData();
                uriString = uri.toString();
                File myFile = new File(uriString);                
                String path = myFile.getAbsolutePath(); 
                if (uriString.startsWith("content://")) {                   
                    Cursor cursor = null;
                    try {                           
                        cursor = context.getContentResolver().query(uri, null, null, null, null);                         
                        if (cursor != null && cursor.moveToFirst()) {                               
                            displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        }
                    } finally {
                        cursor.close();
                    }
                } else if (uriString.startsWith("file://")) {           
                    displayName = myFile.getName();
                }
    			Editor edit = sharedPref.edit();
    			int currentSize = sharedPref.getInt("array_size", 0);
    			edit.putString("array_" + currentSize, displayName);
    			edit.commit();
    			edit.putInt("array_size", currentSize+1);
    			edit.commit();
    			edit.putString(displayName, path);
    			edit.commit();
    			
    			Uri myUri = Uri.parse(path);
                BufferedReader br;
        		try {
        			File my2File = new File(myUri.getPath());
        			br = new BufferedReader(new FileReader(my2File));
        			String line = null;
        			while ((line = br.readLine()) != null) {
        				Log.e("TAG", line);
        			}
        			br.close();
        		} catch (FileNotFoundException e) {
        			e.printStackTrace();
        		} catch (IOException e) {
        			e.printStackTrace();     
            	}
    			
    			
    			
    	        PopulateList();
    	        adapter.clearAndSetHashMap(fileNamesList);
    	        adapter.notifyDataSetChanged();
            }
    		else super.onActivityResult(requestCode, resultCode, data);
    	}
    	else {
    		super.onActivityResult(requestCode, resultCode, data);
    	}
	}

    /**
     * Part 2: Options menu: Action Bar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	/**
    	 * Inflate the menu; this adds items to the action bar if it is present.
    	 */
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	/**
    	 * Handle action bar item clicks here. The action bar will
    	 * automatically handle clicks on the Home/Up button, so long
    	 * as you specify a parent activity in AndroidManifest.xml.
    	 */
        switch (item.getItemId()) {
        	case R.id.show_welcome:
        		startActivity(new Intent(this, ScreenSlidePagerActivity.class));
            	return true;
        	case R.id.terms:
                //TODO
            	return true;
        	case R.id.legal_notice:
        		//TODO
            	return true;
        	case R.id.privacy_policy:
        		//TODO
            	return true;
        	default:
        		return super.onOptionsItemSelected(item);
        }
    }
	
}