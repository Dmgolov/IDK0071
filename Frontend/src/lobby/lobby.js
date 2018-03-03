import {inject} from "aurelia-framework";
import {HttpClient, json} from "aurelia-fetch-client";
import {Router} from 'aurelia-router';
import {LobbyInfo} from "../lobby/lobbyInfo";

@inject(LobbyInfo, Router)
export class Lobby {
  constructor(lobbyInfo, router) {
    this.lobbyInfo = lobbyInfo;
    this.router = router;
    this.canDisplayNationOptions = this.lobbyInfo.gameMode !== "";

    console.log(this.lobbyInfo);

    // this.players = [
    //   new Player(this.lobbyInfo.playerName, false)
    // ];

    this.players;
    this.authPlayer;  // here will be written authenticated player
    this.setPlayers();

    this.nationPoints;  // will be asked from server
    this.setDefaultSettings();

    this.nationAttributes = [
      [new Attribute("Vitality", 0), new Attribute("Reproduction", 0)],
      [new Attribute("Strength", 0), new Attribute("Intelligence", 0)],
      [new Attribute("Dexterity", 0), new Attribute("Luck", 0)]
    ];

    this.timerId = setInterval(this.updatePlayers.bind(this), 5000);

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
    let info = {player: this.authPlayer, nationAttributes: {}, lobbyId: this.lobbyInfo.lobbyId};
    for(let row of this.nationAttributes) {
      for(let attribute of row) {
        info.nationAttributes[attribute.name] = attribute.points;
      }
    }
    return info;
  }

  sendReadyStateInfo() {
    let client = new HttpClient();

    this.authPlayer.isReady = !this.authPlayer.isReady;
    this.authPlayer.readyColor = this.authPlayer.isReady ? 'green' : '';

    let info = this.getReadyStateInfo();

    console.log(json(info));

    client.fetch("http://localhost:8080/lobby/ready", {
      "method": "POST",
      "body": json(info),
      headers: {
        'Origin': 'http://localhost:8080',
        'Content-Type': 'application/json'
      }

    })
      .then(response => response.json())
      .then(data => {
        console.log(json(data));
    });

  }

  updatePlayers() {
    let client = new HttpClient();

    client.fetch("http://localhost:8080/lobby/check?lobbyId=" + this.lobbyInfo.lobbyId)
      .then(response => response.json())
      .then(data => {
        console.log(data);
        for(let updatedPlayer of data) {
          let addNewPlayer = true;
          for(let player of this.players) {
            if (updatedPlayer.name === player.name) {
              player.isReady = updatedPlayer.isReady;
              player.readyColor = updatedPlayer.isReady ? 'green' : '';
              addNewPlayer = false;
            }
          }
          if(addThisPlayer) {
            let temporaryPlayer = new Player(updatedPlayer.name, updatedPlayer.isReady);
            temporaryPlayer.readyColor = updatedPlayer.isReady ? 'green' : '';
            this.players.push(temporaryPlayer);
          }
        }
    });
  }

  getAuthPlayer() {
    for(let player of this.players) {
      if(player.name === this.lobbyInfo.playerName) {
        return player;
      }
    }
  }

  setPlayers() {
    let client = new HttpClient();
    let players = [];

    client.fetch("http://localhost:8080/lobby/check?lobbyId=" + this.lobbyInfo.lobbyId)
      .then(response => response.json())
      .then(data => {
        for(let player of data) {
              let readyColor = player.isReady ? 'green' : '';
              let temporaryPlayer = new Player(player.name, player.isReady);
              temporaryPlayer.readyColor = readyColor;
              players.push(temporaryPlayer);
        }
        this.players = players;
        this.authPlayer = this.getAuthPlayer();
        console.log(json(this.players));
        console.log(json(this.authPlayer));
    });

  }

  setDefaultSettings() {
    let client = new HttpClient();

    client.fetch("http://localhost:8080/lobby/defaultSettings")
      .then(response => response.json())
      .then(data => {
        this.nationPoints = data.nationPoints;
        console.log(this.nationPoints);
    });
  }

  sendGameMode(mode) {
    this.lobbyInfo.gameMode = mode;
    this.canDisplayNationOptions = this.lobbyInfo.gameMode !== "";
  }

}

class Player {
  constructor(name, isReady) {
    this.name = name;
    this.isReady = isReady;
    this.readyColor = "";
  }
}

class Attribute {
  constructor(name, points) {
    this.name = name;
    this.points = points;
  }
}
