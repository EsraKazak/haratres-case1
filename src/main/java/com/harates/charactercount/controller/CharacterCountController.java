package com.harates.charactercount.controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.harates.charactercount.dto.CharacterCountRequest;
import com.harates.charactercount.dto.CharacterCountResponse;
import com.harates.charactercount.service.CharacterCountService;

@RestController 
@RequestMapping("/api/v1/character-count")
public class CharacterCountController {
    private final CharacterCountService characterCountService;

    public CharacterCountController(CharacterCountService characterCountService) {
        this.characterCountService = characterCountService;
    }

    @PostMapping 
    public CharacterCountResponse countCharacter ( @RequestBody  CharacterCountRequest request){
        return characterCountService.countResponse(request);
    }
    
}
