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

      self.promote = function (user) {
        console.log("Promoting user:", user, user.userId);
        $http.post('/api/v1/users/' + user.userId + '/promote').then(function (response) {
          window.location.reload(true /* get the data from server */);
        });
      };
      self.demote = function (user) {
        console.log("Demoting user:", user, user.userId);
        $http.post('/api/v1/users/' + user.userId + '/demote').then(function (response) {
          window.location.reload(true /* get the data from server */);
        });
      };
    }
    ]
});
