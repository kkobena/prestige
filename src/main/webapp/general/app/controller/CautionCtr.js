/* global Ext */

Ext.define('testextjs.controller.CautionCtr', {
    extend: 'Ext.app.Controller',
    views: ['testextjs.view.caution.Caution', 'testextjs.view.caution.Add', 'testextjs.view.caution.Edit', 'testextjs.view.caution.Historiques', 'testextjs.view.caution.Achats'],
    refs: [{
            ref: 'cautiontierspayant',
            selector: 'cautiontierspayant'
        },

        {
            ref: 'cautiontierspayantGrid',
            selector: 'cautiontierspayant gridpanel'
        },
        {
            ref: 'pagingtoolbar',
            selector: 'cautiontierspayant gridpanel pagingtoolbar'
        },
        {ref: 'rechercherButton',
            selector: 'cautiontierspayant #rechercher'

        },
        {ref: 'tiersPayantId',
            selector: 'cautiontierspayant #tiersPayantId'

        },
        {ref: 'cautionAddForm',
            selector: 'cautionAddForm'

        },
        {ref: 'form',
            selector: 'cautionAddForm form'

        },
        {
            ref: 'editForm',
            selector: 'cautionEditForm form'

        },
        {ref: 'btnsave',
            selector: 'cautionAddForm #btnsave'

        },
        {ref: 'btnCancel',
            selector: 'cautionAddForm #btnCancel'

        },
        {ref: 'cautionEditForm',
            selector: 'cautionEditForm'

        }, {ref: 'btnEdit',
            selector: 'cautionEditForm #btnsave'

        },
        {ref: 'btnCancelEdit',
            selector: 'cautionEditForm #btnCancel'

        },
        {ref: 'tiersPayantIdAddForm',
            selector: 'cautionAddForm #tiersPayantId'

        },
        {ref: 'historiques',
            selector: 'cautionHistoriques'

        },
        {ref: 'cautionAchats',
            selector: 'cautionAchats'

        }
        ,
        {ref: 'historiquesDtEnd',
            selector: 'cautionHistoriques #dtEnd'

        }
        ,
        {ref: 'historiquesDtStart',
            selector: 'cautionHistoriques #dtStart'

        },
        {ref: 'cautionAchatsDtEnd',
            selector: 'cautionAchats #dtEnd'

        }
        ,
        {ref: 'cautionAchatsDtStart',
            selector: 'cautionAchats #dtStart'

        },
        {ref: 'cautionAchatsGrid',
            selector: 'cautionAchats fieldset gridpanel'

        }
        ,
        {ref: 'historiquesGrid',
            selector: 'cautionHistoriques fieldset gridpanel'

        },
        {ref: 'historiquesIdCaution',
            selector: 'cautionHistoriques #idCaution'

        }, {ref: 'cautionAchatsIdCaution',
            selector: 'cautionAchats #idCaution'

        }

    ],
    init: function (application) {
        this.control({
            'cautiontierspayant gridpanel pagingtoolbar': {
                beforechange: this.doBeforechange
            },
            'cautionAchats gridpanel pagingtoolbar': {
                beforechange: this.doBeforechangeCautionAchats
            },
            'cautionHistoriques gridpanel pagingtoolbar': {
                beforechange: this.doBeforechangeHistoriques
            },
            'cautiontierspayant #tiersPayantId': {
                select: this.doSearch
            },

            'cautiontierspayant gridpanel': {
                viewready: this.doInitStore
            },
            'cautiontierspayant #rechercher': {
                click: this.doSearch
            }
            ,
            "cautiontierspayant gridpanel actioncolumn": {
                remove: this.remove,
                editer: this.editer,
                fetchVentes: this.fetchVentes,
                fetchDepots: this.fetchDepots
            },

            'cautiontierspayant #addBtn': {
                click: this.add
            },
            'cautionAddForm #btnsave': {
                click: this.saveRecord
            },
            'cautionAddForm #btnCancel': {
                click: this.closeWindows
            },
            'cautionEditForm #btnsave': {
                click: this.edit
            },
            'cautionEditForm #btnCancel': {
                click: this.closeEditWindows
            },
            'cautionHistoriques #btnCancel': {
                click: this.closeHistoriques
            },
            'cautionAchats #btnCancel': {
                click: this.closeAchat
            }
            ,
            'cautionHistoriques #btnPrint': {
                click: this.onPrintHistoriques
            },
            'cautionAchats #btnPrint': {
                click: this.onPrintAchats
            }, 'cautionAchats #rechercher': {
                click: this.loadAchats
            }, 'cautionHistoriques #rechercher': {
                click: this.loadHistoriques
            }

        });
    },
    add: function () {
        const formwin = Ext.create('testextjs.view.caution.Add');
        formwin.show();
    },
    closeWindows: function () {
        const me = this;
        me.getCautionAddForm().destroy();
    },
    closeEditWindows: function () {
        const me = this;
        me.getCautionEditForm().destroy();
    },
    closeHistoriques: function () {
        const me = this;
        me.getHistoriques().destroy();
    },
    closeAchat: function () {
        const me = this;
        me.getCautionAchats().destroy();
    },
    onPrintHistoriques: function () {
        const me = this;

        let dtStart = me.getHistoriquesDtStart().getSubmitValue();
        if (dtStart === null || dtStart === undefined) {
            dtStart = '';
        }
        let dtEnd = me.getHistoriquesDtEnd().getSubmitValue();
        if (dtEnd === null || dtEnd === undefined) {
            dtEnd = '';
        }
        const linkUrl = '../cautionServlet?mode=historiques&idCaution=' + me.getHistoriquesIdCaution().getValue() + '&dtStart=' + dtStart + '&dtEnd=' + dtEnd;
        window.open(linkUrl);
    },
    onPrintAchats: function () {
        const me = this;
        let dtStart = me.getCautionAchatsDtStart().getSubmitValue();
        if (dtStart === null || dtStart === undefined) {
            dtStart = '';
        }
        let dtEnd = me.getCautionAchatsDtEnd().getSubmitValue();
        if (dtEnd === null || dtEnd === undefined) {
            dtEnd = '';
        }
        const linkUrl = '../cautionServlet?mode=achats&idCaution=' + me.getCautionAchatsIdCaution().getValue() + '&dtStart=' + dtStart + '&dtEnd=' + dtEnd;
        window.open(linkUrl);
    },
    lunchPrinter: function (mvtCaisseId) {

        Ext.Ajax.request({
            method: 'GET',
            url: '../api/v1/caisse/ticke-mvt-caisse?mvtCaisseId=' + mvtCaisseId,
            failure: function (response)
            {

                const object = Ext.JSON.decode(response.responseText, false);
                console.log("Bug " + object);
                console.log("Bug " + response.responseText);
                Ext.MessageBox.alert('Error Message', response.responseText);

            }
        });
    },
    edit: function () {
        const me = this;
        const   form = me.getEditForm();
        if (form.isValid()) {
            let datas = form.getValues();
            const progress = Ext.MessageBox.wait('Veuillez patienter . . .', 'En cours de traitement!');
            Ext.Ajax.request({
                method: 'PUT',
                headers: {'Content-Type': 'application/json'},
                url: '../api/v1/cautions',
                params: Ext.JSON.encode(datas),
                success: function (response, options) {
                    progress.hide();
                    const result = Ext.JSON.decode(response.responseText, true);
                    if (result.success) {
                        Ext.MessageBox.show({
                            title: 'Message d\'erreur',
                            width: 320,
                            msg: "Voulez-vous imprimer le ticket ?",
                            buttons: Ext.MessageBox.OKCANCEL,
                            icon: Ext.MessageBox.INFO,
                            fn: function (buttonId) {
                                if (buttonId === "ok") {

                                    me.lunchPrinter(result.mvtId);
                                }
                            }
                        });


                        me.closeEditWindows();
                        me.doSearch();
                    } else {
                        Ext.Msg.alert("Message", result.msg);
                    }




                },
                failure: function (response, options) {
                    progress.hide();
                    Ext.Msg.alert("Message", 'server-side failure with status code' + response.status);
                }
            });
        }

    },
    saveRecord: function () {
        const me = this;
        const   form = me.getForm();
        if (form.isValid()) {
            let datas = form.getValues();
            const progress = Ext.MessageBox.wait('Veuillez patienter . . .', 'En cours de traitement!');
            Ext.Ajax.request({
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                url: '../api/v1/cautions',
                params: Ext.JSON.encode(datas),
                success: function (response, options) {
                    progress.hide();
                    const result = Ext.JSON.decode(response.responseText, true);

                    if (result.success) {
                        Ext.MessageBox.show({
                            title: 'Message d\'erreur',
                            width: 320,
                            msg: "Voulez-vous imprimer le ticket ?",
                            buttons: Ext.MessageBox.OKCANCEL,
                            icon: Ext.MessageBox.INFO,
                            fn: function (buttonId) {
                                if (buttonId === "ok") {

                                    me.lunchPrinter(result.mvtId);
                                }
                            }
                        });
                    } else {
                        Ext.Msg.alert("Message", result.msg);
                    }



                    me.closeWindows();
                    me.doSearch();
                },
                failure: function (response, options) {
                    progress.hide();
                    Ext.Msg.alert("Message", 'server-side failure with status code' + response.status);
                }
            });
        }

    },
    fetchDepots: function (view, rowIndex, colIndex, item, e, record, row) {
        const me = this;
        const formwin = Ext.create('testextjs.view.caution.Historiques', {"caution": record.data});
        me.intloadHistoriques(formwin);



    },
    intloadHistoriques: function (formwin) {
        const me = this;
        me.getHistoriquesGrid().getStore().load({
            params: {
                "idCaution": me.getHistoriquesIdCaution().getValue(),
                "dtStart": me.getHistoriquesDtStart().getSubmitValue(),
                "dtEnd": me.getHistoriquesDtEnd().getSubmitValue()
            }, callback: function (records, operation, successful) {

                formwin.show();
            }

        });

    },
    loadHistoriques: function () {
        const me = this;
        me.getHistoriquesGrid().getStore().load({
            params: {
                "idCaution": me.getHistoriquesIdCaution().getValue(),
                "dtStart": me.getHistoriquesDtStart().getSubmitValue(),
                "dtEnd": me.getHistoriquesDtEnd().getSubmitValue()
            }

        });

    },
    initAchats: function (win) {
        const me = this;
        me.getCautionAchatsGrid().getStore().load({
            params: {
                "idCaution": me.getCautionAchatsIdCaution().getValue(),
                "dtStart": me.getCautionAchatsDtStart().getSubmitValue(),
                "dtEnd": me.getCautionAchatsDtEnd().getSubmitValue()
            }, callback: function (records, operation, successful) {

                win.show();
            }
        });

    },
    loadAchats: function () {
        const me = this;
        me.getCautionAchatsGrid().getStore().load({
            params: {
                "idCaution": me.getCautionAchatsIdCaution().getValue(),
                "dtStart": me.getCautionAchatsDtStart().getSubmitValue(),
                "dtEnd": me.getCautionAchatsDtEnd().getSubmitValue()
            }
        });

    },
    fetchVentes: function (view, rowIndex, colIndex, item, e, record, row) {
        const me = this;
        const formwin = Ext.create('testextjs.view.caution.Achats', {"caution": record.data});

        me.initAchats(formwin);

    },
    editer: function (view, rowIndex, colIndex, item, e, record, row) {
        const formwin = Ext.create('testextjs.view.caution.Edit', {"idCaution": record.data.id, "tiersPayantName": record.data.tiersPayantName});
        formwin.show();

    },

    remove: function (view, rowIndex, colIndex, item, e, record, row) {
        const me = this;
        const progress = Ext.MessageBox.wait('Veuillez patienter . . .', 'En cours de traitement!');
        Ext.Ajax.request({
            method: 'DELETE',
            headers: {'Content-Type': 'application/json'},
            url: '../api/v1/cautions/' + record.get('id'),
            success: function (response, options) {
                progress.hide();
                const result = Ext.JSON.decode(response.responseText, true);
                if (result.success) {
                    me.doSearch();
                } else {
                    Ext.Msg.alert("Message", result.msg);
                }

            },
            failure: function (response, options) {
                progress.hide();
                Ext.Msg.alert("Message", 'server-side failure with status code' + response.status);
            }
        });

    },

    doBeforechange: function (page, currentPage) {
        const me = this;
        const myProxy = me.getCautiontierspayantGrid().getStore().getProxy();
        myProxy.params = {
            tiersPayantId: null
        };
        const query = me.getTiersPayantId().getValue();
        myProxy.setExtraParam('tiersPayantId', query);

    },
    doInitStore: function () {
        const me = this;
        me.doSearch();
    },

    doBeforechangeHistoriques: function (page, currentPage) {
        const me = this;
        const myProxy = me.getHistoriquesGrid().getStore().getProxy();
        myProxy.params = {
            idCaution: null,
            dtStart: null,
            dtEnd: null
        };
      
        myProxy.setExtraParam('idCaution', me.getHistoriquesIdCaution().getValue());
        myProxy.setExtraParam('dtStart', me.getHistoriquesDtStart().getSubmitValue());
        myProxy.setExtraParam('dtEnd',  me.getHistoriquesDtEnd().getSubmitValue());

    },
    doBeforechangeCautionAchats: function (page, currentPage) {
        const me = this;
        const myProxy = me.getCautionAchatsGrid().getStore().getProxy();
        myProxy.params = {
            idCaution: null,
            dtStart: null,
            dtEnd: null
        };
          myProxy.setExtraParam('idCaution', me.getCautionAchatsIdCaution().getValue());
        myProxy.setExtraParam('dtStart', me.getCautionAchatsDtStart().getSubmitValue());
        myProxy.setExtraParam('dtEnd',  me.getCautionAchatsDtEnd().getSubmitValue());

    },
    doInitStoreHistoriques: function () {
        const me = this;
        me.loadHistoriques();
    },

    doSearch: function () {
        const me = this;
        const query = me.getTiersPayantId().getValue();
        me.getCautiontierspayantGrid().getStore().load({
            params: {
                tiersPayantId: query

            }
        });
    }

});