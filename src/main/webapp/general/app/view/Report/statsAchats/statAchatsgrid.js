
/* global Ext */

function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}
Ext.define('testextjs.view.Report.statsAchats.statAchatsgrid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.statachats-grid',
    initComponent: function () {
        var store = Ext.create('testextjs.store.Statistics.QtyAchatsStore');


        Ext.apply(this, {

            id: 'statachatsGrid',
            store: store,
            viewConfig: {
                forceFit: true,
                emptyText: '<h1 style="margin:10px 10px 10px 30%;">Pas de donn&eacute;es</h1>'
            },

            columns: [
                {
                    header: 'id',
                    dataIndex: 'id',
                    hidden: true
                },
                {
                    header: 'CIP',
                    dataIndex: 'CIP',
                    width:80

                }, {
                    header: 'LIBELLE',
                    dataIndex: 'DESC',
                    width:200

                },
                {
                    text: 'Janvier',
                    columns: [{
                            text: 'Montant',
                            dataIndex: 'JANUARY',
                            flex: 1, renderer: amountformat,
                            align: 'right'

                        }, {
                            text: 'Qté',
                            dataIndex: 'JANUARYQTY',
                            flex: 1, renderer: amountformat,
                            align: 'right'

                        }, {
                            text: 'Qté UG',
                            dataIndex: 'JANUARYUG',
                            flex: 1, renderer: amountformat,
                            align: 'right'

                        }

                    ]},
                
                  {
                    text: 'Février',
                    columns: [{
                            text: 'Montant',
                            dataIndex: 'FEBRUARY',
                            flex: 1, renderer: amountformat,
                            align: 'right'

                        }, {
                            text: 'Qté',
                            dataIndex: 'FEBRUARYQTY',
                            flex: 1, renderer: amountformat,
                            align: 'right'

                        }, {
                            text: 'Qté UG',
                            dataIndex: 'FEBRUARYUG',
                            flex: 1, renderer: amountformat,
                            align: 'right'

                        }

                    ]},

                 {
                    text: 'Mars',
                    columns: [{
                            text: 'Montant',
                            dataIndex: 'MARCH',
                            flex: 1, renderer: amountformat,
                            align: 'right'

                        }, {
                            text: 'Qté',
                            dataIndex: 'MARCHQTY',
                            flex: 1, renderer: amountformat,
                            align: 'right'

                        }, {
                            text: 'Qté UG',
                            dataIndex: 'MARCHUG',
                            flex: 1, renderer: amountformat,
                            align: 'right'

                        }

                    ]}
               
                , 
                {
                    text: 'Avril',
                    columns: [{
                            text: 'Montant',
                            dataIndex: 'APRIL',
                            flex: 1, renderer: amountformat,
                            align: 'right'

                        }, {
                            text: 'Qté',
                            dataIndex: 'APRILQTY',
                            flex: 1, renderer: amountformat,
                            align: 'right'

                        }, {
                            text: 'Qté UG',
                            dataIndex: 'APRILUG',
                            flex: 1, renderer: amountformat,
                            align: 'right'

                        }

                    ]},
                
                
                {
                    text: 'Mai',
                    columns: [{
                            text: 'Montant',
                            dataIndex: 'MAY',
                            flex: 1, renderer: amountformat,
                            align: 'right'

                        }, {
                            text: 'Qté',
                            dataIndex: 'MAYQTY',
                            flex: 1, renderer: amountformat,
                            align: 'right'

                        }, {
                            text: 'Qté UG',
                            dataIndex: 'MAYUG',
                            flex: 1, renderer: amountformat,
                            align: 'right'

                        }

                    ]}
                
               ,
               {
                    text: 'Juin',
                    columns: [{
                            text: 'Montant',
                            dataIndex: 'JUNE',
                            flex: 1, renderer: amountformat,
                            align: 'right'

                        }, {
                            text: 'Qté',
                            dataIndex: 'JUNEQTY',
                            flex: 1, renderer: amountformat,
                            align: 'right'

                        }, {
                            text: 'Qté UG',
                            dataIndex: 'JUNEUG',
                            flex: 1, renderer: amountformat,
                            align: 'right'

                        }

                    ]}
                ,
               {
                    text: 'Juillet',
                    columns: [{
                            text: 'Montant',
                            dataIndex: 'JULY',
                            flex: 1, renderer: amountformat,
                            align: 'right'

                        }, {
                            text: 'Qté',
                            dataIndex: 'JULYQTY',
                            flex: 1, renderer: amountformat,
                            align: 'right'

                        }, {
                            text: 'Qté UG',
                            dataIndex: 'JULYUG',
                            flex: 1, renderer: amountformat,
                            align: 'right'

                        }

                    ]}
               
                ,
                {
                    text: 'Août',
                    columns: [{
                            text: 'Montant',
                            dataIndex: 'AUGUST',
                            flex: 1, renderer: amountformat,
                            align: 'right'

                        }, {
                            text: 'Qté',
                            dataIndex: 'AUGUSTQTY',
                            flex: 1, renderer: amountformat,
                            align: 'right'

                        }, {
                            text: 'Qté UG',
                            dataIndex: 'AUGUSTUG',
                            flex: 1, renderer: amountformat,
                            align: 'right'

                        }

                    ]},
                
                {
                    text: 'Septembre',
                    columns: [{
                            text: 'Montant',
                            dataIndex: 'SEPTEMBER',
                            flex: 1, renderer: amountformat,
                            align: 'right'

                        }, {
                            text: 'Qté',
                            dataIndex: 'SEPTEMBERQTY',
                            flex: 1, renderer: amountformat,
                            align: 'right'

                        }, {
                            text: 'Qté UG',
                            dataIndex: 'SEPTEMBERUG',
                            flex: 1, renderer: amountformat,
                            align: 'right'

                        }

                    ]},
                 {
                    text: 'Octobre',
                    columns: [{
                            text: 'Montant',
                            dataIndex: 'OCTOBER',
                            flex: 1, renderer: amountformat,
                            align: 'right'

                        }, {
                            text: 'Qté',
                            dataIndex: 'OCTOBERQTY',
                            flex: 1, renderer: amountformat,
                            align: 'right'

                        }, {
                            text: 'Qté UG',
                            dataIndex: 'OCTOBERUG',
                            flex: 1, renderer: amountformat,
                            align: 'right'

                        }

                    ]},
                {
                    text: 'Novembre',
                    columns: [{
                            text: 'Montant',
                            dataIndex: 'NOVEMBER',
                            flex: 1, renderer: amountformat,
                            align: 'right'

                        }, {
                            text: 'Qté',
                            dataIndex: 'NOVEMBERQTY',
                            flex: 1, renderer: amountformat,
                            align: 'right'

                        }, {
                            text: 'Qté UG',
                            dataIndex: 'NOVEMBERUG',
                            flex: 1, renderer: amountformat,
                            align: 'right'

                        }

                    ]},
                
                {
                    text: 'Décembre',
                    columns: [{
                            text: 'Montant',
                            dataIndex: 'DECEMBER',
                            flex: 1, renderer: amountformat,
                            align: 'right'

                        }, {
                            text: 'Qté',
                            dataIndex: 'DECEMBERQTY',
                            flex: 1, renderer: amountformat,
                            align: 'right'

                        }, {
                            text: 'Qté UG',
                            dataIndex: 'DECEMBERUG',
                            flex: 1, renderer: amountformat,
                            align: 'right'

                        }

                    ]}
                
                
            ],
            selModel: {
                selType: 'cellmodel'
            },
            dockedItems:
                    [
                  {
            xtype: 'toolbar',
            dock: 'top',
            items: [
                {
                    xtype: 'textfield',
                    id: 'rechQtyAchat',
                    flex: 1,
                    emptyText: 'Recherche',
                    listeners: {
                        specialKey: function (field, e, Familletion) {
                            if (e.getKey() === e.ENTER) {
                                var grid = Ext.getCmp('statachatsGrid');

                                var dt_end_vente = Ext.getCmp('cmbyearsachat').getValue();

                                grid.getStore().load({
                                    params: {

                                        year: dt_end_vente,
                                        search_value: field.getValue()
                                    }
                                });
                            }

                        }
                    }
                }, {
                    xtype: 'tbseparator'
                }


                ,
                {
                    xtype: 'combo',

                    emptyText: 'Sélectionnez une année',

                    fieldLabel: 'Année',
                    labelWidth: 40,
                    flex: 1.5,
                    id: 'cmbyearsachat',
                    valueField: 'YEAR',
                    displayField: 'YEAR',
                    value: new Date().getFullYear(),
                    store: Ext.create("Ext.data.Store", {
                        fields: [
                            {
                                name: 'id',
                                type: 'string'
                            },
                            {
                                name: 'YEAR',
                                type: 'int'
                            }


                        ],
                        pageSize: 10,
                        // autoLoad: true,
                        proxy: {
                            type: 'ajax',
                            url: '../webservices/Report/qtyorder/ws_years.jsp',
                            reader: {
                                type: 'json',
                                root: 'data',
                                totalProperty: 'total'
                            }
                        }
                    }),
                    listeners: {
                        select: function () {

                            var grid = Ext.getCmp('statachatsGrid');

                            var rechQtyAchat = Ext.getCmp('rechQtyAchat').getValue();

                            grid.getStore().load({
                                params: {

                                    year: this.getValue(),
                                    search_value: rechQtyAchat
                                }
                            });
                        }
                    }

                }

                , {
                    xtype: 'tbseparator'
                },
                {
                    // flex: 0.4,
                    width: 100,
                    xtype: 'button',
                    iconCls: 'searchicon',
                    text: 'Rechercher',
                    listeners: {
                        click: function () {
                            var grid = Ext.getCmp('statachatsGrid');

                            var dt_end_vente = Ext.getCmp('cmbyearsachat').getValue();
                            var search_value = Ext.getCmp('rechQtyAchat').getValue();

                            grid.getStore().load({
                                params: {

                                    year: dt_end_vente,
                                    search_value: search_value
                                }
                            });
                        }
                    }


                }, {
                    xtype: 'tbseparator'
                }


                ,
                {
                    width: 100,
                    xtype: 'button',
                    text: 'Imprimer',
                    iconCls: 'printable',
//                    glyph: 0xf1c1,
                    listeners: {
                        click: function () {

                            var year = Ext.getCmp('cmbyearsachat').getValue();
                            var search_value = Ext.getCmp('rechQtyAchat').getValue();


                            if (year === null) {
                                year = '';
                            }

                            var linkUrl = "../webservices/Report/statsAchats/ws_generate_pdf.jsp" + "?year=" + year + "&search_value=" + search_value;
                            window.open(linkUrl);

                        }
                    }


                }


            ]
        },
                
                {
                xtype: 'pagingtoolbar',
                store: store, // same store GridPanel is using
                dock: 'bottom',
                displayInfo: true,
                listeners: {
                    beforechange: function (page, currentPage) {
                        var myProxy = this.store.getProxy();
                        myProxy.params = {
                            year: '',

                            search_value: ''
                        };
                        var year = Ext.getCmp('cmbyearsachat').getValue();
                        var search_value = Ext.getCmp('rechQtyAchat').getValue();

                        myProxy.setExtraParam('year', year);

                        myProxy.setExtraParam('search_value', search_value);

                    }

                }
            }]
        });
        this.callParent();
    }
});


