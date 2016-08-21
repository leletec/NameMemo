package wifiP2P;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;

/*
 * https://developer.android.com/guide/topics/connectivity/wifip2p.html
 */
public class WifiDirectBroadcastReceiver extends BroadcastReceiver {
	private WifiP2pManager manager;
	private Channel channel;
	private WifiActivity activity;
	private PeerListListener listener;

	public WifiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel, WifiActivity activity) {
		super();
		this.manager = manager;
		this.channel = channel;
		this.activity = activity;
		
		listener = new PeerListListener() {
			@Override
			public void onPeersAvailable(WifiP2pDeviceList peers) {
				// TODO Auto-generated method stub
			}
		};
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

		switch (action) {
		case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:
			// Check to see if Wi-Fi is enabled and notify appropriate activity
			int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
			if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
				// Wifi P2P is enabled
			} else {
				// Wifi P2P is not enabled
			}
			break;
		case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:
			// request available peers from the wifi p2p manager. This is an
			// asynchronous call and the calling activity is notified with a
			// callback on PeerListListener.onPeersAvailable()
		    if (manager != null) {
		        manager.requestPeers(channel, listener);
		    }
			break;
		case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:
			// Respond to new connection or disconnections
			break;
		case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:
			// Respond to this device's wifi state changing
		}
	}

}
