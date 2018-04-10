import {inject} from 'aurelia-framework';
import {Router} from 'aurelia-router';
import {UtilityInfo} from "../utility/utilityInfo";
import {AuthService} from 'aurelia-authentication';
import {Endpoint} from 'aurelia-api';

@inject(UtilityInfo, Router, AuthService, Endpoint.of('map'))
export class MapTool {
  constructor(utilityInfo, router, authService, mapEndpoint) {
    this.utilityInfo = utilityInfo;
    this.utilityInfo.requestUsernameUpdate();
    this.router = router;
    this.authService = authService;
    this.mapEndpoint = mapEndpoint;

    this.fileName = 'Choose file';
  }

  showFileName() {
    let file = this.mapInput.files[0];
    this.fileName = file === undefined ? 'Choose file' : file.name;
  }

  sendMapImage() {
    if (this.authService.isAuthenticated()) {

      let mapImage = this.mapInput.files[0];
      mapImage = mapImage === undefined ? new File([""], "EMPTY_FILE") : mapImage;
      let formData = new FormData();
      formData.append('username', this.utilityInfo.username);
      formData.append('mapImage', mapImage);

      this.mapEndpoint.client.fetch('upload', {
        method: 'POST',
        body: formData
      })
      .then()
      .catch(console.error);
    }
  }
}
