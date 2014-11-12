(function () {
    'use strict';

    angular
        .module('myApp')
        .factory('tinkerService', tinkerService);

    /* @ngInject */
    function tinkerService($http) {
        var apiPath = 'http://localhost:3010/node-sim';

        var service = {
            checkNodes: checkNodes,
            addNodes: addNodes,
            deleteNodes: deleteNodes,
            changeTimer: changeTimer
        };

        return service;

        function addNodes(count) {
            var method = 'POST';
            var path = '/node/add';
            var jsonData = {
                'nodes': count
            };
            return unauthenticated(method, path, jsonData);
        }

        function deleteNodes(count) {
            var method = 'POST';
            var path = '/node/delete';
            var jsonData = {
                'nodes': count
            };
            return unauthenticated(method, path, jsonData);
        }

        function changeTimer(timeout) {
            var method = 'POST';
            var path = '/node/timer';
            var jsonData = {
                'timeout': timeout
            };
            return unauthenticated(method, path, jsonData);
        }

        function checkNodes() {
            var method = 'GET';
            var path = '/status';
            var jsonData = {};
            return unauthenticated(method, path, jsonData);
        }

        function unauthenticated(method, path, data) {
            return $http({
                method: method,
                url: apiPath + path,
                data: data
            })
                .then(requestComplete)
                .catch(function (message) {
                    if (message.status === 400) {
                    } else if (message.status === 504){
                    }
                });

            function requestComplete(data, status, headers, config) {
                return data.data;
            }
        }
    }
})();