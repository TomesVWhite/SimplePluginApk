/*
 * Copyright (c) 2013. wyouflf (wyouflf@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tomes.servicepluginimp.utils;

import android.util.Log;

/**
 * Log工具，类似android.util.Log。
 */
public class LogUtils {

    private static String tag = "Tomes";

    private LogUtils() {
    }

    public static boolean DEBUG = true;

   
    public static void d(String content) {
        if (!DEBUG) return;

            Log.d(tag, content);

    }

    public static void d(String content, Throwable tr) {
        if (!DEBUG) return;


            Log.d(tag, content, tr);
   
    }

    public static void e(String content) {
        if (!DEBUG) return;
        Log.e(tag, content);
        
    }

    public static void e(String content, Throwable tr) {
        if (!DEBUG) return;

            Log.e(tag, content, tr);
  
    }

    public static void i(String content) {
        if (!DEBUG) return;


            Log.i(tag, content);
  
    }

    public static void i(String content, Throwable tr) {
        if (!DEBUG) return;

        
            
        
            Log.i(tag, content, tr);
 
    }

    public static void v(String content) {
        if (!DEBUG) return;

            Log.v(tag, content);

    }

    public static void v(String content, Throwable tr) {
        if (!DEBUG) return;

        
            
        
            Log.v(tag, content, tr);

    }

    public static void w(String content) {
        if (!DEBUG) return;

            Log.w(tag, content);

    }

    public static void w(String content, Throwable tr) {
        if (!DEBUG) return;

            Log.w(tag, content, tr);

    }

    public static void w(Throwable tr) {
        if (!DEBUG) return;


            Log.w(tag, tr);

    }



}