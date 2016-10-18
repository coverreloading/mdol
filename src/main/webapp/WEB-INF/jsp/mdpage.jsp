<%--
  Created by IntelliJ IDEA.
  User: Vincent
  Date: 16/9/16
  Time: 下午11:42
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>mdpage</title>
    <script src="js/vue.js"></script>
    <script src="js/marked.min.js"></script>
    <script src="js/angular.min.js"></script>
    <style>
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

<body style="width: 100%;height: 100%" ng-app="mdApp" ng-controller="mdCtrl">
<div id="editor">
    <textarea v-model="input" debounce="300"></textarea>
    <div v-html="input | marked"></div>
</div>
</body>
<script>
    var inputString = null;
    var app = angular.module("mdApp", []);
    app.controller("mdCtrl", function ($scope, $http) {
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
    });

</script>

</html>