package com.Naukri.Naukri;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ProfileUpdateScheduler {

	private final NaukriService naukriService;

    @Autowired
    public ProfileUpdateScheduler(NaukriService naukriService) {
        this.naukriService = naukriService;
    }

    @Scheduled(fixedRateString = "${scheduler.profile.update.interval}")
    public void updateProfilePeriodically() {
        naukriService.updateProfile();
    }
}
