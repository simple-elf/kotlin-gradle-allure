<#ftl output_format="HTML">
<#-- @ftlvariable name="data" type="io.qameta.allure.attachment.http.HttpResponseAttachment" -->

<#if data.body??>
    <pre class="preformated-text">
    ${data.body}
    </pre>
</#if>