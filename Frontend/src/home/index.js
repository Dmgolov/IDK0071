import {inject} from "aurelia-framework";
import {Router} from 'aurelia-router';
import {UtilityInfo} from "../utility/utilityInfo";
import {AuthService} from 'aurelia-authentication';
import {Endpoint} from 'aurelia-api';

import environment from '../environment';

@inject(UtilityInfo, Router, AuthService, Endpoint.of('auth'), Endpoint.of('lobby'))
export class Home {
  constructor(utilityInfo, router, authService, authEndpoint, lobbyEndpoint) {
    this.utilityInfo = utilityInfo;
    this.router = router;
    this.authService = authService;
    this.authEndpoint = authEndpoint;
    this.lobbyEndpoint = lobbyEndpoint;
  }

  attached() {
    this.getUsername();
  }

  getUsername() {
    if (this.authService.isAuthenticated()) {
      this.authEndpoint.find('user')
      .then(data => {
        this.utilityInfo.username = data.username;
      });
    }
  }

  signOut() {
    this.authService.logout();
  }

  createLobby() {
    if (this.authService.isAuthenticated()) {
      this.lobbyEndpoint.post('new', {
        "playerName": this.utilityInfo.username
      })
      .then(data => {
        this.utilityInfo.lobbyId = data.lobbyId;
        this.router.navigate("lobby");
      })
      .catch(console.error);
    }
  }

  connectToLobby() {
    if (this.authService.isAuthenticated()) {
      this.lobbyEndpoint.post('connect', {
        "playerName": this.utilityInfo.username,
        "lobbyId": this.utilityInfo.lobbyId
      })
      .then(data => {
        if (data.status !== "failed") {
          this.utilityInfo.gameMode = data.gameMode;
          this.router.navigate("lobby");
        }
      })
      .catch(console.error);
    }
  }
}
