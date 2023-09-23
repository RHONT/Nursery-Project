package com.nursery.nursery_api.global;

import com.nursery.nursery_api.model.Volunteer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalVariable {
    /**
     * key - Volunteer
     * value - chat id human needy
     */
    public static final Map<Volunteer, Long> volunteersList = new ConcurrentHashMap<>();
}
