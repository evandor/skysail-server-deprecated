raw_linkAsButton(link,i) ::= <<
    $if(link.showAsButtonInHtml)$
      <a href="$link.uri$?_f=$model.filterParamValueEncoded$" class="btn btn-default btn-xs $if(link.selfRelation)$active$endif$"
            $if(link.selfRelation)$ disabled="disabled" $endif$
            role="button" tabindex="1"
            alt="hi"
            style="width:90%; max-width:200px; margin-bottom: 9px;">
      $if(link.image)$
        <span class="glyphicon glyphicon-$link.image$" aria-hidden="true"></span>
      $endif$
      $link.title$
      </a>&nbsp;
    $endif$
>>
