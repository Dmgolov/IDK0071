export class Game {
  gameMap: HTMLCanvasElement;
  constructor(){
    this.players = [
      "Player 1",
      "Player 2",
      "Player 3",
      "Player 4"
    ];
    this.fillCell = 0.5;
    let cells = [];
    console.log();
    console.log(cells);
  }

  updateMap(){
    const context = this.gameMap.getContext('2d');
    var mapSizeX = 901;
    var mapSizeY = 501;
    var cellSize = 4;
    var randomX = Math.random()*(901 - 0.5) + 0.5;
    var randomY = Math.random()*(501 - 0.5) + 0.5;
    context.rect(randomX,randomY,20,20);
    context.fill();
    context.stroke();
    this.fillCell += 1;
  }

  attached(){
    const context = this.gameMap.getContext('2d');
    var mapSizeX = 901;
    var mapSizeY = 501;
    var cellSize = 50;
    let cells = [];
    for (let i = 0; i < mapSizeX - 1; i += cellSize) {
      let row = new Array();
      for (let j = 0; j < mapSizeY - 1; j += cellSize) {
        // cells.push({
        //   x: i,
        //   y: j,
        //   nation: "Green"
        // });
        row.push({x: i, y: j, nation: "Green"});

      }
      cells.push(row);
    }
    console.log(cells);
    for (var x = 0.5; x < mapSizeX; x += cellSize) {
      for (var y = 0.5; y < mapSizeY; y += cellSize) {
      context.moveTo(x, 0);
      context.lineTo(x, mapSizeY - 1);
      context.moveTo(0, y);
      context.lineTo(mapSizeX - 1, y);
    }
      context.stroke();
    }
    //setInterval(this.updateMap(), 10);
    for (let row of cells){
      for (let cell of row){
        if (cell.nation == "Green"){
          console.log("IS GREEN PLACE");
        }
      }
    }
  }
  fullRandom() {

  }

}
