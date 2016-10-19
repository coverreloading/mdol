<%--
  Created by IntelliJ IDEA.
  User: Vincent
  Date: 16/9/16
  Time: 下午11:43
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE HTML>
<html style="overflow-y: hidden; ">
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
<body style="overflow-y: hidden; ">
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
                <a class="navbar-brand" href="main">MarkDownOL</a>
            </div>

            <form hidden="hidden" class="navbar-form navbar-right">
                <input type="text" class="form-control" value="搜索...">
            </form>
            <div class="navbar-default sidebar" role="navigation">
                <div>
                    <a href="#" class="list-group-item active" ng-click="addNote()">新建笔记</a>
                </div>
                <div style="overflow:scroll; overflow-x:hidden; height:{{noteListHeight}}px;background-color:#337ab7; ">
                    <div ng-repeat="note in notes">
                        <a class="list-group-item " ng-click="getNote(note.id)">{{note.name}}</a>
                    </div>
                </div>
            </div>
        </nav>
        <div id="page-wrapper">
            <div style="position: relative">
                <div class="editormd" id="test-editormd">
                    <textarea class="editormd-markdown-textarea"
                              name="test-editormd-markdown-doc">{{getNote}}</textarea>
                    <!-- 第二个隐藏文本域，用来构造生成的HTML代码，方便表单POST提交，这里的name可以任意取，后台接受时以这个name键为准 -->
                    <textarea class="editormd-html-textarea" name="text"></textarea>
                </div>
                <div style="width: 100px;height: 20px;position:absolute;right:15px;bottom: 45px;">
                    <a class="btn btn-info btn-block btn-lg" ng-click="updateNote(noteInActiveid)">保存</a>
                    {{user.email}}
                </div>
                <div style="width: 100px;height: 20px;position:absolute;right:15px;bottom: 100px;">
                    <a class="btn btn-info btn-block btn-lg" ng-click="delNote(noteInActiveid)">删除</a>
                </div>
                <div id="addSuccessMsg" hidden="hidden"
                     style="width: 100px;height: 20px;position:absolute;right:15px;bottom: 155px;">
                    <a class="btn  btn-block btn-lg" style="background-color: #5cb85c"><span
                            style="color: #d9edf7">新建成功</span></a>
                </div>
                <div id="updateSuccessMsg" hidden="hidden"
                     style="width: 100px;height: 20px;position:absolute;right:15px;bottom: 155px;">
                    <a class="btn  btn-block btn-lg" style="background-color: #5cb85c"><span
                            style="color: #d9edf7">保存成功</span></a>
                </div>
                <div id="delSuccessMsg" hidden="hidden"
                     style="width: 100px;height: 20px;position:absolute;right:15px;bottom: 155px;">
                    <a class="btn  btn-block btn-success btn-lg">删除成功</a>
                </div>
                <div hidden="hidden">
                    // 临时放置被选中笔记
                    {{noteInActiveid}}
                    <br/>
                    {{noteInActivename}}
                    <br/>
                    {{noteInActivedata}}
                    <br/>
                    {{noteInActivecreate}}
                    <br/>
                    {{noteInActiveupdate}}
                    <br/>
                    {{noteInActiveisshared}}
                    <br/>
                    {{noteInActiveisdel}}
                    <br/>
                    {{noteInActiveuserid}}
                    <br/>
                    {{check}}
                </div>
            </div>
        </div>
    </div>
</div>
</body>

<script type="text/javascript">
    // 页面高度
    var winowHeight = document.documentElement.clientHeight; //获取窗口高度

    winowHeight = winowHeight - 42;
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
    app.controller("noteCtrl", function ($scope, $http, $window, $timeout, $interval) {
        $scope.noteListHeight = winowHeight - 51;
        // 未登录跳转
        if ("${token}" == "") {
            $window.location.href = '${request.getContextPath()}/login';
        }
        // 获取所有笔记
        $scope.check = 0;
        var getAllNote = function () {
            $http({
                method: 'POST',
                url: '${request.getContextPath()}/note/getAllNote',
                data: "token=${token}&check=" + $scope.check,
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
            })
                    .success(function (data) {
                        console.log(data);
                        if (data.status == 200 || data.status == 500) {
                            $scope.notes = data.data;
                            $scope.check = 1;
                        }
                        else {
                            alert("session过期,请重新登录");
                            $window.location.href = '${request.getContextPath()}/login';
                        }
                    });
        };

        // 获取所有笔记
        $timeout(function () {
            initMdEditor();
            getAllNote();
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
                            getAllNote();
                            $('#addSuccessMsg').fadeIn("slow");
                            $('#addSuccessMsg').fadeOut(3000);
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
        $scope['getNote'] = function (noteId) {
            $http({
                method: 'POST',
                url: '${request.getContextPath()}/note/getNote',
                data: "token=${token}&noteId=" + noteId,
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
            })
                    .success(function (respon) {
                        console.log(respon);
                        if (respon.status == 200) {
                            testEditor.setMarkdown(respon.data.data);
                            $scope.noteInActiveid = noteId;
                            $scope.noteInActivename = respon.data.name;
                            $scope.noteInActivedata = respon.data.data;
                            $scope.noteInActivecreate = respon.data.create;
                            $scope.noteInActiveupdate = respon.data.updata;
                            $scope.noteInActiveisshared = respon.data.isshared;
                            $scope.noteInActiveisdel = respon.data.isdel;
                            $scope.noteInActiveuserid = respon.data.userid;

                            $interval(function () {
                                updateNoteFun(noteId);
                            }, 300000);

                        } else {
                            alert("session过期,请重新登录");
                        }
                    });
        }

        // 笔记删除方法
        $scope['delNote'] = function (noteId) {
            if (confirm("你确定删除笔记" + $scope.noteInActivename + "吗？")) {
                $http({
                    method: 'POST',
                    url: '${request.getContextPath()}/note/delNote',
                    data: "token=${token}&noteId=" + noteId,
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
                })
                        .success(function (respon) {
                            console.log(respon);
                            if (respon.status == 200) {
                                getAllNote();
                                $('#delSuccessMsg').fadeIn("slow");
                                $('#delSuccessMsg').fadeOut(3000);
                            } else {
                                alert("session过期,请重新登录");
                            }
                        });
            }
            else {

            }
        };


        // 笔记保存方法

        $scope['updateNote'] = updateNoteFun = function (noteId) {
            if (noteId == "") {
                alert("没有选中笔记");
                return;
            }
            $http({
                method: 'POST',
                url: '${request.getContextPath()}/note/updateNote',
                data: "token=${token}&id=" + noteId +
                "&data=" + testEditor.getMarkdown() +
                "&userid=" + $scope.noteInActiveuserid,
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
            })
                    .success(function (data) {
                        console.log(data);
                        if (data.status == 200) {
//                            $scope.check=0;
                            getAllNote();
                            $('#updateSuccessMsg').fadeIn("slow");
                            $('#updateSuccessMsg').fadeOut(3000);
//                            alert("保存成功");
                        } else {
                            alert("session过期,请重新登录");
                        }
                    });
        }


    });
</script>
</html>