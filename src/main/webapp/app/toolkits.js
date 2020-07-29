function showLoader(div_loader_zone){
    jQuery('#'+div_loader_zone ).html('<div align="center" class="loader" width="100%" ><img  src="resources/images/ajax-loader.gif" ></img></div>');
}
function hideLoader(div_loader_zone){
    jQuery('#'+div_loader_zone ).fadeOut();
}
function jsonp(url) {
    var head = document.head;
    var script = document.createElement("script");
    script.setAttribute("src", url);
    head.appendChild(script);
    head.removeChild(script);
}
function BuildAndDisplayErrorMessage(div_error_zone,message) {
   //jQuery('#'+div_error_zone ).html('<div align="center" class="loader" width="100%" >'+message+'</div>');

   alert(message);
 
}



