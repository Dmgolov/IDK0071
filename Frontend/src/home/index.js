import {inject} from 'aurelia-framework';
import {Router} from 'aurelia-router';
import {UtilityInfo} from "../utility/utilityInfo";
import {AuthService} from 'aurelia-authentication';
import {Endpoint} from 'aurelia-api';

<<<<<<< HEAD
import environment from '../environment';


@inject(LobbyInfo, Router)
=======
@inject(UtilityInfo, Router, AuthService, Endpoint.of('auth'), Endpoint.of('lobby'))
>>>>>>> master
export class Home {
  constructor(utilityInfo, router, authService, authEndpoint, lobbyEndpoint) {
    this.utilityInfo = utilityInfo;
    this.router = router;
    this.authService = authService;
    this.authEndpoint = authEndpoint;
    this.lobbyEndpoint = lobbyEndpoint;
  }

  attached() {
    this.utilityInfo.requestUsernameUpdate();
  }

  signOut() {
    this.authService.logout();
  }

<<<<<<< HEAD
    let client = new HttpClient();
    client.fetch(environment.apiBaseUrl + "/lobby/new", {
      "method": "POST",
      "body": json({"playerName": this.lobbyInfo.playerName}),
      headers: {
        'Origin': environment.apiBaseUrl,
        'Content-Type': 'application/json'
      }
    })
      .then(response => response.json())
=======
  createLobby() {
    if (this.authService.isAuthenticated()) {
      this.lobbyEndpoint.post('new', {
        "playerName": this.utilityInfo.username
      })
>>>>>>> master
      .then(data => {
        this.utilityInfo.lobbyId = data.lobbyId;
        this.router.navigate('lobby');
      })
      .catch(console.error);
    }
  }

  connectToLobby() {
    if (this.authService.isAuthenticated()) {
      console.log({
        "playerName": this.utilityInfo.username,
        "lobbyId": this.utilityInfo.lobbyId
      });
      this.lobbyEndpoint.post('connect', {
        "playerName": this.utilityInfo.username,
        "lobbyId": this.utilityInfo.lobbyId
      })
      .then(data => {
        console.log(data);
        if (data.status !== 'failed') {
          this.utilityInfo.gameMode = data.gameMode;
          this.router.navigate('lobby');
        }
      })
      .catch(console.error);
    }
  }

  uploadMapPage() {
    this.router.navigate("mapTool");
  }
}
