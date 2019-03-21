package com.elderdrivers.riru.xposed.entry.bootstrap;

import com.elderdrivers.riru.common.KeepMembers;
import com.elderdrivers.riru.xposed.entry.hooker.HandleBindAppHooker;
import com.elderdrivers.riru.xposed.entry.hooker.LoadedApkConstructorHooker;
import com.elderdrivers.riru.xposed.entry.hooker.OnePlusWorkAroundHooker;
import com.elderdrivers.riru.xposed.entry.hooker.SystemMainHooker;

public class SysBootstrapHookInfo implements KeepMembers {
    public static String[] hookItemNames = {
            OnePlusWorkAroundHooker.class.getName()
    };

    public static Class[] hookItems = {
            HandleBindAppHooker.class,
            SystemMainHooker.class,
            LoadedApkConstructorHooker.class
    };

}
