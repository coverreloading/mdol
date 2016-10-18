<%--
  Created by IntelliJ IDEA.
  User: Vincent
  Date: 16/9/16
  Time: 下午11:43
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Home</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <!-- Bootstrap Core CSS -->
    <link href="css/bootstrap.min.css" rel='stylesheet' type='text/css'/>
    <!-- Custom CSS -->
    <link href="css/style.css" rel='stylesheet' type='text/css'/>
    <link href="css/self.css" rel='stylesheet' type='text/css'/>
    <!-- Graph CSS -->
    <link href="css/lines.css" rel='stylesheet' type='text/css'/>
    <link href="css/font-awesome.css" rel="stylesheet">
    <!-- jQuery -->
    <script src="js/jquery.min.js"></script>
    <!-- Nav CSS -->
    <link href="css/custom.css" rel="stylesheet">
    <!-- Metis Menu Plugin JavaScript -->
    <script src="js/metisMenu.min.js"></script>
    <script src="js/custom.js"></script>
    <!-- Graph JavaScript -->
    <script src="js/d3.v3.js"></script>
    <script src="js/rickshaw.js"></script>
    <%--<script src="${request.getContextPath()}/css/bootstrap.min.css"></script>--%>
    <script src="${request.getContextPath()}/js/angular.min.js"></script>
</head>
<body>
<div ng-app="noteApp" ng-controller="noteCtrl">
    <div id="wrapper">
        <!-- Navigation -->
        <nav class="top1 navbar navbar-default navbar-static-top" role="navigation" style="margin-bottom: 0;background-color: #31b0d5">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="index.html">Modern</a>
            </div>

            <form class="navbar-form navbar-right">
                <input type="text" class="form-control" value="Search..." onfocus="this.value = '';"
                       onblur="if (this.value == '') {this.value = 'Search...';}">
            </form>
            <div class="navbar-default sidebar" role="navigation">
                <div style="width: 250px;">
                    <button style="width: 250px;height: 60px;background-color: #31b0d5" ng-click="addNote()">新建笔记</button>
                </div>
                <div class="sidebar-nav navbar-collapse">
                    <%--<ul class="nav" id="side-menu">--%>
                        <li>
                            <ul class="nav nav-second-level">
                                <li ng-repeat="note in notes">
                                    <div style="width: 250px;height: 50px;">
                                        <a ng-click="getNote(note.id)" class="btn btn-info btn-block">{{note.name}}</a>
                                    </div>
                                </li>

                            </ul>
                        </li>

                    <%--</ul>--%>
                </div>
                <!-- /.sidebar-collapse -->
            </div>
            <!-- /.navbar-static-side -->
        </nav>
        <div class="copyrights">Collect from <a href="http://www.cssmoban.com/">网页模板</a></div>
        <div id="page-wrapper">
            <div class="graphs">
                <iframe id="mdpage" src="mdpage.html"></iframe>
                <div>{{ tokenShow }}</div>
            </div>
        </div>
        <!-- /#page-wrapper -->

    </div>
    <!-- /#wrapper -->
</div>


</body>
<script>
    var app = angular.module("noteApp", []);
    app.controller("noteCtrl", function ($scope, $http, $window, $timeout) {
        $scope.tokenShow = `${token}`;
        // 未登录跳转
        if ("${token}" == "") {
            $window.location.href = '${request.getContextPath()}/login';
        }
        // 获取所有笔记
        $timeout(function () {
            $http({
                method: 'POST',
                url: '${request.getContextPath()}/note/getAllNote',
                data: "token=${token}",
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
            })
                    .success(function (data) {
                        console.log(data);
                        if (data.status == 200) {
                            $scope.notes = data.data;
                            // $window.location.href = '${request.getContextPath()}/main';
                        } else {
                            alert("session过期,请重新登录");
                        }
                    });
        }, 1000);

        // 新建笔记函数
        $scope['addNote'] = function () {
            $http({
                method: 'POST',
                url: '${request.getContextPath()}/note/addNote',
                data: "token=${token}",
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
            })
                    .success(function (data) {
                        console.log(data);
                        if (data.status == 200) {
                            $window.location.href = '${request.getContextPath()}/main';
                        } else {
                            alert("session过期,请重新登录");
                        }
                    });
        }

        //TODO // 选择查看笔记
        $scope['getNote'] = function (noteId) {
            alert(noteId);
        };
    });
</script>
</html>
