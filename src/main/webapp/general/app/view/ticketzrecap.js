

/* global Ext, panel */

Ext.define('testextjs.view.ticketzrecap', {
    extend: 'Ext.panel.Panel',
    xtype: 'ticketzmanager',
    title: 'TICKET Z',
    requires: [
        'Ext.ux.IFrame'
    ],
    layout: 'fit',
    autoScroll: false,
    width: '99%',
    height: Ext.getBody().getViewSize().height*0.80,
    border: false,
    initComponent: function () {
        this.items = [{
                xtype: "component",
                autoScroll: false,
                border: true,
                autoEl: {
                    tag: "iframe",
                    src: 'ticketzview.html'
                }
            }],
                this.callParent();

    }
});

