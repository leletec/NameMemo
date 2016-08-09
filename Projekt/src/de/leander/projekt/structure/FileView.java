package de.leander.projekt.structure;

import java.io.File;

import android.content.Context;
import android.graphics.Color;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FileView extends RelativeLayout {

	private TextView textview;

	public FileView(Context context) {
		super(context);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		params.addRule(CENTER_IN_PARENT);
		textview = new TextView(context);
		textview.setTextSize(20);
		textview.setTextColor(Color.BLACK);
		textview.setLayoutParams(params);
		addView(textview);
	}

	public void configure(File file) {
		String filename = file.getName();
		textview.setText(filename);
	}
}
