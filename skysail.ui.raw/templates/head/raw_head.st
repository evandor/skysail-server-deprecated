raw_head(converter) ::= <<
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="description" content="skysail raw template viewer">

  <title>skysail raw viewer</title>

  <link rel="stylesheet" href="/_ui/raw/0.6.2/pure-min.css">
  
  <!--[if lte IE 8]>
    <link rel="stylesheet" href="/_ui/raw/0.6.2/layouts/side-menu-old-ie.css">
  <![endif]-->
  <!--[if gt IE 8]><!-->
    <link rel="stylesheet" href="/_ui/raw/0.6.2/layouts/side-menu.css">
  <!--<![endif]-->
  
  <!--[if lte IE 8]>
    <link rel="stylesheet" href="/_ui/raw/0.6.2/grids-responsive-old-ie-min.css">
  <![endif]-->
  <!--[if gt IE 8]><!-->
    <link rel="stylesheet" href="/_ui/raw/0.6.2/grids-responsive-min.css">
  <!--<![endif]-->
  
  <link rel="shortcut icon" href="/webapp/favicon.ico" type="image/x-icon" >
    
  <script type="text/javascript" src="/_polymer/bower_components/webcomponentsjs/webcomponents-lite.min.js"></script>
  
  <link rel="import"        href="/_ui/raw/sky-raw/imports.html">
  <!--<link rel="stylesheet"    href="/_ui/raw/sky-raw/skysail.css">-->
    
  <!-- UI Extensions from bundles (if any) -->
  $converter.uiPolymerExtensions: {extension | <link rel="import" href="$extension$">};separator="\n"$
>>