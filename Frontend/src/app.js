import {AuthenticateStep} from 'aurelia-authentication';

export class App {
  configureRouter(config, router) {
        this.router = router;

        config.title = 'Empires game (Aurelia)';

        config.addPipelineStep('authorize', AuthenticateStep);

        config.map([
          { route: ['', 'home'], name: 'home', moduleId: 'home/index' },
          { route: 'signIn', name: 'signIn', moduleId: 'signIn/signIn', nav: true, title: 'Sign In'},
          { route: 'signUp', name: 'signUp', moduleId: 'signUp/signUp', nav: true, title: 'Sign Up'},
          { route: 'lobby', name: 'lobby', moduleId: 'lobby/lobby', nav: true, title: 'Lobby', auth: true},
          { route: 'game', name: 'game', moduleId: 'game/game', nav: true, title: 'Game', auth: true},
        ]);
      }
}
