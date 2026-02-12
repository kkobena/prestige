Ext.define('testextjs.view.stat.ArticleMvtGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'articlemvtgrid',

    requires: [
        'testextjs.store.ArticleMvtStore'
    ],

    title: 'Articles en mouvement',
    store: Ext.create('testextjs.store.ArticleMvtStore'),

    // ✅ tes contraintes UI
    frame: true,
    iconCls: 'icon-grid',
    width: '97%',
    height: 'auto',
    minHeight: 570,
    cls: 'custompanel',
    layout: {
        type: 'fit'
        // align: 'stretch'
    },

    // ✅ empêche le scroll horizontal (colonnes s’adaptent à la largeur)
    forceFit: true,

    columnLines: true,
    viewConfig: {
        stripeRows: true,
        enableTextSelection: true
    },

    selModel: Ext.create('Ext.selection.CheckboxModel', {
        mode: 'MULTI',
        checkOnly: true
    }),

    initComponent: function () {
        var me = this;

        Ext.apply(me, {
            tbar: [
                {
                    xtype: 'datefield',
                    itemId: 'dtStart',
                    fieldLabel: 'Du',
                    labelWidth: 25,
                    width: 160,
                    format: 'Y-m-d',
                    submitFormat: 'Y-m-d',
                    maxValue: new Date()
                },
                {
                    xtype: 'datefield',
                    itemId: 'dtEnd',
                    fieldLabel: 'Au',
                    labelWidth: 20,
                    width: 155,
                    format: 'Y-m-d',
                    submitFormat: 'Y-m-d',
                    maxValue: new Date()
                },
                {
                    xtype: 'textfield',
                    itemId: 'queryField',
                    flex: 1,
                    emptyText: 'Rechercher (CIP ou Nom)...',
                    enableKeyEvents: true
                },
                {
                    xtype: 'button',
                    itemId: 'btnSearch',
                    text: 'Rechercher',
                    iconCls: 'icon-find'
                },
                {
                    xtype: 'button',
                    itemId: 'btnReset',
                    text: 'Réinitialiser',
                    iconCls: 'icon-refresh'
                },
                '->',
                {
                    xtype: 'button',
                    itemId: 'btnCreateInventaire',
                    text: 'Créer inventaire (sélection)',
                    iconCls: 'icon-add',
                    disabled: true
                }
            ],

            columns: [
                { text: 'ID', dataIndex: 'lgFamilleId', hidden: true },

                { text: 'CIP', dataIndex: 'codeCip', flex: 1 },
                { text: 'Désignation', dataIndex: 'strName', flex: 3 },

                {
                    text: 'PA',
                    dataIndex: 'prixAchat',
                    flex: 1,
                    align: 'right',
                    renderer: function (v) { return Ext.util.Format.number(v || 0, '0,0'); }
                },
                {
                    text: 'PV',
                    dataIndex: 'prixVente',
                    flex: 1,
                    align: 'right',
                    renderer: function (v) { return Ext.util.Format.number(v || 0, '0,0'); }
                }
            ],

            dockedItems: [
                {
                    xtype: 'pagingtoolbar',
                    dock: 'bottom',
                    store: me.store,
                    displayInfo: true
                }
            ]
        });

        me.callParent(arguments);

        me.getSelectionModel().on('selectionchange', function (sm, selections) {
            // itemId => selector itemId
            me.down('button[itemId=btnCreateInventaire]').setDisabled(selections.length === 0);
        });
    }
});
