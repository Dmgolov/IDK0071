import {inject} from 'aurelia-framework';
import {Router} from 'aurelia-router';
import {UtilityInfo} from "../utility/utilityInfo";
import {AuthService} from 'aurelia-authentication';
import {Endpoint} from 'aurelia-api';
import {HttpClient} from 'aurelia-fetch-client';

@inject(UtilityInfo, Router, AuthService, Endpoint.of('map'))
export class MapTool {
  constructor(utilityInfo, router, authService, mapEndpoint) {
    this.utilityInfo = utilityInfo;
    this.utilityInfo.requestUsernameUpdate();
    this.router = router;
    this.authService = authService;
    this.mapEndpoint = mapEndpoint;
  }

  sendMapImage() {
    let mapImage = this.mapInput.files[0];
    let formData = new FormData();
    formData.append('username', this.utilityInfo.username);
    formData.append('mapImage', mapImage);
    //
    // this.mapEndpoint.post('upload', formData)
    // .then(data => {
    //   console.log(data);
    // })
    // .catch(console.error);

    if (this.authService.isAuthenticated()) {
      // let httpClient = new HttpClient();
      //
      // httpClient.fetch('http://localhost:8080/map/upload', {
      //   method: 'POST',
      //   body: formData;
      // });







      this.mapEndpoint.client.fetch('upload', {
        method: 'POST',
        body: formData
      })
      .then()
      .catch(console.error);
    }

    // let headers = new Headers();
    // headers.append('Authorization', `Bearer ${localStorage.getItem('id_token')}`);
    // //myHeaders.append('Content-Type', 'application/x-www-form-urlencoded');
    //
    // return this.http.fetch(`${this.apiController}`, {
    //      method: 'post',
    //      headers: myHeaders,
    //      body: data
    //  })
    //  .then(response => {
    //      if (response.status >= 200 && response.status < 400) {
    //          return response.json().catch(() => null);
    //      }
    //
    //      throw response;
    //  })
    //  .then(result => { return result; })
    //  .catch(errorProcessor);
  }
}
