package qiwi.com;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Currency;
import java.util.Locale;
import java.util.Scanner;

//Первый вариант через консоль
public class ExchangeRate {
    // private static final Pattern PARSING_PATTERN =
    private static final Locale RUSSIAN_LOCALE = new Locale("ru", "RU");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final HttpClient client = HttpClient.newHttpClient();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            try {
                var input = sc.nextLine();
                input = deleteExcessSpaces(input);
                if (input.equalsIgnoreCase("exit")) {
                    return;
                }
                String[] parse = input.split("\\s+");
                if (parse.length != 3) {
                    System.out.println("Некорректные данные");
                    break;
                }
                String out = findExchangeRate(parse);

            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            } catch (DateTimeParseException e) {
                System.out.println("Неверно указан формат даты");
            } catch (DateTimeException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static String findExchangeRate(String[] parse) {
        boolean isCorrectParam = checkParams(parse);
        if (isCorrectParam) {
            String stringCurrency = splitByEqualSymbol(parse[1]);
            String stringDate = splitByEqualSymbol(parse[2]);

            var currency = Currency.getInstance(stringCurrency);
            String result = currency.getCurrencyCode() + " (" + currency.getDisplayName(RUSSIAN_LOCALE) + ") ";
            System.out.println(result);

            LocalDate date = LocalDate.parse(stringDate, DATE_FORMAT);
            var now = LocalDate.now();
            if (date.isAfter(now)) {
                throw new DateTimeException("А сегодня в завтрашний день не все могут смотреть. " +
                        "Вернее смотреть могут не только лишь все, мало кто может это делать (c) Кличко. " +
                        "Дата не может быть позже сегодняшнего дня");
            }
            double rate = getRate(currency.getCurrencyCode(), date);
            System.out.println(date);
            return "test";
        } else {
            throw new IllegalArgumentException("Введены некорректные параметры");
        }
    }

    private static double getRate(String currencyCode, LocalDate date) {
        if (currencyCode.equals("RUB")) {
            return 1d;
        }
        String address = "http://www.cbr.ru/scripts/XML_val.asp?d=0";
        URI url = URI.create(address);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                var data = response.body();
                //Не успел доделать, но суть была в том, что мы сохраняем xml файл с уникальными кодами ЦБ в файл, с помощью
                // javax делаем анмаршлинг в модели ItemRate, Item и потом просто ищем нужный нам код валюты формируем точно такой же запрос на
                // api http://www.cbr.ru/scripts/XML_dynamic.asp?date_req1=02/03/2001&date_req2=14/03/2001&VAL_NM_RQ=R01235
                new RatesFileManager().save(data);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static boolean checkParams(String[] parse) {
        if (parse[0].equals("currency_rates") && parse[1].startsWith("--code") && parse[2].startsWith("--date")) {
            return true;
        } else {
            return false;
        }
    }

    private static String splitByEqualSymbol(String s) {
        var splited = s.split("=", 2);
        if (splited.length != 2) {
            throw new IllegalArgumentException("неправильно введенный формат данных. Смотри около  знака = ");
        }
        if (splited[1].isEmpty()) {
            throw new IllegalArgumentException("неправильно введенный формат данных. Смотри около  знака = ");
        }
        return splited[1];
    }

    private static String deleteExcessSpaces(String input) {
        input = input.trim();
        return input.replaceAll("\\s+", " ");
    }
}
