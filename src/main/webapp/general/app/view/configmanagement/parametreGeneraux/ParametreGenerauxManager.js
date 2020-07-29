var url_services_data_famille_famille = '../webservices/sm_user/famille/ws_data.jsp';
var url_services_transaction_famille = '../webservices/sm_user/famille/ws_transaction.jsp?mode=';
var url_services_transaction_app_ticket = '../webservices/configmanagement/famillearticle/ws_transaction_maxVente.jsp?mode=';
var url_services_data_max_app_ticket = '../webservices/configmanagement/famillearticle/ws_data_maxVente.jsp';
var Me_para;
Ext.define('testextjs.view.configmanagement.parametreGeneraux.ParametreGenerauxManager', {
    extend: 'Ext.form.Panel',
    xtype: 'ParametreGenerauxManager',
    id: 'ParametreGenerauxID',
    frame: true,
    title: 'Parametre generaux',
    bodyPadding: 10,
    autoScroll: true,
    width: '30%',
    closable: true,
    fieldDefaults: {
        labelAlign: 'right',
        labelWidth: 115,
        msgTarget: 'side'
    },
    initComponent: function() {
        Me_caisse = this;


        


        var lg_RESUME_CAISSE_ID = new Ext.form.field.Display(
                {
                    xtype: 'displayfield',
                    //allowBlank: false,
                    fieldLabel: 'Code',
                    name: 'lg_RESUME_CAISSE_ID',
                    id: lg_RESUME_CAISSE_ID,
                    emptyText: 'lg_RESUME_CAISSE_ID'
                });


        var int_AMOUNT = new Ext.form.field.Display(
                {
                    xtype: 'displayfield',
                    //allowBlank: false,
                    fieldLabel: 'Solde',
                    name: 'int_AMOUNT',
                    id: int_AMOUNT,
                    emptyText: 'int_AMOUNT'
                });

        this.items = [
               {
                xtype: 'fieldset',
                title: '',
                defaultType: 'textfield',
                defaults: {
                    anchor: '100%'
                },
                items: [
                    {
                    xtype:'checkbox',
                    id:'checkTicketId',
                    name:'checkTicketId',
                    fieldLabel: 'Activer l\'edition de ticket ',                    
                    scope: this,
                    handler: this.onCheckBoxClick
                }
                    //dt_CREATED,
                    //lg_CAISSE_ID,
                    //str_STATUT
                ]
            }];

        this.callParent();
        
        Ext.Ajax.request({
            url: url_services_data_max_app_ticket + "?ticket=ticket",
            success: function (response)
            {
                var object = Ext.JSON.decode(response.responseText, false);
                
                if(object.total === "1")
                {
                   // Ext.MessageBox.alert("Valeur Check",object.total);
                    Ext.getCmp('checkTicketId').setValue(true);
                }
                else
                {
                    Ext.getCmp('checkTicketId').setValue(false);
                }
                //Ext.getCmp('checkRemiseId').setValue(object.total);
           
                if (object.success === 0) {
                    Ext.MessageBox.alert('Error Message', object.errors);
                    return;
                }
            },
            failure: function (response)
            {

                var object = Ext.JSON.decode(response.responseText, false);
                console.log("Bug " + response.responseText);
                Ext.MessageBox.alert('Error Message', response.responseText);

            }
        });
    },
    buttons: [
        ],
        onCheckBoxClick:function()
    {   
        var internal_url = "";         
        bool_check = Ext.getCmp('checkTicketId').getValue();   
        //Ext.MessageBox.alert("checkRemiseId",bool_check);
        internal_url = url_services_transaction_app_ticket + 'update&ticket_check=' + bool_check;

        Ext.Ajax.request({
            url: internal_url,
            params: {
                //int_NUMBER_AVAILABLE: Ext.getCmp('int_NUMBER_AVAILABLE').getValue()          
               
            },
            success: function (response)
            {
                var object = Ext.JSON.decode(response.responseText, false);
                if (object.success === 0) {
                    //Ext.MessageBox.alert('Error Message', object.errors);
                    return;
                } else {
                    //Ext.MessageBox.alert('Confirmation', object.errors);
                }

            },
            failure: function (response)
            {

                var object = Ext.JSON.decode(response.responseText, false);
                console.log("Bug " + response.responseText);
                Ext.MessageBox.alert('Error Message', response.responseText);

            }
        });
    }
    
});

