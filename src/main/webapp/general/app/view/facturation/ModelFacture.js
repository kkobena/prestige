
/* global Ext */

Ext.define('testextjs.view.facturation.ModelFacture', {
    extend: 'Ext.panel.Panel',
    xtype: 'modelfacture',
    frame: true,

    title: 'Modèle facture',
    scrollable: true,
    width: '80%',
    minHeight: 500,

    cls: 'custompanel',
    layout: {
        type: 'fit'
    },
    initComponent: function () {
        var store = Ext.create('Ext.data.Store', {
            idProperty: 'id',
            fields:
                    [
                        {name: 'id',
                            type: 'string'

                        },

                        {name: 'libelle',
                            type: 'string'

                        },
                        {name: 'valeur',
                            type: 'string'

                        },
                        {name: 'nomFichier',
                            type: 'string'

                        }

                    ],
            autoLoad: true,
            pageSize: null,

            proxy: {
                type: 'ajax',
                url: '../api/v1/facturation/modelfacture',
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
                    xtype: 'gridpanel',
                    store: store,
                    viewConfig: {
                        forceFit: true,
                        columnLines: true

                    },

                    columns: [
                        {
                            header: 'modelèle',
                            dataIndex: 'valeur',
                            flex: 1
                        },
                        {
                            header: 'Description',
                            dataIndex: 'libelle',
                            flex: 2
                        },{
                            header: 'Nom fichier',
                            dataIndex: 'nomFichier',
                            flex: 1
                        },
                        {
                            xtype: 'actioncolumn',
                            width: 30,
                            sortable: false,
                            menuDisabled: true,
                            items: [{
                                    icon: 'resources/images/icons/fam/page_white_edit.png',
                                    tooltip: 'Editer la description',
                                    scope: this,
                                    handler: me.onEdit

                                }]
                        }


                    ],
                    selModel: {
                        selType: 'rowmodel'
//                        mode: 'SINGLE'
                    },
                    bbar: {
                        xtype: 'pagingtoolbar',
                        store: store,
                        pageSize: null,
                        dock: 'bottom',
                        displayInfo: true

                    }

                }
            ]

        });
        this.callParent();
    },
    onEdit: function (grid, rowIndex) {

        var rec = grid.getStore().getAt(rowIndex);
        var form = Ext.create('Ext.window.Window',
                {

                    autoShow: true,
                    height: 230,
                    width: 450,
                    modal: true,
                    title: 'Editer la description du modèle de facture',
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
                                    text: 'Enregistrer',
                                    handler: function (btn) {
                                        var _this = btn.up('window'), _form = _this.down('form');
                                        if (_form.isValid()) {
                                            Ext.Ajax.request({
                                                method: 'PUT',
                                                headers: {'Content-Type': 'application/json'},
                                                url: '../api/v1/facturation/modelfacture/' + rec.get('id'),
                                                params: Ext.JSON.encode(_form.getValues()),
                                                success: function (response, options) {
                                                    form.destroy();
                                                    var result = Ext.JSON.decode(response.responseText, true);
                                                    grid.getStore().load();

                                                },
                                                failure: function (response, options) {
                                                    Ext.MessageBox.show({
                                                        title: 'Message d\'erreur',
                                                        width: 320,
                                                        msg: 'Erreur de mise à jour',
                                                        buttons: Ext.MessageBox.OK,
                                                        icon: Ext.MessageBox.ERROR

                                                    });
                                                }

                                            });

                                        }

                                    }
                                },
                                {
                                    xtype: 'button',
                                    iconCls: 'cancelicon',
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
                            xtype: 'form',

//                              anchor: '100%',
                            layout: 'fit',
                            items: [
                                {
                                    xtype: 'fieldset',

                                    layout: 'anchor',

                                    collapsible: false,
                                    title: 'Information ',
                                    items: [
                                        {xtype: 'textareafield',
                                            grow: true,
                                            anchor: '100%',
                                            fieldLabel: 'Description',
                                            emptyText: 'Décrire le modèle',
                                            name: 'libelle',
                                            value: rec.get('libelle')

                                        },
                                        {xtype: 'textfield',
                                            anchor: '100%',
                                            fieldLabel: 'Nom fichier',
                                            emptyText: 'Nom du fichier :ex rp_facture_0178.jrxml',
                                            name: 'nomFichier',
                                            value: rec.get('nomFichier')

                                        },
                                        {xtype: 'textfield',
                                            anchor: '100%',
                                            fieldLabel: 'Nom fichier',
                                            emptyText: 'Nom du fichier :ex rp_facture_0178.jrxml',
                                            name: 'nomFichier',
                                            value: rec.get('nomFichier')

                                        }

                                    ]
                                }
                            ]
                        }

                    ]
                });

    }

});


