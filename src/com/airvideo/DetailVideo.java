//package com.airvideo;
//
//import java.io.IOException;
//
//import com.pocketjourney.media.StreamingMediaPlayer;
//
//import android.app.Activity;
//import android.content.Context;
//import android.media.MediaPlayer;
//import android.net.Uri;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.view.Window;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//public class DetailVideo extends Activity {
//	public static AVVideo video;
//	private Button playRaw;
//	private Button playStream;
//	private Button back;
//	private StreamingMediaPlayer audioStreamer;
//	TextView textStreamed;
//	boolean isPlaying;
//	
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		
//		final boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
//		
//		setContentView(R.layout.detail);
//		
//		if (customTitleSupported) {
//			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.video_title);
//		}
//		
//		initControls();
//	}
//	
//	private void playRaw_onClick() {
//		//Uri url = Uri.parse(video.url().toString());
//		Uri url = Uri.parse("http://www.daily3gp.com/vids/3.3gp");
//    	try { 
//    		final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
//    		if ( audioStreamer != null) {
//    			audioStreamer.interrupt();
//    		}
//    		audioStreamer = new StreamingMediaPlayer(this, textStreamed, playRaw, playStream,progressBar);
//    		//audioStreamer.startStreaming("http://www.pocketjourney.com/downloads/pj/tutorials/audio.mp3",1717, 214);
//    		audioStreamer.startStreaming("http://internet.chonbuk.ac.kr/~songwei/DD/tellme.mp3",5208, 216);
//    		//streamButton.setEnabled(false);
//    	} catch (IOException e) {
//	    	Log.e(getClass().getName(), "Error starting to stream audio.", e);            		
//    	}
//	}
//	
//	private void playStream_onClick() {
//		if (audioStreamer.getMediaPlayer().isPlaying()) {
//			audioStreamer.getMediaPlayer().pause();
//		} else {
//			audioStreamer.getMediaPlayer().start();
//			audioStreamer.startPlayProgressUpdater();
//		}
//		isPlaying = !isPlaying;
//	}
//	
//	private void initControls() {
//		textStreamed = (TextView)findViewById(R.id.TextView01);
//		playRaw = (Button)findViewById(R.id.play_raw);
//		playRaw.setOnClickListener(new OnClickListener() {
//			public void onClick(View arg0) {
//				playRaw_onClick();
//			}
//		});
//		
//		playStream = (Button)findViewById(R.id.play_stream);
//		playStream.setOnClickListener(new OnClickListener() {
//			public void onClick(View arg0) {
//				playStream_onClick();
//			}
//		});
//
//		TextView title = (TextView)findViewById(R.id.detailVideoTitle);
//		title.setText(video.name);
//		
//		ImageView frame = (ImageView)findViewById(R.id.icon);
//		frame.setImageBitmap(video.thumbnail());
//	}
//}
