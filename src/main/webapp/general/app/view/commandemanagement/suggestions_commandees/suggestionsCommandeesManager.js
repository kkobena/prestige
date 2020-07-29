var url_services_data_sugg_order_list = '../webservices/commandemanagement/suggestionOrder/ws_data.jsp';
var url_services_transaction_suggerercmde = '../webservices/sm_user/suggerercde/ws_transaction.jsp?mode=';
var Me;
var store_suggestion;

Ext.util.Format.decimalSeparator = ',';
Ext.util.Format.thousandSeparator = '.';
function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}

Ext.define('testextjs.view.commandemanagement.suggestions_commandees.suggestionsCommandeesManager', {
    extend: 'Ext.grid.Panel',
    xtype: 'suggestionscommandeesmanager',
    id: 'Suggestions_commandees_managerID',
    frame: true,
    collapsible: true,
    animCollapse: false,
    title: 'Liste Suggestion Commandees',
    iconCls: 'icon-grid',
    plain: true,
    maximizable: true,
    tools: [{type: "pin"}],
    closable: true,
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

        url_services_data_sugg_order_list = '../webservices/commandemanagement/suggestionOrder/ws_data.jsp?str_STATUT=';

        Me = this;

        var itemsPerPage = 20;
        store_suggestion = new Ext.data.Store({
            model: 'testextjs.model.SuggestionOrder',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_services_data_sugg_order_list+'enable',
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                },
                timeout: 240000
            }

        });




        Ext.apply(this, {
            width: 950,
            height: 580,
            store: store_suggestion,
            columns: [
                {
                    header: 'lg_SUGGESTION_ORDER_ID',
                    dataIndex: 'lg_SUGGESTION_ORDER_ID',
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
                    header: 'REF',
                    dataIndex: 'str_REF',
                    flex: 1
                },
                {
                    header: 'GROSSISTE',
                    dataIndex: 'lg_GROSSISTE_ID',
                    flex: 1
                },
                // int_NUMBER
                {
                    header: 'NOMBRE.LIGNE',
                    dataIndex: 'int_NOMBRE_ARTICLES',
                    flex: 1
                },
                {
                    header: 'QTE.ARTICLES',
                    dataIndex: 'int_NUMBER',
                    flex: 1
                },
                // str_STATUT
                {
                    header: 'STATUT',
                    dataIndex: 'str_STATUT',
                    flex: 1,
                    renderer: function (val) {
                        if (val === 'is_Process') {
                            val = 'INITIATION';
                        } else if (val === 'enable') {
                            val = 'COMMANDEE';
                        } else {
                            val = 'RECUE';
                        }
                        return val;
                    }
                },
                {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
                    menuDisabled: true,
                    items: [{
                            icon: 'resources/images/icons/fam/page_white_edit.png',
                            tooltip: 'Voir Details',
                            scope: this,
                            handler: this.onManageDetailsClick
                        }]
                }
            ],
            selModel: {
                selType: 'cellmodel'
            },
            tbar: [
                '-',
                {
                    xtype: 'textfield',
                    id: 'rechecher',
                    name: 'suggestion',
                    emptyText: 'Rech'
                },
                {
                    text: 'rechercher',
                    tooltip: 'rechercher',
                    scope: this,
                    handler: this.onRechClick
                }
            ],
            bbar: {
                xtype: 'pagingtoolbar',
                pageSize: 10,
                store: store_suggestion,
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
                url: url_services_data_sugg_order_list + 'update',
                params: {
                    lg_SUGGESTION_ORDER_ID: e.record.data.lg_SUGGESTION_ORDER_ID,
                    lg_GROSSISTE_ID: e.record.data.lg_GROSSISTE_ID,
                    int_NUMBER: e.record.data.int_NUMBER
                },
                success: function (response)
                {
                    console.log(response.responseText);
                    e.record.commit();
                    store_suggestion.reload();
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
        var xtype = "suggerercdemanager";
        var alias = 'widget.' + xtype;
        testextjs.app.getController('App').onLoadNewComponentWithDataSource(xtype, "Suggestion_de_reapprovisionnement", rec.get('lg_SUGGESTION_ORDER_ID'), rec.data);
        //alert("test"+rec.get('lg_SUGGESTION_ORDER_ID'));
    },
    onPrintClick: function () {

        window.print();
        body :{
            visibility:visible
        }
        print: {
            visibility:visible
        }
    },
    onRechClick: function () {
        var val = Ext.getCmp('rechecher');
        this.getStore().load({
            params: {
                search_value: val.value
            }
        }, url_services_data_sugg_order_list);
    }

});