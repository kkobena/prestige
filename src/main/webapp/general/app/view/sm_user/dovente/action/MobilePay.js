


/* global Ext, win */

//var Operator_Filter_Store = Ext.create('Ext.data.Store', {
//    
//    fields: ['name', 'value'],
//    data: [
//        {"value": "FLOOZ", "name": "MOOV"},
//        {"value": "MTN MONEY", "name": "MTN"},
//        {"value": "ORANGE MONEY", "name": "ORANGE"}
//    ]
//});

Ext.define('testextjs.view.sm_user.dovente.action.MobilePay', {
    extend: 'Ext.window.Window',
    modal: true,
    resizable: false,
    onEsc: Ext.emptyFn,
    width: '60%',
    autoShow: true,
    height: 380,
    autoHeight: true,
    title: 'Paiement Mobile',
    autoScroll: true,
    frame: true,
    requires: ['Ext.data.Store'],
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    items: [
        {
            xtype: 'form',
            reference: 'form',
            id: 'PaymentForm',
            bodyPadding: 5,
            border: false,
            modelValidation: true,
            layout: {
                type: 'hbox',
                align: 'stretch'
            },
            items: [
                {
                    xtype: 'fieldset',
                    flex: 1,
                    title: '',
                    bodyPadding: 10,
                    border: false,
                    layout: 'anchor',
                    defaults: {
                        labelAlign: 'middle',
                        labelWidth: 130
                    },
                    items: [
                        {
                            xtype: 'combo',
                            id: 'operatorID',
                            fieldLabel: 'Opérateur',
                            allowBlank: false,
                            //labelWidth: 70,
                            queryMode: 'local',
                            name: 'OPERATEUR',
                            width: '27%',
                            margin: '30 25 0 10',
                            store: ["FLOOZ", "MTN MONEY", "ORANGE MONEY"],
                            typeAhead: true,
                            emptyText: 'OPERATEUR...',
                            listeners: {
                                select: function (cmp) {
                                    Ext.getCmp('selectedOperatorID').setValue("Paiement Mobile par "+ cmp.getValue());
                                    Ext.getCmp('selectedOperatorID').setVisible(true);
                                    Ext.getCmp('merchantID').setVisible(true);
                                    Ext.getCmp('amountID').setVisible(true);
                                    Ext.getCmp('phoneNumberID').setVisible(true);
                                    Ext.getCmp('referenceID').setVisible(true);
                                }

                            }
                        },
                        {
                            xtype: 'displayfield',
                            id: 'selectedOperatorID',
                            margin: '20 0 10 10',
                            hidden: true
                           // labelWidth: 120,
                           // width: '50%'
                        },{
                            xtype: 'textfield',
                            id: 'merchantID',
                            margin: '10 0 10 10',
                            hidden: true,
                            fieldLabel: 'ID Marchant',
                            //labelWidth: 120,
                            width: '50%'
                        }
                        ,{
                            xtype: 'textfield',
                            id: 'phoneNumberID',
                            margin: '10 0 10 10',
                            hidden: true,
                            fieldLabel: 'Télephone',
                            //labelWidth: 120,
                            width: '50%'
                        }
                        ,{
                            xtype: 'textfield',
                            id: 'amountID',
                            margin: '10 0 10 10',
                            hidden: true,
                           // minValue: 0,
                            fieldLabel: 'Montant',
                           // labelWidth: 120,
                            width: '50%'
                        }
                        ,{
                            xtype: 'textfield',
                            id: 'referenceID',
                            margin: '10 0 10 10',
                            hidden: true,
                            fieldLabel: 'Référence',
                            //labelWidth: 120,
                            width: '50%'
                        }
                        

                    ]
                }


            ]
        }
    ],
    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'bottom',
            ui: 'footer',
            layout: {
                pack: 'end', //#22
                type: 'hbox'
            },
            items: [
                {
                    xtype: 'button',
                    text: 'Enregistrer',
                    glyph: 0xf067,
                    listeners: {
                        click: function (button) {

                            var form = Ext.getCmp('PaymentForm');
                            if (form && form.isValid()) {

                                form.submit({
                                    //clientValidation: true,
                                    //url: 'controllers/Main.php?action=updateCategory',
                                    //scope: this,
                                    success: function (form, action) {
                                        win.close();
                                    },
                                    failure: function (form, action) {

                                    }
                                });
                            }
                        }
                    }
                },
                {
                    xtype: 'button',
                    text: 'Annuler',
                    glyph: 0xf190,
//                   
                    listeners: {
                        click: function () {
                            win.close();
                        }
                    }
                }
            ]
        } // end
    ], 
    displayOperator: function(value){
        alert(value);
        return;
    }

});


