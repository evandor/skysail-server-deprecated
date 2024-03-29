package io.skysail.testsupport;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkUtil;
import org.restlet.data.MediaType;
import org.restlet.engine.resource.VariantInfo;
import org.restlet.representation.Variant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestsupportTestBase {

    protected static final Variant HTML_VARIANT = new VariantInfo(MediaType.TEXT_HTML);
    protected static final Variant JSON_VARIANT = new VariantInfo(MediaType.APPLICATION_JSON);

    protected Bundle thisBundle = FrameworkUtil.getBundle(this.getClass());
    protected SecureRandom random = new SecureRandom();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public TestRule watcher = new TestWatcher() {
        @Override
        protected void starting(Description description) {
            log.info("");
            log.info("--------------------------------------------");
            log.info("running test '{}'", description.getMethodName());
            log.info("--------------------------------------------");
            log.info("");
        }
    };

    protected int determinePort() {
        log.info("setting up test case...");
//        ServiceReference<InstallationProvider> serviceReference = this.thisBundle.getBundleContext().getServiceReference(InstallationProvider.class);
//        InstallationProvider service = thisBundle.getBundleContext().getService(serviceReference);
//        log.info("setting test port to {}", service.getPort());
        return 2015;//service.getPort();
    }

    protected void stopAndStartBundle(Class<?> cls) throws BundleException {
        Bundle bundle = FrameworkUtil.getBundle(cls);
        bundle.stop();
        bundle.start();
    }

    protected void capture(String html) throws IOException {
        StringBuilder path = new StringBuilder("generated").append(File.separatorChar).append("test-reports-largeTests").append(File.separatorChar);
        path.append(getFileName());
        new File(path.toString()).mkdirs();
        path.append(File.separatorChar);
        path.append(getMethodName()).append(".html");
        Files.write(Paths.get(path.toString()), Arrays.stream(html.split("\n")).collect(Collectors.toList()));
    }

    private static String getFileName() {
        return Thread.currentThread().getStackTrace()[3].getFileName().replace(".java", "");
    }

    private static String getMethodName() {
        return Thread.currentThread().getStackTrace()[4].getMethodName();
    }

}
