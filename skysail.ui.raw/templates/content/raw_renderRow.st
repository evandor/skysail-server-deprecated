raw_renderRow(model, field, data) ::= <<
  <tr><th>name:$field.name$</th><td>&nbsp;<td>$data.(field.name)$</td><td>$raw_handleDates(model, data,field)$</td></tr>
>>