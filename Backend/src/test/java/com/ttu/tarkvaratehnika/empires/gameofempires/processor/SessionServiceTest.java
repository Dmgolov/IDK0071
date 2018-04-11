package com.ttu.tarkvaratehnika.empires.gameofempires.processor;

import com.ttu.tarkvaratehnika.empires.gameofempires.game.Game;
import com.ttu.tarkvaratehnika.empires.gameofempires.gamesession.GameLobby;
import com.ttu.tarkvaratehnika.empires.gameofempires.repository.GameRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class SessionServiceTest {

    private GameRepository gameRepository;
    private SessionService sessionService;
    private GameLobby lobby;

    @Before
    public void setUp() {
        gameRepository = Mockito.mock(GameRepository.class);
        lobby = Mockito.mock(GameLobby.class);
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
    public void testTerminateLobbyAddsEntryToGameResults() {
        Mockito.doReturn(1L).when(lobby).getLobbyId();
        sessionService.terminateLobby(lobby, null, null);
        assertEquals("{\"name\":\"null\", \"color\":\"null\"}", sessionService.getResultForGame(1));
    }

    @Test
    public void testSaveGameToDatabaseCreatesNewEntryToDatabase() {
        Mockito.doReturn(1L).when(lobby).getLobbyId();
        Mockito.doReturn(null).when(lobby).getStartTime();
        Mockito.doReturn(0).when(lobby).getNumOfTurns();
        sessionService.saveGameToDatabase(lobby, null);
        Mockito.verify(gameRepository, Mockito.times(1)).save(Mockito.any(Game.class));
    }
}