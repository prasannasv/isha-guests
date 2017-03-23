'use strict';

angular.
  module('eventDetail').
  component('eventDetail', {
    templateUrl: 'event-detail/template.html',
    controller: ['$routeParams', '$http',
      /**
       * Exports the following model information for the given eventId.
       *
       * event - the event object corresponding to the given eventId.
       */
      function EventDetailController($routeParams, $http) {
        var self = this;

        $http.get('/api/v1/events/' + $routeParams.eventId).then(function(response) {
          console.log("response of event lookup by id:", $routeParams.eventId, response);
          self.event = response.data;
        });
      }
    ]
  });
