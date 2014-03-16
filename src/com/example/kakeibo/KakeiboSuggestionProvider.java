package com.example.kakeibo;

import android.content.SearchRecentSuggestionsProvider;

public class KakeiboSuggestionProvider extends SearchRecentSuggestionsProvider {
	public KakeiboSuggestionProvider(){  
        setupSuggestions("kakeiboproviderauthority", KakeiboSuggestionProvider.DATABASE_MODE_QUERIES);  
    }  
}
