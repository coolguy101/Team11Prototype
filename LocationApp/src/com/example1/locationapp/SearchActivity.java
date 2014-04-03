package com.example1.locationapp;

import java.lang.reflect.Type;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import InternetConnection.ConnectToInternet;
import InternetConnection.ElasticSearchResponse;
import InternetConnection.ElasticSearchSearchResponse;
import Model.Comments;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example1.locationapp.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * This activity for user search the comment by time.
 * @author zuo2
 *
 */
public class SearchActivity extends Activity {
    cutadapter adapter2;
    ArrayList<Comments> comment_list;
    ListView listview2;
    HttpClient httpclient;
    Gson gson;
    private String query;
    private ConnectToInternet connect = new ConnectToInternet();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_search);
		ActionBar bar = getActionBar();
		bar.setTitle("Search Result");
		httpclient = new DefaultHttpClient();
		gson = new Gson();
		handleIntent(getIntent());
	}

	public void onNewIntent(Intent intent) {
		setIntent(intent);
		handleIntent(intent);
	}

	public void onListItemClick(ListView l, View v, int position, long id) {
		// call detail activity for clicked entry
	}

	private void handleIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			query = intent.getStringExtra(SearchManager.QUERY);
			System.out.println("query is:"+query);
			
			
			doSearch(query);
			
		}
		//listview2.setAdapter(adapter);
	}

	private void doSearch(String queryStr) {
		// get a Cursor, prepare the ListAdapter
		// and set it
		listview2 =(ListView) findViewById(R.id.search_list);
		comment_list = new ArrayList<Comments>();
		
		
		adapter2 = new cutadapter(SearchActivity.this,R.layout.listlayout, comment_list);
		listview2.setAdapter(adapter2);
		comment_list.clear();
		new AsyncTask<Void,Void,Void>() {

			@Override
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				adapter2.notifyDataSetChanged();
			}

			@Override
			protected Void doInBackground(Void... params) {
				try
				{
				System.out.println("doing search");
				HttpPost httpPost = new HttpPost("http://cmput301.softwareprocess.es:8080/cmput301w14t11/emouse/_search?pretty=1");
				String query_range2 = "{\"query\":{\"bool\":{\"must\":{\"terms\":{\"TagsList\":["+"\""+query+"\""+"],minimum_match:1}}}}}";
				StringEntity entity = new StringEntity(query_range2);
				httpPost.setHeader("Accept", "application/json");
				httpPost.setEntity(entity);
				HttpResponse response = httpclient.execute(httpPost);
				
				String json1 = connect.getEntityContent(response);
				System.out.println(response.getStatusLine().toString() + "status");
				System.out.println("search result is the:"+json1);
				Type elasticSearchSearchResponseType = new TypeToken<ElasticSearchSearchResponse<Comments>>() {
				}.getType();
				ElasticSearchSearchResponse<Comments> esResponse = gson.fromJson(
						json1, elasticSearchSearchResponseType);
				for (ElasticSearchResponse<Comments> r : esResponse.getHits())
				{
					comment_list.add(r.getSource());
				}
				
				}
				catch(Exception e)
				{
					
				}
				return null;
			}
		}.execute();
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_search,
					container, false);
			return rootView;
		}
	}

}
