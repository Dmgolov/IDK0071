package com.ttu.tarkvaratehnika.empires.gameofempires.processor;

import com.ttu.tarkvaratehnika.empires.gameofempires.game.Game;
import com.ttu.tarkvaratehnika.empires.gameofempires.gamesession.GameLobby;
import com.ttu.tarkvaratehnika.empires.gameofempires.repository.GameRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class SessionServiceTest {

    private GameRepository gameRepository;
    private SessionService sessionService;
    private GameLobby lobby;

    @Before
    public void setUp() {
        gameRepository = Mockito.mock(GameRepository.class);
        lobby = Mockito.mock(GameLobby.class);
        Mockito.doReturn(LocalDateTime.now()).when(lobby).getStartTime();
        sessionService = new SessionService(gameRepository);
    }

    @Test
    public void testAddLobbyAddsLobbyToAllLobbiesList() {
        sessionService.addLobby(lobby);
        assertTrue(sessionService.getLobbies().contains(lobby));
    }

    @Test
    public void testTerminateLobbyRemovesLobbyFromList() {
        sessionService.addLobby(lobby);
        sessionService.terminateLobby(lobby, null, null);
        assertFalse(sessionService.getLobbies().contains(lobby));
    }

    @Test
    public void testSaveGameToDatabaseCreatesNewEntryToDatabase() {
        Mockito.doReturn(1L).when(lobby).getLobbyId();
        Mockito.doReturn(null).when(lobby).getStartTime();
        Mockito.doReturn(0).when(lobby).getNumOfTurns();
        Mockito.doReturn(LocalDateTime.now()).when(lobby).getStartTime();
        sessionService.saveGameToDatabase(lobby, null, null);
        Mockito.verify(gameRepository, Mockito.times(1)).save(Mockito.any(Game.class));
    }
}