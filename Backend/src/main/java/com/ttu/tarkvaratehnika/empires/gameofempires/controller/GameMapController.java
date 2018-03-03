package com.ttu.tarkvaratehnika.empires.gameofempires.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/game")
public class GameMapController {
    // Here will be send updated map and game state (win/lose)
    // Map is array with information about cells.
    // Send information about game time.
}
