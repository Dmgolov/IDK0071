import {inject} from "aurelia-framework";
import {HttpClient, json} from "aurelia-fetch-client";
import {Router} from 'aurelia-router';
import {UtilityInfo} from "../utility/utilityInfo";

@inject(UtilityInfo, Router)
export class SignIn {
  constructor(utilityInfo, router) {
  }
}
