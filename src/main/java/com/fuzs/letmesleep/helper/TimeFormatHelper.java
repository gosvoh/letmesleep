package com.fuzs.letmesleep.helper;

import com.fuzs.letmesleep.handler.ConfigBuildHandler;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimeFormatHelper {

    public static String formatTime(long time) {

        int i = (int) ((time + 6000L) % 24000L);
        int hours = i / 1000;
        int minutes = (int) ((i % 1000) * 3 / 50.0F);

        String format = ConfigBuildHandler.sleepTimingsConfig.timeTwelve ? "h:mm a" : "H:mm";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalTime.of(hours, minutes).format(formatter);

    }

}
