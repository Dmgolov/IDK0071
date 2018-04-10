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

    this.canDisplayNationOptions = this.utilityInfo.gameMode !== '';

    this.players = [];
    this.authPlayer;

    this.nationPoints;

    this.maps = [];
    this.mapName = 'gameMap5.png';  // name of image, which is choosen by user

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
  }

  attached() {
    this.setPlayers();
    this.setDefaultSettings();
    this.setGameStartMessage(false);
    this.setMapsArray();
    this.timerId = setInterval(this.update.bind(this), 1000);
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
    this.authPlayer.isReady = !this.authPlayer.isReady;
    let info = this.getReadyStateInfo();

    if (this.authService.isAuthenticated()) {
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
        this.playersAreReady = true;
        for(let updatedPlayer of data) {
          let addNewPlayer = true;
          for(let player of this.players) {
            if (updatedPlayer.name === player.name) {
              player.isReady = updatedPlayer.isReady;
              addNewPlayer = false;
            }
          }
          if(addNewPlayer) {
            let newPlayer = new Player(updatedPlayer.name, updatedPlayer.isReady);
            this.players.push(newPlayer);
          }
          if(updatedPlayer.isReady === false) {
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

  setDefaultSettings() {
    if (this.authService.isAuthenticated()) {
      this.lobbyEndpoint.find('defaultSettings')
      .then(data => {
        this.nationPoints = data.nationPoints;
      })
      .catch(console.error);
    }
  }

  sendGameMode(mode) {
    this.utilityInfo.gameMode = mode;
    this.canDisplayNationOptions = this.utilityInfo.gameMode !== "";

    if (this.authService.isAuthenticated()) {
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
