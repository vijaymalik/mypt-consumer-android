# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keepattributes Signature
-keepattributes Exceptions

-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }

-dontwarn okhttp3.**
-dontwarn okio.**

-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

-keepattributes *Annotation*

-keep class com.google.gson.** { *; }
-keep class sun.misc.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

-dontwarn kotlin.**
-keep class kotlin.** {*;}
-keep class kotlin.Metadata { *; }
-keepclassmembers class ** {
    @kotlin.Metadata *;
}

-keepclassmembers class **$WhenMappings {
    <fields>;
}

-keep class com.google.android.gms.** { *; }
-keep class com.google.android.libraries.places.** { *; }
-keep class com.google.maps.android.** { *; }

-keep class co.com.mypt.model.** { *; }
