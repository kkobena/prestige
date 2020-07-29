/* global Ext */

var url_services_data_sugg_order_list = '../webservices/commandemanagement/suggestionOrder/ws_data.jsp?str_STATUT=';
var url_services_transaction_suggerercmde = '../webservices/sm_user/suggerercde/ws_transaction.jsp?mode=';
var Me;
//var store_suggestion;
var url_services_pdf_liste_suggerercde = '../webservices/sm_user/suggerercde/ws_generate_pdf.jsp';

Ext.util.Format.decimalSeparator = ',';
Ext.util.Format.thousandSeparator = '.';
var _myAppController;
function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}

Ext.define('testextjs.view.commandemanagement.suggestion.Suggestion_Manager', {
    extend: 'Ext.grid.Panel',
    xtype: 'i_sugg_manager',
    id: 'i_sugg_managerID',
    frame: true,
//    collapsible: true,
    animCollapse: false,
    title: 'Liste Suggestion',
//    iconCls: 'icon-grid',
    plain: true,
    maximizable: true,
//    tools: [{type: "pin"}],
    closable: false,
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

        url_services_data_sugg_order_list = '../webservices/commandemanagement/suggestionOrder/ws_data.jsp?str_STATUT=';
        _myAppController = Ext.create('testextjs.controller.App', {});
        Me = this;

        var itemsPerPage = 20;
        var store_suggestion = new Ext.data.Store({
            model: 'testextjs.model.SuggestionOrder',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_services_data_sugg_order_list + 'is_Process',
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
            store: store_suggestion,
            columns: [
                {
                    header: 'lg_SUGGESTION_ORDER_ID',
                    dataIndex: 'lg_SUGGESTION_ORDER_ID',
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
                    header: 'REF',
                    dataIndex: 'str_REF',
                    flex: 1
                },
                {
                    header: 'GROSSISTE',
                    dataIndex: 'lg_GROSSISTE_ID',
                    flex: 1
                },
                // int_NUMBER
                {
                    header: 'NOMBRE.LIGNE',
                    dataIndex: 'int_NOMBRE_ARTICLES',
                    flex: 1
                },
                {
                    header: 'QTE.ARTICLES',
                    dataIndex: 'int_NUMBER',
                    flex: 1
                },
                // str_STATUT
                {
                    header: 'STATUT',
                    dataIndex: 'str_STATUT',
                    flex: 1,
                    renderer: function (val, m, r) {


                        if (val === 'is_Process') {
                            val = 'MANUELLE';
                        } else if (val === 'enable') {
                            val = 'COMMANDEE';
                        } else if (val === 'auto') {
                            val = 'AUTO';
                        } else if (val === 'pending') {
                            val = 'CLOTURE';
                            m.style = 'background-color:#73C774;color:#FFF;font-weight:800;';

                        }
                        return val;
                    }
                },
                {
                    header: 'Date',
                    dataIndex: 'dt_CREATED',
                    flex: 1
                }, {
                    header: 'Heure',
                    dataIndex: 'dt_UPDATED',
                    flex: 1
                },
                {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
                    menuDisabled: true,
                    items: [{
                            icon: 'resources/images/icons/fam/folder_go.png',
                            tooltip: 'Commander',
                            scope: this,
                            handler: this.onMakeOrderClick
                        }]
                },
                {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
                    menuDisabled: true,
                    items: [{
                            icon: 'resources/images/icons/fam/page_white_edit.png',
                            tooltip: 'Modifier',
                            scope: this,
                            handler: this.onManageDetailsClick
                        }]
                },
                {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
                    menuDisabled: true,
                    items: [{
                            icon: 'resources/images/icons/fam/delete.gif',
                            tooltip: 'Supprimer',
                            scope: this,
                            handler: this.onRemoveClick
                        }]
                },
                {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
                    menuDisabled: true,
                    items: [{
                            icon: 'resources/images/icons/fam/printer.png',
                            tooltip: 'Imprimer une suggession',
                            scope: this,
                            handler: this.onbtnprint
                        }]
                },

                {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
                    menuDisabled: true,
                    items: [{
                            icon: 'resources/images/icons/fam/excel_csv.png',
                            tooltip: 'Generer le fichier CSV',
                            scope: this,
                            handler: function (grid, rowIndex) {

                                Ext.MessageBox.confirm('Message',
                                        'Voulez-vous generer le fichier CSV ?',
                                        function (btn) {
                                            if (btn === 'yes') {
                                                var rec = grid.getStore().getAt(rowIndex);//
                                                window.location = '../api/v1/suggestion/csv?id=' + rec.get('lg_SUGGESTION_ORDER_ID');
                                            }
                                        });

                            }
                        }]
                }
            ],
            selModel: {
                selType: 'cellmodel'
            },
            tbar: [
                {
                    text: 'Suggerer',
                    scope: this,
                    hidden: true,
                    handler: this.onAddClick
                },
                {
                    xtype: 'textfield',
                    id: 'rechecher',
                    name: 'suggestion',
                    emptyText: 'Recherche',
                    listeners: {
                        'render': function (cmp) {
                            cmp.getEl().on('keypress', function (e) {
                                if (e.getKey() === e.ENTER) {
                                    Me.onRechClick();

                                }
                            });
                        }
                    }
                },
                {
                    text: 'rechercher',
                    tooltip: 'rechercher',
                    iconCls: 'searchicon',
                    scope: this,
                    handler: this.onRechClick
                }, '-',
                {
                    text: 'Imprimer les suggestions',
                    id: 'btn_devis',
                    hidden: true,
//                            iconCls: 'icon-clear-group',
                    scope: this,
                    handler: this.onbtnprint
                }
            ],
            bbar: {
                xtype: 'pagingtoolbar',
                pageSize: 10,
                store: store_suggestion,
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
                url: url_services_data_sugg_order_list + 'update',
                params: {
                    lg_SUGGESTION_ORDER_ID: e.record.data.lg_SUGGESTION_ORDER_ID,
                    lg_GROSSISTE_ID: e.record.data.lg_GROSSISTE_ID,
                    int_NUMBER: e.record.data.int_NUMBER
                },
                success: function (response)
                {
                    console.log(response.responseText);
                    e.record.commit();
                    store_suggestion.reload();
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
    onbtnprint: function (grid, rowIndex) {
        Ext.MessageBox.confirm('Message',
                'Confirmation de l\'impression de cette suggestion',
                function (btn) {
                    if (btn == 'yes') {
                        var rec = grid.getStore().getAt(rowIndex);
                        var lg_SUGGESTION_ORDER_ID = rec.get('lg_SUGGESTION_ORDER_ID');
                        Me.onPdfClick(lg_SUGGESTION_ORDER_ID);
                        return;
                    }
                });

    },
    onPdfClick: function (lg_SUGGESTION_ORDER_ID) {
        var chaine = location.pathname;
        var reg = new RegExp("[/]+", "g");
        var tableau = chaine.split(reg);
        var sitename = tableau[1];
        var linkUrl = url_services_pdf_liste_suggerercde + "?lg_SUGGESTION_ORDER_ID=" + lg_SUGGESTION_ORDER_ID;
        window.open(linkUrl);
    },
    onMakeOrderClick: function (grid, rowIndex) {
        var rec = grid.getStore().getAt(rowIndex);
        //alert("url_services_transaction_suggerercmde "+url_services_transaction_suggerercmde);
        testextjs.app.getController('App').ShowWaitingProcess();
        Ext.Ajax.request({
            url: url_services_transaction_suggerercmde + 'makeorder',
            timeout: 24000000,
            params: {
                lg_SUGGESTION_ORDER_ID: rec.get('lg_SUGGESTION_ORDER_ID'),
                str_STATUT: rec.get('str_STATUT')
            },
            success: function (response)
            {
                testextjs.app.getController('App').StopWaitingProcess();
                var object = Ext.JSON.decode(response.responseText, false);
                if (object.errors_code == "0") {
                    Ext.MessageBox.alert('Error Message', object.errors);
                    return;
                }
                Ext.MessageBox.alert('Confirmation', object.errors);
                grid.getStore().reload();

            },
            failure: function (response)
            {
                testextjs.app.getController('App').StopWaitingProcess();
                var object = Ext.JSON.decode(response.responseText, false);
                //  alert(object);

                console.log("Bug " + response.responseText);
                Ext.MessageBox.alert('Error Message', response.responseText);

            }
        });
    },
    onManageDetailsClick: function (grid, rowIndex) {
        var rec = grid.getStore().getAt(rowIndex);
        var xtype = "suggerercdemanager";
        var alias = 'widget.' + xtype;
        Ext.Ajax.request({
            url: '../webservices/sm_user/suggerercde/ws_transaction.jsp?mode=pending',

            params: {
                lg_SUGGESTION_ORDER_ID: rec.get('lg_SUGGESTION_ORDER_ID')
            },
            success: function (response)
            {
                testextjs.app.getController('App').onLoadNewComponentWithDataSource(xtype, "Suggerer une commande", rec.get('lg_SUGGESTION_ORDER_ID'), rec.data);
            },
            failure: function (response)
            {

                var object = Ext.JSON.decode(response.responseText, false);

                Ext.MessageBox.alert('Error Message', response.responseText);

            }
        });




    },
    onAddClick: function () {
        var xtype = "suggerercdemanager";
        var alias = 'widget.' + xtype;
        testextjs.app.getController('App').onLoadNewComponent(xtype, "Ajouter detail commande", "0");

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
                        _myAppController.ShowWaitingProcess();
                        Ext.Ajax.request({
                            url: url_services_transaction_suggerercmde + 'delete',
                            timeout: 2400000,
                            params: {
                                lg_SUGGESTION_ORDER_ID: rec.get('lg_SUGGESTION_ORDER_ID')
                            },
                            success: function (response)
                            {
                                _myAppController.StopWaitingProcess();
                                var object = Ext.JSON.decode(response.responseText, false);
                                if (object.success === 0) {
                                    Ext.MessageBox.alert('Error Message', object.errors);
                                    return;
                                }
                                grid.getStore().reload();
                            },
                            failure: function (response)
                            {
                                _myAppController.StopWaitingProcess();
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
    onEditClick: function (grid, rowIndex) {
        var rec = grid.getStore().getAt(rowIndex);
        new testextjs.view.sm_user.preenregistrement.action.add({
            odatasource: rec.data,
            parentview: this,
            mode: "update",
            titre: "Modification Suggestion  [" + rec.get('lg_SUGGESTION_ORDER_ID') + "]"
        });



    },
    onRechClick: function () {
        var val = Ext.getCmp('rechecher');
        this.getStore().load({
            params: {
                search_value: val.value
            }
        }, url_services_data_sugg_order_list);
    }

});