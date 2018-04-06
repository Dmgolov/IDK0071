import {inject} from 'aurelia-framework';
import {Router} from 'aurelia-router';
import {FetchConfig} from 'aurelia-authentication';
import {AuthenticateStep} from 'aurelia-authentication';


@inject(Router,FetchConfig)
export class App {
  constructor(router, fetchConfig) {
    this.router = router;
    this.fetchConfig = fetchConfig;
  }

  activate() {
    // this will add the interceptor for the Authorization header to the HttpClient singleton
    this.fetchConfig.configure();
  }

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
