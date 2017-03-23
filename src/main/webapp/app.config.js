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
        when('/admin/centers', {
          template: '<center-list></center-list>'
        }).
        when('/admin/centers/new', {
          template: '<center-create></center-create>'
        }).
        otherwise('/events');
    }
  ]);
