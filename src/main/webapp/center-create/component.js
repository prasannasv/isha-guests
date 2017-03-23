'use strict';

angular.
  module('centerCreate').
  component('centerCreate', {
    templateUrl: 'center-create/template.html',
    controller: ['$http',
      function CenterCreateController($http) {
        var self = this;

        self.createCenter = function (center) {
          $http.post('/api/v1/centers', center).then(function (response) {
            window.location.href = '/';
          });
        };
      }
    ]
  });
