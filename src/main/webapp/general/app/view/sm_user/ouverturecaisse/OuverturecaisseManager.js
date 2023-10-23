

var Me;


Ext.define('testextjs.view.sm_user.ouverturecaisse.OuverturecaisseManager', {
    extend: 'Ext.form.Panel',
    xtype: 'ouverturecaissemanger',
    id: 'ouverturecaissemangerID',
    frame: true,
    title: 'Ouverture de Caisse',
    bodyPadding: 10,
    autoScroll: true,
    width: 420,
    fieldDefaults: {
        labelAlign: 'left',
        labelWidth: 115,
        msgTarget: 'side'
    },
    closable: false,
    initComponent: function () {
        Me = this;


        this.items = [{
                xtype: 'fieldset',
                title: 'INFORMATIONS TRANSACTIONS',
                defaultType: 'displayfield',
                defaults: {
                    anchor: '100%',
                    fieldStyle: "color:blue;font-size:1.5em;font-weight: bold;"
                },
                items: [
                    {
                        fieldLabel: 'NOM',
                        name: 'str_NAME_USER',
                        id: 'str_NAME_USER'
                    },
                    {
                        fieldLabel: 'MONTANT',
                        name: 'int_AMOUNT',
                        id: 'int_AMOUNT'
                    }
                ]
            },
            {
                xtype: 'fieldset',
                title: 'DETAIL DE LA TRANSACTION',
                defaultType: 'displayfield',
                defaults: {
                    fieldStyle: "color:blue;font-size:1.5em;font-weight: bold;",
                    anchor: '100%'
                },
                items: [
                    {
                        fieldLabel: 'DATE',
                        name: 'dt_CREATED',
                        id: 'dt_CREATED'
                    },
                    {
                        fieldLabel: 'ENVOY&Eacute; PAR',
                        name: 'ld_CREATED_BY',
                        id: 'ld_CREATED_BY'
                    },
                    {
                        xtype: 'hiddenfield',
                        name: 'ID_COFFRE_CAISSE',
                        id: 'ID_COFFRE_CAISSE'
                    }
                ]
            }];

        this.callParent();

        this.LoadData();
    },
    buttons: [{
            text: 'Valider',
            id: 'btn_validate',
            tooltip: 'Validation d\'ouverture de caisse',
            handler: function () {
                testextjs.app.getController('App').ShowWaitingProcess();
                Ext.Ajax.request({
                    method: 'PUT',
                    url: '../api/v1/caisse/validationfondcaisse/' + Ext.getCmp('ID_COFFRE_CAISSE').getValue(),

                    success: function (response)
                    {
                        testextjs.app.getController('App').StopWaitingProcess();
                        var object = Ext.JSON.decode(response.responseText, false);
                        if (!object.success) {
                            Ext.MessageBox.show({
                                title: "Message d'erreur",
                                width: 320,
                                msg: object.msg,
                                buttons: Ext.MessageBox.OK,
                                icon: Ext.MessageBox.WARNING
                            });
                            return;
                        }

                        Me.onbtnprint(object.mvtId);
                    },
                    failure: function (response)
                    {
                        testextjs.app.getController('App').StopWaitingProcess();
                        var object = Ext.JSON.decode(response.responseText, false);
                        console.log("Bug " + response.responseText);
                        Ext.MessageBox.alert('Error Message', response.responseText);

                    }
                });

            }
        }],
    LoadData: function () {
        Ext.Ajax.request({
            method: 'GET',
            url: '../api/v1/billetage/ouventure-data',
            success: function (response)
            {
                const object = Ext.JSON.decode(response.responseText, false);
                const caisse = object.data;
                Ext.getCmp('str_NAME_USER').setValue(caisse?.userFullName);
                Ext.getCmp('int_AMOUNT').setValue(Ext.util.Format.number(caisse?.amount, '0,000.') + " CFA");
                Ext.getCmp('ID_COFFRE_CAISSE').setValue(caisse?.id);
                Ext.getCmp('dt_CREATED').setValue(caisse?.createAt);
                Ext.getCmp('ld_CREATED_BY').setValue(caisse?.createdByFullName);
            },
            failure: function (response)
            {
                var object = Ext.JSON.decode(response.responseText, false);
                console.log(object);
                Ext.MessageBox.alert('Error Message', response.responseText);

            }

        });
    },
    onbtnprint: function (ref) {
        Ext.MessageBox.confirm('Message',
                'Confirmation de l\'impression du ticket',
                function (btn) {
                    if (btn == 'yes') {
                        Me.lunchPrinter(ref);

                    } else {
                        testextjs.app.getController('App').onLoadNewComponent(xtypeload, "", "");
                    }
                    testextjs.app.getController('App').onLoadNewComponent(xtypeload, "", "");
                });

    },
    lunchPrinter: function (str_REF) {

        Ext.Ajax.request({
            url: "../webservices/sm_user/mvtcaisse/ws_generate_pdf.jsp?str_REF=" + str_REF,
            success: function (response)
            {
                var object = Ext.JSON.decode(response.responseText, false);
                if (object.success == "0") {
                    Ext.MessageBox.alert('Error Message', object.errors);
                    return;
                }
                testextjs.app.getController('App').onLoadNewComponent(xtypeload, "", "");

            },
            failure: function (response)
            {
                testextjs.app.getController('App').onLoadNewComponent("ouverturecaisseempmanager", "Attribution Caisse Emp");
            }
        });
    }
});