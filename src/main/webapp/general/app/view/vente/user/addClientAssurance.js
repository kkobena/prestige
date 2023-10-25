/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* global Ext */

Ext.define('testextjs.view.vente.user.addClientAssurance', {
    extend: 'Ext.window.Window',
    xtype: 'addaddclientwindow',
    autoShow: false,
    height: 600,
    width: '70%',
    modal: true,
    title: 'Gestion des clients',
    closeAction: 'hide',
    closable: false,
    maximizable: true,
    layout: {
        type: 'fit'

    },
    initComponent: function () {
        var me = this;
        var villeStore = new Ext.data.Store({
            idProperty: 'lgVILLEID',
            fields: [
                {name: 'lgVILLEID', type: 'string'},
                {name: 'strName', type: 'string'}
            ],
            pageSize: null,
            autoLoad: true,
            proxy: {
                type: 'ajax',
                url: '../api/v1/common/villes',
                reader: {
                    type: 'json',
                    root: 'data',
                    totalProperty: 'total'
                }
            }
        });
        var clientTpStore = new Ext.data.Store({
            idProperty: 'lgTIERSPAYANTID',
            fields: [
                {name: 'lgTIERSPAYANTID', type: 'string'},
                {name: 'compteTp', type: 'string'},
                {name: 'tpFullName', type: 'string'},
                {name: 'numSecurity', type: 'string'},
                {name: 'order', type: 'number'},
                {name: 'bIsAbsolute', type: 'boolean'},
                {name: 'dbPLAFONDENCOURS', type: 'number'},
                {name: 'dblQUOTACONSOMENSUELLE', type: 'number'},
                {name: 'taux', type: 'number'},
                {name: 'canRemove', type: 'number', defaultValue: 0}

            ],
            pageSize: null,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: '../api/v1/client/tiers-payants-associes',
                reader: {
                    type: 'json',
                    root: 'data',
                    totalProperty: 'total'
                }
            }
        });


        var tierspayantss = new Ext.data.Store({
            idProperty: 'lgTIERSPAYANTID',
            fields: [
                {name: 'lgTIERSPAYANTID', type: 'string'},
                {name: 'strFULLNAME', type: 'string'}
            ],
            pageSize: null,
            autoLoad: true,
            proxy: {
                type: 'ajax',
                url: '../api/v1/client/tiers-payants/assurance',
                reader: {
                    type: 'json',
                    root: 'data',
                    totalProperty: 'total'
                }
            }
        });
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
                            text: 'Enregistrer'
                        },
                        {
                            xtype: 'button',
                            iconCls: 'cancelicon',
                            itemId: 'btnCancelAssClient',
                            text: 'Annuler'

                        }
                    ]
                }
            ],
            items: [
                {
                    xtype: 'form',
                    itemId: 'addaddclientwindowform',
                    bodyPadding: 2,
                    modelValidation: true,
                    layout: {
                        type: 'hbox',
                        align: 'stretch'
                    },
                    items: [

                        {
                            xtype: 'container',
                            flex: 1,
                            layout: {
                                type: 'vbox',
                                align: 'stretch'
                            },
                            items: [
                                {
                                    xtype: 'fieldset',
                                    collapsible: false,
                                    height: 160,
                                    bodyPadding: 5,
                                    title: 'Information sur le client',
                                    layout: {
                                        type: 'vbox',
                                        align: 'stretch'
                                    },
                                    items: [
                                        {
                                            xtype: 'fieldcontainer',
                                            flex: 1,
                                            layout: {type: 'hbox', align: 'stretch'},
                                            items: [
                                                {
                                                    xtype: 'textfield',
                                                    fieldLabel: 'Nom',
                                                    emptyText: 'Nom',
                                                    name: 'strFIRSTNAME',
                                                    itemId: 'strFIRSTNAME',
                                                    height: 30, flex: 1,
                                                    allowBlank: false,
                                                    enableKeyEvents: true,
                                                    listeners: {
                                                        afterrender: function (field) {
                                                            field.focus(false, 100);
                                                        }
                                                    }

                                                }, {xtype: 'splitter'},
                                                {
                                                    xtype: 'textfield',
                                                    fieldLabel: 'Prénom',
                                                    emptyText: 'Prénom',
                                                    name: 'strLASTNAME',
                                                    height: 30, flex: 1,
                                                    allowBlank: false,
                                                    enableKeyEvents: true

                                                },
                                                {
                                                    xtype: 'hiddenfield',
                                                    name: 'compteTp',
                                                    allowBlank: true
                                                }

                                            ]
                                        },
                                        {
                                            xtype: 'fieldcontainer',
                                            flex: 1,
                                            layout: {type: 'hbox', align: 'stretch'},
                                            items: [
                                                {xtype: 'textfield',
                                                    fieldLabel: 'Matricule/SS',
                                                    emptyText: 'Numéro de matricule ',
                                                    name: 'strNUMEROSECURITESOCIAL',
                                                    height: 30, flex: 1,
                                                    allowBlank: false,
                                                    enableKeyEvents: true

                                                }, {xtype: 'splitter'},
                                                {xtype: 'datefield',
                                                    fieldLabel: 'Date.Naiss',
                                                    emptyText: 'Date de naissance',
                                                    name: 'dtNAISSANCE',
                                                    height: 30, flex: 1,
                                                    submitFormat: 'Y-m-d',
                                                    format: 'd/m/Y',
                                                    maxValue: new Date(),
                                                    enableKeyEvents: true

                                                }
                                            ]
                                        }
                                        , {
                                            xtype: 'fieldcontainer',
                                            flex: 1,
                                            layout: {type: 'hbox', align: 'stretch'},
                                            items: [
                                                {xtype: 'textfield',
                                                    fieldLabel: 'Adresse',
                                                    emptyText: 'Adresse',
                                                    name: 'strADRESSE',
                                                    height: 30, flex: 1,
                                                    enableKeyEvents: true

                                                }, {xtype: 'splitter'},
                                                {
                                                    xtype: 'textfield',
                                                    fieldLabel: 'Code Postale',
                                                    emptyText: 'Code Postale',
                                                    name: 'strCODEPOSTAL',
                                                    height: 30, flex: 1,
                                                    enableKeyEvents: true

                                                }
                                            ]
                                        },
                                        {
                                            xtype: 'fieldcontainer',
                                            flex: 1,
                                            layout: {type: 'hbox', align: 'stretch'},
                                            items: [
                                                {
                                                    xtype: "radiogroup",
                                                    fieldLabel: "Genre",
                                                    allowBlank: true,
                                                    flex: 1,
                                                    vertical: true,
                                                    items: [
                                                        {boxLabel: 'Féminin', name: 'strSEXE', inputValue: 'F'},
                                                        {boxLabel: 'Masculin', name: 'strSEXE', inputValue: 'M'}
                                                    ]
                                                }, {xtype: 'splitter'},
                                                {
                                                    xtype: 'combobox',
                                                    fieldLabel: 'Ville',
                                                    flex: 1,
                                                    height: 30,
//                                                    forceSelection:true,
                                                    minChars: 2,
                                                    name: 'lgVILLEID',
                                                    store: villeStore,
                                                    valueField: 'lgVILLEID',
                                                    displayField: 'strName',
                                                    queryMode: 'remote',
                                                    emptyText: 'Choisir une ville...'
                                                }
                                            ]
                                        }

                                    ]
                                },

                                {
                                    xtype: 'fieldset',
                                    collapsible: false,
                                    height: 120,
                                    bodyPadding: 5,
                                    title: 'Infos.Tiers.Payant.RO',
                                    layout: {
                                        type: 'vbox', align: 'stretch'

                                    },
                                    items: [
                                        {
                                            xtype: 'fieldcontainer',
                                            flex: 1,
                                            layout: {type: 'hbox'},
                                            items: [
                                                {
                                                    xtype: 'combobox',
                                                    fieldLabel: 'Tiers.Payant',
                                                    name: 'lgTIERSPAYANTID',
                                                    flex: 1, height: 30,
                                                    minChars: 2,
                                                    itemId: 'tiersvo',
                                                    store: tierspayantss,
                                                    valueField: 'lgTIERSPAYANTID',
                                                    displayField: 'strFULLNAME',
//                                                    forceSelection:true,
                                                    typeAhead: false,
                                                    allowBlank: false,
                                                    queryMode: 'remote',
                                                    emptyText: 'Choisir un tierspayant...'
                                                },
                                                {xtype: 'splitter'},
                                                {
                                                    xtype: 'numberfield',
                                                    flex: 1, height: 30,
                                                    fieldLabel: 'Pourcentage',
                                                    allowDecimals: false,
                                                    allowBlank: false,
                                                    hideTrigger: true,
                                                    name: 'intPOURCENTAGE', minValue: 1,
                                                    maxValue: 100,
                                                    maskRe: /[1-100.]/,
                                                    emptyText: 'Pourcentage'
                                                }
                                            ]
                                        },
                                        {
                                            xtype: 'fieldcontainer',
                                            flex: 1,
                                            layout: {type: 'hbox'},
                                            items: [
                                                {
                                                    xtype: 'numberfield',
                                                    hideTrigger: true,
                                                    flex: 1, height: 30,
                                                    allowDecimals: false,
                                                    fieldLabel: 'Plafond.Vente',
                                                    name: 'dblQUOTACONSOMENSUELLE', minValue: 0,
                                                    emptyText: 'Plafond.Vente'
                                                },
                                                {xtype: 'splitter'},
                                                {
                                                    xtype: 'numberfield',
                                                    flex: 1,
                                                    hideTrigger: true,
                                                    allowDecimals: false,
                                                    fieldLabel: 'Plafond.Encours',
                                                    name: 'dbPLAFONDENCOURS', minValue: 0,
                                                    height: 30,
                                                    maskRe: /[0-100.]/,
                                                    emptyText: 'Plafond.Encours'
                                                }, {xtype: 'splitter'}, {xtype: 'splitter'}, {xtype: 'splitter'},
                                                {
                                                    xtype: 'checkbox',
                                                    boxLabel: 'Le plafond est-il absolu ?',
                                                    labelAlign: 'right',
                                                    flex: 1,
                                                    height: 30,
                                                    name: 'bIsAbsolute'
//                                                    checked: false

                                                }, {
                                                    xtype: 'hiddenfield',
                                                    name: 'intPRIORITY',
                                                    value: 1
                                                }
                                                , {
                                                    xtype: 'hiddenfield',
                                                    name: 'lgCLIENTID'

                                                }, {
                                                    xtype: 'hiddenfield',
                                                    name: 'lgTYPECLIENTID',
                                                    value: '1'

                                                }
                                            ]
                                        }
                                    ]
                                },
                                {
                                    xtype: 'fieldset',
                                    collapsible: false,
                                    height: 200,
                                    bodyPadding: 5,
                                    title: 'Infos.Tiers-payant.complémentaire',
                                    layout: {
                                        type: 'fit'

                                    },
                                    items: [
                                        {
                                            xtype: 'grid',
                                            selModel: {
                                                selType: 'cellmodel',
                                                mode: 'SINGLE'
                                            }, 
                                            dockedItems: [
                                                {
                                                    xtype: 'toolbar',
                                                    dock: 'top',
//                                                    ui: 'footer',
                                                    layout: {
                                                        pack: 'start',
                                                        type: 'hbox'
                                                    },
                                                    items: [
                                                        {
                                                            xtype: 'button',
                                                            itemId: 'associertps',
                                                            text: 'Associer Tiers-payant'
                                                        }
                                                    ]
                                                }
                                            ],

                                            store: clientTpStore,
                                            columns: [
                                                {
                                                    text: '',
                                                    hidden: true,
                                                    dataIndex: 'lgTIERSPAYANTID'
                                                },
                                                {
                                                    text: '',
                                                    hidden: true,
                                                    dataIndex: 'compteTp'
                                                },
                                                {
                                                    text: 'Tiers-payant',
                                                    flex: 1,
                                                    dataIndex: 'tpFullName'
                                                },
                                                {
                                                    text: 'Taux.Couverture',
                                                    flex: 0.5,
                                                    dataIndex: 'taux'/*,
                                                     editor: {
                                                     xtype: 'numberfield',
                                                     completeOnEnter: true,
                                                     allowBlank: false,
                                                     minValue: 1,
                                                     maskRe: /[0-9.]/,
                                                     selectOnFocus: true,
                                                     hideTrigger: true
                                                     }*/
                                                },
                                                {
                                                    text: 'Numéro/SS',
                                                    flex: 0.8,
                                                    dataIndex: 'numSecurity'/*,
                                                     editor: {
                                                     completeOnEnter: true,
                                                     allowBlank: true,
                                                     selectOnFocus: true
                                                     
                                                     }*/
                                                },
                                                {
                                                    text: 'RC',
                                                    flex: 0.5,
                                                    dataIndex: 'order'/*,
                                                     editor: {
                                                     completeOnEnter: true,
                                                     xtype: 'numberfield',
                                                     maskRe: /[2-4.]/,
                                                     minValue: 2,
                                                     maxValue: 4,
                                                     hideTrigger: true,
                                                     allowBlank: false,
                                                     selectOnFocus: true
                                                     
                                                     }*/
                                                }
                                                , {
                                                    xtype: 'actioncolumn',
                                                    width: 30,
                                                    sortable: false,
                                                    menuDisabled: true,
                                                    items: [{
                                                            icon: 'resources/images/icons/fam/delete.png',
                                                            tooltip: 'Retirer',
                                                            scope: me,
                                                            getClass: function (value, metadata, record) {
                                                                if (record.get('canRemove') == 1) {
                                                                    return 'x-display-hide';
                                                                } else {
                                                                    return "x-hide-display";
                                                                }
                                                            }

                                                        }]
                                                }
                                            ]
                                        }
                                    ]
                                }
                            ]
                        }

                    ]
                }
            ]
        });
        me.callParent(arguments);
    }

});

