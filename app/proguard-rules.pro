# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /opt/android-sdk/tools/proguard/proguard-android.txt
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

-dontwarn java.applet.Applet
-dontwarn sun.misc.Cleaner
-dontwarn sun.misc.Unsafe
-dontwarn sun.nio.ch.FileChannelImpl
-dontwarn sun.reflect.ReflectionFactory

-dontwarn com.sun.jdi.Bootstrap
-dontwarn com.sun.jdi.connect.AttachingConnector
-dontwarn com.sun.jdi.connect.Connector
-dontwarn com.sun.jdi.connect.Connector$Argument
-dontwarn com.sun.jdi.connect.IllegalConnectorArgumentsException
-dontwarn com.sun.jdi.event.Event
-dontwarn com.sun.jdi.event.EventIterator
-dontwarn com.sun.jdi.event.EventQueue
-dontwarn com.sun.jdi.event.EventSet
-dontwarn com.sun.jdi.event.MethodEntryEvent
-dontwarn com.sun.jdi.ReferenceType
-dontwarn com.sun.jdi.request.EventRequest
-dontwarn com.sun.jdi.request.EventRequestManager
-dontwarn com.sun.jdi.request.MethodEntryRequest
-dontwarn com.sun.jdi.VirtualMachine
-dontwarn com.sun.jdi.VirtualMachineManager
