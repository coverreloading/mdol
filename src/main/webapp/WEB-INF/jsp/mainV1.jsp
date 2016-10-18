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
    <link href="css/bootstrap.min.css" rel='stylesheet' type='text/css'/>
    <link href="css/style.css" rel='stylesheet' type='text/css'/>
    <link href="css/custom.css" rel="stylesheet">
    <script src="js/jquery.min.js"></script>
    <script src="js/vue.js"></script>
    <script src="js/marked.min.js"></script>
    <script src="js/angular.min.js"></script>
    <style>
        <%--编辑器样式--%>
        html, body, #editor {
            margin: 0;
            height: 100%;
            font-family: 'Helvetica Neue', Arial, sans-serif;
            color: #333;
        }

        textarea, #editor div {
            display: inline-block;
            width: 49%;
            height: 100%;
            vertical-align: top;
            -webkit-box-sizing: border-box;
            -moz-box-sizing: border-box;
            box-sizing: border-box;
            padding: 0 20px;
        }

        textarea {
            border: none;
            border-right: 1px solid #ccc;
            resize: none;
            outline: none;
            background-color: #f6f6f6;
            font-size: 14px;
            font-family: 'Monaco', courier, monospace;
            padding: 20px;
        }

        code {
            color: #f66;
        }
    </style>
</head>
<body>
<div ng-app="noteApp" ng-controller="noteCtrl">
    <div id="wrapper">
        <!-- Navigation -->
        <nav class="top1 navbar navbar-default navbar-static-top" role="navigation"
             style="margin-bottom: 0;background-color: #337ab7">
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
                <div>
                    <a href="#" class="list-group-item active" ng-click="addNote()">新建笔记</a>
                </div>
                <div ng-repeat="note in notes">
                    <a class="list-group-item " ng-click="getNote(note.id)">{{note.name}}</a>
                </div>
                <div class="sidebar-nav navbar-collapse">
                    <%--<ul class="nav" id="side-menu">--%>
                    <li>
                        <ul>

                        </ul>
                    </li>

                    <%--</ul>--%>
                </div>
                <!-- /.sidebar-collapse -->
            </div>
            <!-- /.navbar-static-side -->
        </nav>
        <div id="page-wrapper">
            <div class="graphs">
                <%--<iframe id="mdpage" src="mdpage.html"></iframe>--%>
                <div id="editor">
                    <textarea v-model="input" debounce="300"></textarea>
                    <div v-html="input | marked"></div>
                </div>
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
            initMdEditor();
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
        // 编辑器初始化
        function initMdEditor(noteId) {
            $http.get("${request.getContextPath()}/note/defaultShow")
                    .success(function (data) {
                        if (data.status == 200) {
                            inputString = data.msg;
                            new Vue({
                                el: '#editor',
                                data: {
                                    input: inputString
                                },
                                filters: {
                                    marked: marked
                                }
                            })
                        }
                        else {
                            alert(data.msg);
                            // TODO
                        }
                    });
        };

        //TODO // 选择查看笔记

        $scope['getNote'] = function getNote(noteId) {
            $http({
                method: 'POST',
                url: '${request.getContextPath()}/note/getNote',
                data: "token=${token}&noteId=" + noteId,
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
            })
                    .success(function (data) {
                        console.log(data);
                        if (data.status == 200) {
                            inputString = data.msg;
                            new Vue({
                                el: '#editor',
                                data: {
                                    input: inputString
                                },
                                filters: {
                                    marked: marked
                                }
                            })
                        } else {
                            alert("session过期,请重新登录");
                        }
                    });
        }


    });


</script>
</html>
