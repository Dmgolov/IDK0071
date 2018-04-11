package com.ttu.tarkvaratehnika.empires.gameofempires.gamesession;

import com.ttu.tarkvaratehnika.empires.gameofempires.gamefield.GameField;
import com.ttu.tarkvaratehnika.empires.gameofempires.nation.Nation;
import com.ttu.tarkvaratehnika.empires.gameofempires.person.PersonValues;
import com.ttu.tarkvaratehnika.empires.gameofempires.processor.SessionService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;

public class GameLobbyTest {

    private GameLobby lobby;
    private GameLobby lobbyMock;
    private SessionService sessionService;
    private GameField field;

    @Before
    public void setUp() {
        sessionService = Mockito.mock(SessionService.class);
        field = Mockito.mock(GameField.class);
        lobby = new GameLobby(sessionService);
        lobbyMock = Mockito.mock(GameLobby.class);
        Mockito.doCallRealMethod().when(lobbyMock).setGameField(Mockito.any());
        Mockito.doCallRealMethod().when(lobbyMock).setSessionService(Mockito.any());
        lobbyMock.setGameField(field);
        lobbyMock.setSessionService(sessionService);
    }

    @Test
    public void testDifferentLobbiesHaveDifferentIDs() {
        GameLobby another = new GameLobby(sessionService);
        assertNotEquals(another.getLobbyId(), lobby.getLobbyId());
    }

    @Test
    public void testEnterLobbyAddsNewNationIfNotStarted() {
        lobby.enterSession("test");
        assertEquals(1, lobby.getNations().size());
    }

    @Test
    public void testEnterLobbyDoesNotAddIfFull() {
        for (int i = 0; i < SessionSettings.DEFAULT_MAX_USERS; i++) {
            lobby.enterSession("test" + i);
        }
        assertEquals(SessionSettings.DEFAULT_MAX_USERS, lobby.getNations().size());
        lobby.enterSession("test");
        assertEquals(SessionSettings.DEFAULT_MAX_USERS, lobby.getNations().size());
    }

    @Test
    public void testEnterLobbyDoesNotAddIfGameStarted() {
        lobby.setHasStarted(true);
        lobby.enterSession("test");
        assertEquals(0, lobby.getNations().size());
    }

    @Test
    public void testLeaveSessionRemovesFromLobby() {
        lobby.enterSession("test");
        lobby.leaveSession("test");
        assertEquals(0, lobby.getNations().size());
    }

    @Test
    public void testLeaveSessionTerminatesLobbyIfEmpty() {
        lobby.enterSession("test");
        lobby.leaveSession("test");
        Mockito.verify(sessionService, Mockito.times(1)).terminateLobby(lobby, null, null);
    }

    @Test
    public void testLeaveLobbyDoesNothingIfNotInLobby() {
        lobby.enterSession("test");
        lobby.leaveSession("another");
        assertEquals(1, lobby.getNations().size());
    }

    @Test
    public void testChangeToSinglePlayerAddsBots() {
        lobby.enterSession("test");
        lobby.changeToSinglePlayer();
        assertEquals(4, lobby.getNations().size());
    }

    @Test
    public void testEndTurnUpdatesMapIfAllNationsFinishedTheirTurn() {
        Mockito.doReturn(true).when(lobbyMock).allNationsWaiting();
        Mockito.doCallRealMethod().when(lobbyMock).endTurn();
        lobbyMock.endTurn();
        Mockito.verify(field, Mockito.times(1)).updateMap();
    }

    @Test
    public void testStartNewTurnTerminatesLobbyIfHasWinner() {
        Mockito.doReturn(Optional.of(Mockito.mock(Nation.class))).when(lobbyMock).checkWinner();
        Mockito.doReturn(true).when(lobbyMock).allNationsWaiting();
        Mockito.doCallRealMethod().when(lobbyMock).startNewTurn();
        Mockito.doCallRealMethod().when(lobbyMock).setNations(Mockito.anySet());
        lobbyMock.setNations(Mockito.mock(Set.class));
        lobbyMock.startNewTurn();
        Mockito.verify(sessionService, Mockito.times(1))
                .terminateLobby(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    public void testCheckPlayerStateShowsIfPLayerIsReady() {
        lobby.enterSession("test");
        List<Map<String, Object>> response = new ArrayList<>();
        Map<String, Object> playerEntry = new HashMap<>();
        playerEntry.put("isReady", false);
        playerEntry.put("name", "test");
        response.add(playerEntry);
        assertEquals(response, lobby.checkPlayerState());
    }

    @Test
    public void testGetProperColorsSendsProperPlayerColors() {
        Optional<Nation> nation = lobby.enterSession("test");
        List<Map<String, String>> response = new ArrayList<>();
        Map<String, String> playerEntry = new HashMap<>();
        if (nation.isPresent()) {
            playerEntry.put("color", nation.get().getTeamColor());
            playerEntry.put("name", "test");
            response.add(playerEntry);
            assertEquals(response, lobby.getPlayerColors());
        } else {
            fail("Nation is missing");
        }
    }

    @Test
    public void testStartSessionInitiatesMapLoad() {
        try {
            lobby.setGameField(field);
            lobby.setStartDelay(0);
            lobby.startSession();
            Mockito.verify(field, Mockito.times(1)).loadField();
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testReadyCheckStartsGameIfEveryoneReady() {
        Mockito.doCallRealMethod().when(lobbyMock).setNations(Mockito.anySet());
        try {
            Mockito.doCallRealMethod().when(lobbyMock).readyCheck(Mockito.anyString(),
                    Mockito.anyBoolean(), Mockito.eq(PersonValues.DEFAULT_STATS), Mockito.anyString());
            Set<Nation> nations = new HashSet<>();
            nations.add(new Nation("test", "#ffffff", lobbyMock));
            lobbyMock.setNations(nations);
            lobbyMock.readyCheck("test", true, PersonValues.DEFAULT_STATS, "map");
            Mockito.verify(lobbyMock, Mockito.times(1)).startSession();
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}