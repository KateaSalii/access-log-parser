import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        int filePathCount = 0; // Счётчик для верно указанных файлов
        int totalLines = 0; // Счётчик для количества строк
        int maxLength = 0; //Длина самой длинной строки
        int minLength = Integer.MAX_VALUE; // Длина самой короткой строки

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
            // Если файл существует и это именно файл
            filePathCount++; // Увеличиваем счетчик верно указанных файлов

            try {
                FileReader fileReader = new FileReader(path);
                BufferedReader reader = new BufferedReader(fileReader);
                String line;

                while ((line = reader.readLine()) != null) {
                    int length = line.length();
                    totalLines++;

                    //Проверка длины строки
                    if (length > 1024) {
                        throw new LineLongException("Строка, длиной " + length + " символов больше допустимого значения в 1024 символов.");
                    }

                    //Обновление максимальной и минимальной длины строки
                    if (length > maxLength) {
                        maxLength = length;
                    }
                    if (length < minLength) {
                        minLength = length;
                    }
                }
                reader.close();
            } catch (LineLongException e) {
                System.out.println(e.getMessage());
                continue;
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            System.out.println("Путь указан верно. Это файл номер " + filePathCount);
            System.out.println("Общее количество строк в файле " + totalLines);
            System.out.println("Длина самой длинной строки в файле " + maxLength);
            System.out.println("Длина самой короткой строки в файле " + (minLength == Integer.MAX_VALUE ? 0 : minLength));
        }
    }
}
