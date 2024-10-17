import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

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

            // Обработка файла
            File file = new File(path);
            if (!file.exists() || !file.isFile()) {
                System.out.println("Файл не существует или это не файл. Попробуйте снова.");
                continue;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    totalLines++;
                    // Проверяем длину строки
                    if (line.length() > 1024) {
                        System.out.println("Строка длиной " + line.length() + " символов превышает допустимое значение в 1024 символа.");
                        continue;
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
            } catch (IOException e) {
                System.out.println("Ошибка при чтении файла: " + e.getMessage());
            }

            // Подсчет доли запросов от Google и Yandex ботов
            double googleBotPart = (totalLines == 0) ? 0 : ((double) googleBotCount / totalLines) * 100;
            double yandexBotPart = (totalLines == 0) ? 0 : ((double) yandexBotCount / totalLines) * 100;

            // Подсчет среднего трафика за час
            double trafficRate = statistics.getTrafficRate();

            // Вывод результатов
            System.out.println("Количество строк в файле: " + totalLines);
            System.out.println("Доля запросов от Googlebot: " + googleBotPart + "%");
            System.out.println("Доля запросов от YandexBot: " + yandexBotPart + "%");
            System.out.println("Средний трафик за час: " + trafficRate + " байт/час");
        }
    }

    // Метод для извлечения юзер агента
    private static String extractUserAgent(String line) {
        String[] parts = line.split("\""); // Разбиваем строку по кавычкам
        return (parts.length > 5) ? parts[5] : null; // User-Agent обычно находится в шестой части (индекс 5)
    }
}
