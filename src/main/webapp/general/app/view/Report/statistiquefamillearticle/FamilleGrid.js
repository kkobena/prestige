
/* global Ext */

function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}
Ext.define('testextjs.view.Report.statistiquefamillearticle.FamilleGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'famillestaistqgrid',
    initComponent: function() {
        Me = this;
        var store = Ext.create('testextjs.store.Statistics.FamilleArticles');
        Ext.apply(this, {
            features: [
                {
                    ftype: 'summary'
                }],
            id: 'FamilleGrid',
            store: store,
            viewConfig: {
              
                    emptyText: '<h1 style="margin:10px 10px 10px 40px;">Pas de donn&eacute;es</h1>'
                },
            columns: [
                {
                    header: 'id',
                    dataIndex: 'id',
                    hidden: true
                },
                {
                    header: 'CODE FAMILLE',
                    dataIndex: 'CODE FAMILLE',
                    flex: 0.4,
                    summaryType: "count",
                    summaryRenderer: function(value) {

                        if (value > 0) {
                            return "<b><span style='color:blue;'>TOTAL: </span></b>";
                        } else {
                            return '';
                        }
                    }
                },
                {
                    header: 'Famille libell&eacute;',
                    dataIndex: 'str_Libelle_Produit',
                    flex:1.4

                },
                {
                    text: 'Mont.net TTC',
                    dataIndex: 'MONTANT NET TTC',
                    flex: 1,
                    align: 'right',
                    summaryType: "sum",
                    renderer: amountformat,
                    summaryRenderer: function(value) {
                        if (value > 0) {
                            return "<b><span style='color:blue;'>" + amountformat(value) + " F </span></b>";
                        } else {
                            return '';
                        }
                    }
                }
                , {
                    text: 'Mont.net HT',
                    dataIndex: 'MONTANT NET HT',
                    flex: 1,
                    summaryType: "sum",
                    align: 'right',
                    renderer: amountformat,
                    summaryRenderer: function(value) {
                        if (value > 0) {
                            return "<b><span style='color:blue;'>" +  amountformat(value) + " F </span></b>";
                        } else {
                            return '';
                        }
                    }


                },
                {
                    text: 'Valeur.Achat',
                    dataIndex: 'VALEUR ACHAT',
                    summaryType: "sum",
                    align: 'right',
                    renderer: amountformat,
                    flex: 1,
                    summaryRenderer: function(value) {
                        if (value > 0) {
                            return "<b><span style='color:blue;'>" + amountformat(value) + " F </span></b>";
                        } else {
                            return '';
                        }
                    }

                },
                {
                    text: '%P&eacute;riode',
                    dataIndex: 'POURCENTAGE TOTAL',
                    align: 'right',
                    flex: 0.5,
                },
                {
                    text: 'Marge.net',
                    dataIndex: 'MARGE NET',
                    summaryType: "sum",
                    align: 'right',
                    renderer: amountformat,
                    flex: 0.5,
                    summaryRenderer: function(value) {
                        if (value > 0) {
                            return "<b><span style='color:blue;'>" + amountformat(value) + " F </span></b>";
                        } else {
                            return '';
                        }
                    }

                }
                ,
                {
                    text: '%Marge',
                    align: 'right',
                    dataIndex: 'MARGE POURCENTAGE',
                    flex: 0.5
                }


            ],
            selModel: {
                selType: 'cellmodel'
            },
           
            bbar: {
                xtype: 'pagingtoolbar',
                store: store, // same store GridPanel is using
                dock: 'bottom',
                displayInfo: true
            }
        });

        this.callParent();


    }


});


