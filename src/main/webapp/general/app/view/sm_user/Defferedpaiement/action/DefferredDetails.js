


/* global Ext */

var odatasource;
var my_url;
var Me;
var Omode;
var ref;
var net = 0;
var lg_DEFFERED_ID;
var str_ORGANISME;
var ORGANISME = "";
var DATEREGL;
var HEUREREGL;
var MONTANTREGL;
var CODE_FACTURE;
var LaborexWorkFlow;


function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}
Ext.define('testextjs.view.sm_user.Defferedpaiement.action.DefferredDetails', {
    extend: 'Ext.form.Panel',
    requires: [
        'Ext.selection.CellModel',
        'Ext.grid.*',
        'Ext.form.*',
       'testextjs.controller.LaborexWorkFlow',
       'testextjs.model.DetailsBorderaux'
   ],
    config: {
        odatasource: '',
        parentview: '',
        mode: '',
        titre: '',
        plain: true,
        maximizable: true,
        
        nameintern: ''
    },
    xtype: 'defferreddetails',
    id: 'defferreddetailsID',
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

        ORGANISME = odatasource.ORGANISME;
        DATEREGL = odatasource.DATEREGL;
        MONTANTREGL = odatasource.MONTANTREGL;
        lg_DEFFERED_ID = odatasource.lg_DEFFERED_ID;
        HEUREREGL=odatasource.HEUREREGL;
        titre = this.getTitre();
        LaborexWorkFlow = Ext.create('testextjs.controller.LaborexWorkFlow', {});
      
  var store_detail_bordereau = new Ext.data.Store({
            model: 'testextjs.model.DetailsBorderaux',
            pageSize: 5,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url:'../webservices/sm_user/defferredpayment/ws_details_deffered.jsp?lg_DEFFERED_ID=' + lg_DEFFERED_ID,
                reader: {
                    type: 'json',
                    root: 'data',
                    totalProperty: 'total'
                },
                timeout: 2400000
            }

        });




        var int_NBR_DOSSIER =
                {
                    xtype: 'displayfield',
                    fieldLabel: 'Nombre de Dossiers ',
                    
                    name: 'int_Nbr_Dossier',
                    id: 'int_Nbr_Dossier',
                    fieldStyle: "color:blue;font-weight:800;",
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
                    title: 'Infos Client',
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
                                    fieldLabel: 'Nom&Pr&eacute;nom(s):',
                                    labelWidth: 130,
                                    fieldStyle: "color:blue;font-weight:800;",
                                    name: 'cmb_TYPE_TIERS_PAYANT',
                                    margins: '0 0 0 10',
                                    id: 'cmb_TYPE_TIERS_PAYANT',
                                }, 
                               
                                
                                
                                {
                                    xtype: 'displayfield',
                                    fieldLabel: 'Date R&eacute;glement',
                                    id: 'dt_reglement',
                                    fieldStyle: "color:blue;font-weight:800;",
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
                            fieldLabel: 'Montant Total:',
                            labelWidth: 100,
                            margin: '0 5',
                            
                            id: 'TOTAL_AMOUNT',
                            fieldStyle: "color:blue;font-weight:800;"
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
                                    text: 'Montant',
                                    flex: 1,
                                    dataIndex: 'Amount',
                                    renderer: amountformat,
                                    align: 'right'
                                }

                            ],
                            //  selModel: selModel,
                            bbar: {
                                xtype: 'pagingtoolbar',
                                pageSize: 10,
                                store: store_detail_bordereau,
                                displayInfo: true,
                                plugins: new Ext.ux.ProgressBarPager()
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
        
        Ext.getCmp('dt_reglement').setValue(DATEREGL + " " + HEUREREGL);

        Ext.getCmp('cmb_TYPE_TIERS_PAYANT').setValue(ORGANISME);
        var amount = Ext.util.Format.number(Ext.Number.toFixed(MONTANTREGL, 0), '0,000.');
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
    }
 ,onbtncancel: function() {

        var xtype = "deferredpayment";
        testextjs.app.getController('App').onLoadNewComponent(xtype, "", "");
    }

});


   