package database;

import android.content.Context;
import android.graphics.Color;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * TODO
 */
public class DbView extends RelativeLayout {

	private TextView textview;

	public DbView(Context context) {
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

	public void configure(Picture pic) {
		textview.setText(pic.getShowAs());
	}
}
