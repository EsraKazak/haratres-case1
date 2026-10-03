package com.harates.charactercount.service;

import java.util.Locale;
import org.springframework.stereotype.Service;
import com.harates.charactercount.dto.CharacterCountResponse;
import com.harates.charactercount.exception.InvalidInputException;
import com.harates.charactercount.dto.CharacterCountRequest;

@Service
public class CharacterCountService {
    public CharacterCountResponse countResponse(CharacterCountRequest request) {

        validateMaxLength(request.maxLength());
        validateSentence(request.sentence(), request.maxLength());
        validateCaseSensitive(request.caseSensitive());
        validateCharacter(request.character());

        boolean caseSensitive = "Evet".equalsIgnoreCase(request.caseSensitive());
        String sentence = request.sentence();
        String character = request.character();
        String originalCharacter = request.character();
        if (!caseSensitive) {
            sentence = sentence.toLowerCase(Locale.forLanguageTag("tr"));
            character = character.toLowerCase(Locale.forLanguageTag("tr"));
        }

        int count = 0;
        char target = character.charAt(0);
        for (char c : sentence.toCharArray()) {
            if (c == target) {
                count++;
            }
        }
        String message = "Girilen cümlede '" + originalCharacter + "' harfi toplamda " + count + " defa geçmektedir.";

        return new CharacterCountResponse(originalCharacter, count, message);
    }

    public void validateMaxLength(int maxLength) {
        if (maxLength <= 0) {
            throw new InvalidInputException("Lütfen geçerli bir değer giriniz. Maksimum uzunluk negatif olamaz.");
        }
    }

    public void validateSentence(String sentence, int maxLength) {
        if (sentence == null || sentence.isBlank()) {
            throw new InvalidInputException("Lütfen geçerli bir değer giriniz. Cümle boş olamaz.");
        }
        if (sentence.length() > maxLength) {
            throw new InvalidInputException("Lütfen geçerli bir değer giriniz. Cümle maksimum uzunluğu aşamaz.");
        }
    }

    public void validateCaseSensitive(String caseSensitive) {
        if (!"Evet".equalsIgnoreCase(caseSensitive) && !"Hayır".equalsIgnoreCase(caseSensitive)) {
            throw new InvalidInputException("Lütfen geçerli bir cevap giriniz.");
        }
    }

    public void validateCharacter(String character) {
        if (character == null || character.isBlank() || character.length() != 1) {
            throw new InvalidInputException("Geçerli bir karakter giriniz");
        }
    }

}
