Ext.define('testextjs.view.tree.TwoTrees', {
    extend: 'Ext.container.Container',
    
    requires: [
        'Ext.tree.*',
        'Ext.data.*',
        'Ext.layout.container.HBox'
    ],
    xtype: 'tree-two',
    
    //<example>
    exampleTitle: 'Drag and Drop between 2 TreePanels',
    exampleDescription: [
        '<p>The TreePanels have a TreeSorter applied in "folderSort" mode.</p>',
        '<p>Both TreePanels are in "appendOnly" drop mode since they are sorted.</p>',
        '<p>Target node is sorted upon drop to maintain initially configured sort order</p>',
        '<p>Hover at top or bottom edge of the tree to trigger auto scrolling while performing a drag and drop.</p>',
        '<p>The data for this tree is asynchronously loaded with a TreeStore and AjaxProxy.</p>'
    ].join(''),
    //</example>
    
    layout: {
        type: 'hbox',
        align: 'stretch'
    },
    height: 300,
    width: 550,
    
    initComponent: function(){
        var group = this.id + '-ddgroup';
        
        Ext.apply(this, {
            items: [{
                title: 'Source',
                xtype: 'treepanel',
                store: new Ext.data.TreeStore({
                    proxy: {
                        type: 'ajax',
                        //<example>
                        extraParams: {
                            path: Ext.repoDevMode ? '' : 'extjs'
                        },
                        //</example>
                        url: 'resources/data/tree/get-nodes.php'
                    },
                    root: {
                        text: 'Ext JS',
                        id: 'src',
                        expanded: true
                    },
                    folderSort: true,
                    sorters: [{
                        property: 'text',
                        direction: 'ASC'
                    }]
                }),
                margin: '0 15 0 0',
                flex: 1,
                viewConfig: {
                    plugins: {
                        ptype: 'treeviewdragdrop',
                        ddGroup: group,
                        appendOnly: true,
                        sortOnDrop: true,
                        containerScroll: true
                    }
                }
            }, {
                title: 'Custom Build',
                xtype: 'treepanel',
                store: new Ext.data.TreeStore({
                    proxy: {
                        type: 'ajax',
                        url: 'resources/data/tree/get-nodes.php',
                        extraParams: {
                            path: 'extjs'
                        }
                    },
                    root: {
                        text: 'Custom Ext JS',
                        id: 'src',
                        expanded: true,
                        children: []
                    },
                    folderSort: true,
                    sorters: [{
                        property: 'text',
                        direction: 'ASC'
                    }]
                }),
                flex: 1,
                viewConfig: {
                    plugins: {
                        ptype: 'treeviewdragdrop',
                        ddGroup: group,
                        appendOnly: true,
                        sortOnDrop: true,
                        containerScroll: true
                    }
                }
            }]
        });
        this.callParent();
    }
});
