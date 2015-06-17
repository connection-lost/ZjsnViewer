package me.crafter.android.zjsnviewer;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class NetworkManager {


    public static String url_init = "api/initData&t=233&e=0dbb983b790ab997d2e59453d8f3f27b";
    public static String url_passport = "http://login.alpha.p7game.com/index/passportLogin/";//+username/password
    public static String url_login = "index/login/";//+uid
    public static String[] url_server = {
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
            "http://s11.zj.p7game.com/"
    };

    public static void updateDockInfo(Context context){

        Log.i("NetworkManager", "updateDockInfo()");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String username = prefs.getString("username", "none");
        String password = prefs.getString("password", "none");
        String server = prefs.getString("server", "-1");
        if (server != null && server.equals("-1")) return;
        server = url_server[Integer.parseInt(server)];

        Boolean on = prefs.getBoolean("on", false);
        if (!on){
            Storage.str_tiduName = Storage.str_notOn[Storage.language];
            DockInfo.updateInterval = 15;
            return;
        }

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        for (int i = 0; i < procInfos.size(); i++){
            if (procInfos.get(i).processName.startsWith("com.muka.shipwar")){
                DockInfo.updateInterval = 15;
                Storage.str_tiduName = Storage.str_gameRunning[Storage.language];
                return;
            }
        }
        String error = "";

        try {
            // STEP 1 PASSPORT LOGIN

            URL url = new URL(url_passport+username+"/"+password);
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
            String loginCookie = "";
            for (String cookie : cookies){
                loginCookie = cookie;
            }

            in.close();

            JSONObject obj = new JSONObject(response.toString());
            int uid = obj.getInt("userId");

            // STEP 2 UID SERVER LOGIN

            url = new URL(server + url_login + uid);
            connection = url.openConnection();
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);
            connection.setRequestProperty("cookie", loginCookie);
            cookies = connection.getHeaderFields().get("Set-Cookie");
            loginCookie = "";
            for (String cookie : cookies){
                loginCookie = cookie;
            }


            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            //String inputLine;
            response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // STEP 3 GET USER DATA
            String urString = server + url_init;

            url = new URL(urString);
            Log.i("NetworkManager", url.toString());
            connection = url.openConnection();
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);
            connection.setRequestProperty("cookie", loginCookie);

            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            //String inputLine;
            response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            //Log.i("NetworkManager", response.toString());

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
//
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

}
