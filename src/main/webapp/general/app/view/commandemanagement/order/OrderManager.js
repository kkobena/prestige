/* global Ext */

var url_services_data_order_list = '../webservices/commandemanagement/order/ws_data.jsp';
var url_services_transaction_order = '../webservices/commandemanagement/order/ws_transaction.jsp?mode=';
var url_services_pdf = '../webservices/commandemanagement/order/ws_generate_pdf.jsp';



var Me;
//var store_order;
var val;
var listOrderSelected;
var listGrossisteSelected;
Ext.util.Format.decimalSeparator = ',';
Ext.util.Format.thousandSeparator = '.';
function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}

Ext.define('testextjs.view.commandemanagement.order.OrderManager', {
    extend: 'Ext.grid.Panel',
    xtype: 'i_order_manager',
    id: 'i_order_managerID',
    frame: true,
//    collapsible: true,
    animCollapse: false,
    title: 'Liste des commandes en cours ',
//    iconCls: 'icon-grid',
    plain: true,
    maximizable: true,
    closable: false,
    requires: ['testextjs.view.commandemanagement.bonlivraison.ImportXLS'],
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
     
        url_services_data_order_list = '../webservices/commandemanagement/order/ws_data.jsp?';
        url_services_pdf = '../webservices/commandemanagement/order/ws_generate_pdf.jsp?str_STATUT=is_Process';
        Me = this;
        listOrderSelected = [];
        listGrossisteSelected = [];
        var itemsPerPage = 20;
        var store_order = new Ext.data.Store({
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
                },
                timeout: 180000
            }

        });

        Ext.apply(this, {
            width: '98%',
            height: valheight,
            store: store_order,
            id: 'OderGrid',
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
                    header: '',
                    dataIndex: 'lg_GROSSISTE_ID',
                    flex: 1,
                    hidden: true
                },
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
                    header: 'Nbre.Ligne',
                    dataIndex: 'int_LINE',
                    align: 'right',
                    flex: 0.5
                },
                // int_NBRE_PRODUIT
                {
                    header: 'Nbre.Produits',
                    dataIndex: 'int_NBRE_PRODUIT',
                    align: 'right',
                    flex: 0.5
                },
                {
                    header: 'P.ACHAT',
                    dataIndex: 'PRIX_ACHAT_TOTAL',
                    renderer: amountformat,
                    align: 'right',
                    flex: 0.5
                },
                {
                    header: 'P.VENTE',
                    dataIndex: 'PRIX_VENTE_TOTAL',
                    renderer: amountformat,
                    align: 'right',
                    flex: 0.5
                },
                {
                    header: 'Op&eacute;rateur',
                    dataIndex: 'lg_USER_ID',
                    align: 'center',
                    flex: 1
                },
                {
                    header: 'Date',
                    dataIndex: 'dt_CREATED',
                    flex:0.7
                }, {
                    header: 'Heure',
                    dataIndex: 'dt_UPDATED',
                    flex: 0.5
                },
                {
                    text: '',
                    width: 30,
                    dataIndex: 'isChecked',
                    xtype: 'checkcolumn',
                    listeners: {
                        checkChange: this.onCheckChange
                    }

                },
                {
                    xtype: 'actioncolumn',
                    flex: 2,
                    sortable: false,
                    menuDisabled: true,
                    items: [

                        {
                            icon: 'resources/images/icons/order_tracking.png',
                            tooltip: 'ENVOI PAR PHARMAML',
                            scope: this,
                            hidden:true,
                            handler: this.envoiPharmaML
                        }, '-',
                        
                        
                        {
//                            icon: 'resources/images/icons/fam/passed.png',
                            icon: 'resources/images/icons/fam/folder_go.png',
                            tooltip: 'Commander',
                            scope: this,
                            handler: this.onPasseOrderClick
                        }, '-',
                        {
                            icon: 'resources/images/icons/fam/printer.png',
                            tooltip: 'Imprimer',
                            scope: this,
                            handler: this.onbtnprint
                        }, '-',
                        {
                            icon: 'resources/images/icons/fam/upload_icone.png',
                            tooltip: 'Importer la reponse du grossiste',
                            scope: this,
                            handler: this.onEditOrderByImportClick
                        }, '-',
                        {
                            icon: 'resources/images/icons/fam/excel_csv.png',
                            tooltip: 'Generer le fichier CSV de la commande',
                            scope: this,
                            handler: this.onbtnexport
                        }, '-',
                        {
                            icon: 'resources/images/icons/fam/page_white_edit.png',
                            tooltip: 'Modifier',
                            scope: this,
                            handler: this.onManageDetailsClick
                        }, '-',
                        {
                            icon: 'resources/images/icons/fam/delete.gif',
                            tooltip: 'Supprimer',
                            scope: this,
                            handler: this.onRemoveClick
                        }


                    ]
                }

            ],
            selModel: {
                selType: 'cellmodel'
            },
            tbar: [
                {
                    text: 'NOUVELLE COMMANDE',
                    scope: this,
                    iconCls: 'addicon',
                    handler: this.onAddClick
                }, '-',
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
                    text: 'RECHERCHER',
                    tooltip: 'rechercher',
                    scope: this,
                    iconCls: 'searchicon',
                    handler: this.onRechClick
                }, '-',
                {
                    text: 'IMPORTER UNE NOUVELLE COMMANDE',
                    tooltip: 'Importer une nouvelle commande',
                    scope: this,
                    iconCls: 'importicon',
                    handler: function () {
                        new testextjs.view.commandemanagement.bonlivraison.ImportXLS();
                    }
                }, '-',
                {
                    text: 'VERIFICATION DU FICHIER IMPORTE',
                    tooltip: 'Vérification du fichier importé',
//                        iconCls: 'printable',
                    iconCls: 'importicon',
                    scope: this,
                    menu: [
                        {
                            text: 'V&eacute;rifier le fichier FACTURE 1',
                            iconCls: 'printable',
                            handler: function () {
                                new testextjs.view.commandemanagement.order.action.importOrder({
                                    odatasource: 'TABLE_ORDER',
                                    parentview: this,
                                    mode: "checkimportfile",
                                    titre: "V&eacute;rifier les lignes du fichier qui ne sont pas import&eacute;es",
                                    type: 'format1'
                                });
                            }
                        },
                        {
                            text: 'V&eacute;rifier le fichier FACTURE 2',
                            iconCls: 'importicon',
                            handler: function () {
                                new testextjs.view.commandemanagement.order.action.importOrder({
                                    odatasource: 'TABLE_ORDER',
                                    parentview: this,
                                    mode: "checkimportfile",
                                    titre: "V&eacute;rifier les lignes du fichier qui ne sont pas import&eacute;es",
                                    type: 'format2'
                                });
                            }
                        }
                    ]
                },

                {
                    text: 'FUSIONNER DES COMMANDES',
                    iconCls: 'fusionicon',
                    scope: this,
                    handler: this.funsionCommande
                },
                
                '-', {
                    text: 'Verifier l\'importation',
                    tooltip: 'Verifier l\'importation',
                    id: 'btn_checkimport',
                    iconCls: 'check_icon',
                    scope: this,
                    hidden: true,
                    handler: this.onbtncheckimport
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
    onImportOrderClick1: function () {
        Me.onImportOrderClick("format1");
    },
    onImportOrderClick2: function () {
        Me.onImportOrderClick("format2");
    },
    onVerifyExcelFormat: function () {
        Me.onVerifyImportedFile("format2");
    },
    onVerifyCSVFormat: function () {
        Me.onVerifyImportedCSVFile("format1");
    },

    onbtncheckimport: function () {
        new testextjs.view.commandemanagement.order.action.importOrder({
            odatasource: 'TABLE_ORDER',
            parentview: this,
            mode: "checkimportfile",
            titre: "Verification de l'importation des differents articles de l'officine"
        });
    },
    onImportOrderClick: function (type) {
        new testextjs.view.commandemanagement.order.action.importOrder({
            odatasource: '',
            parentview: this,
            mode: "importfileCreate",
            titre: "Cr&eacute;er une nouvelle commande",
            type: type
        });
    },

    onVerifyImportedFile: function (type) {
        new testextjs.view.commandemanagement.order.action.importOrder({
            odatasource: 'TABLE_ORDER',
            parentview: this,
            mode: "importfileUpdate",
            titre: "V&eacute;rifier les lignes du fichier qui ne sont pas import&eacute;es",
            type: type
        });
    },
    onVerifyImportedCSVFile: function (type) {
        new testextjs.view.commandemanagement.order.action.importOrder({
            odatasource: 'TABLE_ORDER',
            parentview: this,
            mode: "importfileUpdate",
            titre: "V&eacute;rifier les lignes du fichier qui ne sont pas import&eacute;es",
            type: type
        });
    },

    onEditOrderByImportClick: function (grid, rowIndex) {
        var rec = grid.getStore().getAt(rowIndex);
        new testextjs.view.commandemanagement.order.action.importOrder({
            odatasource: rec.data,
            parentview: this,
            mode: "importfileUpdate",
            titre: "Importation de la commande  [" + rec.get('str_REF_ORDER') + "]"
        });
    },
    onManageDetailsClick: function (grid, rowIndex) {
        var rec = grid.getStore().getAt(rowIndex);
        var xtype = "ordermanagerlist";
        var alias = 'widget.' + xtype;
        testextjs.app.getController('App').onLoadNewComponentWithDataSource(xtype, "Modifier les informations de la commande", rec.get('lg_ORDER_ID'), rec.data);
    },
    onbtnprint: function (grid, rowIndex) {

        var rec = grid.getStore().getAt(rowIndex);

        Ext.MessageBox.confirm('Message',
                'Imprimer la commande en cours?',
                function (btn) {
                    if (btn === 'yes') {
                        Me.onPdfClick(rec.get('lg_ORDER_ID'));
                        return;
                    }
                });

    },
    onPdfClick: function (lg_ORDER_ID) {
        var linkUrl = url_services_pdf + '&lg_ORDER_ID=' + lg_ORDER_ID;
        window.open(linkUrl);

    },
    onAddClick: function () {
        var xtype = "ordermanagerlist";
        var alias = 'widget.' + xtype;
        testextjs.app.getController('App').onLoadNewComponentWithDataSource(xtype, "Ajouter les articles a une commande", "0", "is_Process");
    },
    onPasseOrderClick: function (grid, rowIndex) {
        rec = grid.getStore().getAt(rowIndex);
        new testextjs.view.commandemanagement.order.action.manageorderpass({
            odatasource: rec.data,
            nameintern: "Passation de commande",
            parentview: this,
            mode: "passed",
            titre: "Passation de la commande [" + rec.get('str_REF_ORDER') + "]"
        });
    },
    envoiPharmaML: function (grid, rowIndex) {
        var record = grid.getStore().getAt(rowIndex);
        var progress = Ext.MessageBox.wait('Veuillez patienter . . .', 'En cours de traitement!');
        Ext.Ajax.request({
            method: 'PUT',
            timeout: 240000,
            headers: {'Content-Type': 'application/json'},
            url: '../api/v1/pharma/' + record.get('lg_ORDER_ID'),
            success: function (response, options) {

                var runnerPharmaMl = new Ext.util.TaskRunner();
                var result = Ext.JSON.decode(response.responseText, true);
                if (result.success) {
                    var count = 0;
                    var task = runnerPharmaMl.newTask({
                        run: function () {
                            Ext.Ajax.request({
                                method: 'GET',
                                url: '../api/v1/pharma/responseorder',
                                params: {
                                    "orderId": record.get('lg_ORDER_ID')
                                },
                                success: function (response, options) {

                                    const _result = Ext.JSON.decode(response.responseText, true);
                                    if (_result.success) {
                                         task.stop();
                                        progress.hide();
                                        grid.getStore().reload();
                                        Ext.MessageBox.show({
                                            title: 'Info',
                                            width: 320,
                                            msg: "<span style='color: green;'> " + _result.nbreproduit + "</span> produit(s) pris en compte ; <span style='color:red;'>" + _result.nbrerupture + "</span> produit(s) en rupture",
                                            buttons: Ext.MessageBox.OK,
                                            icon: Ext.MessageBox.INFO,
                                            fn: function (buttonId) {
                                                if (buttonId === "ok") {
                                                }
                                            }
                                        });
                                    } else {
                                        if (_result.status === 'responseNotFound') {
                                            if (count < 6) {
                                                task.start();
                                                count++;
                                            } else {
                                                progress.hide();
                                                task.stop();
                                                Ext.MessageBox.show({
                                                    title: 'Info',
                                                    width: 320,
                                                    msg: "Aucune réponse de la part du client PharmaMl après une minute d'attente",
                                                    buttons: Ext.MessageBox.OK,
                                                    icon: Ext.MessageBox.WARNING,
                                                    fn: function (buttonId) {
                                                        if (buttonId === "ok") {

                                                        }
                                                    }
                                                });
                                            }

                                        } else {
                                            progress.hide();
                                            task.stop();
                                        }
                                    }

                                },
                                failure: function (response, options) {
                                    progress.hide();

                                }
                            });

                        },
                        interval: 10000
                    });
                    task.start();
                    count++;
                   
                } else {
                    progress.hide();
                    Ext.MessageBox.show({
                        title: 'Message d\'erreur',
                        width: 320,
                        msg: "ERROR",
                        buttons: Ext.MessageBox.OK,
                        icon: Ext.MessageBox.ERROR

                    });
                }

            },
            failure: function (response, options) {
                progress.hide();
                Ext.Msg.alert("Message", 'Erreur du serveur ' + response.status);
            }

        });
    },

    onbtnexportCsv: function () {
        var str_TYPEREPORT = "csv";

        if (Ext.getCmp('lg_ORDER_ID').getValue() === null) {
            lg_ORDER_ID = "";
        } else {
            lg_ORDER_ID = Ext.getCmp('lg_ORDER_ID').getValue();
        }

        window.location = '../DownloadFileServlet?lg_ORDER_ID=' + lg_ORDER_ID + "&str_TYPEREPORT=" + str_TYPEREPORT;
    },
    onbtnexport: function (grid, rowIndex) {
        Ext.MessageBox.confirm('Message',
                'Voulez-vous generer le fichier CSV de la commande?',
                function (btn) {
                    if (btn === 'yes') {
                        var rec = grid.getStore().getAt(rowIndex);//
                        window.location = '../DownloadFileServlet?lg_ORDER_ID=' + rec.get('lg_ORDER_ID') + '&str_TYPE_ACTION=COMMANDE';
                    }
                });
    },
    onRemoveClick: function (grid, rowIndex) {
        Ext.MessageBox.confirm('Message',
                'Confirmer la suppresssion',
                function (btn) {
                    if (btn === 'yes') {
                        var rec = grid.getStore().getAt(rowIndex);
                        testextjs.app.getController('App').ShowWaitingProcess();
                        Ext.Ajax.request({
                            url: url_services_transaction_order + 'delete',
                            params: {
                                lg_ORDER_ID: rec.get('lg_ORDER_ID')
                            },
                            success: function (response)
                            {

                                testextjs.app.getController('App').StopWaitingProcess();
                                var object = Ext.JSON.decode(response.responseText, false);
                                if (object.success === "0") {
                                    Ext.MessageBox.alert('Error Message', object.errors);
                                    return;
                                } else {
                                    Ext.MessageBox.alert('Confirmation', object.errors);
                                    grid.getStore().reload();
                                }
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
                        return;
                    }
                });

    },
    onRechClick: function () {

        val = Ext.getCmp('rechecher');
        this.getStore().load({
            params: {
                search_value: val.value
            }
        }, url_services_data_order_list);

    },
    onCheckChange: function (column, rowIndex, checked, eOpts) {
        Array.prototype.unset = function (val) {
            var index = this.indexOf(val);
            if (index > -1) {
                this.splice(index, 1);
            }
        };
        var store = Ext.getCmp('OderGrid').getStore();
        var rec = store.getAt(rowIndex); // on recupere la ligne courante de la grid

        if (checked === true) {
            if (listOrderSelected.length === 0) {
                listOrderSelected.push(rec.get('lg_ORDER_ID'));
                listGrossisteSelected.push(rec.get('lg_GROSSISTE_ID'));
            } else {
                if (listGrossisteSelected.indexOf(rec.get('lg_GROSSISTE_ID')) < 0) {
                    Ext.MessageBox.alert('Avertissement', "Veuillez s&eacute;lectionner une commande du m&ecirc;me grossiste");
                    rec.set('isChecked', false);
                } else {
                    listOrderSelected.push(rec.get('lg_ORDER_ID'));
                    listGrossisteSelected.push(rec.get('lg_GROSSISTE_ID'));
                }

            }
        } else {
            listOrderSelected.unset(rec.get('lg_ORDER_ID'));
            listGrossisteSelected.unset(rec.get('lg_GROSSISTE_ID'));

        }

    },
    funsionCommande: function () {
        if (listOrderSelected.length >= 2) {
            testextjs.app.getController('App').ShowWaitingProcess();
            Ext.Ajax.request({
                url: '../webservices/commandemanagement/order/ws_transaction.jsp',
                timeout: 2400000,
                params: {
                    mode: 'mergeOrder',
                    checkedList: Ext.encode(listOrderSelected)
                },
                success: function (response)
                {
                    testextjs.app.getController('App').StopWaitingProcess();
                    var object = Ext.JSON.decode(response.responseText, false);

                    if (object.success === "1") {

                        Ext.MessageBox.alert('Info', "Fusion effectu&eacute;e avec succ&egrave;s");


                    } else {
                        Ext.MessageBox.alert('Error Message', object.success);
                    }
                    listGrossisteSelected = [];
                    listOrderSelected = [];
                    Ext.getCmp('OderGrid').getStore().load();

                },
                failure: function (response)
                {
                    testextjs.app.getController('App').StopWaitingProcess();
                    listGrossisteSelected = [];
                    listOrderSelected = [];
                    Ext.MessageBox.alert('Error Message', "La fusion a &eacute;chou&eacute;e ");
                    Ext.getCmp('OderGrid').getStore().load();
                }

            });
//                checkedList: Ext.encode(checkedList),
        } else {
            Ext.MessageBox.alert('Avertissement', "Veuillez s&eacute;lectionner au moins deux commandes du m&ecirc;me grossiste");
        }

    }

});