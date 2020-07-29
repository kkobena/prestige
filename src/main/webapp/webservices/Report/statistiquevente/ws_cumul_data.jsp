<%@page import="dal.TUser"%>
<%@page import="toolkits.parameters.commonparameter"%>
<%@page import="java.time.LocalDate"%>
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
    LocalDate now = LocalDate.now();
    String dt_start = now.minusMonths(1).toString();
    String dt_end = LocalDate.now().toString();

    if (request.getParameter("dt_start_vente") != null && !"".equals(request.getParameter("dt_start_vente"))) {
        dt_start = request.getParameter("dt_start_vente");
        System.out.println("dt_start_vente   " + request.getParameter("dt_start_vente"));
    }

    if (request.getParameter("dt_end_vente") != null && !"".equals(request.getParameter("dt_end_vente"))) {
        dt_end = request.getParameter("dt_end_vente");

    }
   TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    dataManager OManager = new dataManager();
    OManager.initEntityManager();
    StatisticSales statisticSales = new StatisticSales(OManager);
    JSONArray data = new JSONArray();
    JSONObject json = new JSONObject();
    String janv = "1", fev = "2", mars = "3", avri = "4", mai = "5", juin = "6", juil = "7", aout = "8", sept = "9", oct = "10", nov = "11", dec = "12";
    List<TPreenregistrement> list = statisticSales.getPreenregistrementsForSalesStatistics(dt_start, dt_end,OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID());

    int count_2_cumul = 0, count_vo_cumul_2 = 0, count_vno_cumul_2 = 0;
    double brut_2_cumul = 0;
    double remise_2_cumul = 0;
    double net_2_cumul = 0;
    double pan_moy_vo_cumul_2 = 0;
    double pan_moy_vno_cumul_2 = 0;
    double ventes_vo_cumul_2 = 0, ventes_vno_cumul_2 = 0;
    int count_1_cumul = 0, count_vo_cumul_1 = 0, count_vno_cumul_1 = 0;
    double brut_1_cumul = 0;
    double remise_1_cumul = 0;
    double net_1_cumul = 0;
    double pan_moy_vo_cumul_1 = 0;
    double pan_moy_vno_cumul_1 = 0;
    double ventes_vo_cumul_1 = 0, ventes_vno_cumul_1 = 0;
    int count_3_cumul = 0, count_vo_cumul_3 = 0, count_vno_cumul_3 = 0;
    double brut_3_cumul = 0;
    double remise_3_cumul = 0;
    double net_3_cumul = 0;
    double pan_moy_vo_cumul_3 = 0;
    double pan_moy_vno_cumul_3 = 0;
    double ventes_vo_cumul_3 = 0, ventes_vno_cumul_3 = 0;

    int count_4_cumul = 0, count_vo_cumul_4 = 0, count_vno_cumul_4 = 0;
    double brut_4_cumul = 0;
    double remise_4_cumul = 0;
    double net_4_cumul = 0;
    double pan_moy_vo_cumul_4 = 0;
    double pan_moy_vno_cumul_4 = 0;
    double ventes_vo_cumul_4 = 0, ventes_vno_cumul_4 = 0;
    int count_5_cumul = 0, count_vo_cumul_5 = 0, count_vno_cumul_5 = 0;
    double brut_5_cumul = 0;
    double remise_5_cumul = 0;
    double net_5_cumul = 0;
    double pan_moy_vo_cumul_5 = 0;
    double pan_moy_vno_cumul_5 = 0;
    double ventes_vo_cumul_5 = 0, ventes_vno_cumul_5 = 0;
    int count_6_cumul = 0, count_vo_cumul_6 = 0, count_vno_cumul_6 = 0;
    double brut_6_cumul = 0;
    double remise_6_cumul = 0;
    double net_6_cumul = 0;
    double pan_moy_vo_cumul_6 = 0;
    double pan_moy_vno_cumul_6 = 0;
    double ventes_vo_cumul_6 = 0, ventes_vno_cumul_6 = 0;
    int count_7_cumul = 0, count_vo_cumul_7 = 0, count_vno_cumul_7 = 0;
    double brut_7_cumul = 0;
    double remise_7_cumul = 0;
    double net_7_cumul = 0;
    double pan_moy_vo_cumul_7 = 0;
    double pan_moy_vno_cumul_7 = 0;
    double ventes_vo_cumul_7 = 0, ventes_vno_cumul_7 = 0;
    int count_8_cumul = 0, count_vo_cumul_8 = 0, count_vno_cumul_8 = 0;
    double brut_8_cumul = 0;
    double remise_8_cumul = 0;
    double net_8_cumul = 0;
    double pan_moy_vo_cumul_8 = 0;
    double pan_moy_vno_cumul_8 = 0;
    double ventes_vo_cumul_8 = 0, ventes_vno_cumul_8 = 0;
    int count_9_cumul = 0, count_vo_cumul_9 = 0, count_vno_cumul_9 = 0;
    double brut_9_cumul = 0;
    double remise_9_cumul = 0;
    double net_9_cumul = 0;
    double pan_moy_vo_cumul_9 = 0;
    double pan_moy_vno_cumul_9 = 0;
    double ventes_vo_cumul_9 = 0, ventes_vno_cumul_9 = 0;
    int count_10_cumul = 0, count_vo_cumul_10 = 0, count_vno_cumul_10 = 0;
    double brut_10_cumul = 0;
    double remise_10_cumul = 0;
    double net_10_cumul = 0;
    double pan_moy_vo_cumul_10 = 0;
    double pan_moy_vno_cumul_10 = 0;
    double ventes_vo_cumul_10 = 0, ventes_vno_cumul_10 = 0;
    int count_11_cumul = 0, count_vo_cumul_11 = 0, count_vno_cumul_11 = 0;
    double brut_11_cumul = 0;
    double remise_11_cumul = 0;
    double net_11_cumul = 0;
    double pan_moy_vo_cumul_11 = 0;
    double pan_moy_vno_cumul_11 = 0;
    double ventes_vo_cumul_11 = 0, ventes_vno_cumul_11 = 0;
    int count_12_cumul = 0, count_vo_cumul_12 = 0, count_vno_cumul_12 = 0;
    double brut_12_cumul = 0;
    double remise_12_cumul = 0;
    double net_12_cumul = 0;
    double pan_moy_vo_cumul_12 = 0;
    double pan_moy_vno_cumul_12 = 0;
    double ventes_vo_cumul_12 = 0, ventes_vno_cumul_12 = 0;

    for (TPreenregistrement OPreenregistrement : list) {
        if (janv.equals(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            if ((Integer.valueOf(janv) >= Integer.valueOf(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) && (date.FORMATTERYEAR.format(OPreenregistrement.getDtCREATED()).equals(date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start))))) {

                count_1_cumul++;
                brut_1_cumul += OPreenregistrement.getIntPRICE();
                remise_1_cumul += OPreenregistrement.getIntPRICEREMISE();
                if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                    ventes_vno_cumul_1 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                    count_vno_cumul_1++;
                } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                    ventes_vo_cumul_1 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                    count_vo_cumul_1++;
                }

            }
        }
        if (fev.equals(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            if ((Integer.valueOf(fev) >= Integer.valueOf(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) && (date.FORMATTERYEAR.format(OPreenregistrement.getDtCREATED()).equals(date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start))))) {

                count_2_cumul++;
                brut_2_cumul += OPreenregistrement.getIntPRICE();
                remise_2_cumul += OPreenregistrement.getIntPRICEREMISE();
                if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                    ventes_vno_cumul_2 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                    count_vno_cumul_2++;
                } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                    ventes_vo_cumul_2 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                    count_vo_cumul_2++;
                }

            }
        }
        if (mars.equals(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            if ((Integer.valueOf(mars) >= Integer.valueOf(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) && (date.FORMATTERYEAR.format(OPreenregistrement.getDtCREATED()).equals(date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start))))) {
                count_3_cumul++;
                brut_3_cumul += OPreenregistrement.getIntPRICE();
                remise_3_cumul += OPreenregistrement.getIntPRICEREMISE();
                if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                    ventes_vno_cumul_3 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                    count_vno_cumul_3++;
                } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                    ventes_vo_cumul_3 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                    count_vo_cumul_3++;
                }

            }
        }
        if (avri.equals(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            if ((Integer.valueOf(avri) >= Integer.valueOf(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) && (date.FORMATTERYEAR.format(OPreenregistrement.getDtCREATED()).equals(date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start))))) {

                count_4_cumul++;
                brut_4_cumul += OPreenregistrement.getIntPRICE();
                remise_4_cumul += OPreenregistrement.getIntPRICEREMISE();
                if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                    ventes_vno_cumul_4 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                    count_vno_cumul_4++;
                } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                    ventes_vo_cumul_4 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                    count_vo_cumul_4++;
                }

            }
        }
        if (mai.equals(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            if ((Integer.valueOf(mai) >= Integer.valueOf(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) && (date.FORMATTERYEAR.format(OPreenregistrement.getDtCREATED()).equals(date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start))))) {

                count_5_cumul++;
                brut_5_cumul += OPreenregistrement.getIntPRICE();
                remise_5_cumul += OPreenregistrement.getIntPRICEREMISE();
                if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                    ventes_vno_cumul_5 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                    count_vno_cumul_5++;
                } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                    ventes_vo_cumul_5 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                    count_vo_cumul_5++;
                }

            }
        }
        if (dec.equals(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            if ((Integer.valueOf(dec) >= Integer.valueOf(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) && (date.FORMATTERYEAR.format(OPreenregistrement.getDtCREATED()).equals(date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start))))) {

                count_12_cumul++;
                brut_12_cumul += OPreenregistrement.getIntPRICE();
                remise_12_cumul += OPreenregistrement.getIntPRICEREMISE();
                if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                    ventes_vno_cumul_12 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                    count_vno_cumul_12++;
                } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                    ventes_vo_cumul_12 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                    count_vo_cumul_12++;
                }

            }
        }
        if (nov.equals(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            if ((Integer.valueOf(nov) >= Integer.valueOf(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) && (date.FORMATTERYEAR.format(OPreenregistrement.getDtCREATED()).equals(date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start))))) {

                count_11_cumul++;
                brut_11_cumul += OPreenregistrement.getIntPRICE();
                remise_11_cumul += OPreenregistrement.getIntPRICEREMISE();
                if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                    ventes_vno_cumul_11 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                    count_vno_cumul_11++;
                } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                    ventes_vo_cumul_11 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                    count_vo_cumul_11++;
                }

            }
        }
        if (oct.equals(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            if ((Integer.valueOf(oct) >= Integer.valueOf(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) && (date.FORMATTERYEAR.format(OPreenregistrement.getDtCREATED()).equals(date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start))))) {

                count_10_cumul++;
                brut_10_cumul += OPreenregistrement.getIntPRICE();
                remise_10_cumul += OPreenregistrement.getIntPRICEREMISE();
                if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                    ventes_vno_cumul_10 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                    count_vno_cumul_10++;
                } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                    ventes_vo_cumul_10 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                    count_vo_cumul_10++;
                }

            }
        }
        if (juin.equals(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            if ((Integer.valueOf(juin) >= Integer.valueOf(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) && (date.FORMATTERYEAR.format(OPreenregistrement.getDtCREATED()).equals(date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start))))) {

                count_6_cumul++;
                brut_6_cumul += OPreenregistrement.getIntPRICE();
                remise_6_cumul += OPreenregistrement.getIntPRICEREMISE();
                if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                    ventes_vno_cumul_6 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                    count_vno_cumul_6++;
                } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                    ventes_vo_cumul_6 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                    count_vo_cumul_6++;
                }

            }
        }
        if (juil.equals(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            if ((Integer.valueOf(juil) >= Integer.valueOf(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) && (date.FORMATTERYEAR.format(OPreenregistrement.getDtCREATED()).equals(date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start))))) {

                count_7_cumul++;
                brut_7_cumul += OPreenregistrement.getIntPRICE();
                remise_7_cumul += OPreenregistrement.getIntPRICEREMISE();
                if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                    ventes_vno_cumul_7 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                    count_vno_cumul_7++;
                } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                    ventes_vo_cumul_7 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                    count_vo_cumul_7++;
                }

            }
        }
        if (aout.equals(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            if ((Integer.valueOf(aout) >= Integer.valueOf(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) && (date.FORMATTERYEAR.format(OPreenregistrement.getDtCREATED()).equals(date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start))))) {

                count_8_cumul++;
                brut_8_cumul += OPreenregistrement.getIntPRICE();
                remise_8_cumul += OPreenregistrement.getIntPRICEREMISE();
                if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                    ventes_vno_cumul_8 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                    count_vno_cumul_8++;
                } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                    ventes_vo_cumul_8 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                    count_vo_cumul_8++;
                }

            }
        }
        if (sept.equals(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            if ((Integer.valueOf(sept) >= Integer.valueOf(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) && (date.FORMATTERYEAR.format(OPreenregistrement.getDtCREATED()).equals(date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start))))) {

                count_9_cumul++;
                brut_9_cumul += OPreenregistrement.getIntPRICE();
                remise_9_cumul += OPreenregistrement.getIntPRICEREMISE();
                if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                    ventes_vno_cumul_9 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                    count_vno_cumul_9++;
                } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                    ventes_vo_cumul_9 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                    count_vo_cumul_9++;
                }

            }
        }

    }

    net_1_cumul = brut_1_cumul - remise_1_cumul;
    if (count_vno_cumul_1 > 0) {
        pan_moy_vno_cumul_1 = ventes_vno_cumul_1 / count_vno_cumul_1;
    }
    if (count_vo_cumul_1 > 0) {
        pan_moy_vo_cumul_1 = ventes_vo_cumul_1 / count_vo_cumul_1;
    }

    net_2_cumul = brut_2_cumul - remise_2_cumul;

    if (count_vno_cumul_2 > 0) {
        pan_moy_vno_cumul_2 = ventes_vno_cumul_2 / count_vno_cumul_2;
    }
    if (count_vo_cumul_2 > 0) {
        pan_moy_vo_cumul_2 = ventes_vo_cumul_2 / count_vo_cumul_2;
    }

    net_3_cumul = brut_3_cumul - remise_3_cumul;

    if (count_vno_cumul_3 > 0) {
        pan_moy_vno_cumul_3 = ventes_vno_cumul_3 / count_vno_cumul_3;
    }
    if (count_vo_cumul_3 > 0) {
        pan_moy_vo_cumul_3 = ventes_vo_cumul_3 / count_vo_cumul_3;
    }

    net_4_cumul = brut_4_cumul - remise_4_cumul;

    if (count_vno_cumul_4 > 0) {
        pan_moy_vno_cumul_4 = ventes_vno_cumul_4 / count_vno_cumul_4;
    }
    if (count_vo_cumul_4 > 0) {
        pan_moy_vo_cumul_4 = ventes_vo_cumul_4 / count_vo_cumul_4;
    }

    net_5_cumul = brut_5_cumul - remise_5_cumul;

    if (count_vno_cumul_5 > 0) {
        pan_moy_vno_cumul_5 = ventes_vno_cumul_5 / count_vno_cumul_5;
    }
    if (count_vo_cumul_5 > 0) {
        pan_moy_vo_cumul_5 = ventes_vo_cumul_5 / count_vo_cumul_5;
    }

    net_6_cumul = brut_6_cumul - remise_6_cumul;

    if (count_vno_cumul_6 > 0) {
        pan_moy_vno_cumul_6 = ventes_vno_cumul_6 / count_vno_cumul_6;
    }
    if (count_vo_cumul_6 > 0) {
        pan_moy_vo_cumul_6 = ventes_vo_cumul_6 / count_vo_cumul_6;
    }

    net_7_cumul = brut_7_cumul - remise_7_cumul;

    if (count_vno_cumul_7 > 0) {
        pan_moy_vno_cumul_7 = ventes_vno_cumul_7 / count_vno_cumul_7;
    }
    if (count_vo_cumul_7 > 0) {
        pan_moy_vo_cumul_7 = ventes_vo_cumul_7 / count_vo_cumul_7;
    }

    net_8_cumul = brut_8_cumul - remise_8_cumul;

    if (count_vno_cumul_8 > 0) {
        pan_moy_vno_cumul_8 = ventes_vno_cumul_8 / count_vno_cumul_8;
    }
    if (count_vo_cumul_8 > 0) {
        pan_moy_vo_cumul_8 = ventes_vo_cumul_8 / count_vo_cumul_8;
    }

    net_9_cumul = brut_9_cumul - remise_9_cumul;

    if (count_vno_cumul_9 > 0) {
        pan_moy_vno_cumul_9 = ventes_vno_cumul_9 / count_vno_cumul_9;
    }
    if (count_vo_cumul_9 > 0) {
        pan_moy_vo_cumul_9 = ventes_vo_cumul_9 / count_vo_cumul_9;
    }

    net_10_cumul = brut_10_cumul - remise_10_cumul;

    if (count_vno_cumul_10 > 0) {
        pan_moy_vno_cumul_10 = ventes_vno_cumul_10 / count_vno_cumul_10;
    }
    if (count_vo_cumul_10 > 0) {
        pan_moy_vo_cumul_10 = ventes_vo_cumul_10 / count_vo_cumul_10;
    }

    net_11_cumul = brut_11_cumul - remise_11_cumul;

    if (count_vno_cumul_11 > 0) {
        pan_moy_vno_cumul_11 = ventes_vno_cumul_11 / count_vno_cumul_11;
    }
    if (count_vo_cumul_11 > 0) {
        pan_moy_vo_cumul_11 = ventes_vo_cumul_11 / count_vo_cumul_11;
    }

    net_12_cumul = brut_12_cumul - remise_12_cumul;

    if (count_vno_cumul_12 > 0) {
        pan_moy_vno_cumul_12 = ventes_vno_cumul_12 / count_vno_cumul_12;
    }
    if (count_vo_cumul_12 > 0) {
        pan_moy_vo_cumul_12 = ventes_vo_cumul_12 / count_vo_cumul_12;
    }

    json.put("id", 1);
    //json.put("month", "12/" + date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start)));
    json.put("month", "12/" + date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start)));
    json.put("N Clients Cumul", count_12_cumul);
    json.put("M BrutTTC Cumul", brut_12_cumul);
    json.put("Remise Cumul", remise_12_cumul);
    json.put("M NetTTC Cumul", net_12_cumul);
    json.put("Pan MoyOrd Cumul", pan_moy_vo_cumul_12);
    json.put("Pan MoyNo Cumul", pan_moy_vno_cumul_12);
    json.put("Vente Ord Cumul", ventes_vo_cumul_12);
    json.put("Vente No Cumul", ventes_vno_cumul_12);
    data.put(json);
    json = new JSONObject();
    json.put("id", 2);
    json.put("month", "11/" + date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start)));
    json.put("N Clients Cumul", count_11_cumul);
    json.put("M BrutTTC Cumul", brut_11_cumul);
    json.put("Remise Cumul", remise_11_cumul);
    json.put("M NetTTC Cumul", net_11_cumul);
    json.put("Pan MoyOrd Cumul", pan_moy_vo_cumul_11);
    json.put("Pan MoyNo Cumul", pan_moy_vno_cumul_11);
    json.put("Vente Ord Cumul", ventes_vo_cumul_11);
    json.put("Vente No Cumul", ventes_vno_cumul_11);
    data.put(json);
    json = new JSONObject();
    json.put("id", 3);
    json.put("month", "10/" + date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start)));
    json.put("N Clients Cumul", count_10_cumul);
    json.put("M BrutTTC Cumul", brut_10_cumul);
    json.put("Remise Cumul", remise_10_cumul);
    json.put("M NetTTC Cumul", net_10_cumul);
    json.put("Pan MoyOrd Cumul", pan_moy_vo_cumul_10);
    json.put("Pan MoyNo Cumul", pan_moy_vno_cumul_10);
    json.put("Vente Ord Cumul", ventes_vo_cumul_10);
    json.put("Vente No Cumul", ventes_vno_cumul_10);
    data.put(json);

    json = new JSONObject();
    json.put("id", 4);
    json.put("month", "09/" + date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start)));
    json.put("N Clients Cumul", count_9_cumul);
    json.put("M BrutTTC Cumul", brut_9_cumul);
    json.put("Remise Cumul", remise_9_cumul);
    json.put("M NetTTC Cumul", net_9_cumul);
    json.put("Pan MoyOrd Cumul", pan_moy_vo_cumul_9);
    json.put("Pan MoyNo Cumul", pan_moy_vno_cumul_9);
    json.put("Vente Ord Cumul", ventes_vo_cumul_9);
    json.put("Vente No Cumul", ventes_vno_cumul_9);
    data.put(json);

    json = new JSONObject();
    json.put("id", 5);
    json.put("month", "08/" + date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start)));
    json.put("N Clients Cumul", count_8_cumul);
    json.put("M BrutTTC Cumul", brut_8_cumul);
    json.put("Remise Cumul", remise_8_cumul);
    json.put("M NetTTC Cumul", net_8_cumul);
    json.put("Pan MoyOrd Cumul", pan_moy_vo_cumul_8);
    json.put("Pan MoyNo Cumul", pan_moy_vno_cumul_8);
    json.put("Vente Ord Cumul", ventes_vo_cumul_8);
    json.put("Vente No Cumul", ventes_vno_cumul_8);
    data.put(json);
    json = new JSONObject();
    json.put("id", 6);
    json.put("month", "07/" + date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start)));
    json.put("N Clients Cumul", count_7_cumul);
    json.put("M BrutTTC Cumul", brut_7_cumul);
    json.put("Remise Cumul", remise_7_cumul);
    json.put("M NetTTC Cumul", net_7_cumul);
    json.put("Pan MoyOrd Cumul", pan_moy_vo_cumul_7);
    json.put("Pan MoyNo Cumul", pan_moy_vno_cumul_7);
    json.put("Vente Ord Cumul", ventes_vo_cumul_7);
    json.put("Vente No Cumul", ventes_vno_cumul_7);
    data.put(json);

    json = new JSONObject();
    json.put("id", 7);
    json.put("month", "06/" + date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start)));
    json.put("N Clients Cumul", count_6_cumul);
    json.put("M BrutTTC Cumul", brut_6_cumul);
    json.put("Remise Cumul", remise_6_cumul);
    json.put("M NetTTC Cumul", net_6_cumul);
    json.put("Pan MoyOrd Cumul", pan_moy_vo_cumul_6);
    json.put("Pan MoyNo Cumul", pan_moy_vno_cumul_6);
    json.put("Vente Ord Cumul", ventes_vo_cumul_6);
    json.put("Vente No Cumul", ventes_vno_cumul_6);
    data.put(json);

    json = new JSONObject();
    json.put("id", 8);
    json.put("month", "05/" + date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start)));
    json.put("N Clients Cumul", count_5_cumul);
    json.put("M BrutTTC Cumul", brut_5_cumul);
    json.put("Remise Cumul", remise_5_cumul);
    json.put("M NetTTC Cumul", net_5_cumul);
    json.put("Pan MoyOrd Cumul", pan_moy_vo_cumul_5);
    json.put("Pan MoyNo Cumul", pan_moy_vno_cumul_5);
    json.put("Vente Ord Cumul", ventes_vo_cumul_5);
    json.put("Vente No Cumul", ventes_vno_cumul_5);

    data.put(json);

    json = new JSONObject();
    json.put("id", 9);
    json.put("month", "04/" + date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start)));
    json.put("N Clients Cumul", count_4_cumul);
    json.put("M BrutTTC Cumul", brut_4_cumul);
    json.put("Remise Cumul", remise_4_cumul);
    json.put("M NetTTC Cumul", net_4_cumul);
    json.put("Pan MoyOrd Cumul", pan_moy_vo_cumul_4);
    json.put("Pan MoyNo Cumul", pan_moy_vno_cumul_4);
    json.put("Vente Ord Cumul", ventes_vo_cumul_4);
    json.put("Vente No Cumul", ventes_vno_cumul_4);
    data.put(json);
    json = new JSONObject();
    json.put("id", 10);
    json.put("month", "03/" + date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start)));
    json.put("N Clients Cumul", count_3_cumul);
    json.put("M BrutTTC Cumul", brut_3_cumul);
    json.put("Remise Cumul", remise_3_cumul);
    json.put("M NetTTC Cumul", net_3_cumul);
    json.put("Pan MoyOrd Cumul", pan_moy_vo_cumul_3);
    json.put("Pan MoyNo Cumul", pan_moy_vno_cumul_3);
    json.put("Vente Ord Cumul", ventes_vo_cumul_3);
    json.put("Vente No Cumul", ventes_vno_cumul_3);

    data.put(json);

    json = new JSONObject();
    json.put("id", 11);
    json.put("month", "02/" + date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start)));
    json.put("N Clients Cumul", count_2_cumul);
    json.put("M BrutTTC Cumul", brut_2_cumul);
    json.put("Remise Cumul", remise_2_cumul);
    json.put("M NetTTC Cumul", net_2_cumul);
    json.put("Pan MoyOrd Cumul", pan_moy_vo_cumul_2);
    json.put("Pan MoyNo Cumul", pan_moy_vno_cumul_2);
    json.put("Vente Ord Cumul", ventes_vo_cumul_2);
    json.put("Vente No Cumul", ventes_vno_cumul_2);

    data.put(json);

    json = new JSONObject();
    json.put("id", 12);
    json.put("month", "01/" + date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start)));
    json.put("N Clients Cumul", count_1_cumul);
    json.put("M BrutTTC Cumul", brut_1_cumul);
    json.put("Remise Cumul", remise_1_cumul);
    json.put("M NetTTC Cumul", net_1_cumul);
    json.put("Pan MoyOrd Cumul", pan_moy_vo_cumul_1);
    json.put("Pan MoyNo Cumul", pan_moy_vno_cumul_1);
    json.put("Vente Ord Cumul", ventes_vo_cumul_1);
    json.put("Vente No Cumul", ventes_vno_cumul_1);
    data.put(json);

    JSONObject jSONObject = new JSONObject();
    jSONObject.put("data", data);

    jSONObject.put("total", data.length());

%>

<%= jSONObject%>