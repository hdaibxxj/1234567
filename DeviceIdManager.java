import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import java.util.UUID;

public class DeviceIdManager {
    
    public static String getDeviceId(Context context) {
        // 方法1: 使用Android ID（系统提供的设备标识）
        String androidId = Settings.Secure.getString(
            context.getContentResolver(), 
            Settings.Secure.ANDROID_ID
        );
        
        // 检查是否是无效的Android ID
        if (androidId != null && !androidId.equals("9774d56d682e549c")) {
            return "device_" + androidId;
        }
        
        // 方法2: 备用方案 - 使用UUID并持久化存储
        SharedPreferences prefs = context.getSharedPreferences("device_info", Context.MODE_PRIVATE);
        String savedId = prefs.getString("device_id", null);
        
        if (savedId == null) {
            // 生成新的UUID
            savedId = "generated_" + UUID.randomUUID().toString();
            // 保存到SharedPreferences
            prefs.edit().putString("device_id", savedId).apply();
        }
        
        return savedId;
    }
}
