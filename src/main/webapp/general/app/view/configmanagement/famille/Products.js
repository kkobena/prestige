/* global Ext */


var Me_Workflow;


Ext.util.Format.decimalSeparator = ',';
Ext.util.Format.thousandSeparator = '.';
function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}


Ext.define('testextjs.view.configmanagement.famille.Products', {
    extend: 'Ext.grid.Panel',
    xtype: 'produitsxx',
  
    requires: [
     'Ext.selection.CellModel',
        'Ext.grid.*',
        'Ext.window.Window',
        'Ext.data.*',
        'Ext.util.*',
        'Ext.form.*',
        'Ext.JSON.*',
        'testextjs.model.Famille'
    ],
    title: 'Gestion des Articles',
    plain: true,
    frame: true,
    initComponent: function () {
        Me_Workflow = this;
        var itemsPerPage = 20;
        var store = new Ext.data.Store({
            model: 'testextjs.model.Famille',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: '../webservices/sm_user/famille/ws_data.jsp',
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
            
            store: store,
            id: 'produitsxxID',
            columns: [
                {
                    header: 'lg_FAMILLE_ID',
                    dataIndex: 'lg_FAMILLE_ID',
                    hidden: true,
                    flex: 1
                },
                {

                    header: 'Etat.cmde',
                    dataIndex: 'STATUS',
                    renderer: function (v, m, r) {
                        var STATUS = r.data.STATUS;
                        switch (STATUS) {
                            case 1:
                                m.style = 'background-color:#73C774;';
                                break;
                            case 2:
                                m.style = 'background-color:#5fa2dd;';
                                break;
                            case 3:
                                m.style = 'background-color:#f98012;';
                                break;
                            case 4:
                                m.style = 'background-color:#a62a3e;';
                                break;
                            default:
                                m.style = 'background-color:#d4d4d4;';
                                break;
                        }


                        return v;
                    },
                    width: 35
                },

                {
                    header: 'CIP',
                    dataIndex: 'int_CIP',
                    flex: 0.7

                },
                {
                    header: 'Designation',
                    dataIndex: 'str_DESCRIPTION',
                    flex: 2.5

                },
                {
                    header: 'Prix Vente',
                    dataIndex: 'int_PRICE',
                    renderer: amountformat,
                    align: 'right',
                    flex: 0.7

                },
                {
                    header: 'Prix Achat F',
                    dataIndex: 'int_PAF',
                    renderer: amountformat,
                    align: 'right',
                    flex: 0.7

                },
                {
                    header: 'Stock',
                    dataIndex: 'int_NUMBER_AVAILABLE',
                    align: 'center',
                    flex: 0.7

                },
                {
                    xtype: 'checkcolumn',
                    header: ' ',
                    dataIndex: 'bool_ACCOUNT',
                    width: 30,
                   sortable: false,
                    menuDisabled: true,
                   
                    listeners: {checkchange: function (scr, rowIndex, checked, eOpts) {
                           
                            var rec = Ext.getCmp('produitsxxID').getStore().getAt(rowIndex);

                            Ext.Ajax.request({
                                url: '../webservices/sm_user/famille/ws_update.jsp',
                                params: {
                                    lg_FAMILLE_ID: rec.get("lg_FAMILLE_ID"),
                                    bool_ACCOUNT: checked
                                },
                                success: function (response)
                                {



                                },
                                failure: function (response)
                                {

                                }
                            });
                        }}


                }



            ],
            selModel: {
                selType: 'cellmodel'
            },
            tbar: [
               
                {
                    xtype: 'textfield',
                    id: 'rechecherProductxx',
                    emptyText: 'Recherche',
                    width:350,
                    listeners: {
                        'render': function (cmp) {
                            cmp.getEl().on('keypress', function (e) {
                                if (e.getKey() === e.ENTER) {
                                    Me_Workflow.onRechClick();

                                }
                            });
                        }
                    }
                }, '-', {
                    text: 'rechercher',
                    tooltip: 'rechercher',
                    scope: this,
                    iconCls: 'searchicon',
                    handler: this.onRechClick
                }
            ],
            bbar: {
                xtype: 'pagingtoolbar',
                pageSize: itemsPerPage,
                store: store,
                displayInfo: true,
               
                listeners: {
                    beforechange: function (page, currentPage) {
                        var myProxy = this.store.getProxy();
                        myProxy.params = {
                            search_value: ''
                           
                        };

                         var search_value = Ext.getCmp('rechecherProductxx').getValue();

                        myProxy.setExtraParam('search_value', search_value);
                    }

                }
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
     onRechClick: function () {
        var val = Ext.getCmp('rechecherProductxx');

        Ext.getCmp('produitsxxID').getStore().load({
            params: {
                search_value: val.getValue()
            }
        });
    }
});