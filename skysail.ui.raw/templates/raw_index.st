raw_index(user, messages, converter, model, request) ::= <<
<!doctype html>
<html lang="en">
<head>
  $head/raw_head(converter)$
</head>
<body>

  <div id="layout">
    <a href="#menu" id="menuLink" class="menu-link">
        <span></span>
    </a>

    <div id="menu">
        <div class="pure-menu">
            <a class="pure-menu-heading" href="/">skysail</a>
            <br>
            <ul class="pure-menu-list">
                $model.applicationContextLinks: { link | <li class="pure-menu-item"><a href='$link.uri$'>$link.title$</a></li>}$
              <li class="pure-menu-item">&nbsp;</li>
              <li class="pure-menu-item"><a href='/_doc/swagger/2.0/api/$model.applicationName$/v1'>swagger (JSON)</a></li>
              <!--<li class="pure-menu-item"><a href='http://swagger.skysail.io?url=http://$model.host$/_doc/swagger/2.0/api/$model.applicationName$/v1'>swagger (UI)</a></li>-->
              <li class="pure-menu-item">&nbsp;</li>
            $if(user.principal)$
              <li class="pure-menu-item"><a href='/_logout?targetUri=/'>Logout</a></li>
            $else$
              <li class="pure-menu-item"><a href='/_login'>Login</a></li>
            $endif$
            </ul>
        </div>
    </div>

    <div id="main">
        <div class="header">
            <h2>$request.request$</h2>
            <h1>$model.resourceSimpleName$</h1>
            <h4>$model.resource.description$</h4>
            <h5>$model.responseStatusAsHtml$</h5>
        </div>
        
        <div class="pure-g">
          <div class="pure-u-lg-1-24">&nbsp;</div>
          <div class="pure-u-lg-22-24">$content/raw_content(user,converter,messages,model,request)$</div>
          <div class="pure-u-lg-1-24">&nbsp;</div>
        </div>

    </div>
  </div>

  <script src="/_ui/raw/0.6.2/js/ui.js"></script>

</body>
</html>







>>