Ext.define('testextjs.view.window.BasicWindow', {
    extend: 'Ext.window.Window',
    xtype: 'basic-window',


    height: 500,
    width: 700,
    title: 'Window',
    autoScroll: true,
    bodyPadding: 10,
    html:'Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.' ,//testextjs.DummyText.extraLongText,
    constrain: true
});