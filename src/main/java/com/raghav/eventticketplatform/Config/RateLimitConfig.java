package com.raghav.eventticketplatform.Config;

import io.github.bucket4j.BucketConfiguration;

import java.time.Duration;

public class RateLimitConfig {
    public static BucketConfiguration loginLimiter(){
        return BucketConfiguration.builder()
                .addLimit(Limit->
                        Limit.capacity(3).refillGreedy(3, Duration.ofMinutes(1)))
                .build();
    }
    public static BucketConfiguration EventCreationLimit(){
        return BucketConfiguration.builder().addLimit(bandwidthBuilderCapacityStage ->
                bandwidthBuilderCapacityStage.capacity(3).refillGreedy(3,Duration.ofDays(1)))
                .build();
    }
    public static BucketConfiguration registerationLimiter(){
        return BucketConfiguration.builder()
                .addLimit(Limit ->
                        Limit.capacity(5).refillGreedy(5,Duration.ofHours(5)))
                .build();
    }
    public static BucketConfiguration ticketPurchaseLimiter(){
        return BucketConfiguration.builder()
                .addLimit(limit ->
                        limit.capacity(1).refillGreedy(1,Duration.ofMinutes(5)))
                .build();
    }
}
