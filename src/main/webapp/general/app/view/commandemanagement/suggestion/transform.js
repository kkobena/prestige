var url_services_data_suggestion = '../webservices/commandemanagement/suggestion/ws_data.jsp';
// var url_services_transaction_suggestion = '../webservices/configmanagement/suggestion/ws_transaction.jsp?mode=';

Ext.define('testextjs.view.commandemanagement.suggestion.action.transform', {
    extend: 'Ext.window.Window',
    xtype: 'tranformsuggestion',
    id: 'tranformsuggestionID',
    requires: [
        'Ext.form.*',
        'Ext.window.Window',
        'testextjs.model.Suggestion'
    ],
    config: {
        odatasource: '',
        parentview: '',
        mode: '',
        titre: ''
    },
    initComponent: function () {

        Oview = this.getParentview();
        Omode = this.getMode();
        Me = this;
        var itemsPerPage = 20;

        var form = new Ext.form.Panel({
            bodyPadding: 10,
            fieldDefaults: {
                labelAlign: 'right',
                labelWidth: 160,
                msgTarget: 'side'
            },
            items: [{
                    xtype: 'fieldset',
                    title: 'Suggestion::Stock',
                    defaultType: 'textfield',
                    defaults: {
                        anchor: '100%'
                    },
                    items: [
                        // str_CODE
//                        {
//                            name: 'str_CODE',
//                            id: 'str_CODE',
//                            fieldLabel: 'Code',
//                            emptyText: 'Code',
//                            flex: 1,
//                            allowBlank: false
//                        },
                        // str_LIBELLE_CATEGORIE_AYANTDROIT
                        {
                            name: 'int_NUMBER',
                            id: 'int_NUMBER',
                            fieldLabel: 'Qte Suggeree',
                            emptyText: 'Qte Suggeree',
                            flex: 1,
                            allowBlank: false
                        }                        
                    ]
                }]
        });
        //Initialisation des valeur


        if (Omode === "update") {

            var ref = this.getOdatasource().lg_SUGGESTION_ID;
//            Ext.getCmp('str_CODE').setValue(this.getOdatasource().str_CODE);
            Ext.getCmp('int_NUMBER').setValue(this.getOdatasource().int_NUMBER);
        }



        var win = new Ext.window.Window({
            autoShow: true,
            title: this.getTitre(),
            width: 600,
            height: 300,
            minWidth: 300,
            minHeight: 200,
            layout: 'fit',
            plain: true,
            items: form,
            buttons: [{
                    text: 'Enregistrer',
                    handler: this.onbtnsave
                }, {
                    text: 'Annuler',
                    handler: function () {
                        win.close();
                    }
                }]
        });
    },
    onbtnsave: function () {

//        var internal_url = "";
//        if (Omode === "create") {
//            internal_url = url_services_transaction_suggestion + 'create';
//        } else {
//            internal_url = url_services_transaction_suggestion + 'update&lg_SUGGESTION_ID=' + ref;
//        }

//        Ext.Ajax.request({
//            url: internal_url,
//            params: {
//                
////                str_CODE: Ext.getCmp('str_CODE').getValue(),
//                int_NUMBER: Ext.getCmp('int_NUMBER').getValue()
//
//            },
//            success: function (response)
//            {
//                //alert("succes");
//                var object = Ext.JSON.decode(response.responseText, false);
//                if (object.success === 0) {
//                    Ext.MessageBox.alert('Infos Message', 'CREER AVEC SUCCES');
//                    return;
//                }else{
//                    Ext.MessageBox.alert('  Message', object.errors);
//                }
//                Oview.getStore().reload();
//            },
//            failure: function (response)
//            {
//                //alert("echec");
//                var object = Ext.JSON.decode(response.responseText, false);
//                console.log("Bug " + response.responseText);
//                Ext.MessageBox.alert('  Message', response.responseText);
//            }
//        });
        this.up('window').close();
    }
});


