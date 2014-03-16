package com.example.kakeibo;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.Toast;
@EActivity(R.layout.activity_search)
public class SearchActivity extends Activity {
	@ViewById(R.id.search_result)
	TableLayout search_result;
	
	@AfterViews
	void initialize(){ 
		final Intent queryIntent = getIntent();  
	    final String queryAction = queryIntent.getAction(); 
	    // ACTION_SEARCH の Intent で呼び出された場合  
	    if (Intent.ACTION_SEARCH.equals(queryAction)) {  
	    	System.out.print("search\n");
	        doSearchWithIntent(queryIntent);  
	    }
	    
	    // Quick Search Box から呼び出された場合  
	    if (Intent.ACTION_VIEW.equals(queryAction)){
	    	System.out.print("quick\n");
	        if(queryIntent.getFlags() == Intent.FLAG_ACTIVITY_NEW_TASK) {  
	            doSearchWithIntent(queryIntent);  
	        }  
	    }  
	}
	
	// 検索用 Activity から呼び出されたとき   
	@Override  
	protected void onNewIntent(Intent intent) {  
		System.out.print("itent\n");
	    doSearchWithIntent(intent);  
	}  
	  
	private void doSearchWithQuery(String query){
		Toast.makeText(this, query, Toast.LENGTH_LONG).show();
	}
	
	private void doSearchWithIntent(final Intent queryIntent) {  
	    // 検索文字列は SearchManager.QUERY というキーに入っている  
	    final String queryString = queryIntent.getStringExtra(SearchManager.QUERY);  
	    doSearchWithQuery(queryString);  
	    
	    SearchRecentSuggestions suggestions =  
	            new SearchRecentSuggestions(this, "kakeiboproviderauthority",  
	            KakeiboSuggestionProvider.DATABASE_MODE_QUERIES);  
	    suggestions.saveRecentQuery(queryString, null); 
	}  

}
