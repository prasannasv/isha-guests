'use strict';

angular.
  module('guestCreate').
  component('guestCreate', {
    templateUrl: 'guest-create/template.html',
    controller: ['$scope', '$routeParams', '$http', 'FileUploader',
      function GuestCreateController($scope, $routeParams, $http, FileUploader) {
        var self = this;
        self.eventId = $routeParams.eventId;

        $scope.uploader = new FileUploader({
          url: '/api/v1/events/' + self.eventId + '/guests',
          autoUpload: true
        });

        $scope.uploader.onSuccessItem = function (item, response, status, headers) {
          window.location.reload(true /* get the data from server */);
        };

        self.createGuest = function (guest) {
          $http.post('/api/v1/events/' + self.eventId + '/guest', guest).then(function (response) {
            window.location.reload(true /* get the data from server */);
          });
        };
      }
    ]
  });
