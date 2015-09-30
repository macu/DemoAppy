package io.macu.demoappy.util;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import timber.log.Timber;

public class ResUtil {

	public static String loadRawResourceAsString(Context ctx, int id) {
		InputStream inStream = null;
		String outString = null;
		try {
			inStream = ctx.getResources().openRawResource(id);
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int count;
			while ((count = inStream.read(buffer)) >= 0) {
				outStream.write(buffer, 0, count);
			}
			outString = outStream.toString();
		} catch (IOException e) {
			Timber.e("Could not load raw resource", e);
		} finally {
			try {
				if (inStream != null) {
					inStream.close();
				}
			} catch (IOException ignored) {
			}
		}
		return outString;
	}

}
