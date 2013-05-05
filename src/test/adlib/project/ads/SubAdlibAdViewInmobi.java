/*
 * adlibr - Library for mobile AD mediation.
 * http://adlibr.com
 * Copyright (c) 2012-2013 Mocoplex, Inc.  All rights reserved.
 * Licensed under the BSD open source license.
 */

/*
 * confirmed compatible with Inmobi SDK 3.6.2
 */

package test.adlib.project.ads;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;

import com.inmobi.androidsdk.IMAdListener;
import com.inmobi.androidsdk.IMAdRequest;
import com.inmobi.androidsdk.IMAdRequest.ErrorCode;
import com.inmobi.androidsdk.IMAdView;
import com.mocoplex.adlib.SubAdlibAdViewCore;

/*
 AndroidManifest.xml 에 아래 내용을 추가해주세요.

 <activity android:name="com.inmobi.androidsdk.IMBrowserActivity"
 android:configChanges="keyboardHidden|orientation|keyboard|smallestScreenSize|screenSize" /> 
 */

public class SubAdlibAdViewInmobi extends SubAdlibAdViewCore {

	protected IMAdView ad;
	private IMAdRequest mAdRequest;
	protected boolean bGotAd = false;

	protected long lastGotAd = 0;

	// 여기에 인모비에서 발급받은 key 를 입력하세요.
	String inmobiKey = "14e6c6816e0d4d6e8950caec1d14a901";

	private int getPixels(int dipValue) {
		Resources r = getResources();
		int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				dipValue, r.getDisplayMetrics());
		return px;
	}

	public SubAdlibAdViewInmobi(Context context) {
		this(context, null);
	}

	public SubAdlibAdViewInmobi(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	protected int QFlag = 0;

	// 스케줄러에의해 자동으로 호출됩니다.
	// 실제로 광고를 보여주기 위하여 요청합니다.
	public void query() {
		// SYNC query / gotAd
		QFlag = 0;

		if (lastGotAd + (1000 * 30) < new Date().getTime()) {
			bGotAd = false;
		}

		if (!bGotAd) {
			this.removeAllViews();

			// 원하는 크기의 배너 크기를 설정하세요.
			// http://developer.inmobi.com/wiki/index.php?title=Integration_Guidelines#Android_2
			ad = new IMAdView((Activity) this.getContext(),
					IMAdView.INMOBI_AD_UNIT_320X50, inmobiKey);
			LayoutParams params = new LayoutParams(getPixels(320),
					getPixels(50));
			ad.setLayoutParams(params);

			// 광고 뷰의 위치 속성을 제어할 수 있습니다.
			this.setGravity(Gravity.CENTER);

			this.addView(ad);
		}

		// set the test mode to true (Make sure you set the test mode to false
		// when distributing to the users)
		mAdRequest = new IMAdRequest();
		// mAdRequest.setTestMode(true);

		Map<String, String> reqParams = new HashMap<String, String>();
		reqParams.put("tp", "c_adlib");
		mAdRequest.setRequestParams(reqParams);

		ad.setIMAdRequest(mAdRequest);

		// set the listener if the app has to know ad status notifications
		ad.setIMAdListener(new IMAdListener() {

			@Override
			public void onShowAdScreen(IMAdView adView) {

				// invalidate..
				bGotAd = false;
			}

			@Override
			public void onDismissAdScreen(IMAdView adView) {
			}

			@Override
			public void onAdRequestFailed(IMAdView adView, ErrorCode errorCode) {
				if (bGotAd)
					return;

				failed();
			}

			@Override
			public void onAdRequestCompleted(IMAdView adView) {

				// SYNC query / gotAd
				if (++QFlag == 1)
					gotAd();

				bGotAd = true;
				lastGotAd = new Date().getTime();
			}

			@Override
			public void onLeaveApplication(IMAdView adView) {
			}
		});

		ad.loadNewAd();

		if (bGotAd)
			gotAd();
	}

	// 광고뷰를 삭제하는 경우 호출됩니다.
	public void clearAdView() {
		if (ad != null) {
		}

		super.clearAdView();
	}

	public void onResume() {
		super.onResume();

		if (ad != null) {
		}
	}

	public void onPause() {
		super.onPause();

		if (ad != null) {
		}
	}
}
