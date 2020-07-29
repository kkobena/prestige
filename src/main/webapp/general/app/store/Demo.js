Ext.define('testextjs.store.Demo', {
    extend: 'Ext.data.Store',
    requires: [
        'testextjs.model.statistics.Demo'
    ],
    storeId: 'population',
    model:'testextjs.model.statistics.Demo',
    data: [
        {"year": "1610", "population": 350},
        {"year": "1650", "population": 50368},
        {"year": "1700", "population": 250888},
        {"year": "1750", "population": 1170760},
        {"year": "1800", "population": 5308483},
        {"year": "1900", "population": 76212168},
        {"year": "1950", "population": 151325798},
        {"year": "2000", "population": 281421906},
        {"year": "2010", "population": 308745538},
    ]
});
