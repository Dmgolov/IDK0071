export class App {
  configureRouter(config, router) {
        this.router = router;
        config.title = 'Empires game (Aurelia)';
        config.map([
          { route: ['', 'home'], name: 'home', moduleId: 'home/index' },
          { route: 'lobby', name: 'lobby', moduleId: 'lobby/lobby', nav: true, title: 'Lobby' },
        ]);
      }
}
