/* global Ext */


//var todaydate=new Date();
//var diff=todaydate.getFullYear()-2015;
//var data=[2015];
//for (var i = 1; i <= diff ; i++) {
//   data.push(2015+i) ;
//}
Ext.define('testextjs.view.Report.statsAchats.statsAchats', {
    extend: 'Ext.panel.Panel',
    xtype: 'orderstatscmp',
    id: 'orderstatscmpID',
    requires: [
        'testextjs.view.Report.statsAchats.statAchatsgrid'

    ],
    frame: true,
    title: 'Statistique des achats',
    width: '98%',
    height: 570,
    minHeight: 570,
    maxHeight: 800,
    cls: 'custompanel',
//    autoScroll: true,
    layout: 'fit',
    items: [{
            xtype: 'statachats-grid'

        }


    ]

});


