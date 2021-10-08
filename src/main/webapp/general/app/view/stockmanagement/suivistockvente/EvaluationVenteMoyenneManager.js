var url_services_data_evaluationventemoyenne = '../webservices/stockmanagement/suivistockvente/ws_data_evaluation_ventemoyenne.jsp';
var Me;

Ext.util.Format.decimalSeparator = ',';
Ext.util.Format.thousandSeparator = '.';
function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}

Ext.define('testextjs.view.stockmanagement.suivistockvente.EvaluationVenteMoyenneManager', {
    extend: 'Ext.grid.Panel',
    xtype: 'evaluationventemoyenne',
    id: 'evaluationventemoyenneID',
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
    title: 'Evaluation des ventes',
    plain: true,
    maximizable: true,
    //    tools: [{type: "pin"}],
    closable: false,
    frame: true,
    viewConfig: {
        listeners: {
            cellclick: function(view, cell, cellIndex, record, row, rowIndex, e) { //gere le click sur la cellule d'une grid donné

                var clickedDataIndex = view.panel.headerCt.getHeaderAtIndex(cellIndex).dataIndex; //recupere l'index de la colonne sur lequel l'on a cliqué
                var clickedColumnName = view.panel.headerCt.getHeaderAtIndex(cellIndex).text; //recupere le nom de la colonne sur lequel l'on a cliqué 
                var clickedCellValue = record.get(clickedDataIndex); //recupere la valeur de la colonne sur lequel l'on a cliqué

                //                alert('clickedCellValue '+clickedCellValue);
            }
        }
    },
    initComponent: function() {
//set months of year
        

        var AppController = testextjs.app.getController('App');

        var itemsPerPage = 20;
        Me = this;
        var store = new Ext.data.Store({
            model: 'testextjs.model.FamilleStock',
            pageSize: itemsPerPage,
            autoLoad: true,
            proxy: {
                type: 'ajax',
                url: url_services_data_evaluationventemoyenne,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                },
                timeout: 240000
            }

        });


        this.cellEditing = new Ext.grid.plugin.CellEditing({
            clicksToEdit: 1
        });


        Ext.apply(this, {
            width: '98%',
            height: 580,
            plugins: [this.cellEditing],
            store: store,
            id: 'GridSuiviStockVenteID',
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
                    flex: 0.5
                },
                {
                    header: 'Article',
                    dataIndex: 'str_NAME',
                    flex: 1.5
                },
                {
                    header: 'Moyenne',
                    dataIndex: 'int_NUMBER',
                    renderer: amountformat,
                    flex: 0.5
                }, {
                    header: 'Qte Total Vendue',
                    align: 'center',
                    renderer: amountformat,
                    dataIndex: 'int_STOCK_REAPROVISONEMENT',
                    flex: 0.7
                }, {
                    header: 'Co&ucirc;t total',
                    dataIndex: 'int_NUMBER_SORTIE',
                    align: 'right',
                    flex: 0.7
                }, {
                    header: 'Periode',
                    dataIndex: 'dt_UPDATED',
                    hidden: true,
                    flex: 1.3
                }, {
                    header: AppController.getMonthToDisplay(0, currentMonth),
                    dataIndex: 'int_VALUE0',
                    align: 'center',
                    flex: 0.7
                }, {
                    header: AppController.getMonthToDisplay(1, currentMonth),
                    dataIndex: 'int_VALUE1',
                    align: 'center',
                    flex: 0.7
                }, {
                    header: AppController.getMonthToDisplay(2, currentMonth),
                    dataIndex: 'int_VALUE2',
                    align: 'center',
                    flex: 0.7
                }, {
                    header: AppController.getMonthToDisplay(3, currentMonth),
                    dataIndex: 'int_VALUE3',
                    align: 'center',
                    flex: 0.7
                }],
            selModel: {
                selType: 'cellmodel'
            },
            tbar: [
                {
                    xtype: 'datefield',
                    id: 'datedebut',
                    fieldLabel: 'Du',
                    name: 'datedebut',
                     hidden:true,
                    emptyText: 'Date debut',
                    submitFormat: 'Y-m-d',
                    maxValue: new Date(), //                    flex: 0.7,
                    format: 'd/m/Y',
                    listeners: {
                        'change': function(me) {
                            Ext.getCmp('datefin').setMinValue(me.getValue());
                        }
                    }
                },
                {
                    xtype: 'datefield',
                    id: 'datefin',
                    fieldLabel: 'Au',
                    name: 'datefin',
                    hidden:true,
                    emptyText: 'Date fin',
                    maxValue: new Date(),
                    //                    flex: 0.7,
                    submitFormat: 'Y-m-d',
                    format: 'd/m/Y',
                    listeners: {
                        'change': function(me) {
                            Ext.getCmp('datedebut').setMaxValue(me.getValue());
                        }
                    }
                }, '-', {
                    xtype: 'textfield',
                    id: 'rechecher',
                       width: 350,
                    name: 'facture',
                    emptyText: 'Rech',
                    listeners: {
                        'render': function(cmp) {
                            cmp.getEl().on('keypress', function(e) {
                                if (e.getKey() === e.ENTER) {
                                    Me.onRechClick();

                                }
                            });
                        }
                    }
                }, {
                    text: 'rechercher',
                    iconCls: 'searchicon',
                    tooltip: 'rechercher', scope: this,
                    handler: this.onRechClick
                }],
            bbar: {
                xtype: 'pagingtoolbar',
                store: store, // same store GridPanel is using
                dock: 'bottom',
                pageSize: itemsPerPage,
                displayInfo: true, // same store GridPanel is using
                listeners: {
                    beforechange: function(page, currentPage) {
                        var myProxy = this.store.getProxy();
                        myProxy.params = {
                            datedebut: '',
                            datefin: '',
                            search_value: ''
                        };
                        myProxy.setExtraParam('search_value', Ext.getCmp('rechecher').getValue());
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
    loadStore: function() {
        this.getStore().load({
            callback: this.onStoreLoad
        });
    },
    onStoreLoad: function() {
    },
    onRechClick: function() {
        var val = Ext.getCmp('rechecher');
        this.getStore().load({
            params: {
                search_value: val.getValue(),
                datedebut: '',
                datefin: ''
            }
        }, url_services_data_evaluationventemoyenne);
    }

});