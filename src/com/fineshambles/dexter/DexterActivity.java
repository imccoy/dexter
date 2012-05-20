package com.fineshambles.dexter;

import dexter.CljActivity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.Thread;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import dalvik.system.DexClassLoader;



public class DexterActivity extends Activity
{
    private ClassLoader loader;
    private CljActivity clj;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getClj().onCreate(savedInstanceState);
    }

    protected String cljClassName() {
        return "com.fineshambles.prebuilt.hello";
    }

    private CljActivity getClj() {
        try {
            if (clj == null) {
                Class cljClass = getLoader().loadClass(cljClassName());
                Constructor<CljActivity> cljClassConstructor
                  = cljClass.getConstructor(Activity.class);
                clj = cljClassConstructor.newInstance(this);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Couldn't get clojureized " + cljClassName(), e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Couldn't get clojureized " + cljClassName(), e);
        } catch (InstantiationException e) {
            throw new RuntimeException("Couldn't get clojureized " + cljClassName(), e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Couldn't get clojureized " + cljClassName(), e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Couldn't get clojureized " + cljClassName(), e);
        }
        return clj;
    }

    private ClassLoader getLoader() {
        if (loader == null) {
            loader = hookUpClojure();
        }
        return loader;
    }

    private ClassLoader hookUpClojure() {
        try {
            ClassLoader loader = classLoader();
            Thread.currentThread().setContextClassLoader(loader);
            Class cljRT = loader.loadClass("clojure.lang.RT");
            Field varUseCxtLoader = cljRT.getField("USE_CONTEXT_CLASSLOADER");
            Class cljVar = loader.loadClass("clojure.lang.Var");
            cljVar.getDeclaredMethod("doReset", Object.class).invoke(
                varUseCxtLoader.get(null),
                loader);

            return loader;
        } catch (Exception ex) {
            throw new RuntimeException("Couldn't hook up Clojure pieces", ex);
        }

    }

    private ClassLoader classLoader()
    {
        Log.e("Dexter", "In getClassLoader");
        Context context = this;
        // the DexClassLoader docs say this next line is a good idea
        File dexOutputDir = context.getDir("dex", 0);
        try {
            String paths = pathForApk("hello", R.raw.hello) + ":" +
              pathForApk("clojure", R.raw.clojure);
            return new DexClassLoader(paths,
                dexOutputDir.getAbsolutePath(), null, this.getClassLoader());
        } catch (IOException e) {
            Log.e("Dexter", "Couldn't list APK resources", e);
            throw new RuntimeException("Couldn't list APK resources", e);
        }
    }

    public String pathForApk(String apk, int id) 
        throws IOException, FileNotFoundException
    {
        Context context = this;
        File apkOutputDir = context.getDir("apk", 0);

        String apkWithExtension = apk + ".apk";

        File apkOutputFile = new File(apkOutputDir, apkWithExtension);
        final int BUF_SIZE= 1024 * 8;
        BufferedInputStream assetInS = new BufferedInputStream(
            getResources().openRawResource(id), BUF_SIZE);
        BufferedOutputStream assetOutS = new BufferedOutputStream(
            new FileOutputStream(apkOutputFile), BUF_SIZE);
        int n = 0;
        byte[] b = new byte[BUF_SIZE];
        while ((n = assetInS.read(b, 0, b.length)) != -1) {
            assetOutS.write(b, 0, n);
        }
        assetInS.close();
        assetOutS.close();
        Log.e("Dexter", "produced " + apkOutputFile.getAbsolutePath());

        return apkOutputFile.getAbsolutePath();
    }

}
