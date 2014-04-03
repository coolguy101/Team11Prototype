package com.example1.locationapp;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import Controller.BitmapConverter;
import Controller.LocalFileLoder;
import Controller.LocalFileSaver;
import Controller.SubCommentModel;
import Controller.SubCommentSort;
import Model.CommentUser;
import Model.Comments;
import Model.FavouriteComment;
import Model.FavouriteModel;
import Model.IDModel;
import Model.UserModel;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
/**
 * this class is to control the sub-comments part
 * It will find sub-comment of the master comment
 * @author qyu4
 *
 */
public class SubCommetsRead extends Activity {
	public static final String SERVER = "http://cmput301.softwareprocess.es:8080/cmput301w14t11/";
	public static final String MASTERCOMMENT = "emouse/";
	private ListView listViewSubComment;
	private EditText editText;
	private int index;
	private Button button1;
	private cutadapter ListAdapter;
	private ArrayList<Comments> comment_list;
	private Bitmap bitmap;
	private int number;
	private Context content;
	private Location location;
	private GPSTracker gps;
	private HttpClient httpclient;
	private double longitude;
	private double latitude;
	private int subCoId = 1;
	private Gson gson = new Gson();
	private CommentUser someuser;
	double radius = 0.01;
	private IDModel id_obj;
	private int ServerID;
	private LocalFileLoder fileLoder = new LocalFileLoder(this);
	private LocalFileSaver fileSaver = new LocalFileSaver(this);
	private UserModel user;
	private Comments comment1;
	private String subCommentsTitle;
	private int replyFloor=0;

	private SubCommentModel subModel = new SubCommentModel(
			comment1);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sub_commets_read);
		listViewSubComment = (ListView) findViewById(R.id.listViewSubComments);
		editText = (EditText) findViewById(R.id.editTextSubmitSubComments);
		button1 = (Button) findViewById(R.id.buttonSaveSubComments);
		button1.setText("Send");
		comment_list = new ArrayList<Comments>();
		
		ActionBar bar = getActionBar();
		bar.setDisplayHomeAsUpEnabled(false);
		httpclient = new DefaultHttpClient();
		Intent intent = getIntent();
		number = intent.getIntExtra("masterID", 0);
		id_obj = new IDModel(0);
		gps = new GPSTracker(this);
		ListAdapter = new cutadapter(SubCommetsRead.this, R.layout.listlayout,comment_list);
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				
				comment_list =subModel.get_comments(comment_list, number,httpclient);
				subCoId=comment_list.size()+1;
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				
				super.onPostExecute(result);
				Collections.sort(comment_list, new SubCommentSort());
				ListAdapter.notifyDataSetChanged();
			}

		}.execute();

		content = this;
		if (gps.canGetLocation) {
			location = gps.getLocation();
			System.out.println(location + "wocao");
			gps.stopUsingGPS();
		} else {
			gps.showSettingsAlert();
		}

		System.out.println("lol" + location);

		longitude = location.getLongitude();
		latitude = location.getLatitude();
		View footerView = ((LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.footlayout, null, false);

		listViewSubComment.addFooterView(footerView);
		editText.setHint("reply to 1");
		listViewSubComment.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				replyFloor = arg2;
				editText.setHint("reply to "+(replyFloor+1));
			}
		});
		listViewSubComment.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				AlertDialog.Builder builder = new AlertDialog.Builder(content);
				String items[] = { "Edit Comment", "Add Tags","View profile" };
				builder.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						switch (which) {
						case 0:
							String CheckName = user.getUser_name();
							final Comments SelectedComment = comment_list.get(position);
							if(SelectedComment.getUserName().equals(CheckName))
							{  
								System.out.println("edit my comment");
							
								final Dialog dialogui = new Dialog(content);
								dialogui.setContentView(R.layout.dialogui);
								dialogui.setTitle("Edit my comment");
								dialogui.show();
								Button Changebutton = (Button) dialogui.findViewById(R.id.button1);
								Button Locationbutton = (Button) dialogui.findViewById(R.id.button2);
								final EditText titleedit = (EditText) dialogui.findViewById(R.id.editText1);
								final EditText subjectedit = (EditText) dialogui.findViewById(R.id.editText2);
								Changebutton.setOnClickListener(new OnClickListener() {
									
									@Override
									public void onClick(View v) {
										comment_list.get(position).setThe_comment(titleedit.getText().toString());
										//System.out.println("comment has changed"+comment_array.get(arg2).getThe_comment());
										comment_list.get(position).setSubject_comment(subjectedit.getText().toString());
										ListAdapter.notifyDataSetChanged();
										dialogui.dismiss();
										
										new AsyncTask<Void,Void,Void>()
										{

											@Override
											protected Void doInBackground(
													Void... params) {
												HttpClient httpclient = new DefaultHttpClient();
												HttpPost httpPost = new HttpPost("http://cmput301.softwareprocess.es:8080/cmput301w14t11/emouse/"+comment_list.get(position).getMaster_ID());
												try {
													StringEntity data = new StringEntity(gson.toJson(comment_list.get(position)));
													httpPost.setEntity(data);
													httpPost.setHeader("Accept", "application/json");
													HttpResponse response = httpclient.execute(httpPost);
													System.out.println(response.getStatusLine().toString() + "testing");
													
												} catch (UnsupportedEncodingException e) {

													e.printStackTrace();
												} catch (ClientProtocolException e) {

													e.printStackTrace();
												} catch (IOException e) {

													e.printStackTrace();
												}
												return null;
											}
											
										}.execute();
										
										Toast.makeText(content,"Comment has changed",Toast.LENGTH_SHORT).show();
									}
								});
							}
							else
							{
								Toast.makeText(content,"You can only edit your own comment",Toast.LENGTH_SHORT).show();
							}
							break;
						case 1:
							Intent intent = new Intent();
							intent.setClass(SubCommetsRead.this,
									TagActivity.class);
							index = position;
							startActivityForResult(intent, 1258);
							break;
						case 2:
							final String name = comment_list.get(position).getUserName();
							new AsyncTask<Void, Void, Void>()
							{
								@Override
								protected void onPostExecute(Void result) {
									super.onPostExecute(result);
									if (flag==0)
									{
										AlertDialog.Builder builder = new AlertDialog.Builder(SubCommetsRead.this);
										builder.setTitle("User has did not create profile");
										builder.setMessage("Please check back later");
										builder.setCancelable(true);
										builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
											
											@Override
											public void onClick(DialogInterface dialog, int which) {
												dialog.cancel();
												
											}
										});
										AlertDialog adialog = builder.create();
										adialog.show();
									}
								}
								int flag = 0;
								@Override
								protected Void doInBackground(Void... params) {
									try{
										Gson gson = new Gson();
										
										HttpPost httppost = new HttpPost("http://cmput301.softwareprocess.es:8080/cmput301w14t11/profile/_search?pretty=1");
										String query_profile = "{\"query\":{\"match\":{\"name\":\""+name+"\"}}}";
										StringEntity entity;
										entity = new StringEntity(query_profile);
										httppost.setHeader("Accept", "application/json");
										httppost.setEntity(entity);
										HttpResponse response = httpclient.execute(httppost);
										String json1 = subModel.getEntityContent(response);
										Type elasticSearchSearchResponseType = new TypeToken<ElasticSearchSearchResponse<CommentUser>>() {
										}.getType();
										ElasticSearchSearchResponse<CommentUser> esResponse = gson.fromJson(
												json1, elasticSearchSearchResponseType);
										
										
										for(ElasticSearchResponse<CommentUser> r : esResponse.getHits())
										{   // get some result, then flag is 1
											someuser = r.getSource();
											flag=1;
											break;
										}
										System.out.println(json1+"profilehehe");
										
										
										if (flag==1)
										{
											// have result , result code 939
											Intent intent_profile = new Intent();
											intent_profile.setClass(content, ProfileActivity.class);
											Bundle bundle = new Bundle();
											intent_profile.putExtra("name",someuser);
											startActivityForResult(intent_profile, 939);
										}
										
										}
										 catch (ClientProtocolException e) {
											e.printStackTrace();
										} catch (IOException e) {
											e.printStackTrace();
										}
										return null;
									
								}
								
							}.execute();
							break;
						}

					}
				});
				AlertDialog dialog = builder.create();
				dialog.show();
				return false;
			}
		});
	
		listViewSubComment.setAdapter(ListAdapter);
		footerView.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				editText.setHint("reply to 1");
				new AsyncTask<Void, Void, Void>() {
					
					@Override
					protected Void doInBackground(Void... params) {
						comment_list.clear();
						comment_list=subModel.get_comments(comment_list, number,httpclient);
						
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						
						super.onPostExecute(result);
						Collections.sort(comment_list, new SubCommentSort());
						ListAdapter.notifyDataSetChanged();
					}

				}.execute();
			}
		});

		button1.setOnClickListener(new MyButton1Listener());
	}

	/**
	 * there are two sub-munu list which is using for saving favourite and
	 * another is using for save, it will help us save the comments and
	 * sub-comments in the file.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.fav:
			
			faviSaving(0);
			
			break;

		case R.id.save:
			
			faviSaving(1);

			break;

		}
		
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Save the Faviourite comments into the local file
	 * @param code - used to determine between faviourite and saving
	 */
	public void faviSaving(int code)
	{
		user = new UserModel();
		user = fileLoder.loadFromFile();
		boolean saved=false;
		if (user.getUser_name().equals(""))
		{
			Toast.makeText(SubCommetsRead.this,
					"Guest cannot add this comment",
					Toast.LENGTH_SHORT).show();
		}
		else
		{
			for (int i=0; i<user.getFaviourte().size(); i++)
			{
				if (user.getFaviourte().get(i).getID()==number && user.getFaviourte().get(i).getUsername().equals(user.getUser_name()) && user.getFaviourte().get(i).getCode() == code)
				{
					saved=true;
				}
			}
			
			if (saved == true)
			{
				Toast.makeText(SubCommetsRead.this,
						"You already saved this comment",
						Toast.LENGTH_SHORT).show();
			}
			else
			{
				FavouriteComment fc = new FavouriteComment();
				ArrayList<FavouriteComment> subcomment = new ArrayList<FavouriteComment>();
				fc.setText(comment_list.get(0).getSubject_comment());
				fc.setTitle(comment_list.get(0).getThe_comment());
				fc.setImage(comment_list.get(0).getImage_encode());
				fc.setDistance(comment_list.get(0).getDistance());
				fc.setUserName(comment_list.get(0).getUserName());
				fc.setLocation(location.getLatitude(), location.getLongitude());
				fc.setID(number);
				
				for (int i =1;i<comment_list.size();i++)
				{
					FavouriteComment sub = new FavouriteComment();
					sub.setText(comment_list.get(i).getSubject_comment());
					sub.setTitle(comment_list.get(i).getThe_comment());
					sub.setImage(comment_list.get(i).getImage_encode());
					sub.setDistance(comment_list.get(i).getDistance());
					sub.setUserName(comment_list.get(i).getUserName());
					sub.setLocation(location.getLatitude(), location.getLongitude());
					subcomment.add(sub);
				}
					

				FavouriteModel favi = new FavouriteModel(user.getUser_name(), fc,
						subcomment);
				favi.setID(number);
				favi.setCode(code);
				user.addFaviourte(favi);
				fileSaver.saveInFile(user);
			}
			
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sub_commets_read, menu);
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_OK)
		{
			switch(requestCode)
			{
				case 2828:
					String file = data.getStringExtra("image");
					String file2 = data.getStringExtra("choseimage");
					if (file != null) {
						bitmap = BitmapFactory.decodeFile(file);
						System.out.println("haha" + file);
					} else {
						bitmap = BitmapFactory.decodeFile(file2);
						System.out.println("haha2" + file);
					}
					if(bitmap!=null)
					{
						Toast.makeText(SubCommetsRead.this, "Picture added", Toast.LENGTH_SHORT).show();
					}
					//imageview.setImageBitmap(bitmap);
					break;
				}
					
		}
		
	}

	public void picture(View v)
	{
		Intent intent = new Intent(this, ChoseImageActivity.class);
		startActivityForResult(intent,2828);
	}
	class MyButton1Listener implements OnClickListener {

		@Override
		public void onClick(View v) {
			
			user = fileLoder.loadFromFile();
			if (user.getUser_name().equals(""))
			{
				Toast.makeText(getBaseContext(),
						"Guest cannot add comments!!!",
						Toast.LENGTH_SHORT).show();
			}
			else
			{
				String title = editText.getText().toString();
				radius = radius + 0.01;
				if ("".equals(title)) {

					Toast.makeText(getBaseContext(),
							"Title is empty! add some words please!",
							Toast.LENGTH_SHORT).show();

				} else {
					new AsyncTask<Void, Void, Void>() {
						ProgressDialog dialog1 = new ProgressDialog(content);

						@Override
						protected void onPreExecute() {
							
							dialog1.setTitle("Loading cause your internet is too slow!");
							dialog1.show();
							super.onPreExecute();
							new AsyncTask<Void, Void, Void>() {

								@Override
								protected Void doInBackground(Void... params) {
									
									ServerID = get_id();
									ServerID++;
									System.out.println(comment_list.size() + "size"
											+ ServerID);
									return null;
								}

							}.execute();

						}

						@Override
						protected Void doInBackground(Void... params) {
							
							if (bitmap == null) {
								subCommentsTitle=subCoId+". Relpy to ";
								user = fileLoder.loadFromFile();
								final Comments new_comment = new Comments(0,
										number, subCoId, 0, (subCommentsTitle+" "+(replyFloor+1)).toString(), editText.getText()
												.toString(), new Date(),
										longitude, latitude, user.getUser_name());
								subModel.insertMaster(new_comment, ServerID);
								subCoId++;
								replyFloor =0;
							} else {
								System.out.println("image posted");
								subCommentsTitle=subCoId+". Relpy to ";
								//String encode_image = convert_image_to_string(bitmap);
								JsonElement encode_image = new BitmapConverter().serialize(bitmap, null, null);
								final Comments new_comment = new Comments(0,
										number, subCoId, 0, (subCommentsTitle+" "+(replyFloor+1)), editText.getText()
												.toString(), new Date(),
										longitude, latitude, encode_image,
										user.getUser_name());
								subModel.insertMaster(new_comment, ServerID);
								subCoId++;
								replyFloor =0;
								
							}
							
					
							return null;
						}

						@Override
						protected void onPostExecute(Void result) {
							
							super.onPostExecute(result);
							new AsyncTask<Void, Void, Void>() {

								@Override
								protected Void doInBackground(Void... params) {
									
									id_obj.setId_for_master(ServerID);
									try {
										subModel.insert(id_obj,content);
									} catch (IllegalStateException e) {
										
										e.printStackTrace();
									} catch (IOException e) {
										
										e.printStackTrace();
									}
									return null;
								}

							}.execute();
                        dialog1.dismiss();
                        bitmap=null;
                        editText.setText("");
						}

					}.execute();

					setResult(RESULT_OK);
					
				}
			}
			
		}
	}

	public int get_id() {
		
		IDModel id_toReturn;// this is ID object from server
		int id = 0;
		try {
			// IDModel id_toReturn ;// this is ID object from server
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(
					"http://cmput301.softwareprocess.es:8080/testing/lab111/1");
			httpget.addHeader("Accept", "application/json");

			HttpResponse response = httpclient.execute(httpget);

			String json = subModel.getEntityContent(response);

			// We have to tell GSON what type we expect
			Type elasticSearchResponseType = new TypeToken<ElasticSearchResponse<IDModel>>() {
			}.getType();
			// Now we expect to get a Recipe response
			ElasticSearchResponse<IDModel> esResponse = gson.fromJson(json,
					elasticSearchResponseType);
			// We get the recipe from it!
			id_toReturn = esResponse.getSource();
			System.out.println();

			id = id_toReturn.getId_for_master();

			// System.out.println(recipe.toString());
			// httpget.releaseConnection();
		} catch (ClientProtocolException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		} catch (NullPointerException e) {
			
			Toast.makeText(content, "no internet", Toast.LENGTH_SHORT).show();
		} catch (RuntimeException e) {
			
			Toast.makeText(content, "no internet", Toast.LENGTH_SHORT).show();
		}
		return id;

	}

	public String convert_image_to_string(Bitmap bitmap) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
		byte[] byteArray = byteArrayOutputStream.toByteArray();
		String encoded = Base64.encodeToString(byteArray, Base64.NO_WRAP);
		return encoded;
	}


	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent intent = new Intent();
		intent.setClass(SubCommetsRead.this, MainActivity.class);
		
	}

	

}
