package me.crafter.android.zjsnviewer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class NetworkManager {


    public static String url_init_p7 = "api/initGame&t=233&e=5f3cd4e0d30c4376f8c9685d263f5184";
    public static String url_init_zero = "api/initGame&t=233&e=5f3cd4e0d30c4376f8c9685d263f5184";
    public static String url_init_hm = "api/initGame&t=233&e=3deb25e23f5fdd11d792d63bd66ced7c";
    public static String url_passport_p7 = "http://login.alpha.p7game.com/index/passportLogin/";// +username/password
    //hm change the login in url as http://login.jianniang.com/index/passportLogin/
    public static String url_passport_hm = "http://login.jianniang.com/index/passportLogin/";// +username/password
    public static String url_login = "index/login/";//+uid
    public static String[] url_server_p7 = {
            "http://zj.alpha.p7game.com/",
            "http://s2.zj.p7game.com/",
            "http://s3.zj.p7game.com/",
            "http://s4.zj.p7game.com/",
            "http://s5.zj.p7game.com/",
            "http://s6.zj.p7game.com/",
            "http://s7.zj.p7game.com/",
            "http://s8.zj.p7game.com/",
            "http://s9.zj.p7game.com/",
            "http://s10.zj.p7game.com/",
            "http://s11.zj.p7game.com/",
    };

    public static String[] url_server_hm = {
            "http://zj.alpha.jianniang.com/",
            "http://s2.jianniang.com/",
            "http://s3.jianniang.com/",
            "http://s4.jianniang.com/",
            "http://s5.jianniang.com/",
            "http://s6.jianniang.com/",
            "http://s7.jianniang.com/",
            "http://s8.jianniang.com/",
            "http://s9.jianniang.com/",
            "http://s10.jianniang.com/",
            "http://s11.jianniang.com/"
    };

    public static String getCurrentUnixTime() {
        long unixTime = System.currentTimeMillis() / 10L;
        return String.valueOf(unixTime);
    }

    public static void updateDockInfo(Context context){
        Log.i("NetworkManager", "updateDockInfo()");
        Log.i("NetworkManager", "Unix: " + getCurrentUnixTime());
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String username = prefs.getString("username", "none");
        String password = prefs.getString("password", "none");
        String server = prefs.getString("server", "-1");
        if (server != null && server.equals("-1")) return;
        int serverId = Integer.parseInt(server);
        if (serverId < 100){
            server = url_server_p7[serverId];
        } else {
            server = url_server_hm[serverId-100];
        }

        // Black: Alt Server
        boolean altserver = prefs.getBoolean("altserver", false);
        if (altserver){
            server = prefs.getString("alt_url_server", "");
        }
        // the login alt server is changed in step 1

        Boolean on = prefs.getBoolean("on", false);
        if (!on){
            Storage.str_tiduName = Storage.str_notOn[Storage.language];
            DockInfo.updateInterval = 15;
            return;
        }

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        for (int i = 0; i < procInfos.size(); i++){
            String processName = procInfos.get(i).processName;
            if (processName.startsWith("com.muka.shipwar") || processName.startsWith("org.huanmeng.Zhanjian2")){
                DockInfo.updateInterval = 15;
                Storage.str_tiduName = Storage.str_gameRunning[Storage.language];
                return;
            }
        }
        String error = "";

        try {
            // STEP 1 PASSPORT LOGIN
            URL url;
            if (serverId < 100){
                url = new URL(url_passport_p7 +username+"/"+password);
            } else {
                url = new URL(url_passport_hm +username+"/"+password);
            }
            if (altserver){
                url = new URL(prefs.getString("alt_url_login", "") +username+"/"+password);
            }
            Log.i("NetWorkManager > 1", url.toString());
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            if (response.toString().contains("\"eid\"")){
                error = Storage.str_badLogin[Storage.language];
            }

            List<String> cookies = connection.getHeaderFields().get("Set-Cookie");
            Map<String, String> cookieMap = new HashMap<String, String>();
            String loginCookie = parseCookie(cookies, cookieMap);

            in.close();

            JSONObject obj = new JSONObject(response.toString());
            int uid = obj.getInt("userId");

            // STEP 2 UID SERVER LOGIN
            url = new URL(server + url_login + uid);
            Log.i("NetWorkManager > 2", url.toString());
            connection = url.openConnection();
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);
            connection.setRequestProperty("cookie", loginCookie);
            cookies = connection.getHeaderFields().get("Set-Cookie");
            loginCookie = parseCookie(cookies, cookieMap);

            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            //String inputLine;
            response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // STEP 3 GET USER DATA
            String urString;
            if (serverId == 0){
                urString = server + url_init_zero;
            } else if (serverId < 100){
                urString = server + url_init_p7;
            } else {
                urString = server + url_init_hm;
            }

            url = new URL(urString);
            Log.i("NetWorkManager > 3", url.toString());
            connection = url.openConnection();
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);
            connection.setRequestProperty("cookie", loginCookie);

            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            // String inputLine;
            response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
//          Log.i("NetworkManager", response.toString());
            JSONObject data = new JSONObject(response.toString());

            if (!data.has("userVo")){
                error = Storage.str_noUserData[Storage.language];
            }

            Storage.str_tiduName = data.getJSONObject("userVo").getString("username");
            DockInfo.exp = data.getJSONObject("userVo").getString("exp");
            DockInfo.nextExp = data.getJSONObject("userVo").getString("nextExp");
            DockInfo.level = data.getJSONObject("userVo").getString("level");

            JSONObject pveExploreVo = data.getJSONObject("pveExploreVo");
            JSONArray levels = pveExploreVo.getJSONArray("levels");
            for (int i = 0; i < levels.length(); i++){
                JSONObject level = levels.getJSONObject(i);
                DockInfo.dockTravelTime[level.getInt("fleetId")-1] = level.getInt("endTime");
            }

            JSONArray dockVo = data.getJSONArray("dockVo");
            JSONArray repairDockVo = data.getJSONArray("repairDockVo");
            JSONArray equipmentDockVo = data.getJSONArray("equipmentDockVo");
            for (int i = 0; i < 4; i++){
                JSONObject o = dockVo.getJSONObject(i);
                if (o.getInt("locked") == 1){
                    DockInfo.dockBuildTime[i] = -1;
                } else if (o.has("endTime")){
                    DockInfo.dockBuildTime[i] = o.getInt("endTime");
                } else {
                    DockInfo.dockBuildTime[i] = 0;
                }
                o = repairDockVo.getJSONObject(i);
                if (o.getInt("locked") == 1){
                    DockInfo.dockRepairTime[i] = -1;
                } else if (o.has("endTime")){
                    DockInfo.dockRepairTime[i] = o.getInt("endTime");
                } else {
                    DockInfo.dockRepairTime[i] = 0;
                }
                o = equipmentDockVo.getJSONObject(i);
                if (o.getInt("locked") == 1){
                    DockInfo.dockMakeTime[i] = -1;
                } else if (o.has("endTime")){
                    DockInfo.dockMakeTime[i] = o.getInt("endTime");
                } else {
                    DockInfo.dockMakeTime[i] = 0;
                }
            }
            Log.i("NetworkManager", "Update successful");
            DockInfo.updateInterval += 75;
            DockInfo.updateInterval = Math.min(DockInfo.updateInterval, 1210);

//            if (WeatherGuard.yes){
//                DockInfo.updateInterval = Math.min(DockInfo.updateInterval, 305);
//                WeatherGuard.dash(data, server, loginCookie);
//            }

        } catch (Exception ex) {
            Log.e("UpdateDockInfo()", "ERR1");
            ex.printStackTrace();
            if (error.equals("")){
                Storage.str_tiduName = Storage.str_badConnection[Storage.language];
            } else {
                Storage.str_tiduName = error;
            }
            if (DockInfo.updateInterval > 90){
                DockInfo.updateInterval = 90;
            } else {
                DockInfo.updateInterval += 15;
            }
        }


    }

    private static String parseCookie(List<String> cookies, Map<String, String> cookieMap) {
        for (String cookie : cookies) {
            String[] token = cookie.split("=");
            cookieMap.put(token[0], cookie);
        }
        StringBuffer sb = new StringBuffer();
        int size = cookieMap.size();
        int count = 0;
        for (String cookie : cookieMap.values()) {
            sb.append(cookie);
            if (++count != size) {
                sb.append(";");
            }
        }
        return sb.toString();
    }
}
