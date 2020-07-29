/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.   
 */
var Str_Product_Name ="Produits";
var str_contour = "";
var str_contour_bloc = "";
var svgEntitiegrpe="";
var map_name="";
var int_INDEX="";

var attr = {
    fill: "#b1b1b1",
    stroke: "#000000",
    "stroke-width": 1,
    "font-size": '7',
    "stroke-linejoin": "round"
};

var attrEclip= {
    'stroke-width': '0',
    'stroke-opacity': '1',
    "font-size": '1',
    'fill': '#000000'
}


var tabColor;
var svgEntitieGlob="";
var table_source_child="";
var svgEntitie ="";
function jsonpCallbackColor(data){
    var obj = jQuery.parseJSON(JSON.stringify(data));
    tabColor = obj[0].results;

}





function jsonpCallback2imap(data) {
    var obj = jQuery.parseJSON(JSON.stringify(data));

    var tabResult = obj[0].results;
    map_name = obj[0].str_NAME;
    Str_Product_Name = obj[0].Str_Product_Name;
    str_contour = obj[0].str_contour;
    str_contour_bloc = obj[0].str_contour_bloc;
    //   jQuery('#datasProduits').html(Str_Product_Name);// Str_Product_Name
    //  jQuery('#Color').html(str_contour_bloc+"--"+str_contour);// Str_Product_Name
    html ="<tr>   <td align='left' width='20' bgcolor='"+str_contour_bloc+"'>&nbsp;</td> <td align='left'> "+map_name+"</td>  </tr>";
    html +="<tr> <td align='left' width='20' >&nbsp;</td> <td align='right'>  </td>  </tr>";
    html +="<tr> <td align='left' width='20' bgcolor='"+str_contour+"'>&nbsp;</td> <td align='left'> "+map_name+"</td>  </tr>";
    jQuery('#Color').html(html);// Str_Product_Name
   

    // alert(Str_Product_Name);
    jQuery('#map_name').html(map_name);
    str_contour = obj[0].str_contour;
    str_contour_bloc = obj[0].str_contour_bloc;
    var   str_contour_bloc_name = obj[0].str_contour_bloc_name;

    var str_bloc="MAP";


    //  html ="<tr> <td align='left' width='15' HEIGHT='10' bgcolor='"+str_contour+"'>&nbsp;</td> <td align='left'>&nbsp;<b> "+map_name+"</b></td>  </tr>";
    html ="<tr><td><hr style='width:30px' color="+str_contour+"></td><td align='left'>&nbsp;&nbsp;<b>"+map_name+"</b> </td>  </tr>";
    //html ="<hr style='width:50px' color='"+str_contour+>"'>&nbsp; &nbsp; "+map_name+";
    if(jQuery.trim(str_contour_bloc_name.toString()) != "MAP"){
        html +="<tr> <td align='left' width='10' >&nbsp;</td> <td align='right'>  </td>  </tr>";
        html +="<tr><td><hr style='width:30px' color="+str_contour_bloc+"></td><td align='left'>&nbsp;&nbsp;<b>"+str_contour_bloc_name+"</b> </td>  </tr>";

    //  html +="<tr>   <td align='left' width='15'  bgcolor='"+str_contour_bloc+"'>&nbsp;</td> <td align='left'>&nbsp;<b>"+str_contour_bloc_name+"</b></td>  </tr>";
    }



    jQuery('#Color').html(html);
    var id = "map_content";
    // jQuery('#'+id).fadeOut();
    if(obj[0].statut =="0"){
        jQuery('#map_content').html('<div align="center" width="100%">ERROR SYSTEM</div>');
    }else{

        jQuery('#map_content').html('<div align="center" id="crsr" width="100%"><div id="rsr"></div></div>');
        jQuery('#'+id).show();
        jQuery('#'+id).fadeIn();

     
       

        loadMap(tabResult,obj[0].str_CENTER);


    }

}


function getLoadLocalite(id){
 
    //alert('recharge cart'+id)
    //   jQuery('#map_content').html('<div align="center" width="100%" style="margin-top:50%"><img  src="resources/images/ajax-loader.gif" style="margin-top:50%"></img></div>');
    //   jsonp(url_service_ecap_map);
    jQuery('#DivdatasOflocalite').hide();
    jsonp(url_service_localite+"?search_value="+id);
    jQuery('#DivdatasOflocalite').show();
}


function getLoadLegende(id,option){
    lg_PRODUIT_ID = id;

    jQuery('#DivdatasLegende').hide();
    jsonp(url_service_gateway_legende+"?lg_PRODUIT_ID="+id);
    
    loadChildMap(svgEntitie,option,lg_PRODUIT_ID);
    jQuery('#DivdatasLegende').show();
}




function getLoadCartes(id){

    jQuery('#map_content').html('<div class="loader" align="center" width="100%"><img  src="resources/images/ajax-loader.gif" style="margin-top:200px"></img></div>');
    jsonp(url_service_ecap_map+"?NAV_METHODE=NAV_BAR&DIRECTION=JUMP&SOURCE=1&int_INDEX="+id);

}




function ReloadMapToLoadLocalite(id,option){
    //alert('ReloadMapToLoadLocalite'+"  "+id+'-->'+option)

    jQuery('#map_content').html('<div align="center" width="100%" style="margin-top:50%"><img  src="resources/images/ajax-loader.gif" style="margin-top:200px" ></img></div>');
    jsonp(url_service_ecap_map+"?KEY_LOCALITE="+id+"&"+option);
   
}


function jsonpCallbackTypeLocalite(data) {


    var obj = jQuery.parseJSON(JSON.stringify(data));
    var tabResult = obj[0].results;
    var id = "toolboxQueryContentItem";
    jQuery('#'+id).fadeOut();
    if(obj[0].statut =="0"){
        jQuery('#toolboxQueryContentItem').html('<div align="center" width="100%">ERROR SYSTEM</div>');
    }else{
        jQuery('#'+id).show();
        jQuery('#'+id).fadeIn();
        html = "";
        html += "<option value='tout'>Type de Localite</option>"
        for(var k_x= 0; k_x < tabResult.length; k_x++){
            html += "<option value='" + tabResult[k_x].lg_ID   + "'>" +tabResult[k_x].str_NAME+ "</option>"
        // html += "<input type='text' value='"+ tabResult[k_x].str_POSITION  + "'/>"
        
           
        }
        document.getElementById("datasTypeOflocalite").innerHTML = html;
    }
}



// recuperation de la listes des produits
function jsonpCallbackProduits(data) {
   

    var obj = jQuery.parseJSON(JSON.stringify(data));
    var tabResult = obj[0].results;
    var map = "1";

  
    //   jQuery('#map').html(map);

    var id = "toolboxQueryContentItem";
    jQuery('#'+id).fadeOut();
    if(obj[0].statut =="0"){
        jQuery('#toolboxQueryContentItem').html('<div align="center" width="100%">ERROR SYSTEM</div>');
    }else{
        jQuery('#'+id).show();
        jQuery('#'+id).fadeIn();
        html = "";
        //   html += "<option value='tout'>Produits</option>"
        html += "<option value='0'>" +Str_Product_Name+ "</option>"

        for(var k_x= 0; k_x < tabResult.length; k_x++){
            html += "<option value='" + tabResult[k_x].lg_PRODUIT_ID   + "'>" +tabResult[k_x].str_NAME+ "</option>"
        // html += "<input type='text' value='"+ tabResult[k_x].str_POSITION  + "'/>"
        //   alert(tabResult[k_x].lg_PRODUIT_ID)
        }
        //     alert(Str_Product_Name)  ;
        document.getElementById("datasProduits").innerHTML = html;
    }
}


function AddComponent_menu(component){
    jQuery("#dataschema").append(component+'<br><br>');
}

jQuery("#dataschemaconteneur").append("<div id='dataschema'  align='center' style='vertical-align: center' >");

//recuperation des schemas de carte
function jsonpCallbackTSchemaCarte(data) {
    var obj = jQuery.parseJSON(JSON.stringify(data));
    var tabResultTSchemaCarte = obj[0].results;
    for(var i= 0; i < tabResultTSchemaCarte.length; i++){
        AddComponent_menu('<a href="../common/index.html" >'+tabResultTSchemaCarte[i].str_DESCRIPTION+'</a>');
    }
}




//recuperation de la liste des cartes
function jsonpCallbackTMap(data) {


    var obj = jQuery.parseJSON(JSON.stringify(data));
    var tabResult = obj[0].results;
    var id = "toolboxQueryContentItem";
    jQuery('#'+id).fadeOut();
    if(obj[0].statut =="0"){
        jQuery('#toolboxQueryContentItem').html('<div align="center" width="100%">ERROR SYSTEM</div>');
    }else{
        jQuery('#'+id).show();
        jQuery('#'+id).fadeIn();
        html = "";
        //    alert(map_name);
        // html += "<option value='tout'>Liste des cartes</option>"
        // html += "<option value='tout'>"+map_name+"</option>"
        for(var k_x= 0; k_x < tabResult.length; k_x++){
            html += "<option value='" + tabResult[k_x].lg_ID   + "'>" +tabResult[k_x].str_NAME+ "</option>"
        // html += "<input type='text' value='"+ tabResult[k_x].str_POSITION  + "'/>"
        
        }
        document.getElementById("datasOfcarte").innerHTML = html;
    //  document.getElementById('datasOfcarte').selectedIndex =0;
    }
}



function jsonpCallbackLegend(data) {
    var obj = jQuery.parseJSON(JSON.stringify(data));
    var tabResult = obj[0].results;

    var id = "toolboxQueryContentItem";
   
    jQuery('#'+id).fadeOut();
    if(obj[0].statut =="0"){
        jQuery('#toolboxQueryContentItem').html('<div align="center" width="100%">ERROR SYSTEM</div>');
    }
    else{
        jQuery('#'+id).show();
        jQuery('#'+id).fadeIn();

        html ="";
        html = "<table width='190' border='0'><tr>";
        for(var k_x= 0; k_x < tabResult.length; k_x++){
            html +="<tr>   <td align='right' width='50' bgcolor='"+tabResult[k_x].str_COLOR+"'>&nbsp;</td> <td align='right'>"+tabResult[k_x].MIN_MAX+"</td>  </tr>";
        }
        html +="</table>";
     
        document.getElementById("datasLegend").innerHTML = html;
    }

}

function jsonpCallbackProduitsddd(data) {
   

    var obj = jQuery.parseJSON(JSON.stringify(data));
    var tabResult = obj[0].results;
    var map = obj[0].str_NAME;


    jQuery('#map').html(map);

    var id = "toolboxQueryContentItem";
    jQuery('#'+id).fadeOut();
    if(obj[0].statut =="0"){
        jQuery('#toolboxQueryContentItem').html('<div align="center" width="100%">ERROR SYSTEM</div>');
    }else{
        jQuery('#'+id).show();
        jQuery('#'+id).fadeIn();
        // html = "<select name='datasProduits' id='datasProduits'>";
		
        html += "<option value='tout'>Produit</option>"
        for(var k_x= 0; k_x < tabResult.length; k_x++){
            html += "<option value='" + tabResult[k_x].lg_PRODUIT_ID   + "'>" +tabResult[k_x].str_NAME+ "</option>"
         
        // html += "<input type='text' value='"+ tabResult[k_x].str_NAME  + "'/>"
      
        }
        html +="</select>"
    
		
        document.getElementById("DivdatasProduits").innerHTML = html;
    }
}

function jsonpCallbackLocalite(data) {

    var obj = jQuery.parseJSON(JSON.stringify(data));
    var tabResult = obj[0].results;
    var id = "toolboxQueryContentItem";
    jQuery('#'+id).fadeOut();
    if(obj[0].statut =="0"){
        jQuery('#toolboxQueryContentItem').html('<div align="center" width="100%">ERROR SYSTEM</div>');
    }else{
        jQuery('#'+id).show();
        jQuery('#'+id).fadeIn();
        html = "";
        // html += "<option value='tout'>Tout</option>"
        for(var k_x= 0; k_x < tabResult.length; k_x++){
            html += "<option value='" + tabResult[k_x].lg_ID   + "'>" +tabResult[k_x].str_NAME+ "</option>"
 
        }
        document.getElementById("datasOflocalite").innerHTML = html;
    }
}

function jsonp(url) {
    var head = document.head;
    var script = document.createElement("script");
 
    script.setAttribute("src", url);
    head.appendChild(script);
    head.removeChild(script);
}

function alignTop(t) {


    var b = t.getBBox();
    var h = Math.abs(b.y2) - Math.abs(b.y) + 1;

    t.attr({
        'y': b.y + h
    });
}
function loadMap(svgEntitie,str_CENTER){
    svgEntitieGlob = svgEntitie;
    
 
    var str_str_CENTERTab =str_CENTER.split(",");
    var X_c = parseInt(str_str_CENTERTab[0]);
    var Y_c = parseInt(str_str_CENTERTab[1]);
  
    var paper = new ScaleRaphael('rsr', '590', '600');
    var viewBoxWidth = paper.width;
    var viewBoxHeight = paper.height;
    var canvasID = "#paper";
    var startX,startY;
    var mousedown = false;
    var dX,dY;
    //var oX = 0, oY = 0, oWidth = viewBoxWidth, oHeight = viewBoxHeight;
    var oX = X_c, oY = Y_c, oWidth =500, oHeight =500;
    var viewBox = paper.setViewBox(oX, oY, viewBoxWidth, viewBoxHeight);
    viewBox.X = oX;
    viewBox.Y = oY;

    var vB = paper.rect(viewBox.X,viewBox.Y,viewBoxWidth,viewBoxHeight)
    .attr({
        /*stroke: "#000000",
        "stroke-width": 1*/
        });
    ;
    function handle(delta) {
        vBHo = viewBoxHeight;
        vBWo = viewBoxWidth;
        if (delta < 0) {
            viewBoxWidth *= 0.95;
            viewBoxHeight*= 0.95;
        }
        else {
            viewBoxWidth *= 1.05;
            viewBoxHeight *= 1.05;
        }

        viewBox.X -= (viewBoxWidth - vBWo) / 2;
        viewBox.Y -= (viewBoxHeight - vBHo) / 2;
        paper.setViewBox(viewBox.X,viewBox.Y,viewBoxWidth,viewBoxHeight);
    }

    function wheel(event){
        var delta = 0;
        if (!event)
            event = window.event;
        if (event.wheelDelta) {
            delta = event.wheelDelta/120;
        } else if (event.detail) {

            delta = -event.detail/3;
        }

        if (delta)
            handle(delta);

        if (event.preventDefault)
            event.preventDefault();
        event.returnValue = false;
    }

    if (window.addEventListener)
        window.addEventListener('DOMMouseScroll', wheel, false);

    window.onmousewheel = document.onmousewheel = wheel;

    var startX, startY, endX, endY;

    /*
    this.addEventListener("touchstart", function(e){
        
        startX = e.touches[0].pageX;
        startY = e.touches[0].pageY;
        e.preventDefault();//Stops the default behavior jj
    }, true);
    this.addEventListener("touchend", function(e){
    
        endX = e.touches[0].pageX;
        endY = e.touches[0].pageY;
        e.preventDefault();//Stops the default behavior
    }, true);
     */
    //Pane
    jQuery(canvasID).mousedown(function(e){
        if (paper.getElementByPoint( e.pageX, e.pageY ) != null) {
            return;
        }

        mousedown = true;
        startX = e.pageX;
        startY = e.pageY;
    });
    jQuery(canvasID).mousemove(function(e){
        if (mousedown == false) {

            return;
        }

        dX = startX - e.pageX;
        dY = startY - e.pageY;
        x = viewBoxWidth / paper.width;
        y = viewBoxHeight / paper.height;

        dX *= x;
        dY *= y;
    
        paper.setViewBox(viewBox.X + dX, viewBox.Y + dY, viewBoxWidth, viewBoxHeight);

    })

    jQuery(canvasID).mouseup(function(e){
        if ( mousedown == false ) return;
        viewBox.X += dX;
        viewBox.Y += dY;
        mousedown = false;

    });

    function resizePaper(){
        var win = jQuery(this);
        paper.changeSize(win.width(), win.height(), true, false);
    //   paper.changeSize("1500","700", true, false);
    //   alert(win.width()+","+ win.height());
    }
   
    resizePaper();
    jQuery(window).resize(resizePaper);

    var mycountry = {};
    // alert(parseInt(svgEntitie[0].int_Index));

    svgEntitiegrpe =svgEntitie;
    document.getElementById('datasOfcarte').selectedIndex =(parseInt(svgEntitie[0].int_Index)-1);
    int_INDEX =svgEntitie[0].int_Index;
    jQuery('#divtxtselectlocalite').html('</br><font size=2>'+""+'</font>');


    for(var k_x= 0; k_x < svgEntitie.length; k_x++)
    {

        var attrTextML = {
            "font-family": 'MyriadPro-Regular',
            "font-size": svgEntitie[k_x].str_SVG_FONT,
            'stroke-width': '0',
            'stroke-opacity': '1',
            'fill': '#000000'
        }


        var text_b = paper.text(50, 0, svgEntitie[k_x].Str_Libelle);
        text_b.attr(attrTextML);//
        //   alert(svgEntitie[k_x].Str_Libelle)
        text_b.transform(svgEntitie[k_x].Str_SVG_DATA_TEXT).data('id', 'text_b');


     
     

        var attrItemBloc = {
            fill: svgEntitie[k_x].str_COLOR_FILL,
            stroke: svgEntitie[k_x].Str_STROKE,
            "stroke-width": svgEntitie[k_x].str_STROKE_WIDTH,
            //"stroke-width": 1,
            // stroke="#FF0000",
            //  'stroke-opacity': '0.25',
            "font-size": '7',
            "stroke-linejoin": "round"
        };
        var attrItemItemBlocGroup = {
            //  fill: svgEntitie[k_x].str_COLOR_FILL,
            fill:"",
            stroke: svgEntitie[k_x].Str_STROKE,
            "stroke-width": svgEntitie[k_x].str_STROKE_WIDTH,
            // 'stroke-opacity': '1',
            "font-size": '7',
            "stroke-linejoin": "round"
        };



        if(svgEntitie[k_x].Str_TYPEBLOCK=="GROUPE"){
            
            attrItemBloc=attrItemItemBlocGroup;
        //  alert(attrItemBloc.stroke)


        }


        if(svgEntitie[k_x].str_id_zone_select==svgEntitie[k_x].Str_ID_LOCALITE){
            attrItemBloc = {
                //     fill: svgEntitie[k_x].str_COLOR_FILL,
                fill: "url(resources/images/fond.png)",
                // stroke: svgEntitie[k_x].Str_STROKE,
                //   fill:"#D26606",
               
                //  src: "resources/images/fond.png",
                // stroke:"#5D1907",
                "stroke-width": svgEntitie[k_x].Str_STROKE,
                // 'stroke-opacity': '1',
                "font-size": '7',
                "stroke-linejoin": "round"
            };
        /*   var attrItemBloc = paper.image("resources/images/fond.png", 10, 10,40, 40);
            attrItemBloc.attr({
                "clip-rect": "20,20,30,30"
            });
            
            path_4.attr({fill: "url(images/tiles.jpg)"});*/

        }
        mycountry.country = paper.path(svgEntitie[k_x].Str_SVG_DATA).attr(attrItemBloc);
        //    var svgEntitie_localite = svgEntitie[k_x];
        var svgEntitie_localite = svgEntitie[k_x];
        for(var y_x=0; y_x < svgEntitie_localite.tab_Localite.length; y_x++)
        {

            try
            {

                var notes= svgEntitie_localite.tab_Localite[y_x].str_ECLIPSE_Localite;
                     
                var str_ECLIPSE_LocaliteTab=notes.split(",");
                var r = parseInt(str_ECLIPSE_LocaliteTab[2])/1;

                var ellipse_c = paper.circle(parseInt(str_ECLIPSE_LocaliteTab[0]), parseInt(str_ECLIPSE_LocaliteTab[1]),r, parseInt(str_ECLIPSE_LocaliteTab[3]));
                var attrText = {
                    // "font-family": 'MyriadPro-Regular',
                    "font-size": svgEntitie_localite.tab_Localite[y_x].str_SVG_FONT,
                    'stroke-width': '0',
                    'stroke-opacity': '1',
                    'fill': '#000000'
                }

                ellipse_c.attr(attrEclip).data('id', 'ellipse_c');
                var text_d = paper.text(0, 0, svgEntitie_localite.tab_Localite[y_x].Str_Libelle_Localite);

                text_d.attr(attrText);
                //  str_ECLIPSE_Localite alert(svgEntitie_localite.tab_Localite[y_x].Str_Libelle_Localite+'__'+svgEntitie_localite.tab_Localite[1].Str_Libelle_Localite+'__'+svgEntitie_localite.tab_Localite.length);
                // alert(svgEntitie_localite.tab_Localite[y_x].Str_SVG_DATA_TEXT_Localite);
                var txtPosition = svgEntitie_localite.tab_Localite[y_x].Str_SVG_DATA_TEXT_Localite;
                var str_Position_LocaliteTab = txtPosition.split(" ");

                var x_val = str_Position_LocaliteTab[4];
                var y_val = str_Position_LocaliteTab[5];


                var txtPositionItemx_val = x_val;
                var str_Position_LocaliteTabItemx_val = txtPositionItemx_val.split(".");

                if(str_Position_LocaliteTabItemx_val.length == 1){
                    x_val =  parseInt(txtPositionItemx_val);
                }else{
                    x_val = parseInt(str_Position_LocaliteTabItemx_val[0]);
                    x_val = x_val+"."+str_Position_LocaliteTabItemx_val[1];
                }


                // x_val = (x_val+ 18);

                var txtPositionItemy_val = y_val;
                var str_Position_LocaliteTabItemy_val = txtPositionItemy_val.split(".");


                if(str_Position_LocaliteTabItemx_val.length == 1){
                    y_val = parseInt(txtPositionItemy_val);
                }else{
                    y_val = parseInt(str_Position_LocaliteTabItemy_val[0]);
                    y_val = y_val+"."+str_Position_LocaliteTabItemy_val[1];

                }


                svgEntitie_localite.tab_Localite[y_x].Str_SVG_DATA_TEXT_Localite =
                str_Position_LocaliteTab[0] +" "+
                str_Position_LocaliteTab[1]+" "+
                str_Position_LocaliteTab[2]+" "+
                str_Position_LocaliteTab[3]+" "+
                x_val+" "+
                y_val;
                text_d.transform(svgEntitie_localite.tab_Localite[y_x].Str_SVG_DATA_TEXT_Localite).data('id', 'text_d');

            }
            catch(err)
            {
        
                alert("Bug   "+err.message);


            }

        }

        mycountry.country.toBack();
        text_b.toFront();

        var current = null;
        for (var state in mycountry) {

            mycountry[state].color = "#996633";
            (function (st, state,refIntern) {
                var attrItemBlocItem = {
                    fill: svgEntitie[refIntern].str_COLOR_FILL,
                    stroke: svgEntitie[refIntern].Str_STROKE,
                    "stroke-width": svgEntitie[refIntern].str_STROKE_WIDTH,
                    "font-size": '7',
                    //   'stroke-opacity': '1',
                    "stroke-linejoin": "round"
                };
                var attrItemItemBlocGroup = {
                    fill:"",
                    stroke: svgEntitie[refIntern].Str_STROKE,
                    "stroke-width": svgEntitie[refIntern].str_STROKE_WIDTH,
                    // 'stroke-opacity': '0.25',
                    "font-size": '7',
                    "stroke-linejoin": "round"
                };

                if(svgEntitie[refIntern].Str_TYPEBLOCK=="GROUPE"){
                   
                    attrItemBlocItem=attrItemItemBlocGroup;
                }
                if(svgEntitie[refIntern].str_id_zone_select==svgEntitie[refIntern].Str_ID_LOCALITE){
                    attrItemBlocItem = {
                        // fill: svgEntitie[refIntern].str_COLOR_FILL,
                        //     fill: svgEntitie[k_x].str_COLOR_FILL,
                        fill: "url(resources/images/fond.png)",
                        "stroke-width":svgEntitie[refIntern].str_STROKE_WIDTH,
                        "font-size": '7',
                        "stroke-linejoin": "round"
                    };
                }

                st[0].style.cursor = "pointer";
                st[0].onmouseover = function () {
                    if(svgEntitie[refIntern].Str_TYPEBLOCK!="GROUPE"){
                        current && mycountry[current].animate({
                            fill: "#b1b1b1",
                            stroke: "#d5d5d5"
                        }, 300);
                        st.animate({
                            fill: st.color
                        // stroke: "#FFFFFF"
                        }, 300);
                        paper.safari();
                    }else{
                //alert("groupe")
                }
                };
                st[0].onmouseout = function () {
                    if(svgEntitie[refIntern].Str_TYPEBLOCK!="GROUPE"){
                        st.animate(attrItemBlocItem, 300);
                        paper.safari();
                    }else{
              
                }
                //  alert(attrItemBlocItem.stroke);
                };
          
                st[0].onclick = function () {
                    if(svgEntitie[refIntern].Str_TYPEBLOCK!="GROUPE"){
                        st.animate({
                            fill: "#b1b1b1",
                            stroke: "#FFFFFF"
                        }, 300);
                    }else{

                    }
                    if(svgEntitie[refIntern].str_SVG_REF!=null){

                    }
                    jQuery('#divtxtselectlocalite').html('</br><font size=2>'+svgEntitie[refIntern].Str_Libelle+'</font>');
                //  optioalert("clik ici")
                };
            })
            (mycountry[state], state,k_x);
        }
    }
    var rsrGroups = [];


// getLoadLegende("1");

}

//est charger la premiere fois
function loadChildMap(svgEntitie,option,lg_PRODUIT_ID){
    
    
    
    jQuery('#map_content').html('<div align="center" class="loader" width="100%" ><img  src="resources/images/ajax-loader.gif" ></img></div>');
    if(svgEntitie !== ""){
        //   jsonp(url_service_ecap_map+"?T_table_source="+svgEntitie.T_table_source+"&T_table_source_child="+svgEntitie.T_table_source_child+"&str_SVG_REF="+svgEntitie.str_SVG_REF+"&str_SVG_REF=%% &T_map_back="+svgEntitie.T_map_back+"&T_map_next="+svgEntitie.T_map_next+"&lg_PRODUIT_ID="+lg_PRODUIT_ID+"&"+option);


        jsonp(url_service_ecap_map+"?T_table_source="+svgEntitie.T_table_source+"&int_INDEX=1&T_table_source_child="+svgEntitie.T_table_source_child+"&str_SVG_REF="+svgEntitie.str_SVG_REF+"&str_SVG_REF=%% &T_map_back="+svgEntitie.T_map_back+"&T_map_next="+svgEntitie.T_map_next+"&lg_PRODUIT_ID="+lg_PRODUIT_ID+"&"+option);
        
    }else{
        //  alert(url_service_ecap_map+"?lg_PRODUIT_ID="+lg_PRODUIT_ID+"&"+option+"&int_INDEX="+int_INDEX);
        jsonp(url_service_ecap_map+"?lg_PRODUIT_ID="+lg_PRODUIT_ID+"&"+option+"&int_INDEX="+int_INDEX);


    }
};



function loadNext(option){
    //map_name

    jQuery('#map_content').html('<div align="center" width="100%" ><img  src="resources/images/ajax-loader.gif"></img></div>');
    if(option !== ""){
        jsonp(url_service_defil_map+"?"+option);
    }else{
        jsonp(url_service_defil_map+"?"+option);
    }
//alert(document.getElementById("datasOfcarte").length)
//document.getElementById('datasOfcarte').selectedIndex = 3;
}


function deleteAllCookies() {
    var cookies = document.cookie.split(";");
    for (var i = 0; i < cookies.length; i++) {
        var cookie = cookies[i];
        var eqPos = cookie.indexOf("=");
        var name = eqPos > -1 ? cookie.substr(0, eqPos) : cookie;
        document.cookie = name + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT";
    }
}
function loadPageByAjax(Mypage){
    deleteAllCookies();
    jQuery('#toolboxQueryContentItem').html('<div align="center" width="100%"><img  src="resources/images/ajax-loader.gif"></img></div>');
    jQuery.post(Mypage, {}, function(response){
        setTimeout("finishAjaxloadPageByAjax('toolboxQueryContentItem', '"+escape(response)+"')", 400);
    })
    internal_page = Mypage;
    return "";
}
function finishAjaxloadPageByAjax(id, response){
    jQuery('#'+id).remove();
    //  lastPostFunc = undefined;
    jQuery("div#toolboxQueryContent").append("<div id='toolboxQueryContentItem' >");
    jQuery('#'+id).html("");
    jQuery('#'+id).show();
    jQuery('#'+id).html(unescape(response));
    jQuery('#'+id).fadeIn();
    // return "";
    // NotificationManager();
    return "";
}

