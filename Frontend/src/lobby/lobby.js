export class Lobby {
  constructor() {
    this.players = [
      "Player A",
      "Player B",
      "Player C",
      "Player D"
    ];

    // this.nationAttributes =
    //   [["Vitality", 0], ["Growth rate", 0]],
    //   [["Strength", 0], ["Intelligence", 0]],
    //   [["Dexterity", 0], ["Luck", 0]]
    // ;

    this.nationAttributes = [
      [{name: "Vitality", points: 0}, {name: "Growth rate", points: 0}],
      [{name: "Strength", points: 0}, {name: "Intelligence", points: 0}],
      [{name: "Dexterity", points: 0}, {name: "Luck", points: 0}]
    ];

    this.nationPoints = 20;
  }

  changeNationAttributeValue(name, points) {
    for(let row of this.nationAttributes) {
      for(let attribute of row) {
        if(attribute.name === name)
        {
          attribute.points += points;
          console.log(attribute.points + " " + attribute.name);
        }
      }
    }
  }
}
