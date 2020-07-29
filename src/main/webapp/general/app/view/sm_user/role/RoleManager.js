var url_services_data_role = '../webservices/sm_user/role/ws_data.jsp';
var url_services_transaction_role = '../webservices/sm_user/role/ws_transaction.jsp?mode=';
var Me_Workflow;
Ext.define('testextjs.view.sm_user.role.RoleManager', {
    extend: 'Ext.grid.Panel',
    xtype: 'rolemanager',
    id: 'rolemanagerID',
    requires: [
        'Ext.selection.CellModel',
        'Ext.grid.*',
        'Ext.window.Window',
        'Ext.data.*',
        'Ext.util.*',
        'Ext.form.*',
        'Ext.JSON.*',
        'testextjs.model.Role',
        'testextjs.view.sm_user.role.action.add',
        'Ext.ux.ProgressBarPager',
        'testextjs.view.sm_user.role.action.addPrivilege',
    ],
    title: 'Gestion des Profils',
    closable: false,
    frame: true,
    initComponent: function () {

        Me_Workflow= this;
         var itemsPerPage = 20;
        var store = new Ext.data.Store({
            model: 'testextjs.model.Role',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_services_data_role,
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
            width: '98%',
            height: 580,
            plugins: [this.cellEditing],
            store: store,
            columns: [{
                    header: 'lg_ROLE_ID',
                    dataIndex: 'lg_ROLE_ID',
                    hidden: true,
                    flex: 1,
                    editor: {
                        allowBlank: false
                    }
                }, {
                    header: 'Libelle',
                    dataIndex: 'str_NAME',
                    flex: 1/*,
                    editor: {
                        allowBlank: false
                    }*/
                }, {
                    header: 'Description',
                    dataIndex: 'str_DESIGNATION',
                    flex: 1/*,
                    editor: {
                        allowBlank: false
                    }*/

                }, {
                    header: 'Type',
                    dataIndex: 'str_TYPE',
                    hidden: true,
                    flex: 1/*,
                    editor: {
                        allowBlank: false
                    }*/

                }, {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
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
                            icon: 'resources/images/icons/fam/folder_wrench.png',
                            tooltip: 'Attribution des privileges a ce role',
                            scope: this,
                            handler: this.onAssociateClick
                        }]
                }, {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
                    menuDisabled: true,
                    items: [{
                            icon: 'resources/images/icons/fam/delete.gif',
                            tooltip: 'Supprimer des privileges a dans ce role',
                            scope: this,
                            handler: this.onRemoveClick
                        }]
                }],
            selModel: {
                selType: 'cellmodel'
            },
            tbar: [
                {
                    text: 'Creer',
                    scope: this,
                    iconCls: 'addicon',
                    handler: this.onAddClick
                }, '-', {
                    xtype: 'textfield',
                    id: 'rechecher',
                    name: 'user',
                    emptyText: 'Rechercher un role',
                    listeners: {
                        'render': function(cmp) {
                            cmp.getEl().on('keypress', function(e) {
                                if (e.getKey() === e.ENTER) {
                                    Me_Workflow.onRechClick();

                                }
                            });
                        }
                    }
                }, {
                    id: 'P_BT_rolemanager_FIND',
                    text: 'rechercher',
                    tooltip: 'rechercher',
                    iconCls: 'searchicon',
                    scope: this,
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
      //  this.checkPrivilegeToUI();
        this.on('afterlayout', this.loadStore, this, {
            delay: 1,
            single: true
        })


        this.on('edit', function (editor, e) {

            Ext.Ajax.request({
                url: url_services_transaction_role + 'update',
                params: {
                    lg_ROLE_ID: e.record.data.lg_ROLE_ID,
                    str_NAME: e.record.data.str_NAME,
                    str_DESIGNATION: e.record.data.str_DESIGNATION,
                    str_TYPE: e.record.data.str_TYPE
                },
                success: function (response)
                {
                    console.log(response.responseText);
                    e.record.commit();
                    store.reload();
                },
                failure: function (response)
                {
                    console.log("Bug " + response.responseText);
                    alert(response.responseText);
                }
            });
        });


    },
    loadStore: function () {
        this.getStore().load({
            callback: this.onStoreLoad
        });
    },
    onStoreLoad: function () {
    },
   /* onAddClick: function () { // a decommenter en cas de probleme
        // Create a model instance
        var rec = new testextjs.model.Role({
            lg_ROLE_ID: 'init',
            str_NAME: '',
            str_DESIGNATION: '',
            str_TYPE: ''

        });

        this.getStore().insert(0, rec);
        this.cellEditing.startEditByPosition({
            row: 0,
            column: 0
        });
    },*/
    onAddClick: function() {

        new testextjs.view.sm_user.role.action.add({
            odatasource: "",
            parentview: this,
            mode: "create",
            titre: "Ajouter un role"
        });
    },
     onEditClick: function(grid, rowIndex) {
        var rec = grid.getStore().getAt(rowIndex);


        new testextjs.view.sm_user.role.action.add({
            odatasource: rec.data,
            parentview: this,
            mode: "update",
            titre: "Modification du role  [" + rec.get('str_DESIGNATION') + "]"
        });

    },
    
//    onEditClick: function (grid, rowIndex) {
//        var rec = grid.getStore().getAt(rowIndex);
//
//        new testextjs.view.sm_user.role.action.addPrivilege({
//            odatasource: rec.data,
//            parentview: this,
//            mode: "update",
//            titre: "Attribution des privilege pour le role [" + rec.get('str_DESIGNATION') + "]"
//        });
//    },
    onAssociateClick: function (grid, rowIndex) {
        var rec = grid.getStore().getAt(rowIndex);

        new testextjs.view.sm_user.role.action.addPrivilegeBis({
            odatasource: rec.data,
            parentview: this,
            mode: "update",
            titre: "Attribution des privilege pour le role [" + rec.get('str_DESIGNATION') + "]"
        });
    },
    onRemoveClick: function (grid, rowIndex) {
        Ext.MessageBox.confirm('Message',
                'Confirmer la suppresssion',
                function (btn) {
                    if (btn == 'yes') {
                        var rec = grid.getStore().getAt(rowIndex);
                        Ext.Ajax.request({
                            url: url_services_transaction_role + 'delete',
                            params: {
                                lg_ROLE_ID: rec.get('lg_ROLE_ID')
                            },
                            success: function (response)
                            {
                                var object = Ext.JSON.decode(response.responseText, false);
                                if (object.success == "0") {
                                    Ext.MessageBox.alert('Error Message', object.errors);
                                    return;
                                } else {
                                    Ext.MessageBox.alert('confirmation', object.errors);
                                    grid.getStore().reload();
                                }
                                
                            },
                            failure: function (response)
                            {

                                var object = Ext.JSON.decode(response.responseText, false);
                                console.log("Bug " + response.responseText);
                                Ext.MessageBox.alert('Error Message', response.responseText);

                            }
                        });
                        return;
                    }
                });


    },
    onRechClick: function () {
        var val = Ext.getCmp('rechecher');
        this.getStore().load({
            params: {
                search_value: val.value
            }
        }, url_services_data_role);
    }
    , checkPrivilegeToUI: function () {

        var url_services_data_ws_get_privilege_by_bt = '../webservices/sm_user/privilege/ws_get_privilege_by_bt.jsp';
        var storePrivilege = new Ext.data.Store({
            model: 'testextjs.model.Privilege',
            // pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_services_data_ws_get_privilege_by_bt,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                }
            }
        });



        /*storePrivilege.load({
            callback: function () {
                var index;
                var tab_component = new Array(
                        "P_BT_rolemanager_CREATE",
                        "P_BT_rolemanager_FIND"
                        );
                var tab_privilege = [];
                for (i = 0; i < storePrivilege.getCount(); i++) {
                    tab_privilege[i] = storePrivilege.getAt(i).get('str_NAME');
                }
                for (i = 0; i < tab_component.length; ++i) {
                    Ext.getCmp(tab_component[i]).disable();
                    console.log(tab_component[i]);
                    for (k = 0; k < tab_privilege.length; ++k) {
                        if (tab_component[i] === tab_privilege[k]) {
                            //Activer le component
                            Ext.getCmp(tab_component[i]).enable();
                            // return;
                        }
                    }
                }
            }
        });*/
//        storePrivilege.load({});
    }

})