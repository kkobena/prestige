
/* global Ext */

url_services_data_detail_facture_tiers_payant = '../webservices/sm_user/facturation/ws_data_detail_tiers_payant.jsp';
var url_services_data_tiers_payant = '../webservices/tierspayantmanagement/tierspayant/ws_data.jsp';



var url_detail_ = '../webservices/sm_user/reglement/ws_details_reglement.jsp';


var odatasource;
var my_url;
var Me;
var Omode;
var ref;
var net = 0;
var lg_DOSSIER_REGLEMENT_ID;
var str_ORGANISME;
var LIBELLE_TYPE_TIERS_PAYANT = "";
var dt_DATE_REGLEMENT;
var HEURE_REGLEMENT;
var str_MONTANT;
var CODE_FACTURE;
var LaborexWorkFlow;

var isHide = true;
Ext.util.Format.decimalSeparator = ',';
Ext.util.Format.thousandSeparator = '.';
function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}
Ext.define('testextjs.view.sm_user.reglement.action.ReglementDetails', {
    extend: 'Ext.form.Panel',
    requires: [
        'Ext.selection.CellModel',
        'Ext.grid.*',
        'Ext.form.*',
        'Ext.layout.container.Column',
        'testextjs.model.Famille',
        'testextjs.model.Grossiste',
        'testextjs.model.OrderDetail',
        'testextjs.controller.LaborexWorkFlow',
        'Ext.ux.CheckColumn',
        'Ext.selection.CheckboxModel',
        'testextjs.model.DossierFacture',
        'testextjs.model.DetailsBorderaux'
        
    ],
    config: {
        odatasource: '',
        parentview: '',
        mode: '',
        titre: '',
        plain: true,
        maximizable: true,
        //tools: [{type: "pin"}],
        // closable: true,
        nameintern: ''
    },
    xtype: 'reglementdetails',
    id: 'reglementdetailsID',
    frame: true,
    title: 'D&eacute;tails  R&eacute;glement',
    bodyPadding: 5,
    layout: 'column',
    initComponent: function() {
        Me = this;

        var itemsPerPage = 20;
        famille_id_search = "";
        in_total_vente = 0;
        int_total_formated = 0;
        isHide = true;
        ref = this.getNameintern();
        odatasource = this.getOdatasource();

        LIBELLE_TYPE_TIERS_PAYANT = odatasource.LIBELLE_TYPE_TIERS_PAYANT;
        dt_DATE_REGLEMENT = odatasource.dt_DATE_REGLEMENT;
        str_MONTANT = odatasource.str_MONTANT;
        lg_DOSSIER_REGLEMENT_ID = odatasource.lg_DOSSIER_REGLEMENT_ID;
        str_ORGANISME = odatasource.str_ORGANISME;
        HEURE_REGLEMENT=odatasource.HEURE_REGLEMENT;
        CODE_FACTURE=odatasource.CODE_FACTURE;
        titre = this.getTitre();
        LaborexWorkFlow = Ext.create('testextjs.controller.LaborexWorkFlow', {});
      



        var store_detail_bordereau = new Ext.data.Store({
            model: 'testextjs.model.DetailsBorderaux',
            pageSize: 10,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_detail_ + "?lg_DOSSIER_REGLEMENT_ID=" + lg_DOSSIER_REGLEMENT_ID,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                }
            }

        });




        var int_NBR_DOSSIER =
                {
                    xtype: 'displayfield',
                    fieldLabel: 'Dossiers ',
                    labelWidth:80,
                    name: 'int_Nbr_Dossier',
                    id: 'int_Nbr_Dossier',
                    fieldStyle: "color:blue;",
                    margin: '0 5 0 15',
                    value: "0"
                };


        Ext.apply(this, {
            width: '98%',
            height: 580,
            cls: 'custompanel',
            fieldDefaults: {
                labelAlign: 'left',
                labelWidth: 120,
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
            // items: ['gridpanelID'],
            items: [
                {
                    xtype: 'fieldset',
                    title: 'Infos Organisme',
                    collapsible: true,
                    flex: 0.4,
//                    margin: '-10 0 0 1',
                    defaultType: 'textfield',
                    layout: 'anchor',
                    defaults: {
                        hideLabel: 'true'
                    },
                    items: [
                        {
                            xtype: 'fieldcontainer',
                            layout: 'hbox',
                            // combineErrors: true,
                            defaultType: 'textfield',
                            defaults: {
                                //hideLabel: 'true'
                            },
                            items: [
                                {
                                    xtype: 'displayfield',
                                    fieldLabel: 'Type organisme :',
                                    labelWidth: 110,
                                    fieldStyle: "color:blue;",
                                    name: 'cmb_TYPE_TIERS_PAYANT',
                                    margins: '0 0 0 10',
                                    id: 'cmb_TYPE_TIERS_PAYANT',
                                }, {
                                    xtype: 'displayfield',
                                    fieldLabel: 'Organisme : ',
                                    fieldStyle: "color:blue;",
                                    name: 'cmb_CUSTOMER_ID',
                                    margin: '0 15 0 15',
                                    id: 'cmb_CUSTOMER_ID'
                                },
                               
                                {
                                    xtype: 'displayfield',
                                    fieldLabel: 'Code Facture: ',
                                    fieldStyle: "color:blue;",
                                    margin: '0 15 0 15',
                                    id: 'CODE_FACTURE'
                                },
                                
                                
                                {
                                    xtype: 'displayfield',
                                    fieldLabel: 'Date R&eacute;glement',
                                    id: 'dt_reglement',
                                    fieldStyle: "color:blue;",
                                    labelWidth: 130,
                                    margin: '0 0 0 15'

                                }

                            ]
                        }]
                },
                {
                    xtype: 'fieldset',
                    id: 'fieldset_information_reglement',
                    title: 'Information R&eacute;glement',
                    collapsible: true,
                    layout: 'hbox',
                    flex: 0.4,
//                    margin: '1 0 0 1',
                    defaultType: 'textfield',
                    defaults: {
                        anchor: '100%'
                    },
                    items: [
                        {
                            xtype: 'displayfield',
                            fieldLabel: 'Montant Total::',
                            labelWidth: 100,
                            margin: '0 5',
                            //name: 'TOTAL_AMOUNT',
                            id: 'TOTAL_AMOUNT',
                            fieldStyle: "color:blue;"
                        }
                        ,
                        int_NBR_DOSSIER



                    ]
                },
                {
                    xtype: 'fieldset',
                    id: 'detaildiffere',
                    title: 'Detail(s) du r&eacute;glement',
                    collapsible: true,
                    flex: 2,
//                    margin: '1 0 0 1',
                    defaultType: 'textfield',
                    layout: 'fit',
                   
                    items: [
                        {
//                            columnWidth: 0.65,
                            xtype: 'gridpanel',
                            id: 'gridDetailBordereau',
//                            hidden: true,
                            //  plugins: [this.cellEditing],
                   
                            store: store_detail_bordereau,
//                            flex:1,
                         
                            columns: [
                                {
                                    text: 'lg_FACTURE_DETAIL_ID',
                                    flex: 1,
                                    sortable: true,
                                    hidden: true,
                                    align: 'right',
                                    dataIndex: 'lg_FACTURE_DETAIL_ID'
                                }, {
                                    text: 'Num Dossier',
                                    flex: 1,
                                    // align: 'right',
                                    dataIndex: 'str_REF'
                                }
                                , {
                                    text: 'Date ',
                                    flex: 1,
                                    // align: 'right',
                                    dataIndex: 'dt_DATE'
                                },
                                {
                                    text: 'Heure ',
                                    flex: 1,
                                    // align: 'right',
                                    dataIndex: 'dt_HEURE'
                                }
                                
                                
                                , {
                                    text: 'Nom & Pr&eacute;nom(s)',
                                    flex: 1,
                                    dataIndex: 'CLIENT_FULL_NAME'
                                            // align: 'right'
                                }, {
                                    text: 'Num Matricule',
                                    flex: 1,
                                    dataIndex: 'CLIENT_MATRICULE',
                                    // align: 'right'
                                }, {
                                    text: 'Montant',
                                    flex: 1,
                                    dataIndex: 'Amount',
                                    renderer: amountformat
                                    // align: 'right'
                                }

                            ],
                            //  selModel: selModel,
                            bbar: {
                                xtype: 'pagingtoolbar',
                                pageSize: 10,
                                store: store_detail_bordereau,
                                displayInfo: true,
                                plugins: new Ext.ux.ProgressBarPager()
                            },
                            listeners: {
                                scope: this,
                                selectionchange: this.onSelectionChange
                            }
                        }
                    ]

                },
                        {
                            xtype: 'toolbar',
                            ui: 'footer',
                            dock: 'bottom',
                            flex:0.2,
                            margin: '-5 0 0 0 ',
                            border: '0',
                            items: ['->',
                                 {
                                    text: 'RETOUR',
                                    id: 'btn_cancel',
                                    iconCls: 'icon-clear-group',
                                    scope: this,
                                    hidden: false,
                                    //disabled: true,
                                    handler: this.onbtncancel
                                }
                            ]
                        }

            ]

        });
        this.callParent();
        this.on('afterlayout', this.loadStore, this, {
            delay: 1,
            single: true
        });

        // initialisation des valeurs par defaut
        
         Ext.getCmp('CODE_FACTURE').setValue(CODE_FACTURE);
        Ext.getCmp('cmb_CUSTOMER_ID').setValue(str_ORGANISME);
        Ext.getCmp('dt_reglement').setValue(dt_DATE_REGLEMENT + " " + HEURE_REGLEMENT);

        Ext.getCmp('cmb_TYPE_TIERS_PAYANT').setValue(LIBELLE_TYPE_TIERS_PAYANT);
        var amount = Ext.util.Format.number(Ext.Number.toFixed(str_MONTANT, 0), '0,000.');
        Ext.getCmp('TOTAL_AMOUNT').setValue(amount + " FCFA");



    },
    loadStore: function() {
        Ext.getCmp('gridDetailBordereau').getStore().load({
            callback: this.onStoreLoad
        });
    },
    onStoreLoad: function() {
        var grid = Ext.getCmp('gridDetailBordereau');


        if (grid.getStore().getCount() > 0) {
            var firstRec = grid.getStore().getAt(0);
            Ext.getCmp('int_Nbr_Dossier').setValue(firstRec.get('int_NB_DOSSIER_RESTANT'));

        }
    },
    onTextFieldSpecialKey: function(field, e, options) {
        if (e.getKey() === e.ENTER) {

        }
    },
    DisplayTotal: function(int_price, int_qte) {
        var TotalAmount_final = 0;
        var TotalAmount_temp = int_qte * int_price;
        var TotalAmount = Number(TotalAmount_temp);
        return TotalAmount;
    },
    DisplayAmount: function() {

    },
    onbtnadd: function() {



    },
    onPdfClick: function() {
        var chaine = location.pathname;
        var reg = new RegExp("[/]+", "g");
        var tableau = chaine.split(reg);
        var sitename = tableau[1];
        var linkUrl = url_services_pdf_ticket + '?lg_PREENREGISTREMENT_ID=' + ref;
        window.open(linkUrl);
        var xtype = "";
        if (my_view_title === "by_cloturer_vente") {
            xtype = "ventemanager";
            testextjs.app.getController('App').onLoadNewComponentWithDataSource(xtype, "", "", "");
        } else {
            xtype = "preenregistrementmanager";
            testextjs.app.getController('App').onLoadNewComponentWithDataSource(xtype, "", "", "");
        }

    },
    changeRenderer: function(val) {
        if (val > 0) {
            return '<span style="color:green;">' + val + '</span>';
        } else if (val < 0) {
            return '<span style="color:red;">' + val + '</span>';
        }
        return val;
    },
    pctChangeRenderer: function(val) {
        if (val > 0) {
            return '<span style="color:green;">' + val + '%</span>';
        } else if (val < 0) {
            return '<span style="color:red;">' + val + '%</span>';
        }
        return val;
    },
    renderRating: function(val) {
        switch (val) {
            case 0:
                return 'A';
            case 1:
                return 'B';
            case 2:
                return 'C';
        }
    },
    onSelectionChange: function(model, records) {
        var rec = records[0];
        if (rec) {
            this.getForm().loadRecord(rec);
        }
    },
    onsplitovalue: function(Ovalue) {

        var int_ovalue;
        var string = Ovalue.split(" ");
        int_ovalue = string[0];
        return int_ovalue;
    },onbtncancel: function() {

        var xtype = "reglementmanager";
        testextjs.app.getController('App').onLoadNewComponent(xtype, "", "");
    },

});


   