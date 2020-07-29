var url_transaction_caisse = "../webservices/sm_user/caisse/ws_transaction.jsp?mode=";
var Me_caisse;

Ext.define('testextjs.view.sm_user.caisse.CaisseManager', {
    extend: 'Ext.form.Panel',
    xtype: 'caissemanger',
    id: 'caissemangerID',
    frame: true,
    title: 'Cl&ocirc;ture de Caisse',
    bodyPadding: 10,
    autoScroll: true,
//    width: '30%',
    width: 400,
    fieldDefaults: {
        labelAlign: 'right',
        labelWidth: 115,
        msgTarget: 'side'
    },
    config: {
        nameintern: '',
        titre: ''
    },
    initComponent: function () {
        Me_caisse = this;
        var url_data = "../webservices/sm_user/caisse/ws_data_user_cloture.jsp";

        this.items = [
            {
                xtype: 'fieldset',
                title: 'INFORMATION CAISSE',
                defaultType: 'displayfield',
                defaults: {
                    fieldStyle: "color:blue;font-size:1.5em;font-weight: bold;",
                    anchor: '100%',
                    labelWidth: 120
                },
                items: [
                    {
                        fieldLabel: 'NOM',
                        name: 'str_NAME_USER',
                        id: 'str_NAME_USER'
                    },
                    {
                        fieldLabel: 'FOND DE CAISSE',
                        name: 'int_AMOUNT_FOND_CAISSE',
                        id: 'int_AMOUNT_FOND_CAISSE'
                    },
                    {
                        xtype: 'hiddenfield',
                        name: 'lg_RESUME_CAISSE_ID',
                        id: 'lg_RESUME_CAISSE_ID'
                    }
                ]
            },
            {
                xtype: 'fieldset',
                title: 'INFORMATIONS SUPPLEMENTAIRES',
                defaultType: 'displayfield',
                defaults: {
                    fieldStyle: "color:blue;font-size:1.5em;font-weight: bold;",
                    labelWidth: 120,
                    anchor: '100%'
                },
                items: [
                    {
                        fieldLabel: 'DATE',
                        name: 'dt_CREATED',
                        id: 'dt_CREATED'
                    },
                    {
                        fieldLabel: 'CODE',
                        name: 'lg_CAISSE_ID',
                        id: 'lg_CAISSE_ID'
                    }
                ]
            }];

        this.callParent();

        this.LoadData(url_data);
    },
    buttons: [
        {
            text: 'Cl&ocirc;turer',
            tooltip: 'Cl&ocirc;ture de caisse',
            hidden: true,
            id: 'btn_validate',
            handler: function () {
                Ext.MessageBox.confirm('CONFIRMATION',
                        'Voulez-vous cl&ocirc;turer votre caisse',
                        function (btn) {
                            if (btn === 'yes') {
                                Ext.MessageBox.confirm('CONFIRMATION',
                                        'Etes vous s&ucirc;r de vouloir cl&ocirc;turer la caisse',
                                        function (btn) {
                                            if (btn === 'yes') {
                                                testextjs.app.getController('App').ShowWaitingProcess();
                                                Ext.Ajax.request({
                                                    url: url_transaction_caisse + "close",
                                                    params: {
                                                        lg_RESUME_CAISSE_ID: Ext.getCmp('lg_RESUME_CAISSE_ID').getValue()
                                                    },
                                                    success: function (response)
                                                    {
                                                        testextjs.app.getController('App').StopWaitingProcess();
                                                        var object = Ext.JSON.decode(response.responseText, false);
                                                        if (object.success == 0) {
                                                            Ext.MessageBox.alert('Error Message', object.errors);
                                                            return;
                                                        }
                                                        Me_caisse.callBilletage_window();

                                                        //Ext.MessageBox.alert('Message', object.errors);
                                                    },
                                                    failure: function (response)
                                                    {
                                                        testextjs.app.getController('App').StopWaitingProcess();
                                                        var object = Ext.JSON.decode(response.responseText, false);
                                                        console.log("Bug " + response.responseText);
                                                        Ext.MessageBox.alert('Error Message', response.responseText);

                                                    }

                                                });
                                                return;
                                            }

                                        });
                                return;
                            }

                        });
            }
        }],
    LoadData: function (url) {
        Ext.Ajax.request({
            url: url,
            success: function (response)
            {

                var object = Ext.JSON.decode(response.responseText, false);
                if (object.errors_code == "0") {
                    Ext.MessageBox.show({
                        title: "Message d'erreur",
                        width: 320,
                        msg: object.errors,
                        buttons: Ext.MessageBox.OK,
                        icon: Ext.MessageBox.WARNING
                    });
                }
                var Caisse = object.results[0];
                Ext.getCmp('str_NAME_USER').setValue(Caisse.str_NAME_USER);
                Ext.getCmp('int_AMOUNT_FOND_CAISSE').setValue(Ext.util.Format.number(Caisse.int_AMOUNT_FOND_CAISSE, '0,000.') + " CFA");
                Ext.getCmp('lg_RESUME_CAISSE_ID').setValue(Caisse.lg_RESUME_CAISSE_ID);
                Ext.getCmp('dt_CREATED').setValue(Caisse.dt_CREATED);
                Ext.getCmp('lg_CAISSE_ID').setValue(Caisse.lg_CAISSE_ID);
                if (Caisse.display == true) {
                    Ext.getCmp('btn_validate').show();
                }


            },
            failure: function (response)
            {
                var object = Ext.JSON.decode(response.responseText, false);
                Ext.MessageBox.alert('Error Message', response.responseText);

            }

        });
    },
    callBilletage_window: function () {
        new testextjs.view.sm_user.caisse.action.DoBilletage({
            odatasource: Ext.getCmp('lg_RESUME_CAISSE_ID').getValue(),
            parentview: this,
            mode: "dobilletage",
            titre: "BILLETAGE"
        });
    }
});
