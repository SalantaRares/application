package ro.btrl.miswebappspringdemo.auth;

import ro.btrl.miswebappspringdemo.enums.AppParts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SecurityMapping {

    private static HashMap<String, String[]> securityMap;

    static {
        securityMap = new HashMap<>();
        securityMap.put(AppParts.F_REPORTING.getValue(), new String[]{"MIS Users", "MIS ANAF"});
    }

    public static String[] getAllValues() {
        List<String> list = new ArrayList();
        securityMap.values().stream().forEach(element -> {
            list.addAll(Arrays.asList(element));
        });

        return list.toArray(new String[list.size()]);
    }

    public static String[] getValues(String appPart) {
        return securityMap.get(appPart);
    }
}