
/* global Ext */

function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}
Ext.define('testextjs.view.Report.analyseventestock.AnalyseGrid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.analyse-grid',
    initComponent: function () {

        Me = this;

        var itemsPerPage = 20;

        var store = Ext.create('testextjs.store.Statistics.AnalyseVente');

        Ext.apply(this, {
            features: [
                {
                    ftype: 'summary'
                }],
            id: 'analyseGrid',
            store: store,
            viewConfig: {
                forceFit: true,
                emptyText: '<h1 style="margin:10px 10px 10px 30%">Pas de donn&eacute;es</h1>'
            },
            columns: [
                {
                    header: 'id',
                    dataIndex: 'id',
                    hidden: true
                },
                {
                    header: 'CODE FAMILLE',
                    dataIndex: 'str_CODE_FAMILLE',
                    
                    summaryType: "count",
                    summaryRenderer: function (value) {

                        if (value > 0) {
                            return "<b><span style='color:blue;'>TOTAL: </span></b>";
                        } else {
                            return '';
                        }
                    }
                },
                {
                    header: 'Famille',
                    dataIndex: 'str_FAmille'
                   

                }

                , {
                    text: 'N.Vente.Comptant',
                    dataIndex: 'NB VENTES VNO',
                    
                    align: 'right',
                    summaryType: "sum",
                    summaryRenderer: function (value) {
                        if (value > 0) {
                            return "<b><span style='color:blue;'>" + value + "  </span></b>";
                        } else {
                            return '';
                        }
                    }


                },
                {
                    text: 'N.Vente.Cr&eacute;dit',
                    dataIndex: 'NB VENTES VO',
                    summaryType: "sum",
                    
                    align: 'right',
                    summaryRenderer: function (value) {
                        if (value > 0) {
                            return "<b><span style='color:blue;'>" + value + "  </span></b>";
                        } else {
                            return '';
                        }
                    }

                },
                {
                    text: 'Qt&eacute;.Vendue',
                    dataIndex: 'QTE VENDUE',
                    summaryType: "sum",
                    align: 'right',
                    
                    summaryRenderer: function (value) {
                        if (value > 0) {
                            return "<b><span style='color:blue;'>" + amountformat(value) + "  </span></b>";
                        } else {
                            return '';
                        }
                    }

                },
                {
                    text: 'Stock',
                    dataIndex: 'QTY',
                    align: 'right',
                    summaryType: "sum",
                    
                    summaryRenderer: function (value) {
                       if(value!==0)
                            return "<b><span style='color:blue;'>" + amountformat(value) + "  </span></b>";
                        
                    }

                }
                ,
                {
                    text: '%Famille',
                    dataIndex: 'Pourcentage',
                    align: 'right'
                   
                   


                },
                {
                    text: 'N.Sortie',
                    dataIndex: 'NBRE SORTIE',
                    summaryType: "sum",
                    align: 'right',
                    
                    summaryRenderer: function (value) {
                        if (value > 0) {
                            return "<b><span style='color:blue;'>" + amountformat(value) + "</span></b>";
                        } else {
                            return '';
                        }
                    }

                },
                {
                    text: 'U.Moy.Vente',
                    dataIndex: 'UNITE MOY VENTE',
                    //summaryType: "average",
                    align: 'right'

                   /* summaryRenderer: function (value) {
                        if (value > 0) {
                            return "<b><span style='color:blue;'>" + value + "</span></b>";
                        } else {
                            return '';
                        }
                    }*/

                }
               

                , {text: 'Qt&eacute;. Reapp',
                    dataIndex: 'int_QTE_REAPP',
                    align: 'right',
                    renderer: amountformat,
                    
                },
                {text: 'Seuil. Reapp',
                    dataIndex: 'int_SEUIL_REAPP',
                    align: 'right',
                    renderer: amountformat,
                    
                }, 
                {
                    text: 'Montant Ventes',
                    dataIndex: 'MONTANT VENTES',
                    summaryType: "sum",
                    align: 'right',
                    renderer: amountformat,
                    
                    summaryRenderer: function (value) {
                        if (value > 0) {
                            return "<b><span style='color:blue;'>" + amountformat(value) + " F</span></b>";
                        } else {
                            return '';
                        }
                    }

                }

            ],
            selModel: {
                selType: 'cellmodel'
            },
            /* tbar: [
             {
             text: 'Imprimer',
             scope: this,
             handler: this.onImprimeClick
             }
             ],*/
            bbar: {
                xtype: 'pagingtoolbar',
                store: store, // same store GridPanel is using
                dock: 'bottom',
                displayInfo: true
            }
        });

        this.callParent();


    }


})


