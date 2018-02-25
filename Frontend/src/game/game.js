export class Game {
  gameMap: HTMLCanvasElement;
  constructor(){
    this.players = [
      "Player 1",
      "Player 2",
      "Player 3",
      "Player 4"
    ];
  }

  updateMap(){

  }

  attached(){
    const context = this.gameMap.getContext('2d');
    for (var x = 0.5; x < 901; x += 4) {
      context.moveTo(x, 0);
      context.lineTo(x, 501);
      context.strokeStyle = "#ddd";
    }

    for (var y = 0.5; y < 501; y += 4) {
      context.moveTo(0, y);
      context.lineTo(900, y);
    }

    context.stroke();

  }
}
