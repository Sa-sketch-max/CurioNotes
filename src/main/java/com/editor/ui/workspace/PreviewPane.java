package com.editor.ui.workspace;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;

public class PreviewPane extends StackPane {
    public final WebView webView;

    public PreviewPane(){
        webView = new WebView();

        getChildren().add(webView);

        webView.getEngine().loadContent("""
<!DOCTYPE html>
<html>

<head>

<style>

html, body{
    margin:0;
    padding:0;
    width:100%;
    height:100%;

    background:#1A1A1A;
    color:#ECECEC;

    font-family:"Segoe UI", sans-serif;
}

body{
    padding:30px;
}

h2{
    color:#FFFFFF;
}

p{
    color:#B8B8B8;
}

</style>

</head>

<body>

<h2>CurioNotes</h2>

<p>Start typing to see the preview.</p>

</body>

</html>
""");
    }
    public void setHtml(String html) {

        webView.getEngine().loadContent("""
        <!DOCTYPE html>
        <html>

        <head>

        <style>

        body{
            background:#1A1A1A;
            color:#ECECEC;
            font-family:"Segoe UI";
            padding:30px;
            line-height:1.6;
        }

        h1,h2,h3{
            color:white;
        }

        code{
            background:#2B2B2B;
            padding:2px 6px;
            border-radius:4px;
        }

        pre{
            background:#2B2B2B;
            padding:12px;
            border-radius:8px;
        }

        </style>

        </head>

        <body>

        """ + html + """

        </body>

        </html>
        """);

    }
    }

