Ext.define('testextjs.view.configmanagement.famille.action.WizardForm', {
  extend  : 'Ext.form.Panel',
  alias   : 'widget.wizard',
  width   : 400,
  //height  : 150,
  layout  : 'card',
  itemId  : 'wizardForm',
  title   : 'Wizard Based Registration',
  defaults: {
    //border  : true,
    bodyPadding: 20
  },
  items: [{
    xtype: 'personal'
  }/*,{
    xtype: 'comptabilitefamille'
  },{
    xtype: 'autreinfosfamille'
  }*/]
});
