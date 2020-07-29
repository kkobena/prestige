var OTUser;
//var intAmount = 0;  f 

var lg_ROLE_ID_REF = 0;
var url_services_data = '../webservices/configmanagement/ayantdroit/ws_generateAyanDroit_pdf.jsp';
//var url_services_transaction_myaccount = '../webservices/sm_user/myaccount/ws_transaction.jsp?mode=';
 

Ext.define('testextjs.view.configmanagement.ayantdroit.GenerateAyantDroitReport', {
    extend: 'Ext.form.Panel',
    xtype: 'rapportayantdroittest',
    id: 'rapportayantdroittestID',
    frame: true,
    title: 'Rapport ayant ddroit',
    closable: true,
    bodyPadding: 10,
    autoScroll: true,
    width: 355,
    fieldDefaults: {
        labelAlign: 'right',
        labelWidth: 115,
        msgTarget: 'side'
    },
    initComponent: function() {

        var str_FIRST_NAME = new Ext.form.field.Text(
                {
                    xtype: 'textfield',
                    allowBlank: false,
                    fieldLabel: 'Nom',
                    id: 'str_first_name',
                    name: 'str_FIRST_NAME',
                    emptyText: 'str_FIRST_NAME'
                });



        var str_LAST_NAME = new Ext.form.field.Text(
                {
                    xtype: 'textfield',
                    allowBlank: false,
                    fieldLabel: 'Prenom',
                    id: 'str_last_name',
                    name: 'str_LAST_NAME',
                    emptyText: 'str_LAST_NAME'
                });


        var str_LOGIN = new Ext.form.field.Display(
                {
                    xtype: 'displayfield',
                    //allowBlank: false,
                    fieldLabel: 'Login',
                    name: 'str_LOGIN',
                    id: str_LOGIN
                });
     
        var btn_Bouton_click = new Ext.button.Button(
                {
                    text : "Click",
                    id : "mybutton",
                    handler : function(){
                        var chaine = location.pathname;
                        var reg = new RegExp("[/]+", "g");
                        var tableau = chaine.split(reg);
                        var sitename = tableau[1];
                        var linkUrl = url_services_data;
                        //var linkUrl = url_services_data + '?lg_RETROCESSION_ID=' + ref;
                        alert(url_services_data);
                        //alert("Ok ca marche " + linkUrl);
                        window.open(linkUrl);
                        //var xtype = "";
                        //xtype = "retrocessionmanager";
                       // testextjs.app.getController('App').onLoadNewComponentWithDataSource(xtype, "", "", "");
                    }
                });

        var store = new Ext.data.Store({
            model: 'testextjs.model.Utilisateur',
            proxy: {
                type: 'ajax',
                url: url_services_data_myaccount
            }
        });        


        this.items = [{
                xtype: 'fieldset',
                title: 'Infos.Utilisateur',
                defaultType: 'textfield',
                defaults: {
                    anchor: '100%'
                },
                items: [
                    str_FIRST_NAME,
                    str_LAST_NAME,
                    str_LOGIN,
                    btn_Bouton_click
                ]
            }
            ];

        this.callParent();
    }
});