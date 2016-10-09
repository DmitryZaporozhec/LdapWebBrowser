<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@page pageEncoding="UTF-8" %>
<html lang="en">
<!-- Angular Material requires Angular.js Libraries -->
<script src="http://ajax.googleapis.com/ajax/libs/angularjs/1.5.5/angular.min.js"></script>
<script src="http://ajax.googleapis.com/ajax/libs/angularjs/1.5.5/angular-animate.min.js"></script>
<script src="http://ajax.googleapis.com/ajax/libs/angularjs/1.5.5/angular-aria.min.js"></script>
<script src="http://ajax.googleapis.com/ajax/libs/angularjs/1.5.5/angular-messages.min.js"></script>
<!-- Angular Material Library -->
<script src="http://ajax.googleapis.com/ajax/libs/angular_material/1.1.0/angular-material.min.js"></script>

<link rel="stylesheet" type="text/css" href="/css/style.css">
<link rel="stylesheet" href="/css/bootstrap.css">
<link rel="stylesheet" href="http://ajax.googleapis.com/ajax/libs/angular_material/1.1.0/angular-material.min.css">
<script language="javascript" src="/js/jquery.js" type="text/javascript"></script>
<body>

<div id="userManagmentApp" style="display: none;" class="container-fluid" ng-app="userManagmentApp"
     ng-controller="userManagmentCtrl">
    <div class="row text-center">
        <div class="col-md-12 text-center"><h1 class="text-center">Ldap Web browser</h1></div>
    </div>
    <div class="row">
        <div class="col-md-3">
            <h1>Connection configuration</h1>
            <form>
                <div class="form-group">
                    <label for="url">Connection String</label>
                    <input type="text" class="form-control" id="url" placeholder="Connection string"
                           ng-model="connection.url">
                </div>
                <div class="form-group">
                    <label for="userName">User name</label>
                    <input type="text" class="form-control" id="userName" placeholder="User name"
                           ng-model="connection.userName">
                </div>
                <div class="form-group">
                    <label for="userPassword">User Password</label>
                    <input type="password" class="form-control" id="userPassword" placeholder="User Password"
                           ng-model="connection.userPassword">
                </div>
                <div class="form-group">
                    <label for="baseDN">Base DN</label>
                    <input type="text" class="form-control" id="baseDN" placeholder="Base DN"
                           ng-model="connection.baseDN">
                </div>
                <div class="form-group">
                    <div class="alert alert-info" role="alert">{{connectionStatus}}</div>
                </div>
                <input id="testConnection" class="btn btn-lg" type='button' value="Test connection"
                       ng-click="testConnection()"/>
                <h2>Search</h2>
                <div class="form-group">
            <textarea style="width: 100%" class="form-control" type="text"
                      ng-model="connection.searchFilter"></textarea>
                </div>
                <input class="btn btn-primary btn-lg" type="button" ng-click="search()" value="Search">
                <h2>Search results</h2>
                <div style="overflow: auto;white-space: nowrap">
                    <div class="row" ng-repeat='x in tree'>
                        <div class="col-sm-12" ng-click="showDetails(x)"><a>{{x.id}}</a></div>
                    </div>
                </div>
            </form>
        </div>
        <div class="col-md-9" id="infocontainer">
            <div class="row col-sm-12 " ng-repeat="(key,value) in detailsObject">
                <div class="row col-sm-4 list-group-item grey">{{key}}</div>
                <div class="row col-sm-8">
                    <ul class="list-group">
                        <li class="list-group-item" ng-repeat="val in value">{{val}}</li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
<script>
    var app = angular.module('userManagmentApp', []);
    app.controller('userManagmentCtrl', function ($scope, $http) {
        $scope.testConnection = function () {
            $http.post("/rest/api/ldap/test", $scope.connection).then(function (response) {
                $scope.connectionStatus = response.data.message;
            }, function (response) {
                $scope.connectionStatus = response.data.message;
            });
        }
        $scope.search = function () {
            $http.post("/rest/api/ldap/search", $scope.connection).then(function (response) {
                $scope.tree = response.data.returnObject;
                $scope.connectionStatus = response.data.responseCode + (response.data.message != null ? ": " + response.data.message : "");
            }, function (response) {
                $scope.connectionStatus = response.data.responseCode + (response.data.message != null ? ": " + response.data.message : "");
            });
        }
        $scope.showDetails = function (nodeObject) {
            $scope.detailsObject = nodeObject.attributes;
        }
    });
    $(document).ready()
    {
        $('#userManagmentApp').show();
    }
</script>
</html>

