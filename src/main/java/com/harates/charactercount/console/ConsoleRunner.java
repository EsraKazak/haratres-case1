package com.harates.charactercount.console;

import java.util.Scanner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.harates.charactercount.dto.CharacterCountRequest;
import com.harates.charactercount.dto.CharacterCountResponse;
import com.harates.charactercount.exception.InvalidInputException;
import com.harates.charactercount.service.CharacterCountService;

@Component
@Profile("console")
public class ConsoleRunner implements CommandLineRunner {

    private final CharacterCountService characterCountService;

    public ConsoleRunner(CharacterCountService characterCountService) {
        this.characterCountService = characterCountService;
    }

    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);
        int maxLength;
        while (true) {
            System.out.print("Lütfen cümlenin maksimum uzunluğunu giriniz: ");
            String giris = scanner.nextLine();
            try {
                maxLength = Integer.parseInt(giris.trim());
                characterCountService.validateMaxLength(maxLength);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Lütfen geçerli bir sayı giriniz.");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        System.out.println("girdiğiniz değer: " + maxLength);

        String sentence;
        while (true) {
            System.out.print("Lütfen cümleyi giriniz: ");
            sentence = scanner.nextLine();
            try {
                characterCountService.validateSentence(sentence, maxLength);
                break;
            } catch (InvalidInputException e) {
                System.out.println(e.getMessage());
            }
        }

        System.out.println("girdiğiniz cümle: " + sentence);

        String caseSensitive;
        while (true) {
            System.out.print("Harf duyarlılığı istiyor musunuz? (Evet/Hayır): ");
            caseSensitive = scanner.nextLine();
            try {
                characterCountService.validateCaseSensitive(caseSensitive);
                break;
            } catch (InvalidInputException e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println("girdiğiniz değer: " + caseSensitive);

        String character;
        while (true) {
            System.out.print("Lütfen sayılacak karakteri giriniz: ");
            character = scanner.nextLine();
            try {
                characterCountService.validateCharacter(character);
                break;
            } catch (InvalidInputException e) {
                System.out.println(e.getMessage());
            }
        }
        CharacterCountRequest request = new CharacterCountRequest(maxLength, sentence, caseSensitive, character);
        CharacterCountResponse response = characterCountService.countResponse(request);
        System.out.println(response.message());
    }
}
