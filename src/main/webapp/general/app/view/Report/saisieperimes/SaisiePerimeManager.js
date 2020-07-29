/* global Ext */

var myAppController;
function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}
Ext.define('testextjs.view.Report.saisieperimes.SaisiePerimeManager', {
    extend: 'Ext.grid.Panel',
    xtype: 'saisieperime',
    id: 'saisieperimeID',
    requires: [
        'Ext.selection.CellModel',
        'Ext.grid.*',
        'Ext.window.Window',
        'Ext.data.*',
        'Ext.util.*',
        'Ext.form.*',
        'Ext.JSON.*',

        'testextjs.view.Report.saisieperimes.action.add',
        'testextjs.model.perimesModel',
        'Ext.ux.ProgressBarPager'

    ],
    title: 'Gestion des p&eacute;rim&eacute;s ',
    frame: true,
    initComponent: function () {

        Me = this;
        myAppController = Ext.create('testextjs.controller.App', {});
        var itemsPerPage = 20;
        var store = new Ext.data.Store({
            model: 'testextjs.model.perimesModel',
            pageSize: 25,
            autoLoad: true,
            proxy: {
                type: 'ajax',
                url: '../webservices/Report/saisieperimes/ws_data.jsp',
                reader: {
                    type: 'json',
                    root: 'data',
                    totalProperty: 'total'
                }
            }

        });






        Ext.apply(this, {
            width: "98%",
            height: 580,
            cls: 'custompanel',
            features: [
                {
                    ftype: 'summary'
                }],
            store: store,
            id: 'saisiGrid',
            columns: [{
                    header: 'ID',
                    dataIndex: 'lg_FAMILLE_ID',
                    hidden: true,

                }, {
                    header: 'CODE CIP',
                    dataIndex: 'CIP',
                    flex: 1, summaryType: "count",
                    
                    summaryRenderer: function (value) {

                        if (value > 0) {
                            return "<b><span style='color:blue;'>TOTAL: </span></b>";
                        } else {
                            return '';
                        }
                    }

                }, {
                    header: 'Article',
                    dataIndex: 'ARTICLE',
                    flex: 1.5

                }, {
                    header: 'Num.Lot',
                    dataIndex: 'LOT',
                    flex: 05
                }, {
                    header: 'Quantit&eacute;',
                    dataIndex: 'QTY',
                    renderer: amountformat,
                    align: 'right',
                    flex: 0.8,
                    summaryType: "sum",
                    summaryRenderer: function (value) {

                        if (value > 0) {
                            return "<b><span style='color:blue;'>" + amountformat(value) + " </span></b>";
                        } else {
                            return '';
                        }
                    }

                }, {
                    header: 'Prix.U',
                    dataIndex: 'PU',
                    renderer: amountformat,
                    align: 'right',
                    flex: 1

                }, {
                    header: 'Montant',
                    dataIndex: 'MONTANT',
                    renderer: amountformat,
                    align: 'right',
                    flex: 1.5,
                    summaryType: "sum",
                    summaryRenderer: function (value) {

                        if (value > 0) {
                            return "<b><span style='color:blue;'>" + amountformat(value) + " F</span></b>";
                        } else {
                            return '';
                        }
                    }

                }



                , {
                    header: 'Repartiteur',
                    dataIndex: 'GROSSISTE',
                    flex: 1

                }
                , {
                    header: 'Date Entr&eacute;e',
                    dataIndex: 'DATEENTREE',
                    flex: 1

                }
                , {
                    header: 'Date p&eacute;remption',
                    dataIndex: 'DATEPEREMPTION',
                    flex: 1

                }, {
                    header: 'Op&eacute;rateur',
                    dataIndex: 'OPERATEUR'

                }


            ],
            selModel: {
                selType: 'cellmodel'
            },
            tbar: [
                {
                    text: 'Ajouter des produits',
                    scope: this,
                    iconCls: 'addicon',
                    handler: this.onAddCreate
                }, '-', {
                    xtype: 'textfield',
                    id: 'rechecher',
                    flex: 1,
                    emptyText: 'Rech',
                    listeners: {
                        specialKey: function (field, e) {

                            if (e.getKey() === e.ENTER) {

                                var val = field.getValue();




                                Ext.getCmp('saisiGrid').getStore().load({
                                    params: {
                                        search_value: val,

                                        dt_end: Ext.getCmp('dt_end').getSubmitValue(),
                                        dt_start: Ext.getCmp('dt_start').getSubmitValue(),
//               
                                    }
                                });


                            }

                        }
                    }
                }, '-', {
                    xtype: 'datefield',
                    id: 'dt_start',
                    name: 'dt_start',
                    emptyText: 'Date debut',
//                   
                    flex: 1,
                    submitFormat: 'Y-m-d',
                    maxValue: new Date(),
                    format: 'd/m/Y',
                    listeners: {
                        'change': function (me) {

                            valdt_start = me.getSubmitValue();
                            Ext.getCmp('dt_end').setMinValue(me.getValue());
                        }
                    }
                }, {
                    xtype: 'tbseparator'
                }, {
                    xtype: 'datefield',
                    id: 'dt_end',
                    name: 'dt_end',
                    emptyText: 'Date fin',
                    maxValue: new Date(),
                    submitFormat: 'Y-m-d',
//                   
                    flex: 1,
                    format: 'd/m/Y',
                    listeners: {
                        'change': function (me) {
                            //alert(me.getSubmitValue());
                            valdt_end = me.getSubmitValue();
                            Ext.getCmp('dt_start').setMaxValue(me.getValue());
                        }
                    }
                }, {
                    xtype: 'tbseparator'
                }, {
                    text: 'rechercher',
                    tooltip: 'rechercher',
                    iconCls: 'searchicon',
                    flex: 0.7,
                    scope: this,
                    handler: this.onRechClick
                },
                {
                    xtype: 'tbseparator'
                },
                {
                    text: 'Imprimer',
                    tooltip: 'Imprimer',
                    iconCls: 'importicon',
                    flex: 0.7,
                    scope: this,
                    handler: this.onPrint
                }


            ],
            bbar: {
                xtype: 'pagingtoolbar',
                pageSize: 25,
                store: store,
                displayInfo: true,
                plugins: new Ext.ux.ProgressBarPager()
                ,
                listeners: {
                    beforechange: function (page, currentPage) {
                        var myProxy = this.store.getProxy();
                        myProxy.params = {
                            search_value: '',
                            dt_end: '',
                            dt_start: ''
                        };
                        var val = Ext.getCmp('rechecher').getValue();


                        var dt_start = Ext.getCmp('dt_start').getSubmitValue();
                        var dt_end = Ext.getCmp('dt_end').getSubmitValue();

                        myProxy.setExtraParam('search_value', val);
                        myProxy.setExtraParam('dt_start', dt_start);
                        myProxy.setExtraParam('dt_end', dt_end);

                    }

                }
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
            callback: this.onStoreLoad
        });
    },
    onStoreLoad: function () {
    },
    onAddCreate: function () {
        var xtype = "addPerimer";
        var alias = 'widget.' + xtype;
        //A DECOMMENTER EN CAS DE PROBLEME
        testextjs.app.getController('App').onLoadNewComponent(xtype, "Ajout de produits perimes", "0");


    },

    onRechClick: function () {
        var val = Ext.getCmp('rechecher').getValue();


        this.getStore().load({
            params: {
                search_value: val,

                dt_end: Ext.getCmp('dt_end').getSubmitValue(),
                dt_start: Ext.getCmp('dt_start').getSubmitValue(),
//               
            }
        }, url_services_data_facturation);
    },

    onPrint: function () {

        var
                dt_end = Ext.getCmp('dt_end').getSubmitValue(), dt_start = Ext.getCmp('dt_start').getSubmitValue()
                ;

        var search_value = Ext.getCmp('rechecher').getValue();

        var linkUrl = "../webservices/Report/saisieperimes/ws_perimer_pdf.jsp" + "?dt_start=" + dt_start + "&dt_end=" + dt_end + "&search_value=" + search_value;
        window.open(linkUrl);



    }

});