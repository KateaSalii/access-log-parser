import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        int totalLines = 0; // Счётчик для количества строк
        int googleBotCount = 0; //Счетчик для GoogleBot
        int yandexBotCount = 0; //Счетчик для YandexBot

        // Бесконечный цикл while
        while (true) {
            System.out.println("Введите путь к файлу ");
            String path = new Scanner(System.in).nextLine();
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

            try {
                FileReader fileReader = new FileReader(path);
                BufferedReader reader = new BufferedReader(fileReader);
                String line;

                while ((line = reader.readLine()) != null) {
                    totalLines++;

                    if (line.length() > 1024) {
                        throw new LineLongException("Строка, длиной " + line.length() + " символов больше допустимого значения в 1024 символов.");
                    }

                    //Разбор строки лога
                    String userAgent = extractUserAgent(line);
                    if (userAgent != null) {
                        if (userAgent.contains("Googlebot")) {
                            googleBotCount++;
                        } else if (userAgent.contains("YandexBot")) {
                            yandexBotCount++;
                        }
                    }
                }
                reader.close();
            } catch (LineLongException e) {
                System.out.println(e.getMessage());
                continue;
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            //Подсчет доли запросов от гугл и яндекс ботов
            double googleBotPart = totalLines == 0 ? 0 : ((double) googleBotCount / totalLines) * 100;
            double yandexBotPart = totalLines == 0 ? 0 : ((double) yandexBotCount / totalLines) * 100;

            //Вывод результатов
            System.out.println("Количество строк в файле: " + totalLines);
            System.out.println("Доля запросов от Googlebot: " + googleBotPart);
            System.out.println("Доля запросов от YandexBot: " + yandexBotPart);
        }
    }

    //Метод для извлечения юзер агента
    private static String extractUserAgent(String line) {
        String[] parts = line.split("\""); // Разбиваем строку по кавычкам
        if (parts.length > 5) {
            return parts[5]; // User-Agent обычно находится в шестой части (индекс 5)
        }
        return null; //
    }
}
