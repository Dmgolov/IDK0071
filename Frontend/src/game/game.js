export class Game {
  gameMap: HTMLCanvasElement;
  constructor(){
    this.players = [
      "Player 1",
      "Player 2",
      "Player 3",
      "Player 4"
    ];
    this.fillCell = 0;
    this.map = {
      height: 500,
      width: 900,
      cellSize: 1,
      context: null
    };
  }


  // updateMap() {
  //   const context = this.gameMap.getContext('2d');
  //   var mapSizeX = 901;
  //   var mapSizeY = 501;
  //   var cellSize = 4;
  //   var randomX = Math.random()*(901 - 0.5) + 0.5;
  //   var randomY = Math.random()*(501 - 0.5) + 0.5;
  //   context.rect(randomX,randomY,20,20);
  //   context.fill();
  //   context.stroke();
  //   this.fillCell += 1;
  // }

  // createMap() {
  //   var mapSizeX = 901;
  //   var mapSizeY = 501;
  //   var cellSize = 1;
  //   let cells = [];
  //   let nation = "";
  //   let nationColor = "";
  //   for (let i = 0; i < mapSizeX - 1; i += cellSize) {
  //     let row = new Array();
  //     for (let j = 0; j < mapSizeY - 1; j += cellSize) {
  //       if (Math.random() < 0.5){
  //         row.push({x: i, y: j, nation: "green", nationColor: "#0F0"});
  //       } else {
  //         row.push({x: i, y: j, nation: "red", nationColor: "#F00"});
  //       }
  //     }
  //     cells.push(row);
  //   }
  //   console.log(cells);
  //   this.cells = cells;
  // }

  attached(){
    this.map.context = this.gameMap.getContext('2d');
    let cells = this.createNations(this.map.width, this.map.height, this.map.cellSize);
    this.fillMap(cells, this.map.cellSize);
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
          //console.log("THIS PLACE BELONG TO " + cell.nation);
          context.fillStyle = cell.nationColor;
          context.fillRect(cell.x, cell.y, cellSize, cellSize);
          // context.fill();
        } else if (cell.nation == "green") {
          context.fillStyle = cell.nationColor;
          context.fillRect(cell.x, cell.y, cellSize, cellSize);
          // context.rect(cell.x, cell.y, cellSize, cellSize);
          // context.fillStyle = cell.nationColor;
          // context.fill();
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
    console.log(cells);
    return cells;
  }

}
