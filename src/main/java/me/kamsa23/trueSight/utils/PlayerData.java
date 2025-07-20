package me.kamsa23.trueSight.utils;

import java.util.HashMap;
import java.util.UUID;

public class PlayerData {
    private static final HashMap<UUID, Object> dataMap = new HashMap<>();

    public static void set(UUID uuid, Object value) {
        dataMap.put(uuid, value);
    }

    public static Object get(UUID uuid) {
        return dataMap.get(uuid);
    }

    public static boolean has(UUID uuid) {
        return dataMap.containsKey(uuid);
    }

    public static void remove(UUID uuid) {
        dataMap.remove(uuid);
    }
}