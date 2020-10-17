/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/* global Ext */

Ext.define('testextjs.model.caisse.MedecinModel', {
    extend: 'Ext.data.Model',
    idProperty: 'id',
    fields: [
        {
            name: 'id',
            type: 'string'
        },
        {
            name: 'commentaire',
            type: 'string'
        },
        {
            name: 'nom',
            type: 'string'
        },
        {
            name: 'numOrdre',
            type: 'string'
        }

    ]
});
