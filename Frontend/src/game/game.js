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
      cellSize: 5,
      context: null
    };
  }

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
    console.log(cells);
    return cells;
  }

}
