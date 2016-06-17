# PublicationAndroid
PublicationAndroid is complete implementation of BakerFramework magazine style html5 contents being downloaded and readed in devices, you can configure as much as you want, only thing you might need to 
include in your project would be ReadActivityView if you want to complete customizations or event more.

if you want to run application just do the following in your manifest.xml and you are ready to go
```xml
<!-- this is required if functionality with NetworkType is defined -->
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.INTERNET" />
    <!-- might be needed since it required to move file into external storage -->

    <!-- PublicationApp needs to be overriden so that dependency injection provided by dagger2 can be initialized background or you can change content by your heart -->
    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:supportsRtl="true"
        android:hardwareAccelerated="true"
        android:name="org.fs.publication.PublicationApp" 
        android:theme="@style/ThemeLight">

        <!--ShelfActivity that is provided to show you shelf of BakerFramework -->
        <activity android:name="org.fs.publication.views.ShelfActivityView"
                  android:configChanges="keyboardHidden|orientation|screenSize">

            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>

        </activity>
        
        <!-- ReadActivityView that is showing us content of html5 magazine -->
        <activity android:name="org.fs.publication.views.ReadActivityView"
                  android:configChanges="keyboardHidden|orientation|screenSize" />

        <!-- DownloadService is EvokeAndroid that requires to work background of the app to download content via it's own thread pool and can be configure as concurrent if needed -->
        <!-- calling this service via intent is acceptable and it has category -->
        <service
            android:name="org.fs.evoke.DownloadService"
            android:exported="false">
            <!-- start command  -->
            <intent-filter>
                <action
                    android:name="org.fs.evoke.ACTION_START"        />
                <category
                    android:name="org.fs.evoke.CATEGORY_DEFAULT"    />
            </intent-filter>
            <!-- stop command -->
            <intent-filter>
                <action
                    android:name="org.fs.evoke.ACTION_STOP"         />
                <category
                    android:name="org.fs.evoke.CATEGORY_DEFAULT"    />
            </intent-filter>
        </service>
    </application>
```
