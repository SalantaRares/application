package ro.btrl.miswebappspringdemo.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class AppConfig {
    private static final List<String> productionMachines = new ArrayList<>(Arrays.asList("mis-wbl-01-prd", "mis-wbl-02-prd"));
    private static final List<String> developmentMachines = new ArrayList<>(Arrays.asList("mdl07cj001"));
    private static AppConfig appConfigInstance = null;
    public boolean IS_PRODUCTION = false;
    public String ANAF_TARGET_DIRECTORY = null;

    private Parameters parameters = null;

    public static AppConfig getInstance() {
        if (appConfigInstance == null) {
            appConfigInstance = new AppConfig();
        }
        return appConfigInstance;
    }

    public Parameters getParameters() {
        return parameters;
    }

    private AppConfig() {
        ServerHostInfo info = ServerHostInfo.getInstance();
        if (productionMachines.contains(info.getMachineName())) {
            this.parameters = configMap.get("PROD");
            this.ANAF_TARGET_DIRECTORY = TARGET_DIRECTORY_FOLDER.get("PROD");
            this.IS_PRODUCTION = true;
        }
        if (developmentMachines.contains(info.getMachineName())) {
            this.parameters = configMap.get("DEV");
            this.ANAF_TARGET_DIRECTORY = TARGET_DIRECTORY_FOLDER.get("DEV");
        }
    }

    private static HashMap<String, Parameters> configMap;

    static {
        configMap = new HashMap<>();
        configMap.put("PROD", new Parameters("HTTP/mis-webappl.bt.wan", "/u01/oracle/projects/mis_domain/mis-webappl.bt.wan.keytab"));
        configMap.put("DEV", new Parameters("HTTP/mdl07cj001.bt.wan@BT.WAN", "/u01/app/oracle/product/fmw/12c/user_projects/domains/mis_adf_domain/mdl07cj001.bt.wan.keytab"));
    }

    private static HashMap<String, String> TARGET_DIRECTORY_FOLDER;

    static {
        TARGET_DIRECTORY_FOLDER = new HashMap<>();
        TARGET_DIRECTORY_FOLDER.put("PROD", "/mis_extrase/extfiles/anaf/upload/");
        TARGET_DIRECTORY_FOLDER.put("DEV", "/u01/app/oracle/product/fmw/12c/user_projects/domains/mis_adf_domain/servers/adf_server2/anaf/");
    }


    public static class Parameters {
        private final String keyTabName;
        private final String keyTabLocation;

        public Parameters(String keyTabName, String keyTabLocation) {
            this.keyTabName = keyTabName;
            this.keyTabLocation = keyTabLocation;
        }

        public String getKeyTabName() {
            return keyTabName;
        }

        public String getKeyTabLocation() {
            return keyTabLocation;
        }
    }
}
