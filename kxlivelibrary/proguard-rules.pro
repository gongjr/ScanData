# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in F:/adt_64/Android/android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}


#-dontwarn 缺省proguard 会检查每一个引用是否正确,
#但是第三方库里面往往有些不会用到的类,没有正确引用。如果不配置的话,系统就会报错

#roboguice
#-libraryjars   libs/roboguice-2.0.jar
-dontwarn roboguice.**
-keep class roboguice.**{ *; }
#-libraryjars libs/guice-3.0-no_aop.jar
-dontwarn com.google.inject.**
-keep class com.google.inject.**{ *; }
#-libraryjars libs/jsr305-1.3.9.jar
-dontwarn javax.annotation.**
-keep class javax.annotation.**{ *; }
#-libraryjars libs/javax.inject-1.jar
-dontwarn javax.inject.**
-keep class javax.inject.**{ *; }
-keepattributes **


#litepal
#-libraryjars libs/litepal-1.2.0.jar
-dontwarn org.litepal.*
-keep class org.litepal.** { *; }
-keep enum org.litepal.**
-keep interface org.litepal.** { *; }
-keep public class * extends org.litepal.**
-keepattributes *Annotation*
-keepclassmembers class * extends org.litepal.crud.DataSupport{
    *;
}

#eventbus
-keep class de.greenrobot.event.**{ *; }
-keepclassmembers class ** {
    public void onEvent*(**);
    void onEvent*(**);
    public boolean onEvent*(**);
        boolean onEvent*(**);
}


#Gson
-keep class com.google.**{*;}
##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature
# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }
# Application classes that will be serialized/deserialized over Gson
-keep class com.asiainfo.mealorder.entity.** { *; }  ##������Ҫ�ĳɽ������ĸ�  javabean
##---------------End: proguard configuration for Gson  ----------


#极光
-dontoptimize
-dontpreverify

-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }

#volley
-keep class com.android.volley.**  {* ;}
-keep class org.apache.http.**  {* ;}
-keepattributes Signature,RuntimeVisibleAnnotations,AnnotationDefault,*Annotation*

#不混淆资源类
-keepclassmembers class **.R$* {
    public static <fields>;
}

#保持枚举类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#保持 Serializable 不被混淆
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
#保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
#保持自定义控件类不被混淆
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
#如果引用了v4或者v7包
-dontwarn android.support.**
#我是以libaray的形式引用了一个图片加载框架,如果不想混淆 keep 掉
-keep class com.nostra13.universalimageloader.** { *; }

#xstream xml解析
-dontwarn com.thoughtworks.xstream.**
-keep class com.thoughtworks.xstream.**{*;}

