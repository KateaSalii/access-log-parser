import java.io.File;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        int filePathCount = 0; // Счётчик для верно указанных файлов

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
            System.out.println("Путь указан верно. Это файл номер " + filePathCount);
        }
    }
}
