raw_handleSingleBreadcrumb(bc,i) ::= <<
    <li>
    $if(bc.href)$
      <a href='$bc.href$'>$bc.value$</a>
    $else$
      <b>$bc.value$</b>
    $endif$
         $if(bc.favoriteEmpty)$<a href='?favorite=true'><span class="glyphicon glyphicon-star-empty" aria-hidden="true"></span></a>$endif$
         $if(bc.favoriteFull)$<a href='?favorite=false'><span class="glyphicon glyphicon-star" aria-hidden="true"></span></a>$endif$
    </li>
>>