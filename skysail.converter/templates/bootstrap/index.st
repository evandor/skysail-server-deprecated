index(user, messages, converter, model,request,response) ::= <<
<!DOCTYPE html>
<html lang="en">
  <head>
    $head()$
  </head>
  
  <body>
    $navbar(user, messages, converter, model)$
    $mainContent(user, converter, messages, model)$
    $footer(converter)$

    <!-- Bootstrap core JavaScript ================================================== -->
    <!-- Placed at the end of the document so the pages load faster 
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <script>window.jQuery || document.write('<script src="../../assets/js/vendor/jquery.min.js"><\/script>')</script>-->

    <script src="/webapp/js/bootstrap.min.js"></script>
    $notifications(converter.notifications)$
    
    $peity()$
    <script type="text/javascript">
        \$(".bar").peity("bar");

        \$(function () {
              \$('[data-toggle="tooltip"]').tooltip()
        });

      \$('.editable').jinplace();
    </script>

  </body>
</html>
>>