import {HttpClient, json} from "aurelia-fetch-client";

export class Game {
  gameMap: HTMLCanvasElement;
  constructor(){
    this.players = [
      new Player("Player 1", 0),
      new Player("Player 2", 0),
      new Player("Player 3", 0),
      new Player("Player 4", 0),
    ];
    this.stortedPayers = [];
    this.fillCell = 0;
    this.map = {
      height: 100,
      width: 100,
      cellSize: 25,
      context: null
    };
    this.stepCounter = 0;
    this.cells = this.createNations(this.map.width, this.map.height, this.map.cellSize);
  }

  attached() {
    for (let i = 0; i < this.players.length; i++) {
      console.log(this.players[i].playerScore);
    }
    this.map.context = this.gameMap.getContext('2d');
    this.fillMap(this.cells, this.map.cellSize);
    setInterval(function () {
      console.log("OP");
    }, 1000);
  }

  createGrid() {
    const context = this.map.context;

    for (var x = 0.5; x < mapSizeX; x += cellSize) {
      for (var y = 0.5; y < mapSizeY; y += cellSize) {
      context.moveTo(x, 0);
      context.lineTo(x, mapSizeY - 1);
      context.moveTo(0, y);
      context.lineTo(mapSizeX - 1, y);
    }
      context.stroke();
    }
  }

  fillMap(cells, cellSize) {
    const context = this.map.context;
    for (let row of cells){
      for (let cell of row){
        if (cell.nation == "red"){
          context.fillStyle = cell.nationColor;
          context.fillRect(cell.x, cell.y, cellSize, cellSize);
        } else if (cell.nation == "green") {
          context.fillStyle = cell.nationColor;
          context.fillRect(cell.x, cell.y, cellSize, cellSize);
        }
      }
    }
  }

  createNations(mapWidth, mapHeight, cellSize) {
    let cells = [];
    let nation = "";
    let nationColor = "";
    for (let i = 0; i < mapWidth - 1; i += cellSize) {
      let row = new Array();
      for (let j = 0; j < mapHeight - 1; j += cellSize) {
        if (Math.random() < 0.5){
          row.push({x: i, y: j, nation: "green", nationColor: "#0F0"});
        } else {
          row.push({x: i, y: j, nation: "red", nationColor: "#F00"});
        }
      }
      cells.push(row);
    }
    console.log(cells[1][0].x);
    console.log(cells);
    return cells;
  }

  clearMap(cells, cellSize) {
    const context = this.map.context;
    for (let row of cells){
      for (let cell of row){
        context.clearRect(cell.x, cell.y, cellSize, cellSize);
      }
    }
  }

  updateMap(cells, cellSize){
    const context = this.map.context;
    for (let row of cells) {
      for (let cell of row) {
        if (cell.nation == "red"){
          context.fillStyle = "#0F0";
          context.fillRect(cell.x, cell.y, cellSize, cellSize);
        } else if (cell.nation == "green") {
          context.fillStyle = "#F00";
          context.fillRect(cell.x, cell.y, cellSize, cellSize);
        }
      }
    }
  }

  movePersons(){

  }

  sortTableByScore() {
    //for (let i = 0; i < this.players.size)
  }

  nextStep() {
    this.stepCounter += 1;
    this.map.context = this.gameMap.getContext('2d');
    this.updateMap(this.cells, this.map.cellSize);
    this.movePersons();
  }

  sendRequestForNextStep(){
    // here should send every 1 second requset to the server to update map
  }

  receviedMapData(){
    // here sould be map update from server by json
  }

  endGame(){
    // determinate game winner
  }



}

class Player {
  constructor(playerName, playerScore) {
    this.playerName = playerName;
    this.playerScore = Math.random() * 100;
  }

  getPlayerScore() {
    this.playerScore = Math.random() * 20;
  }

}
