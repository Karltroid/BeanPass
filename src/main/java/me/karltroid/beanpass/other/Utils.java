package me.karltroid.beanpass.other;

import java.text.DecimalFormat;

public class Utils
{
    public static String formatDouble(double value)
    {
        value = Math.round(value * 100.0) / 100.0;

        DecimalFormat decimalFormat;
        if (value == (long) value) decimalFormat = new DecimalFormat("#"); // If the value is an integer, show it without decimals
        else decimalFormat = new DecimalFormat("#.00"); // If the value has decimals, show it with two decimal places

        return decimalFormat.format(value);
    }
}
