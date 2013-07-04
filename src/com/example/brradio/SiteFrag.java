package com.example.brradio;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class SiteFrag extends Fragment {
	
	private WebView webView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.site, container,false);
		
		webView = (WebView)view.findViewById(R.id.webView);
		//webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebViewClient()); // Tous les liens s'ouvriront directement depuis la WebView
		webView.loadUrl("http://metropolitanafm.uol.com.br/");
		
		//String customHtml = "<html><body><h1>Hello, WebView</h1></body></html>";
		//webView.loadData(customHtml, "text/html", "UTF-8");
		
		return view;
	}
}
