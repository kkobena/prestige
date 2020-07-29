
/* global Ext */

function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}
Ext.define('testextjs.view.Report.recaptableaupharmacien.RecapTableauParmaGrid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.recpatableaupharm-grid',
    initComponent: function () {
        var store = Ext.create('testextjs.store.Statistics.RuptureStoks');

        Ext.apply(this, {
            features: [
                {
                    ftype: 'summary'
                }],
            id: 'RuptureGrid',
            store: store,
            viewConfig: {
                forceFit: false,
                emptyText: '<h1 style="margin:10px 10px 10px 70px;">Pas de donn&eacute;es</h1>'
            },
            columns: [
                {
                    header: 'id',
                    dataIndex: 'id',
                    hidden: true
                },
                {
                    header: 'Code CIP',
                    dataIndex: 'CODECIP',
                    summaryType: "count",
                    flex:1,
                    summaryRenderer: function (value) {

                        if (value > 0) {
                            return "<b><span style='color:blue;'>TOTAL: </span></b>";
                        } else {
                            return '';
                        }
                    }
                },
                {
                    header: 'Libell&eacute; du produit',
                    dataIndex: 'str_LIBELLE',
                    flex: 2,
                },
                {
                    text: 'Code Gestion',
                    dataIndex: 'CODEGESTION',
                    flex: 0.5,
                }
                , {
                    text: 'Qt&eacute; Reapp',
                    dataIndex: 'QTEREAP',
                    align: 'right',
                    flex: 1,
                    renderer: amountformat
                },
                {
                    text: 'Seuil Reapp.',
                    dataIndex: 'SEUILREAP',
                    align: 'right',
                    flex: 1,
                    renderer: amountformat
                }, {
                    text: 'Ruptures',
                    columns: [{
                            text: 'Nombre de Fois',
                            dataIndex: 'Nombre Fois',
                            align: 'right',
                             flex:1,
                            summaryType: "sum",
                            renderer: amountformat,
                            summaryRenderer: function (value) {
                                if (value > 0) {
                                    return "<b><span style='color:blue;'>" + amountformat(value) + " </span></b>";
                                } else {
                                    return '';
                                }
                            }

                        }
                        , {
                            text: 'Quantit&eacute;',
                            dataIndex: 'Quantite',
                            align: 'right',
                            summaryType: "sum",
                             flex:1,
                            renderer: amountformat,
                            summaryRenderer: function (value) {
                                if (value > 0) {
                                    return "<b><span style='color:blue;'>" + amountformat(value) + " </span></b>";
                                } else {
                                    return '';
                                }
                            }

                        }

                    ]}, {
                    text: 'Qt&eacute; Propos&eacute;e',
                    dataIndex: 'QTEPROPOSE',
                    align: 'right',
                     flex:1,
                    renderer: amountformat
                }, {
                    text: 'Seuil Propos&eacute',
                    dataIndex: 'SEUILPROPOSE',
                    align: 'right',
                     flex:1,
                    renderer: amountformat
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


