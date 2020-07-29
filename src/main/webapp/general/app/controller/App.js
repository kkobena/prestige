/* global Ext, boxWaitingProcess, lg_USER_ID */

var Str_customer_name = "";
var Me_Workflow;

//set months of year
var month = new Array(), today = new Date(), currentMonth = today.getMonth();
month[0] = "Janvier";
month[1] = "F&eacute;vrier";
month[2] = "Mars";
month[3] = "Avril";
month[4] = "Mai";
month[5] = "Juin";
month[6] = "Juillet";
month[7] = "Ao&ucirc;t";
month[8] = "Septembre";
month[9] = "Octobre";
month[10] = "Novembre";
month[11] = "Decembre";
//end set months of year

Ext.define('testextjs.controller.App', {
    extend: 'Ext.app.Controller',
    requires: [
        'testextjs.view.*',
        'testextjs.model.Utilisateur',
        'testextjs.view.sm_user.notification.NotificationManager',
        'Ext.window.Window'
    ],
    stores: [
        //'Companies',
        //'Restaurants',
        'Files',
//        'States',
//        'BigData'
    ],
    refs: [
        {
            ref: 'doventeManager',
            selector: 'doventeManager'
        },
        {
            ref: 'viewport',
            selector: 'viewport'
        },
        {
            ref: 'navigation',
            selector: 'navigation'
        },
        {
            ref: 'contentPanel',
            selector: 'contentPanel'
        },
        {
            ref: 'descriptionPanel',
            selector: 'descriptionPanel'
        },
        {
            ref: 'codePreview',
            selector: 'codePreview'
        }
    ],
    exampleRe: /^\s*\/\/\s*(\<\/?example>)\s*$/,
    init: function () {

        // testextjs.app.getController('App').onCheckNotification();

        //contregisterordermanager
        Me_Workflow = this;



        this.control({
            'navigation': {
//                selectionchange: 'onNavSelectionChange'
            },
            'viewport': {
                afterlayout: 'afterViewportLayout'
            },
            'codePreview tool[type=maximize]': {
                click: 'onMaximizeClick'
            },
            'contentPanel': {
                resize: 'centerContent'
            },
            'tool[regionTool]': {
                click: 'onSetRegion'
            },
            afterrender: function () {
                setTimeout(function () {

                    testextjs.app.getController('App').onLoadNewComponent("aboutmanager", "A Propos", "");

                }
                , 0);
            }

        });

    },
    onSetRegion: function (tool) {
        var panel = tool.toolOwner;

        var regionMenu = panel.regionMenu || (panel.regionMenu =
                Ext.widget({
                    xtype: 'menu',
                    items: [{
                            text: 'North',
                            checked: panel.region === 'north',
                            group: 'mainregion',
                            handler: function () {
                                panel.setBorderRegion('north');
                            }
                        }, {
                            text: 'South',
                            checked: panel.region === 'south',
                            group: 'mainregion',
                            handler: function () {
                                panel.setBorderRegion('south');
                            }
                        }, {
                            text: 'East',
                            checked: panel.region === 'east',
                            group: 'mainregion',
                            handler: function () {
                                panel.setBorderRegion('east');
                            }
                        }, {
                            text: 'West',
                            checked: panel.region === 'west',
                            group: 'mainregion',
                            handler: function () {
                                panel.setBorderRegion('west');
                            }
                        }]
                }));

        regionMenu.showBy(tool.el);
    },
    afterViewportLayout: function () {
        if (!this.navigationSelected) {
            var id = location.hash.substring(1),
                    navigation = this.getNavigation(),
                    store = navigation.getStore(),
                    node;

            node = id ? store.getNodeById(id) : store.getRootNode().firstChild.firstChild;
            navigation.getSelectionModel().select(node);
            navigation.getView().focusNode(node);
            this.navigationSelected = true;
        }
    },
    gettuserName: function () {

        Ext.getCmp('commonsettingapp').setText("Str_customer_name");
        alert(Ext.getCmp('commonsettingapp'));
        return Str_customer_name;
    }
    ,
    inituserName: function () {
        var OTUser;
        var url_services_data_myaccount = '../webservices/sm_user/myaccount/ws_data.jsp';
        var store_myaccount = new Ext.data.Store({
            model: 'testextjs.model.Utilisateur',
            proxy: {
                type: 'ajax',
                url: url_services_data_myaccount
            }
        });
        store_myaccount.load({
            callback: function () {
                OTUser = store_myaccount.getAt(0);
                lg_USER_ID.setValue(OTUser.get('lg_USER_ID'));
                xtypeload = OTUser.get('xtypeload');
//                alert(OTUser.get('xtypeload'));
                Ext.getCmp('commonsettingapp').setText(OTUser.get('str_FIRST_NAME') + " " + OTUser.get('str_LAST_NAME'));
            }
        });




    },
    onCheckNotification: function () {
        // Start a simple clock task that updates a div once per second
        var task = {
            run: function () {


                var url_services_data_notification = '../webservices/sm_user/notification/ws_data.jsp';
                var itemsPerPage = 20;
                var storeNotification = new Ext.data.Store({
                    model: 'testextjs.model.Notification',
                    proxy: {
                        pageSize: itemsPerPage,
                        autoLoad: true,
                        type: 'ajax',
                        url: url_services_data_notification,
                        reader: {
                            type: 'json',
                            root: 'results',
                            totalProperty: 'total'
                        },
                        timeout: 240000
                    }
                });


                storeNotification.load({
                    callback: function () {
                        //alert(storeNotification.getCount());
                        if (storeNotification.getCount() === 0) {
                            return;
                        }
                        ONotification = storeNotification.getAt(0);
                        //alert(ONotification.get('str_CONTENT'));


                        var str_title = ONotification.get('str_TYPE');
                        var str_detailMessage = ONotification.get('str_CONTENT');

                        Ext.create('widget.uxNotification', {
                            corner: 'br',
                            manager: 'fullscreen',
                            cls: 'ux-notification-light',
                            iconCls: 'icon-info',
                            closable: true,
                            title: str_title,
                            html: str_detailMessage,
                            //     slideInDelay: 800,
                            slideDownDelay: 1000,
                            autoDestroyDelay: 5000,
                            slideInAnimation: 'bounceOut',
                            slideDownAnimation: 'easeIn'
                        }).show();


                    }
                });




                //alert("Fin");
                //  this.onRunNutification('Description','This notification is to inform you that something different has changed');
                // alert(Ext.Date.format(new Date(), 'g:i:s A' ));
                // Ext.fly('clock').update(Ext.Date.format(new Date(), 'g:i:s A'));
            },
            interval: 5000 //1 second
        };

        var runner = new Ext.util.TaskRunner();
        runner.start(task);


    },
    onRunNutification: function (str_title, str_detailMessage) {
        alert("Debut");
        Ext.create('widget.uxNotification', {
            corner: 'br',
            manager: 'fullscreen',
            cls: 'ux-notification-light',
            iconCls: 'icon-info',
            closable: true,
            title: str_title,
            html: str_detailMessage,
            //     slideInDelay: 800,
            slideDownDelay: 500,
            autoDestroyDelay: 5000,
            slideInAnimation: 'bounceOut',
            slideDownAnimation: 'easeIn'
        }).show();



    },
    onLoadNewComponentInit: function (ComponentXtype, ComponentLabel, name_ressource) {
        // alert(ComponentXtype);

        var text = ComponentLabel,
                xtype = ComponentXtype,
                alias = 'widget.' + xtype,
                contentPanel = this.getContentPanel(),
                themeName = Ext.themeName,
                cmp;



        contentPanel.removeAll(true);

        var className = Ext.ClassManager.getNameByAlias(alias);

        var ViewClass = Ext.ClassManager.get(className);

        // alert("139");
        var clsProto = ViewClass.prototype;
        // alert("141");
        if (clsProto.themes) {
            clsProto.themeInfo = clsProto.themes[themeName];
            if (themeName === 'gray' || themeName === 'access') {
                clsProto.themeInfo = Ext.applyIf(clsProto.themeInfo || {}, clsProto.themes.classic);
            }
        }

        cmp = new ViewClass(
                {
                    nameintern: name_ressource,
                    titre: text
                });
        contentPanel.add(cmp);
        if (cmp.floating) {
            cmp.show();
        } else {
            this.centerContent();
        }

        contentPanel.setTitle(text);

        // alert("Ok dd");

        document.title = document.title.split(' - ')[0] + ' - ' + text;
        location.hash = xtype;

        this.updateDescription(clsProto);

        if (clsProto.exampleCode) {
            this.updateCodePreview(clsProto.exampleCode);
        } else {
            this.updateCodePreviewAsync(clsProto, xtype);
        }

        //  alert("Fin");

    },
    onLoadNewComponent: function (ComponentXtype, ComponentLabel, name_ressource) {
        // alert(ComponentXtype);

        var text = ComponentLabel,
                xtype = ComponentXtype,
                alias = 'widget.' + xtype,
                contentPanel = this.getContentPanel(),
                themeName = Ext.themeName,
                cmp;



        contentPanel.removeAll(true);

        var className = Ext.ClassManager.getNameByAlias(alias);

        var ViewClass = Ext.ClassManager.get(className);

        // alert("139");
        var clsProto = ViewClass.prototype;
        // alert("141");
        if (clsProto.themes) {
            clsProto.themeInfo = clsProto.themes[themeName];
            if (themeName === 'gray' || themeName === 'access') {
                clsProto.themeInfo = Ext.applyIf(clsProto.themeInfo || {}, clsProto.themes.classic);
            }
        }

        cmp = new ViewClass(
                {
                    nameintern: name_ressource,
                    titre: text
                });
        contentPanel.add(cmp);
        if (cmp.floating) {
            cmp.show();
        } else {
            this.centerContent();
        }

        contentPanel.setTitle(text);

        // alert("Ok dd");

        document.title = document.title.split(' - ')[0] + ' - ' + text;
        location.hash = xtype;

        this.updateDescription(clsProto);

        if (clsProto.exampleCode) {
            this.updateCodePreview(clsProto.exampleCode);
        } else {
            this.updateCodePreviewAsync(clsProto, xtype);
        }

        //  alert("Fin");

    },

    onLoadNewComponentWithDataSource: function (ComponentXtype, ComponentLabel, name_ressource, ODatatasource) {
        // alert(ComponentXtype);

        var text = ComponentLabel,
                xtype = ComponentXtype,
                alias = 'widget.' + xtype,
                contentPanel = this.getContentPanel(),
                themeName = Ext.themeName,
                cmp;



        contentPanel.removeAll(true);

        var className = Ext.ClassManager.getNameByAlias(alias);

        var ViewClass = Ext.ClassManager.get(className);

        // alert("139");
        var clsProto = ViewClass.prototype;
        // alert("141");
        if (clsProto.themes) {
            clsProto.themeInfo = clsProto.themes[themeName];
            if (themeName === 'gray' || themeName === 'access') {
                clsProto.themeInfo = Ext.applyIf(clsProto.themeInfo || {}, clsProto.themes.classic);
            }
        }

        cmp = new ViewClass(
                {
                    nameintern: name_ressource,
                    titre: text,
                    odatasource: ODatatasource

                });
        contentPanel.add(cmp);
        if (cmp.floating) {
            cmp.show();
        } else {
            this.centerContent();
        }

        contentPanel.setTitle(text);

        // alert("Ok dd");

        document.title = document.title.split(' - ')[0] + ' - ' + text;
        location.hash = xtype;

        this.updateDescription(clsProto);

        if (clsProto.exampleCode) {
            this.updateCodePreview(clsProto.exampleCode);
        } else {
            this.updateCodePreviewAsync(clsProto, xtype);
        }

        //  alert("Fin");

    },
    onLoadNewComponentWith2DataSource: function (ComponentXtype, ComponentLabel, name_ressource, ODatatasource, ODatatasourceparent) {
        // alert(ComponentXtype);

        var text = ComponentLabel,
                xtype = ComponentXtype,
                alias = 'widget.' + xtype,
                contentPanel = this.getContentPanel(),
                themeName = Ext.themeName,
                cmp;



        contentPanel.removeAll(true);

        var className = Ext.ClassManager.getNameByAlias(alias);

        var ViewClass = Ext.ClassManager.get(className);

        // alert("139");
        var clsProto = ViewClass.prototype;
        // alert("141");
        if (clsProto.themes) {
            clsProto.themeInfo = clsProto.themes[themeName];
            if (themeName === 'gray' || themeName === 'access') {
                clsProto.themeInfo = Ext.applyIf(clsProto.themeInfo || {}, clsProto.themes.classic);
            }
        }

        cmp = new ViewClass(
                {
                    nameintern: name_ressource,
                    titre: text,
                    odatasource: ODatatasource,
                    odatatasourceparent: ODatatasourceparent

                });
        contentPanel.add(cmp);
        if (cmp.floating) {
            cmp.show();
        } else {
            this.centerContent();
        }

        contentPanel.setTitle(text);

        // alert("Ok dd");

        document.title = document.title.split(' - ')[0] + ' - ' + text;
        location.hash = xtype;

        this.updateDescription(clsProto);

        if (clsProto.exampleCode) {
            this.updateCodePreview(clsProto.exampleCode);
        } else {
            this.updateCodePreviewAsync(clsProto, xtype);
        }

        //  alert("Fin");

    },
    onLoadNewComponentWith4DataSource: function (ComponentXtype, ComponentLabel, name_ressource, ODatatasource, ODatatasourceparent, ODatatasource1, ODatatasource2) {
        // alert(ComponentXtype);

        var text = ComponentLabel,
                xtype = ComponentXtype,
                alias = 'widget.' + xtype,
                contentPanel = this.getContentPanel(),
                themeName = Ext.themeName,
                cmp;



        contentPanel.removeAll(true);

        var className = Ext.ClassManager.getNameByAlias(alias);

        var ViewClass = Ext.ClassManager.get(className);

        // alert("139");
        var clsProto = ViewClass.prototype;
        // alert("141");
        if (clsProto.themes) {
            clsProto.themeInfo = clsProto.themes[themeName];
            if (themeName === 'gray' || themeName === 'access') {
                clsProto.themeInfo = Ext.applyIf(clsProto.themeInfo || {}, clsProto.themes.classic);
            }
        }

        cmp = new ViewClass(
                {
                    nameintern: name_ressource,
                    titre: text,
                    odatasource: ODatatasource,
                    odatatasourceparent: ODatatasourceparent,
                    odatasource1: ODatatasource1,
                    odatasource2: ODatatasource2
                });
        contentPanel.add(cmp);
        if (cmp.floating) {
            cmp.show();
        } else {
            this.centerContent();
        }

        contentPanel.setTitle(text);

        // alert("Ok dd");

        document.title = document.title.split(' - ')[0] + ' - ' + text;
        location.hash = xtype;

        this.updateDescription(clsProto);

        if (clsProto.exampleCode) {
            this.updateCodePreview(clsProto.exampleCode);
        } else {
            this.updateCodePreviewAsync(clsProto, xtype);
        }

        //  alert("Fin");

    },
    onNavSelectionChange: function (selModel, records) {
        var record = records[0],
                text = record.get('text'),
                xtype = record.get('id'),
                alias = 'widget.' + xtype,
                contentPanel = this.getContentPanel(),
                themeName = Ext.themeName,
                cmp;
        alert(xtype);
        //alert("Ok -> " + record.get('id'));

        //alert("Ok -> " + record.get('id'));


        if (xtype) { // only leaf nodes have ids


            contentPanel.removeAll(true);

            var className = Ext.ClassManager.getNameByAlias(alias);

            var ViewClass = Ext.ClassManager.get(className);

            // alert("139");
            var clsProto = ViewClass.prototype;
            // alert("141");
            if (clsProto.themes) {
                clsProto.themeInfo = clsProto.themes[themeName];
                if (themeName === 'gray' || themeName === 'access') {
                    clsProto.themeInfo = Ext.applyIf(clsProto.themeInfo || {}, clsProto.themes.classic);
                }
            }

            cmp = new ViewClass();
            contentPanel.add(cmp);
            if (cmp.floating) {
                cmp.show();
            } else {
                this.centerContent();
            }

            contentPanel.setTitle(text);

            // alert("Ok dd");

            document.title = document.title.split(' - ')[0] + ' - ' + text;
            location.hash = xtype;

            this.updateDescription(clsProto);

            if (clsProto.exampleCode) {
                this.updateCodePreview(clsProto.exampleCode);
            } else {
                this.updateCodePreviewAsync(clsProto, xtype);
            }
        } else {
            //alert("Pas ");
        }
    },
    onMaximizeClick: function () {
        var preview = this.getCodePreview(),
                code = preview.getEl().down('.prettyprint').dom.innerHTML;

        var w = new Ext.window.Window({
            rtl: false,
            baseCls: 'x-panel',
            maximized: true,
            title: 'Code Preview',
            plain: true,
            cls: 'preview-container',
            autoScroll: true,
            bodyStyle: 'background-color:white',
            html: '<pre class="prettyprint">' + code + '</pre>',
            closable: false,
            tools: [{
                    type: 'close',
                    handler: function () {
                        w.hide(preview, function () {
                            w.destroy();
                        });
                    }
                }]
        });
        w.show(preview);
    },
    processCodePreview: function (clsProto, text) {
        var me = this,
                lines = text.split('\n'),
                removing = false,
                keepLines = [],
                tempLines = [],
                n = lines.length,
                i, line;

        // Remove all "example" blocks as they are fluff.
        //
        for (i = 0; i < n; ++i) {
            line = lines[i];
            if (removing) {
                if (me.exampleRe.test(line)) {
                    removing = false;
                }
            } else if (me.exampleRe.test(line)) {
                removing = true;
            } else {
                tempLines.push(line);
            }
        }

        // Inline any themeInfo values to clarify the code.
        //
        if (clsProto.themeInfo) {
            var path = ['this', 'themeInfo'];

            function process(obj) {
                for (var name in obj) {
                    var value = obj[name];

                    path.push(name);

                    if (Ext.isPrimitive(value)) {
                        if (Ext.isString(value)) {
                            value = "'" + value + "'";
                        }
                        me.replaceValues(tempLines, path.join('.'), value);
                    } else {
                        process(value);
                    }

                    path.pop();
                }
            }

            process(clsProto.themeInfo);
        }

        // Remove any lines with remaining (unused) themeInfo. These properties will
        // be "undefined" for this theme and so are useless to the example.
        //
        for (i = 0, n = tempLines.length; i < n; ++i) {
            line = tempLines[i];
            if (line.indexOf('themeInfo') < 0) {
                keepLines.push(line);
            }
        }

        var code = keepLines.join('\n');
        code = Ext.htmlEncode(code);
        clsProto.exampleCode = code;
    },
    replaceValues: function (lines, text, value) {
        var n = lines.length,
                i, pos, line;

        for (i = 0; i < n; ++i) {
            line = lines[i];
            pos = line.indexOf(text);
            if (pos >= 0) {
                lines[i] = line.split(text).join(String(value));
            }
        }
    },
    updateCodePreview: function (text) {
        this.getCodePreview().update(
                '<pre id="code-preview-container" class="prettyprint">' + text + '</pre>'
                );
        prettyPrint();
    },
    updateCodePreviewAsync: function (clsProto, xtype) {
        var me = this,
                className = Ext.ClassManager.getNameByAlias('widget.' + xtype),
                path = className.replace(/\./g, '/').replace('KitchenSink', 'app') + '.js';

        if (!Ext.repoDevMode) {
            path = '../../../kitchensink/' + path;
        }

        Ext.Ajax.request({
            url: path,
            success: function (response) {
                me.processCodePreview(clsProto, response.responseText);
                me.updateCodePreview(clsProto.exampleCode);
            }
        });
    },
    updateDescription: function (clsProto) {
        var description = clsProto.exampleDescription,
                descriptionPanel = this.getDescriptionPanel();

        if (Ext.isArray(description)) {
            clsProto.exampleDescription = description = description.join('');
        }

        descriptionPanel.update(description);
    },
    centerContent: function () {
        var contentPanel = this.getContentPanel(),
                body = contentPanel.body,
                item = contentPanel.items.getAt(0),
                align = 'c-c',
                overflowX,
                overflowY,
                offsets;

        if (item) {
            overflowX = (body.getWidth() < (item.getWidth() + 40));
            overflowY = (body.getHeight() < (item.getHeight() + 40));

            if (overflowX && overflowY) {
                align = 'tl-tl';
                offsets = [20, 20];
            } else if (overflowX) {
                align = 'l-l';
                offsets = [20, 0];
            } else if (overflowY) {
                align = 't-t';
                offsets = [0, 20];
            }

            item.alignTo(contentPanel.body, align, offsets);
        }
    },
    onGeneratePdfFile: function (url_generate) {
        window.open(url_generate);
    },
    onLunchPrinter: function (url_printer) {
        this.ShowWaitingProcess();
        Ext.Ajax.request({
            url: url_printer,
            success: function (response)
            {
                var object = Ext.JSON.decode(response.responseText, false);
//                this.StopWaitingProcess();
                boxWaitingProcess.hide();
                if (object.success === "0") {
                    Ext.MessageBox.alert('Error Message', object.errors);
                    return;
                }

            },
            failure: function (response)
            {

                var object = Ext.JSON.decode(response.responseText, false);
                console.log("Bug " + response.responseText);
                Ext.MessageBox.alert('Error Message', response.responseText);

            }
        });

    },
    ShowWaitingProcess: function () {
        boxWaitingProcess = Ext.MessageBox.wait('Veuillez patienter . . .', 'En cours de traitement!');
    },
    StopWaitingProcess: function () {
        boxWaitingProcess.hide();
    },
    GetComponentById: function (Ovalue_component_id) {
        var Ocomponent = Ext.getCmp('' + Ovalue_component_id + '');
        return Ocomponent;
    },
    SearchArticle: function (comp_id, url) {

        var OComponent = Me_Workflow.GetComponentById(comp_id);
        var OComponent_val = Me_Workflow.trim(OComponent.getValue());
        var OFamille_store = OComponent;
        if (OComponent_val !== null && OComponent_val !== "" && OComponent_val !== undefined) {
            var OComponent_length = OComponent_val.length;

            var url_final = url + "?search_value=" + OComponent_val;

            if (OComponent_length >= 3) {
                //OFamille_store.getStore().getProxy().url = url_final;
                // OFamille_store.getStore().reload();

                var store = OFamille_store.getStore();
                store.getProxy().url = url_final;
                store.load({
                    callback: function () {
                        if (store.getCount() === 1) {
                            var rec = store.getAt(0);
                            OComponent.setValue(rec.get('str_DESCRIPTION'));
                            Ext.getCmp('lg_FAMILLE_ID_VENTE').setValue(rec.get('lg_FAMILLE_ID'));

                        }
//                        alert("ok"+store.getCount());
                    }
                });
                /*alert(store.getCount());
                 if (store.getCount() === 1) {
                 // var record=store.getAt(0);
                 //OFamille_store.setValue(record.get('str_DESCRIPTION_PLUS'));
                 
                 
                 }*/

            }
        } else {
            //alert('ici');
            OFamille_store.getStore().getProxy().url = url;
            OFamille_store.getStore().reload();

        }
    },
    onLunchPrinterBis: function (linkUrl) {
        window.open(linkUrl);
    },
    findColumnByDataIndex: function (grid, columnIndex) {
        var columnFind = grid.headerCt.getHeaderAtIndex(columnIndex);
        return columnFind;
    },
//    getMonthToDisplay: function(month, indiceTab, indiceCurrentMonth) {
    getMonthToDisplay: function (indiceTab, indiceCurrentMonth) {
        var indiceMonth = "";
        indiceMonth = indiceCurrentMonth - indiceTab;
        return month[(indiceMonth >= 0 ? indiceMonth : 12 + indiceMonth)];
    },
    onRedirectTo: function (ComponentXtype, data) {


        var
                xtype = ComponentXtype,
                alias = 'widget.' + xtype,
                contentPanel = this.getContentPanel(),
                themeName = Ext.themeName,
                cmp;



        contentPanel.removeAll(true);

        var className = Ext.ClassManager.getNameByAlias(alias);

        var ViewClass = Ext.ClassManager.get(className);

        // alert("139");
        var clsProto = ViewClass.prototype;
        // alert("141");
        if (clsProto.themes) {
            clsProto.themeInfo = clsProto.themes[themeName];
            if (themeName === 'gray' || themeName === 'access') {
                clsProto.themeInfo = Ext.applyIf(clsProto.themeInfo || {}, clsProto.themes.classic);
            }
        }

        cmp = new ViewClass(
                {

                    data: data

                });
        contentPanel.add(cmp);
        if (cmp.floating) {
            cmp.show();
        } else {
            this.centerContent();
        }


        this.updateDescription(clsProto);

        if (clsProto.exampleCode) {
            this.updateCodePreview(clsProto.exampleCode);
        } else {
            this.updateCodePreviewAsync(clsProto, xtype);
        }



    }

});
