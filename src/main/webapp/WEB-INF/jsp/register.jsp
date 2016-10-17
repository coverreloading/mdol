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
    <meta charset="UTF-8">
    <link rel="stylesheet" href="${request.getContextPath()}/css/bootstrap.min.css">
    <script src="${request.getContextPath()}/js/jquery.min.js"></script>
    <script src="${request.getContextPath()}/js/bootstrap.min.js"></script>
    <script src="${request.getContextPath()}/js/angular.min.js"></script>
    <%-- 语法提示 用 --%>
    <%--<link rel="stylesheet" href="./css/bootstrap.min.css">--%>
    <%--<script src="./js/jquery.min.js"></script>--%>
    <%--<script src="./js/bootstrap.min.js"></script>--%>
    <%--<script src="./js/angular.min.js"></script>--%>
    <%-- 语法提示 END--%>
    <title>mdol注册</title>
</head>
<body>
<div ng-app="registApp" ng-controller="resgistCtrl" class="well"
     style="max-width: 400px; margin: 0 auto 10px; margin-top: 150px">
    <h1 align="center">mdol注册</h1>
    <form ng-submit="formSub()">
        <div class="form-group">
            <h3>邮箱</h3><input class="form-control bg-info " type="email" name="email" ng-model="email">
        </div>
        <div class="form-group">
            <h3>密码</h3><input class="form-control bg-info " name="password" type="password" ng-model="password">
        </div>
        <div class="form-group">
            <h3>密码</h3><input class="form-control bg-info " ng-model="user.passwordCheck">
        </div>
        <button class="btn btn-info btn-block btn-lg" type="submit">注册</button>
    </form>
    {{msg}}
</div>
</body>
<script>
    var app = angular.module("registApp", []);
    app.controller("resgistCtrl", function ($scope, $http, $timeout, $window) {
        $scope.formSub = function () {
            <%--$http.post("${request.getContextPath()}/user/regist", $scope.formData)--%>
            <%--.success(function (response) {--%>
            <%--if (response.status == 200) {--%>
            <%--alert(注册成功);--%>
            <%--}--%>
            <%--});--%>
            $http({
                method: 'POST',
                url: '${request.getContextPath()}/user/regist',
                data: "stringtest=11111&email=" + $scope.email + "&password=" + $scope.password,
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
            })
                    .success(function (data) {
                        console.log(data);
                        if (data.status == 200) {
                            $scope.msg = "注册成功,跳转到登录";
                            $timeout(function () {
                                <%--$location.path('${request.getContextPath()}/login');--%>
                                $window.location.href = '${request.getContextPath()}/login';
                            }, 1000);
                        } else {
                            $scope.msg = data.msg + "   一般情况下是有人要搞事";
                        }
                    });
        }
    });
</script>
</html>
