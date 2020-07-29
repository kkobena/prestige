/* global Ext */

Ext.define('testextjs.view.vente.TerminerAvoir', {
    extend: 'Ext.panel.Panel',
    xtype: 'doAvoir',
    requires: [
        'testextjs.model.caisse.Vente'
    ],
    config: {
        data: null
    },

    frame: true,
    title: 'GESTION DES AVOIRS',
    width: '97%',
    height: 'auto',
    minHeight: 570,

    cls: 'custompanel',
    layout: {
        type: 'vbox',
        align: 'stretch',
        padding: 10
    },
    initComponent: function () {
        var typeventeStore = Ext.create('Ext.data.Store', {
            model: 'testextjs.model.caisse.Vente',
            autoLoad: false,
            pageSize: 15,

            proxy: {
                type: 'ajax',
                url: '../api/v1/ventestats/avoirs',
                reader: {
                    type: 'json',
                    root: 'data',
                    totalProperty: 'total'
                }

            }
        });

      

        var venteDetails = new Ext.data.Store({
            model: 'testextjs.model.caisse.VenteItem',
            pageSize: 10,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: '../api/v1/vente/deatails',
                reader: {
                    type: 'json',
                    root: 'data',
                    totalProperty: 'total'
                }
            }
        });

        var me = this;
        Ext.applyIf(me, {

            items: [
                {
                    xtype: 'fieldset',
                    title: '<span style="color:blue;">INFOS AVOIR</span>',
                    collapsible: false,
                    defaultType: 'textfield',
                    cls: 'background_gray',
                    layout: 'anchor',
                    defaults: {
                        anchor: '100%'
                    },

                    items: [
                        {
                            xtype: 'container',
                            layout: 'hbox',
                            margin: '0 0 5 0',
//                            height: 35,
                            style: 'padding-bottom:3px;',
                            defaultType: 'textfield',
                            items: [
                                {
                                    xtype: 'combobox',
                                    itemId: 'typeVente',
                                    store: typeventeStore,
                                    editable: false,
                                    flex: 1,
                                    hidden:true,
                                    margin: '0 15 0 0',
                                    height: 30,
                                    valueField: 'lgPREENREGISTREMENTID',
                                    displayField: 'strREF',
                                    typeAhead: false,
                                    value: '1',
                                    queryMode: 'local',
                                    emptyText: 'Choisir une référence de vente...'

                                },

                                {
                                    xtype: 'displayfield',
                                    itemId: 'vendeur',
                                    flex: 1,
                                    labelWidth: 70,
                                    fieldStyle: "color:blue;",
                                    fieldLabel: "Vendeur",
                                    margin: '0 15 0 0'

                                },
                                {
                                    xtype: 'displayfield',
                                    itemId: 'caissier',
                                    flex: 1,
                                    labelWidth: 70,
                                    fieldStyle: "color:blue;",
                                    fieldLabel: "Caissier",
                                    margin: '0 15 0 0'

                                }


                            ]
                        }
                    ]
                },

                {
                    xtype: 'container',
                    layout: 'anchor',
                    itemId: 'contenu',
                    items: [
                        {
                            xtype: 'panel',
                            border: 0,
                            cls: 'custompanel',
                            layout: {
                                type: 'vbox',
                                align: 'stretch'
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
                                            text: "Terminer l'avoir",
                                            itemId: 'btnCloture',
                                            iconCls: 'icon-clear-group',
                                            scope: this
                                          
                                        },
                                        {
                                            text: 'Retour',
                                            itemId: 'btnGoBack',
                                            iconCls: 'icon-clear-group',
                                            scope: this


                                        }


                                    ]
                                }

                            ],
                            items: [
                                {
                                    xtype: 'container',
                                    itemId: 'assureContainer',
                                    hidden: false,
                                    collapsible: false,
                                    bodyStyle: 'background:#F0F8FF !important;',
                                    defaultType: 'textfield',
                                    layout: {type: 'vbox', align: 'stretch'},

                                    items: [
                                        {
                                            xtype: 'container',
                                            bodyStyle: 'background:#F0F8FF !important;',
                                            layout: {type: 'hbox', align: 'stretch'},

                                            flex: 1,
                                            items: [
                                                {
                                                    xtype: 'fieldset',
                                                    style: 'background-color:#F0F8FF !important;',
                                                    itemId: 'clientStandard',
                                                      hidden:true,
                                                    title: '<span style="color:blue;">INFOS CLIENT</span>',
                                                    flex: 1,
                                                    margin: '0 10 0 0',
                                                    layout: {type: 'hbox', align: 'stretch'},

                                                    items: [
                                                        {
                                                            xtype: 'displayfield',
                                                            fieldLabel: 'Nom/Prénom(s)',
                                                            flex: 2,
                                                            labelWidth: 110,
                                                            fieldStyle: "color:blue;",
                                                            itemId: 'nom',
                                                            margin: '0 10 0 0'
                                                        },
                                                      
                                                        {
                                                            xtype: 'displayfield',
                                                            fieldLabel: 'Téléphone:',
                                                            flex: 1,
                                                            labelWidth:80,
                                                            itemId: 'mobile',
                                                            fieldStyle: "color:blue;",
                                                            margin: '0 10 0 0'
                                                        }


                                                    ]
                                                },

                                                {
                                                    xtype: 'fieldset',
                                                    style: 'background-color:#F0F8FF !important;',
                                                    title: '<span style="color:blue;">INFOS ASSURE</span>',
                                                    itemId: 'assureCmp',
                                                      hidden:true,
                                                    margin: '0 5 0 0',
                                                    flex: 1,
                                                    layout: {
                                                        type: 'hbox', pack: 'start',
                                                        align: 'middle'
                                                    },
                                                    items: [
                                                        {
                                                            xtype: 'container',

                                                            flex: 1,
                                                            layout: {type: 'hbox', align: 'stretch'},
                                                            items: [

                                                                {
                                                                    xtype: 'displayfield',
                                                                    fieldLabel: 'Nom/Prénom(s) :',
                                                                    flex: 1.3,
                                                                    labelWidth: 100,
                                                                    itemId: 'nomAssure',
                                                                    fieldStyle: "color:blue;",
                                                                    margin: '0 10 0 0'
                                                                }, 
                                                                {
                                                                    xtype: 'displayfield',
                                                                    fieldLabel: 'Matricule/SS:',
                                                                    itemId: 'numAssure',
                                                                    flex: 0.7,
                                                                    labelWidth: 90,
                                                                    fieldStyle: "color:blue;",
                                                                    margin: '0 10 0 0'
                                                                }

                                                            ]

                                                        }

                                                    ]

                                                },

                                                {
                                                    xtype: 'fieldset',
                                                    style: 'background-color:#F0F8FF !important;',
                                                    title: '<span style="color:blue;">INFOS AYANT DROIT</span>',
                                                    itemId: 'ayantCmp',
                                                    hidden:true,
                                                    margin: '0 5 0 0',
                                                    flex: 1,
                                                    layout: {
                                                        type: 'hbox', pack: 'start',
                                                        align: 'middle'
                                                    },
                                                    items: [
                                                        {
                                                            xtype: 'container',

                                                            flex: 1,
                                                            layout: {type: 'hbox', align: 'stretch'},
                                                            items: [

                                                                {
                                                                    xtype: 'displayfield',
                                                                    fieldLabel: 'Nom/Prénom(s):',
                                                                    flex: 1.3,
                                                                    labelWidth: 100,
                                                                    itemId: 'nomAyant',
                                                                    fieldStyle: "color:blue;",
                                                                    margin: '0 10 0 0'
                                                                },
                                                                {
                                                                    xtype: 'displayfield',
                                                                    fieldLabel: 'Matricule/SS:',
                                                                    itemId: 'numAyant',
                                                                    flex: 0.7,
                                                                    labelWidth: 90,
                                                                    fieldStyle: "color:blue;",
                                                                    margin: '0 10 0 0'
                                                                }

                                                            ]

                                                        }

                                                    ]

                                                }

                                            ]
                                        }

                                    ]
                                },

                                {
                                    xtype: 'fieldset',
                                    title: '<span style="color:blue;">INFOS VENTE</span>',
                                    collapsible: false,
                                    defaultType: 'textfield',
                                    layout: 'anchor',
                                    cls: 'background_gray',
                                    itemId: 'infoscontainer',
                                    defaults: {
                                        anchor: '100%'
                                    },
                                    items: [
                                        {
                                            xtype: 'container',
                                            layout: 'hbox',
                                            height: 30,
                                            defaultType: 'textfield',
                                            margin: '0 0 10 0',

                                            items: [
                                                {
                                                    xtype: 'displayfield',
                                                    fieldLabel: 'Réf vente:',
                                                    labelWidth: 70,
                                                    itemId: 'refVente',
                                                    fieldStyle: "color:blue;",
                                                    flex:1
                                                    
                                                },
                                                {
                                                    xtype: 'displayfield',
                                                    fieldLabel: 'Type vente:',
                                                    labelWidth: 80,
                                                    itemId: 'categorieVente',
                                                    fieldStyle: "color:blue;",
                                                    flex:1.3
                                                    
                                                },
                                                {
                                                    xtype: 'displayfield',
                                                    fieldLabel: 'Montant.Total:',
                                                    labelWidth: 90,
                                                    itemId: 'montant',
                                                     renderer: function (v) {
                                                                return Ext.util.Format.number(v, '0,000.') + ' CFA';
                                                            },
                                                    fieldStyle: "color:blue;",
                                                    flex: 0.9
                                                },
                                                 {
                                                    xtype: 'displayfield',
                                                    fieldLabel: 'Montant.Net:',
                                                    labelWidth: 90,
                                                    itemId: 'montantNet',
                                                     renderer: function (v) {
                                                                return Ext.util.Format.number(v, '0,000.') + ' CFA';
                                                            },
                                                            
                                                    fieldStyle: "color:blue;",
                                                    flex: 0.8
                                                },
                                                  {
                                                    xtype: 'displayfield',
                                                    fieldLabel: 'Remise:',
                                                    labelWidth: 50,
                                                    itemId: 'montantRemise',
                                                     renderer: function (v) {
                                                                return Ext.util.Format.number(v, '0,000.') + ' CFA';
                                                            },
                                                    fieldStyle: "color:blue;",
                                                    flex: 0.6
                                                }
                                            ]
                                        }
                                    ]
                                },
                                {
                                    xtype: 'fieldset',
                                    title: '<span style="color:blue;">LISTE DES ARTICLES CHOISIS</span>',
                                    collapsible: false,
                                    itemId: 'gridContainer',
                                    defaultType: 'textfield',
                                    layout: 'anchor',
                                    defaults: {
                                        anchor: '100%'
                                    },
                                    items: [
                                        {

                                            xtype: 'gridpanel',
                                            itemId: 'venteGrid',
                                            selModel: {
                                                selType: 'cellmodel',
                                                mode: 'SINGLE'
                                            },
                                            
                                            margin: '0 0 5 0',
                                            store: venteDetails,
                                            height: 'auto',
                                            minHeight: 250,
                                            columns: [
                                                {
                                                    text: '#',
                                                    width: 45,
                                                    dataIndex: 'lgPREENREGISTREMENTDETAILID',
                                                    hidden: true

                                                },
                                                {
                                                    xtype: 'rownumberer',
                                                    text: 'LG',
                                                    width: 45,
                                                    sortable: true
                                                }, {
                                                    text: 'C.CIP',
                                                    flex: 1,
                                                    sortable: true,
                                                    dataIndex: 'intCIP'
                                                }, {
                                                    text: 'DESIGNATION',
                                                    flex: 2.5,
                                                    sortable: true,
                                                    dataIndex: 'strNAME'
                                                }, {
                                                    xtype: 'numbercolumn',
                                                    header: 'QD',
                                                    dataIndex: 'intQUANTITY',
                                                    format: '0,000.',
                                                    align: 'right',
                                                    flex: 1
                                                  
                                                }, {
                                                    text: 'QS',
                                                    xtype: 'numbercolumn',
                                                    flex: 1,
                                                    sortable: true,
                                                    dataIndex: 'intQUANTITYSERVED',
                                                    MaskRe: /[0-9.]/,
                                                    minValue: 0,
                                                    format: '0,000.',
                                                    align: 'right',

                                                    renderer: function (value, metadata, record) {
                                                        if (record.get('BISAVOIR') === true) {
                                                            metadata.style = "color: red;font-weight: bold;";
                                                        }
                                                        return value;
                                                    }
                                                }, {
                                                    text: 'P.U',
                                                    xtype: 'numbercolumn',
                                                    flex: 1,
                                                    sortable: true,
                                                    dataIndex: 'intPRICEUNITAIR',
                                                    format: '0,000.',
                                                    align: 'right'


                                                }, {
                                                    text: 'MONTANT',
                                                    flex: 1,
                                                    xtype: 'numbercolumn',
                                                    sortable: true,
                                                    dataIndex: 'intPRICE',
                                                    format: '0,000.',
                                                    align: 'right'
                                                }
                                                
                                            ],
                                            dockedItems: [

                                                {
                                                    xtype: 'toolbar',
                                                    dock: 'top',
                                                    ui: 'footer',
                                                    itemId: 'querytoolbar',
                                                    items: [
                                                        {
                                                            xtype: 'textfield',

                                                            itemId: 'query',
                                                            emptyText: 'Recherche d\'un produit',
                                                            width: '30%',
                                                            height: 35,
                                                            enableKeyEvents: true
                                                        }, '-', {
                                                            text: 'rechercher',
                                                            tooltip: 'rechercher',
                                                            scope: this,
                                                            itemId: 'btnRecherche',
                                                            iconCls: 'searchicon'

                                                        }
                                                    ]
                                                },
                                                {
                                                    xtype: 'toolbar',
                                                    dock: 'bottom',
                                                    itemId: 'pagingtoolbar',
                                                    items: [{
                                                            xtype: 'pagingtoolbar',
                                                            displayInfo: true,
                                                            flex: 2,
                                                            displayMsg: 'nombre(s) de produit(s): {2}',
                                                            pageSize: 10,
                                                            store: venteDetails

                                                        },
                                                        {
                                                            xtype: 'tbseparator'
                                                        }, {
                                                            xtype: 'displayfield',
                                                            fieldLabel: 'TOTAL VENTE :',
                                                            itemId: 'totalField',
                                                            flex: 1,
                                                            labelWidth: 120,
                                                            renderer: function (v) {
                                                                return Ext.util.Format.number(v, '0,000.') + ' CFA';
                                                            },
                                                            fieldStyle: "color:blue;font-weight: bold;font-size: 1.3em",
                                                            margin: '0 15 0 0',
                                                            value: 0
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


