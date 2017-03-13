'use strict';

angular.
  module('eventGuestsApp').
  config(['$locationProvider', '$routeProvider',
    function config($locationProvider, $routeProvider) {
      $locationProvider.hashPrefix('!');

      $routeProvider.
        when('/events', {
          template: '<event-list></event-list>'
        }).
        when('/events/:eventId', {
          template: '<event-detail></event-detail>'
        }).
        when('/event_create', {
          template: '<event-create></event-create>'
        }).
        otherwise('/events');
    }
  ]);
