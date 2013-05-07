# 이거어때(howabout) Android App

[![Google Play](http://developer.android.com/images/brand/en_generic_rgb_wo_45.png)](https://play.google.com/store/apps/details?id=io.recom.howabout)

This repository contains the source code for the 이거어때(howabout) Android app.

<a href="https://play.google.com/store/apps/details?id=io.recom.howabout" alt="Download from Google Play">
  <img src="https://lh3.ggpht.com/9MBiquxq_DmRHT0x-0OQ6X8uqNsKO2MahTx9gVWZ6oYTuH2BZFdvvF_oEWAKP0OxQnI" />
  &nbsp;
  <img src="https://lh3.ggpht.com/di3JSryz_jFEskTKTpw6NPmP7SgsBffYGbFOdVpoUOg0ML0uipX2qDf2YqaQwynV1Y4S" />
</a>

Please see the [issues](https://github.com/recomio/howabout-android/issues) section to report any bugs or feature requests and to see the list of known issues.

## License

* [Apache Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)


## Acknowledgements

This project uses the [Recomio API](http://recom.io) and [ListenA](http://listena.recom.io) to get K-pop music recommendations.

It also uses many other open source libraries such as:

* [ActionBarSherlock](https://github.com/JakeWharton/ActionBarSherlock)
* [ViewPagerIndicator](https://github.com/JakeWharton/Android-ViewPagerIndicator)
* [RoboGuice](http://code.google.com/p/roboguice/)
* [Universal Image Loader](https://github.com/nostra13/Android-Universal-Image-Loader)
* [RoboSpice](https://github.com/octo-online/robospice)
* [spring-android](http://www.springsource.org/spring-android)
* [Jackson](http://jackson.codehaus.org/)
* [Flurry](http://www.flurry.com/)
* [adlibr](http://adlibr.com/)


## Contributing

Please fork this repository and contribute back using
[pull requests](https://github.com/recomio/howabout-android/pulls).

Any contributions, large or small, major features, bug fixes, additional
language translations, unit/integration tests are welcomed and appreciated
but will be thoroughly reviewed and discussed.



## for Korean

안녕하세요.
안드로이드 무료 음악 추천 및 스트리밍 앱 '이거어때' 소스코드를 오픈소스로 공개합니다.

안드로이드 2.x 버전 개발을 마지막으로 2년 만에 4.x 버전으로 앱을 만들어봤습니다. 안드로이드 개발도 많이 안정화 되고 UI 등 많은 부분이 표준화 된 것을 느꼈습니다. 하지만 완성된 앱의 소스코드는 쉽게 찾아볼 수 없었기에 많은 분들에게 도움이 될 수 있지 않을까란 마음으로 공개합니다.

이 앱은 아래와 모듈을 사용해 만들어졌습니다.
* ActionBarSherlock (액션바)
* ViewPagerIndicator (뷰페이저)
* Universal Image Loader (이미지 로딩)
* RoboGuice (인스턴스 인젝션)
* RoboSpice (비동기 네트워킹)
* spring-android (RESTful)
* Jackson (JSON)
* Flurry (트래픽 분석)
* adlibr (광고)

범용적이고 널리 사용되는 라이브러리를 골라 사용하였으며 UI와 액티비티 구성에서 최대한 안드로이드가 제시하는 가이드라인을 최대한 따르도록 했습니다. 서버와의 RESTful+JSON 통신, 음악 스트리밍을 위한 MediaPlayer, ForegroundService 연계도 보실 수 있습니다.

소스코드에 대한 문의사항나 기능 추가 등은 GitHub 이슈 페이지에 남겨주시면 따르도록 하겠습니다.

감사합니다.
