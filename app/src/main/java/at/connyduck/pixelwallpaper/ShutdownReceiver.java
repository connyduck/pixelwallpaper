package at.connyduck.pixelwallpaper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ShutdownReceiver extends BroadcastReceiver {
    private PixelWallpaperService.PixelWallpaperEngine pwe;

    public ShutdownReceiver(PixelWallpaperService.PixelWallpaperEngine pwe) {
    	this.pwe = pwe;
    }
    
	@Override
	public void onReceive(Context context, Intent intent) {

        if(Intent.ACTION_SHUTDOWN.equals(intent.getAction())) {
        	pwe.onShutdown();
        }
		
	}
}