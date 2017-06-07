raw_breadcrumb(model) ::= <<
  <ol class="breadcrumb" style="margin-bottom:0px;padding:6px 20px 5px 0px;">
    $model.breadcrumbs:{ breadcrumb | $raw_handleSingleBreadcrumb(breadcrumb,i)$}$
  </ol>
>>