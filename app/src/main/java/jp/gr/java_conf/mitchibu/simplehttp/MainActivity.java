package jp.gr.java_conf.mitchibu.simplehttp;

import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import jp.gr.java_conf.mitchibu.lib.simplehttp.SimpleHTTP;
import jp.gr.java_conf.mitchibu.lib.simplehttp.request.StringRequest;


public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		TestRequest request = new TestRequest();
		new SimpleHTTP(this).exec(request, new SimpleHTTP.Listener<String>() {
			@Override
			public void onDone(Future<SimpleHTTP.Response<String>> future) {
				try {
					SimpleHTTP.Response<String> response = future.get();
					android.util.Log.v("test", "test");
				} catch(Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onCancelled(Future<SimpleHTTP.Response<String>> future) {
			}
		});
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if(id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	class TestRequest extends StringRequest {
		public TestRequest() {
			super(Method.GET, Uri.parse("https://cpfaplbasicout.famima.com/CMN/api/news?top=1"));
		}

		@Override
		protected int getConnectTimeout() throws Exception {
			return 10000;
		}

		@Override
		protected int getReadTimeout() throws Exception {
			return super.getReadTimeout();
		}
	}
}
