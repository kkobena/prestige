
/* global Ext */

function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}
var formatter = Ext.Date.format(new Date(), 'd/m/Y');
var _thisyear = formatter.split('/')[2];
var _pastyear = Number(_thisyear) - 1;
Ext.define('testextjs.view.Report.comparaisonCAFamilles.CAFamilleGrid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.familleca-grid',
    initComponent: function () {
        var store = Ext.create('testextjs.store.Statistics.FamillesCA');

        Ext.apply(this, {
            features: [
                {
                    ftype: 'summary'
                }],
            id: 'FamillecaGrid',
            store: store,
            viewConfig: {
                forceFit: true,
                emptyText: '<h1 style="margin:10px 10px 10px 200px;">Pas de donn&eacute;es</h1>'
            },
            columns: [
                {
                    text: 'GP',
                    dataIndex: 'GP'
                }, {
                    text: 'Libellé Famille',
                    dataIndex: 'str_Libelle_Produit'
                },
                {
                    header: '01/' + _pastyear,
                    dataIndex: 'janvier_1',
                    summaryType: "sum",
                    align: 'right',
                    renderer: amountformat,
                    summaryRenderer: function (value) {
                        if (value > 0) {
                            return "<b><span style='color:blue;'>" + amountformat(value) + " F </span></b>";
                        } else {
                            return '';
                        }
                    }
                }, {
                    header: '%',
                    dataIndex: 'P_janvier_1',
                    align: 'right'
                },
                {
                    header: '01/' + _thisyear,
                    dataIndex: 'janvier',
                    summaryType: "sum",
                    align: 'right',
                    renderer: amountformat,
                    summaryRenderer: function (value) {
                        if (value > 0) {
                            return "<b><span style='color:blue;'>" + amountformat(value) + " F </span></b>";
                        } else {
                            return '';
                        }
                    }
                }, {
                    header: '%',
                    align: 'right',
                    dataIndex: 'P_janvier'
                },
                {
                    header: '%Pr',
                    align: 'right',
                    dataIndex: 'Prog_janvier'
                },
                {
                    header: '02/' + _pastyear,
                    dataIndex: 'fevrier_1',
                    align: 'right',
                    summaryType: "sum",
                    renderer: amountformat,
                    summaryRenderer: function (value) {
                        if (value > 0) {
                            return "<b><span style='color:blue;'>" + amountformat(value) + " F </span></b>";
                        } else {
                            return '';
                        }
                    }
                }, {
                    header: '%',
                    align: 'right',
                    dataIndex: 'P_fevrier_1'
                },
                {
                    header: '02/' + _thisyear,
                    dataIndex: 'fevrier',
                    align: 'right',
                    summaryType: "sum",
                    renderer: amountformat,
                    summaryRenderer: function (value) {
                        if (value > 0) {
                            return "<b><span style='color:blue;'>" + amountformat(value) + " F </span></b>";
                        } else {
                            return '';
                        }
                    }
                }, {
                    header: '%',
                    align: 'right',
                    dataIndex: 'P_fevrier'
                },
                {
                    header: '%Pr',
                    align: 'right',
                    dataIndex: 'Prog_fevrier'
                },
                {
                    header: '03/' + _pastyear,
                    dataIndex: 'mars_1',
                    align: 'right',
                    summaryType: "sum",
                    renderer: amountformat,
                    summaryRenderer: function (value) {
                        if (value > 0) {
                            return "<b><span style='color:blue;'>" + amountformat(value) + " F </span></b>";
                        } else {
                            return '';
                        }
                    }
                }, {
                    header: '%',
                    align: 'right',
                    dataIndex: 'P_mars_1'
                },
                {
                    header: '03/' + _thisyear,
                    dataIndex: 'mars',
                    align: 'right',
                    summaryType: "sum",
                    renderer: amountformat,
                    summaryRenderer: function (value) {
                        if (value > 0) {
                            return "<b><span style='color:blue;'>" + amountformat(value) + " F </span></b>";
                        } else {
                            return '';
                        }
                    }
                }, {
                    header: '%',
                    align: 'right',
                    dataIndex: 'P_mars'
                },
                {
                    header: '%Pr',
                    align: 'right',
                    dataIndex: 'Prog_mars'
                },
                {
                    header: '04/' + _pastyear,
                    dataIndex: 'avril_1',
                    align: 'right',
                    summaryType: "sum",
                    renderer: amountformat,
                    summaryRenderer: function (value) {
                        if (value > 0) {
                            return "<b><span style='color:blue;'>" + amountformat(value) + " F </span></b>";
                        } else {
                            return '';
                        }
                    }
                }, {
                    header: '%',
                    align: 'right',
                    dataIndex: 'P_avril_1'
                    
                },
                {
                    header: '04/' + _thisyear,
                    align: 'right',
                    dataIndex: 'avril',
                    summaryType: "sum",
                    renderer: amountformat,
                    summaryRenderer: function (value) {
                        if (value > 0) {
                            return "<b><span style='color:blue;'>" + amountformat(value) + " F </span></b>";
                        } else {
                            return '';
                        }
                    }
                }, {
                    header: '%',
                    align: 'right',
                    dataIndex: 'P_avril'
                }, {
                    header: '%Pr',
                    align: 'right',
                    dataIndex: 'Prog_avril'
                }, {
                    header: '05/' + _pastyear,
                    align: 'right',
                    dataIndex: 'mai_1',
                    summaryType: "sum",
                    renderer: amountformat,
                    summaryRenderer: function (value) {
                        if (value > 0) {
                            return "<b><span style='color:blue;'>" + amountformat(value) + " F </span></b>";
                        } else {
                            return '';
                        }
                    }
                }, {
                    header: '%',
                    align: 'right',
                    dataIndex: 'P_mai_1'
                },
                {
                    header: '05/' + _thisyear,
                    dataIndex: 'mai',
                    align: 'right',
                    summaryType: "sum",
                    renderer: amountformat,
                    summaryRenderer: function (value) {
                        if (value > 0) {
                            return "<b><span style='color:blue;'>" + amountformat(value) + " F </span></b>";
                        } else {
                            return '';
                        }
                    }
                }, {
                    header: '%',
                    align: 'right',
                    dataIndex: 'P_mai'
                }, {
                    header: '%Pr',
                    align: 'right',
                    dataIndex: 'Prog_mai'
                },
                {
                    header: '06/' + _pastyear,
                    dataIndex: 'juin_1',
                    align: 'right',
                    summaryType: "sum",
                    renderer: amountformat,
                    summaryRenderer: function (value) {
                        if (value > 0) {
                            return "<b><span style='color:blue;'>" + amountformat(value) + " F </span></b>";
                        } else {
                            return '';
                        }
                    }
                }, {
                    header: '%',
                    align: 'right',
                    dataIndex: 'P_juin_1'
                },
                {
                    header: '06/' + _thisyear,
                    dataIndex: 'juin',
                    align: 'right',
                    summaryType: "sum",
                    renderer: amountformat,
                    summaryRenderer: function (value) {
                        if (value > 0) {
                            return "<b><span style='color:blue;'>" + amountformat(value) + " F </span></b>";
                        } else {
                            return '';
                        }
                    }
                }, {
                    header: '%',
                    dataIndex: 'P_juin',
                    align: 'right'
                }, {
                    header: '%Pr',
                    dataIndex: 'Prog_juin',
                    align: 'right',
                    renderer: amountformat
                },
                {
                    header: '07/' + _pastyear,
                    dataIndex: 'juillet_1',
                    align: 'right',
                    summaryType: "sum",
                    renderer: amountformat,
                    summaryRenderer: function (value) {
                        if (value > 0) {
                            return "<b><span style='color:blue;'>" + amountformat(value) + " F </span></b>";
                        } else {
                            return '';
                        }
                    }
                }, {
                    header: '%',
                    align: 'right',
                    dataIndex: 'P_juillet_1'
                },
                {
                    header: '07/' + _thisyear,
                    dataIndex: 'juillet',
                    summaryType: "sum",
                    renderer: amountformat,
                    summaryRenderer: function (value) {
                        if (value > 0) {
                            return "<b><span style='color:blue;'>" + amountformat(value) + " F </span></b>";
                        } else {
                            return '';
                        }
                    }
                }, {
                    header: '%',
                     align: 'right',
                    dataIndex: 'P_juillet'
                   
                }, {
                    header: '%Pr',
                     align: 'right',
                      renderer: amountformat,
                    dataIndex: 'Prog_juillet'
                },
                {
                    header: '08/' + _pastyear,
                    dataIndex: 'aout_1',
                    align: 'right',
                    summaryType: "sum",
                    renderer: amountformat,
                    summaryRenderer: function (value) {
                        if (value > 0) {
                            return "<b><span style='color:blue;'>" + amountformat(value) + " F </span></b>";
                        } else {
                            return '';
                        }
                    }
                }, {
                    header: '%',
                    align: 'right',
                    dataIndex: 'P_aout_1'
                },
                {
                    header: '08/' + _thisyear,
                    dataIndex: 'aout',
                    align: 'right',
                    summaryType: "sum",
                    renderer: amountformat,
                    summaryRenderer: function (value) {
                        if (value > 0) {
                            return "<b><span style='color:blue;'>" + amountformat(value) + " F </span></b>";
                        } else {
                            return '';
                        }
                    }
                }, {
                    header: '%',
                    align: 'right',
                    dataIndex: 'P_aout'
                }, {
                    header: '%Pr',
                    align: 'right',
                    dataIndex: 'Prog_aout'
                },
                {
                    header: '09/' + _pastyear,
                    dataIndex: 'sep_1',
                    align: 'right',
                    summaryType: "sum",
                    renderer: amountformat,
                    summaryRenderer: function (value) {
                        if (value > 0) {
                            return "<b><span style='color:blue;'>" + amountformat(value) + " F </span></b>";
                        } else {
                            return '';
                        }
                    }
                }, {
                    header: '%',
                    align: 'right',
                    dataIndex: 'P_sep_1'
                },
                {
                    header: '09/' + _thisyear,
                    dataIndex: 'sep',
                    align: 'right',
                    summaryType: "sum",
                    renderer: amountformat,
                    summaryRenderer: function (value) {
                        if (value > 0) {
                            return "<b><span style='color:blue;'>" + amountformat(value) + " F </span></b>";
                        } else {
                            return '';
                        }
                    }
                }, {
                    header: '%',
                    align: 'right',
                    dataIndex: 'P_sep'
                }, {
                    header: '%Pr',
                    align: 'right',
                    dataIndex: 'Prog_sep'
                },
               
                {
                    header: '10/' + _pastyear,
                    dataIndex: 'oct_1',
                    align: 'right',
                    summaryType: "sum",
                    renderer: amountformat,
                    summaryRenderer: function (value) {
                        if (value > 0) {
                            return "<b><span style='color:blue;'>" + amountformat(value) + " F </span></b>";
                        } else {
                            return '';
                        }
                    }
                }, {
                    header: '%',
                    align: 'right',
                    dataIndex: 'P_oct_1'
                },
                {
                    header: '10/' + _thisyear,
                    dataIndex: 'oct',
                    align: 'right',
                    summaryType: "sum",
                    renderer: amountformat,
                    summaryRenderer: function (value) {
                        if (value > 0) {
                            return "<b><span style='color:blue;'>" + amountformat(value) + " F </span></b>";
                        } else {
                            return '';
                        }
                    }
                }, {
                    header: '%',
                    align: 'right',
                    dataIndex: 'P_oct'
                }, {
                    header: '%Pr',
                    align: 'right',
                    dataIndex: 'Prog_oct'
                },
                {
                    header: '11/' + _pastyear,
                    dataIndex: 'nov_1',
                    align: 'right',
                    summaryType: "sum",
                    renderer: amountformat,
                    summaryRenderer: function (value) {
                        if (value > 0) {
                            return "<b><span style='color:blue;'>" + amountformat(value) + " F </span></b>";
                        } else {
                            return '';
                        }
                    }
                }, {
                    header: '%',
                    align: 'right',
                    dataIndex: 'P_nov_1'
                },
                {
                    header: '11/' + _thisyear,
                    dataIndex: 'nov',
                    align: 'right',
                    summaryType: "sum",
                    renderer: amountformat,
                    summaryRenderer: function (value) {
                        if (value > 0) {
                            return "<b><span style='color:blue;'>" + amountformat(value) + " F </span></b>";
                        } else {
                            return '';
                        }
                    }
                }, {
                    header: '%',
                    align: 'right',
                    dataIndex: 'P_nov'
                }, {
                    header: '%Pr',
                    dataIndex: 'Prog_nov',
                    align: 'right'
                },
                {
                    header: '12/' + _pastyear,
                    dataIndex: 'dec_1',
                    summaryType: "sum",
                    align: 'right',
                    renderer: amountformat,
                    summaryRenderer: function (value) {
                        if (value > 0) {
                            return "<b><span style='color:blue;'>" + amountformat(value) + " F </span></b>";
                        } else {
                            return '';
                        }
                    }
                }, {
                    header: '%',
                    dataIndex: 'P_dec_1'
                },
                {
                    header: '12/' + _thisyear,
                    align: 'right',
                    dataIndex: 'dec',
                    summaryType: "sum",
                    renderer: amountformat,
                    summaryRenderer: function (value) {
                        if (value > 0) {
                            return "<b><span style='color:blue;'>" + amountformat(value) + " F </span></b>";
                        } else {
                            return '';
                        }
                    }
                }, {
                    header: '%',
                    dataIndex: 'P_dec',
                    align: 'right'
                }, {
                    header: '%Pr',
                    dataIndex: 'Prog_dec',
                    align: 'right'
                }
            ],
            selModel: {
                selType: 'cellmodel'
            },
            
            bbar: {
                xtype: 'pagingtoolbar',
                store: store, 
                dock: 'bottom',
                id: 'pagindCA',
                displayInfo: true
            }
        });

        this.callParent();
}
});


