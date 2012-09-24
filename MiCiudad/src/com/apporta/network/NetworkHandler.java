package com.apporta.network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

public class NetworkHandler {
	
	private HttpClient httpClient;
	
	private String url;
	
	public static final int POST = 2;
	
	public NetworkHandler(String u) {
		httpClient = new DefaultHttpClient();
		url = u;
	}
	
	public boolean isOnline(Context context) {
	    ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	    return (networkInfo != null && networkInfo.isConnected());
	} 
	
	public void execute(int method, String data, String address) {
		new AsyncOperations().execute(method, data, address);
	}
	
	private String post(String data, String address){
		HttpPost httpPost = new HttpPost(url + address);
		
		// Encode the data.
		try {
            httpPost.setEntity(new StringEntity(data));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
		
		// Make the Http request.
		try {
            HttpResponse response = httpClient.execute(httpPost);
            return response.toString();
        } catch (ClientProtocolException e) {
             e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		return null;
	}
	
	private class AsyncOperations extends AsyncTask {
        @Override
        protected String doInBackground(Object... params) {
        	if((Integer) params[0] == POST) {
        		return post((String) params[1], (String) params[2]);
        	}
        	return null;
        }
        
        // Takes the result of doInBackground as a parameter.
        @Override
        protected void onPostExecute(Object result) {}
    }
}
