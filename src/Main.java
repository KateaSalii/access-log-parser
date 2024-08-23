import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Введите первое число:");
        int firstNumber= new  Scanner(System.in).nextInt();
        System.out.println("Введите второе число:");
        int secondNumber= new  Scanner(System.in).nextInt();

        System.out.println("Сумма чисел: " + (firstNumber + secondNumber));
        System.out.println("Разность чисел: " + (firstNumber - secondNumber));
        System.out.println("Произведение чисел: " + (firstNumber * secondNumber));
        System.out.println("Частное чисел: " + ((double)firstNumber / (double)secondNumber));

    }
}
