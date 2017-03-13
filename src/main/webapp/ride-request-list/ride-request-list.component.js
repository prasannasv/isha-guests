'use strict';

angular
  .module('rideRequestList')
  .component('rideRequestList', {
    templateUrl: 'ride-request-list/ride-request-list.template.html',
    controller: ['$routeParams', '$http',
      function RideRequestListController ($routeParams, $http) {
        var self = this;

        self.orderByField = 'requestedOn';
        self.reverseOrder = false;

        self.orderBy = function (field) {
          self.reverseOrder = (field === self.orderByField) ? !self.reverseOrder : false;
          self.orderByField = field;
        };

        self.isPrivilegedUser = false;

        var tripId = $routeParams.tripId;

        $http.get('/api/v1/trips/' + tripId + '/ride_requests').then(function (response) {
          console.log("Got ride requests for tripId:", tripId, response);

          if (response.data && response.data !== "null") {
            // it will be a collection for a trip coordinator or an administrator.
            // for regular users, it will be a single object.
            self.isPrivilegedUser = Array.isArray(response.data);
            console.log("isPrivilegedUser:", self.isPrivilegedUser);

            self.rideRequests = Array.isArray(response.data) ? response.data : [response.data];
          } else {
            self.rideRequests = [];
          }
        });

      }
    ]
  });