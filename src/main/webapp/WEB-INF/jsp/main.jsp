<%--
  Created by IntelliJ IDEA.
  User: Vincent
  Date: 16/9/16
  Time: 下午11:43
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE HTML>
<html>
<head>
    <title>Home</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link href="css/bootstrap.min.css" rel='stylesheet' type='text/css'/>
    <link href="css/style.css" rel='stylesheet' type='text/css'/>
    <link href="css/custom.css" rel="stylesheet">
    <script src="editormd/js/jquery.min.js"></script>
    <!--<script src="js/jquery.min.js"></script>-->
    <script src="js/angular.min.js"></script>
    <!--mdEditor-->
    <script src="editormd/lib/marked.min.js"></script>
    <script src="editormd/lib/prettify.min.js"></script>
    <script src="editormd/editormd.min.js"></script>
    <!-- mdEditor-->
    <link rel="stylesheet" href="editormd/css/editormd.preview.min.css"/>
    <link rel="stylesheet" href="editormd/css/editormd.min.css"/>
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
                       onblur="if (this.value == '') {this.value = '搜索...';}">
            </form>
            <div class="navbar-default sidebar" role="navigation">
                <div>
                    <a href="#" class="list-group-item active" ng-click="addNote()">新建笔记</a>
                </div>
                <div style="overflow:scroll; overflow-x:hidden; height:{{noteListHeight}}px;">
                    <div ng-repeat="note in notes">
                        <a class="list-group-item " ng-click="getNote(note.id)">{{note.name}}</a>
                    </div>
                </div>
            </div>
        </nav>
        <div id="page-wrapper">
            <div>
                <div class="editormd" id="test-editormd">
                    <textarea class="editormd-markdown-textarea" name="test-editormd-markdown-doc">{{getNote}}</textarea>
                    <!-- 第二个隐藏文本域，用来构造生成的HTML代码，方便表单POST提交，这里的name可以任意取，后台接受时以这个name键为准 -->
                    <textarea class="editormd-html-textarea" name="text"></textarea>
                </div>
            </div>
        </div>
    </div>
</div>
</body>

<script type="text/javascript">
    // 页面高度
    var winowHeight = document.documentElement.clientHeight; //获取窗口高度

    winowHeight = winowHeight-51;
    $(function () {
        testEditor = editormd("test-editormd", {
            width: "100%",
            height: winowHeight,
            toc: true,
            //atLink    : false,    // disable @link
            //emailLink : false,    // disable email address auto link
            todoList: true,
            path: 'editormd/lib/'
        });
    });

    var text = null;
    var app = angular.module("noteApp", []);
    app.controller("noteCtrl", function ($scope, $http, $window, $timeout) {
        $scope.noteListHeight=winowHeight-51;
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
                            testEditor.setMarkdown(data.data.data);
                        } else {
                            alert("session过期,请重新登录");
                        }
                    });
        }
    });

</script>
