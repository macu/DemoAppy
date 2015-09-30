package io.macu.demoappy.ui;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.macu.demoappy.R;
import io.macu.demoappy.dbms.DemoDB;
import io.macu.demoappy.model.User;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

	private DemoDB demoDB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		findViewById(R.id.btn_fetch_user).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				beginFetchUser();
			}
		});

		// Create and retain a demo database for use in this activity.
		// If the database is on-disk, all DemoDB instances will read/write to the same database.
		demoDB = new DemoDB(this);

		// Start fresh.
		demoDB.discardAllUsers();
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
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (demoDB != null) {
			demoDB.close();
			demoDB = null;
		}
	}

	private void beginFetchUser() {

		// Show progress dialog.
		final ProgressDialog prog;
		prog = ProgressDialog.show(this, "Fetching user...", "PLEASE WAIT", true, false);

		// Do the fetch in a background thread, and handle the result on the main thread.
		(new AsyncTask<Void, Void, Void>() {

			OkHttpClient client = new OkHttpClient();
			Request request;
			Response response;
			IOException ex;

			@Override
			protected Void doInBackground(Void... params) {

				// Build request.
				request = new Request.Builder()
						.url("http://jaredeverett.ca/geo2/fetchUser.php")
						.build();

				// Execute the request and retain the result.
				try {
					response = client.newCall(request).execute();
				} catch (IOException e) {
					ex = e;
				}

				return null;
			}

			/**
			 * Invoked on the main thread after background work is done.
			 */
			@Override
			protected void onPostExecute(Void result) {

				// Dismiss progress dialog.
				prog.dismiss();

				if (ex != null) {
					// Exception occurred when executing request.
					notifyException("Failed to fetch", ex);
					return;
				}

				if (response.code() != 200) {
					// Currently expecting 200 OK.
					notifyException("Failed to fetch", new IOException("Unexpected response code: " + response.code()));
					return;
				}

				// Handle the response.
				try {
					String body = response.body().string();
					JSONObject object = new JSONObject(body);
					Timber.d("Parsed JSON object from fetch response", object);
					handleUserFetchResult(object);
				} catch (IOException e) {
					notifyException("Failed to read response", e);
				} catch (JSONException e) {
					notifyException("Failed to parse response", e);
				}
			}

		}).execute();
	}

	private void notifyException(String title, Exception ex) {
		Timber.e(title, ex);
		(new AlertDialog.Builder(this))
				.setTitle(title)
				.setMessage(ex.getMessage())
				.setNeutralButton("Alright", null)
				.show();
	}

	private void handleUserFetchResult(JSONObject o) {
		try {

			// Let's treat this object as a user.
			String id = o.getString("id");
			String username = o.getString("username");
			User u = new User(id, username);

			// Persist for local use.
			demoDB.persist(u);

			// Load the new user from the database (to demonstrate it was persisted):
			u = demoDB.getUserById(id);

			// Do something with the user now or later.
			Toast.makeText(this,
					"Loaded user with id: " + u.id + "; username: " + u.username,
					Toast.LENGTH_LONG
			).show();

		} catch (JSONException ex) {
			notifyException("Failed to extract user info", ex);
		}
	}

}
