raw_renderEntity(user, model) ::= <<
  <table class="table">
    $model.formfieldsWrapper.formfields: {field | $raw_renderRow(model, field, first(model.data))$}$
  </table>
>>