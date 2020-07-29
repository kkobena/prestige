/* global Ext */

//var url_services ='../webservices/sm_user/menu'; 
var url_services_data_coffrecaisse = '../webservices/sm_user/coffrecaisse/ws_data.jsp';
var url_services_transaction_coffrecaisse = '../webservices/sm_user/coffrecaisse/ws_transaction.jsp?mode=';
var dt_Date_Debut;
var dt_Date_Fin;
var Me;

function amountfarmat(val) {
    return Ext.util.Format.number(val, '0,000.');
}

Ext.define('testextjs.view.sm_user.coffrecaisse.CoffrecaisseManager', {
    extend: 'Ext.grid.Panel',
    xtype: 'ouverturecaisseempmanager',
    id: 'ouverturecaisseempmanagerID',
    requires: [
        'Ext.selection.CellModel',
        'Ext.grid.*',
        'Ext.window.Window',
        'Ext.data.*',
        'Ext.util.*',
        'Ext.form.*',
        'Ext.JSON.*',
        'testextjs.model.Coffrecaisse',
        'testextjs.view.sm_user.coffrecaisse.action.add',
        'Ext.ux.ProgressBarPager'

    ],
    title: 'Attribution Caisse',
    closable: false,
    frame: true,
    initComponent: function() {

        var itemsPerPage = 20;
        
        Me = this;

        var store = new Ext.data.Store({
            model: 'testextjs.model.Coffrecaisse',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_services_data_coffrecaisse,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                }
            }

        });


        this.cellEditing = new Ext.grid.plugin.CellEditing({
            clicksToEdit: 1
        });

        Ext.apply(this, {
//            width: 950,
            width: '98%',
            height: valheight,
            id: 'Grid_Prevente_ID',
            plugins: [this.cellEditing],
            store: store,
            columns: [{
                    header: 'ID_COFFRE_CAISSE',
                    dataIndex: 'ID_COFFRE_CAISSE',
                    hidden: true,
                    flex: 1
                            /*  editor: {
                             allowBlank: false
                             }
                             */
                }, {
                    header: 'Utilisateur',
                    dataIndex: 'lg_USER_ID',
                    flex: 1
                            /* editor: {
                             allowBlank: false
                             }*/

                }, {
                    header: 'Montant',
                    dataIndex: 'int_AMOUNT',
                    flex: 1,
                    renderer: amountfarmat
                            /* editor: {
                             allowBlank: false
                             }
                             */}, {
                    header: 'Date',
                    dataIndex: 'dt_CREATED',
                    flex: 1
                            /* editor: {
                             allowBlank: false
                             }*/
                }, {
                    header: 'Enregistre par',
                    dataIndex: 'ld_CREATED_BY',
                    flex: 1
                            /* editor: {
                             allowBlank: false
                             }*/




                }, {
                    header: 'Lieu de Travail',
                    dataIndex: 'lg_EMPLACEMENT_ID',
//                    hidden: true,
                    flex: 1
                }, {
                    header: 'Statut',
                    dataIndex: 'str_STATUT',
                    flex: 1
                            /* editor: {
                             allowBlank: false
                             }*/



                }, {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
                    hidden: true,
                    menuDisabled: true,
                    items: [{
                            icon: 'resources/images/icons/fam/page_white_edit.png',
                            tooltip: 'Modifier',
                            scope: this,
                            handler: this.onEditClick
                        }]
                }, {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
                    menuDisabled: true,
                    items: [{
                            icon: 'resources/images/icons/fam/delete.png',
                            tooltip: 'Supprimer',
                            scope: this,
                            handler: this.onRemoveClick
                        }]
                }],
            selModel: {
                selType: 'cellmodel'
            },
            tbar: [
                {
                    text: 'Attribuer',
                    iconCls: 'addicon',
                    scope: this,
                    handler: this.onAddClick
                }, '-',
                {
                    xtype: 'datefield',
                    fieldLabel: 'Du',
                    name: 'dt_debut',
                    id: 'dt_debut',
                    allowBlank: false,
                    submitFormat: 'Y-m-d',
                    maxValue: new Date(),
                    format: 'd/m/Y',
                    listeners: {
                        'change': function (me) {
                            // alert(me.getSubmitValue());
                            dt_Date_Debut = me.getSubmitValue();
                            Ext.getCmp('dt_fin').setMinValue(me.getValue());
                              Ext.getCmp('Grid_Prevente_ID').getStore().getProxy().url = url_services_data_coffrecaisse + "?dt_Date_Debut=" + dt_Date_Debut;
                        }
                    }
                }, {
                    xtype: 'datefield',
                    fieldLabel: 'Au',
                    name: 'dt_fin',
                    id: 'dt_fin',
                    allowBlank: false,
                    maxValue: new Date(),
                    submitFormat: 'Y-m-d',
                    format: 'd/m/Y',
                    listeners: {
                        'change': function (me) {
                            dt_Date_Fin = me.getSubmitValue();
                            Ext.getCmp('dt_debut').setMaxValue(me.getValue());
                             Ext.getCmp('Grid_Prevente_ID').getStore().getProxy().url = url_services_data_coffrecaisse + "?dt_Date_Debut=" + dt_Date_Debut + "&dt_Date_Fin=" + dt_Date_Fin;
                        }
                    }
                }, '-', {
                    xtype: 'textfield',
                    id: 'rechecher',
                    name: 'user',
                    emptyText: 'Recherche',
                    listeners: {
                        'render': function(cmp) {
                            cmp.getEl().on('keypress', function(e) {
                                if (e.getKey() === e.ENTER) {
                                    Me.onRechClick();
                                    
                                }
                            });
                        }
                    }
                }, {
                    text: 'rechercher',
                    tooltip: 'rechercher',
                    scope: this,
                    iconCls: 'searchicon',
                    handler: this.onRechClick
                }],
            bbar: {
                xtype: 'pagingtoolbar',
                pageSize: 10,
                store: store,
                displayInfo: true,
                plugins: new Ext.ux.ProgressBarPager()
            }
        });

        this.callParent();

        this.on('afterlayout', this.loadStore, this, {
            delay: 1,
            single: true
        })


        this.on('edit', function(editor, e) {



            Ext.Ajax.request({
                url: url_services_transaction_coffrecaisse + 'update',
                params: {
                    ID_COFFRE_CAISSE: e.record.data.ID_COFFRE_CAISSE,
                    lg_USER_ID: e.record.data.lg_USER_ID,
                    dt_CREATED: e.record.data.dt_CREATED,
                    int_AMOUNT: e.record.data.int_AMOUNT,
                    str_STATUT: e.record.data.str_STATUT,
                    ld_CREATED_BY: e.record.data.ld_CREATED_BY
                },
                success: function(response)
                {
                    console.log(response.responseText);
                    e.record.commit();
                    store.reload();
                },
                failure: function(response)
                {
                    console.log("Bug " + response.responseText);
                    alert(response.responseText);
                }
            });
        });


    },
    loadStore: function() {
        this.getStore().load({
            callback: this.onStoreLoad
        });
    },
    onStoreLoad: function() {
      /*  var grid = Ext.getCmp('Grid_Prevente_ID');
        if (grid.getStore().getCount() > 0) {
            var firstRec = grid.getStore().getAt(0);
            if (firstRec.get('show') == false) { // cacher le champ stock machine
               // testextjs.app.getController('App').findColumnByDataIndex(grid, 4).setVisible(false);
            }
        }*/
    },
    onAddClick: function() {
        new testextjs.view.sm_user.coffrecaisse.action.add({
            odatasource: "",
            parentview: this,
            mode: "create",
            titre: "Ouverture de caisse Employé"
        });
    },
    onRemoveClick: function(grid, rowIndex) {
        Ext.MessageBox.confirm('Message',
                'confirmer la suppresssion',
                function(btn) {
                    if (btn == 'yes') {
                        var rec = grid.getStore().getAt(rowIndex);
                        Ext.Ajax.request({
                            url: url_services_transaction_coffrecaisse + 'delete',
                            params: {
                                ID_COFFRE_CAISSE: rec.get('ID_COFFRE_CAISSE')
                            },
                            success: function(response)
                            {
                                var object = Ext.JSON.decode(response.responseText, false);
                                if (object.success == 0) {
                                    Ext.MessageBox.alert('Error Message', object.errors);
                                    return;
                                }
                                grid.getStore().reload();
                            },
                            failure: function(response)
                            {

                                var object = Ext.JSON.decode(response.responseText, false);
                                //  alert(object);

                                console.log("Bug " + response.responseText);
                                Ext.MessageBox.alert('Error Message', response.responseText);

                            }
                        });
                        return;
                    }
                });


    },
    onEditClick: function(grid, rowIndex) {
        var rec = grid.getStore().getAt(rowIndex);

        // alert(rec.data.str_DESCRIPTION);

        new testextjs.view.sm_user.coffrecaisse.action.add({
            odatasource: rec.data,
            parentview: this,
            mode: "update",
            titre: "Modification Ouverture Caisse Emp [" + rec.get('lg_USER_ID') + "]"
        });



    },
    onRechClick: function() {
        var val = Ext.getCmp('rechecher');
        if (new Date(dt_Date_Debut) > new Date(dt_Date_Fin)) {
            Ext.MessageBox.alert('Erreur au niveau date', 'La date de d&eacute;but doit &ecirc;tre inf&eacute;rieur &agrave; la date fin');
            return;
        }
        this.getStore().load({
            params: {
                search_value: val.getValue(),
                dt_Date_Debut: dt_Date_Debut,
                dt_Date_Fin: dt_Date_Fin
            }
        }, url_services_data_coffrecaisse);
    }

})