import {inject} from "aurelia-framework";
import {Router} from 'aurelia-router';
import {UtilityInfo} from "../utility/utilityInfo";
import {AuthService} from 'aurelia-authentication';
import {Endpoint} from 'aurelia-api';

@inject(UtilityInfo, Router, AuthService, Endpoint.of('auth'))
export class SignUp {
  constructor(utilityInfo, router, authService, authEndpoint) {
    this.utilityInfo = utilityInfo;
    this.router = router;
    this.authService = authService;
    this.authEndpoint = authEndpoint;

    this.username = "";
    this.email = "";
    this.password = "";

    this.resultFailed = false;
    this.message = "";
  }

  signUp() {
    // this.authService.signup(this.username, this.email, this.password)
    //   .then(data => {
    //     console.log(data);
    //     if (data.status === "failed") {
    //       console.log(data.message);
    //     }
    //   })
    //   .catch(err => {
    //     console.log(err);
    //   });

    this.authEndpoint.post('signup', {
      "displayName": this.username,
      "email": this.email,
      "password": this.password
    })
    .then(data => {
      if (data.result === "failed") {
        this.resultFailed = true;
        this.message = data.message;
      } else {
        this.resultFailed = false;
        this.message = "";
        this.router.navigate("home");
      }
    })
    .catch(console.error);
  }

  goBack() {
    this.router.navigate("home");
  }

}
