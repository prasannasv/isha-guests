'use strict';

angular.
  module('eventList').
  component('eventList', {
    templateUrl: 'event-list/template.html',
    controller: ['$http', function EventListController($http) {
      var self = this;

      self.orderByField = 'eventDate';
      self.reverseOrder = false;

      self.orderBy = function (field) {
        self.reverseOrder = (field === self.orderByField) ? !self.reverseOrder : false;
        self.orderByField = field;
      };

      $http.get('/api/v1/events').then(function (response) {
        console.log("events list response", response);
        var allEvents = response.data.events;
        self.userRole = response.data.userRole;
        self.upcomingEvents = [];
        self.pastEvents = [];

        // Group expired events and upcoming events separately.
        // Convert the date in string format to JavaScript Date objects.
        var now = new Date().getTime();
        for (var i = 0, len = allEvents.length; i < len; ++i) {
          var event = allEvents[i];
          event.eventDate = new Date(event.eventDate);
          event.createdAt = new Date(event.createdAt);

          if (event.eventDate.getTime() > now) {
            self.upcomingEvents.push(event);
          } else {
            self.pastEvents.push(event);
          }
        }
      });
    }
    ]
});
