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

# ------------------------------------------------------------------------------------------------
# Gson (reflective deserialization of the app's own DTOs)
#
# Gson populates data/remote/dto/** reflectively, so R8 must not strip or repurpose those classes'
# fields. Gson 2.14, Retrofit 3.0, and OkHttp 5 all ship their own consumer rules (TypeToken,
# @SerializedName field retention with allowobfuscation, retrofit2/okhttp3 internals), so those
# library-side rules are deliberately NOT duplicated here. What the libraries cannot know about is
# this app's DTO package — R8 full mode may still remove the classes themselves (or their
# constructors) when their only "use" is reflective instantiation through the Gson converter.
# ------------------------------------------------------------------------------------------------

# Generic signatures are required so Gson can resolve List<UserDto> etc. through TypeToken.
-keepattributes Signature

# Keep the DTO classes and their fields (names may still be obfuscated only where @SerializedName
# provides the wire name, but keeping them outright is the simplest rule that survives full mode).
-keep class me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.remote.dto.** { <init>(...); }
-keepclassmembers class me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.remote.dto.** { <fields>; }
