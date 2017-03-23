'use strict';

angular.
  module('guestCreate').
  component('guestCreate', {
    templateUrl: 'guest-create/template.html',
    controller: ['$routeParams', '$http',
      function GuestCreateController($routeParams, $http) {
        var self = this;
        self.eventId = $routeParams.eventId;

        self.createGuest = function (guest) {
          $http.post('/api/v1/events/' + self.eventId + '/guest', guest).then(function (response) {
            window.location.reload(true /* get the data from server */);
          });
        };
      }
    ]
  });
