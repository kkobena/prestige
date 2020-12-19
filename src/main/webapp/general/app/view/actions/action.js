/* global Ext */
var boxProcess;
Ext.define('testextjs.view.actions.action', {
    extend: 'Ext.panel.Panel',
    xtype: 'kobysky',
    id: 'kobysky',

    frame: true,
    title: 'Menu personnel',
    width: 650,
    height: 350,
    bodyPadding: 10,
//    layout: 'vbox',
    layout: {
        type: 'vbox',
        align: 'center'
//        pack: 'start'
    },
    initComponent: function () {
        var me = this;
        me.items = me.buildItems();
        me.dockedItems = [
            {
                xtype: 'toolbar',
                dock: 'top',
                padding: '8',

                items: [

                    {
                        xtype: 'datefield',
                        fieldLabel: 'Du',
                        id: 'dt_action_start',
                        labelWidth: 17,
                        flex: 1,
                        name: 'start',
                        emptyText: 'Du',
                        submitFormat: 'Y-m-d',
                        value: new Date(),
                        format: 'd/m/Y',
                        listeners: {
                            change: function () {
                                Ext.getCmp('dt_end_action').setMinValue(this.getValue());
                            }
                        }


                    }

                    , {
                        xtype: 'tbseparator'
                    },
                    {
                        xtype: 'datefield',
                        fieldLabel: 'Au',
                        name: 'dt_end',
                        id: 'dt_end_action',
                        labelWidth: 17,
                        flex: 1,
                        emptyText: 'Au',
                        submitFormat: 'Y-m-d',
                        value: new Date(),
                        format: 'd/m/Y',
                        listeners: {
                            change: function () {
                                Ext.getCmp('dt_action_start').setMaxValue(this.getValue());
                            }
                        }


                    },

                    {
                        xtype: 'tbseparator'
                    },

                    {
                        xtype: 'button',
                        text: 'Afficher CA',
                        iconCls: 'searchicon',
                        width: 100,
                        listeners: {
                            click: function () {
                                me.getCa();
                            }
                        }

                    }


                ]
            }
        ],
                me.callParent(arguments);
        me.getCa();



    },
    getCa: function () {
        Ext.Ajax.request({
            url: '../custom',
            params: {
                action: 'getca',
                dt_start: Ext.getCmp('dt_action_start').getSubmitValue(),
                dt_end: Ext.getCmp('dt_end_action').getSubmitValue()


            },
            success: function (response)
            {
                var object = Ext.JSON.decode(response.responseText, false);
                Ext.getCmp('action_ca').setValue(object.CA);
            },
            failure: function (response)
            {

            }

        });
    },

    appliquer: function () {
        Ext.Ajax.request({
            url: '../custom',
            params: {
                action: 'getca',
                dt_start: Ext.getCmp('dt_action_start').getSubmitValue(),
                dt_end: Ext.getCmp('dt_end_action').getSubmitValue()


            },
            success: function (response)
            {
                var object = Ext.JSON.decode(response.responseText, false);
                Ext.getCmp('action_ca').setValue(object.CA);
            },
            failure: function (response)
            {

            }

        });
    },

    buildItems: function () {
        return [
            {
                xtype: 'fieldset',
                border: true,
                height: 50,
                layout: {
                    type: "hbox",
                    pack: "center",
                    align: "middle"
                },
                items: {
                    xtype: 'displayfield',
                    labelWidth: 170,
                    fieldLabel: 'Espèce Vente au comptant',
                    id: 'action_ca',
                    fieldStyle: "color:blue;font-weight:800;font-size:15px;",
                    renderer: function (value) {

                        return Ext.util.Format.number(value, '0,000') + " FCFA";

                    }
                }
            }
            ,

            {
                xtype: 'fieldset',

                layout: {
                    type: "hbox",
                    pack: "center",
                    align: "middle"
                },
                id: 'fixecontainer',
                height: 50,
                border: true,
                items: [
                    {
                        xtype: 'numberfield',
                        fieldLabel: "Montant",
                        emptyText: 'Montant',
                        name: 'fixedamount',
                        id: 'fixedamount',
                        minValue: 0,
                        step: 100,
                        labelWidth: 50,
                        flex: 1,
                        cls: 'custominput',
                        hideTrigger: true,
                        margins: '0 5 0 0',
                        listeners: {
                            change: function (e) {
                                var ca = Ext.getCmp('action_ca').getValue();
                                var _thisVal = e.getValue();
                                var percent = (Number(_thisVal) * 100) / Number(ca);
                                Ext.getCmp('fixedpercent').setValue(Ext.Number.toFixed(percent, 2));
                                Ext.getCmp('peramount').setValue(_thisVal);
                            }
                        }

                    },
                    {
                        xtype: 'displayfield',
                        fieldLabel: 'Pourcentage:',
                        fieldStyle: "color:blue;font-weight:800;font-size:15px;",
                        flex: 1,
                        value: 0,
                        labelWidth: 80,
                        margins: '0 5 0 20',
                        id: 'fixedpercent'
                    },
                    {
                        xtype: 'button',
                        text: "Appliquer",
                        listeners: {
                            "click": function (src) {
                                boxProcess = Ext.MessageBox.wait('Veuillez patienter . . .', 'En cours de traitement!');
                                Ext.Ajax.request({
                                    url: '../custom',
                                    params: {
                                        'action': 'finish',
                                        dt_start: Ext.getCmp('dt_action_start').getSubmitValue(),
                                        dt_end: Ext.getCmp('dt_end_action').getSubmitValue(),
                                        amount: Ext.getCmp('fixedamount').getValue(),
                                        ca: Ext.getCmp('action_ca').getValue()
                                    },
                                    success: function ( action) {
                                        boxProcess.hide();
                                        var object = Ext.JSON.decode(action.responseText, false);
                                        if (object.success === 1) {
                                            Ext.getCmp('nb').setValue(object.nb);
                                            Ext.getCmp('nb').show();
                                        }
                                    },
                                    failure: function (form, action) {
                                        boxProcess.hide();
                                        console.log(action.failureType);
                                        console.log(action.result);
                                        alert(action.response.status + ", " + action.response.statusText);
                                    }
                                });
//                                }
                            }
                        }


                    }
                ]
            },
            {
                xtype: 'fieldset',
                layout: {
                    type: "hbox",
                    pack: "center",
                    align: "middle"
                },
                border: true,
                height: 50,
                name: 'percentcontainer',
                items: [
                    {
                        xtype: 'numberfield',
                        fieldLabel: "Pourcentage",
                        emptyText: 'Pourcentage',
                        name: 'percentage',
                        id: 'percentage',
                        minValue: 0,
                        labelWidth: 80,
                        step: 0.1,
                        flex: 1,
                        cls: 'custominput',
                        hideTrigger: true,
                        margins: '0 5 0 0',
                        listeners: {
                            change: function (e) {
                                var ca = Ext.getCmp('action_ca').getValue();
                                var _thisVal = e.getValue();
                                var montant = (Number(_thisVal) * Number(ca)) / 100;
                                Ext.getCmp('percentamount').setValue(Ext.Number.toFixed(montant, 0));
                                Ext.getCmp('peramount').setValue(montant);


                            }
                        }

                    },
                    {
                        xtype: 'displayfield',
                        fieldLabel: 'Montant:',
                        flex: 1,
                        labelWidth: 50,
                        id: 'percentamount',
                        value: 0,
                        margins: '0 5 0 20',
                        fieldStyle: "color:blue;font-weight:800;font-size:15px;",
                        renderer: function (value) {

                            return Ext.util.Format.number(value, '0,000') + " FCFA";

                        }
                    },
                    {
                        xtype: 'hiddenfield',
                        name: 'peramount',
                        id: 'peramount'

                    },
                    {
                        xtype: 'button',
                        text: "Appliquer",
                        listeners: {
                            "click": function (src) {
                                boxProcess = Ext.MessageBox.wait('Veuillez patienter . . .', 'En cours de traitement!');
                                Ext.Ajax.request({
                                    url: '../custom',
                                    params: {
                                        'action': 'finish',
                                        amount: Ext.getCmp('percentamount').getValue(),
                                        ca: Ext.getCmp('action_ca').getValue(),
                                        dt_start: Ext.getCmp('dt_action_start').getSubmitValue(),
                                        dt_end: Ext.getCmp('dt_end_action').getSubmitValue()
                                    },
                                    success: function (action) {
                                        boxProcess.hide();
                                        var object = Ext.JSON.decode(action.responseText, false);

                                        if (object.success === 1) {
                                            Ext.getCmp('nb').setValue(object.nb);
                                            Ext.getCmp('nb').show();
                                        }
                                    },
                                    failure: function (form, action) {
                                        boxProcess.hide();
                                        console.log(action.failureType);
                                        console.log(action.result);

                                    }
                                });
//                                }
                            }
                        }


                    }
                ]
            },

            {
                xtype: 'fieldset',
                border: false,

                layout: {
                    type: "hbox",
                    pack: "center",
                    align: "middle"
                },
                items: {
                    xtype: 'displayfield',
                    id: 'nb',
                    hidden: true,
                    fieldStyle: "color:red;font-weight:800;font-size:15px;"

                }
            }
        ];
    }


});


