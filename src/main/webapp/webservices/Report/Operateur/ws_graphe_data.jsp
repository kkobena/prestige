<%@page import="bll.common.Parameter"%>
<%@page import="dal.TPreenregistrement"%>
<%@page import="java.util.List"%>
<%@page import="toolkits.utils.date"%>
<%@page import="dal.dataManager"%>
<%@page import="bll.report.StatisticSales"%>
<%@page import="java.util.Date"%>
<%@page import="org.json.JSONObject"%>
<%@page import="org.json.JSONArray"%>
 




<%
     String dt_start = "";
    String dt_end = "";
    if (request.getParameter("dt_start_vente") != null && !"".equals(request.getParameter("dt_start_vente"))) {
        dt_start = request.getParameter("dt_start_vente");
        System.out.println("dt_start_vente   " + request.getParameter("dt_start_vente"));
    }

    if (request.getParameter("dt_end_vente") != null && !"".equals(request.getParameter("dt_end_vente"))) {
        dt_end = request.getParameter("dt_end_vente");

    }
    if("".equals(dt_start)){
        dt_start=date.formatterMysqlShort.format(new Date());
    }

    dataManager OManager = new dataManager();
    OManager.initEntityManager();
    StatisticSales statisticSales = new StatisticSales(OManager);
    JSONArray data = new JSONArray();
    JSONObject json = new JSONObject();
    String janv = "1", fev = "2", mars = "3", avri = "4", mai = "5", juin = "6", juil = "7", aout = "8", sept = "9", oct = "10", nov = "11", dec = "12";
    List<TPreenregistrement> list = statisticSales.getPreenregistrementsForSalesStatistics(dt_start, dt_end,"1");
    /* start boocle for */
    int count_1 = 0, count_vo_1 = 0, count_vno_1 = 0;
    double brut_1 = 0;
    double remise_1 = 0;
    double net_1 = 0;
    double pan_moy_vo_1 = 0;
    double pan_moy_vno_1 = 0;
    double ventes_vo = 0, ventes_vno = 0;
    double vente_percent_vno = 0;
    double vente_percent_vo = 0;
    int count_2 = 0, count_vo_2 = 0, count_vno_2 = 0;
    double brut_2 = 0;
    double remise_2 = 0;
    double net_2 = 0;
    double pan_moy_vo_2 = 0;
    double pan_moy_vno_2 = 0;
    double ventes_vo_2 = 0, ventes_vno_2 = 0;
    double vente_percent_vno_2 = 0;
    double vente_percent_vo_2 = 0;

    int count_3 = 0, count_vo_3 = 0, count_vno_3 = 0;
    double brut_3 = 0;
    double remise_3 = 0;
    double net_3 = 0;
    double pan_moy_vo_3 = 0;
    double pan_moy_vno_3 = 0;
    double ventes_vo_3 = 0, ventes_vno_3 = 0;
    double vente_percent_vno_3 = 0;
    double vente_percent_vo_3 = 0;
    int count_4 = 0, count_vo_4 = 0, count_vno_4 = 0;
    double brut_4 = 0;
    double remise_4 = 0;
    double net_4 = 0;
    double pan_moy_vo_4 = 0;
    double pan_moy_vno_4 = 0;
    double ventes_vo_4 = 0, ventes_vno_4 = 0;
    double vente_percent_vno_4 = 0;
    double vente_percent_vo_4 = 0;
    int count_5 = 0, count_vo_5 = 0, count_vno_5 = 0;
    double brut_5 = 0;
    double remise_5 = 0;
    double net_5 = 0;
    double pan_moy_vo_5 = 0;
    double pan_moy_vno_5 = 0;
    double ventes_vo_5 = 0, ventes_vno_5 = 0;
    double vente_percent_vno_5 = 0;
    double vente_percent_vo_5 = 0;
    int count_6 = 0, count_vo_6 = 0, count_vno_6 = 0;
    double brut_6 = 0;
    double remise_6 = 0;
    double net_6 = 0;
    double pan_moy_vo_6 = 0;
    double pan_moy_vno_6 = 0;
    double ventes_vo_6 = 0, ventes_vno_6 = 0;
    double vente_percent_vno_6 = 0;
    double vente_percent_vo_6 = 0;
    int count_7 = 0, count_vo_7 = 0, count_vno_7 = 0;
    double brut_7 = 0;
    double remise_7 = 0;
    double net_7 = 0;
    double pan_moy_vo_7 = 0;
    double pan_moy_vno_7 = 0;
    double ventes_vo_7 = 0, ventes_vno_7 = 0;
    double vente_percent_vno_7 = 0;
    double vente_percent_vo_7 = 0;
    int count_8 = 0, count_vo_8 = 0, count_vno_8 = 0;
    double brut_8 = 0;
    double remise_8 = 0;
    double net_8 = 0;
    double pan_moy_vo_8 = 0;
    double pan_moy_vno_8 = 0;
    double ventes_vo_8 = 0, ventes_vno_8 = 0;
    double vente_percent_vno_8 = 0;
    double vente_percent_vo_8 = 0;
    int count_9 = 0, count_vo_9 = 0, count_vno_9 = 0;
    double brut_9 = 0;
    double remise_9 = 0;
    double net_9 = 0;
    double pan_moy_vo_9 = 0;
    double pan_moy_vno_9 = 0;
    double ventes_vo_9 = 0, ventes_vno_9 = 0;
    double vente_percent_vno_9 = 0;
    double vente_percent_vo_9 = 0;
    int count_10 = 0, count_vo_10 = 0, count_vno_10 = 0;
    double brut_10 = 0;
    double remise_10 = 0;
    double net_10 = 0;
    double pan_moy_vo_10 = 0;
    double pan_moy_vno_10 = 0;
    double ventes_vo_10 = 0, ventes_vno_10 = 0;
    double vente_percent_vno_10 = 0;
    double vente_percent_vo_10 = 0;
    int count_11 = 0, count_vo_11 = 0, count_vno_11 = 0;
    double brut_11 = 0;
    double remise_11 = 0;
    double net_11 = 0;
    double pan_moy_vo_11 = 0;
    double pan_moy_vno_11 = 0;
    double ventes_vo_11 = 0, ventes_vno_11 = 0;
    double vente_percent_vno_11 = 0;
    double vente_percent_vo_11 = 0;
    int count_12 = 0, count_vo_12 = 0, count_vno_12 = 0;
    double brut_12 = 0;
    double remise_12 = 0;
    double net_12 = 0;
    double pan_moy_vo_12 = 0;
    double pan_moy_vno_12 = 0;
    double ventes_vo_12 = 0, ventes_vno_12 = 0;
    double vente_percent_vno_12 = 0;
    double vente_percent_vo_12 = 0;

    for (TPreenregistrement OPreenregistrement : list) {
        if (janv.equals(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            count_1++;
            brut_1 += OPreenregistrement.getIntPRICE();
            remise_1 += OPreenregistrement.getIntPRICEREMISE();

            if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vno += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vno_1++;
            } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vo += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vo_1++;
            }
        }
        if (fev.equals(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            count_2++;
            brut_2 += OPreenregistrement.getIntPRICE();
            remise_2 += OPreenregistrement.getIntPRICEREMISE();

            if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vno_2 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vno_2++;
            } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vo_2 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vo_2++;
            }
        }
        if (mars.equals(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            count_3++;
            brut_3 += OPreenregistrement.getIntPRICE();
            remise_3 += OPreenregistrement.getIntPRICEREMISE();

            if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vno_3 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vno_3++;
            } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vo_3 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vo_3++;
            }
        }
        if (avri.equals(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            count_4++;
            brut_4 += OPreenregistrement.getIntPRICE();
            remise_4 += OPreenregistrement.getIntPRICEREMISE();

            if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vno_4 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vno_4++;
            } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vo_4 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vo_4++;
            }
        }
        if (mai.equals(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            count_5++;
            brut_5 += OPreenregistrement.getIntPRICE();
            remise_5 += OPreenregistrement.getIntPRICEREMISE();

            if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vno_5 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vno_5++;
            } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vo_5 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vo_5++;
            }
        }
        if (dec.equals(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            count_12++;
            brut_12 += OPreenregistrement.getIntPRICE();
            remise_12 += OPreenregistrement.getIntPRICEREMISE();

            if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vno_12 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vno_12++;
            } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vo_12 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vo_12++;
            }
        }
        if (nov.equals(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            count_11++;
            brut_11 += OPreenregistrement.getIntPRICE();
            remise_11 += OPreenregistrement.getIntPRICEREMISE();

            if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vno_11 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vno_11++;
            } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vo_11 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vo_11++;
            }
        }
        if (oct.equals(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            count_10++;
            brut_10 += OPreenregistrement.getIntPRICE();
            remise_10 += OPreenregistrement.getIntPRICEREMISE();

            if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vno_10 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vno_10++;
            } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vo_10 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vo_10++;
            }
        }
        if (juin.equals(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            count_6++;
            brut_6 += OPreenregistrement.getIntPRICE();
            remise_6 += OPreenregistrement.getIntPRICEREMISE();

            if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vno_6 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vno_6++;
            } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vo_6 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vo_6++;
            }
        }
        if (juil.equals(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            count_7++;
            brut_7 += OPreenregistrement.getIntPRICE();
            remise_7 += OPreenregistrement.getIntPRICEREMISE();

            if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vno_7 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vno_7++;
            } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vo_7 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vo_7++;
            }
        }
        if (aout.equals(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            count_8++;
            brut_8 += OPreenregistrement.getIntPRICE();
            remise_8 += OPreenregistrement.getIntPRICEREMISE();

            if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vno_8 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vno_8++;
            } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vo_8 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vo_8++;
            }
        }
        if (sept.equals(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            count_9++;
            brut_9 += OPreenregistrement.getIntPRICE();
            remise_9 += OPreenregistrement.getIntPRICEREMISE();

            if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vno_9 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vno_9++;
            } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vo_9 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vo_9++;
            }
        }

    }
   net_1 = brut_1 - remise_1;
   
    if (count_vno_1 > 0) {
        pan_moy_vno_1 = ventes_vno / count_vno_1;
    }
    if (count_vo_1 > 0) {
        pan_moy_vo_1 = ventes_vo / count_vo_1;
    }
   
    

    

    /*if (ventes_vo > 0 || ventes_vno > 0) {
        vente_percent_vo = (ventes_vo * 100) / (ventes_vo + ventes_vno);
        vente_percent_vno = (ventes_vno * 100) / (ventes_vno + ventes_vo);
    }*/

    net_2 = brut_2 - remise_2;
     
    if (count_vno_2 > 0) {
        pan_moy_vno_2 = ventes_vno_2 / count_vno_2;
    }
    if (count_vo_2 > 0) {
        pan_moy_vo_2 = ventes_vo_2 / count_vo_2;
    }
  if (ventes_vo_2 > 0 || ventes_vno_2 > 0) {
        vente_percent_vo_2 = (ventes_vo_2 * 100) / (ventes_vo_2 + ventes_vno_2);
        vente_percent_vno_2 = (ventes_vno_2 * 100) / (ventes_vno_2 + ventes_vo_2);
    }
    net_3 = brut_3 - remise_3;
    
    if (count_vno_3 > 0) {
        pan_moy_vno_3 = ventes_vno_3 / count_vno_3;
    }
    if (count_vo_3 > 0) {
        pan_moy_vo_3 = ventes_vo_3 / count_vo_3;
    }
   
    

   
  /* if (ventes_vo_3 > 0 || ventes_vno_3 > 0) {
        vente_percent_vo_3 = (ventes_vo_3 * 100) / (ventes_vo_3 + ventes_vno_3);
        vente_percent_vno_3 = (ventes_vno_3 * 100) / (ventes_vno_3 + ventes_vo_3);
    }*/
    net_4 = brut_4 - remise_4;
    
    if (count_vno_4 > 0) {
        pan_moy_vno_4 = ventes_vno_4 / count_vno_4;
    }
    if (count_vo_4 > 0) {
        pan_moy_vo_4 = ventes_vo_4 / count_vo_4;
    }
   
    

    if (ventes_vo_4 > 0 || ventes_vno_4 > 0) {
        vente_percent_vo_4 = (ventes_vo_4 * 100) / (ventes_vo_4 + ventes_vno_4);
        vente_percent_vno_4 = (ventes_vno_4 * 100) / (ventes_vno_4 + ventes_vo_4);
    }
    net_5 = brut_5 - remise_5;
    
    if (count_vno_5 > 0) {
        pan_moy_vno_5 = ventes_vno_5 / count_vno_5;
    }
    if (count_vo_5 > 0) {
        pan_moy_vo_5 = ventes_vo_5 / count_vo_5;
    }
   
  

   /* if (ventes_vo_5 > 0 || ventes_vno_5 > 0) {
        vente_percent_vo_5 = (ventes_vo_5 * 100) / (ventes_vo_5 + ventes_vno_5);
        vente_percent_vno_5 = (ventes_vno_5 * 100) / (ventes_vno_5 + ventes_vo_5);
    }*/
    net_6 = brut_6 - remise_6;
   
    if (count_vno_6 > 0) {
        pan_moy_vno_6 = ventes_vno_6 / count_vno_6;
    }
    if (count_vo_6 > 0) {
        pan_moy_vo_6 = ventes_vo_6 / count_vo_6;
    }
   

   

    /*if (ventes_vo_6 > 0 || ventes_vno_6 > 0) {
        vente_percent_vo_6 = (ventes_vo_6 * 100) / (ventes_vo_6 + ventes_vno_6);
        vente_percent_vno_6 = (ventes_vno_6 * 100) / (ventes_vno_6 + ventes_vo_6);
    }*/
    net_7 = brut_7 - remise_7;
   
    if (count_vno_7 > 0) {
        pan_moy_vno_7 = ventes_vno_7 / count_vno_7;
    }
    if (count_vo_7 > 0) {
        pan_moy_vo_7 = ventes_vo_7 / count_vo_7;
    }
  

    /*if (ventes_vo_7 > 0 || ventes_vno_7 > 0) {
        vente_percent_vo_7 = (ventes_vo_7 * 100) / (ventes_vo_7 + ventes_vno_7);
        vente_percent_vno_7 = (ventes_vno_7 * 100) / (ventes_vno_7 + ventes_vo_7);
    }*/
    net_8 = brut_8 - remise_8;
   
    if (count_vno_8 > 0) {
        pan_moy_vno_8 = ventes_vno_8 / count_vno_8;
    }
    if (count_vo_8 > 0) {
        pan_moy_vo_8 = ventes_vo_8 / count_vo_8;
    }
  /*if (ventes_vo_8 > 0 || ventes_vno_8 > 0) {
        vente_percent_vo_8 = (ventes_vo_8 * 100) / (ventes_vo_8 + ventes_vno_8);
        vente_percent_vno_8 = (ventes_vno_8 * 100) / (ventes_vno_8 + ventes_vo_8);
    }*/
    net_9 = brut_9 - remise_9;
   
    if (count_vno_9 > 0) {
        pan_moy_vno_9 = ventes_vno_9 / count_vno_9;
    }
    if (count_vo_9 > 0) {
        pan_moy_vo_9 = ventes_vo_9 / count_vo_9;
    }
  
   /* if (ventes_vo_9 > 0 || ventes_vno_9 > 0) {
        vente_percent_vo_9 = (ventes_vo_9 * 100) / (ventes_vo_9 + ventes_vno_9);
        vente_percent_vno_9 = (ventes_vno_9 * 100) / (ventes_vno_9 + ventes_vo_9);
    }*/
    net_10 = brut_10 - remise_10;
   
    if (count_vno_10 > 0) {
        pan_moy_vno_10 = ventes_vno_10 / count_vno_10;
    }
    if (count_vo_10 > 0) {
        pan_moy_vo_10 = ventes_vo_10 / count_vo_10;
    }


   /* if (ventes_vo_10 > 0 || ventes_vno_10 > 0) {
        vente_percent_vo_10 = (ventes_vo_10 * 100) / (ventes_vo_10 + ventes_vno_10);
        vente_percent_vno_10 = (ventes_vno_10 * 100) / (ventes_vno_10 + ventes_vo_10);
    }*/ 
    net_11 = brut_11 - remise_11;
   
    if (count_vno_11 > 0) {
        pan_moy_vno_11 = ventes_vno_11 / count_vno_11;
    }
    if (count_vo_11 > 0) {
        pan_moy_vo_11 = ventes_vo_11 / count_vo_11;
    }
    
    /*if (ventes_vo_11 > 0 || ventes_vno_11 > 0) {
        vente_percent_vo_11 = (ventes_vo_11 * 100) / (ventes_vo_11 + ventes_vno_11);
        vente_percent_vno_11 = (ventes_vno_11 * 100) / (ventes_vno_11 + ventes_vo_11);
    }*/
    net_12 = brut_12 - remise_12;
    
    if (count_vno_12 > 0) {
        pan_moy_vno_12 = ventes_vno_12 / count_vno_12;
    }
    if (count_vo_12 > 0) {
        pan_moy_vo_12 = ventes_vo_12 / count_vo_12;
    }
    
   /* if (ventes_vo_12 > 0 || ventes_vno_12 > 0) {
        vente_percent_vo_12 = (ventes_vo_12 * 100) / (ventes_vo_12 + ventes_vno_12);
        vente_percent_vno_12 = (ventes_vno_12 * 100) / (ventes_vno_12 + ventes_vo_12);
    }
    */

   
     
     
     
    json.put("id", 1);
    //json.put("month", "12/" + date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start)));
    json.put("month", "12/" + date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start)));
    json.put("N Clients", count_12);
    json.put("M BrutTTC", brut_12);
    json.put("Remise", remise_12);
    json.put("M NetTTC", net_12);
    json.put("Pan MoyOrd", pan_moy_vo_12);
    json.put("Pan MoyNo", pan_moy_vno_12);
    json.put("Vente Ord", ventes_vo_12);
    json.put("Vente No", ventes_vno_12);
    data.put(json);
    json = new JSONObject();
    json.put("id", 2);
    json.put("month", "11/" + date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start)));
    json.put("N Clients", count_11);
    json.put("M BrutTTC", brut_11);
    json.put("Remise", remise_11);
    json.put("M NetTTC", net_11);
    json.put("Pan MoyOrd", pan_moy_vo_11);
    json.put("Pan MoyNo", pan_moy_vno_11);
    json.put("Vente Ord", ventes_vo_11);
    json.put("Vente No", ventes_vno_11);
    data.put(json);

    json = new JSONObject();
    json.put("id", 3);
    json.put("month", "10/" + date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start)));
    json.put("N Clients", count_10);
    json.put("M BrutTTC", brut_10);
    json.put("Remise", remise_10);
    json.put("M NetTTC", net_10);
    json.put("Pan MoyOrd", pan_moy_vo_10);
    json.put("Pan MoyNo", pan_moy_vno_10);
    json.put("Vente Ord", ventes_vo_10);
    json.put("Vente No", ventes_vno_10);
    data.put(json);

    json = new JSONObject();
    json.put("id", 4);
    json.put("month", "09/" + date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start)));
    json.put("N Clients", count_9);
    json.put("M BrutTTC", brut_9);
    json.put("Remise", remise_9);
    json.put("M NetTTC", net_9);
    json.put("Pan MoyOrd", pan_moy_vo_9);
    json.put("Pan MoyNo", pan_moy_vno_9);
    json.put("Vente Ord", ventes_vo_9);
    json.put("Vente No", ventes_vno_9);
    data.put(json);

    json = new JSONObject();
    json.put("id", 5);
    json.put("month", "08/" + date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start)));
    json.put("N Clients", count_8);
    json.put("M BrutTTC", brut_8);
    json.put("Remise", remise_8);
    json.put("M NetTTC", net_8);
    json.put("Pan MoyOrd", pan_moy_vo_8);
    json.put("Pan MoyNo", pan_moy_vno_8);
    json.put("Vente Ord", ventes_vo_8);
    json.put("Vente No", ventes_vno_8);
    data.put(json);
 json = new JSONObject();
    json.put("id", 6);
    json.put("month", "07/" + date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start)));
    json.put("N Clients", count_7);
    json.put("M BrutTTC", brut_7);
    json.put("Remise", remise_7);
    json.put("M NetTTC", net_7);
    json.put("Pan MoyOrd", pan_moy_vo_7);
    json.put("Pan MoyNo", pan_moy_vno_7);
    json.put("Vente Ord", ventes_vo_7);
    json.put("Vente No", ventes_vno_7);
    data.put(json);

    json = new JSONObject();
    json.put("id", 7);
    json.put("month", "06/" + date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start)));
    json.put("N Clients", count_6);
    json.put("M BrutTTC", brut_6);
    json.put("Remise", remise_6);
    json.put("M NetTTC", net_6);
    json.put("Pan MoyOrd", pan_moy_vo_6);
    json.put("Pan MoyNo", pan_moy_vno_6);
    json.put("Vente Ord", ventes_vo_6);
    json.put("Vente No", ventes_vno_6);
    data.put(json);

    json = new JSONObject();
    json.put("id", 8);
    json.put("month", "05/" + date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start)));
    json.put("N Clients", count_5);
    json.put("M BrutTTC", brut_5);
    json.put("Remise", remise_5);
    json.put("M NetTTC", net_5);
    json.put("Pan MoyOrd", pan_moy_vo_5);
    json.put("Pan MoyNo", pan_moy_vno_5);
    json.put("Vente Ord", ventes_vo_5);
    json.put("Vente No", ventes_vno_5);

    data.put(json);

    json = new JSONObject();
    json.put("id", 9);
    json.put("month", "04/" + date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start)));
    json.put("N Clients", count_4);
    json.put("M BrutTTC", brut_4);
    json.put("Remise", remise_4);
    json.put("M NetTTC", net_4);
    json.put("Pan MoyOrd", pan_moy_vo_4);
    json.put("Pan MoyNo", pan_moy_vno_4);
    json.put("Vente Ord", ventes_vo_4);
    json.put("Vente No", ventes_vno_4);
    data.put(json);
 json = new JSONObject();
    json.put("id", 10);
    json.put("month", "03/" + date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start)));
    json.put("N Clients", count_3);
    json.put("M BrutTTC", brut_3);
    json.put("Remise", remise_3);
    json.put("M NetTTC", net_3);
    json.put("Pan MoyOrd", pan_moy_vo_3);
    json.put("Pan MoyNo", pan_moy_vno_3);
    json.put("Vente Ord", ventes_vo_3);
    json.put("Vente No", ventes_vno_3);

    data.put(json);

    json = new JSONObject();
    json.put("id", 11);
    json.put("month", "02/" + date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start)));
    json.put("N Clients", count_2);
    json.put("M BrutTTC", brut_2);
    json.put("Remise", remise_2);
    json.put("M NetTTC", net_2);
    json.put("Pan MoyOrd", pan_moy_vo_2);
    json.put("Pan MoyNo", pan_moy_vno_2);
    json.put("Vente Ord", ventes_vo_2);
    json.put("Vente No", ventes_vno_2);

    data.put(json);

    json = new JSONObject();
    json.put("id", 12);
    json.put("month", "01/" + date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start)));
     json.put("N Clients", count_1);
    json.put("M BrutTTC", brut_1);
    json.put("Remise", remise_1);
    json.put("M NetTTC", net_1);
    json.put("Pan MoyOrd", pan_moy_vo_1);
    json.put("Pan MoyNo", pan_moy_vno_1);
    json.put("Vente Ord", ventes_vo);
    json.put("Vente No", ventes_vno);
    data.put(json);

    JSONObject jSONObject = new JSONObject();
    jSONObject.put("data", data);
    System.out.println(jSONObject.toString());
    jSONObject.put("total", data.length()); 

%>

<%= jSONObject%>