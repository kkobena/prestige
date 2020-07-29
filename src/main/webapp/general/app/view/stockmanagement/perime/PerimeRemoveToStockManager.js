var url_services_data_perimeremovestock = '../webservices/stockmanagement/perime/ws_data_removetostock.jsp';
var url_services_data_perime_remove_generate_pdf = '../webservices/stockmanagement/perime/ws_generate_removeperime_pdf.jsp';
var valdatedebut;
var valdatefin;
var store_;
var Me;
Ext.define('testextjs.view.stockmanagement.perime.PerimeRemoveToStockManager', {
    extend: 'Ext.grid.Panel',
    xtype: 'perimeremovetostock',
    id: 'perimeremovetostockID',
    requires: [
        'Ext.selection.CellModel',
        'Ext.grid.*',
        'Ext.window.Window',
        'Ext.data.*',
        'Ext.util.*',
        'Ext.form.*',
        'Ext.JSON.*',
        'Ext.ux.ProgressBarPager',
        'Ext.ux.grid.Printer'

    ],
    title: 'Liste des perimes retires du stock',
    plain: true,
    maximizable: true,
//    tools: [{type: "pin"}],
    closable: false,
    frame: true,
    initComponent: function () {
         valdatedebut = "";
 valdatefin = "";
        var itemsPerPage = 20;
        Me = this;
        store_ = new Ext.data.Store({
            model: 'testextjs.model.FamilleStock',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_services_data_perimeremovestock,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                }
            }

        });

        var store_famille = new Ext.data.Store({
            model: 'testextjs.model.Famille',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_services_data_article,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                }
            }

        });

        var store_grossiste = new Ext.data.Store({
            model: 'testextjs.model.Grossiste',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_services_data_grossiste,
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
            store: store_,
            id: 'GridPerimeID',
            columns: [{
                    xtype: 'rownumberer',
                    text: 'Num.Ligne',
                    width: 45,
                    hidden: true,
                    sortable: true/*,
                     locked: true*/
                }, {
                    header: 'lg_FAMILLE_ID',
                    dataIndex: 'lg_FAMILLE_ID',
                    hidden: true,
                    flex: 1/*,
                     editor: {
                     allowBlank: false
                     }*/
                }, {
                    header: 'CIP',
                    dataIndex: 'int_CIP',
                    flex: 0.5/*,
                     editor: {
                     allowBlank: false  
                     }*/
                },{
                    header: 'Article',
                    dataIndex: 'str_NAME',
                    flex: 3/*,
                     editor: {
                     allowBlank: false
                     }*/
                }, {
                    header: 'lg_WAREHOUSE_ID',
                    dataIndex: 'lg_WAREHOUSE_ID',
                    hidden: true,
                    flex: 1/*,
                     editor: {
                     allowBlank: false
                     }*/
                },
                {
                    header: 'Famille article',
                    dataIndex: 'lg_FAMILLEARTICLE_ID',
                    hidden: true,
                    flex: 1/*,
                     editor: {
                     allowBlank: false
                     }*/

                }, {
                    header: 'Emplacement',
                    dataIndex: 'lg_ZONE_GEO_ID',
//                    hidden: true,
                    flex: 1/*,
                     editor: {
                     allowBlank: false  
                     }*/
                }, {
                    header: 'Repartiteur',
                    dataIndex: 'lg_GROSSISTE_ID',
                    hidden: true,
                    flex: 1/*,
                     editor: {
                     allowBlank: false  
                     }*/
                }, {
                    header: 'Date retrait',
                    dataIndex: 'dt_UPDATED',
                    flex: 0.7/*,
                     editor: {
                     allowBlank: false  
                     }*/
                }, {
                    header: 'Date p&eacute;remption',
                    dataIndex: 'dt_PEREMPTION',
                    flex: 0.7/*,
                     editor: {
                     allowBlank: false  
                     }*/
                }, {
                    header: 'Num.Lot',
                    dataIndex: 'str_CODE_TAUX_REMBOURSEMENT',
                    flex: 0.7
                }, {
                    header: 'Qte.Retir&eacute;',
                    dataIndex: 'int_NUMBER_AJUSTEMENT_IN',
                    flex: 0.7
                }, {
                    header: 'Periode',
                    dataIndex: 'lg_CODE_ACTE_ID',
                    hidden:true,
                    flex: 1.2/*,
                     editor: {
                     allowBlank: false  
                     }*/
                }],
            selModel: {
                selType: 'cellmodel'
            },
            tbar: [{
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
                }, {
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
                }, '-', {
                    xtype: 'textfield',
                    id: 'rechecher',
                    name: 'facture',
                    emptyText: 'Rech'
                }, {
                    text: 'rechercher',
                    tooltip: 'rechercher',
                    iconCls: 'searchicon',
                    scope: this,
                    handler: this.onRechClick
                },'-', {
                    text: 'Imprimer',
                    tooltip: 'Imprimer',
                    scope: this,
                      iconCls: 'importicon',
                    handler: this.onPdfClick
                }],
            bbar: {
                xtype: 'pagingtoolbar',
                store: store_, // same store GridPanel is using
                dock: 'bottom',
                displayInfo: true
            }
        });

        this.callParent();


        this.on('afterlayout', this.loadStore, this, {
            delay: 1,
            single: true
        })


    },
    loadStore: function () {
        this.getStore().load({
//            callback: this.onStoreLoad
        });

    },
    onRechClick: function () {
        var val = Ext.getCmp('rechecher');
        
         if (new Date(valdatedebut) > new Date(valdatefin)) {
         Ext.MessageBox.alert('Erreur au niveau date', 'La date de d&eacute;but doit &ecirc;tre inf&eacute;rieur &agrave; la date fin');
         return;
         }


        this.getStore().load({
            params: {
                search_value: val.getValue(),
                datedebut: valdatedebut,
                 datefin: valdatefin
            }
        }, url_services_data_perimeremovestock);
    },
     onPdfClick: function() {
        
        var chaine = location.pathname;
        var reg = new RegExp("[/]+", "g");
        var tableau = chaine.split(reg);
        var sitename = tableau[1];
        var linkUrl = url_services_data_perime_remove_generate_pdf + "?datedebut="+valdatedebut+"&datefin="+valdatefin+"&search_value="+Ext.getCmp('rechecher').getValue();

        window.open(linkUrl);
    }

});