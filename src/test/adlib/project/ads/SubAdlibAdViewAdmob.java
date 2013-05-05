/*
 * adlibr - Library for mobile AD mediation.
 * http://adlibr.com
 * Copyright (c) 2012-2013 Mocoplex, Inc.  All rights reserved.
 * Licensed under the BSD open source license.
 */

/*
 * confirmed compatible with admob SDK 6.3.1
 */

package test.adlib.project.ads;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;

import com.google.ads.Ad;
import com.google.ads.AdRequest.ErrorCode;
import com.mocoplex.adlib.SubAdlibAdViewCore;

public class SubAdlibAdViewAdmob extends SubAdlibAdViewCore {

	protected com.google.ads.AdView ad;
	protected boolean bGotAd = false;

	public SubAdlibAdViewAdmob(Context context) {
		this(context, null);
	}

	public SubAdlibAdViewAdmob(Context context, AttributeSet attrs) {

		super(context, attrs);

		// 여기에 ADMOB ID 를 입력하세요.
		String admobID = "a1503e039465a8e";

		ad = new com.google.ads.AdView((Activity) this.getContext(),
				com.google.ads.AdSize.BANNER, admobID);

		// 광고 뷰의 위치 속성을 제어할 수 있습니다.
		this.setGravity(Gravity.CENTER);

		ad.setAdListener(new com.google.ads.AdListener() {

			public void onDismissScreen(Ad arg0) {

			}

			public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
				if (!bGotAd)
					failed();
			}

			public void onLeaveApplication(Ad arg0) {

			}

			public void onPresentScreen(Ad arg0) {

			}

			public void onReceiveAd(Ad arg0) {

				if (bGotAd)
					return;

				bGotAd = true;

				// 광고를 받아왔으면 이를 알려 화면에 표시합니다.
				gotAd();
			}

		});

		this.addView(ad);
	}

	// 스케줄러에의해 자동으로 호출됩니다.
	// 실제로 광고를 보여주기 위하여 요청합니다.
	public void query() {
		bGotAd = false;
		ad.loadAd(request);
	}

	private com.google.ads.AdRequest request = new com.google.ads.AdRequest();

	public void onDestroy() {
		super.onDestroy();

		if (ad != null) {
			ad.destroy();
			ad = null;
		}
	}

	public void clearAdView() {
		super.clearAdView();

		// 간헐적으로 발생하는 버그(모든 webview의 동작을 멈춤)방지를 위한 delay
		// 참고:
		// http://marblemice.blogspot.kr/2011/07/weirdest-admob-bug-ever.html
		// :
		// https://groups.google.com/forum/#!msg/google-admob-ads-sdk/lSt-PD9dg3Q/Q8ThsVes7qwJ
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (ad != null) {
					ad.stopLoading();
				}
			}
		}, 300);
	}

	public void onResume() {
		super.onResume();

		if (ad != null)
			ad.loadAd(request);
	}

	public void onPause() {
		super.onPause();

		// 간헐적으로 발생하는 버그(모든 webview의 동작을 멈춤)방지를 위한 delay
		// 참고:
		// http://marblemice.blogspot.kr/2011/07/weirdest-admob-bug-ever.html
		// :
		// https://groups.google.com/forum/#!msg/google-admob-ads-sdk/lSt-PD9dg3Q/Q8ThsVes7qwJ
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (ad != null) {
					ad.stopLoading();
				}
			}
		}, 300);
	}

}