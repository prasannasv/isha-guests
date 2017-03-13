'use strict';

angular.
  module('eventCreate').
  component('eventCreate', {
    templateUrl: 'event-create/template.html',
    controller: ['$http',
      function EventCreateController($http) {
        var self = this;

        self.createEvent = function (event) {
          $http.post('/api/v1/events', event).then(function (response) {
            console.log("new event's id:", response);
            window.location.href = '/';
          });
        };
      }
    ]
  });
