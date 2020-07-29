
/* global Ext */

function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}
Ext.define('testextjs.view.sm_user.RecapOrganisme.RecapGrid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.Recap-grid',
    initComponent: function () {
        var store = Ext.create('testextjs.store.RecpaOrganisme');
        Ext.apply(this, {
            features: [
                {
                    ftype: 'grouping',
                    groupHeaderTpl: "{[values.rows[0].data.CODEORGANISME]}",
                    hideGroupedHeader: true
                }],
            id: 'RecapGrid',
            store: store,
            viewConfig: {
                forceFit: true,
                emptyText: '<h1 style="margin:10px 10px 10px 30%;">Pas de donn&eacute;es</h1>'
            },
            columns: [
                {xtype: "rownumberer",
                    groupable: false
                },
                {
                    header: "Type Organisme",
                    dataIndex: 'TYPEORGANISME',
                    flex: 1

                },
                {
                    header: 'Code Organisme',
                    dataIndex: 'CODEORGANISME',
                    flex: 1
                },
                {
                    text: 'Num Compte',
                    dataIndex: 'NUMORGANISME',
                    flex: 1

                }
                , {
                    text: 'Compte Comptable',
                    dataIndex: 'COMPTECOMPTABLE',
                    flex: 1

                },
                {
                    text: 'Montant Op',
                    dataIndex: 'MONTANTOP',
                    align: 'right',
                    flex: 1,
                    renderer: amountformat
                }, {
                    text: 'Solde',
                    dataIndex: 'MONTANTSOLDE',
                    align: 'right',
                    flex: 1,
                    renderer: amountformat
                }

            ],
            selModel: {
                selType: 'cellmodel'
            },
            bbar: {
                xtype: 'pagingtoolbar',
                store: store,
                dock: 'bottom',
                displayInfo: true
            }
        });
        this.callParent();
    }
});


