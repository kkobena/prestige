var url_services_data_mouvement_ajustement = '../webservices/stockmanagement/suivistockvente/ws_data_mouvement_ajustement.jsp';

var Oview;
var Omode;
var Me;
var ref;
var valdatedebutDetail;
var valdatefinDetail;
var OgridpanelID;
var other;


Ext.util.Format.thousandSeparator = '.';
function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}

Ext.define('testextjs.view.stockmanagement.suivistockvente.action.detailAjustement', {
    extend: 'Ext.window.Window',
    xtype: 'ajustementmouvement',
    id: 'ajustementmouvementID',
    requires: [
        'Ext.form.*',
        'Ext.window.Window',
        'testextjs.store.Statut',
        'Ext.selection.CellModel',
        'Ext.grid.*'
    ],
    config: {
        odatasource: '',
        parentview: '',
        mode: '',
        titre: '',
        other: '',
        datedebut: '',
        datedin: ''
    },
//    frame: true,
    collapsible: true,
    closable: true,
    initComponent: function() {
        var texte = "";
        Oview = this.getParentview();
        Omode = this.getMode();
        Me = this;

        var itemsPerPage = 20;

        ref = this.getOdatasource();
        valdatedebutDetail = this.getDatedebut();
        valdatefinDetail = this.getDatedin();
        other = this.getOther();
        var url_data = "";




        var store = new Ext.data.Store({
            model: 'testextjs.model.FamilleStock',
            pageSize: itemsPerPage,
            autoLoad: true,
            proxy: {
                type: 'ajax',
//                url: url_services_data_mouvement_ajustement + "?lg_FAMILLE_ID=" + ref + "&datedebut="+valdatedebutDetail+"&datefin="+valdatefinDetail,
                url: url_services_data_mouvement_ajustement,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                },
                timeout: 180000
            }

        });

        if (other == "int_NUMBER_AJUSTEMENT_OUT") {
            texte = "Information sur l'ajustement: Ajustement valeur negative";
            url_data = url_services_data_mouvement_ajustement + "?lg_FAMILLE_ID=" + ref + "&datedebut=" + valdatedebutDetail + "&datefin=" + valdatefinDetail + "&str_TYPE_ACTION=REMOVE"
        } else if (other == "int_NUMBER_AJUSTEMENT_IN") {
            texte = "Information sur l'ajustement: Ajustement valeur positive";
            url_data = url_services_data_mouvement_ajustement + "?lg_FAMILLE_ID=" + ref + "&datedebut=" + valdatedebutDetail + "&datefin=" + valdatefinDetail +  "&str_TYPE_ACTION=ADD"
        } 
        var form = new Ext.form.Panel({
            bodyPadding: 15,
            fieldDefaults: {
                labelAlign: 'right',
                labelWidth: 150,
                layout: {
                    type: 'vbox',
                    align: 'stretch',
                    padding: 10
                },
                defaults: {
                    flex: 1
                },
                msgTarget: 'side'
            },
            items: [{
                    xtype: 'fieldset',
                    title: texte,
                    collapsible: true,
//                    id: 'other',
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
//                            plugins: [this.cellEditing],
                            store: store,
                            height: 350,
                            columns: [{
                                    header: 'Date',
                                    dataIndex: 'dt_DAY',
                                    flex: 1
                                }, {
                                    header: 'Heure',
                                    dataIndex: 'dt_UPDATED',
                                    flex: 1
                                }, {
                                    header: 'Quantite',
                                    dataIndex: 'int_TAUX_MARQUE',
                                    flex: 1
                                },{
                                    header: 'Operateur',
                                    dataIndex: 'lg_USER_ID',
                                    flex: 1
                                },
                            ],
                            bbar: {
                                xtype: 'pagingtoolbar',
                                pageSize: 10,
                                store: store,
                                displayInfo: true
//                                plugins: new Ext.ux.ProgressBarPager()
                            },
                            listeners: {
                                scope: this
//                                selectionchange: this.onSelectionChange
                            }
                        }]
                }
            ]


        });

//        alert('url_data:'+url_data);

        OgridpanelID = Ext.getCmp('gridpanelID');
        OgridpanelID.getStore().getProxy().url = url_data;
        OgridpanelID.getStore().reload();


        var win = new Ext.window.Window({
            autoShow: true,
            title: this.getTitre(),
            width: 900,
            height: 450,
            minWidth: 300,
            minHeight: 200,
            layout: 'fit',
            plain: true,
            items: form
        });

    },
    onSelectionChange: function(model, records) {
        var rec = records[0];
        if (rec) {
            this.getForm().loadRecord(rec);
        }
    }

});