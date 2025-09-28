package com.xycsjj.peamcc; // 请替换为您的实际包名

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private String deviceId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // 1. 获取设备ID
        deviceId = DeviceIdManager.getDeviceId(this);
        
        // 2. 设置WebView
        setupWebView();
    }
    
    private void setupWebView() {
        webView = findViewById(R.id.webView);
        WebSettings settings = webView.getSettings();
        
        // 基本WebView配置
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);     // 启用本地存储
        settings.setDatabaseEnabled(true);       // 启用数据库
        settings.setAllowFileAccess(true);       // 允许文件访问
        
        // 缓存配置（可选）
        settings.setAppCacheEnabled(true);
        String cachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        settings.setAppCachePath(cachePath);
        
        // 设置数据库路径（Android 8.0+需要）
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String databasePath = getApplicationContext().getDataDir().getAbsolutePath() + "/databases/";
            settings.setDatabasePath(databasePath);
        }
        
        // 通过URL参数传递设备ID
        String gameUrl = "https://您的游戏域名.com/index.html?device_id=" + deviceId;
        webView.loadUrl(gameUrl);
        
        // 设置WebViewClient，确保链接在WebView内打开
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }
    
    // 处理返回键 - 如果WebView可以返回，则先返回网页
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
    
    // 释放WebView资源（重要！）
    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }
}
