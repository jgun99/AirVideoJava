//package com.airvideo;
//
//import java.net.URL;
//import java.util.ArrayList;
//
//import com.airvideo.R;
//import com.airvideo.R.id;
//import com.airvideo.R.layout;
//
//import android.app.Activity;
//import android.app.ListActivity;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.media.MediaPlayer;
//import android.net.Uri;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//import android.widget.AdapterView.OnItemClickListener;
//
//public class Home extends ListActivity {
//	private AVFolderListAdapter _adapter;
//	private Runnable viewContents;
//	private AVClient server;
//	private ArrayList <AVResource> items ;
//	private AVFolder pwd;
//	private ProgressDialog _ProgressDialog = null;
//	
//	/** Called when the activity is first created. */
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE); // turn off title bar
//		
//		setContentView(R.layout.main); // specify ui
//		getListView().setOnItemClickListener(listlistener); // click handler
//
//		server = new AVClient("192.168.1.105", 45631, ""); // server; this should not be hard coded
//		pwd = new AVFolder(server, "root", null); // root folder of server -- this is ok to be hard coded
//		items = new ArrayList <AVResource>(); // perhaps out to be a 'contents' item off the folder
//
//		this._adapter = new AVFolderListAdapter(this, R.layout.row, items); // logic to render items arra
//		setListAdapter(this._adapter); // use that logic
//
//		// get contents of particular folder on seperate thread
//		// this probably ought to be absorbed into AVFolder
//		viewContents = new Runnable() {
//			public void run() {
//				getContents();
//			}
//		};
//		
//		// Get contents of initial folder
//		Thread thread =  new Thread(null, viewContents, "Interrogation");
//		thread.start();
//		_ProgressDialog = ProgressDialog.show(this, "Communication Status", "Interrogating AirVideoServer", true);
//	}
//
//
//	private void getContents() {
//		// get current directories items
//		items = server.ls(pwd);
//		
//		// Does this run this function (already on its own thread)
//		// in another thread still?
//		runOnUiThread(returnRes);
//	}
//	private Runnable returnRes = new Runnable() {
//		public void run() {
//			// check if resultset is present
//			if(items != null && items.size() > 0){
//				// event callback hook
//				_adapter.notifyDataSetChanged();
//				// rebuild adapter's dataset from supplied dataset
//				_adapter.clear();
//				for(int i=0;i<items.size();i++)
//					_adapter.add(items.get(i));
//			}
//			// done
//			_ProgressDialog.dismiss();
//			_adapter.notifyDataSetChanged();
//		}
//	};
//	private OnItemClickListener listlistener = new OnItemClickListener() {
//		public void onItemClick(AdapterView parent, View arg1, int position, long arg3) {
//			AVResource item = ((AVResource)parent.getItemAtPosition(position));
//			if (item instanceof AVFolder) {
//				// get children
//				_ProgressDialog = ProgressDialog.show(Home.this, "Communication Status", "Getting files in " + item.name, true);
//				pwd = server.cd((AVFolder)item);
//				Thread thread =  new Thread(null, viewContents, "Interrogation");
//				thread.start();
//			} else if (item instanceof AVVideo) {
//				URL url = ((AVVideo)item).url();
//				
//				Intent i = new Intent(Home.this, DetailVideo.class);
//				try {
//					DetailVideo.video = (AVVideo)item;
//					startActivity(i);
//				} 
//				catch (Exception e) {
//					e.printStackTrace();
//				}
//				
//				//Context context = getApplicationContext();
//				//CharSequence text = url.toString();
//				//int duration = Toast.LENGTH_SHORT;
//
//				//Toast toast = Toast.makeText(context, text, duration);
//				//toast.show();
//			}
//		}
//	};
//
//	private class AVFolderListAdapter extends ArrayAdapter<AVResource> {
//		private ArrayList <AVResource> al;
//
//
//		public AVFolderListAdapter(Context context, int textViewResourceId, ArrayList <AVResource> items) {
//			super(context, textViewResourceId, items);
//			this.al = items;
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			View v = convertView;
//			if (v == null) {
//				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//				v = vi.inflate(R.layout.row, null);
//			}
//			AVResource o = al.get(position);
//			if (o != null) {
//				TextView tt = (TextView) v.findViewById(R.id.toptext);
//				TextView bt = (TextView) v.findViewById(R.id.bottomtext);
//				ImageView iv = (ImageView) v.findViewById(R.id.icon);
//				if (tt != null) {
//					tt.setText("Name: "+o.name);
//				}
//				if(bt != null){
//					if (o instanceof AVFolder) {
//						bt.setText("Folder");
//						iv.setVisibility(View.GONE);
//					} else if (o instanceof AVVideo) {
//						bt.setText("Video");
//						iv.setImageBitmap(((AVVideo)o).thumbnail());
//					}
//					
//				}
//			}
//			return v;
//		}
//
//	}
//}