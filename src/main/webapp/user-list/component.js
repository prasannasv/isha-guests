'use strict';

angular.
  module('userList').
  component('userList', {
    templateUrl: 'user-list/template.html',
    controller: ['$http', function UserListController($http) {
      var self = this;

      self.orderByField = 'firstName';
      self.reverseOrder = false;

      self.orderBy = function (field) {
        self.reverseOrder = (field === self.orderByField) ? !self.reverseOrder : false;
        self.orderByField = field;
      };

      $http.get('/api/v1/users').then(function (response) {
        console.log("users list response", response);
        self.users = response.data;
      });
    }
    ]
});
