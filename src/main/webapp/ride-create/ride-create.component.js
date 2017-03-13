'use strict';

angular.
  module('rideCreate').
  component('rideCreate', {
    templateUrl: 'ride-create/ride-create.template.html',
    controller: ['$routeParams', '$http',
      function RideCreateController($routeParams, $http) {
        var self = this;
        var tripId = $routeParams.tripId;

        self.hasAlreadyOfferedOrRequestedRide = false;
        //TODO: Get this from userService
        $http.get('/api/v1/trips/' + tripId + '/ride_offers').then(function (response) {
          if (response.data && response.data !== "null") {
            self.hasAlreadyOfferedOrRequestedRide = true;
          }
        });
        // Do the same check for /ride_requests
        $http.get('/api/v1/trips/' + tripId + '/ride_requests').then(function (response) {
          if (response.data && response.data !== "null") {
            self.hasAlreadyOfferedOrRequestedRide = true;
          }
        });

        self.ride = {
          seats: "1"
        };

        self.createRideOfferOrRequest = function () {
          $http.post('/api/v1/trips/' + tripId + '/ride_' + self.ride.option, self.ride).then(function (response) {
            window.location.reload(true /* get the data from server */);
          });
        };

        console.log("tripId:", tripId, "ride:", self.ride);
      }
    ]
  });
