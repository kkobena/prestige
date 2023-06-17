var url_services_data_bl_list = '../webservices/commandemanagement/bonlivraison/ws_data.jsp';
var url_services_pdf_bonlivraison = '../webservices/commandemanagement/bonlivraison/ws_generate_pdf.jsp';
var Me;
var store_bl;
var myAppController;
Ext.util.Format.decimalSeparator = ',';
Ext.util.Format.thousandSeparator = '.';
function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}

Ext.define('testextjs.view.commandemanagement.bonlivraison.BonLivraisonManager', {
    extend: 'Ext.grid.Panel',
    xtype: 'bonlivraisonmanager',
    id: 'bonlivraisonmanagerID',
    frame: true,
//    collapsible: true,
    animCollapse: false,
    title: 'Gestion des entrees',
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
        myAppController = Ext.create('testextjs.controller.App', {});
        url_services_data_bl_list = '../webservices/commandemanagement/bonlivraison/ws_data.jsp';
        //  alert("url_services_data_bl_list "+url_services_data_bl_list);

        Me = this;
      
        var itemsPerPage = 20;
        store_bl = new Ext.data.Store({
            model: 'testextjs.model.BonLivraison',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_services_data_bl_list + "?str_STATUT=enable",
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                },
                timeout: 240000
            }

        });

        Ext.apply(this, {
            width: '98%',
            height: valheight,
            store: store_bl,
            id: 'gridpanelLivraisonID',
            columns: [
                {
                    header: 'lg_BON_LIVRAISON_ID',
                    dataIndex: 'lg_BON_LIVRAISON_ID',
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
                    dataIndex: 'str_REF_LIVRAISON',
                    flex: 1
                },
                {
                    header: 'Grossiste',
                    dataIndex: 'str_GROSSISTE_LIBELLE',
                    flex: 1
                },
                {
                    header: 'Ref.CMDE',
                    dataIndex: 'str_REF_ORDER',
                    flex: 1
                },
                // int_NBRE_LIGNE_BL_DETAIL
                {
                    header: 'Lignes',
                    dataIndex: 'int_NBRE_LIGNE_BL_DETAIL',
                    align: 'right',
                    flex: 0.5
                },
       
                {
                    header: 'Date',
                    dataIndex: 'dt_DATE_LIVRAISON',
                    align: 'right',
                    flex: 1
                },
                {
                    header: 'MONTANT.HT',
                    dataIndex: 'PRIX_ACHAT_TOTAL',
//                    renderer: amountformat,
                    align: 'right',
                    flex: 1,
                    renderer: function (value, metadata, record) {

                        if (record.get('PRIX_ACHAT_TOTAL') != record.get('int_MHT')) {
                            return '<span style="color: red">' + value + '</span>';
                        } else {
                            return value;
                        }
                    }
                },
                {
                    header: 'PRIX.BL.HT',
                    dataIndex: 'int_MHT',
//                    renderer: amountformat,
                    align: 'right',
                    flex: 1
                },
                {
                    header: 'TVA',
                    dataIndex: 'int_TVA',
                    align: 'right',
//                    renderer: amountformat,
                    flex: 1
                },
                {
                    header: 'PRIX.BL.TTC',
                    dataIndex: 'int_HTTC',
//                    renderer: amountformat,
                    align: 'right',
                    flex: 1
                },
                {
                    header: 'Op&eacute;rateur',
                    dataIndex: 'lg_USER_ID',
                    align: 'center',
                    flex: 1
                },
                {
                    xtype: 'actioncolumn',
                    //width: 80,
                    flex: 1,
                    sortable: false,
                    menuDisabled: true,
                    items: [{
                            icon: 'resources/images/icons/fam/folder_go.png',
                            tooltip: 'Detail livraison',
                            scope: this,
                            handler: this.onManageDetailsClick
                        },/* '-', {
                            icon: 'resources/images/icons/fam/printer.png',
                            tooltip: 'Edition de bon de livraison',
                            scope: this,
                            hidden: true
                            handler: this.onPdfDetailClick
                            
                        },*/ '-', {
                            icon: 'resources/images/icons/fam/delete.png',
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
                    xtype: 'textfield',
                    id: 'rechecher',
                    name: 'suggestion',
                    flex: 1,
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
                },'-',
              
                {
                    text: 'rechercher',
                    tooltip: 'rechercher',
                    iconCls: 'searchicon',
                    scope: this,
                    handler: this.onRechClick
                }
            ],
            bbar: {
                xtype: 'pagingtoolbar',
                pageSize: 10,
                store: store_bl,
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
                url: url_services_data_bl_list + 'update',
                timeout: 240000,
                params: {
                    lg_BON_LIVRAISON_ID: e.record.data.lg_BON_LIVRAISON_ID,
                    str_REF_LIVRAISON: e.record.data.str_REF_LIVRAISON,
                    lg_GROSSISTE_ID: e.record.data.lg_GROSSISTE_ID,
                    int_NUMBER: e.record.data.int_NUMBER
                },
                success: function (response)
                {
                    console.log(response.responseText);
                    e.record.commit();
                    store_bl.reload();
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
    onManageDetailsClick: function (grid, rowIndex) {

        var rec = grid.getStore().getAt(rowIndex);
        var xtype = "bonlivraisondetail";
        var alias = 'widget.' + xtype;
        testextjs.app.getController('App').onLoadNewComponentWithDataSource(xtype, "Details de la livraison", rec.get('lg_BON_LIVRAISON_ID'), rec.data);
    },
    onAddClick: function () {
        var xtype = "ordermanagerlist";
        var alias = 'widget.' + xtype;
        testextjs.app.getController('App').onLoadNewComponent(xtype, "Ajouter detail commande", "0");

    },
    onPdfDetailClick: function (grid, rowIndex) {

        var rec = grid.getStore().getAt(rowIndex);
        var linkUrl = url_services_pdf_bonlivraison + '?lg_BON_LIVRAISON_ID=' + rec.get('lg_BON_LIVRAISON_ID') + "&title=Edition de bon de livraison&str_STATUT=enable";
        testextjs.app.getController('App').onLunchPrinterBis(linkUrl);

    },
    onRemoveClick: function (grid, rowIndex) {
        Ext.MessageBox.confirm('Message',
                'confirm la suppresssion',
                function (btn) {
                    if (btn === 'yes') {
                        var rec = grid.getStore().getAt(rowIndex);
                        myAppController.ShowWaitingProcess();
                        Ext.Ajax.request({
                            timeout: 240000,
                            url: '../webservices/commandemanagement/bonlivraison/ws_transaction.jsp?mode=disable',
                            params: {
                                lg_BON_LIVRAISON_ID: rec.get('lg_BON_LIVRAISON_ID')
                            },
                            success: function (response)
                            {
                                myAppController.StopWaitingProcess();
                                var object = Ext.JSON.decode(response.responseText, false);

                                if (object.success === 0) {
                                    Ext.MessageBox.alert('Error Message', "Suppression a &eacute;Chou&eacute;e");
                                    return;
                                } else {
                                    Ext.MessageBox.alert('Succes', "Suppression &eacute;ffectu&eacute;e avec succ&egrave;s");

                                }
                                grid.getStore().reload();
                            },
                            failure: function (response)
                            {
                                grid.getStore().reload();

                            }
                        });
                        return;
                    }
                });

    },
   
    onRechClick: function () {
        var val = Ext.getCmp('rechecher');
        this.getStore().load({
            timeout: 240000,
            params: {
                search_value: val.value
            }
        }, url_services_data_bl_list);
    }

});