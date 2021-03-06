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
        when('/events/new', {
          template: '<event-create></event-create>'
        }).
        when('/events/:eventId', {
          template: '<event-detail></event-detail>'
        }).
        when('/admin/centers', {
          template: '<center-list></center-list>'
        }).
        when('/admin/centers/new', {
          template: '<center-create></center-create>'
        }).
        when('/admin/users', {
          template: '<user-list></user-list>'
        }).
        otherwise('/events');
    }
  ]);
