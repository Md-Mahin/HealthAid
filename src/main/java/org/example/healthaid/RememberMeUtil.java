package org.example.healthaid;

import java.util.prefs.Preferences;
public class RememberMeUtil {
    private static final Preferences prefs = Preferences.userRoot().node("HealthAID");

    public static void saveUser(int userId, String name, String role) {
        prefs.putInt("userId", userId);
        prefs.put("name", name);
        prefs.put("role", role);
        prefs.putBoolean("remember", true);
    }

    public static void clearUser() {
        prefs.remove("userId");
        prefs.remove("name");
        prefs.remove("role");
        prefs.putBoolean("remember", false);
    }

    public static boolean isRemembered() {
        return prefs.getBoolean("remember", false);
    }

    public static int getUserId() {
        return prefs.getInt("userId", -1);
    }

    public static String getName() {
        return prefs.get("name", null);
    }

    public static String getRole() {
        return prefs.get("role", null);
    }
}