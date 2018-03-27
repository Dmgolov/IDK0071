import environment from './environment';
import authConfig from './authConfig';

export function configure(aurelia) {
  aurelia.use
    .standardConfiguration()
    .plugin('aurelia-api', config => {
      config.registerEndpoint('auth');
    })
    .plugin('aurelia-authentication', baseConfig => {
        baseConfig.configure(authConfig);
    })
    .feature('resources');

  if (environment.debug) {
    aurelia.use.developmentLogging();
  }

  if (environment.testing) {
    aurelia.use.plugin('aurelia-testing');
  }

  aurelia.start().then(() => aurelia.setRoot());
}
