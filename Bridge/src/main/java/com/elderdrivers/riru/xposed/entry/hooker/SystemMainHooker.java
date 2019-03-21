package com.elderdrivers.riru.xposed.entry.hooker;

import android.app.ActivityThread;

import com.elderdrivers.riru.common.KeepMembers;
import com.elderdrivers.riru.xposed.entry.Router;
import com.elderdrivers.riru.xposed.util.PrebuiltMethodsDeopter;
import com.swift.sandhook.SandHook;
import com.swift.sandhook.annotation.HookClass;
import com.swift.sandhook.annotation.HookMethod;
import com.swift.sandhook.annotation.HookMethodBackup;
import com.swift.sandhook.annotation.HookMode;
import com.swift.sandhook.annotation.MethodReflectParams;

import java.lang.reflect.Method;

import de.robv.android.xposed.XposedBridge;

import static de.robv.android.xposed.XposedInit.logD;
import static de.robv.android.xposed.XposedInit.logE;


// system_server initialization
// ed: only support sdk >= 21 for now
@HookClass(ActivityThread.class)
public class SystemMainHooker implements KeepMembers {

    public static String className = "android.app.ActivityThread";
    public static String methodName = "systemMain";
    public static String methodSig = "()Landroid/app/ActivityThread;";

    public static ClassLoader systemServerCL;

    @HookMethodBackup("systemMain")
    static Method backup;

    @HookMethod("systemMain")
    public static ActivityThread hook() throws Throwable {
        if (XposedBridge.disableHooks) {
            return (ActivityThread) SandHook.callOriginByBackup(backup, null);
        }
        logD("ActivityThread#systemMain() starts");
        ActivityThread activityThread = (ActivityThread) SandHook.callOriginByBackup(backup, null);
        try {
            // get system_server classLoader
            systemServerCL = Thread.currentThread().getContextClassLoader();
            // deopt methods in SYSTEMSERVERCLASSPATH
            PrebuiltMethodsDeopter.deoptSystemServerMethods(systemServerCL);
            Router.startSystemServerHook();
        } catch (Throwable t) {
            logE("error when hooking systemMain", t);
        }
        return activityThread;
    }

    public static ActivityThread backup() {
        return null;
    }
}