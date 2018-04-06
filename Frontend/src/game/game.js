import {inject} from 'aurelia-framework';
import {Router} from 'aurelia-router';
import {UtilityInfo} from "../utility/utilityInfo";
import {AuthService} from 'aurelia-authentication';
import {Endpoint} from 'aurelia-api';

<<<<<<< HEAD
import environment from '../environment';


@inject(LobbyInfo, Router)
=======
@inject(UtilityInfo, Router, AuthService, Endpoint.of('game'))
>>>>>>> master
export class Game {
  // this.gameCanvas: HTMLCanvasElement;
  constructor(utilityInfo, router, authService, gameEndpoint) {
    this.utilityInfo = utilityInfo;
    this.utilityInfo.requestUsernameUpdate();
    this.router = router;
    this.authService = authService;
    this.gameEndpoint = gameEndpoint;

    this.players = [];
    this.authPlayer;
    this.winner;

    this.map;

    this.stepCounter = 0;

    this.timerId;

    this.isGameFinished = false;
  }

  attached() {
    this.setPlayers();
    this.setMap();
  }

  //  get players from server and initialize them
  setPlayers() {
<<<<<<< HEAD
    let client = new HttpClient();
    let players = [];

    client.fetch(environment.apiBaseUrl + "/lobby/check?lobbyId=" + this.lobbyInfo.lobbyId)
      .then(response => response.json())
=======
    if (this.authService.isAuthenticated()) {
      this.gameEndpoint.post('players', {
        "lobbyId": this.utilityInfo.lobbyId
      })
>>>>>>> master
      .then(data => {
        for(let playerData of data) {
          let player = new Player(playerData.name, playerData.color);
          if(player.name === this.utilityInfo.username) {
            this.authPlayer = player;
          }
          this.players.push(player);
        }
        console.log(this.players);
      })
      .catch(console.error);
    }
  }

  setMap() {
    if (this.authService.isAuthenticated()) {
      this.gameEndpoint.post('mapSettings', {
        "lobbyId": this.utilityInfo.lobbyId
      })
      .then(data => {
        this.map = new GameMap();

        this.map.height = data.height;
        this.map.width = data.width;

        this.gameCanvas.height = window.innerHeight;
        this.gameCanvas.width = window.innerWidth;

        this.map.cellSize = this.calculateCellSize();
        this.adaptMapSize();
        this.playfieldContainer.style.height = this.map.height + 'px';
        this.playfieldContainer.style.width = this.map.width + "px";
        this.map.context = this.gameCanvas.getContext('2d');
        this.setInitialMap();
      })
      .catch(console.error);
    }
  }

  calculateCellSize() {
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

  setInitialMap() {
    if (this.authService.isAuthenticated()) {
      this.gameEndpoint.post('initialMap', {
        "lobbyId": this.utilityInfo.lobbyId
      })
      .then(data => {
        for(let cell of data) {
          cell.x = cell.x * this.map.cellSize;
          cell.y = cell.y * this.map.cellSize;
          this.map.cells.push(cell);
        }
        this.drawInitialMap();
        this.updateMap();
      })
      .catch(console.error);
    }
  }

  drawInitialMap() {
    const context = this.map.context;
    const cellSize = this.map.cellSize;
    for (let cell of this.map.cells){
      context.fillStyle = cell.color;
      context.fillRect(cell.x, cell.y, cellSize, cellSize);
    }
  }

  updateMap() {
    if (this.authService.isAuthenticated()) {
      this.gameEndpoint.post('state', {
        'lobbyId': this.utilityInfo.lobbyId,
        'turnNr': this.stepCounter,
        'name': this.utilityInfo.username
      })
      .then(data => {
        if(data.status === "finished") {
          this.requestWinnerData();
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
      })
      .catch(console.error);
    }
  }

  drawUpdatedCell(cell) {
    const context = this.map.context;
    const cellSize = this.map.cellSize;
    context.fillStyle = cell.color;
    context.fillRect(cell.x * cellSize, cell.y * cellSize, cellSize, cellSize);
  }

  requestWinnerData() {
    if (this.authService.isAuthenticated()) {
      this.gameEndpoint.post('winner', {
        'lobbyId': this.utilityInfo.lobbyId,
      })
      .then(data => {
        this.isGameFinished = true;
        this.winner = new Player(data.name, data.color);
        this.gameCanvas.style.zIndex = '1';
        this.winnerContainer.style.zIndex = '3';
      })
      .catch(console.error);
    }
  }

}

class Player {
  constructor(name, color) {
    this.name = name;
    this.color = color;
    this.playerScore = Math.floor(Math.random() * 100);
  }
}

class GameMap {
  constructor() {
    this.height = 0;
    this.width = 0;
    this.cellSize = 0;
    this.context = null;
    this.cells = [];
  }
}

class Cell {
  constructor(x, y, color) {
    this.x = x;
    this.y = y;
    this.color = color;
  }
}
