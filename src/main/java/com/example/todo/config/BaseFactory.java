package com.example.todo.config;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.javafaker.Faker;

public abstract class BaseFactory {
    private final Faker faker = new Faker();
    private static final AtomicInteger counter = new AtomicInteger();

    protected LocalDate randomPastDate(int monthsAgo) {

        int days = faker.number().numberBetween(1, monthsAgo * 30);
        return Instant.ofEpochMilli(faker.date().past(days, TimeUnit.DAYS).getTime()).atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    protected int incrementAndGet() {
        return counter.incrementAndGet();
    }

    protected Faker faker() {
        return faker;
    }

    protected LocalDateTime randomDateAfter(LocalDateTime after, int minDays, int maxDays) {
        long randomDays = ThreadLocalRandom.current().nextLong(minDays, maxDays);
        long randomHours = ThreadLocalRandom.current().nextLong(0, 24);
        long randomMinutes = ThreadLocalRandom.current().nextLong(0, 60);

        return after
                .plusDays(randomDays)
                .plusHours(randomHours)
                .plusMinutes(randomMinutes)
                .truncatedTo(ChronoUnit.MINUTES);
    }

    public abstract boolean repoEmpty();

    public abstract void clear();

    public abstract Long findMaxId();

}
