import {HttpClient, json} from "aurelia-fetch-client";

export class Lobby {
  constructor() {
    this.players = [
      new Player("Player_A", false),
      new Player("Player_B", false),
      new Player("Player_C", false),
      new Player("Player_D", false)
    ];

    this.authPlayer = this.players[2];  // here will be written authenticated player

    this.nationPoints = 20;  // will be asked from server

    this.nationAttributes = [
      [new Attribute("Vitality", 0), new Attribute("Reproduction", 0)],
      [new Attribute("Strength", 0), new Attribute("Intelligence", 0)],
      [new Attribute("Dexterity", 0), new Attribute("Luck", 0)]
    ];

    this.timerId = setInterval(this.updatePlayersStateInfo, 1000);

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
    let info = {player: this.authPlayer, nationAttributes: {}};
    for(let row of this.nationAttributes) {
      for(let attribute of row) {
        info.nationAttributes[attribute.name] = attribute.points;
      }
    }
    return info;
  }

  sendReadyStateInfo() {
    let client = new HttpClient();
    let info = this.getReadyStateInfo();

    this.authPlayer.isReady = !this.authPlayer.isReady;
    this.authPlayer.readyColor = this.authPlayer.isReady ? 'green' : '';

    client.fetch("http://localhost:8080/lobby/ready", {
      "method": "POST",
      "body": json(info)
    })
      .then(response => response.json())
      .then(data => {
        console.log(data);
    });

    console.log(json(this.players));
    console.log(json(info));
  }

  updatePlayersStateInfo() {
    let client = new HttpClient();

    client.fetch("http://localhost:8080/lobby/check?lobbyId=1")
      .then(response => response.json())
      .then(data => {
        console.log(data);
        let updatedPlayers = JSON.parse(data);
        for(let updatedPlayer of updatedPlayers) {
          for (let player of this.players) {
            if (updatedPlayer.name === player.name) {
              player.isReady = updatedPlayer.isReady;
              player.readyColor = player.isReady ? 'green' : '';
            }
          }
        }
    });
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
