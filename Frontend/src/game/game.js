import {inject} from "aurelia-framework";
import {HttpClient, json} from "aurelia-fetch-client";
import {Router} from 'aurelia-router';
import {LobbyInfo} from "../lobby/lobbyInfo";

@inject(LobbyInfo, Router)
export class Game {
  gameCanvas: HTMLCanvasElement;
  constructor(lobbyInfo, router) {
    this.lobbyInfo = lobbyInfo;
    this.router = router;

    this.players;
    this.authPlayer;  // here will be written authenticated player
    this.setPlayers();

    this.map = new GameMap();
    this.setMap();

    this.stepCounter = 0;

    this.timerId;
  }

  attached() {
    this.map.cellSize = this.calculateCellSize();
    console.log(this.map.cellSize);
    this.adaptMapSize();
    console.log(this.map);
    this.map.context = this.gameCanvas.getContext('2d');
    this.setInitialMap();

    this.timerId = setInterval(this.updateMap.bind(this), 100);
  }

  //  get players from server and initialize them
  setPlayers() {
    let client = new HttpClient();
    let players = [];

    client.fetch("http://localhost:8080/lobby/check?lobbyId=" + this.lobbyInfo.lobbyId)
      .then(response => response.json())
      .then(data => {
        for(let player of data) {
              players.push(new Player(player.name));
        }
        this.players = players;
        this.authPlayer = this.getAuthPlayer();
    });
  }

  //  get authenticated player from players array
  getAuthPlayer() {
    for(let player of this.players) {
      if(player.name === this.lobbyInfo.playerName) {
        return player;
      }
    }
  }

  setMap() {
    let client = new HttpClient();

    client.fetch("http://localhost:8080/game/mapSettings?lobbyId=" + this.lobbyInfo.lobbyId)
      .then(response => response.json())
      .then(data => {
        this.map.height = data.height;
        this.map.width = data.width;
        this.map.cellSize = 1;
      });
  }

  calculateCellSize() {
    console.log("height:" + this.gameCanvas.height / this.map.height);
    console.log("height:" + this.gameCanvas.width / this.map.width);
    let height = Math.floor(this.gameCanvas.height / this.map.height);
    let width = Math.floor(this.gameCanvas.width / this.map.width);
    return height < width ? height : width;
  }

  adaptMapSize() {
    this.map.height = this.map.height * this.map.cellSize;
    this.map.width = this.map.width * this.map.cellSize;
  }

  drawInitialMap() {
    const context = this.map.context;
    const cellSize = this.map.cellSize;
    for (let cell of this.map.cells){
      context.fillStyle = cell.color;
      context.fillRect(cell.x, cell.y, cellSize, cellSize);
    }
  }

  setInitialMap() {
    let client = new HttpClient();

    client.fetch("http://localhost:8080/game/initialMap?lobbyId=" + this.lobbyInfo.lobbyId)
      .then(response => response.json())
      .then(data => {
        this.map.cells = [];
        for(let cell of data) {
          cell.x = cell.x * this.map.cellSize;
          cell.y = cell.y * this.map.cellSize;
          this.map.cells.push(cell);
        }
        this.drawInitialMap();
      });
  }

  drawUpdatedCell(cell) {
    const context = this.map.context;
    const cellSize = this.map.cellSize;

    context.fillStyle = cell.color;
    context.fillRect(cell.x * cellSize, cell.y * cellSize, cellSize, cellSize);
  }

  updateMap(timerId) {
    let client = new HttpClient();
    let requestInfo = json({'lobbyId': this.lobbyInfo.lobbyId, 'turnNr': this.stepCounter});
    console.log(requestInfo);

    client.fetch("http://localhost:8080/game/state", {
      "method": "POST",
      "body": requestInfo,
      headers: {
        'Origin': 'http://localhost:8080',
        'Content-Type': 'application/json'
      }
    })
      .then(response => response.json())
      .then(data => {
        this.stepCounter = data.turnNr === -1 ? 0 : data.turnNr;
        for (let cell of data.update) {
          this.drawUpdatedCell(cell);
        }
        if(data.status === "finished") {
          clearInterval(this.timerId);
          // TODO: ask for winner info and depict it
        }
      });
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


  movePersons(){
    //method for moving persons.
  }

  sortTableByScore() {
    //for (let i = 0; i < this.players.size)
  }

  nextStep() {
    this.stepCounter += 1;
    this.map.cells = this.createNations(this.map.width, this.map.height, this.map.cellSize);
    this.updateMap(this.map.cells, this.map.cellSize);
    this.movePersons();
  }

  sendRequestForNextStep() {
    // here should send every 1 second requset to the server to update map
  }

  receviedMapData() {
    // here sould be map update from server by json
  }

  endGame() {
    // determinate game winner
  }
}

class Player {
  constructor(name) {
    this.name = name;
    this.playerScore = Math.floor(Math.random() * 100);
  }
}

class GameMap {
  constructor() {
    this.height = 0;
    this.width = 0;
    this.cellSize = 0;
    this.context = null;
    this.cells = null;
  }
}

class Cell {
  constructor(x, y, color) {
    this.x = x;
    this.y = y;
    this.color = color;
  }
}
