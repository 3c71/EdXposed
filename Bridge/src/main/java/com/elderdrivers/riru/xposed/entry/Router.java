package com.elderdrivers.riru.xposed.entry;

import android.text.TextUtils;

import com.elderdrivers.riru.xposed.core.HookMain;
import com.elderdrivers.riru.xposed.dexmaker.DynamicBridge;
import com.elderdrivers.riru.xposed.entry.bootstrap.AppBootstrapHookInfo;
import com.elderdrivers.riru.xposed.entry.bootstrap.SysBootstrapHookInfo;
import com.elderdrivers.riru.xposed.entry.bootstrap.SysInnerHookInfo;
import com.elderdrivers.riru.xposed.entry.bootstrap.WorkAroundHookInfo;
import com.elderdrivers.riru.xposed.entry.hooker.SystemMainHooker;
import com.elderdrivers.riru.xposed.util.Utils;
import com.swift.sandhook.SandHookConfig;
import com.swift.sandhook.xposedcompat.XposedCompat;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedInit;

import static de.robv.android.xposed.XposedInit.startsSystemServer;

public class Router {

    public volatile static boolean forkCompleted = false;

    public static void prepare(boolean isSystem) {
        // this flag is needed when loadModules
        startsSystemServer = isSystem;
//        InstallerChooser.setup();
    }

    public static void checkHookState(String appDataDir) {
        // determine whether allow xposed or not
//        XposedBridge.disableHooks = ConfigManager.shouldHook(parsePackageName(appDataDir));
    }

    private static String parsePackageName(String appDataDir) {
        if (TextUtils.isEmpty(appDataDir)) {
            return "";
        }
        int lastIndex = appDataDir.lastIndexOf("/");
        if (lastIndex < 1) {
            return "";
        }
        return appDataDir.substring(lastIndex + 1);
    }

    public static void installBootstrapHooks(boolean isSystem) {
        // Initialize the Xposed framework
        try {
            XposedInit.initForZygote(isSystem);
        } catch (Throwable t) {
            Utils.logE("error during Xposed initialization", t);
            XposedBridge.disableHooks = true;
        }
    }

    public static void loadModulesSafely() {
        try {
            // FIXME some coredomain app can't reading modules.list
            XposedInit.loadModules();
        } catch (Exception exception) {
            Utils.logE("error loading module list", exception);
        }
    }

    public static void startBootstrapHook(boolean isSystem) {
        Utils.logD("startBootstrapHook starts: isSystem = " + isSystem);
        ClassLoader classLoader = XposedBridge.BOOTCLASSLOADER;
        if (isSystem) {
            HookMain.doHookDefault(
                    Router.class.getClassLoader(),
                    classLoader,
                    SysBootstrapHookInfo.class.getName());
            XposedCompat.addHookers(classLoader, SysBootstrapHookInfo.hookItems);
        } else {
            HookMain.doHookDefault(
                    Router.class.getClassLoader(),
                    classLoader,
                    AppBootstrapHookInfo.class.getName());
            XposedCompat.addHookers(classLoader, AppBootstrapHookInfo.hookItems);
        }
    }

    public static void startSystemServerHook() {
//        HookMain.doHookDefault(
//                Router.class.getClassLoader(),
//                SystemMainHooker.systemServerCL,
//                SysInnerHookInfo.class.getName());
        XposedCompat.addHookers(SystemMainHooker.systemServerCL, SysInnerHookInfo.hookItems);
    }

    public static void startWorkAroundHook() {
        HookMain.doHookDefault(
                Router.class.getClassLoader(),
                XposedBridge.BOOTCLASSLOADER,
                WorkAroundHookInfo.class.getName());
    }

    public static void onEnterChildProcess() {
        forkCompleted = true;
        DynamicBridge.onForkPost();
        //enable compile in child process
        SandHookConfig.compiler = !startsSystemServer;
    }
}
