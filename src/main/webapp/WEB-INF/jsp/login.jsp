<%--
  Created by IntelliJ IDEA.
  User: Vincent
  Date: 16/9/17
  Time: 上午12:00
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

<head>
    <link rel="stylesheet" href="${request.getContextPath()}/css/bootstrap.min.css">
    <script src="${request.getContextPath()}/js/jquery.min.js"></script>
    <script src="${request.getContextPath()}/js/bootstrap.min.js"></script>
    <script src="${request.getContextPath()}/js/angular.min.js"></script>
    <%-- 语法提示 用 --%>
    <link rel="stylesheet" href="./css/bootstrap.min.css">
    <script src="./js/jquery.min.js"></script>
    <script src="./js/bootstrap.min.js"></script>
    <script src="./js/angular.min.js"></script>
    <%-- 语法提示 END--%>
    <title>登录</title>
</head>
<body>
<div ng-app="registApp" ng-controller="resgistCtrl" class="well"
     style="max-width: 400px; margin: 0 auto 10px; margin-top: 150px">
    <h1 align="center">mdol登录</h1>
    <form ng-submit="formSub()">
        <div class="form-group">
            <h3>邮箱</h3><input class="form-control bg-info " type="email" name="email" ng-model="email">
        </div>
        <div class="form-group">
            <h3>密码</h3><input class="form-control bg-info " name="password" ng-model="password">
        </div>
        <button class="btn btn-info btn-block btn-lg" type="submit">登录</button>
    </form>
</div>
</body>
<script>
    var app = angular.module("registApp", []);
    app.controller("resgistCtrl", function ($scope, $http, $timeout, $window) {
        $scope.formSub = function () {
            $http({
                method: 'POST',
                url: '${request.getContextPath()}/user/login',
                data: "email=" + $scope.email + "&password=" + $scope.password,
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
            })
                    .success(function (data) {
                        console.log(data);
                        if (data.status == 200) {
                            $scope.msg = "登录成功,跳转到主页";
                            $timeout(function () {
                                $window.location.href = '${request.getContextPath()}/main';
                            }, 1000);
                        } else {
                            $scope.msg = "用户名或密码出错";
                        }
                    });
        }
    });
</script>
</html>
