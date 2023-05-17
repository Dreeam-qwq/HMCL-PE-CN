package com.tungsten.hmclpe.launcher.setting;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.*;
import com.tungsten.hmclpe.manifest.AppManifest;
import com.tungsten.hmclpe.utils.file.*;
import java.io.*;
import java.util.Objects;

public class InstallLauncherFile {
    public static void checkLauncherFiles(SplashActivity activity){
        /*
         * 初始化进度监听回调
        */
        @SuppressLint("SetTextI18n") AssetsUtils.ProgressCallback progressCallback = progress -> activity.runOnUiThread(() -> {
            activity.loadingProgress.setProgress(progress);
            activity.loadingProgressText.setText(progress + " %");
        });
        /*
         * 检查forge-install-bootstrapper.jar
        */
        activity.runOnUiThread(() -> {
            activity.loadingText.setText(activity.getString(R.string.loading_hint_plugin));
        });
        if (!new File(AppManifest.PLUGIN_DIR + "/installer").exists() || !new File(AppManifest.PLUGIN_DIR + "/installer/version").exists() || Integer.parseInt(Objects.requireNonNull(FileStringUtils.getStringFromFile(AppManifest.PLUGIN_DIR + "/installer/version"))) < Integer.parseInt(Objects.requireNonNull(AssetsUtils.readAssetsTxt(activity, "plugin/installer/version")))) {
            FileUtils.deleteDirectory(AppManifest.PLUGIN_DIR + "/installer");
            AssetsUtils.getInstance(activity).setProgressCallback(progressCallback).copyOnMainThread("plugin/installer",AppManifest.PLUGIN_DIR + "/installer");
        }
        /*
         * 检查TouchInjector.jar
        */
        if (!new File(AppManifest.PLUGIN_DIR + "/touch").exists() || !new File(AppManifest.PLUGIN_DIR + "/touch/version").exists() || Integer.parseInt(Objects.requireNonNull(FileStringUtils.getStringFromFile(AppManifest.PLUGIN_DIR + "/touch/version"))) < Integer.parseInt(Objects.requireNonNull(AssetsUtils.readAssetsTxt(activity, "plugin/touch/version")))) {
            FileUtils.deleteDirectory(AppManifest.PLUGIN_DIR + "/touch");
            AssetsUtils.getInstance(activity).setProgressCallback(progressCallback).copyOnMainThread("plugin/touch",AppManifest.PLUGIN_DIR + "/touch");
        }
        /*
         * 检查authlib-injector.jar
        */
        if (!new File(AppManifest.PLUGIN_DIR + "/login/authlib-injector").exists() || !new File(AppManifest.PLUGIN_DIR + "/login/authlib-injector/version").exists() || Integer.parseInt(Objects.requireNonNull(FileStringUtils.getStringFromFile(AppManifest.PLUGIN_DIR + "/login/authlib-injector/version"))) < Integer.parseInt(Objects.requireNonNull(AssetsUtils.readAssetsTxt(activity, "plugin/login/authlib-injector/version")))) {
            FileUtils.deleteDirectory(AppManifest.PLUGIN_DIR + "/login/authlib-injector");
            AssetsUtils.getInstance(activity).setProgressCallback(progressCallback).copyOnMainThread("plugin/login/authlib-injector",AppManifest.PLUGIN_DIR + "/login/authlib-injector");
        }
        /*
         * 检查nide8auth.jar
        */
        if (!new File(AppManifest.PLUGIN_DIR + "/login/nide8auth").exists() || !new File(AppManifest.PLUGIN_DIR + "/login/nide8auth/version").exists() || Integer.parseInt(Objects.requireNonNull(FileStringUtils.getStringFromFile(AppManifest.PLUGIN_DIR + "/login/nide8auth/version"))) < Integer.parseInt(Objects.requireNonNull(AssetsUtils.readAssetsTxt(activity, "plugin/login/nide8auth/version")))) {
            FileUtils.deleteDirectory(AppManifest.PLUGIN_DIR + "/login/nide8auth");
            AssetsUtils.getInstance(activity).setProgressCallback(progressCallback).copyOnMainThread("plugin/login/nide8auth",AppManifest.PLUGIN_DIR + "/login/nide8auth");
        }
        /*
         * 检查布局方案，如果没有，就生产一个默认布局
        */
        activity.runOnUiThread(() -> {
            activity.loadingText.setText(activity.getString(R.string.loading_hint_control));
        });
        if (SettingUtils.getControlPatternList().size() == 0) {
            AssetsUtils.getInstance(activity.getApplicationContext()).setProgressCallback(progressCallback).copyOnMainThread("control", AppManifest.CONTROLLER_DIR);
        }
        /*
         * 检查除Java外的运行环境[比如渲染器...]，这些文件一定会被内置进启动器，所以进行统一处理
        */
        activity.runOnUiThread(() -> {
            activity.loadingText.setText(activity.getString(R.string.loading_hint_lib));
        });
        if (!new File(AppManifest.DEFAULT_RUNTIME_DIR + "/version").exists() || Integer.parseInt(Objects.requireNonNull(FileStringUtils.getStringFromFile(AppManifest.DEFAULT_RUNTIME_DIR + "/version"))) < Integer.parseInt(Objects.requireNonNull(AssetsUtils.readAssetsTxt(activity, "app_runtime/version")))) {
            FileUtils.deleteDirectory(AppManifest.BOAT_LIB_DIR);
            FileUtils.deleteDirectory(AppManifest.POJAV_LIB_DIR);
            FileUtils.deleteDirectory(AppManifest.CACIOCAVALLO_DIR);
            if (new File(AppManifest.DEFAULT_RUNTIME_DIR + "/version").exists()) {
                new File(AppManifest.DEFAULT_RUNTIME_DIR + "/version").delete();
            }
            AssetsUtils.getInstance(activity).setProgressCallback(progressCallback).copyOnMainThread("app_runtime/boat",AppManifest.BOAT_LIB_DIR);
            AssetsUtils.getInstance(activity).setProgressCallback(progressCallback).copyOnMainThread("app_runtime/pojav",AppManifest.POJAV_LIB_DIR);
            AssetsUtils.getInstance(activity).setProgressCallback(progressCallback).copyOnMainThread("app_runtime/caciocavallo",AppManifest.CACIOCAVALLO_DIR);
            AssetsUtils.getInstance(activity).setProgressCallback(progressCallback).copyOnMainThread("app_runtime/version",AppManifest.DEFAULT_RUNTIME_DIR + "/version");
        }
        /*
         * 检查Java环境，如果版本太老或没有会从安装包的assets目录中释放一份
         * 检查游戏资源情况，如果目录中有数据则先删除数据在释放assets目录中游戏资源
        */
        checkJava8(activity, progressCallback);
        checkJava17(activity, progressCallback);
        /**
         * 删除原游戏数据并释放新游戏整合包资源条件[以下所有条件中只要有一个满足都会执行操作]：
         *     1.
         *       (1) Java17运行环境不存在
         *       (2) ”/data/data/com.tungsten.hmclpe/app_runtime/java/JRE17/version“文件不存在
         *       (3) "/data/data/com.tungsten.hmclpe/app_runtime/java/JRE17/version"文件内设置的版本号 小于(<) 安装包内”assets/app_runtime/java/JRE17/version“版本号
        **/
        if (!new File(AppManifest.DEFAULT_RUNTIME_DIR + "/resolv.conf").exists()){
            try(BufferedWriter bfw = new BufferedWriter(new FileWriter(AppManifest.DEFAULT_RUNTIME_DIR + "/resolv.conf"))) {
                bfw.write("nameserver 8.8.8.8");
                bfw.newLine();
                bfw.write("nameserver 114.114.114.114");
                bfw.flush();
            }catch (Exception ignored){}
        }
        activity.runOnUiThread(() -> {
            enterLauncher(activity);
        });
    }

    @SuppressLint("SetTextI18n")
    public static void checkJava8(SplashActivity activity, AssetsUtils.ProgressCallback callback){
        activity.runOnUiThread(() -> {
            activity.loadingText.setText(activity.getString(R.string.loading_hint_java_8));
        });
        if (!new File(AppManifest.JAVA_DIR + "/default").exists() || !new File(AppManifest.JAVA_DIR + "/default/version").exists() || Integer.parseInt(Objects.requireNonNull(FileStringUtils.getStringFromFile(AppManifest.JAVA_DIR + "/default/version"))) < Integer.parseInt(Objects.requireNonNull(AssetsUtils.readAssetsTxt(activity, "app_runtime/java/default/version")))) {
            FileUtils.deleteDirectory(AppManifest.JAVA_DIR + "/default");
            AssetsUtils.getInstance(activity).setProgressCallback(callback).copyOnMainThread("app_runtime/java/default",AppManifest.JAVA_DIR + "/default");
        }
    }

    @SuppressLint("SetTextI18n")
    public static void checkJava17(SplashActivity activity, AssetsUtils.ProgressCallback callback){
        activity.runOnUiThread(() -> {
            activity.loadingText.setText(activity.getString(R.string.loading_hint_java_17));
        });
        if (!new File(AppManifest.JAVA_DIR + "/JRE17").exists() || !new File(AppManifest.JAVA_DIR + "/JRE17/version").exists() || Integer.parseInt(Objects.requireNonNull(FileStringUtils.getStringFromFile(AppManifest.JAVA_DIR + "/JRE17/version"))) < Integer.parseInt(Objects.requireNonNull(AssetsUtils.readAssetsTxt(activity, "app_runtime/java/JRE17/version")))) {
            FileUtils.deleteDirectory(AppManifest.JAVA_DIR + "/JRE17");
            AssetsUtils.getInstance(activity).setProgressCallback(callback).copyOnMainThread("app_runtime/java/JRE17",AppManifest.JAVA_DIR + "/JRE17");
            //删除原公有目录游戏资源
            deleteMinecraftFiles(activity, callback);
            //从apk安装包的assets目录内取出游戏资源并释放
            installAdditionalFiles(activity, callback);
        }
    }

    @SuppressLint("SetTextI18n")
    public static void installAdditionalFiles(SplashActivity activity, AssetsUtils.ProgressCallback callback){
        if(!new File(AppManifest.DEFAULT_GAME_DIR).exists()){
            activity.runOnUiThread(() -> {
                activity.loadingText.setText(activity.getString(R.string.local_installing_minecraft_resources));
            });
            //释放游戏资源
            AssetsUtils.getInstance(activity).setProgressCallback(callback).copyOnMainThread(".minecraft",AppManifest.DEFAULT_GAME_DIR);
            AssetsUtils.getInstance(activity.getApplicationContext()).copyAssetsToSD("authlib_injector_server.json", AppManifest.ACCOUNT_DIR + "/authlib_injector_server.json");
        }
    }

    @SuppressLint("SetTextI18n")
    public static void deleteMinecraftFiles(SplashActivity activity, AssetsUtils.ProgressCallback callback){
        if(new File(AppManifest.LAUNCHER_DIR).exists()){
            activity.runOnUiThread(() -> {
                activity.loadingProgress.setProgress(0);
                activity.loadingProgressText.setText("0 %");
                activity.loadingText.setText(activity.getString(R.string.local_deleting_minecraft_resources));
            });
            //删除原公有目录游戏资源
            FileUtils.deleteDirectoryOnMainThread(AppManifest.LAUNCHER_DIR,activity.loadingProgress,activity.loadingProgressText,activity);
        }
    }

    @SuppressLint("SetTextI18n")
    public static void enterLauncher (SplashActivity activity) {
        activity.loadingText.setText(activity.getString(R.string.loading_hint_ready));
        activity.loadingProgress.setProgress(100);
        activity.loadingProgressText.setText("100 %");
        Intent intent = new Intent(activity,MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("fullscreen",activity.launcherSetting.fullscreen);
        intent.putExtras(bundle);
        activity.startActivity(intent);
        activity.finish();
    }

}
