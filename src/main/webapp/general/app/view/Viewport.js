/* global Ext */

Ext.define('testextjs.view.Viewport', {
    extend: 'Ext.container.Viewport',
    requires: [
        'Ext.layout.container.Border',
        'testextjs.view.layout.North',
        'testextjs.view.layout.West',
        'testextjs.view.layout.Center'

                ,
        'testextjs.view.Header',
        'testextjs.view.ThemeSwitcher',
        'testextjs.view.Navigation',
        'testextjs.view.ContentPanel',
        'testextjs.view.DescriptionPanel',
        'testextjs.view.CodePreview'


    ],
    layout: {
        type: 'border'
    },
    items: [
        {xtype: 'appHeader', region: 'north'},
        {
            region: 'west',
            xtype: 'navigation',
            width: 250,
            minWidth: 100,
            height: 200,
            split: true,
            stateful: true,
            stateId: 'mainnav.west',
            collapsible: true,
            tools: [{
                    type: 'gear',
                    regionTool: true
                }],
            listeners: {
                ///a decommenter la ligne le lundi avant la livraison
                beforerender: function() // au chargement de la page
                {
                    this.collapse(); // refermer le menu au chargement de la page
                }
                
            }
        },
        {
            region: 'center',
            xtype: 'contentPanel',
            autoScroll: true,
            height:Ext.getBody().getViewSize().height
        } /*{ 
         region: 'east',
         id: 'east-region',
         title: 'Example Info',
         stateful: true,
         stateId: 'mainnav.east',
         split: true,
         collapsible: true,
         layout: {
         type: 'vbox',
         align: 'stretch'
         },
         width: 250,
         height: 200,
         minWidth: 100,
         tools: [{
         type: 'gear',
         regionTool: true
         }],
         items: [{
         xtype: 'descriptionPanel',
         stateful: true,
         stateId: 'mainnav.east.description',
         height: 200,
         minHeight: 100
         }, {
         xtype: 'splitter',
         collapsible: true,
         collapseTarget: 'prev'
         }, {
         xtype: 'codePreview',
         flex: 1//,
         //minHeight: 100
         }]
         }*/
    ]
});


