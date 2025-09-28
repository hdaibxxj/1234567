public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private String deviceId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // 初始化WebView
        setupWebView();
        
        // 初始化设备ID和存档系统
        setupSaveSystem();
        
        // 加载游戏
        webView.loadUrl("https://你的游戏域名.com/index.html");
    }
    
    private void setupWebView() {
        webView = findViewById(R.id.webView);
        WebSettings settings = webView.getSettings();
        
        // 启用所有存储功能 - 这是关键！
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);     // 启用LocalStorage
        settings.setDatabaseEnabled(true);       // 启用数据库
        settings.setAppCacheEnabled(true);       // 启用缓存
        settings.setAllowFileAccess(true);       // 允许文件访问
        
        // 设置数据库路径（Android 8.0+需要）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String databasePath = getApplicationContext().getDataDir().getAbsolutePath() + "/databases/";
            settings.setDatabasePath(databasePath);
        }
        
        // 设置缓存路径
        String cachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        settings.setAppCachePath(cachePath);
        settings.setAppCacheMaxSize(1024 * 1024 * 50); // 50MB缓存
        
        // 监听页面加载，确保存档系统就绪
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // 页面加载完成后注入设备ID
                injectDeviceId();
            }
        });
    }
    
    private void setupSaveSystem() {
        // 获取或生成设备唯一ID
        deviceId = getDeviceId();
        Log.d("SaveSystem", "设备存档ID: " + deviceId);
    }
    
    private String getDeviceId() {
        // 方法1: 使用Android ID（系统提供的设备标识）
        String androidId = Settings.Secure.getString(
            getContentResolver(), 
            Settings.Secure.ANDROID_ID
        );
        
        if (androidId != null && !androidId.equals("9774d56d682e549c")) {
            return "device_" + androidId;
        }
        
        // 方法2: 备用方案 - 使用SharedPreferences存储生成的UUID
        SharedPreferences prefs = getSharedPreferences("game_save_system", MODE_PRIVATE);
        String savedId = prefs.getString("device_id", null);
        
        if (savedId == null) {
            savedId = "generated_" + UUID.randomUUID().toString();
            prefs.edit().putString("device_id", savedId).apply();
        }
        
        return savedId;
    }
    
    private void injectDeviceId() {
        // 注入JavaScript代码，将设备ID传递给H5游戏
        String javascriptCode = String.format(
            "(() => {" +
            "  // 设置设备ID，游戏可以用这个来区分存档" +
            "  window.GAME_DEVICE_ID = '%s';" +
            "  " +
            "  // 如果游戏有存档系统，可以自动调用" +
            "  if (typeof window.onDeviceReady === 'function') {" +
            "    window.onDeviceReady('%s');" +
            "  }" +
            "  " +
            "  // 或者在localStorage中设置标识" +
            "  try {" +
            "    localStorage.setItem('deviceId', '%s');" +
            "    console.log('存档系统就绪，设备ID:', '%s');" +
            "  } catch (e) {" +
            "    console.warn('localStorage设置失败:', e);" +
            "  }" +
            "})();",
            deviceId, deviceId, deviceId, deviceId
        );
        
        // 执行JavaScript
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript(javascriptCode, null);
        } else {
            webView.loadUrl("javascript:" + javascriptCode);
        }
    }
    
    // 处理返回键
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
