package com.reedwellarts.wormhole.client;

import java.util.ArrayList;
import java.util.List;

public class PortalNames {

    private static List<String> KNOWN = new ArrayList<>();

    public static void updateNames(List<String> names){
        KNOWN.clear();
        KNOWN.addAll(names);
    }

    public static boolean isNameTaken(String name){
        return KNOWN.contains(name);
    }
}
