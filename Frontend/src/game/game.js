import {inject} from "aurelia-framework";
import {HttpClient, json} from "aurelia-fetch-client";
import {Router} from 'aurelia-router';
import {UtilityInfo} from "../utility/utilityInfo";

import environment from '../environment';

@inject(UtilityInfo, Router)
export class Game {
  // this.gameCanvas: HTMLCanvasElement;
  constructor(utilityInfo, router) {
    this.utilityInfo = utilityInfo;
    this.router = router;

    this.players;
    this.authPlayer;  // here will be written authenticated player
    this.setPlayers();

    this.map;

    this.stepCounter = 0;

    this.timerId;
  }

  attached() {
    this.setMap();
  }

  //  get players from server and initialize them
  setPlayers() {
    let client = new HttpClient();
    let players = [];

    client.fetch("http://localhost:8080/lobby/check?lobbyId=" + this.utilityInfo.lobbyId)
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
      if(player.name === this.utilityInfo.playerName) {
        return player;
      }
    }
  }

  setMap() {
    let client = new HttpClient();

    client.fetch("http://localhost:8080/game/mapSettings?lobbyId=" + this.utilityInfo.lobbyId)
      .then(response => response.json())
      .then(data => {
        // console.log("set map response:");
        // console.log(data);

        this.map = new GameMap();

        this.map.height = data.height;
        this.map.width = data.width;

        // console.log("this.gameCanvas: ");
        // console.log(this.gameCanvas);

        this.gameCanvas.height = window.innerHeight;
        this.gameCanvas.width = window.innerWidth;

        // moved code here from attached() method, because methods were called before this method is ended
        this.map.cellSize = this.calculateCellSize();
        this.adaptMapSize();
        this.playfieldContainer.style.width = this.map.width + "px";
        this.map.context = this.gameCanvas.getContext('2d');
        this.setInitialMap();
        // this.timerId = setInterval(this.updateMap.bind(this), 100);
        // this.updateMap();  --> was moved into setInitalMap()
      });
  }

  calculateCellSize() {
    // console.log("canvas height: " + this.gameCanvas.height);
    // console.log("map height: " + this.map.height);
    let height = Math.floor(this.gameCanvas.height / this.map.height);
    let width = Math.floor(this.gameCanvas.width / this.map.width);
    return height < width ? height : width;
  }

  adaptMapSize() {
    // adapt size of map in GameMap object
    this.map.height = this.map.height * this.map.cellSize;
    this.map.width = this.map.width * this.map.cellSize;

    // adapt size of canvas according to map size in GameMap object
    this.gameCanvas.height = this.map.height;
    this.gameCanvas.width = this.map.width;
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

    client.fetch("http://localhost:8080/game/initialMap?lobbyId=" + this.utilityInfo.lobbyId)
      .then(response => response.json())
      .then(data => {
        // console.log("set initial map response:");
        // console.log(data);
        this.map.cells = [];
        for(let cell of data) {
          cell.x = cell.x * this.map.cellSize;
          cell.y = cell.y * this.map.cellSize;
          this.map.cells.push(cell);
        }
        this.drawInitialMap();
        this.updateMap();
      });
  }

  drawUpdatedCell(cell) {
    const context = this.map.context;
    const cellSize = this.map.cellSize;

    context.fillStyle = cell.color;
    context.fillRect(cell.x * cellSize, cell.y * cellSize, cellSize, cellSize);
  }

  updateMap() {
    let client = new HttpClient();
    let info = {
      'lobbyId': this.utilityInfo.lobbyId,
      'turnNr': this.stepCounter,
      'name': this.utilityInfo.playerName
    };
    let requestInfo = json(info);
    // console.log("update map request:");
    // console.log(requestInfo);

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
        // console.log("update map response:");
        // console.log(data);
        // console.log(this.timerId);
        if(data.status === "finished") {
          // clearInterval(this.timerId);
          // TODO: ask for winner info and depict it
        } else if (data.status === "received") {
          setTimeout(() => {}, 200);
          this.updateMap();
        } else {
          this.stepCounter = data.turnNr === -1 ? 0 : data.turnNr;
          for (let cell of data.update) {
            this.drawUpdatedCell(cell);
          }
          setTimeout(() => {}, 200);
          this.updateMap();
        }
      });
  }

  nextStep() {
    this.stepCounter += 1;
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
