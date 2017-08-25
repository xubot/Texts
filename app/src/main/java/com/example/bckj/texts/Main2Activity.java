package com.example.bckj.texts;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.webkit.GeolocationPermissions;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.amap.api.location.AMapLocationClient;

@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
public class Main2Activity extends AppCompatActivity {
    private WebView mWebView;

    private AMapLocationClient locationClientSingle = null;
    private int REQUEST_CODE=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
            if (ActivityCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(Main2Activity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
            }else {
                Toast.makeText(Main2Activity.this, "已开通权限", Toast.LENGTH_SHORT).show();
                mWebView = (WebView) findViewById(R.id.mWebView);
                startAssistLocation();
                initWebSettings(mWebView);
                mWebView.setWebViewClient(new CommonWebClient());
                mWebView.setWebChromeClient(new CommonWebChromeWebClient());
                mWebView.loadUrl("http://118.190.91.24:8080/freewifi/app/index.html?id=ch");
            }
    }
    /**
     * 初始化webview设置
     *
     * @param mWebView
     */
    public void initWebSettings(WebView mWebView) {
        if (null == mWebView) {
            return;
        }
        WebSettings webSettings = mWebView.getSettings();
        // 允许webview执行javaScript脚本
        webSettings.setJavaScriptEnabled(true);
        // 设置是否允许定位，这里为了使用H5辅助定位，设置为false。
        //设置为true不一定会进行H5辅助定位，设置为true时只有H5定位失败后才会进行辅助定位
        webSettings.setGeolocationEnabled(false);
        // 设置UserAgent
        String userAgent = webSettings.getUserAgentString();
        mWebView.getSettings().setUserAgentString(userAgent);

        mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setAllowContentAccess(true);
        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setHorizontalScrollbarOverlay(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        //设置定位的数据库路径
        String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        webSettings.setGeolocationDatabasePath(dir);
        // 开启 DOM storage API 功能
        webSettings.setDomStorageEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setDefaultTextEncodingName("utf-8");
    }
    class CommonWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

        }
    }

    class CommonWebChromeWebClient extends WebChromeClient {
        // 处理javascript中的alert
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            return true;
        }

        // 处理javascript中的confirm
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            return true;
        }

        // 处理定位权限请求
        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);
            super.onGeolocationPermissionsShowPrompt(origin, callback);
        }
        @Override
        // 设置网页加载的进度条
        public void onProgressChanged(WebView view, int newProgress) {
            Main2Activity.this.getWindow().setFeatureInt(Window.FEATURE_PROGRESS, newProgress * 100);
            super.onProgressChanged(view, newProgress);
        }

        // 设置应用程序的标题title
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
        }
    }

    /**
     * 启动H5辅助定位
     */
    void startAssistLocation(){
        if(null == locationClientSingle){
            locationClientSingle = new AMapLocationClient(this.getApplicationContext());
            locationClientSingle.startLocation();
        }
        locationClientSingle.startAssistantLocation();
        Toast.makeText(Main2Activity.this, "正在定位...", Toast.LENGTH_LONG).show();
    }
    //得到权限回调的方法
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mWebView = (WebView) findViewById(R.id.mWebView);
                startAssistLocation();
                initWebSettings(mWebView);
                mWebView.setWebViewClient(new CommonWebClient());
                mWebView.setWebChromeClient(new CommonWebChromeWebClient());
                mWebView.loadUrl("http://118.190.91.24:8080/freewifi/app/index.html?id=ch");
                Toast.makeText(this, "获取！", Toast.LENGTH_SHORT).show();
            } else {
                // Permission Denied
                //用户不同意，向用户展示该权限作用
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    AlertDialog dialog = new AlertDialog.Builder(this)
                            .setMessage("签到功能需要定位你目前的位置，不赋予定位权限将无法正常工作！")
                            .setPositiveButton("前往授权", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    // 根据包名打开对应的设置界面
                                    intent.setData(Uri.parse("package:" + getPackageName()));
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("取消授权", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            }).create();
                    dialog.show();
                    return;
                }
                Toast.makeText(this, "访问被拒绝！", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != locationClientSingle) {
            locationClientSingle.stopAssistantLocation();
            locationClientSingle.onDestroy();
        }
        if(null != mWebView){
            mWebView.destroy();
        }
    }
}
