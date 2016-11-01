package design;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Abstract ListAdapter-class.
 *
 * @param <E>   the data type to be displayed
 */
public abstract class AbstractListAdapter<E> extends BaseAdapter{

	protected ArrayList<E> list;
	protected Context context;

	public AbstractListAdapter(Context context, ArrayList<E> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public abstract View getView(int position, View convertView, ViewGroup parent);

	/**
	 * Abstract class for the View to be displayed in the ListAdapter.
	 */
	protected abstract class AbstractView extends RelativeLayout{
		protected TextView tv;

		public AbstractView(Context context) {
			super(context);
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.addRule(CENTER_IN_PARENT);
			tv = new TextView(context);
			tv.setTextSize(20);
			tv.setTextColor(Color.BLACK);
			tv.setLayoutParams(params);
			addView(tv);
		}

		/**
		 * Configure the View. To be called for each entry of the list.
		 *
		 * @param e     the entry
		 */
		public abstract void configure(E e);
	}
}
