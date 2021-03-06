import {inject} from 'aurelia-framework';
import {Router} from 'aurelia-router';
import {UtilityInfo} from "../utility/utilityInfo";
import {AuthService} from 'aurelia-authentication';
import {Endpoint} from 'aurelia-api';
import {json} from 'aurelia-fetch-client';


@inject(UtilityInfo, Router, AuthService, Endpoint.of('lobby'), Endpoint.of('map'))
export class Lobby {

  constructor(utilityInfo, router, authService, lobbyEndpoint, mapEndpoint) {
    this.utilityInfo = utilityInfo;
    this.utilityInfo.requestUsernameUpdate();
    this.router = router;
    this.authService = authService;
    this.lobbyEndpoint = lobbyEndpoint;
    this.mapEndpoint = mapEndpoint;

    this.canDisplayGameModeOptions = this.utilityInfo.gameMode === '';
    this.canDisplayHostOptions = this.utilityInfo.gameMode !== '' &&
                                  this.utilityInfo.lobbyMode === 'create';
    this.canDisplayNationOptions = this.utilityInfo.gameMode !== '' &&
                                  this.utilityInfo.lobbyMode === 'connect';

    this.maps = [];
    this.mapName = 'gameMap5.png';  // name of image, which is choosen by user
    this.iterationsNumber = "";
    this.nationPoints = "";
    this.maxPlayersNumber = "";

    this.players = [];
    this.authPlayer;

    this.nationAttributes = [
      [new Attribute("Vitality", 0)],
      [new Attribute("Reproduction", 0)],
      [new Attribute("Strength", 0)],
      [new Attribute("Intelligence", 0)],
      [new Attribute("Dexterity", 0)],
      [new Attribute("Luck", 0)]
    ];

    this.gameStartMessage;
    this.playersAreReady = false;

    this.timerId;

    this.removePlayerFromLobby = function (event) {
      console.log("removePlayerFromLobby() called");
      clearInterval(this.timerId);
      if (!this.playersAreReady) {
        this.exitLobby();
        this.utilityInfo.resetLobbyInfo();
      }
    }

  }

  attached() {
    this.setPlayers();
    if (this.canDisplayNationOptions) {
      this.getCustomSettings();
    }
    this.setGameStartMessage(false);
    this.setMapsArray();
    this.timerId = setInterval(this.update.bind(this), 1000);

    window.addEventListener("beforeunload", this.removePlayerFromLobby.bind(this));
  }

  changeNationAttributeValue(name, points) {
    for(let row of this.nationAttributes) {
      for(let attribute of row) {
        if(attribute.name === name && attribute.points + points >= 0 && this.nationPoints - points >= 0) {
          attribute.points += points;
          this.nationPoints -= points;
        }
      }
    }
  }

  getReadyStateInfo() {
    let info = {player: this.authPlayer, nationAttributes: {}, lobbyId: this.utilityInfo.lobbyId, mapName: this.mapName};
    console.log(info);
    for(let row of this.nationAttributes) {
      for(let attribute of row) {
        info.nationAttributes[attribute.name] = attribute.points;
      }
    }
    return info;
  }

  sendReadyStateInfo() {
    if (this.authService.isAuthenticated()) {
      this.authPlayer.isReady = !this.authPlayer.isReady;
      let info = this.getReadyStateInfo();

      this.lobbyEndpoint.post('ready', info)
      .then()
      .catch(console.error);
    }
  }

  updatePlayers() {
    if (this.authService.isAuthenticated()) {
      this.lobbyEndpoint.post('check', {
        "lobbyId": this.utilityInfo.lobbyId
      })
      .then(data => {
        this.players = [];
        this.playersAreReady = true;
        for(let updatedPlayer of data) {
          let newPlayer = new Player(updatedPlayer.name, updatedPlayer.isReady);
          this.players.push(newPlayer);
          if (newPlayer.isReady === false) {
            this.playersAreReady = false;
          }
        }
      })
      .catch(console.error);
    }
  }

  startGame() {
    if(this.playersAreReady) {
      this.setGameStartMessage(true);
      clearInterval(this.timerId);
      this.router.navigate("game");
    }
  }

  update() {
    this.updatePlayers();
    this.startGame();
  }

  setPlayers() {
    if (this.authService.isAuthenticated()) {
      this.lobbyEndpoint.post('check', {
        "lobbyId": this.utilityInfo.lobbyId
      })
      .then(data => {
        for(let playerData of data) {
          let player = new Player(playerData.name, playerData.isReady);
          if(player.name === this.utilityInfo.username) {
            this.authPlayer = player;
          }
          this.players.push(player);
        }
      })
      .catch(console.error);
    }
  }

  getDefaultSettings() {
    if (this.authService.isAuthenticated()) {
      this.lobbyEndpoint.find('defaultSettings')
      .then(data => {
        // this.mapName = data.mapName;
        this.iterationsNumber = data.iterationsNumber;
        this.nationPoints = data.nationPoints;
        this.maxPlayersNumber = data.maxPlayersNumber;
      })
      .catch(console.error);
    }
  }

  sendGameMode(mode) {
    if (this.authService.isAuthenticated()) {
      this.utilityInfo.gameMode = mode;
      this.canDisplayHostOptions = this.utilityInfo.gameMode !== '';
      this.canDisplayGameModeOptions = this.utilityInfo.gameMode.length === 0;

      this.lobbyEndpoint.post('mode', {
        "mode": mode,
        "lobbyId": this.utilityInfo.lobbyId
      })
      .then()
      .catch(console.error);
    }
  }

  setGameStartMessage(ready) {
    if(ready) {
      this.gameStartMessage = "Game is starting";
    } else {
      this.gameStartMessage = "Waiting players";
    }
  }

  setMapsArray() {
    if (this.authService.isAuthenticated()) {
      this.mapEndpoint.post('browse', {
        "startIndex" : 0
      })
      .then(data => {
        for (let mapName of data) {
          this.maps.push(mapName);
        }
      })
      .catch(console.error);
    }
  }

  selectMap(){
    let selectedMapIndex = this.mapSelector.selectedIndex;
    this.mapName = this.mapSelector.options[selectedMapIndex].text;
  }

  sendHostOptions() {
    if (this.authService.isAuthenticated()) {
      this.canDisplayHostOptions = false;
      this.canDisplayNationOptions = true;

      this.lobbyEndpoint.post('setCustomSettings', {
        "lobbyId": this.utilityInfo.lobbyId,
        "mapName": this.mapName,
        "iterationsNumber": this.iterationsNumber,
        "nationPoints": this.nationPoints,
        "maxPlayersNumber": this.maxPlayersNumber
      })
      .then(data => this.getCustomSettings())
      .catch(console.error);
    }
  }

  getCustomSettings() {
    if (this.authService.isAuthenticated()) {
      this.lobbyEndpoint.post('getCustomSettings', {
        "lobbyId": this.utilityInfo.lobbyId
      })
      .then(data => {
        this.mapName = data.mapName;
        this.iterationsNumber = data.iterationsNumber;
        this.nationPoints = data.nationPoints;
        this.maxPlayersNumber = data.maxPlayersNumber;
      })
      .catch(console.error);
    }
  }

  exitLobby() {
    if (this.authService.isAuthenticated()) {
      this.lobbyEndpoint.post('leave', {
        "username": this.utilityInfo.username,
        "lobbyId": this.utilityInfo.lobbyId
      })
      .then(data => {
      })
      .catch(console.error);
    }
  }

  goBack() {
    this.router.navigate("home");
  }

  detached() {
    clearInterval(this.timerId);
    if (!this.playersAreReady) {
      this.exitLobby();
      this.utilityInfo.resetLobbyInfo();
    }
    window.removeEventListener("beforeunload", this.removePlayerFromLobby.bind(this));
  }

}

class Player {
  constructor(name, isReady) {
    this.name = name;
    this.isReady = isReady;
  }
}

class Attribute {
  constructor(name, points) {
    this.name = name;
    this.points = points;
  }
}
