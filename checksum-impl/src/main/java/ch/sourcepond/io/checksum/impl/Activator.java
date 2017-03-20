package ch.sourcepond.io.checksum.impl;

import ch.sourcepond.commons.smartswitch.lib.SmartSwitchActivatorBase;
import ch.sourcepond.io.checksum.api.ResourcesFactory;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.Executors.newScheduledThreadPool;

/**
 * Created by rolandhauser on 20.02.17.
 */
public class Activator extends SmartSwitchActivatorBase {

    @Override
    public void init(final BundleContext bundleContext, final DependencyManager dependencyManager) throws Exception {
        dependencyManager.add(createComponent().
                setInterface(ResourcesFactory.class.getName(), null).
                setImplementation(ResourcesFactoryImpl.class).
                add(
                    createSmartSwitchBuilder(ScheduledExecutorService.class).
                            setFilter("(sourcepond.io.checksum.updateexecutor=*)").
                            setShutdownHook(ExecutorService::shutdown).
                            build(() -> newScheduledThreadPool(4))
                ).setComposition("getComposition")
        );
    }
}
