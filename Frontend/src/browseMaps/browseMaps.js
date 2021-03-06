import {inject} from 'aurelia-framework';
import {Router} from 'aurelia-router';
import {UtilityInfo} from "../utility/utilityInfo";
import {AuthService} from 'aurelia-authentication';
import {Endpoint} from 'aurelia-api';
import {json} from 'aurelia-fetch-client';

@inject(UtilityInfo, Router, AuthService, Endpoint.of('map'))
export class BrowseMaps{
  constructor(utilityInfo, router, authService, mapEndpoint) {
    this.utilityInfo = utilityInfo;
    this.utilityInfo.requestUsernameUpdate();
    this.router = router;
    this.authService = authService;
    this.mapEndpoint = mapEndpoint;

    this.usernameForSorting = "";
    this.canShowMaps = true;

    this.mapName = '';  // name of image, which is choosen by user
    this.maps = [];
    this.setMapsArray();
  }

  setMapsArray() {
    let escapedUsername = this.utilityInfo.htmlEscape(this.usernameForSorting);

    if (this.authService.isAuthenticated()) {
      this.mapEndpoint.post('browse', {
        "startIndex": 0,
        "username": escapedUsername
      })
      .then(data => {
        for (let mapName of data) {
          this.maps.push(mapName);
        }
      })
      .catch(console.error);
    }
  }

  showMap() {
    if (this.authService.isAuthenticated()) {
      let myHeaders = new Headers();
      myHeaders.append("Content-Type", "application/json");
      myHeaders.append("Accept", "image/*");

      this.mapEndpoint.client.fetch('image', {
        method: 'POST',
        headers: myHeaders,
        body: json({"mapName": this.mapName})
      })
      .then(response => response.blob())
      .then(data => {
        let blobURL = URL.createObjectURL(data);
        let image = new Image();
        image.src = blobURL;
        image.onload = () => {
          if (image.width == image.height) {
            image.height = 300;
            image.width = 300;
            this.imageContainer.style.width = 300 + 'px';
          } else if (image.width > image.height) {
            image.height = 150;
            image.width = 300;
            this.imageContainer.style.width = 300 + 'px';
          } else if (image.width < image.height) {
            image.height = 300;
            image.width = 150;
            this.imageContainer.style.width = 150 + 'px';
          }

          while (this.imageContainer.firstChild) {
            this.imageContainer.removeChild(this.imageContainer.firstChild);
          }
          this.imageContainer.appendChild(image);
        };
      })
      .catch(console.error);
    }
  }

  selectMap(){
    let selectedMapIndex = this.mapSelector.selectedIndex;
    this.mapName = this.mapSelector.options[selectedMapIndex].text;
    this.showMap();
  }

  /*
  * These functions are not used in project;
  * These may be used for getting text-indent style for text in some container.
  *
  getTextWidth(text) {
    let textSpan = document.createElement('span');
    textSpan.innerHTML = text;
    textSpan.style.fontSize = '13.3333px'
    this.imageContainer.appendChild(textSpan);
    let textWidth = textSpan.offsetWidth;
    textSpan.remove();
    return textWidth;
  }

  getIndentForOption(text) {
    let textWidth = this.getTextWidth(text);
    let selectorWidth = this.mapSelector.clientWidth;
    let indentWidth = (selectorWidth - textWidth) / 2;
    return indentWidth;
  }

  getIndentStyleForOption(text) {
    let indentWidth = this.getIndentForOption(text);
    let indentStyle = 'text-indent: ' + indentWidth + 'px;';
    return indentStyle;

  }
  */

  searchByUsername() {
    let escapedUsername = this.utilityInfo.htmlEscape(this.usernameForSorting);

    if (this.authService.isAuthenticated()) {
      this.mapEndpoint.post('browse', {
        "startIndex": 0,
        "username": escapedUsername
      })
      .then(data => {
        this.canShowMaps = false;
        this.maps.length = 0;
        while (this.imageContainer.firstChild) {
          this.imageContainer.removeChild(this.imageContainer.firstChild);
        }
        let optionElements = this.mapSelector.childNodes;
        while (optionElements.length > 2) {
          this.mapSelector.removeChild(this.mapSelector.lastChild);
        }
        for (let mapName of data) {
          this.maps.push(mapName);
          let optionElement = document.createElement("OPTION");
          optionElement.text = mapName;
          this.mapSelector.appendChild(optionElement);
        }
        this.canShowMaps = true;
      })
      .catch(console.error);
    }
  }

  goBack() {
    this.router.navigate("home");
  }
}
