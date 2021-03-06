package com.pmk.twovidzoneclip.main;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.ProvisionException;
import com.pmk.twovidzoneclip.handler.RestHandler;
import com.pmk.twovidzoneclip.handler.WebHandler;
import com.pmk.twovidzoneclip.handler.impl.RestHandlerImpl;
import com.pmk.twovidzoneclip.handler.impl.WebHandlerImpl;
import com.pmk.twovidzoneclip.injection.VidzUrlsModule;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.platform.Verticle;

public final class VertxServer extends Verticle {

    private static final Integer PORT_NUMBER = 8182;

    @Override
    public final void start() {
        RestHandler restHandler = null;
        WebHandler webHandler = null;
        final RouteMatcher routeMatcher = new RouteMatcher();
        try {

            Injector injector = Guice.createInjector(new VidzUrlsModule());

            restHandler = injector.getInstance(RestHandler.class);

            // Catch all - serve the index page
            webHandler = injector.getInstance(WebHandler.class);

        } catch (ProvisionException e) {
            restHandler = new RestHandlerImpl(null);
            webHandler = new WebHandlerImpl();
        } finally {
            routeMatcher.get("/videoresources/:page/:numberOfResults", restHandler);
            routeMatcher.get("/addvideo/:title/:video/:sound", restHandler);
            routeMatcher.getWithRegEx(".*", webHandler);
            vertx.createHttpServer().requestHandler(
                    routeMatcher).listen(PORT_NUMBER);
        }
    }
}
