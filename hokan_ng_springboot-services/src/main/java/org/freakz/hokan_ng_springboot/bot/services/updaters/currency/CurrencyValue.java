package org.freakz.hokan_ng_springboot.bot.services.updaters.currency;

/**
 * User: petria
 * Date: 12/12/13
 * Time: 10:30 PM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
public class CurrencyValue {

    private String name;
    private Double value;

    public static String[] CURRENCIES = {
            "ANG", "VEF", "BHD", "NPR", "XOF", "JMD", "ILS", "OMR", "NAD", "DZD", "ISK",
            "AUD", "HNL", "SKK", "RON", "TND", "EUR", "JOD", "IDR", "KES", "SEK", "MDL",
            "QAR", "PKR", "BDT", "CAD", "BOB", "BND", "TRY", "SLL", "MKD", "BWP", "MXN",
            "PEN", "DOP", "NZD", "TZS", "LTL", "NOK", "KRW", "RUB", "CHF", "DKK", "ARS",
            "NIO", "CZK", "KYD", "FJD", "MVR", "SAR", "PHP", "ZMK", "CNY", "LBP", "LKR",
            "GBP", "UYU", "TTD", "LVL", "VND", "NGN", "RSD", "HKD", "EGP", "CRC", "USD",
            "COP", "PYG", "UZS", "INR", "YER", "JPY", "KWD", "KZT", "HUF", "SCR", "MUR",
            "BGN", "MYR", "AED", "UGX", "EEK", "UAH", "THB", "ZAR", "PGK", "TWD", "CLP",
            "MAD", "SVC", "PLN", "SGD", "BRL", "HRK"};

    public CurrencyValue(String name, Double value) {
        this.name = name;
        this.value = value;
    }

    public Double getValue() {
        return value;
    }

}
