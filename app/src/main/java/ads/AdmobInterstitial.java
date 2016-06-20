package ads;

import android.app.Activity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class AdmobInterstitial extends AdListener {
	private static final String adUnitId = "ADS_INTERSTITIAL_ID";
	private InterstitialAd interstitial;
	private PreferencesHelper prefHelper;
	private Activity activity;
	
	public AdmobInterstitial(Activity activity){
		this.activity = activity;
		this.prefHelper = new PreferencesHelper(activity.getApplicationContext());
	}
	
	public void showOnStart(){
		if(prefHelper.isExist("interstitial")){
			this.showNow();
		} else{
			prefHelper.writeString("interstitial", "true");
		}		
	}
	
	public void showNow(){
		AdRequest adRequest = new AdRequest.Builder().build();
		this.interstitial = new InterstitialAd(activity);
		this.interstitial.setAdUnitId(adUnitId);
		this.interstitial.loadAd(adRequest);
		this.interstitial.setAdListener(this);
	}
	
	public void loadNewAd(){
		AdRequest adRequest = new AdRequest.Builder().build();
		this.interstitial = new InterstitialAd(activity);
		this.interstitial.setAdUnitId(adUnitId);
		this.interstitial.loadAd(adRequest);		
	}
	
	public void showLoaded(){
		if(this.interstitial !=null && this.interstitial.isLoaded()){
			this.interstitial.show();
		}		
	}
	
	public void showLoaded(boolean loadNext){
		if(this.interstitial !=null && this.interstitial.isLoaded()){
			this.interstitial.show();
			if(loadNext){
				this.loadNewAd();
			}
		}
	}
	
	@Override
	public void onAdLoaded() {
		super.onAdLoaded();
		if(this.interstitial.isLoaded()){
			this.interstitial.show();
			this.loadNewAd();
		}
	}

}
