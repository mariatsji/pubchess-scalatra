'use strict';

var app = angular.module('pubchessApp', ['ui.bootstrap']);

app.config(function ($routeProvider) {
    $routeProvider
        .when('/', {
            templateUrl: 'index.html',
            controller: 'PubchessController'
        })
});