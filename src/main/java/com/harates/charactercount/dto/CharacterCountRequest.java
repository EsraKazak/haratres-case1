package com.harates.charactercount.dto;

public record CharacterCountRequest(int maxLength,String sentence, String caseSensitive,String character)   {
    
}
