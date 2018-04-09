import {inject} from 'aurelia-framework';
import {Router} from 'aurelia-router';
import {UtilityInfo} from "../utility/utilityInfo";
import {AuthService} from 'aurelia-authentication';
import {Endpoint} from 'aurelia-api';

@inject(UtilityInfo, Router, AuthService, Endpoint.of('map'))
export class BrowseMaps{
  constructor(utilityInfo, router, authService, mapEndpoint) {
    this.utilityInfo = utilityInfo;
    this.utilityInfo.requestUsernameUpdate();
    this.router = router;
    this.authService = authService;
    this.mapEndpoint = mapEndpoint;

    this.imageName = 'gameMap5.png';  // name of image, which is choosen by user
  }

  attached() {
    this.showMapsList();

    this.showMap();
  }

  showMapsList() {
    if (this.authService.isAuthenticated()) {
      this.mapEndpoint.post('browse', {
        "startIndex" : 0
      })
      .then(data => {
        console.log(data);
      })
      .catch(console.error);
    }
  }

  showMap2() {
    let myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/json");
    myHeaders.append("Accept", "image/*");

    if (this.authService.isAuthenticated()) {
      this.mapEndpoint.client.fetch('image', {
        method: 'POST',
        headers: myHeaders,
        body: {"imageName": this.imageName}
      })
      .then(data => {
        console.log(data);
      })
      .catch(console.error);
    }
  }

  showMap() {
    if (this.authService.isAuthenticated()) {
      this.mapEndpoint.client.fetch('image', {
        method: 'GET'
      })
      .then(data => {
        console.log(data);
      })
      .catch(console.error);
    }
  }

}
