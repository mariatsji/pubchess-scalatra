'use strict';

var app = angular.module('scalakursApp', ['ui.bootstrap']);

app.config(function ($routeProvider) {
    $routeProvider
        .when('/', {
            templateUrl: 'views/main.html',
            controller: 'MainController'
        })
        .when('/player/:id', {
            templateUrl: 'views/player.html',
            controller: 'PlayerController'
        })
        .otherwise({
            redirectTo: '/'
        });
  });
