import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        int totalLines = 0; // Счётчик для количества строк
        int googleBotCount = 0; // Счетчик для GoogleBot
        int yandexBotCount = 0; // Счетчик для YandexBot

        // Статистика для расчета трафика
        Statistics statistics = new Statistics();

        // Бесконечный цикл while
        while (true) {
            System.out.println("Введите путь к файлу (или 'exit' для выхода): ");
            String path = new Scanner(System.in).nextLine();

            // Проверка на выход
            if (path.equalsIgnoreCase("exit")) {
                System.out.println("Выход из программы.");
                break;
            }

            // Создаём объект File для указанного пути
            File file = new File(path);

            // Проверяем, существует ли файл
            boolean fileExists = file.exists();
            // Проверяем, является ли путь файлом (а не папкой)
            boolean isDirectory = file.isDirectory();
            // Условие проверки: файл не существует или это не файл
            if (!fileExists) {
                System.out.println("Файл не существует");
                continue;
            } else if (isDirectory) {
                System.out.println("Это не файл, а директория");
                continue;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;

                while ((line = reader.readLine()) != null) {
                    totalLines++;

                    if (line.length() > 1024) {
                        throw new LineLongException("Строка, длиной " + line.length() + " символов больше допустимого значения в 1024 символа.");
                    }

                    // Разбор строки лога
                    LogEntry entry;
                    try {
                        entry = new LogEntry(line);
                        statistics.addEntry(entry); // Добавление записи в статистику
                    } catch (Exception e) {
                        System.out.println("Ошибка при разборе строки: " + line);
                        continue;
                    }

                    // Разбор User-Agent
                    String userAgent = extractUserAgent(line);
                    if (userAgent != null) {
                        if (userAgent.contains("Googlebot")) {
                            googleBotCount++;
                        } else if (userAgent.contains("YandexBot")) {
                            yandexBotCount++;
                        }
                    }
                }
            } catch (LineLongException e) {
                System.out.println(e.getMessage());
                continue;
            } catch (IOException ex) {
                System.out.println("Ошибка при чтении файла: " + ex.getMessage());
            }

            // Подсчет доли запросов от Google и Yandex ботов
            double googleBotPart = (totalLines == 0) ? 0 : ((double) googleBotCount / totalLines) * 100;
            double yandexBotPart = (totalLines == 0) ? 0 : ((double) yandexBotCount / totalLines) * 100;

            // Подсчет среднего трафика за час
            double trafficRate = statistics.getTrafficRate();

            // Получение всех существующих страниц (код 200)
            System.out.println("Список всех существующих страниц:");
            for (String page : statistics.getAllPages()) {
                System.out.println(page);
            }

            // Получение всех несуществующих страниц (код 404)
            System.out.println("Список всех несуществующих страниц:");
            for (String page : statistics.getNonExistentPages()) {
                System.out.println(page);
            }

            // Получение статистики по операционным системам
            System.out.println("Статистика по операционным системам:");
            Map<String, Double> osStatistics = statistics.getOsStatistics();
            for (Map.Entry<String, Double> entry : osStatistics.entrySet()) {
                System.out.println("OS: " + entry.getKey() + ", Доля: " + entry.getValue());
            }

            // Получение статистики по браузерам
            System.out.println("Статистика по браузерам:");
            Map<String, Double> browserStatistics = statistics.getBrowserStatistics();
            for (Map.Entry<String, Double> entry : browserStatistics.entrySet()) {
                System.out.println("Browser: " + entry.getKey() + ", Доля: " + entry.getValue());
            }

            // Вывод результатов
            System.out.println("Количество строк в файле: " + totalLines);
            System.out.println("Доля запросов от Googlebot: " + googleBotPart + "%");
            System.out.println("Доля запросов от YandexBot: " + yandexBotPart + "%");
            System.out.println("Средний трафик за час: " + trafficRate + " байт/час");

            //Вывод статистики за час
            double avgVisitsPerHour = statistics.getAverageVisitsPerHour();
            System.out.println("Среднее количество посещений за час: " + avgVisitsPerHour);

            double avgErrorRequestsPerHour = statistics.getAverageErrorRequestsPerHour();
            System.out.println("Среднее количество ошибочных запросов за час: " + avgErrorRequestsPerHour);

            //Вывод средней посещаемости одного пользователя
            double avgVisitsPerUser = statistics.getAverageVisitsPerUser();
            System.out.println("Средняя посещаемость одного пользователя: " + avgVisitsPerUser);
        }
    }

    // Метод для извлечения юзер агента
    private static String extractUserAgent(String line) {
        String[] parts = line.split("\""); // Разбиваем строку по кавычкам
        return (parts.length > 5) ? parts[5] : null; // User-Agent обычно находится в шестой части (индекс 5)
    }
}
