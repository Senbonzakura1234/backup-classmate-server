package com.app.manager.service.interfaceClass;


import com.app.manager.entity.Subscription;

import java.util.Optional;

public interface SubscriptionService {
    Optional<Subscription> getBasicSubscription();
}
