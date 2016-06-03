package me.crafter.android.zjsnviewer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class DockInfo {

    static boolean zjsn_running_state = false;
    static boolean zjsn_formal_state = false;
    public static int lastUpdate = -1;

    public static int[] dockTravelTime = {0, 0, 0, 0};
    public static int[] dockRepairTime = {0, 0, 0, 0};
    public static int[] dockBuildTime = {0, 0, 0, 0};
    public static int[] dockMakeTime = {0, 0, 0, 0};

    public static int updateInterval = 15;

    public static String exp = "0";
    public static String nextExp = "0";
    public static String level = "0";


    public static int[][] lastStatus;

    public static int currentUnix(){
        return ((int)(System.currentTimeMillis() / 1000L));
    }

    public static int countTravelIng(){
        int ing = 0;
        for (int i : dockTravelTime){
            if (i > currentUnix()) ing ++;
        }
        return ing;
    }

    public static int countRepairIng(){
        int ing = 0;
        for (int i : dockRepairTime){
            if (i > currentUnix()) ing ++;
        }
        return ing;
    }

    public static int countBuildIng(){
        int ing = 0;
        for (int i : dockBuildTime){
            if (i > currentUnix()) ing ++;
        }
        return ing;
    }

    public static int countMakeIng(){
        int ing = 0;
        for (int i : dockMakeTime){
            if (i > currentUnix()) ing ++;
        }
        return ing;
    }

    public static String getStatusReportAllFull(){
        String report = "";
        if (countTravelIng() != 0){
            report += Storage.str_thereIs[Storage.language] + countTravelIng() + Storage.str_teamsTravelling[Storage.language];
        }
        String travelDone = "";
        for (int i = 0; i < 4; i++){
            if (dockTravelTime[i] != -1 && dockTravelTime[i] != 0 && dockTravelTime[i] < currentUnix()){
                travelDone += (i+1) + Storage.str_team[Storage.language];
            }
        }
        if (!travelDone.equals("")){
            travelDone = travelDone.substring(0, travelDone.length()-1);
            report += travelDone + Storage.str_travelDone[Storage.language];
        }

        if (countRepairIng() == 0){
            report += Storage.str_allFleetFixed[Storage.language];
        } else {
            report += Storage.str_thereIs[Storage.language] + countRepairIng() + Storage.str_isRepairing[Storage.language];
        }
        //report += "空槽位" + (dockSlotMax[0]-countRepairIng()) + "个；";

        if (countBuildIng() == 0){
            report += Storage.str_allFleetBuilt[Storage.language];
        } else {
            report += Storage.str_thereIs[Storage.language] + countBuildIng() + Storage.str_fleetIsBuilding[Storage.language];
        }
        //report += "空槽位" + (dockSlotMax[0]-countBuildIng()) + "个；";

        if (countMakeIng() == 0){
            report += Storage.str_allEquipmentMade[Storage.language];
        } else {
            report += Storage.str_thereIs[Storage.language] + countMakeIng() + Storage.str_equipmentIsMaking[Storage.language];
        }
        //report += "空槽位" + (dockSlotMax[0]-countBuildIng()) + "个。";
        return report;
    }

    public static int[][] getStatusInt(){
        //-1 = locked, 0 = done, 1 = in progress, 2 = nothing
        int [][] ret = {{2, 2, 2, 2}, {2, 2, 2, 2}, {2, 2, 2, 2}, {2, 2, 2, 2}};
        for (int i = 0; i < 4; i++){
            if (dockTravelTime[i] == -1) ret[0][i] = 2;
            else if (dockTravelTime[i] > currentUnix()) ret[0][i] = 1;
            else if (dockTravelTime[i] <= currentUnix()) ret[0][i] = 0;
            if (dockRepairTime[i] == -1) ret[1][i] = 2;
            else if (dockRepairTime[i] > currentUnix()) ret[1][i] = 1;
            else if (dockRepairTime[i] <= currentUnix()) ret[1][i] = 0;
            if (dockBuildTime[i] == -1) ret[2][i] = 2;
            else if (dockBuildTime[i] > currentUnix()) ret[2][i] = 1;
            else if (dockBuildTime[i] <= currentUnix()) ret[2][i] = 0;
            if (dockMakeTime[i] == -1) ret[3][i] = 2;
            else if (dockMakeTime[i] > currentUnix()) ret[3][i] = 1;
            else if (dockMakeTime[i] <= currentUnix()) ret[3][i] = 0;
        }
//        String debug = "";
//        for (int x = 0; x < 4; x++){
//            for (int y = 0; y < 4; y++){
//                debug += ret[x][y] + " ";
//            }
//            debug += "| ";
//        }
//        Log.i("DockInfo", "getStatusInt() returned " + debug);
        return ret;
    }

    public static boolean shouldNotify(Context context){
        // First check no disturb
        Log.d("DockInfo", "check notify");
        if (Storage.isNoDisturbNow(context)) return false;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        if (!prefs.getBoolean("notification_general", true)) return false;
        boolean[] shouldMap = {prefs.getBoolean("notification_travel", true), prefs.getBoolean("notification_repair", true), prefs.getBoolean("notification_build", true), prefs.getBoolean("notification_make", true)};


        int[][] thisStatus = getStatusInt();
        boolean should = false;
        if (lastStatus == null){

//            for (int x = 0; x < 4; x++){
//                for (int y = 0; y < 4; y++){
//                    if (thisStatus[x][y] == 0) should = true;
//                }
//            }
            //TODO NOTICE! TEMP BYPASS
            //TEMP FORCE NO NOTIFY ON START!!!
            should = false;
            //TEMP FORCE NO NOTIFY ON START!!!
        } else {
            for (int x = 0; x < 4; x++){
                for (int y = 0; y < 4; y++){
                    if (shouldMap[x] && (thisStatus[x][y] == 0 && lastStatus[x][y] != 0)) should = true;
                }
            }
        }
        lastStatus = thisStatus;
        return should;
    }

    public static String timeBetween(int future){
        String ret = "";
        int second = future-currentUnix();
        if (Storage.language == 5){ //Lite display is different
            if (second >= 3600){
                if ((second/3600) < 10){
                    ret += "0";
                }
                ret += second/3600 + Storage.str_hour[Storage.language];
            } else {
                ret += "00:";
            }
            if ((second/60)%60 < 10){
                ret += "0";
            }
            ret += (second/60)%60 + Storage.str_minute[Storage.language];
            if (second < 60){
                ret = "< 00:01" + Storage.str_minute[Storage.language];
            }
        } else { //non-Lite display
            if (second >= 3600){
                ret += second/3600 + Storage.str_hour[Storage.language];
            }
            if ((second/60)%60 < 10){
                ret += "0";
            }
            ret += (second/60)%60 + Storage.str_minute[Storage.language];
            if (second < 60){
                ret = "<1" + Storage.str_minute[Storage.language];
            }
        }
        return ret;
    }

    public static String[] getTravelBoard(){
        String[] ret = {"","","",""};
        for (int i = 0; i < 4; i++){
            if (dockTravelTime[i] == 0){
                ret[i] = Storage.str_idle[Storage.language];
            } else if (dockTravelTime[i] == -1){
                ret[i] = Storage.str_idle[Storage.language];
            } else if (dockTravelTime[i] < currentUnix()){
                ret[i] = Storage.str_travel2[Storage.language];
            } else {
                ret[i] = Storage.str_travel[Storage.language] + timeBetween(dockTravelTime[i]);
            }
        }
        return ret;
    }

    public static String[] getRepairBoard(){
        String[] ret = {"","","",""};
        for (int i = 0; i < 4; i++){
            if (dockRepairTime[i] == 0){
                ret[i] = Storage.str_idle[Storage.language];
            } else if (dockRepairTime[i] == -1){
                ret[i] = Storage.str_locked[Storage.language];
            } else if (dockRepairTime[i] < currentUnix()){
                ret[i] = Storage.str_repair2[Storage.language];
            } else {
                ret[i] = Storage.str_repair[Storage.language] + timeBetween(dockRepairTime[i]);
            }
        }
        return ret;
    }

    public static String[] getBuildBoard(){
        String[] ret = {"","","",""};
        for (int i = 0; i < 4; i++){
            if (dockBuildTime[i] == 0){
                ret[i] = Storage.str_idle[Storage.language];
            } else if (dockBuildTime[i] == -1){
                ret[i] = Storage.str_locked[Storage.language];
            } else if (dockBuildTime[i] < currentUnix()){
                ret[i] = Storage.str_build2[Storage.language];
            } else {
                ret[i] = Storage.str_build[Storage.language] + timeBetween(dockBuildTime[i]);
            }
        }
        return ret;
    }

    public static String[] getMakeBoard(){
        String[] ret = {"","","",""};
        for (int i = 0; i < 4; i++){
            if (dockMakeTime[i] == 0){
                ret[i] = Storage.str_idle[Storage.language];
            } else if (dockMakeTime[i] == -1){
                ret[i] = Storage.str_locked[Storage.language];
            } else if (dockMakeTime[i] < currentUnix()){
                ret[i] = Storage.str_make2[Storage.language];
            } else {
                ret[i] = Storage.str_make[Storage.language] + timeBetween(dockMakeTime[i]);
            }
        }
        return ret;
    }


    //request an update, with a interval of 15 seconds checked
    public static boolean requestUpdate(Context context){
        boolean ret = true;
        Log.i("DockInfo", "Current Interval is " + updateInterval + " (" + (currentUnix() - lastUpdate) + ")");
        zjsn_formal_state = zjsn_running_state;
        switch (ZjsnState.getZjsnState()) {
            case 0:
                zjsn_running_state = true;
                //Zjsn in running(both foreground and background)
                break;
            case 1:
                zjsn_running_state = false;
                //Zjsn in not running
                break;
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//        如果战舰少女没有运行或者自动运行被关闭则允许更新
        if(!zjsn_running_state||!prefs.getBoolean("auto_run", true)) {
            if (currentUnix() - lastUpdate > updateInterval || zjsn_formal_state != zjsn_running_state||!prefs.getBoolean("auto_run", true)) {
                //lastUpdate is put before updateDockInfo
                //to prevent multi request caused by delay
                if (updateInterval == 0) updateInterval =15;
                lastUpdate = currentUnix();
                NetworkManager.updateDockInfo(context);
            } else {
                ret = false;
            }
        }


        if (zjsn_running_state&&prefs.getBoolean("auto_run", true)){
            DockInfo.updateInterval = 15;
            Storage.str_tiduName = Storage.str_gameRunning[Storage.language];
        }

        Boolean on = prefs.getBoolean("on", false);
        if (!on){
            DockInfo.updateInterval = 15;
        }

        return ret;
    }


}
