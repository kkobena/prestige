/* global Ext */

Ext.define('testextjs.view.produits.ItemAjustement', {
    extend: 'Ext.panel.Panel',
    xtype: 'itemAjustement',
    requires: [
        'testextjs.model.caisse.Ajustement'
    ],
    config: {
        data: null
    },
    frame: true,
    title: 'Ajout de produits',
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
      
        var venteDetails = new Ext.data.Store({
            model: 'testextjs.model.caisse.ItemAjust',
            pageSize: null,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: '../api/v1/ajustement/items',
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
                    title: '<span style="color:blue;">Informations Générales</span>',
                    collapsible: false,

//                    cls: 'background_gray',
                    layout: 'anchor',
                    defaults: {
                        anchor: '100%'
                    },

                    items: [
                        {
                            xtype: 'fieldcontainer',
                            layout: 'hbox',
//                            bodyPadding: 5,
                            items: [
                                 {
                                    xtype: 'displayfield',
                                    fieldLabel: 'Opérateur',
                                    labelWidth: 100,
                                    itemId: 'userName',
                                    fieldStyle: "color:blue;font-weight:800;",
                                    flex: 1
                                   
                                },
                                {
                                    xtype: 'displayfield',
                                    itemId: 'dateOp',
                                    fieldLabel: 'Date:',
                                     fieldStyle: "color:blue;font-weight:800;",
                                    flex: 1,
                                    labelWidth: 45
                                    

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
                                            text: 'Retour',
                                            itemId: 'btnGoBack',
                                            iconCls: 'icon-clear-group',
                                            scope: this

                                        },
                                        
                                        {
                                            text: 'Imprimer',
                                            itemId: 'btnCloture',
                                            iconCls: 'icon-clear-group',
                                            scope: this
                                          
                                        }


                                    ]
                                }

                            ],
                            items: [

                              
                                {
                                    xtype: 'fieldset',
                                    title: '<span style="color:blue;">LISTE DES Produits</span>',
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
                                                    dataIndex: 'lgAJUSTEMENTDETAILID',
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
                                                    header: 'Quantité',
                                                    dataIndex: 'intNUMBER',
                                                    format: '0,000.',
                                                    align: 'right',
                                                    flex: 1
                                                  
                                                },
                                                {
                                                    text: 'Stock.Avant',
                                                    flex: 1,
                                                    xtype: 'numbercolumn',
                                                    sortable: true,
                                                    dataIndex: 'intNUMBERCURRENTSTOCK',
                                                    format: '0,000.',
                                                    align: 'right'
                                                },
                                                {
                                                    text: 'Stock.Après',
                                                    flex: 1,
                                                    xtype: 'numbercolumn',
                                                    sortable: true,
                                                    dataIndex: 'intNUMBERAFTERSTOCK',
                                                    format: '0,000.',
                                                    align: 'right'
                                                },
                                                
                                                {
                                                    text: 'P.U',
                                                    xtype: 'numbercolumn',
                                                    flex: 1,
                                                    sortable: true,
                                                    dataIndex: 'intPRICE',
                                                    MaskRe: /[0-9.]/,
                                                    minValue: 0,
                                                    format: '0,000.',
                                                    align: 'right'
                                                }, {
                                                    text: 'PAF',
                                                    flex: 1,
                                                    xtype: 'numbercolumn',
                                                    sortable: true,
                                                    dataIndex: 'intPAF',
                                                    format: '0,000.',
                                                    align: 'right'
                                                },
                                                 {
                                                    text: 'Valorisation',
                                                    flex: 1,
                                                    xtype: 'numbercolumn',
                                                    sortable: true,
                                                    dataIndex: 'montantTotal',
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
                                                            displayMsg: 'nombre(s) de produit(s): {2}',
                                                            pageSize: null,
                                                            store: venteDetails

                                                        }

                                                    ]
                                                }
                                            ]



                                        }
                                        
                                    ]
                                }
,
   {
                    xtype: 'fieldset',
                    title: 'Espace commentaire',
                    layout: 'hbox',
                    collapsible: true,
                    defaultType: 'textfield',
                    defaults: {
                        hideLabel: 'true'
                    },
                    items: [//
                        {
                            xtype: 'textareafield',
                            grow: true,
                            itemId: 'commentaire',
                            fieldLabel: 'Commentaire',
                            flex: 1,
                            margin: '0 0 5 0',
                            emptyText: 'Saisir un commentaire'
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


