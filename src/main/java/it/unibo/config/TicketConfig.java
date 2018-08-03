package it.unibo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@RefreshScope
public class TicketConfig {

	@Value("${app.version}") private int appVersion;
    public int getAppVersion(){ return appVersion; }
    
}
