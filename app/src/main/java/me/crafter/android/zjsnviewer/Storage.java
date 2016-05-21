package me.crafter.android.zjsnviewer;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Calendar;
import java.util.Date;

public class Storage {

    public static int language = 0;

    public static String str_tiduName = "提督の名前";
    public static String[] str_reportTitle = {"战舰少女码头报告","战舰少女码头报告","戰艦少女碼頭通知","Warship Girls Dock Report","提督注意書","战舰少女码头报告","喂！看这里！"};
    public static String[] str_locked = {"该槽位已被锁定","槽位锁定","該槽位已被鎖定","Slot Locked","???","x","还不课金么？"};
    public static String[] str_idle = {"空闲中","空闲中","閒置中","Idle","未使用","-","闲着呢"};
    public static String[] str_hour = {"小时","时","小時","hr","时",":","时"};
    public static String[] str_minute = {"分钟","分","分鐘","min.","分","","分"};
    public static String[] str_travel = {"远征中 - ","远征中 - ","遠征中 - ","Exp.ing - ","遠征中 - ","","不在家"};
    public static String[] str_repair = {"修理中 - ","修理中 - ","修理中 - ","Reparing - ","入渠中 - ","","大破中"};
    public static String[] str_build = {"建造中 - ","建造中 - ","建造中 - ","Cons.ing - ","建造中 - ","","换装中"};
    public static String[] str_make = {"开发中 - ","开发中 - ","開發中 - ","Cons.ing - ","開発中 - ","","开发中"};
    public static String[] str_travel2 = {"远征完成","远征完成","遠征完成","Expedition Complete","遠征完了","00:00","欧尼酱我回来啦"};
    public static String[] str_repair2 = {"修理完成","修理完成","修理完成","Repair Done","入渠完了","00:00","衣服穿好了"};
    public static String[] str_build2 = {"建造完成","建造完成","建造完成","Construction Complete","建造完了","00:00","快抱抱"};
    public static String[] str_make2 = {"开发完成","开发完成","開發完成","Construction Complete","開壳完了","00:00","工具在这里"};
    public static String[] str_thereIs = {"有","有","有","There is(are) ","","","有"};
    public static String[] str_teamsTravelling = {"个队伍正在远征，","个队伍正在远征，","個隊伍正在遠征，"," teams expedicting,","艦隊遠征中，","个队伍远征中，","个队伍出去玩了，"};
    public static String[] str_travelDone = {"远征结束，","远征结束，","遠征結束"," completed. ","遠征完了，","远征结束，","回家了，"};
    public static String[] str_team = {"队、","队、","隊、"," team,","隊，","队、","队"};
    public static String[] str_allFleetFixed = {"全部舰船维修完成，","全部舰船维修完成，","全部艦船維修完成","All fleet repaired. ","全艦隊入渠完了","维修全部结束，","所有的妹妹都穿好了衣服，"};
    public static String[] str_isRepairing = {"艘船正在维修，","艘船正在维修，","艘船正在維修，"," fleet(s) are currently repairing. ","艦娘入渠中，","艘船维修中，","个妹妹爆衣中，"};
    public static String[] str_allFleetBuilt = {"全部舰船建造完成，","全部舰船建造完成，","全部艦船建造完成","All fleets construction complete. ","全艦隊建造完了。","建造全部结束，","所有的妹妹全都准备好啦，"};
    public static String[] str_fleetIsBuilding = {"艘船正在建造，","艘船正在建造，","艘船正在建造，"," fleet(s) are under construction. ","艦娘建造中","艘船建造中","个妹妹还没准备好，"};
    public static String[] str_allEquipmentMade = {"全部装备开发完成。","全部装备开发完成。","全部裝備開發完成。","All equipments construction conplete. ","全裝備開壳完了。","开发全部结束。","所有衣服都做好啦，"};
    public static String[] str_equipmentIsMaking = {"个装备正在开发。","个装备正在开发。","個裝備正在開發。"," equipment(s) are under construction.","裝備開壳中。","个装备开发中。","个衣服正在做。"};
    public static String[] str_badLogin = {"用户名密码错误","用户名密码错误","用戶名密碼錯誤","Bad username or password","ユーザー名またはパスワードが正しくありません","用户名密码错误","吔屎啦 这都输不对"};
    public static String[] str_noUserData = {"数据错误，你选错服务器了？","数据错误，你选错服务器了？","數據錯誤，你選錯伺服器了？","No user data, check your server selection.","データエラー，サーバーの選択は間違っています？","数据错误，你选错服务器了？","然后并没有什么卵用"};
    public static String[] str_badConnection = {"网络错误","网炸了","網絡錯誤","Bad internet.","ネットワーク接続エラー","网络错误","移动联通你家炸了"};
    public static String[] str_notOn = {"未开启","未开启","未啟動","Disabled","起動しない","x","我在睡觉"};
    public static String[] str_gameRunning = {"游戏运行中","游戏运行中","遊戲運行中","Game is running","起動しない","-","游戏在运行"};


    public static String getZjsnPackageName(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int serverId = Integer.parseInt(prefs.getString("server", "-1"));
        if (serverId == 0){
            return "com.muka.shipwarzero";
        } else if (serverId < 100){
            return "com.muka.shipwar";
        } else {
            return "com.huanmeng.zhanjian2";
        }
    }

    public static PendingIntent getStartPendingIntent(Context context){
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(Storage.getZjsnPackageName(context));
        if (intent == null){
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.crafter.me/zjsnviewer/"));
        }
        return PendingIntent.getActivity(context, 0, intent, 0);
    }

    public static float getTextSizeMajor(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String size = prefs.getString("textsize_major", "48");
        float ret = 48;
        try {
            ret = Float.parseFloat(size);
        } catch (Exception ex) {}
        ret = Math.max(0.001F, ret);
        return ret;
    }

    public static float getTextSizeMinor(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String size = prefs.getString("textsize_minor", "48");
        float ret = 48;
        try {
            ret = Float.parseFloat(size);
        } catch (Exception ex) {}
        ret = Math.max(0.001F, ret);
        return ret;
    }

    public static boolean isNoDisturbNow(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean on = prefs.getBoolean("notification_do_not_disturb_on", false);
        if (on){
            int now = Integer.parseInt(DateFormat.format("HHmm", new Date(System.currentTimeMillis() % 86400000)).toString());
            long[] interval = DoubleTimePreference.toTwoLongs(prefs.getString("notification_do_not_disturb_time", "14400000:45000000"));
            int start = Integer.parseInt(DateFormat.format("HHmm", new Date(interval[0])).toString());
            int finish = Integer.parseInt(DateFormat.format("HHmm", new Date(interval[1])).toString());
            Log.i("Storage", now + " " + start + " " + finish);
            if (start < finish){ // Not cross day
                return (now > start && now < finish);
            } else if (start > finish){ // Cross day
                return (now > start || now < finish);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static int getVersion(Context context){
        return context.getResources().getInteger(R.integer.version);
    }

    public static boolean black(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("black", false);
    }

    public static boolean root(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("root", false);
    }

}
