import {inject, computedFrom} from "aurelia-framework";
import {Router} from 'aurelia-router';
import {UtilityInfo} from "../utility/utilityInfo";
import {AuthService} from 'aurelia-authentication';

@inject(UtilityInfo, Router, AuthService)
export class SignIn {
  constructor(utilityInfo, router, authService) {
    this.utilityInfo = utilityInfo;
    this.router = router;
    this.authService = authService;

    this.email;
    this.password;
  }

  signIn() {
      return this.authService.login(this.email, this.password)
      // .then(response => response.json())
      .then(data => {
        // console.log(data);
      })
      .catch(err => {
        console.log("login failure");
        // console.log(err);
      });
  }

  goBack() {
    this.router.navigate("home");
  }

}
