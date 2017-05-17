<div class="row">
<div class="col-md-2">
	<ul class="nav nav-pills nav-stacked">
		<li class="active">
			<a data-toggle="pill" href="#AdvancedContent">
				<g:message code="job.tab.advanced.label" default="advanced"/>
			</a>
		</li>
		<li>
			<a data-toggle="pill" id="chromeTabLink" href="#ChromeContent">
				<g:message code="job.tab.chrome.label" default="advanced"/>
			</a>
		</li>
		<li>
			<a data-toggle="pill" href="#AuthContent">
				<g:message code="job.tab.auth.label" default="auth"/>
			</a>
		</li>
		<li>
			<a data-toggle="pill" href="#BlockContent">
				<g:message code="job.tab.block.label" default="block"/>
			</a>
		</li>
		<li>
			<a data-toggle="pill" href="#SPOFContent">
				<g:message code="job.tab.spof.label" default="spof"/>
			</a>
		</li>
		<li>
			<a data-toggle="pill" href="#CustomContent">
				<g:message code="job.tab.custom.label" default="custom"/>
			</a>
		</li>
	</ul>
</div>

<div class="col-md-10">
	<div class="tab-content">
		<div id="AdvancedContent" class="tab-pane active">
			<g:render template="advancedTab" model="${['job': job]}" />
		</div>
		<div id="ChromeContent" class="tab-pane">
			<g:render template="chromeTab" model="${['job': job]}" />
		</div>
		<div id="AuthContent" class="tab-pane">
			<g:render template="authTab" model="${['job': job]}" />
		</div>
		<div id="BlockContent" class="tab-pane">
			<g:render template="blockTab" model="${['job': job]}" />
		</div>
		<div id="SPOFContent" class="tab-pane">
			<g:render template="spofTab" model="${['job': job]}" />
		</div>
		<div id="CustomContent" class="tab-pane">
			<g:render template="customTab" model="${['job': job]}" />
		</div>
	</div>
</div>
</div>