var url_services_data_detailsajustement = '../webservices/stockmanagement/ajustementmanagement/ws_data_detail.jsp';
var url_services_pdf_fiche_ajustement = '../webservices/stockmanagement/ajustementmanagement/ws_generate_pdf.jsp';

var Me;
var Omode;
var ref = "0";

Ext.util.Format.decimalSeparator = ',';
Ext.util.Format.thousandSeparator = '.';
function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}


Ext.define('testextjs.view.stockmanagement.ajustementmanagement.action.detailAjustement', {
    extend: 'Ext.form.Panel',
    requires: [
        'Ext.selection.CellModel',
        'Ext.grid.*',
        'Ext.form.*',
        'Ext.layout.container.Column'
    ],
    config: {
        odatasource: '',
        parentview: '',
        mode: '',
        titre: 'Detail de l\'ajustement d\'articles',
        plain: true,
        maximizable: true,
//        tools: [{type: "pin"}],
        closable: false,
        nameintern: ''
    },
    xtype: 'showdetailajustementmanager',
    id: 'showdetailajustementmanagerID',
    frame: true,
    title: 'Ajustement d\'articles',
    bodyPadding: 5,
    layout: 'column',
    initComponent: function() {

        Me = this;
        this.title = this.getTitre();
        my_view_title = this.getTitre();

        ref = this.getNameintern();
       // alert("ref " + ref + " my_view_title " + my_view_title);

        var itemsPerPage = 20;

        //declaration des varibles static
        dt_CREATED = new Ext.form.field.Display(
                {
                    xtype: 'displayfield',
                    //allowBlank: false,
                    fieldLabel: 'Date:',
                    name: 'dt_CREATED',
                    id: 'dt_CREATED',
                    fieldStyle: "color:blue;",
                    flex: 1,
                    margin: '0 15 0 0'
                });

        lg_USER_ID = new Ext.form.field.Display(
                {
                    xtype: 'displayfield',
                    //allowBlank: false,
                    fieldLabel: 'Utilisateur :',
                    name: 'lg_USER_ID',
                    id: 'lg_USER_ID',
                    fieldStyle: "color:blue;",
                    margin: '0 15 0 0',
                    flex: 0.7

                });

        str_COMMENTAIRE = new Ext.form.field.Display(
                {
                    xtype: 'displayfield',
                    //allowBlank: false,
                    fieldLabel: 'Commentaire::',
                    name: 'str_COMMENTAIRE',
                    id: 'str_COMMENTAIRE',
                    fieldStyle: "color:blue;",
                    margin: '0 5 15 0',
                    value: "0"


                });

        //fin declaration des variables static


       

        var store_details = new Ext.data.Store({
            model: 'testextjs.model.DetailsAjustement',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_services_data_detailsajustement + "?lg_AJUSTEMENT_ID=" + ref,
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
            width: 1200,
            fieldDefaults: {
                labelAlign: 'left',
                labelWidth: 90,
                anchor: '100%',
                msgTarget: 'side'
            },
            layout: {
                type: 'vbox',
                align: 'stretch',
                padding: 10
            },
            defaults: {
                flex: 1
            },
            id: 'panelID',
            // items: ['rech_prod', 'gridpanelID'], 
            items: [
                {
                    xtype: 'fieldset',
                    title: 'Informations Generales',
                    collapsible: true,
                    defaultType: 'textfield',
                    layout: 'anchor',
                    defaults: {
                        anchor: '100%'
                    },
                    items: [
                        {
                            xtype: 'fieldcontainer',
                            layout: 'hbox',
                            combineErrors: true,
                            defaultType: 'textfield',
                            defaults: {
//                                hideLabel: 'true'
                            },
                            items: [
                                lg_USER_ID,
                                dt_CREATED
                            ]
                        }
                    ]
                },
                {
                    xtype: 'fieldset',
                    title: 'Liste Produit(s)',
                    collapsible: true,
                    defaultType: 'textfield',
                    layout: 'anchor',
                    defaults: {
                        anchor: '100%'
                    },
                    items: [
                        {
                            columnWidth: 0.65,
                            xtype: 'gridpanel',
                            id: 'gridpanelID',
                            margin: '0 0 5 0',
                            plugins: [this.cellEditing],
                            store: store_details,
                            height: 300,
                            columns: [{
                                    text: 'Details Vente Id',
                                    flex: 1,
                                    sortable: true,
                                    hidden: true,
                                    dataIndex: 'lg_AJUSTEMENTDETAIL_ID',
                                    id: 'lg_AJUSTEMENTDETAIL_ID'
                                }, {
                                    text: 'Famille',
                                    flex: 1,
                                    sortable: true,
                                    hidden: true,
                                    dataIndex: 'lg_FAMILLE_ID'
                                }, {
                                    xtype: 'rownumberer',
                                    text: 'Ligne',
                                    width: 45,
                                    sortable: true/*,
                                     locked: true*/
                                }, {
                                    text: 'CIP',
                                    flex: 1,
                                    sortable: true,
                                    dataIndex: 'int_CIP'
                                }, {
                                    text: 'EAN',
                                    flex: 1,
                                    sortable: true,
                                    hidden: true,
                                    dataIndex: 'int_EAN13'
                                }, {
                                    text: 'Designation',
                                    flex: 2,
                                    sortable: true,
                                    dataIndex: 'str_FAMILLE_NAME'
                                }, {
                                    header: 'Quantit&eacute;',
                                    dataIndex: 'int_QUANTITY',
                                    flex: 1,
                                    editor: {
                                        xtype: 'numberfield',
                                        allowBlank: false,
                                        regex: /[0-9.]/
                                    }
                                }, {
                                    text: 'Stock.avant.Ajus.',
                                    flex: 1,
                                    sortable: true,
                                    dataIndex: 'int_QUANTITY_SERVED',
                                    renderer: amountformat,
                                    align: 'center'
                                }, {
                                    text: 'Stock.apres.Ajus.',
                                    flex: 1,
                                    sortable: true,
                                    dataIndex: 'int_T',
                                    renderer: amountformat,
                                    align: 'center'
                                }, {
                                    text: 'QS',
                                    flex: 1,
                                    hidden: true,
                                    sortable: true,
                                    dataIndex: 'int_QUANTITY_SERVED'
                                }, {
                                    text: 'P.U',
                                    flex: 1,
                                    sortable: true,
                                    dataIndex: 'int_S',
                                    renderer: amountformat,
                                    align: 'right'
                                }, {
                                    text: 'PAF',
                                    flex: 1,
                                    sortable: true,
                                    dataIndex: 'int_FAMILLE_PRICE',
                                    renderer: amountformat,
                                    align: 'center'
                                }, {
                                    text: 'T',
                                    flex: 1,
                                    hidden: true,
                                    sortable: true,
                                    dataIndex: 'int_T'


                                }, {
                                    text: 'Valorisation',
                                    flex: 1,
                                    sortable: true,
                                    dataIndex: 'int_PRICE_DETAIL',
                                    renderer: amountformat,
                                    align: 'center'
                                }],
                            tbar: [
                                {
                                    xtype: 'textfield',
                                    id: 'rechecher',
                                    name: 'rechecher',
                                    emptyText: 'Recherche article',
                                    listeners: {
                                        render: function(cmp) {
                                            cmp.getEl().on('keypress', function(e) {
                                                if (e.getKey() === e.ENTER) {
                                                    Me.onfiltercheck(cmp.getValue());
                                                }
                                            });
                                        }
                                    }
                                }
                            ],
                            bbar: {
                                xtype: 'pagingtoolbar',
                                pageSize: 10,
                                store: store_details,
                                displayInfo: true,
                                plugins: new Ext.ux.ProgressBarPager()
                            },
                            listeners: {
                                scope: this,
                                // selectionchange: this.onSelectionChange
                            }
                        }]
                }, {
                    xtype: 'fieldset',
                    title: 'Espace commentaire',
                    layout: 'hbox',
                    collapsible: true,
                    defaultType: 'textfield',
                    defaults: {
                        hideLabel: 'true'
                    },
                    items: [//
                        str_COMMENTAIRE
                    ]
                },
                {
                    xtype: 'toolbar',
                    ui: 'footer',
                    dock: 'bottom',
                    border: '0',
                    items: ['->', {
                            text: 'Retour',
                            id: 'btn_back',
                            iconCls: 'icon-clear-group',
                            scope: this,
                            handler: this.onbtnback
                        }, {
                            text: 'Imprimer',
                            id: 'btn_loturer',
                            iconCls: 'icon-clear-group',
                            scope: this,
                            handler: this.onbtnprint
                        }]
                }]
        });
        this.callParent();
        this.on('afterlayout', this.loadStore, this, {
            delay: 1,
            single: true
        });

        if (my_view_title === "by_detail_ajustementmanagement") {
            Ext.getCmp('lg_USER_ID').setValue(this.getOdatasource().lg_USER_ID);
            Ext.getCmp('dt_CREATED').setValue(this.getOdatasource().dt_CREATED);
            Ext.getCmp('str_COMMENTAIRE').setValue(this.getOdatasource().str_COMMENTAIRE);
            this.title = "Detail de la fiche d'ajustement ::  " + this.getOdatasource().str_NAME;
            ref = this.getOdatasource().lg_AJUSTEMENT_ID;


        }


    },
    loadStore: function() {
        Ext.getCmp('gridpanelID').getStore().load({
            callback: this.onStoreLoad
        });
    },
    onfiltercheck: function(valeur) {

        var OGrid = Ext.getCmp('gridpanelID');
        OGrid.getStore().getProxy().url = url_services_data_detailsajustement + "?lg_AJUSTEMENT_ID=" + ref + "&search_value=" + valeur;
        OGrid.getStore().reload();
        OGrid.getStore().getProxy().url = url_services_data_detailsajustement + "?lg_AJUSTEMENT_ID=" + ref;
    },
    onStoreLoad: function() {
        var OGrid = Ext.getCmp('gridpanelID');
        // alert("Nombre d'element " + OGrid.getStore().getCount());
        if (OGrid.getStore().getCount() > 0) {
            Ext.getCmp('btn_loturer').enable();
        } else {
            Ext.getCmp('btn_loturer').disable();
        }
    },
    onbtnback: function() {
        var xtype = "";
        xtype = "ajustementmanager";
        testextjs.app.getController('App').onLoadNewComponentWithDataSource(xtype, "", "", "");
    },
    onbtnprint: function () {
        Ext.MessageBox.confirm('Message',
                'Confirmation de l\'impression de l\'ajustement',
                function (btn) {
                    if (btn == 'yes') {
                        Me.onPdfAjustementClick();
                        return;
                    }
                });

    },
    onPdfAjustementClick: function() {
        var chaine = location.pathname;
        var reg = new RegExp("[/]+", "g");
        var tableau = chaine.split(reg);
        var sitename = tableau[1];
        var linkUrl = url_services_pdf_fiche_ajustement + '?lg_AJUSTEMENT_ID=' + ref;
        window.open(linkUrl);

        Me.onbtnback();

    },
    onSelectionChange: function(model, records) {
        var rec = records[0];
        if (rec) {
            this.getForm().loadRecord(rec);
        }
    }

});




