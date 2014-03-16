package com.example.kakeibo;

import android.content.DialogInterface;
import android.widget.Spinner;

public class Setting extends SimpleAlertDialog{
	private static int textSize = 24;
	private static int textSizeList[] = {24,20,16};
	
	public Setting(DialogHolderActivity context) {
		super(context);
		setLayout(R.layout.setting_menu);
		setTitle("‰¼ƒ^ƒCƒgƒ‹");
	}
	
	public void create(){
		mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Spinner text_size = (Spinner)getLayout().findViewById(R.id.textSize);
				textSize = textSizeList[(int)text_size.getSelectedItemId()];
				mContext.DialogOK();
			}
		})
		.show();
	}
	
	public static int getTextSize(){
		return textSize;
	}
}
