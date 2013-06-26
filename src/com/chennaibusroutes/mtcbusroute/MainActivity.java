package com.chennaibusroutes.mtcbusroute;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	AutoCompleteTextView source;
	AutoCompleteTextView destination;
	TextView test;
	Button Search;
	
	String BusNumbers;
	String StagesList;
	LinkedList<String> stages_row = new LinkedList<String>();
	

//	LinkedList<LinkedList<String>> stages_row = new LinkedList<LinkedList<String>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		String stringUrl = "http://www.greenmesg.org/dictionary/routes/chennai_bus_routes.txt?161011";
		ConnectivityManager connMgr = (ConnectivityManager) 
	        getSystemService(Context.CONNECTIVITY_SERVICE);
		
	    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();


        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(stringUrl);

        } else {
            test.setText("No network connection available.");
        }
		
		
		
        test = (TextView) findViewById(R.id.test);
        Search = (Button) findViewById(R.id.Search);              
		
	}
	


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	public void StartSearch(View view)
	{
		String source_get = source.getText().toString();
		String destination_get = destination.getText().toString();
		
		test.setText("");
		

		if(DisplayDirectRoutes(source_get,destination_get) == 0)
		{
			test.append("Since No direct routes found, trying to get indirect routes \n \n");
			
			for(String element:stages_row)
			{
				String[] stages_row_array = element.split(",");
				List<String> list =  Arrays.asList(stages_row_array);
				String source_stage = source.getText().toString();
				
				if(list.contains(source_stage))
				{
					int source_index_next = list.indexOf(source_stage) + 1;
					int source_index_prev = list.indexOf(source_stage) - 1;
					int FinTravelFwd = 0;
					int FinTravelBwd = 0;

					
					while(FinTravelFwd == 0 || FinTravelBwd == 0)
					{
						if(source_index_next  == list.size())
						{
							FinTravelFwd = 1;
						}
						else if((source_index_next == list.size() - 1) && FinTravelFwd == 0)
						{
						
							if(DisplayDirectRoutes(list.get((source_index_next)).toString(),
									destination_get) == 1)
							{
								test.append("Take " + GetBusNumber_Array()
										[stages_row.indexOf(element)] +
										"from " + source_get + " to reach " +
										list.get((source_index_next)).toString() + "\n \n");
							}
							FinTravelFwd = 1;
							
						}
						else
						{
														
							if(DisplayDirectRoutes(list.get((source_index_next)).toString(),
									destination_get) == 0)
							{
								source_index_next++;
							}
							else
							{
								test.append("Take " +GetBusNumber_Array()
										[stages_row.indexOf(element)] +
										"from " + source_get + " to reach " + 
										list.get((source_index_next)).toString() + "\n \n");

								FinTravelFwd = 1;
								FinTravelBwd = 1;
	
							}

						}
						
						if(source_index_prev  == -1)
						{
							FinTravelBwd = 1;
						}
						
						else if((source_index_prev == 0) && FinTravelBwd == 0)
						{

							if(DisplayDirectRoutes(list.get((source_index_prev)).toString(),
									destination_get) == 1)
							{
								test.append("Take " +GetBusNumber_Array()
										[stages_row.indexOf(element)] +
										"from " + source_get + " to reach " + 
										list.get((source_index_prev)).toString() + "\n \n");
							}
							FinTravelBwd = 1;
							
						}
						else
						{


							if(DisplayDirectRoutes(list.get((source_index_prev)).toString(),
									destination_get) == 0)
							{
								source_index_prev--;
							}
							else
							{
								test.append("Take " +GetBusNumber_Array()
										[stages_row.indexOf(element)] +
										"from " + source_get + " to reach " + 
										list.get((source_index_prev)).toString() + "\n \n");
								

								FinTravelFwd = 1;
								FinTravelBwd = 1;
							}

						}

					}

				}
			}
		}
	}
	
	public String[] GetBusNumber_Array()
	{
		return BusNumbers.split(",");
	}
	
	public String[] GetStagesList_Array()
	{
		return StagesList.split(",");
	}

	public int DisplayDirectRoutes(String source_get,String destination_get)
	{
		int DirectRoutesFound = 0;

		for(String element:stages_row)
		{
			if(element.contains(source_get) &&
					element.contains(destination_get))
			{
				test.append("Take " +GetBusNumber_Array()[stages_row.indexOf(element)] +
								"from " + source_get + "to reach " + destination_get + "\n");
				DirectRoutesFound = 1;
			}
		}
		return DirectRoutesFound;
	}
	
	
    // Uses AsyncTask to create a task away from the main UI thread. This task takes a 
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
       private static final String DEBUG_TAG = "Stock Quote";

	@Override
       protected String doInBackground(String... urls) {
             
           // params comes from the execute() call: params[0] is the url.
           try {
               downloadUrl(urls[0]);
               return null;
           } catch (IOException e) {
               return "Unable to retrieve web page. URL may be invalid.";
           }
       }
       
       // onPostExecute displays the results of the AsyncTask.
       @Override
       protected void onPostExecute(String result) {   
    	   
    	   List<String> list_source = Arrays.asList(GetStagesList_Array());
    	   ArrayList<String> list_source_al = new ArrayList<String>(list_source);
    	   
    	   HashSet<String> hs = new HashSet<String>();
    	   hs.addAll(list_source_al);
    	   list_source_al.clear();
    	   list_source_al.addAll(hs);
		   String UniqueSources = hs.toString();
		   String[] sourceArr = UniqueSources.split(", ");

   	    	ArrayAdapter<String> adapter_source = new ArrayAdapter<String>(getBaseContext(),
   	            android.R.layout.simple_list_item_1, sourceArr);
   		
	   		source = (AutoCompleteTextView)
	                   findViewById(R.id.Source);
	   		source.setThreshold(1);
	   		source.setAdapter(adapter_source);
	   		
	   		destination = (AutoCompleteTextView)
	                findViewById(R.id.Destination);
			destination.setThreshold(1);
			destination.setAdapter(adapter_source);

    	   
    	   test.setText("");
    	   test.append("Please start searching your routes" + "\n");
      }
       
      
	// Given a URL, establishes an HttpUrlConnection and retrieves
	// the web page content as a InputStream, which it returns as
	// a string.
	private void downloadUrl(String myurl) throws IOException {
	    InputStream is = null;
	        
	    try {
	        URL url = new URL(myurl);
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setReadTimeout(10000 /* milliseconds */);
	        conn.setConnectTimeout(15000 /* milliseconds */);
	        conn.setRequestMethod("GET");
	        conn.setDoInput(true);
	        // Starts the query
	        conn.connect();
	        int response = conn.getResponseCode();
	        Log.d(DEBUG_TAG, "The response is: " + response);
	        is = conn.getInputStream();
	
	        // Convert the InputStream into a string
	        readIt(is);

	        
	    // Makes sure that the InputStream is closed after the app is
	    // finished using it.
	    } finally {
	        if (is != null) {
	            is.close();
	        } 
	    }
	}
	
		// Reads an InputStream and converts it to a String.
		public void readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
			BufferedReader r = new BufferedReader(new InputStreamReader(stream));
			StringBuilder Stages = new StringBuilder();
			StringBuilder BusNoList = new StringBuilder();
			
			String line;
			String[] BusNoStation;

			while ((line = r.readLine()) != null) {
				BusNoStation=line.split(": ", 2);
				BusNoList.append(BusNoStation[0]).append(",");
			    Stages.append(BusNoStation[1]).append(",");
			    stages_row.add(BusNoStation[1].toString());
			}
			BusNumbers = BusNoList.toString().replace("- ", "");
			StagesList = Stages.toString();
			String debug_test = stages_row.get(0);
			int length = stages_row.size();
		}
   }
	

}
