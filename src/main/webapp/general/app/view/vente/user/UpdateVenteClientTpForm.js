/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* global Ext */

Ext.define('testextjs.view.vente.user.UpdateVenteClientTpForm', {
    extend: 'Ext.window.Window',
    xtype: 'updateClientTpform',
    autoShow: false,
    height: 420,
    width: '80%',
    modal: true,
    title: 'Modification vente',
    closeAction: 'hide',
    closable: false,
    maximizable: true,
    layout: {
        type: 'fit'

    },
    requires: [
        'testextjs.store.caisse.RechercheClientAss'
    ],

    config: {
        venteId: null,
        client: null,
        clientId: null,
        ayantDroitId: null,
        compteClientTiersPayantId: null,
        tiersparent: null,
        ayantDroit: null,
        data: null,
        newVenteId: null,
        newData: null,
        typeVente: null,
        taux: null,
        message: null
    },
    initComponent: function () {
        var me = this;
        var venteId = me.getVenteId();
        Ext.applyIf(me, {
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'bottom',
                    ui: 'footer',
                    layout: {
                        pack: 'end',
                        type: 'hbox'
                    },
                    items: [
                        {
                            xtype: 'button',
                            itemId: 'btnAddClientAssurance',
                            text: 'Enregistrer',
                            handler: me.onSave
                        },
                        {
                            xtype: 'button',
                            iconCls: 'cancelicon',
                            itemId: 'btnCancelAssClient',
                            text: 'Annuler',
                            handler: me.closeWindow

                        }
                    ]
                }
            ],
            items: [
                {
                    xtype: 'form',
                    bodyPadding: 5,
                    modelValidation: true,
                    layout: {
                        type: 'vbox',
                        align: 'stretch'
                    },

                    items: [
                        {
                            xtype: 'fieldset',
                            collapsible: false,
                            bodyPadding: 5,
                            flex: 0.6,
                            title: '<span style="color:blue;">Information vente </span>',
                            layout: {
                                type: 'vbox', align: 'stretch'

                            },
                            items: [
                                {
                                    xtype: 'fieldcontainer',

                                    layout: {type: 'hbox'},
                                    items: [

                                        {
                                            xtype: 'displayfield',
                                            fieldLabel: 'Type vente:',
                                            flex: 1.5,
                                            labelWidth: 70,
//                                            hidden: true,
                                            itemId: 'strTYPEVENTE',
                                            name: 'strTYPEVENTE',
                                            fieldStyle: "color:blue;font-weight: bold;",
                                            margin: '0 10 0 0'
                                        },
                                        {
                                            xtype: 'displayfield',
                                            fieldLabel: 'Total vente:',
                                            flex: 0.8,
                                            labelWidth: 80,
                                            itemId: 'montantTotal',
                                            name: 'intPRICE',
                                            format: '0,000.',
                                            fieldStyle: "color:blue;font-weight: bold;",
                                            margin: '0 10 0 0'
                                        },
                                        {xtype: 'splitter'},
                                        {
                                            xtype: 'displayfield',
                                            fieldLabel: 'Part client:',
                                            flex: 0.8,
                                            labelWidth: 70,
                                            itemId: 'montantClient',
                                            name: 'intCUSTPART',
                                            format: '0,000.',
                                            fieldStyle: "color:blue;font-weight: bold;",
                                            margin: '0 10 0 0'
                                        },
                                        {
                                            xtype: 'displayfield',
                                            fieldLabel: 'Part tiers-payant:',
                                            flex: 1,
                                            itemId: 'montantCredit',
                                            name: 'montantCredit',
                                            format: '0,000.',
                                            fieldStyle: "color:blue;font-weight: bold;",
                                            margin: '0 10 0 0'
                                        },
                                        {
                                            xtype: 'displayfield',
                                            fieldLabel: 'Montant payé:',
                                            flex: 0.8,
                                            labelWidth: 90,
                                            itemId: 'montantPaye',
                                            name: 'montantPaye',
                                            format: '0,000.',
                                            fieldStyle: "color:blue;font-weight: bold;",
                                            margin: '0 10 0 0'
                                        },
                                        {
                                            xtype: 'displayfield',
                                            fieldLabel: 'Montant restant:',
                                            flex: 0.8,
                                            labelWidth: 100,
                                            itemId: 'montantRestant',
                                            name: 'montantRestant',
                                            format: '0,000.',
                                            fieldStyle: "color:blue;font-weight: bold;",
                                            margin: '0 10 0 0'
                                        }




                                    ]
                                }




                            ]
                        },

                        {
                            xtype: 'fieldcontainer',
                            layout: {type: 'hbox'},
                            flex: 1.2,
                            items: [
                                {
                                    xtype: 'fieldset',
                                    title: '<span style="color:blue;">INFOS ASSURE</span>',
                                    itemId: 'assureCmp',
                                    flex: 1,
                                    layout: {type: 'hbox'},
                                    items: [
                                        {
                                            xtype: 'container',

                                            flex: 1.5,
                                            layout: {type: 'vbox', align: 'stretch'},
                                            items: [

                                                {
                                                    xtype: 'displayfield',
                                                    fieldLabel: 'Nom :',
                                                    flex: 1,
                                                    itemId: 'nomAssure',
                                                    name: 'nomAssure',
                                                    fieldStyle: "color:blue;",
                                                    margin: '0 10 0 0'
                                                }, {
                                                    xtype: 'displayfield',
                                                    fieldLabel: 'Prénom(s):',
                                                    itemId: 'prenomAssure',
                                                    name: 'prenomAssure',
                                                    flex: 1,
                                                    fieldStyle: "color:blue;",
                                                    margin: '0 10 0 0'
                                                },
                                                {
                                                    xtype: 'displayfield',
                                                    fieldLabel: 'Matricule/SS:',
                                                    itemId: 'numAssure',
                                                    name: 'numAssure',
                                                    flex: 1,
                                                    fieldStyle: "color:blue;",
                                                    margin: '0 10 0 0'
                                                }

                                            ]

                                        },
                                        {
                                            xtype: 'container',
                                            flex: 0.5,
                                            layout: {type: 'vbox', align: 'middle'},
                                            items: [

                                                {
                                                    text: 'Modifier le client ',
                                                    itemId: 'btnModifierInfo',
                                                    xtype: 'button',
                                                    handler: me.changeClient

                                                }

                                            ]
                                        }
                                    ]

                                },
                                {
                                    xtype: 'fieldset',
                                    title: '<span style="color:blue;">INFOS AYANT DROIT</span>',
                                    itemId: 'ayantDroyCmp',
                                    flex: 1,
                                    layout: {type: 'hbox'},
                                    items: [
                                        {
                                            xtype: 'container',

                                            flex: 1.5,
                                            layout: {type: 'vbox', pack: 'start',
                                                align: 'middle'},
                                            items: [

                                                {
                                                    xtype: 'displayfield',
                                                    fieldLabel: 'Nom :',
                                                    flex: 1,
                                                    itemId: 'nomAyantDroit',
                                                    name: 'nomAyantDroit',
                                                    fieldStyle: "color:blue;",
                                                    margin: '0 10 0 0'
                                                }, {
                                                    xtype: 'displayfield',
                                                    fieldLabel: 'Prénom(s):',
                                                    itemId: 'prenomAyantDroit',
                                                    name: 'prenomAyantDroit',
                                                    flex: 1,
                                                    fieldStyle: "color:blue;",
                                                    margin: '0 10 0 0'
                                                },
                                                {
                                                    xtype: 'displayfield',
                                                    fieldLabel: 'Matricule/SS:',
                                                    itemId: 'numAyantDroit',
                                                    name: 'numAyantDroit',
                                                    flex: 1,
                                                    fieldStyle: "color:blue;",
                                                    margin: '0 10 0 0'
                                                }

                                            ]

                                        },
                                        {
                                            xtype: 'container',
                                            flex: 0.5,
                                            layout: {type: 'vbox', align: 'middle'},
                                            items: [

                                                {
                                                    text: 'Autre ayant droit',
                                                    itemId: 'btnModifierAyant',
                                                    xtype: 'button',
                                                    handler: me.changeAyantDroit

                                                }

                                            ]
                                        }
                                    ]

                                }
                            ]
                        },

                        {

                            xtype: 'fieldset',
                            title: '<span style="color:blue;">INFOS TIERS PAYANTS</span>',
                            itemId: 'tpContainer',
                            layout: {type: 'fit'},
                            flex: 1.2,
                            items: [
                                {
                                    layout: {type: 'hbox', align: 'stretch'},
                                    xtype: 'fieldcontainer',
                                    itemId: 'tpContainerform',
                                    border: 0,
                                    items: []
                                }
                            ]

                        }

                    ]



                }
            ]
        });
        me.callParent(arguments);
        me.loadVenteById(venteId);
    },

    buildtierspayant: function () {
        var me = this, tpContainerForm = me.down('form').down('#tpContainer').down('#tpContainerform');
        var tierspayants = me.getTiersparent();
//        var typeVente = me.getTypeVente();
        Ext.each(tierspayants, function (item) {
            var cmp = me.buildCmp(item);
            me.taux = item.taux;
            me.message = item.numBon;
            tpContainerForm.add(cmp);
        });


    },

    buildCmp: function (record, taux, numBon) {
        var percent = '40%';
        var me = this, typeVente = me.getTypeVente();
        if (typeVente === '3') {
            percent = '50%';
        }
        let _taux = record.taux;
        let _numBon = record.numBon;
        if (numBon) {
            _numBon = numBon;
        }
        if (taux) {
            _taux = taux;
        }
        var cmp = {
            xtype: 'container',
            width: percent,
            margin: '0 10 0 0',
            layout: {type: 'vbox', align: 'stretch'},
            items: [
                {
                    xtype: 'fieldcontainer',
                    layout: {type: 'hbox', align: 'stretch'},
                    items: [{
                            xtype: 'displayfield',
                            fieldLabel: 'TP',
                            flex: 1,
                            labelWidth: 30,
                            fieldStyle: "color:blue;",
                            value: record.tpFullName,
                            margin: '0 10 0 0'
                        }

                    ]
                }
                ,
                {
                    xtype: 'fieldcontainer',
                    layout: {type: 'hbox', align: 'stretch'},
                    items: [
                        {
                            xtype: 'textfield',
                            fieldLabel: 'Ref.Bon:',
                            allowBlank: true,
                            labelWidth: 50,
                            name: 'refBon',
                            itemId: 'refBon',
                            flex: 1,
//                            readOnly: true,
                            height: 30,
                            margin: '0 10 0 0',
                            value: _numBon,
                            listeners: {
                                afterrender: function (field) {
                                    field.focus(false, 100);
                                }
                            }
                        },
                        {
                            xtype: 'button',
                            text: 'Modifier le tiers-payant',
                            margin: '0 10 0 0',
                            handler: me.changeTiespayant
                        }
                    ]
                },

                {
                    xtype: 'fieldcontainer',
                    layout: {type: 'hbox', align: 'stretch'},
                    items: [

                        {
                            xtype: 'numberfield',
                            fieldLabel: 'Taux:',
                            flex: 1,
                            labelWidth: 50,
//                            height: 30,
                            hideTrigger: true,
                            name: 'taux',
                            itemId: 'taux',
//                            fieldStyle: "color:blue;",
                            value: _taux,
                            readOnly: true,
                            margin: '0 10 0 0',
                            listeners: {
                                afterrender: function (field) {
//                                    field.focus(false, 100);
                                }
                            }
                        },
                        {
                            xtype: 'button',
                            text: 'Retirer',
                            hidden: true,
                            flex: 0.7,
                            margin: '0 10 0 0',
                            style: 'background-color:#d9534f  !important;border-color:#d9534f  !important; background:#d9534f  !important;',
                            handler: function (btn) {
                                var cp = btn.up('fieldcontainer');
                                var container = cp.up('container');
//                                var compteTp = container.query('hiddenfield:first');
                                container.destroy();
                            }
                        }

                    ]
                },

                {
                    xtype: 'hiddenfield',
                    name: 'compteTp',
                    itemId: 'compteTp',
                    value: record.compteTp
                }



            ]
        };
        return cmp;
    },
    changeAyantDroit: function (btn) {
        let me = btn.up('window');
        var ayantStore = Ext.create('Ext.data.Store', {
            model: 'testextjs.model.caisse.AyantDroit',
            autoLoad: false,
            pageSize: null,
            proxy: {
                type: 'ajax',
                url: '../api/v1/client/ayant-droits',
                reader: {
                    type: 'json',
                    root: 'data',
                    totalProperty: 'total'
                }

            }
        });
        ayantStore.load({
            params: {"clientId": me.getClient().lgCLIENTID},
            callback: function (records, operation, successful) {
                if (successful) {
                    var form = Ext.create('Ext.window.Window',
                            {

                                autoShow: true,
                                height: 200,
                                width: 550,
                                modal: true,
                                title: "CHOISIR UN AYANT DROIT",
                                closeAction: 'hide',
                                closable: true,
                                maximizable: false,
                                bodyPadding: 10,
                                layout: {
                                    type: 'fit'

                                },
                                dockedItems: [
                                    {
                                        xtype: 'toolbar',
                                        dock: 'bottom',
                                        ui: 'footer',
                                        layout: {
                                            pack: 'end',
                                            type: 'hbox'
                                        },
                                        items: [
                                            {
                                                xtype: 'button',
                                                handler: function (btn) {
                                                    let parentForm = btn.up('window');
                                                    let ayantDroitRecord = parentForm.down('#lgAYANTSDROITSID').findRecord("lgAYANTSDROITSID", parentForm.down('#lgAYANTSDROITSID').getValue());

                                                    if (ayantDroitRecord) {
                                                        me.down('form').down('#nomAyantDroit').setValue(ayantDroitRecord.get('strFIRSTNAME'));
                                                        me.down('form').down('#numAyantDroit').setValue(ayantDroitRecord.get('strNUMEROSECURITESOCIAL'));
                                                        me.down('form').down('#prenomAyantDroit').setValue(ayantDroitRecord.get('strLASTNAME'));
                                                        me.ayantDroitId = ayantDroitRecord.get('lgAYANTSDROITSID');
                                                        me.ayantDroit = ayantDroitRecord.data;
                                                    }


                                                    form.destroy();

                                                },
                                                text: 'Valider'

                                            },
                                            {
                                                xtype: 'button',
                                                handler: function (btn) {
                                                    form.destroy();
                                                },
                                                text: 'Annuler'

                                            }
                                        ]
                                    }
                                ],
                                items: [
                                    {
                                        xtype: 'fieldset',
                                        title: 'LISTE DES CLIENTS',
                                        defaultType: 'textfield',
                                        defaults: {
                                            anchor: '100%'
                                        },
                                        items: [

                                            {
                                                xtype: 'combobox',
                                                fieldLabel: 'Ayant droits',
                                                flex: 1,
                                                height: 30,
                                                minChars: 2,
                                                itemId: 'lgAYANTSDROITSID',
                                                forceSelection: true,
                                                store: ayantStore,
                                                valueField: 'lgAYANTSDROITSID',
                                                displayField: 'fullName',
                                                queryMode: 'local',
                                                allowBlank: false,
                                                emptyText: 'Choisir un ayant droit...'

                                            }


                                        ]
                                    }

                                ]
                            });






                }
            }

        }

        );


    },
    changeClient: function (btn) {
        let me = btn.up('window');
        let url = '../api/v1/client/bytype/' + me.getTypeVente();
//        var clientStore = Ext.create('testextjs.store.caisse.RechercheClientAss');
        var clientStore = Ext.create('Ext.data.Store', {
            autoLoad: false,
            pageSize: null,
            model: 'testextjs.model.caisse.ClientAssurance',
            proxy: {
                type: 'ajax',
                url: url,
                reader: {
                    type: 'json',
                    root: 'data',
                    totalProperty: 'total'
                }
            }

        });
        var form = Ext.create('Ext.window.Window',
                {

                    autoShow: true,
                    height: 300,
                    width: 550,
                    modal: true,
                    title: "CHOISIR UN ASSURE",
                    closeAction: 'hide',
                    closable: true,
                    maximizable: false,
                    bodyPadding: 10,
                    layout: {
                        type: 'fit'

                    },
                    dockedItems: [
                        {
                            xtype: 'toolbar',
                            dock: 'bottom',
                            ui: 'footer',
                            layout: {
                                pack: 'end',
                                type: 'hbox'
                            },
                            items: [
                                {
                                    xtype: 'button',
                                    handler: function (btn) {
                                        let parentForm = btn.up('window');
                                        let clientRecord = parentForm.down('#lgCLIENTID').findRecord("lgCLIENTID", parentForm.down('#lgCLIENTID').getValue());
                                        if (clientRecord) {
                                            let tpRecord = parentForm.down('#compteTp').findRecord("compteTp", parentForm.down('#compteTp').getValue());
                                            let taux = parentForm.down('#taux').getValue();
                                            let ayantDroitRecord = parentForm.down('#lgAYANTSDROITSID').findRecord("lgAYANTSDROITSID", parentForm.down('#lgAYANTSDROITSID').getValue());
                                            me.down('form').down('#prenomAssure').setValue(clientRecord.get('strLASTNAME'));
                                            me.down('form').down('#nomAssure').setValue(clientRecord.get('strFIRSTNAME'));
                                            me.down('form').down('#numAssure').setValue(clientRecord.get('strNUMEROSECURITESOCIAL'));
                                            if (ayantDroitRecord) {
                                                me.down('form').down('#nomAyantDroit').setValue(ayantDroitRecord.get('strFIRSTNAME'));
                                                me.down('form').down('#numAyantDroit').setValue(ayantDroitRecord.get('strNUMEROSECURITESOCIAL'));
                                                me.down('form').down('#prenomAyantDroit').setValue(ayantDroitRecord.get('strLASTNAME'));
                                                me.ayantDroitId = ayantDroitRecord.get('lgAYANTSDROITSID');
                                                me.ayantDroit = ayantDroitRecord.data;
                                            } else {
                                                me.down('form').down('#nomAyantDroit').setValue('');
                                                me.down('form').down('#numAyantDroit').setValue('');
                                                me.down('form').down('#prenomAyantDroit').setValue('');
                                                me.ayantDroitId = null;
                                                me.ayantDroit = null;
                                            }
                                            me.tiersparent = tpRecord.data;
                                            var tpContainerForm = me.down('form').down('#tpContainer').down('#tpContainerform');
                                            tpContainerForm.removeAll();
                                            me.client = clientRecord.data;
                                            me.clientId = clientRecord.get('lgCLIENTID');
                                            var cmp = me.buildCmp(me.getTiersparent(), taux, me.getData().strREFBON);
                                            tpContainerForm.add(cmp);

                                            form.destroy();
                                        }
                                    },
                                    text: 'Valider'

                                },
                                {
                                    xtype: 'button',
                                    handler: function (btn) {
                                        form.destroy();
                                    },
                                    text: 'Annuler'

                                }
                            ]
                        }
                    ],
                    items: [
                        {
                            xtype: 'fieldset',
                            title: 'LISTE DES CLIENTS',
                            defaultType: 'textfield',
                            defaults: {
                                anchor: '100%'
                            },
                            items: [
                                {
                                    xtype: 'combobox',
                                    fieldLabel: 'Assurés',
                                    flex: 1,
                                    height: 30,
                                    minChars: 3,
                                    forceSelection: true,
                                    store: clientStore,
                                    itemId: 'lgCLIENTID',
                                    valueField: 'lgCLIENTID',
                                    displayField: 'fullName',
                                    queryMode: 'remote',
                                    allowBlank: false,
                                    emptyText: 'Choisir le client...',
                                    listeners: {
                                        select: function (field) {
                                            let parent = field.up('fieldset');
                                            let record = field.findRecord("lgCLIENTID", field.getValue());
                                            let   ayantDroits = record.get('ayantDroits');
                                            let  tiersPayants = record.get('tiersPayants');
                                            let ayantdroitCmp = parent.down("#lgAYANTSDROITSID");
                                            let tpCmp = parent.down("#compteTp");
                                            tpCmp.clearValue();
                                            ayantdroitCmp.clearValue();
                                            let newStore = Array.from(tiersPayants);
                                            let tpclientStore = new Ext.data.Store({
                                                model: 'testextjs.model.caisse.ClientTiersPayant',
                                                data: newStore,
                                                pageSize: null,
                                                autoLoad: false,
                                                proxy: {
                                                    type: 'memory',
                                                    reader: {
                                                        model: 'testextjs.model.caisse.ClientTiersPayant',
                                                        type: 'json'
                                                    }
                                                }
                                            });
                                            tpCmp.bindStore(tpclientStore);
                                            tpCmp.show();
                                            if (ayantDroits.length > 0) {
                                                let ayantDroisStore = Array.from(ayantDroits);
                                                var ayantStore = Ext.create('Ext.data.Store', {
                                                    model: 'testextjs.model.caisse.AyantDroit',
                                                    autoLoad: false,
                                                    pageSize: null,
                                                    data: ayantDroisStore,
                                                    proxy: {
                                                        type: 'memory',
                                                        reader: {
                                                            model: 'testextjs.model.caisse.AyantDroit',
                                                            type: 'json'
                                                        }

                                                    }
                                                });
                                                ayantdroitCmp.bindStore(ayantStore);
                                                ayantdroitCmp.show();
                                            }

                                        }
                                    }
                                },
                                {
                                    xtype: 'combobox',
                                    fieldLabel: 'Ayant droits',
                                    flex: 1,
                                    height: 30,
                                    minChars: 2,
                                    itemId: 'lgAYANTSDROITSID',
                                    hidden: true,
                                    forceSelection: true,
                                    store: null,
                                    valueField: 'lgAYANTSDROITSID',
                                    displayField: 'fullName',
                                    queryMode: 'local',
                                    allowBlank: false,
                                    emptyText: 'Choisir un ayant droit...'

                                },
                                {
                                    xtype: 'combobox',
                                    fieldLabel: 'Tiers-payants',
                                    flex: 1,
                                    height: 30,
                                    hidden: true,
                                    minChars: 2,
                                    forceSelection: true,
                                    store: null,
                                    itemId: 'compteTp',
                                    valueField: 'compteTp',
                                    displayField: 'tpFullName',
                                    queryMode: 'local',
                                    allowBlank: false,
                                    emptyText: 'Choisir un tiers-payant...',
                                    listeners: {
                                        select: function (field) {
                                            var parent = field.up('fieldset');
                                            var numberField = parent.down('numberfield');
                                            var record = field.findRecord("compteTp", field.getValue());
                                            slectedRecord = record;
                                            numberField.setValue(record.get('taux'));
                                            numberField.show();
                                            numberField.focus(false, 50);

                                        }
                                    }
                                },
                                {
                                    xtype: 'numberfield',
                                    fieldLabel: 'Pourcentage',
                                    itemId: 'taux',
                                    hidden: true,
                                    height: 30, flex: 1,
                                    allowDecimals: false,
                                    hideTrigger: true,
                                    allowBlank: false,
                                    minValue: 1,
                                    maxValue: 100,
                                    maskRe: /[1-100.]/,
                                    enableKeyEvents: true,
                                    listeners: {
                                        specialKey: function (field, e, options) {
                                            if (e.getKey() === e.ENTER) {

                                                form.destroy();

                                            }
                                        }
                                    }

                                }

                            ]
                        }

                    ]
                });

    },

    changeTiespayant: function (btn) {
        let me = btn.up('window');
        let url = '../api/v1/client/tierspayantsbytype/2';
        if (me.getTypeVente() === '2') {
            url = '../api/v1/client/tierspayantsbytype/1';
        }
        Ext.Ajax.request({
            method: 'GET',
            url: url,
//            params: {"clientId": me.getClient().lgCLIENTID},
            success: function (response, options) {
                var result = Ext.JSON.decode(response.responseText, true);
                if (result.success) {
                    me.tiersparent = result.data;
                    let tierspayants = result.data;
                    var newStore = Array.from(tierspayants);
                    var tpclientStore = new Ext.data.Store({
                        idProperty: 'lgTIERSPAYANTID',
                        fields: [
                            {name: 'lgTIERSPAYANTID', type: 'string'},
                            {name: 'strFULLNAME', type: 'string'}
                        ],
//                        model: 'testextjs.model.caisse.ClientTiersPayant',
                        data: newStore,
                        pageSize: null,
                        autoLoad: false,
                        proxy: {
                            type: 'memory',
                            reader: {
//                                model: 'testextjs.model.caisse.ClientTiersPayant',
                                type: 'json'
                            }
                        }
                    });

                    var form = Ext.create('Ext.window.Window',
                            {

                                autoShow: true,
                                height: 230,
                                width: 500,
                                modal: true,
                                bodyPadding: 10,
                                title: "TIERS-PAYANTS ASSOCIES",
                                closeAction: 'hide',
                                closable: true,
                                maximizable: false,
                                layout: {
                                    type: 'fit'

                                },
                                dockedItems: [
                                    {
                                        xtype: 'toolbar',
                                        dock: 'bottom',
                                        ui: 'footer',
                                        layout: {
                                            pack: 'end',
                                            type: 'hbox'
                                        },
                                        items: [
                                            {
                                                xtype: 'button',
                                                text: 'Valider',
                                                handler: function (_this) {
                                                    let parentForm = _this.up('window').down('form');
                                                    let tpRecord = parentForm.down('#compteTp').findRecord("lgTIERSPAYANTID", parentForm.down('#compteTp').getValue());

                                                    let numBon = parentForm.down('#numBon').getValue();
//                                                    me.tiersparent = tpRecord.data; 
                                                    var tpContainerForm = me.down('form').down('#tpContainer').down('#tpContainerform');
                                                    tpContainerForm.removeAll();
                                                    let obj = {"numBon": numBon, "tpFullName": tpRecord.data.strFULLNAME, "compteTp": tpRecord.data.lgTIERSPAYANTID, "taux": me.getTaux()};
                                                    let cmp = me.buildCmp(obj);
                                                    tpContainerForm.add(cmp);
                                                    form.destroy();
                                                }


                                            },
                                            {
                                                xtype: 'button',
                                                handler: function (btn) {
                                                    form.destroy();
                                                },
                                                text: 'Annuler'

                                            }
                                        ]
                                    }
                                ],
                                items: [{
                                        xtype: 'form',
                                        bodyPadding: 5,
                                        layout: {
                                            type: 'fit'

                                        },
                                        items: [
                                            {
                                                xtype: 'fieldset',
                                                title: 'Tiers-payans',
                                                bodyPadding: 5,
                                                defaultType: 'textfield',
                                                defaults: {
                                                    anchor: '100%'
                                                },
                                                items: [
                                                    {
                                                        xtype: 'combobox',
                                                        fieldLabel: 'Tiers-payant',
                                                        flex: 1,
                                                        height: 30,
                                                        minChars: 2,
                                                        forceSelection: true,
                                                        store: tpclientStore,
                                                        name: 'compteTp',
                                                        itemId: 'compteTp',
                                                        valueField: 'lgTIERSPAYANTID',
                                                        displayField: 'strFULLNAME',
                                                        queryMode: 'remote',
                                                        allowBlank: false,
                                                        emptyText: 'Choisir un tiers-payant...',
                                                        listeners: {
                                                            select: function (field) {
                                                                var parent = field.up('fieldset');
                                                                var numberField = parent.down('#numBon');
//                                                                var record = field.findRecord("lgTIERSPAYANTID", field.getValue());
                                                                numberField.setValue(me.getData().strREFBON);
                                                                numberField.focus(false, 50);
                                                            }
                                                        }
                                                    },

                                                    {
                                                        xtype: 'textfield',
                                                        fieldLabel: 'Num bon',
                                                        name: 'numBon',
                                                        itemId: 'numBon',
                                                        height: 30, flex: 1,
                                                        allowBlank: false,
                                                        minValue: 1,
                                                        enableKeyEvents: true,
                                                        listeners: {
                                                            specialKey: function (field, e, options) {
                                                                if (e.getKey() === e.ENTER) {
                                                                    let parentForm = field.up('window').down('form');
                                                                    let tpRecord = parentForm.down('#compteTp').findRecord("lgTIERSPAYANTID", parentForm.down('#compteTp').getValue());

                                                                    let numBon = parentForm.down('#numBon').getValue();
//                                                    me.tiersparent = tpRecord.data; 
                                                                    var tpContainerForm = me.down('form').down('#tpContainer').down('#tpContainerform');
                                                                    tpContainerForm.removeAll();
                                                                    let obj = {"numBon": numBon, "tpFullName": tpRecord.data.strFULLNAME, "compteTp": tpRecord.data.lgTIERSPAYANTID, "taux": me.getTaux()};
                                                                    let cmp = me.buildCmp(obj);
                                                                    tpContainerForm.add(cmp);
                                                                    form.destroy();


                                                                }
                                                            }
                                                        }

                                                    }

                                                ]
                                            }
                                        ]
                                    }

                                ]
                            });
                }

            }

        });
    },
    closeWindow: function () {
        let me = this;
        let wind = me.up('window');
        wind.destroy();
    },
    loadVenteById: function (id) {
        var me = this;
        Ext.Ajax.request({
            method: 'GET',
            url: '../api/v1/vente/find/infosclienttpforupdating',
            params: {"id": id},
            success: function (response, options) {
                var result = Ext.JSON.decode(response.responseText, true);
                if (result.success) {
                    me.data = result.data;
                    me.typeVente = me.getData().lgTYPEVENTEID;
                    let form = me.down('form');
                    let montantPaye = form.down('#montantPaye');
                    me.client = me.getData().client;
                    me.tiersparent = me.getData().tierspayants;
                    me.ayantDroit = me.getData().ayantDroit;
                    me.clientId = me.getClient().lgCLIENTID;
                    me.ayantDroitId = me.getAyantDroit().lgAYANTSDROITSID;
                    montantPaye.setValue(me.getData().montantPaye);
                    form.down('#strTYPEVENTE').setValue(me.getData().strTYPEVENTE);
                    form.down('#montantCredit').setValue(me.getData().montantCredit);
                    form.down('#montantClient').setValue(me.getData().intCUSTPART);
                    form.down('#montantTotal').setValue(me.getData().intPRICE);
                    form.down('#montantRestant').setValue(me.getData().montantRestant);
                    form.down('#prenomAssure').setValue(me.getClient().strLASTNAME);
                    form.down('#nomAssure').setValue(me.getClient().strFIRSTNAME);
                    form.down('#numAssure').setValue(me.getClient().strNUMEROSECURITESOCIAL);
                    form.down('#nomAyantDroit').setValue(me.getAyantDroit().strFIRSTNAME);
                    form.down('#numAyantDroit').setValue(me.getAyantDroit().strNUMEROSECURITESOCIAL);
                    form.down('#prenomAyantDroit').setValue(me.getAyantDroit().strLASTNAME);
                    me.buildtierspayant();
                }
            }

        });
    },
    buildTpData: function (me) {
        var tpContainerForm = me.down('form').down('#tpContainer').down('#tpContainerform');
        var items = tpContainerForm.items;
        var tierspayants = [];
        Ext.each(items.items, function (item) {
            if (item.items) {
                let numBonCmp = item.down('textfield:first');
                let tauxCmp = item.down('textfield:last');
                let compteTpCmp = item.down('hiddenfield:first');

                tierspayants.push(
                        {
                            "compteTp": compteTpCmp.getValue(),
                            "numBon": numBonCmp.getValue(),
                            "taux": parseInt(tauxCmp.getValue())
                        }
                );
            }


        });
        return tierspayants;
    },
    onSave: function (btn) {
        let wind = btn.up('window');
//        let tauxValue=
        let tierspayantArray = wind.buildTpData(wind);




        let data = {
            'clientId': wind.getClientId(),
            'ayantDroitId': wind.getAyantDroitId(),
            'venteId': wind.getData().lgPREENREGISTREMENTID,
            'tierspayants': tierspayantArray
        };
        var progress = Ext.MessageBox.wait('Veuillez patienter . . .', 'En cours de traitement!');
        Ext.Ajax.request({
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            url: '../api/v1/vente/updateclientortierpayant',
            params: Ext.JSON.encode(data),
            success: function (response, options) {
                progress.hide();
                var result = Ext.JSON.decode(response.responseText, true);
                if (result.success) {
                    Ext.MessageBox.show({
                        title: 'Impression du ticket',
                        msg: 'Voulez-vous imprimer le ticket ?',
                        buttons: Ext.MessageBox.YESNO,
                        fn: function (button) {
                            if ('yes' == button) {
                                wind.onPrintTicket(result.refId);
                            }

                        },
                        icon: Ext.MessageBox.QUESTION
                    });

                    wind.destroy();
                    var xtype = "ventemanager";
                    testextjs.app.getController('App').onRedirectTo(xtype, {});
                } else {
                    Ext.MessageBox.show({
                        title: 'Message d\'erreur',
                        width: 320,
                        msg: result.msg,
                        buttons: Ext.MessageBox.OK,
                        icon: Ext.MessageBox.ERROR

                    });
                }

            },
            failure: function (response, options) {
                progress.hide();
                Ext.Msg.alert("Message", 'Un problème avec le serveur');
            }
        });
//       
    },
    onPrintTicket: function (id) {
        var url = '../api/v1/vente/ticket/vo/' + id;

        var progress = Ext.MessageBox.wait('Veuillez patienter . . .', 'En cours de traitement!');
        Ext.Ajax.request({
            headers: {'Content-Type': 'application/json'},
            method: 'POST',
            url: url,
            success: function (response, options) {
                progress.hide();
                var result = Ext.JSON.decode(response.responseText, true);
            },
            failure: function (response, options) {
                progress.hide();
            }

        });
    }

});

