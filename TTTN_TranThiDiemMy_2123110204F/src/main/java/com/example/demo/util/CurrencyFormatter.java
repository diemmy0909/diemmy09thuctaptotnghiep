package com.example.demo.util;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

@Component("currencyFormatter")
public class CurrencyFormatter {

    private static final BigDecimal EXCHANGE_RATE_USD_VND = new BigDecimal("25000");

    public String format(BigDecimal amount, String currencyCode) {
        if (amount == null) return "0";

        if ("USD".equalsIgnoreCase(currencyCode)) {
            // Convert VND to USD
            BigDecimal converted = amount.divide(EXCHANGE_RATE_USD_VND, 2, java.math.RoundingMode.HALF_UP);
            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
            return format.format(converted);
        } else {
            // Default VND
            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            return format.format(amount);
        }
    }
}
