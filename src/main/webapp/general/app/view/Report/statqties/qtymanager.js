/* global Ext */
//var now=new Date();
//var diffyear=now.getFullYear()-2015;
//var datas=[2015];
//for (var i = 1; i <= diffyear ; i++) {
//   datas.push(2015+i) ;
//}
Ext.define('testextjs.view.Report.statqties.qtymanager', {
    extend: 'Ext.panel.Panel',
    xtype: 'statproductsell',
    id: 'statproductsellID',
    requires: [
      
        'testextjs.view.Report.statqties.qtygrid'

    ],
    frame: true,
    title: 'Statistique vente produit',
    width: '98%',
    height: 570,
    minHeight: 570,
    maxHeight: 800,
    cls: 'custompanel',
//    autoScroll: true,
    layout: 'fit',
    items: [{
            xtype: 'statproduct-grid'

        }


    ] 

});


