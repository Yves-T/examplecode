angular.module("myApp.controllers", []).controller("weatherController", function ($scope) {

    $scope.initiator = false;
    $scope.temperature = 0;

    $scope.socket = {
        client: null,
        stomp: null
    };

    $scope.notify = function (/** Message */ message) {
        var jsonObject = JSON.parse(message.body);
        console.log(jsonObject);
        console.log(jsonObject.temperature);
        $scope.temperature = jsonObject.temperature;
        $scope.$apply()

    };

    $scope.reconnect = function () {
        setTimeout($scope.initSockets, 10000);
    };

    $scope.initSockets = function () {
        $scope.socket.client = new SockJS('/hello');
        $scope.socket.stomp = Stomp.over($scope.socket.client);
        $scope.socket.stomp.connect({}, function () {
            $scope.socket.stomp.subscribe("/topic/notify", $scope.notify);
            $scope.socket.stomp.subscribe("/topic/data", $scope.notify);
        });
        $scope.socket.client.onclose = $scope.reconnect;
        console.log("init sockets....");
    };

    $scope.initSockets();
});