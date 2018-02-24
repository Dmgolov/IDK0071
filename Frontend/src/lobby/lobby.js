import {HttpClient, json} from "aurelia-fetch-client";

export class Lobby {
  constructor() {
    this.players = [
      "Player A",
      "Player B",
      "Player C",
      "Player D"
    ];

    this.authPlayer = 'Player C';  // here will be written authenticated player

    this.nationPoints = 20;

    this.nationAttributes = [
      [{name: "Vitality", points: 0}, {name: "Reproduction", points: 0}],
      [{name: "Strength", points: 0}, {name: "Intelligence", points: 0}],
      [{name: "Dexterity", points: 0}, {name: "Luck", points: 0}]
    ];

    // this.nationAttributes =
    //   [["Vitality", 0], ["Growth rate", 0]],
    //   [["Strength", 0], ["Intelligence", 0]],
    //   [["Dexterity", 0], ["Luck", 0]]
    // ;
  }

  changeNationAttributeValue(name, points) {
    for(let row of this.nationAttributes) {
      for(let attribute of row) {
        if(attribute.name === name &&
          attribute.points + points >= 0 &&
          this.nationPoints - points >= 0) {
            attribute.points += points;
            this.nationPoints -= points;
        }
      }
    }
  }

  sendReadyStateInfo(player) {
    let client = new HttpClient();

    let info = {player: this.authPlayer, nationAttributes: new Array()};
    for(let row of this.nationAttributes) {
      for(let attribute of row) {
        info.nationAttributes.push(attribute);
      }
    }
    console.log(json(info));
  }
}
