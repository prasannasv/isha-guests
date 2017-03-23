'use strict';

angular.
  module('centerList').
  component('centerList', {
    templateUrl: 'center-list/template.html',
    controller: ['$http', function CenterListController($http) {
      var self = this;

      self.orderByField = 'cityName';
      self.reverseOrder = false;

      self.orderBy = function (field) {
        self.reverseOrder = (field === self.orderByField) ? !self.reverseOrder : false;
        self.orderByField = field;
      };

      $http.get('/api/v1/centers').then(function (response) {
        console.log("centers list response", response);
        self.centers = response.data;
      });
    }
    ]
});
