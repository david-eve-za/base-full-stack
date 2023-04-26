package gon.cue.basefullstack.config;

import gon.cue.basefullstack.util.delete.StartUp;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
public class CatchingEvents {

    @EventListener(ApplicationReadyEvent.class)
    public void closeMyResource(ApplicationReadyEvent event) throws Exception {
        StartUp startUp = event.getApplicationContext().getBean(StartUp.class);
        event.getApplicationContext().getBeanFactory().destroyBean(startUp);
    }
}
