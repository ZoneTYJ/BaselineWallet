# This is a configuration file for ProGuard.
# http://proguard.sourceforge.net/index.html#manual/usage.html
#
# Starting with version 2.2 of the Android plugin for Gradle, these files are no longer used. Newer
# versions are distributed with the plugin and unpacked at build time. Files in this directory are
# no longer maintained.

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

# Optimization is turned off by default. Dex does not like code run
# through the ProGuard optimize and preverify steps (and performs some
# of these optimizations on its own).
-dontoptimize
-dontpreverify
# Note that if you want to enable optimization, you cannot just
# include optimization flags in your own project configuration file;
# instead you will need to point to the
# "proguard-android-optimize.txt" file instead of this one from your
# project.properties file.

-keepattributes *Annotation*
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

-keepattributes SourceFile,LineNumberTable


# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**

# Understand the @Keep support annotation.
-keep class android.support.annotation.Keep

-keep @android.support.annotation.Keep class * {*;}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <methods>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <fields>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <init>(...);
}

##---------------End: proguard configuration common for all Android apps ----------

-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}
##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.vfinworks.vfsdk.entity.** { *; }
-keep class com.vfinworks.vfsdk.model.** { *; }
-keep class com.vfinworks.vfsdk.activity.core.channel.** { *; }

##---------------End: proguard configuration for Gson  ----------

##---------------Begin: proguard configuration for Volley  ----------
-keep class com.vfinworks.vfsdk.http.** { *; }
-keep class com.vfinworks.vfsdk.common.HttpRequsetUri{ *; }
-keep class net.sqlcipher.**{ *; }
##---------------End: proguard configuration for Volley  ----------

##---------------Begin: proguard configuration for legacy  ----------
-keep class android.net.compatibility.** { *; }
-keep class android.net.http.** { *; }
-keep class com.android.internal.http.multipart.** { *; }
-keep class org.apache.commons.** { *; }
-keep class org.apache.http.** { *; }

##---------------Begin: proguard configuration for zhifubao  ----------
-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.app.AuthTask{ public *;}

##---------------Begin: proguard configuration for weixin  ----------
-keep class com.tencent.** { *;}

##---------------Begin: proguard configuration for easypermissions  ----------
-keep class pub.devrel.easypermissions.** { *;}

#-----------------------------pinyin4j------------------------------------------
-dontwarn net.soureceforge.pinyin4j.**
-dontwarn demo.**
-keep class net.sourceforge.pinyin4j.** { *;}
-keep class demo.** { *;}

# 银联
-dontwarn com.unionpay.**
-keep class com.unionpay.** { *; }

#忽略警告
#-ignorewarning

#处理警告
-dontwarn com.alipay.android.phone.**
-dontwarn org.apache.http.**
-dontwarn android.net.**

-keep class com.google.**{*;}
-keep class com.vfinworks.vfsdk.context.**{*;}
-keep class com.vfinworks.vfsdk.enumtype.**{*;}
-keep class com.vfinworks.vfsdk.SDKManager{*;}
-keepnames class com.vfinworks.vfsdk.common.Config{
*;
}
-keep class com.vfinworks.vfsdk.activity.BaseActivity{*;}
-keep class com.vfinworks.vfsdk.activity.login.BaseActivity{*;}
-keep class com.vfinworks.vfsdk.common.Utils{
*;
}
-keep class com.vfinworks.vfsdk.common.SharedPreferenceUtil{
*;
}
-keep class com.vfinworks.vfsdk.common.PermissionHelper{*;}
-keep class com.vfinworks.vfsdk.common.PermissionHelper$*{*;}