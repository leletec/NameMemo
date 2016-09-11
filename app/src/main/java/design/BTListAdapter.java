package design;

import java.util.ArrayList;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * ListAdapter for BluetoothActivity.
 */
public class BTListAdapter extends AbstractListAdapter<BluetoothDevice>{

	public BTListAdapter(Context context, ArrayList<BluetoothDevice> list) {
		super(context, list);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		BtView view;
		if (convertView == null) {
			view = new BtView(context);
		} else {
			view = (BtView) convertView;
		}

		view.configure(list.get(position));
		return view;
	}

	public void add(BluetoothDevice device) {
		list.add(device);
	}
	
	private class BtView extends AbstractView {

		public BtView(Context context) {
			super(context);
		}

		@Override
		public void configure(BluetoothDevice e) {
			tv.setText(e.getName() + "\n" + e.getAddress());
		}
	}
}
