'use strict';

angular.module('myApp.view1', ['ngRoute'])

    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/view1', {
            templateUrl: 'view1/view1.html',
            controller: 'View1Ctrl'
        });
    }])

    .controller('View1Ctrl', function ($scope, tinkerService, $interval) {
        $scope.currentNodes = 0;
        $scope.currentTimeout = 0;

        $scope.nodesToAdd = 0;
        $scope.nodesToDelete = 0;
        $scope.newTimeout = 20;
        var requestCount = 0;

        activate();

        $scope.addNodes = addNodes;
        $scope.deleteNodes = deleteNodes;
        $scope.changeTimer = changeTimer;

        $scope.chartData = [{
            label: "Total Requests",
            data: [],
            points: {show: true, radius: 2},
            splines: {show: true, tension: 0.4, lineWidth: 1}
        },
            {
                label: "Per Node",
                data: [],
                points: {show: true, radius: 2},
                splines: {show: true, tension: 0.4, lineWidth: 1}
            }];
        $scope.chartOptions = {
            series: {shadowSize: 3},
            xaxis: {font: {color: '#507b9b'}},
            yaxis: {font: {color: '#507b9b'}},
            grid: {hoverable: true, clickable: true, borderWidth: 0, color: '#1c2b36'},
            tooltip: true,
            tooltipOpts: {content: 'Requests of t%x.1 is %y.4', defaultTheme: false, shifts: {x: 10, y: -25}},
            height: '360px',
            legend: {backgroundColor: '#d9f3fb'}
        };

        function activate() {
            checkNodes()
        }

        $interval(checkNodes, 2000);

        function checkNodes() {
            tinkerService.checkNodes().then(function (data) {
                var totalRequests = 0;
                angular.forEach(data.nodesStatus.nodeStatus, function (value, key) {
                    totalRequests += value.msgPerSecond;
                });
                requestCount++;

                var totalNodes = data.nodesStatus.nodeStatus.length;
                var perNode = Math.round(totalRequests/totalNodes);

                $scope.chartData[0]['data'].push([requestCount, totalRequests]);
                $scope.chartData[1]['data'].push([requestCount, perNode]);


                $scope.currentNodes = totalNodes;
                $scope.currentTimeout = data.nodesStatus.timeout;
            });
        }

        function addNodes() {
            tinkerService.addNodes(parseInt($scope.nodesToAdd));
        }

        function deleteNodes() {
            tinkerService.deleteNodes(parseInt($scope.nodesToDelete));
        }

        function changeTimer() {
            tinkerService.changeTimer(parseInt($scope.newTimeout));
        }
    });