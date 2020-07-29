/* global Ext */

Ext.define('testextjs.controller.GestionCaisseCtr', {
    extend: 'Ext.app.Controller',
    views: ['testextjs.view.caisseManager.GestionCaisse'],
    refs: [{
            ref: 'gestcaissemanager',
            selector: 'gestcaissemanager'
        },
        {
            ref: 'imprimerBtn',
            selector: 'gestcaissemanager #imprimer'
        },
        {
            ref: 'totalAmount',
            selector: 'gestcaissemanager #totalAmount'
        },

        {
            ref: 'listeCaisseGrid',
            selector: 'gestcaissemanager gridpanel'
        },
        {
            ref: 'pagingtoolbar',
            selector: 'gestcaissemanager listeCaisseGrid pagingtoolbar'
        }

        , {
            ref: 'dtStart',
            selector: 'gestcaissemanager #dtStart'
        }, {
            ref: 'dtEnd',
            selector: 'gestcaissemanager  #dtEnd'
        },

        {ref: 'userComboField',
            selector: 'gestcaissemanager #user'
        },
        {ref: 'rechercherButton',
            selector: 'gestcaissemanager #rechercher'

        }




    ],
    init: function (application) {
        this.control({
            'gestcaissemanager gridpanel pagingtoolbar': {
                beforechange: this.doBeforechange
            },
            'gestcaissemanager #rechercher': {
                click: this.doSearch
            },
            'gestcaissemanager #imprimer': {
                click: this.onPdfClick
            },

            'gestcaissemanager #reglement': {
                select: this.doSearch
            },
            'gestcaissemanager #user': {
                select: this.doSearch
            },
            'gestcaissemanager gridpanel': {
                viewready: this.doInitStore
            },

            "gestcaissemanager gridpanel actioncolumn": {
                toprint: this.toprint,
                valider: this.valider,
                annuler:this.annuler

            },
            'gestcaissemanager #dtStart': {
                afterrender: this.setdate
            }

        });
    },
    toprint: function (view, rowIndex, colIndex, item, e, record, row) {
        var me = this;
        me.onPrintBilletage(record.get('ldCAISSEID'));
    },
    annuler: function (view, rowIndex, colIndex, item, e, record, row) {
        var me = this;
        me.onRemoveClick(record);
    },
    valider: function (view, rowIndex, colIndex, item, e, record, row) {
        var me = this;
        me.onValidateClotureClick(record);
    },
    setdate: function (cmp) {
        var CurrentDate = new Date();
        CurrentDate.setMonth(CurrentDate.getMonth() - 1);
        cmp.setValue(CurrentDate);

    },
    handleActionColumn: function (view, rowIndex, colIndex, item, e, r, row) {
        var me = this;
        var store = me.getListeCaisseGrid().getStore(),
                rec = store.getAt(colIndex);

        if (parseInt(item) === 12) {
            me.onRemoveClick(rec);
        } else if (parseInt(item) === 11) {
            me.onPrintBilletage(rec.get('ldCAISSEID'));
        } else if (parseInt(item) === 10) {
            me.onValidateClotureClick(rec);
        }

    },
    onValidateClotureClick: function (rec) {
        var me = this;
        Ext.MessageBox.confirm('Message',
                'Confirmer la validation de la cloture de la caisse de  ' + rec.get('userFullName'),
                function (btn) {
                    if (btn == 'yes') {
                        Ext.Ajax.request({
                            method: 'PUT',
                            url: '../api/v1/caisse/validatecloture/' + rec.get('ldCAISSEID'),
                            success: function (response)
                            {
                                var object = Ext.JSON.decode(response.responseText, false);
                                if (!object.success) {
                                    Ext.MessageBox.alert('Error Message', object.msg);
                                    return;
                                }
                                me.doSearch();
                            },
                            failure: function (response)
                            {

                            }
                        });
                        return;
                    }
                });


    },
    onPrintBilletage: function (id) {
        var linkUrl = '../webservices/sm_user/gestcaisse/ws_generate_pdf.jsp?lg_CAISSE_ID=' + id;
        window.open(linkUrl);

    },
    onRemoveClick: function (rec) {
        var me = this;
        Ext.MessageBox.confirm('Message',
                'Voulez-Vous annuler la cloture de la caisse de ' + rec.get('userFullName'),
                function (btn) {
                    if (btn === 'yes') {

                        Ext.Ajax.request({
                            timeout: 240000,
                            method: 'PUT',
                            url: '../api/v1/caisse/rollbackclose/' + rec.get('ldCAISSEID'),
                            success: function (response)
                            {
                                var object = Ext.JSON.decode(response.responseText, false);
                                if (!success) {
                                    Ext.MessageBox.alert('Error Message', object.msg);
                                    return;
                                }

                                me.doSearch();
                            },
                            failure: function (response)
                            {
                                var object = Ext.JSON.decode(response.responseText, false);
                                Ext.MessageBox.alert('Error Message', response.responseText);

                            }
                        });
                        return;
                    }
                });

    },
    onPdfClick: function () {
        var me = this;
        var userId = '';
        var user = me.getUserComboField().getValue();
        if (user != null && user != '') {
            userId = user;

        }
        var dtStart = me.getDtStart().getSubmitValue();
        var dtEnd = me.getDtEnd().getSubmitValue();

        var linkUrl = '../BalancePdfServlet?mode=GESTION_CAISSE&dtStart=' + dtStart + '&dtEnd=' + dtEnd + "&userId=" + userId;
        window.open(linkUrl);
    },

    doBeforechange: function (page, currentPage) {
        var me = this;
        var myProxy = me.getListeCaisseGrid().getStore().getProxy();
        myProxy.params = {
            userId: null,
            dtStart: null,
            dtEnd: null

        };
        myProxy.setExtraParam('userId', me.getUserComboField().getValue());
        myProxy.setExtraParam('dtStart', me.getDtStart().getSubmitValue());
        myProxy.setExtraParam('dtEnd', me.getDtEnd().getSubmitValue());

    },
    doMetachange: function (store, meta) {
        var me = this;
        me.getTotalAmount().setValue(meta.summary);

    },
    doInitStore: function () {
        var me = this;
        me.getListeCaisseGrid().getStore().addListener('metachange', this.doMetachange, this);
        me.doSearch();

    },

    doSearch: function () {
        var me = this;
        me.getListeCaisseGrid().getStore().load({
            params: {
                userId: me.getUserComboField().getValue(),
                dtStart: me.getDtStart().getSubmitValue(),
                dtEnd: me.getDtEnd().getSubmitValue()

            }
        });
    }
});