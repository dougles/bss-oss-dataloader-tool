package dataloader;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import configuration.Configuration;
import log.DataLogger;

import java.util.ArrayList;
import java.util.List;

public class Application {
    private static Injector injector;

    public static Injector getInjector() {
        if (injector == null) {
            try {
                List<AbstractModule> modules = new ArrayList<>();
                List<String> modulesClass = Configuration.current().modules;
                for (String m : modulesClass) {
                    Class<?> clazz = Class.forName(m);
                    modules.add((AbstractModule) clazz.newInstance());
                }
                injector = Guice.createInjector(modules);
            } catch (Exception e) {
                DataLogger.error(Application.class, e.getMessage());
            }
        }

        return injector;
    }

    public static void run() {
        final DataLoader loader = Application.getInjector().getInstance(DataLoader.class);
        try {
            loader.execute();
        } catch (Exception e) {
            DataLogger.error(Application.class, e.getMessage());
        }
    }
}
