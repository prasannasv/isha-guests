'use strict';

angular.
  module('eventCreate').
  component('eventCreate', {
    templateUrl: 'event-create/template.html',
    controller: ['$http',
      function EventCreateController($http) {
        var self = this;

        self.centers = [];
        //TODO: Get this once
        $http.get('/api/v1/centers').then(function (response) {
          console.log("centers:", response);
          self.centers = response.data;

          // Construct a displayable key formed with city, state and country so that the center is unambiguous.
          for (var id in self.centers) {
            var center = self.centers[id];
            center.key = center.cityName + ', ' + center.stateCode + ', ' + center.country;
          }
        });

        self.createEvent = function (event) {
          var offeringsSelected = [];
          for (var property in event.offerings) {
            if (event.offerings.hasOwnProperty(property)) {
              if (event.offerings[property]) {
                offeringsSelected.push(property);
              }
            }
          }
          event.offerings = offeringsSelected;

          $http.post('/api/v1/events', event).then(function (response) {
            console.log("new event's id:", response);
            window.location.href = '/';
          });
        };
      }
    ]
  });
