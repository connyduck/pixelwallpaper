-printconfiguration
-optimizationpasses 5
-allowaccessmodification
-repackageclasses ''

-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** w(...);
    public static *** v(...);
    public static *** i(...);
    public static *** e(...);
    public static *** getStackTraceString(...);
}