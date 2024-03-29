raw_handleReadonlyInputField(model, source, raw, field, messages) ::= <<
  $if(field.readonlyInputType)$
      $if(field.tagsInputType)$
        <div class='form-group'>
          <label for='s2id_$field.fieldAnnotation.name$' class='col-sm-1 control-label' style="text-align: left;">$field.fieldAnnotation.name$</label>
          <div class='col-sm-10'>
            <input type = 'hidden' name = '$field.fieldAnnotation.name$' id = '$field.fieldAnnotation.name$' value='$source.(field.id)$'/>
          </div>
        </div>
      $elseif(field.textareaInputType)$
        <div class='form-group'>
          <label for='$field.fieldAnnotation.name$' class='col-sm-1 control-label' style="text-align: left;">$helpmessage(messages, field.nameKey)$</label>
          <div class='col-sm-10'>
            <textarea class='form-control' rows=3 name='$field.fieldAnnotation.name$' id='$field.fieldAnnotation.name$'>$source.(field.id)$</textarea>
          </div>
        </div>
      $elseif(field.markdownEditorInputType)$
        <div class='form-group'>
          <label for='$field.fieldAnnotation.name$' class='col-sm-1 control-label' style="text-align: left;">$helpmessage(messages, field.nameKey)$</label>
          <div class='col-sm-10'>
            <textarea data-uk-htmleditor="{maxsplitsize:600, markdown:true}" name='$field.fieldAnnotation.name$' id='$field.fieldAnnotation.name$'>$source.(field.id)$</textarea>
          </div>
        </div>
      $elseif(field.dateType)$
        <div class='form-group'>
          <b>$helpmessage(messages, field.nameKey)$:</b><br>
          &nbsp;$source.(field.id)$
          <p class="text-muted">&nbsp;<script>document.write(moment("$source.(field.id)$", "$model.dateFormat$").fromNow());</script></p>
        </div>
      $else$
        <div class='form-group'>
          <b>$helpmessage(messages, field.nameKey)$:</b><br>
          &nbsp;$source.(field.id)$
        </div>
      $endif$
  $endif$
>>