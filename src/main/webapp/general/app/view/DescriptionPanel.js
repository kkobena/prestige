Ext.define('testextjs.view.DescriptionPanel', {
    extend: 'Ext.panel.Panel',
    xtype: 'descriptionPanel',
    id: 'description-panel',
    title: 'Description',
    bodyStyle: "background-image:url(../../../resources/images/headerlb.png) !important",
    autoScroll: true,
    rtl: false,

    initComponent: function() {
        this.ui = (Ext.themeName === 'neptune') ? 'light' : 'default';
        this.callParent();
    }
});