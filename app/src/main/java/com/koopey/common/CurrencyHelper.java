package com.koopey.common;

public class CurrencyHelper {

    private final static String LOG_HEADER = "CURRENCY:HELPER";

    public static String currencyCodeToSymbol(String currencyCode) {
        if (currencyCode.equals("btc")) {
            return "฿";
        } else if (currencyCode.equals("eth")) {
            return "Ξ";
        } else if (currencyCode.equals("eur")) {
            return "€";
        } else if (currencyCode.equals("gbp")) {
            return "£";
        } else if (currencyCode.equals("usd")) {
            return "$";
        } else if (currencyCode.equals("toc")) {
            return "T";
        } else if (currencyCode.equals("zar")) {
            return "R";
        } else {
            return "£";
        }
    }

    public static String currencySymbolToCode(String currencySymbol) {
        if (currencySymbol.equals("฿")) {
            return "btc";
        } else if (currencySymbol.equals("Ξ")) {
            return "eth";
        } else if (currencySymbol.equals("€")) {
            return "eur";
        } else if (currencySymbol.equals("£")) {
            return "gbp";
        } else if (currencySymbol.equals("$")) {
            return "usd";
        } else if (currencySymbol.equals("T")) {
            return "toc";
        } else if (currencySymbol.equals("R")) {
            return "zar";
        } else {
            return "btc";
        }
    }
}
