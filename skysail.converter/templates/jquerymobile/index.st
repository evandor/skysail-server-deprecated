index(user, messages, converter, model,request) ::= <<
<!doctype html>
<html>
<head>

    <title>My Page</title>

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <!-- <link rel="shortcut icon" href="../favicon.ico"> -->
    <link rel="stylesheet" href="https://code.jquery.com/mobile/1.4.5/jquery.mobile-1.4.5.min.css">
    <link rel="stylesheet" href="http://fonts.googleapis.com/css?family=Open+Sans:300,400,700">

    <script src="https://code.jquery.com/jquery-2.1.4.min.js"></script>
    <script src="https://code.jquery.com/mobile/1.4.5/jquery.mobile-1.4.5.min.js"></script>
</head>

<body>
    <div data-role="page">

        <div data-role="header" style="overflow:hidden;">
          $if(user.principal)$
            <a href="javascript:history.back()" class="ui-btn-left">Back</a>
          $endif$
          <h1><a href='/'>$model.title$</a></h1>
          $if(user.principal)$
            <a href="/_logout" class="ui-btn-right">Logout</a>
          $else$
            <a href="/_login" data-icon="gear" class="ui-btn-right">Login</a>
          $endif$
        </div>

        <div role="main" class="ui-content">
$mainContentMobile(user, converter, messages, model)$
        </div>
    </div>
</body>
</html>
>>