var url_services_data_order_list = '../webservices/commandemanagement/order/ws_data_suivi.jsp?';
var url_services_transaction_preenregistrement_cloturer = '../webservices/sm_user/preenregistrement/ws_transaction.jsp?mode=';

var url_services_transaction_order = '../webservices/commandemanagement/order/ws_transaction.jsp?mode=';

var url_services_data_grossiste_suivi_order = '../webservices/configmanagement/grossiste/ws_data.jsp';

var url_services_data_order_suivi_list_rech = '../webservices/commandemanagement/order/ws_data_suivi_rech.jsp';

var Me;
var store_order;
var store_order_grossiste;
var val;

var valdatedebut;
var valdatefin;

Ext.util.Format.decimalSeparator = ',';
Ext.util.Format.thousandSeparator = '.';
function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}

Ext.define('testextjs.view.commandemanagement.suivi_order.SuiviOrderManager', {
    extend: 'Ext.grid.Panel',
    xtype: 'suiviordermanager',
    id: 'suiviordermanagerID',
    frame: true,
    collapsible: false,
    animCollapse: false,
    title: 'Suivi des commandes',
    iconCls: 'icon-grid',
    plain: true,
    maximizable: true,
    //tools: [{type: "pin"}],
    //closable: true,
    plugins: [{
            ptype: 'rowexpander',
            rowBodyTpl: new Ext.XTemplate(
                    '<p> {str_FAMILLE_ITEM}</p>',
                    {
                        formatChange: function (v) {
                            var color = v >= 0 ? 'green' : 'red';
                            return '<span style="color: ' + color + ';">' + Ext.util.Format.usMoney(v) + '</span>';
                        }
                    })
        }],
    initComponent: function () {

        url_services_data_order_list = '../webservices/commandemanagement/order/ws_data_suivi.jsp';

        url_services_transaction_preenregistrement_cloturer = '../webservices/sm_user/preenregistrement/ws_transaction.jsp?mode=';

        url_services_data_order_suivi_list_rech = '../webservices/commandemanagement/order/ws_data_suivi_rech.jsp?';

        Me = this;

        var itemsPerPage = 20;

        // store_order_grossiste
        store_order_grossiste = new Ext.data.Store({
            model: 'testextjs.model.Grossiste',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_services_data_grossiste_suivi_order,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                }
            }

        });

        store_order = new Ext.data.Store({
            model: 'testextjs.model.Order',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_services_data_order_list,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                }
            }

        });

        Ext.apply(this, {
            width: '98%',
            height: 580,
            store: store_order,
            id: 'GridOrderSuiviID',
            columns: [
                {
                    header: 'lg_ORDER_ID',
                    dataIndex: 'lg_ORDER_ID',
                    hidden: true,
                    flex: 1,
                    editor: {
                        allowBlank: false
                    }

                },
                {
                    xtype: 'rownumberer',
                    text: 'LG',
                    width: 45,
                    sortable: true/*,
                     locked: true*/
                },
                // str_REF
                {
                    header: 'Ref.',
                    dataIndex: 'str_REF_ORDER',
                    flex: 1
                },
                {
                    header: 'Grossiste',
                    dataIndex: 'str_GROSSISTE_LIBELLE',
                    flex: 1
                },
                // int_LINE
                {
                    header: 'Nombre de lignes',
                    dataIndex: 'int_LINE',
                    flex: 1
                },
                // int_NBRE_PRODUIT
                {
                    header: 'Nombre de produits',
                    dataIndex: 'int_NBRE_PRODUIT',
                    flex: 1
                },
//                {
//                    header: 'PRIX.ACHAT',
//                    dataIndex: 'PRIX_ACHAT_TOTAL',
//                    flex: 1
//                },
//                {
//                    header: 'PRIX.VENTE',
//                    dataIndex: 'PRIX_VENTE_TOTAL',
//                    flex: 1
//                },
// str_STATUT
                {
                    header: 'STATUT',
                    dataIndex: 'str_STATUT',
                    flex: 2,
                    renderer: function (val) {
                        if (val === 'is_Process') {
                            val = 'COMMANDE EN COURS';
                        } else if (val === 'passed') {
                            val = 'COMMANDE ENVOYEE';
                        } else if (val === 'is_Closed') {
                            val = 'COMMANDE CLOTUREE';
                        }
                        return val;
                    }
                },
//                {
//                    xtype: 'actioncolumn',
//                    width: 30,
//                    sortable: false,
//                    menuDisabled: true,
//                    items: [{
//                            icon: 'resources/images/icons/fam/folder_go.png',
//                            tooltip: 'Commander',
//                            scope: this,
//                            handler: this.onPasseOrderClick
//                        }]
//                },
                {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
                    menuDisabled: true,
                    items: [{
                            icon: 'resources/images/icons/fam/folder_go.png',
                            tooltip: 'Voir Details',
                            scope: this,
                            handler: this.onManageDetailsClick
                        }]
//                },
//                {
//                    xtype: 'actioncolumn',
//                    width: 30,
//                    sortable: false,
//                    menuDisabled: true,
//                    items: [{
//                            icon: 'resources/images/icons/fam/delete.gif',
//                            tooltip: 'Supprimer',
//                            scope: this,
//                            handler: this.onRemoveClick
//                        }]
                }
            ],
            selModel: {
                selType: 'cellmodel'
            },
            tbar: [
                {
                    text: 'Ajouter',
                    tooltip: 'Nouvelle Commande',
                    scope: this,
                    handler: this.onAddClick
                },
                '-',
                {
                    xtype: 'textfield',
                    id: 'rechecher',
                    name: 'suggestion',
                    emptyText: 'Recherche par ref.'
                },
                {
                    text: 'rechercher',
                    tooltip: 'rechercher',
                    scope: this,
                    handler: this.onRechClick
                }, '-',
                {
                    xtype: 'combobox',
                    name: 'lg_GROSSISTE_ID',
                    margins: '0 0 0 10',
                    id: 'lg_GROSSISTE_ID',
                    store: store_order_grossiste,
                    valueField: 'lg_GROSSISTE_ID',
                    displayField: 'str_LIBELLE',
                    typeAhead: true,
                    queryMode: 'remote',
                    flex: 1,
                    emptyText: 'Selectionner Grossiste...',
                    listeners: {
                        select: function (cmp) {
                            var value = cmp.getValue();
                            var OGrid = Ext.getCmp('GridOrderSuiviID');
                            //var url_data_order_suivi_grossiste = '../webservices/commandemanagement/order/ws_data_suivi_grossiste.jsp';
//                            OGrid.getStore().getProxy().url = url_services_data_order_suivi_list_rech + "?lg_GROSSISTE_ID=" + value;
                            OGrid.getStore().getProxy().url = "../webservices/commandemanagement/order/ws_data_suivi.jsp?lg_GROSSISTE_ID=" + value;
                           OGrid.getStore().reload();
                        }
                    }
                }, '-',
                {
                    xtype: 'datefield',
                    id: 'datedebut',
                    name: 'datedebut',
                    emptyText: 'Date debut',
                    submitFormat: 'Y-m-d',
                    maxValue: new Date(),
                    flex: 0.7,
                    format: 'd/m/Y',
                    listeners: {
                        'change': function (me) {
                            // alert(me.getSubmitValue());
                            valdatedebut = me.getSubmitValue();
                        }
                    }
                },
                {
                    xtype: 'datefield',
                    id: 'datefin',
                    name: 'datefin',
                    emptyText: 'Date fin',
                    maxValue: new Date(),
                    flex: 0.7,
                    submitFormat: 'Y-m-d',
                    format: 'd/m/Y',
                    listeners: {
                        'change': function (me) {
                            //alert(me.getSubmitValue());
                            valdatefin = me.getSubmitValue();
                        }
                    }
                }, '-',
                {
                    text: 'RAZ',
                    scope: this,
                    tooltip: 'Rafraichir le tableau',
                    handler: this.onRAZClick
                }
            ],
            bbar: {
                xtype: 'pagingtoolbar',
                pageSize: 10,
                store: store_order,
                displayInfo: true,
                plugins: new Ext.ux.ProgressBarPager()
            }
        });

        this.callParent();

        this.on('afterlayout', this.loadStore, this, {
            delay: 1,
            single: true
        });


        this.on('edit', function (editor, e) {



            Ext.Ajax.request({
                url: url_services_data_order_list + 'update',
                params: {
                    lg_ORDER_ID: e.record.data.lg_ORDER_ID,
                    str_REF_ORDER: e.record.data.str_REF_ORDER,
                    lg_GROSSISTE_ID: e.record.data.lg_GROSSISTE_ID,
                    int_NUMBER: e.record.data.int_NUMBER
                },
                success: function (response)
                {
                    console.log(response.responseText);
                    e.record.commit();
                    store_order.reload();
                },
                failure: function (response)
                {
                    console.log("Bug " + response.responseText);
                    Ext.MessageBox.alert('Error Message', object.errors);
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
    // onRAZClick
    onRAZClick: function () {

        Ext.getCmp('lg_GROSSISTE_ID').setValue("");
        Ext.getCmp('rechecher').setValue("");
        Ext.getCmp('datedebut').setValue("");
        Ext.getCmp('datefin').setValue("");
        
        var OGrid = Ext.getCmp('GridOrderSuiviID');
        OGrid.getStore().getProxy().url = url_services_data_order_suivi_list_rech;
        OGrid.getStore().reload();
    },
    onManageDetailsClick: function (grid, rowIndex) {
        var rec = grid.getStore().getAt(rowIndex);
        var xtype = "ordermanagermanageIT";
        var alias = 'widget.' + xtype;
        testextjs.app.getController('App').onLoadNewComponentWithDataSource(xtype, "Detail_commande", rec.get('lg_ORDER_ID'), rec.data);
        //alert("test"+rec.get('lg_ORDER_ID'));
    },
    onAddClick: function () {
        var xtype = "ordermanagerlist";
        var alias = 'widget.' + xtype;
        testextjs.app.getController('App').onLoadNewComponent(xtype, "Ajouter detail commande", "0");

    },
    onPasseOrderClick: function (grid, rowIndex) {

        var rec = grid.getStore().getAt(rowIndex);
        Ext.Ajax.request({
            url: url_services_transaction_order + 'passeorder',
            params: {
                lg_ORDER_ID: rec.get('lg_ORDER_ID'),
                str_STATUT: rec.get('str_STATUT')
            },
            success: function (response)
            {
                var object = Ext.JSON.decode(response.responseText, false);
                if (object.success === 0) {
                    Ext.MessageBox.alert('Error Message', object.errors);
                    return;
                } else {
                    Ext.MessageBox.alert('PASSATION DE COMMANDE ', 'COMMANDE ' + '[' + rec.get('str_REF_ORDER') + '] ' + 'PASSEE AVEC SUCCES');
                }
                grid.getStore().reload();
            },
            failure: function (response)
            {

                var object = Ext.JSON.decode(response.responseText, false);
                //  alert(object);

                console.log("Bug " + response.responseText);
                Ext.MessageBox.alert('Error Message', response.responseText);

            }
        });
        //alert('Commande passee avec succes');



    },
    onPrintClick: function () {

        window.print();
        body :{
            visibility:visible
        }
        print: {
            visibility:visible
        }


    },
    onRemoveClick: function (grid, rowIndex) {
        Ext.MessageBox.confirm('Message',
                'confirm la suppresssion',
                function (btn) {
                    if (btn === 'yes') {
                        var rec = grid.getStore().getAt(rowIndex);
                        Ext.Ajax.request({
                            url: url_services_transaction_order + 'delete',
                            params: {
                                lg_ORDER_ID: rec.get('lg_ORDER_ID')
                            },
                            success: function (response)
                            {
                                var object = Ext.JSON.decode(response.responseText, false);
                                if (object.success === 0) {
                                    Ext.MessageBox.alert('Error Message', object.errors);
                                    return;
                                }
                                grid.getStore().reload();
                            },
                            failure: function (response)
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
    onRechClick: function () {
        
        var val = Ext.getCmp('rechecher');
        var lg_GROSSISTE_ID = "";

        if (Ext.getCmp('lg_GROSSISTE_ID').getValue() == null) {
            lg_GROSSISTE_ID = "";
        } else {
            lg_GROSSISTE_ID = Ext.getCmp('lg_GROSSISTE_ID').getValue();
        }

        if (new Date(valdatedebut) > new Date(valdatefin)) {
            Ext.MessageBox.alert('Erreur au niveau date', 'La date de d&eacute;but doit &ecirc;tre inf&eacute;rieur &agrave; la date fin');
            return;
        }

        this.getStore().load({
            params: {
                search_value: val.value,
                datedebut: valdatedebut,
                datefin: valdatefin,
                lg_GROSSISTE_ID: lg_GROSSISTE_ID
            }
        }, url_services_data_order_list);

    }

});