'use strict';

angular.
  module('guestList').
  component('guestList', {
    templateUrl: 'guest-list/template.html',
    controller: ['$routeParams', '$http', function GuestListController($routeParams, $http) {
      var self = this;

      self.orderByField = 'firstName';
      self.reverseOrder = false;

      self.orderBy = function (field) {
        self.reverseOrder = (field === self.orderByField) ? !self.reverseOrder : false;
        self.orderByField = field;
      };

      self.eventId = $routeParams.eventId;

      $http.get('/api/v1/events/' + self.eventId + '/guests').then(function (response) {
        console.log("guest list response", response);
        self.guests = response.data;
      });
    }
    ]
});
