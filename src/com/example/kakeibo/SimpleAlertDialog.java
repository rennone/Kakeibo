package com.example.kakeibo;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

public abstract class SimpleAlertDialog extends AlertDialog {

	protected DialogHolderActivity mContext;
	protected AlertDialog.Builder mBuilder;
	protected View layoutView;
	
	protected SimpleAlertDialog(DialogHolderActivity context) {
		super(context);
		mContext = context;
		mBuilder = new AlertDialog.Builder(context).setCancelable(true);
		// TODO Auto-generated constructor stub
	}
	
	protected void setLayout(int layout){
		//�R���e�L�X�g����C���t���[�^���擾
		LayoutInflater inflater = LayoutInflater.from(mContext);
		//���C�A�E�gXML����r���[(���C�A�E�g)���C���t���[�g
		layoutView = inflater.inflate(layout, null);
		mBuilder.setView(layoutView);
	}
	
	protected void setTitle(String title){
		mBuilder.setTitle(title);
	}
	protected View getLayout(){
		return layoutView;
	}
	abstract void create();
	
}
