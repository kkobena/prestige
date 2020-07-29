/* global Ext */


var Me;
var view_title;

Ext.util.Format.decimalSeparator = ',';
Ext.util.Format.thousandSeparator = '.';
function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}

Ext.define('testextjs.view.tierspayantmanagement.tierspayant.DetailsVentesTP', {
    extend: 'Ext.grid.Panel',
    xtype: 'tpventes',
    id: 'tpventesID',
    title: 'LISTTE DES BORDEREAUX',
    requires: [
        'Ext.selection.CellModel',
        'Ext.grid.*',
        'Ext.window.Window',
        'Ext.data.*',
        'Ext.util.*',
        'Ext.form.*',
        'Ext.JSON.*',
        'testextjs.model.VenteTP',
        'testextjs.model.statistics.TiersPayant'
    ],
    frame: true,
    initComponent: function () {

        var searchstore = Ext.create('testextjs.store.Statistics.TiersPayans');




        Me = this;

        var itemsPerPage = 20;
        var store = Ext.create('testextjs.store.Statistics.TPStore');
        store.on({
            'load': {
                fn: function (store, records, success, eOpts) {
                    Ext.each(records, function (record, index, records) {
                        if (index > 0) {

                            return;

                        }
                        Ext.getCmp('TOTALBON').setValue(record.get('TOTALBON'));
                        Ext.getCmp('TOTALMONTANT').setValue(record.get('TOTALMONTANT'));

                    }

                    , this);

                },
                scope: this
            }
        });




        Ext.apply(this, {
            width: '98%',
//            cls: 'custompanel',
            // height:Ext.getBody().getViewSize().height*0.85,
            minHeight: 570,
            maxHeight: 570,
            cls: 'custompanel',
            id: 'Grid_tpvente_ID',
           

            store: store,
            columns: [{
                    header: 'CODE ORGANISME',
                    dataIndex: 'CODE',
                    flex: 1

                },
                {
                    header: 'NOM TIERS-PAYANT',
                    dataIndex: 'TPNAME',
                    flex: 1

                }


                , {
                    header: 'NBRE DOSSIERS',
                    dataIndex: 'NBBON',
                     renderer: amountformat,
                    align: 'right',

                    flex: 1
                }
                , {
                    header: 'MONTANT ATTENDU',
                    dataIndex: 'MONTANTVENTE',
                     renderer: amountformat,
                    align: 'right',
                    flex:1

                }
             
            
            ],
            selModel: {
                selType: 'cellmodel'
            },
            tbar: [
                 {
                    xtype: 'datefield',
                    id: 'datedebut',
                    name: 'datedebut',
                    emptyText: 'Date debut',
                    submitFormat: 'Y-m-d',
                    maxValue: new Date(),
                    format: 'd/m/Y',
                    listeners: {
                        'change': function (me) {

                            valdatedebut = me.getSubmitValue();
                            Ext.getCmp('datefin').setMinValue(this.getValue());

                        }
                    }
                }, {
                    xtype: 'datefield',
                    id: 'datefin',
                    name: 'datefin',
                    emptyText: 'Date fin',
                    maxValue: new Date(),
                    submitFormat: 'Y-m-d',
                    format: 'd/m/Y',
                    listeners: {
                        'change': function (me) {
                            //alert(me.getSubmitValue());
                            valdatefin = me.getSubmitValue();
                            Ext.getCmp('datedebut').setMaxValue(this.getValue());

                        }
                    }
                },
                
                
                
                
                
                {
                    xtype: 'textfield',
                    id: 'TP_search',

                    width: 150,
                    emptyText: 'Rech',
                    listeners: {
                        'render': function (cmp) {
                            cmp.getEl().on('keypress', function (e) {
                                if (e.getKey() === e.ENTER) {

                                    var TP_search = Ext.getCmp('TP_search').getValue();
                                    var OGrid = Ext.getCmp('Grid_tpvente_ID'), lgTP = '';

                                    var dt_fin = '', dt_debut = '';
                                    if (Ext.getCmp('CBlgTIERSPAYANTID').getValue() !== null && Ext.getCmp('CBlgTIERSPAYANTID').getValue() !== "") {
                                        lgTP = Ext.getCmp('CBlgTIERSPAYANTID').getValue();
                                    }

                                    if (Ext.getCmp('datefin').getSubmitValue() !== null && Ext.getCmp('datefin').getSubmitValue() !== "") {
                                        dt_fin = Ext.getCmp('datefin').getSubmitValue();
                                    }


                                    if (Ext.getCmp('datedebut').getSubmitValue() !== null && Ext.getCmp('datedebut').getSubmitValue() !== "") {
                                        dt_debut = Ext.getCmp('datedebut').getSubmitValue();
                                    }
                                    OGrid.getStore().load({
                                        params: {
                                            lgTP: lgTP,
                                            dt_end_vente: dt_fin,
                                            dt_start_vente: dt_debut,
                                            search_value: TP_search

                                        }
                                    });



                                }
                            });
                        }
                    }
                }, {
                    xtype: 'combobox',
                    //fieldLabel: 'Tiers payant',
                    //allowBlank: false,

                    margins: '0 0 0 10',
                    id: 'CBlgTIERSPAYANTID',
                    //  store: store_client,
                    store: searchstore,
                    //disabled: true,
                    valueField: 'lg_TIERS_PAYANT_ID',
                    displayField: 'str_FULLNAME',
                    typeAhead: false,
                    queryMode: 'remote',
                    pageSize: 10,
                    minChars: 2,
                    flex: 1,
                    enableKeyEvents: true,
                    emptyText: 'Sectionner un tiers-payant...',
                    listeners: {
                        keypress: function (field, e) {

                            if (e.getKey() === e.BACKSPACE || e.getKey() === 46) {

                                if (field.getValue().length <= 2) {
                                    field.getStore().load();
                                }
                                // alert(e.BACKSPACE);
                            }

                        },
                        select: function (cmp) {
                            var value = cmp.getValue();

                            var OGrid = Ext.getCmp('Grid_tpvente_ID');
                            var dt_fin = '', dt_debut = '';
                            if (Ext.getCmp('datefin').getSubmitValue() !== null && Ext.getCmp('datefin').getSubmitValue() !== "") {
                                dt_fin = Ext.getCmp('datefin').getSubmitValue();
                            }

                            if (Ext.getCmp('datedebut').getSubmitValue() !== null && Ext.getCmp('datedebut').getSubmitValue() !== "") {
                                dt_debut = Ext.getCmp('datedebut').getSubmitValue();
                            }
                            OGrid.getStore().load({
                                params: {
                                    lgTP: this.getValue(),
                                    dt_end_vente: dt_fin,
                                    dt_start_vente: dt_debut,
                                    search_value: Ext.getCmp('TP_search').getValue()

                                }
                            });


                        }

                    }
                }, {
                    text: 'rechercher',
                    tooltip: 'rechercher',
                    iconCls: 'searchicon',
                    scope: this,
                    handler: this.onRechDifClick
                }
                , {
                    text: 'Imprimer',
                    tooltip: 'Imprimer',
                    iconCls: 'importicon',
                    scope: this,

                    handler: this.onPdfClick
                }


            ],
            bbar: {
                xtype: 'pagingtoolbar',
                store: store, // same store GridPanel is using
                dock: 'bottom',
                displayInfo: true,
                listeners: {
                    beforechange: function (page, currentPage) {
                        var myProxy = this.store.getProxy();
                        myProxy.params = {
                            dt_start_vente: '',
                            dt_end_vente: '',
                            search_value: '',
                            lgTP: ''
                        };
                        var search_value = Ext.getCmp('TP_search').getValue();


                        myProxy.setExtraParam('dt_start_vente', Ext.getCmp('datedebut').getSubmitValue());
                        myProxy.setExtraParam('dt_end_vente', Ext.getCmp('datefin').getSubmitValue());
                        myProxy.setExtraParam('search_value', search_value);
                        myProxy.setExtraParam('lgTP', Ext.getCmp('CBlgTIERSPAYANTID').getValue());

                    }

                },

                items: [
                 
                    {
                        xtype: 'displayfield',
                        fieldLabel: 'NOMBRE BON',
                        labelWidth: 135,
                        id: 'TOTALBON',
                        fieldStyle: "color:blue;font-weight:800;",
                        margin: '0 10 0 10',
                        renderer: function (value) {
                            return Ext.util.Format.number(value, '0,000') + " ";
                        }
                    },
                    
                    {
                        xtype: 'displayfield',
                        fieldLabel: 'MONTANT ATTENDU',
                        labelWidth: 150,
                        id: 'TOTALMONTANT',
                        fieldStyle: "color:blue;font-weight:800;",
                        margin: '0 10 0 10',
                        renderer: function (value) {
                            return Ext.util.Format.number(value, '0,000') + " FCFA";
                        }
                    }
                    


                ]
            }


        });

        this.callParent();

        this.on('afterlayout', this.loadStore, this, {
            delay: 1,
            single: true
        });



    },
    loadStore: function () {
        this.getStore().load({
            callback: this.onStoreLoad
        });
    },
    onStoreLoad: function () {
    },

    onRechDifClick: function () {
        var lg_TIERS_PAYANT_ID = Ext.getCmp('CBlgTIERSPAYANTID').getValue();
        if (lg_TIERS_PAYANT_ID === '') {
            Ext.MessageBox.alert('Message ', "Veuillez choisir un Grossiste");
            return;
        }
        var grid = Ext.getCmp('Grid_tpvente_ID');
        var dt_fin = '', dt_debut = '';
        if (Ext.getCmp('datefin').getSubmitValue() !== null && Ext.getCmp('datefin').getSubmitValue() !== "") {
            dt_fin = Ext.getCmp('datefin').getSubmitValue();
        }

        if (Ext.getCmp('datedebut').getSubmitValue() !== null && Ext.getCmp('datedebut').getSubmitValue() !== "") {
            dt_debut = Ext.getCmp('datedebut').getSubmitValue();
        }

        grid.getStore().load({
            params: {
                lgTP: lg_TIERS_PAYANT_ID,
                dt_end_vente: dt_fin,
                dt_start_vente: dt_debut,
                search_value: Ext.getCmp('TP_search').getValue()
            }
        });
    },

    onPdfClick: function () {

        var lgTP = Ext.getCmp('CBlgTIERSPAYANTID').getValue(),
                dt_fin = Ext.getCmp('datefin').getSubmitValue(), dt_debut = Ext.getCmp('datedebut').getSubmitValue()
                ;
        if (lgTP === null) {
            lgTP = '';
        }
        var linkUrl = "../webservices/configmanagement/tierspayant/ws_generate_pdf.jsp" + "?lgTP=" + lgTP + "&dt_start_vente=" + dt_debut + "&dt_end_vente=" + dt_fin+"&search_value="+Ext.getCmp('TP_search').getValue();
        window.open(linkUrl);



    }
});