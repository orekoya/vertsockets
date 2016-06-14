package io.hyperbuffer.samples.vertsockets.core.util;

import io.hyperbuffer.samples.vertsockets.core.vert.Instance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author vorekoya on 11/06/2016.
 */
@Component
public class Sample {

    private final Instance instance;

    @Autowired
    public Sample(Instance instance) {
        this.instance = instance;
        startTimer();
    }

    private void startTimer() {
        this.instance.getVertxInstance().setPeriodic(2000, event -> {

            this.instance.getVertxInstance().executeBlocking(event12 -> {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(event12.toString());
            }, false, event1 -> System.out.println(event1.toString() + "::2"));

        });
    }


}
