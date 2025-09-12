package com.specsShope.specsBackend.Config;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@EnableScheduling
public class SelfPingScheduler {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String appUrl = "https://your-backend-url.onrender.com/healthz"; // <-- replace with your Render URL

    // Every 10 minutes (600000 ms)
    @Scheduled(fixedRate = 600_000)
    public void pingSelf() {
        try {
            restTemplate.getForObject(appUrl, String.class);
            System.out.println("Self-ping successful");
        } catch (Exception e) {
            System.err.println("Self-ping failed: " + e.getMessage());
        }
    }
}
