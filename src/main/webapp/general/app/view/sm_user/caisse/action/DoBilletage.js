var url_transaction_dobilletage = '../webservices/sm_user/caisse/ws_transaction.jsp?mode=dobilletage';


var Me_dobilletage;
var ref_resume;



Ext.util.Format.decimalSeparator = ',';
Ext.util.Format.thousandSeparator = '.';
function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}


Ext.define('testextjs.view.sm_user.caisse.action.DoBilletage', {
    extend: 'Ext.window.Window',
    xtype: 'DoBilletage',
    id: 'DoBilletageID',
    requires: [
        'Ext.form.*',
        'Ext.window.Window',
        'testextjs.store.Statut',
        'Ext.grid.*',
        'Ext.layout.container.Column'
    ],
    config: {
        odatasource: '',
        o2ndatasource: '',
        parentview: '',
        mode: '',
        titre: '',
        obtntext: '',
        nameintern: '',
        from: ''
    },
    title: 'BILLETAGE',
    bodyPadding: 5,
    layout: 'column',
    closable: false,
    initComponent: function() {

        Me_dobilletage = this;

        ref_resume = Me_dobilletage.getOdatasource();

        //alert("ref_resume  " + ref_resume);
        var form = new Ext.form.Panel({
            bodyPadding: 5,
            fieldDefaults: {
                labelAlign: 'right',
                labelWidth: 115,
                msgTarget: 'side'
            },
            items: [
                {
                    xtype: 'container',
                    layout: 'vbox',
                    defaultType: 'textfield',
                    //margin: '0 0 15 0',
                    items: [
                        {
                            xtype: 'numberfield',
                            allowBlank: false,
                            regex: /[0-9.]/,
                            minValue: 0,
                            value: 0,
                            name: 'int_NB_DIX',
                            id: 'int_NB_DIX',
                            fieldLabel: '10.000 :',
                            flex: 1,
                            emptyText: '10.000',
                            listeners: {
                                change: function() {
                                    Me_dobilletage.getTotalBilletage();

                                }
                            }
                        }, {
                            xtype: 'numberfield',
                            allowBlank: false,
                            regex: /[0-9.]/,
                            minValue: 0,
                            value: 0,
                            name: 'int_NB_CINQ',
                            id: 'int_NB_CINQ',
                            fieldLabel: '5.000 :',
                            flex: 1,
                            emptyText: '5.000',
                            listeners: {
                                change: function() {
                                    Me_dobilletage.getTotalBilletage();

                                }
                            }
                        }, {
                            xtype: 'numberfield',
                            allowBlank: false,
                            regex: /[0-9.]/,
                            minValue: 0,
                            value: 0,
                            name: 'int_NB_DEUX',
                            id: 'int_NB_DEUX',
                            fieldLabel: '2.000 :',
                            flex: 1,
                            emptyText: '2.000',
                            listeners: {
                                change: function() {
                                    Me_dobilletage.getTotalBilletage();

                                }
                            }
                        }, {
                            xtype: 'numberfield',
                            allowBlank: false,
                            regex: /[0-9.]/,
                            minValue: 0,
                            value: 0,
                            name: 'int_NB_MIL',
                            id: 'int_NB_MIL',
                            fieldLabel: '1.000 :',
                            flex: 1,
                            emptyText: '1.000',
                            listeners: {
                                change: function() {
                                    Me_dobilletage.getTotalBilletage();

                                }
                            }
                        }, {
                            xtype: 'numberfield',
                            allowBlank: false,
                            regex: /[0-9.]/,
                            minValue: 0,
                            value: 0,
                            name: 'int_NB_CINQ_CENT',
                            id: 'int_NB_CINQ_CENT',
                            fieldLabel: '500 :',
                            flex: 1,
                            emptyText: '500',
                            listeners: {
                                change: function() {
                                    Me_dobilletage.getTotalBilletage();

                                }
                            }
                        }, {
                            xtype: 'numberfield',
                            allowBlank: false,
                            regex: /[0-9.]/,
                            minValue: 0,
                            value: 0,
                            name: 'int_NB_AUTRE',
                            id: 'int_NB_AUTRE',
                            fieldLabel: 'AUTRE :',
                            flex: 1,
                            emptyText: 'AUTRE',
                            listeners: {
                                'render': function(cmp) {
                                    cmp.getEl().on('keypress', function(e) {
                                        if (e.getKey() === e.ENTER) {
                                            Me_dobilletage.onbtnsave_dobilletage();

                                        }
                                    });
                                },
                                change: function() {
                                    Me_dobilletage.getTotalBilletage();

                                }
                            

                            }
                        },
                        {
                            xtype: 'displayfield',
                            fieldLabel: 'TOTAL',
                            name: 'int_TOTAL',
                            fieldStyle: "color:red;font-weight: bold; font-size:1.5em;",
                            id: 'int_TOTAL',
                            value: 0 + " CFA",
                            emptyText: 'Total saisi'
                        }
                    ]
                }

            ]
        });
        this.callParent();
        win_dobilletage = new Ext.window.Window({
            autoShow: true,
            id: 'do_billetage_ID',
            title: this.getTitre(),
            width: 400,
            height: 300,
            layout: 'fit',
            plain: true,
            items: form,
            buttons: [{
                    text: 'VALIDER',
                    //  hidden: true,
                    handler: function() {
                        Me_dobilletage.onbtnsave_dobilletage();
                    }
                }, {
                    text: 'Annuler',
                    hidden: true,
                    handler: function() {
                        win_dobilletage.close();
                    }
                }]

        });
    },
    onbtnsave_dobilletage: function() {

        if (Ext.getCmp('int_NB_DIX').getValue() === null && Ext.getCmp('int_NB_CINQ').getValue() === null && Ext.getCmp('int_NB_DEUX').getValue() === null && Ext.getCmp('int_NB_MIL').getValue() === null && Ext.getCmp('int_NB_CINQ_CENT').getValue() === null && Ext.getCmp('int_NB_AUTRE').getValue() === null) {
            Ext.MessageBox.alert('Attention', 'Renseignez au moins un champs svp');
            return;
        }




        internal_url = url_transaction_dobilletage;


        Ext.Ajax.request({
            url: internal_url,
            params: {
                lg_RESUME_CAISSE_ID: ref_resume,
                int_NB_DIX: Ext.getCmp('int_NB_DIX').getValue(),
                int_NB_CINQ: Ext.getCmp('int_NB_CINQ').getValue(),
                int_NB_DEUX: Ext.getCmp('int_NB_DEUX').getValue(),
                int_NB_MIL: Ext.getCmp('int_NB_MIL').getValue(),
                int_NB_CINQ_CENT: Ext.getCmp('int_NB_CINQ_CENT').getValue(),
                int_NB_AUTRE: Ext.getCmp('int_NB_AUTRE').getValue()
            },
            success: function(response)
            {
                var object = Ext.JSON.decode(response.responseText, false);
                if (object.success == 0) {
                    Ext.MessageBox.alert('Error Message', object.errors);
                    return;
                }

                Ext.MessageBox.alert('Message', object.errors);
                testextjs.app.getController('App').onLoadNewComponent(xtypeload, "", "");
               // testextjs.app.getController('App').onLoadNewComponent("mainmenumanager", "", "");
            },
            failure: function(response)
            {

                var object = Ext.JSON.decode(response.responseText, false);
                console.log("Bug " + response.responseText);
                Ext.MessageBox.alert('Error Message', response.responseText);

            }
        });

        win_dobilletage.close();



    },
    getTotalBilletage: function() {
        var int_NB_DIX = 0, int_NB_CINQ = 0, int_NB_DEUX = 0, int_NB_MIL = 0, int_NB_CINQ_CENT = 0, int_NB_AUTRE = 0, Total = 0;

        if (Ext.getCmp('int_NB_DIX').getValue() != null) {
            int_NB_DIX = Number(Ext.getCmp('int_NB_DIX').getValue()) * 10000;
        }
        if (Ext.getCmp('int_NB_CINQ').getValue() != null) {
            int_NB_CINQ = Number(Ext.getCmp('int_NB_CINQ').getValue()) * 5000;
        }
        if (Ext.getCmp('int_NB_DEUX').getValue() != null) {
            int_NB_DEUX = Number(Ext.getCmp('int_NB_DEUX').getValue()) * 2000;
        }

        if (Ext.getCmp('int_NB_MIL').getValue() != null) {
            int_NB_MIL = Number(Ext.getCmp('int_NB_MIL').getValue()) * 1000;
        }
        if (Ext.getCmp('int_NB_CINQ_CENT').getValue() != null) {
            int_NB_CINQ_CENT = Number(Ext.getCmp('int_NB_CINQ_CENT').getValue()) * 500;
        }
        if (Ext.getCmp('int_NB_AUTRE').getValue() != null) {
            int_NB_AUTRE = Number(Ext.getCmp('int_NB_AUTRE').getValue());
        }
        Total = int_NB_DIX + int_NB_CINQ + int_NB_DEUX + int_NB_MIL + int_NB_CINQ_CENT + int_NB_AUTRE;
        Ext.getCmp('int_TOTAL').setValue(Ext.util.Format.number(Total, '0,000.') + " CFA");
    }

});