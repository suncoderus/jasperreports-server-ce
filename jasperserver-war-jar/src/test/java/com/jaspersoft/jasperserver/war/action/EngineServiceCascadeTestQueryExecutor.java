/*
 * Copyright © 2005 - 2018 TIBCO Software Inc.
 * http://www.jaspersoft.com.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.jaspersoft.jasperserver.war.action;

import com.jaspersoft.jasperserver.dto.reports.inputcontrols.InputControlOption;
import org.apache.commons.collections.OrderedMap;
import org.apache.commons.collections.map.LinkedMap;

import java.util.*;

import static com.jaspersoft.jasperserver.war.cascade.handlers.ParametersHelper.*;

/**
 * Fake executor that emulates ReportParametersAction.executeQuery,
 * it takes that same arguments as method takes
 * and returns that same resultSet as the database returns
 * but it encapsulates logic that behave like controls are in cascade:
 * Country, State, AccountType, Industry, Name.
 *
 * Thus we are independent form database in the test.
 *
 * @author Anton Fomin
 * @version $Id: EngineServiceCascadeTestQueryExecutor.java 22715 2012-03-21 19:04:33Z afomin $
 */
public class EngineServiceCascadeTestQueryExecutor {

    public static OrderedMap executeOrderedMap(String parameterName, Map<String, Object> parameterValues) {

        String wrongParameterValue = null;
        if (parameterValues.get(CASCADE_COUNTRY) != null && !(parameterValues.get(CASCADE_COUNTRY) instanceof Collection)) { wrongParameterValue = CASCADE_COUNTRY; }
        else if (parameterValues.get(CASCADE_STATE) != null && !(parameterValues.get(CASCADE_STATE) instanceof Collection)) { wrongParameterValue = CASCADE_STATE; }
        else if (parameterValues.get(CASCADE_ACCOUNT_TYPE) instanceof Collection) { wrongParameterValue = CASCADE_ACCOUNT_TYPE; }
        else if (parameterValues.get(CASCADE_INDUSTRY) instanceof Collection) { wrongParameterValue = CASCADE_INDUSTRY; }
        else if (parameterValues.get(CASCADE_NAME) instanceof Collection) { wrongParameterValue = CASCADE_NAME; }
        if (wrongParameterValue != null) {
            String message = new StringBuilder()
                    .append("Parameter ").append(wrongParameterValue).append(" has wrong type - ")
                    .append(parameterValues.get(wrongParameterValue) != null ? parameterValues.get(wrongParameterValue).getClass().toString() : "Null")
                    .append("!").toString();
            throw new IllegalArgumentException(message);
        }

        Map<String, Collection<String>> valuesAsList = new HashMap<String, Collection<String>>();
        for (Map.Entry<String, Object> entry : parameterValues.entrySet()) {
            if (entry.getValue() == null) {
                valuesAsList.put(entry.getKey(), Collections.<String>emptyList());
            } else if (entry.getValue() instanceof Collection) {
                valuesAsList.put(entry.getKey(), (Collection<String>) entry.getValue());
            } else {
                List paramList = new ArrayList();
                paramList.add(entry.getValue());
                valuesAsList.put(entry.getKey(), paramList);
            }
        }

        OrderedMap results = new LinkedMap();

        if (parameterName.equals(CASCADE_COUNTRY)) {
            feedResults(results, "USA", "Ukraine", "Canada");
        } else if (parameterName.equals(CASCADE_STATE)) {
            Collection<String> countries = !valuesAsList.get(CASCADE_COUNTRY).isEmpty()
                    ? valuesAsList.get(CASCADE_COUNTRY) : list("USA", "Ukraine", "Canada");
            for (Object country : countries) {
                if ("USA".equals(country)) {
                    feedResults(results, "CA", "OR", "WA");
                } else if ("Ukraine".equals(country)) {
                    feedResults(results, "Zakarpatska", "Kyivska", "Kharkivska");
                } else if ("Canada".equals(country)) {
                    addResult("BC", results);
                }
            }
        } else if (parameterName.equals(CASCADE_ACCOUNT_TYPE)) {
            feedResults(results, "Consulting", "Distribution", "Manufactoring");
        } else if (parameterName.equals(CASCADE_INDUSTRY)) {
            Collection<String> states = !(valuesAsList.get(CASCADE_STATE) == null || valuesAsList.get(CASCADE_STATE).isEmpty())
                    ? valuesAsList.get(CASCADE_STATE) : list("CA", "OR", "WA", "Zakarpatska", "Kyivska", "Kharkivska", "BC");
            for (Object state : states) {
                if ("CA".equals(state)) {
                    for (Object accountType : valuesAsList.get(CASCADE_ACCOUNT_TYPE)) {
                        if ("Consulting".equals(accountType)) {
                            feedResults(results, "Engineering", "Machinery");
                        } else if ("Distribution".equals(accountType)) {
                            feedResults(results, "Communications", "Engineering", "Telecommunications");
                        } else if ("Manufactoring".equals(accountType)) {
                            feedResults(results, "Construction", "Engineering", "Machinery", "Telecommunications");
                        }
                    }
                } else if ("OR".equals(state)) {
                    for (Object accountType : valuesAsList.get(CASCADE_ACCOUNT_TYPE)) {
                        if ("Consulting".equals(accountType)) {
                            addResult("Construction", results);
                            addResult("Communications", results);
                        } else if ("Distribution".equals(accountType)) {
                            addResult("Communications", results);
                            addResult("Engineering", results);
                            addResult("Machinery", results);
                        } else if ("Manufactoring".equals(accountType)) {
                            addResult("Machinery", results);
                            addResult("Telecommunications", results);
                        }
                    }
                } else if ("WA".equals(state)) {
                    for (Object accountType : valuesAsList.get(CASCADE_ACCOUNT_TYPE)) {
                        if ("Consulting".equals(accountType)) {
                            addResult("Communications", results);
                            addResult("Engineering", results);
                        } else if ("Distribution".equals(accountType)) {
                            addResult("Machinery", results);
                            addResult("Telecommunications", results);
                        } else if ("Manufactoring".equals(accountType)) {
                            addResult("Construction", results);
                            addResult("Engineering", results);
                            addResult("Machinery", results);
                            addResult("Telecommunications", results);
                        }
                    }
                } else if ("Zakarpatska".equals(state)) {
                   for (Object accountType : valuesAsList.get(CASCADE_ACCOUNT_TYPE)) {
                        if ("Consulting".equals(accountType)) {
                            /* Leave industries here empty */
                        } else if ("Distribution".equals(accountType)) {
                            addResult("Machinery", results);
                            addResult("Telecommunications", results);
                        } else if ("Manufactoring".equals(accountType)) {
                            addResult("Communications", results);
                            addResult("Telecommunications", results);
                        }
                    }
                } else if ("Kyivska".equals(state)) {
                   for (Object accountType : valuesAsList.get(CASCADE_ACCOUNT_TYPE)) {
                        if ("Consulting".equals(accountType)) {
                            feedResults(results, "Construction", "Engineering", "Machinery", "Telecommunications");
                        } else if ("Distribution".equals(accountType)) {
                            addResult("Engineering", results);
                            addResult("Telecommunications", results);
                        } else if ("Manufactoring".equals(accountType)) {
                            addResult("Communications", results);
                            addResult("Engineering", results);
                            addResult("Machinery", results);
                        }
                    }
                } else if ("BC".equals(state)) {
                   for (Object accountType : valuesAsList.get(CASCADE_ACCOUNT_TYPE)) {
                        if ("Consulting".equals(accountType)) {
                            addResult("Construction", results);
                            addResult("Machinery", results);
                            addResult("Telecommunications", results);
                        } else if ("Distribution".equals(accountType)) {
                            addResult("Communications", results);
                            addResult("Telecommunications", results);
                        } else if ("Manufactoring".equals(accountType)) {
                            addResult("Telecommunications", results);
                            addResult("Engineering", results);
                        }
                    }
                }
            }
        } else if (parameterName.equals(CASCADE_NAME)) {
            Collection<String> states = !(valuesAsList.get(CASCADE_STATE) == null || valuesAsList.get(CASCADE_STATE).isEmpty())
                    ? valuesAsList.get(CASCADE_STATE) : list("CA", "OR", "WA", "Zakarpatska", "Kyivska", "Kharkivska", "BC");
            for (Object state : states) {
                for (Object accountType : valuesAsList.get(CASCADE_ACCOUNT_TYPE)) {
                    for (Object industry : valuesAsList.get(CASCADE_INDUSTRY)) {
                        if ("CA".equals(state)) {
                            if ("Consulting".equals(accountType)) {
                                if ("Construction".equals(industry)) {
                                    addResult("Bill & Co., Ltd", results);
                                    addResult("Charley & Son", results);
                                } else if ("Communications".equals(industry)) {
                                    feedResults(results, "ConnectIt, LLC", "Nice Communications, Inc");
                                } else if ("Engineering".equals(industry)) {
                                    feedResults(results, "EngBureau, Ltd", "BestEngineering, Inc", "SuperSoft, LLC");
                                } else if ("Machinery".equals(industry)) {
                                    feedResults(results, "SmartMachines, LLC", "Rage Against The Machines, LLC");
                                } else if ("Telecommunications".equals(industry)) {
                                    addResult("Uninterrupted Calls, Inc", results);
                                    addResult("Infinity Communication Calls, Ltd", results);
                                    addResult("BeHappy Telecom, Inc", results);
                                }
                            } else if ("Distribution".equals(accountType)) {
                                if ("Construction".equals(industry)) {
                                    addResult("C & A Magenheim Engineering Group", results);
                                    addResult("Mehlert-Duran Construction Group", results);
                                } else if ("Communications".equals(industry)) {
                                    addResult("M & Y Bruno Communications, Ltd", results);
                                    addResult("Stewart-Velasquez Electronics Holdings", results);
                                    addResult("X & M Dubois Construction Corp", results);
                                } else if ("Engineering".equals(industry)) {
                                    addResult("Dubois-Maestas Telecommunications, Ltd", results);
                                    addResult("Azari-Dabit Telecommunications, Ltd", results);
                                    addResult("D & A Carmona Machinery Group", results);
                                } else if ("Machinery".equals(industry)) {
                                    addResult("D & Z McLaughlin Electronics, Ltd", results);
                                    addResult("Henry-Barros Engineering Company", results);
                                } else if ("Telecommunications".equals(industry)) {
                                    addResult("W & N Brumagen Telecommunications Partners", results);
                                    addResult("D & D Wilson Electronics Holdings", results);
                                    addResult("B & P Pierson Telecommunications Holdings", results);
                                }
                            } else if ("Manufactoring".equals(accountType)) {
                                if ("Construction".equals(industry)) {
                                    addResult("Neuhauser-Coke Transportation Holdings", results);
                                    addResult("Warren-Chow-Wang Machinery, Inc", results);
                                    addResult("E & M Wood Electronics Company", results);
                                } else if ("Communications".equals(industry)) {
                                    addResult("W & W Solano Electronics Associates", results);
                                    addResult("Bayol-Thumann Machinery Associates", results);
                                } else if ("Engineering".equals(industry)) {
                                    addResult("Scheulen-Koon Construction Group", results);
                                    addResult("N & W Amole Transportation Corp", results);
                                } else if ("Machinery".equals(industry)) {
                                    addResult("Sutton-Mlincek Communications Associates", results);
                                } else if ("Telecommunications".equals(industry)) {
                                    addResult("Anderson-Sosa Telecommunications, Ltd", results);
                                    addResult("Fulcher-Berg Engineering Corp", results);
                                }
                            }
                        } else if ("OR".equals(state)) {
                            if ("Consulting".equals(accountType)) {
                                if ("Construction".equals(industry)) {
                                    addResult("Q & Q Lopez Telecommunications Associates", results);
                                    addResult("X & N Eichorn Construction Corp", results);
                                } else if ("Communications".equals(industry)) {
                                    addResult("J & C Anderson Telecommunications Associates", results);
                                    addResult("E & Q Curtsinger Engineering, Ltd", results);
                                } else if ("Engineering".equals(industry)) {
                                    addResult("Detwiler-Biltoft Transportation Corp", results);
                                    addResult("F & M Detwiler Transportation Corp", results);
                                } else if ("Machinery".equals(industry)) {
                                    addResult("O & C Younce Construction, Inc", results);
                                    addResult("Doolittle-Burnham Transportation Corp", results);
                                } else if ("Telecommunications".equals(industry)) {
                                    addResult("I & G Sharp Machinery Group", results);
                                    addResult("D & E Sall Telecommunications, Inc", results);
                                }
                           } else if ("Distribution".equals(accountType)) {
                                if ("Construction".equals(industry)) {
                                    addResult("T & P Zablah Engineering, Inc", results);
                                    addResult("Bowen-Pochert Engineering Associates", results);
                                } else if ("Communications".equals(industry)) {
                                    addResult("J & B Heiser Telecommunications Company", results);
                                    addResult("Romero-Hicks Construction, Ltd", results);
                                } else if ("Engineering".equals(industry)) {
                                    addResult("C & Y Difatta Machinery Partners", results);
                                    addResult("Mills-Reed Electronics Associates", results);
                                } else if ("Machinery".equals(industry)) {
                                    addResult("B & X Rybolt Electronics, Inc", results);
                                    addResult("H & G Van Antwerp Machinery Corp", results);
                                } else if ("Telecommunications".equals(industry)) {
                                    addResult("X & V Thorne Communications Partners", results);
                                    addResult("Short-Swesey Engineering Corp", results);
                                }
                            } else if ("Manufactoring".equals(accountType)) {
                                if ("Construction".equals(industry)) {
                                    addResult("Carlson-Margolis Machinery Company", results);
                                    addResult("S & Y Miller Construction, Ltd", results);
                                } else if ("Communications".equals(industry)) {
                                    addResult("X & V Slusher Construction, Ltd", results);
                                    addResult("X & C McCain Transportation, Ltd", results);
                                } else if ("Engineering".equals(industry)) {
                                    addResult("LaTulippe-Lewis Engineering Group", results);
                                    addResult("Y & B Bomar Communications Partners", results);
                                } else if ("Machinery".equals(industry)) {
                                    addResult("L & O Wheeler Machinery Group", results);
                                    addResult("Wheeler-Irvin Machinery Corp     ", results);
                                } else if ("Telecommunications".equals(industry)) {
                                    addResult("J & R Green Construction Group", results);
                                    addResult("Green-Stanford Communications Associates", results);
                                }
                            }
                        } else if ("WA".equals(state)) {
                            if ("Consulting".equals(accountType)) {
                                if ("Construction".equals(industry)) {
                                    feedResults(results, "Bruha-Suggs Construction, Ltd", "Q & Q Lopez Telecommunications Associates", "X & N Eichorn Construction Corp");
                                } else if ("Communications".equals(industry)) {
                                    feedResults(results, "J & C Anderson Telecommunications Associates", "E & Q Curtsinger Engineering, Ltd");
                                } else if ("Engineering".equals(industry)) {
                                    feedResults(results, "D & D Barrera Transportation, Ltd", "Detwiler-Biltoft Transportation Corp", "F & M Detwiler Transportation Corp");
                                } else if ("Machinery".equals(industry)) {
                                    feedResults(results, "Mann-Prater Electronics Corp", "O & C Younce Construction, Inc", "Doolittle-Burnham Transportation Corp");
                                } else if ("Telecommunications".equals(industry)) {
                                    addResult("I & G Sharp Machinery Group", results);
                                    addResult("D & E Sall Telecommunications, Inc", results);
                                }
                            } else if ("Distribution".equals(accountType)) {
                                if ("Construction".equals(industry)) {
                                    addResult("Cardoza-Hall Engineering, Inc", results);
                                    addResult("T & P Zablah Engineering, Inc", results);
                                    addResult("Bowen-Pochert Engineering Associates", results);
                                } else if ("Communications".equals(industry)) {
                                    addResult("U & W Tate Construction Corp", results);
                                    addResult("J & B Heiser Telecommunications Company", results);
                                    addResult("Romero-Hicks Construction, Ltd", results);
                                } else if ("Engineering".equals(industry)) {
                                    addResult("Tate-Watson Electronics Company", results);
                                    addResult("C & Y Difatta Machinery Partners", results);
                                    addResult("Mills-Reed Electronics Associates", results);
                                } else if ("Machinery".equals(industry)) {
                                    addResult("B & X Rybolt Electronics, Inc", results);
                                    addResult("H & G Van Antwerp Machinery Corp", results);
                                } else if ("Telecommunications".equals(industry)) {
                                    addResult("X & V Thorne Communications Partners", results);
                                    addResult("Short-Swesey Engineering Corp", results);
                                }
                            } else if ("Manufactoring".equals(accountType)) {
                                if ("Construction".equals(industry)) {
                                    addResult("U & S Hass Transportation, Inc", results);
                                    addResult("Neuhauser-Coke Transportation Holdings", results);
                                    addResult("Warren-Chow-Wang Machinery, Inc", results);
                                    addResult("E & M Wood Electronics Company", results);
                                } else if ("Communications".equals(industry)) {
                                    addResult("Aguilar-Vanderbout Communications Associates", results);
                                    addResult("W & W Solano Electronics Associates", results);
                                    addResult("Bayol-Thumann Machinery Associates", results);
                                } else if ("Engineering".equals(industry)) {
                                    addResult("H & Y Smith Engineering, Inc", results);
                                    addResult("N & W Amole Transportation Corp", results);
                                } else if ("Machinery".equals(industry)) {
                                    addResult("Cook-Thompson Transportation Holdings", results);
                                    addResult("Sutton-Mlincek Communications Associates", results);
                                } else if ("Telecommunications".equals(industry)) {
                                    addResult("V & A Gentry Transportation Corp", results);
                                    addResult("Anderson-Sosa Telecommunications, Ltd", results);
                                    addResult("Fulcher-Berg Engineering Corp", results);
                                }
                            }
                        } else if ("Kyivska".equals(state)) {
                            if ("Consulting".equals(accountType)) {
                                if ("Construction".equals(industry)) {
                                    feedResults(results, "Bill & Co., Ltd", "Charley & Son");
                                } else if ("Communications".equals(industry)) {
                                    addResult("ConnectIt, LLC", results);
                                    addResult("Nice Communications, Inc", results);
                                } else if ("Engineering".equals(industry)) {
                                    feedResults(results, "EngBureau, Ltd", "Infinity Communication Calls, Ltd", "SuperSoft, LLC");
                                } else if ("Machinery".equals(industry)) {
                                    feedResults(results, "SmartMachines, LLC", "Rage Against The Machines, LLC");
                                } else if ("Telecommunications".equals(industry)) {
                                    addResult("Uninterrupted Calls, Inc", results);
                                    addResult("Infinity Communication Calls, Ltd", results);
                                    addResult("BeHappy Telecom, Inc", results);
                                }
                            } else if ("Distribution".equals(accountType)) {
                                if ("Construction".equals(industry)) {
                                    addResult("T & P Zablah Engineering, Inc", results);
                                    addResult("Bowen-Pochert Engineering Associates", results);
                                } else if ("Communications".equals(industry)) {
                                    addResult("J & B Heiser Telecommunications Company", results);
                                    addResult("Romero-Hicks Construction, Ltd", results);
                                } else if ("Engineering".equals(industry)) {
                                    addResult("C & Y Difatta Machinery Partners", results);
                                    addResult("Mills-Reed Electronics Associates", results);
                                } else if ("Machinery".equals(industry)) {
                                    addResult("B & X Rybolt Electronics, Inc", results);
                                    addResult("H & G Van Antwerp Machinery Corp", results);
                                } else if ("Telecommunications".equals(industry)) {
                                    addResult("X & V Thorne Communications Partners", results);
                                    addResult("Short-Swesey Engineering Corp", results);
                                }
                            } else if ("Manufactoring".equals(accountType)) {
                                if ("Construction".equals(industry)) {
                                    addResult("U & S Hass Transportation, Inc", results);
                                    addResult("Neuhauser-Coke Transportation Holdings", results);
                                    addResult("Warren-Chow-Wang Machinery, Inc", results);
                                    addResult("E & M Wood Electronics Company", results);
                                } else if ("Communications".equals(industry)) {
                                    addResult("Aguilar-Vanderbout Communications Associates", results);
                                    addResult("W & W Solano Electronics Associates", results);
                                    addResult("Bayol-Thumann Machinery Associates", results);
                                } else if ("Engineering".equals(industry)) {
                                    addResult("H & Y Smith Engineering, Inc", results);
                                    addResult("N & W Amole Transportation Corp", results);
                                } else if ("Machinery".equals(industry)) {
                                    addResult("Cook-Thompson Transportation Holdings", results);
                                    addResult("Sutton-Mlincek Communications Associates", results);
                                } else if ("Telecommunications".equals(industry)) {
                                    addResult("V & A Gentry Transportation Corp", results);
                                    addResult("Anderson-Sosa Telecommunications, Ltd", results);
                                    addResult("Fulcher-Berg Engineering Corp", results);
                                }
                            }
                        } else if ("Zakarpatska".equals(state)) {
                            if ("Consulting".equals(accountType)) {
                                if ("Construction".equals(industry)) {
                                    addResult("Bruha-Suggs Construction, Ltd", results);
                                    addResult("Q & Q Lopez Telecommunications Associates", results);
                                    addResult("X & N Eichorn Construction Corp", results);
                                } else if ("Communications".equals(industry)) {
                                    addResult("J & C Anderson Telecommunications Associates", results);
                                    addResult("E & Q Curtsinger Engineering, Ltd", results);
                                } else if ("Engineering".equals(industry)) {
                                    addResult("D & D Barrera Transportation, Ltd", results);
                                    addResult("Detwiler-Biltoft Transportation Corp", results);
                                    addResult("F & M Detwiler Transportation Corp", results);
                                } else if ("Machinery".equals(industry)) {
                                    addResult("Mann-Prater Electronics Corp", results);
                                    addResult("O & C Younce Construction, Inc", results);
                                    addResult("Doolittle-Burnham Transportation Corp", results);
                                } else if ("Telecommunications".equals(industry)) {
                                    addResult("I & G Sharp Machinery Group", results);
                                    addResult("D & E Sall Telecommunications, Inc", results);
                                }
                            } else if ("Distribution".equals(accountType)) {
                                if ("Construction".equals(industry)) {
                                    addResult("T & P Zablah Engineering, Inc", results);
                                    addResult("Bowen-Pochert Engineering Associates", results);
                                } else if ("Communications".equals(industry)) {
                                    addResult("J & B Heiser Telecommunications Company", results);
                                    addResult("Romero-Hicks Construction, Ltd", results);
                                } else if ("Engineering".equals(industry)) {
                                    addResult("C & Y Difatta Machinery Partners", results);
                                    addResult("Mills-Reed Electronics Associates", results);
                                } else if ("Machinery".equals(industry)) {
                                    addResult("B & X Rybolt Electronics, Inc", results);
                                    addResult("H & G Van Antwerp Machinery Corp", results);
                                } else if ("Telecommunications".equals(industry)) {
                                    addResult("X & V Thorne Communications Partners", results);
                                    addResult("Short-Swesey Engineering Corp", results);
                                    addResult("Aguilar-Vanderbout Communications Associates", results);
                                }
                            } else if ("Manufactoring".equals(accountType)) {
                                if ("Construction".equals(industry)) {
                                    addResult("Neuhauser-Coke Transportation Holdings", results);
                                    addResult("Warren-Chow-Wang Machinery, Inc", results);
                                    addResult("E & M Wood Electronics Company", results);
                                } else if ("Communications".equals(industry)) {
                                    addResult("W & W Solano Electronics Associates", results);
                                    addResult("Bayol-Thumann Machinery Associates", results);
                                } else if ("Engineering".equals(industry)) {
                                    addResult("Scheulen-Koon Construction Group", results);
                                    addResult("N & W Amole Transportation Corp", results);
                                } else if ("Machinery".equals(industry)) {
                                    addResult("Sutton-Mlincek Communications Associates", results);
                                } else if ("Telecommunications".equals(industry)) {
                                    addResult("Anderson-Sosa Telecommunications, Ltd", results);
                                    addResult("Fulcher-Berg Engineering Corp", results);
                                }
                            }
                        } else if ("BC".equals(state)) {
                            if ("Consulting".equals(accountType)) {
                                if ("Construction".equals(industry)) {
                                    addResult("Q & Q Lopez Telecommunications Associates", results);
                                    addResult("X & N Eichorn Construction Corp", results);
                                } else if ("Communications".equals(industry)) {
                                    addResult("J & C Anderson Telecommunications Associates", results);
                                    addResult("E & Q Curtsinger Engineering, Ltd", results);
                                } else if ("Engineering".equals(industry)) {
                                    addResult("Detwiler-Biltoft Transportation Corp", results);
                                    addResult("F & M Detwiler Transportation Corp", results);
                                } else if ("Machinery".equals(industry)) {
                                    addResult("O & C Younce Construction, Inc", results);
                                    addResult("Doolittle-Burnham Transportation Corp", results);
                                } else if ("Telecommunications".equals(industry)) {
                                    addResult("I & G Sharp Machinery Group", results);
                                    addResult("D & E Sall Telecommunications, Inc", results);
                                }
                            } else if ("Distribution".equals(accountType)) {
                                if ("Construction".equals(industry)) {
                                    addResult("C & A Magenheim Engineering Group", results);
                                    addResult("Mehlert-Duran Construction Group", results);
                                } else if ("Communications".equals(industry)) {
                                    addResult("M & Y Bruno Communications, Ltd", results);
                                    addResult("Stewart-Velasquez Electronics Holdings", results);
                                    addResult("X & M Dubois Construction Corp", results);
                                } else if ("Engineering".equals(industry)) {
                                    addResult("Dubois-Maestas Telecommunications, Ltd", results);
                                    addResult("Azari-Dabit Telecommunications, Ltd", results);
                                    addResult("D & A Carmona Machinery Group", results);
                                } else if ("Machinery".equals(industry)) {
                                    addResult("D & Z McLaughlin Electronics, Ltd", results);
                                    addResult("Henry-Barros Engineering Company", results);
                                } else if ("Telecommunications".equals(industry)) {
                                    addResult("W & N Brumagen Telecommunications Partners", results);
                                    addResult("D & D Wilson Electronics Holdings", results);
                                    addResult("B & P Pierson Telecommunications Holdings", results);
                                }
                            } else if ("Manufactoring".equals(accountType)) {
                                if ("Construction".equals(industry)) {
                                    addResult("Carlson-Margolis Machinery Company", results);
                                    addResult("S & Y Miller Construction, Ltd", results);
                                } else if ("Communications".equals(industry)) {
                                    addResult("X & V Slusher Construction, Ltd", results);
                                    addResult("X & C McCain Transportation, Ltd", results);
                                } else if ("Engineering".equals(industry)) {
                                    addResult("LaTulippe-Lewis Engineering Group", results);
                                    addResult("Y & B Bomar Communications Partners", results);
                                } else if ("Machinery".equals(industry)) {
                                    addResult("L & O Wheeler Machinery Group", results);
                                    addResult("Wheeler-Irvin Machinery Corp     ", results);
                                } else if ("Telecommunications".equals(industry)) {
                                    addResult("J & R Green Construction Group", results);
                                    addResult("Green-Stanford Communications Associates", results);
                                }
                            }
                        }
                    }
                }
            }
        }
        return results;
    }

    public static List<InputControlOption> executeOption(String parameterName, Map<String, Object> parameterValues) {
        List<InputControlOption> eventOptions = new ArrayList<InputControlOption>();

        OrderedMap orderedMap = executeOrderedMap(parameterName, parameterValues);
        for (Map.Entry<String, String[]> entry : ((Map<String, String[]>) orderedMap).entrySet()) {
            InputControlOption option = new InputControlOption(entry.getKey(), entry.getValue()[1]);
            eventOptions.add(option);
        }
        return eventOptions;
    }

    public static void addResult(String result, Map results) {
        results.put(result, new String[] {result});
    }

    public static void feedResults(Map results, String... values) {
        for (String value : values) {
            addResult(value, results);
        }
    }
}
