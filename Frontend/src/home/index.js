import {inject} from "aurelia-framework";
// import {HttpClient, json} from "aurelia-fetch-client";
import {Router} from 'aurelia-router';
import {UtilityInfo} from "../utility/utilityInfo";
import {AuthService} from 'aurelia-authentication';
import {Endpoint} from 'aurelia-api';

import environment from '../environment';

@inject(UtilityInfo, Router, AuthService, Endpoint.of('auth'))
export class Home {
  constructor(utilityInfo, router, authService, authEndpoint) {
    this.utilityInfo = utilityInfo;
    this.router = router;
    this.authService = authService;
    this.authEndpoint = authEndpoint;

    // console.log(this.router);
  }

  attached() {
    this.getUsername();
  }

  getUsername() {
    if (this.authService.isAuthenticated()) {
      this.authEndpoint.find('user')
      .then(data => {
        console.log(data);
      });
    }
  }

  signOut() {
    this.authService.logout();
  }

  createLobby() {
    // console.log(this.utilityInfo);

    let client = new HttpClient();
    client.fetch(environment.apiBaseUrl + "/lobby/new", {
      "method": "POST",
      "body": json({"playerName": this.utilityInfo.username}),
      headers: {
        'Origin': environment.apiBaseUrl,
        'Content-Type': 'application/json'
      }
    })
      .then(response => response.json())
      .then(data => {
        this.utilityInfo.lobbyId = data.lobbyId;
        // console.log(this.utilityInfo);

        this.router.navigate("lobby");
    });
  }

  connectToLobby() {
    // console.log(this.utilityInfo);

    let client = new HttpClient();
    client.fetch(environment.apiBaseUrl + "/lobby/connect", {
      "method": "POST",
      "body": json({
        "playerName": this.utilityInfo.username,
        "lobbyId": this.utilityInfo.lobbyId
      }),
      headers: {
        'Origin': environment.apiBaseUrl,
        'Content-Type': 'application/json'
      }
    })
      .then(response => response.json())
      .then(data => {
        // console.log("this was connect response");
        // console.log(data);
        if (data.status !== "failed") {
          this.utilityInfo.gameMode = data.gameMode;
          this.router.navigate("lobby");
        }
    });
  }
}
