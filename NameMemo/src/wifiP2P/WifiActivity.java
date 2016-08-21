package wifiP2P;

import de.leander.projekt.R;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

public class WifiActivity extends Activity {
	private ListView devLv;
	private TextView devTv;

	private WifiP2pManager manager;
	private BroadcastReceiver receiver;
	private Channel channel;
	private IntentFilter filter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bluetooth);
		
		devLv = (ListView) findViewById(R.id.lvDevices);
		devTv = (TextView) findViewById(R.id.tvDevices);
		
		manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		channel = manager.initialize(this, getMainLooper(), null);
		receiver = new WifiDirectBroadcastReceiver(manager, channel, this);
		
		filter = new IntentFilter();
		filter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		filter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
	    filter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
	    filter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(receiver, filter);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
			}
			@Override
			public void onFailure(int reason) {
				// TODO Auto-generated method stub
			}
		});
	}
	
	private void connect() {
		//obtain a peer from the WifiP2pDeviceList
		WifiP2pDevice device = null; //XXX
		WifiP2pConfig config = new WifiP2pConfig();
		config.deviceAddress = device.deviceAddress;
		manager.connect(channel, config, new ActionListener() {
			@Override
			public void onSuccess() {
				//success logic
			}
			@Override
			public void onFailure(int reason) {
				//failure logic
			}
		});
	}
}
