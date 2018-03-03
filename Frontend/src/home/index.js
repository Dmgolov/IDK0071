import {inject} from "aurelia-framework";
import {HttpClient, json} from "aurelia-fetch-client";
import {Router} from 'aurelia-router';
import {LobbyInfo} from "../lobby/lobbyInfo";

@inject(LobbyInfo, Router)
export class Home {
  constructor(lobbyInfo, router) {
    this.lobbyInfo = lobbyInfo;
    this.router = router;

    console.log(this.router);
  }

  createLobby() {
    console.log(this.lobbyInfo);

    let client = new HttpClient();
    client.fetch("http://localhost:8080/lobby/new", {
      "method": "POST",
      "body": json({"playerName": this.lobbyInfo.playerName}),
      headers: {
        'Origin': 'http://localhost:8080',
        'Content-Type': 'application/json'
      }
    })
      .then(response => response.json())
      .then(data => {
        this.lobbyInfo.lobbyId = data.lobbyId;
        console.log(this.lobbyInfo);
        
        this.router.navigate("lobby");
    });
  }
}
