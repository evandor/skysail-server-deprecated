raw_content(user,converter,messages,model,request)::= <<
  <div class="container">
    <div class="row">
      <div class="col-md-16">
        <sky-content identifier="$model.resourceClassName$.message" content='' edit-base-url='$converter.editBaseUrl$' >
        $messages.("content.header").value$
        </sky-content>
      </div>
    </div>
    <div class="row">
    
    $if(model.form)$
      <div class="pure-g">
        <div class="pure-u-4-5">
          <form class="pure-form pure-form-aligned" action="$model.formTarget$" method="POST" id="form" name="form">
            <fieldset>
              $model.formfieldsWrapper.formfields: { ff | $raw_formElement(model,converter,ff)$ };separator="\n"$ 
              <div class="pure-controls">
                <button type="submit" name="submit" value="submit" class="pure-button pure-button-primary" >submit</button>
              </div>
            </fieldset>
          </form>
        </div>
        <div class="pure-u-1-5">
        <br>
        $raw_renderEntityInfo(model.formfields, messages, model)$
        </div>
      </div>

    $elseif(model.relationTargetList)$
 
      <div class="col-md-16" id="showRelationTargetFormDiv">
        <br>
        $showRelationTargetForm(model.formfields, messages, model)$
      </div>
 
    $elseif(model.list)$

      <div class="col-md-16" id="renderTableDiv">
        <br>
        <table class="pure-table">
          <tr>
            $model.formfieldsWrapper.formfields: { ff | $raw_thElement(model,ff)$ };separator="\n"$ 
	  	    <th>Actions</th>
          </tr>
          $model.data: { row | $raw_trElement(model.formfieldsWrapper.formfields,row)$ };separator="\n"$ 
        </table>
        <div class="row">
    	    <div class="col-md-16">
        	  $raw_menuNav(model.createFormLinks)$
    	    </div>
        </div>
      </div>
    
    $else$
      <div class="col-md-16" id="renderEntityDiv">
        <br>
        $raw_renderEntity(user, model)$
      </div>
    $endif$
    
    
      <div>
        <br>
        <hr>
            [<a href='$model.modelLink.uri$'>Application Model</a>]&nbsp;[...]
        <hr>
        <h5>Links</h5>
        <small><table  class="pure-table">
        	<tr><th>Title</th><th>URI</th><th>Alt</th><th>Rel</th><th>Verbs</th><th>needsAuthentication</th><th>role</th><th>refId</th><th>cls</th></tr>
        	$model.links: { link | 
        	<tr><td>$link.title$</td><td><a href='$link.uri$'>[$link.uri$]</a></td><td>$link.alt$</td><td>$link.relation$</td><td>$link.verbs$</td><td>$link.needsAuth$</td><td>$link.linkRole$</td><td>$link.refId$</td><td>$link.resourceClass$</td></tr>
        };separator="<br>\n"$</table></small>
        <h5>Form Fields</h5>
        <small><table  class="pure-table">
        	<tr><th>Label</th><th>id</th><th>name</th><th>inputType</th></tr>
        	$model.formfieldsWrapper.formfields: { row | 
        		<tr><td>$row.label$</td><td>$row.id$</td><td>$row.name$</td><td>$row.inputType$</td></tr>
        	};separator="<br>\n"$</table></small>
        <h5>Raw Data</h5>
        <small>$model.rawData: { row | $row$};separator="<br>\n"$</small>
        <h5>Data</h5>
        <small>$model.data: { row | $row$};separator="<br>\n"$</small>
      </div> 
        
      <div>
        <h3>Request</h3>
        $request$
      </div> 

      <div>
        <h3>User</h3>
        $user$
      </div> 

      <div>
        <h3>Converter</h3>
        $converter$
      </div> 

      <div>
        <h3>Messages</h3>
        $messages$
      </div> 
  
    
  </div>
  
</div>
>>