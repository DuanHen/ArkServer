/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson.JSONArray
 *  com.alibaba.fastjson.JSONObject
 *  com.hypergryph.arknights.ArknightsApplication
 *  com.hypergryph.arknights.core.dao.userDao
 *  com.hypergryph.arknights.core.decrypt.Utils
 *  com.hypergryph.arknights.core.pojo.Account
 *  com.hypergryph.arknights.game.charBuild
 *  java.lang.Float
 *  java.lang.Long
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.text.DecimalFormat
 *  java.util.Date
 *  java.util.List
 *  java.util.Map$Entry
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.web.bind.annotation.PostMapping
 *  org.springframework.web.bind.annotation.RequestBody
 *  org.springframework.web.bind.annotation.RequestHeader
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RestController
 */
package com.hypergryph.arknights.game;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hypergryph.arknights.ArknightsApplication;
import com.hypergryph.arknights.core.dao.userDao;
import com.hypergryph.arknights.core.decrypt.Utils;
import com.hypergryph.arknights.core.pojo.Account;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/charBuild"})
public class charBuild {
    @PostMapping(value={"/upgradeChar"}, produces={"application/json;charset=UTF-8"})
    public JSONObject UpgradeChar(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        int i;
        int AddedExp;
        int i2;
        int levelexp;
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /charBuild/upgradeChar");
        int charInstId = JsonBody.getIntValue("charInstId");
        JSONArray expMats = JsonBody.getJSONArray("expMats");
        List Accounts = userDao.queryAccountBySecret((String)secret);
        if (Accounts.size() != 1) {
            JSONObject result = new JSONObject(true);
            result.put("result", 2);
            result.put("error", "无法查询到此账户");
            return result;
        }
        Long uid = ((Account)Accounts.get(0)).getUid();
        if (((Account)Accounts.get(0)).getBan() == 1L) {
            response.setStatus(500);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 403);
            result.put("error", "Bad Request");
            result.put("message", "error");
            return result;
        }
        JSONObject UserSyncData = JSONObject.parseObject((String)((Account)Accounts.get(0)).getUser());
        JSONObject chars = UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf((int)charInstId));
        String charid = chars.getString("charId");
        int evolvePhase = chars.getIntValue("evolvePhase");
        int level = chars.getIntValue("level");
        int exp = chars.getIntValue("exp");
        JSONObject inventory = new JSONObject(true);
        int AddExp = 0;
        for (int s = 0; s < expMats.size(); ++s) {
            String itemid = expMats.getJSONObject(s).getString("id");
            int count = expMats.getJSONObject(s).getInteger("count");
            if (itemid.equals("2001")) {
                AddExp += 200 * count;
            } else if (itemid.equals("2002")) {
                AddExp += 400 * count;
            } else if (itemid.equals("2003")) {
                AddExp += 1000 * count;
            } else if (itemid.equals("2004")) {
                AddExp += 2000 * count;
            }
            UserSyncData.getJSONObject("inventory").put(itemid, (UserSyncData.getJSONObject("inventory").getIntValue(itemid) - count));
            inventory.put(itemid, UserSyncData.getJSONObject("inventory").getIntValue(itemid));
        }
        int rarity = ArknightsApplication.characterJson.getJSONObject(charid).getIntValue("rarity");
        String evolve_0 = "[0,100,117,134,151,168,185,202,219,236,253,270,287,304,321,338,355,372,389,406,423,440,457,474,491,508,525,542,559,574,589,605,621,637,653,669,685,701,716,724,739,749,759,770,783,804,820,836,852,888,-1]";
        String e_0_cost = "[0,30,36,43,50,57,65,73,81,90,99,108,118,128,138,149,160,182,206,231,258,286,315,346,378,411,446,482,520,557,595,635,677,720,764,809,856,904,952,992,1042,1086,1131,1178,1229,1294,1353,1413,1474,1572,-1]";
        String evolve_1 = "[0,120,172,224,276,328,380,432,484,536,588,640,692,744,796,848,900,952,1004,1056,1108,1160,1212,1264,1316,1368,1420,1472,1524,1576,1628,1706,1784,1862,1940,2018,2096,2174,2252,2330,2408,2584,2760,2936,3112,3288,3464,3640,3816,3992,4168,4344,4520,4696,4890,5326,6019,6312,6505,6838,7391,7657,7823,8089,8355,8621,8887,9153,9419,9605,9951,10448,10945,11442,11939,12436,12933,13430,13927,14549,-1]";
        String e_1_cost = "[0,48,71,95,120,146,173,201,231,262,293,326,361,396,432,470,508,548,589,631,675,719,765,811,859,908,958,1010,1062,1116,1171,1245,1322,1400,1480,1562,1645,1731,1817,1906,1996,2171,2349,2531,2717,2907,3100,3298,3499,3705,3914,4127,4344,4565,4807,5294,6049,6413,6681,7098,7753,8116,8378,8752,9132,9518,9909,10306,10709,11027,11533,12224,12926,13639,14363,15097,15843,16599,17367,18303,-1]";
        String evolve_2 = "[0,191,303,415,527,639,751,863,975,1087,1199,1311,1423,1535,1647,1759,1871,1983,2095,2207,2319,2431,2543,2655,2767,2879,2991,3103,3215,3327,3439,3602,3765,3928,4091,4254,4417,4580,4743,4906,5069,5232,5395,5558,5721,5884,6047,6210,6373,6536,6699,6902,7105,7308,7511,7714,7917,8120,8323,8526,8729,9163,9597,10031,10465,10899,11333,11767,12201,12729,13069,13747,14425,15103,15781,16459,17137,17815,18493,19171,19849,21105,22361,23617,24873,26129,27385,28641,29897,31143,-1]";
        String e_2_cost = "[0,76,124,173,225,279,334,392,451,513,577,642,710,780,851,925,1001,1079,1159,1240,1324,1410,1498,1588,1680,1773,1869,1967,2067,2169,2273,2413,2556,2702,2851,3003,3158,3316,3477,3640,3807,3976,4149,4324,4502,4684,4868,5055,5245,5438,5634,5867,6103,6343,6587,6835,7086,7340,7599,7861,8127,8613,9108,9610,10120,10637,11163,11696,12238,12882,13343,14159,14988,15828,16681,17545,18422,19311,20213,21126,22092,23722,25380,27065,28778,30519,32287,34083,35906,37745,-1]";
        JSONArray characterExpMap = new JSONArray();
        JSONArray characterUpgradeCostMap = new JSONArray();
        int List2 = 0;
        int lv = 0;
        int ep = 0;
        if (evolvePhase == 0) {
            characterExpMap = JSONArray.parseArray((String)evolve_0);
            characterUpgradeCostMap = JSONArray.parseArray((String)e_0_cost);
        } else if (evolvePhase == 1) {
            characterExpMap = JSONArray.parseArray((String)evolve_1);
            characterUpgradeCostMap = JSONArray.parseArray((String)e_1_cost);
        } else if (evolvePhase == 2) {
            characterExpMap = JSONArray.parseArray((String)evolve_2);
            characterUpgradeCostMap = JSONArray.parseArray((String)e_2_cost);
        }
        if (rarity == 5) {
            levelexp = 0;
            for (i2 = 0; i2 < characterExpMap.size(); ++i2) {
                List2 += characterExpMap.getIntValue(i2);
                if (level != i2 + 1) continue;
                levelexp = List2;
                break;
            }
            List2 = 0;
            AddedExp = levelexp + exp + AddExp;
            for (i = 0; i < characterExpMap.size(); ++i) {
                if (AddedExp < (List2 += characterExpMap.getIntValue(i))) {
                    lv = i;
                    ep = characterExpMap.getIntValue(i) - (List2 - AddedExp);
                    chars.put("level", lv);
                    chars.put("exp", ep);
                } else if (evolvePhase == 0) {
                    if (AddedExp < 24400) continue;
                    lv = 50;
                    ep = 0;
                    chars.put("level", lv);
                    chars.put("exp", ep);
                } else if (evolvePhase == 1) {
                    if (AddedExp < 337000) continue;
                    lv = 80;
                    ep = 0;
                    chars.put("level", lv);
                    chars.put("exp", ep);
                } else {
                    if (evolvePhase != 2 || AddedExp < 750000) continue;
                    lv = 90;
                    ep = 0;
                    chars.put("level", lv);
                    chars.put("exp", ep);
                }
                break;
            }
        } else if (rarity == 4) {
            levelexp = 0;
            for (i2 = 0; i2 < characterExpMap.size(); ++i2) {
                List2 += characterExpMap.getIntValue(i2);
                if (level != i2 + 1) continue;
                levelexp = List2;
                break;
            }
            List2 = 0;
            AddedExp = levelexp + exp + AddExp;
            for (i = 0; i < characterExpMap.size(); ++i) {
                if (AddedExp < (List2 += characterExpMap.getIntValue(i))) {
                    lv = i;
                    ep = characterExpMap.getIntValue(i) - (List2 - AddedExp);
                    chars.put("level", lv);
                    chars.put("exp", ep);
                } else if (evolvePhase == 0) {
                    if (AddedExp < 24400) continue;
                    lv = 50;
                    ep = 0;
                    chars.put("level", lv);
                    chars.put("exp", ep);
                } else if (evolvePhase == 1) {
                    if (AddedExp < 215000) continue;
                    lv = 70;
                    ep = 0;
                    chars.put("level", lv);
                    chars.put("exp", ep);
                } else {
                    if (evolvePhase != 2 || AddedExp < 495000) continue;
                    lv = 80;
                    ep = 0;
                    chars.put("level", lv);
                    chars.put("exp", ep);
                }
                break;
            }
        } else if (rarity == 3) {
            levelexp = 0;
            for (i2 = 0; i2 < characterExpMap.size(); ++i2) {
                List2 += characterExpMap.getIntValue(i2);
                if (level != i2 + 1) continue;
                levelexp = List2;
                break;
            }
            List2 = 0;
            AddedExp = levelexp + exp + AddExp;
            for (i = 0; i < characterExpMap.size(); ++i) {
                if (AddedExp < (List2 += characterExpMap.getIntValue(i))) {
                    lv = i;
                    ep = characterExpMap.getIntValue(i) - (List2 - AddedExp);
                    chars.put("level", lv);
                    chars.put("exp", ep);
                } else if (evolvePhase == 0) {
                    if (AddedExp < 20200) continue;
                    lv = 45;
                    ep = 0;
                    chars.put("level", lv);
                    chars.put("exp", ep);
                } else if (evolvePhase == 1) {
                    if (AddedExp < 130000) continue;
                    lv = 60;
                    ep = 0;
                    chars.put("level", lv);
                    chars.put("exp", ep);
                } else {
                    if (evolvePhase != 2 || AddedExp < 333800) continue;
                    lv = 70;
                    ep = 0;
                    chars.put("level", lv);
                    chars.put("exp", ep);
                }
                break;
            }
        } else if (rarity == 2) {
            levelexp = 0;
            for (i2 = 0; i2 < characterExpMap.size(); ++i2) {
                List2 += characterExpMap.getIntValue(i2);
                if (level != i2 + 1) continue;
                levelexp = List2;
                break;
            }
            List2 = 0;
            AddedExp = levelexp + exp + AddExp;
            for (i = 0; i < characterExpMap.size(); ++i) {
                if (AddedExp < (List2 += characterExpMap.getIntValue(i))) {
                    lv = i;
                    ep = characterExpMap.getIntValue(i) - (List2 - AddedExp);
                    chars.put("level", lv);
                    chars.put("exp", ep);
                } else if (evolvePhase == 0) {
                    if (AddedExp < 16400) continue;
                    lv = 40;
                    ep = 0;
                    chars.put("level", lv);
                    chars.put("exp", ep);
                } else {
                    if (evolvePhase != 1 || AddedExp < 99000) continue;
                    lv = 55;
                    ep = 0;
                    chars.put("level", lv);
                    chars.put("exp", ep);
                }
                break;
            }
        } else if (rarity == 1) {
            levelexp = 0;
            for (i2 = 0; i2 < characterExpMap.size(); ++i2) {
                List2 += characterExpMap.getIntValue(i2);
                if (level != i2 + 1) continue;
                levelexp = List2;
                break;
            }
            List2 = 0;
            AddedExp = levelexp + exp + AddExp;
            for (i = 0; i < characterExpMap.size(); ++i) {
                if (AddedExp < (List2 += characterExpMap.getIntValue(i))) {
                    lv = i;
                    ep = characterExpMap.getIntValue(i) - (List2 - AddedExp);
                    chars.put("level", lv);
                    chars.put("exp", ep);
                } else {
                    if (evolvePhase != 0 || AddedExp < 9800) continue;
                    lv = 30;
                    ep = 0;
                    chars.put("level", lv);
                    chars.put("exp", ep);
                }
                break;
            }
        } else if (rarity == 0) {
            levelexp = 0;
            for (i2 = 0; i2 < characterExpMap.size(); ++i2) {
                List2 += characterExpMap.getIntValue(i2);
                if (level != i2 + 1) continue;
                levelexp = List2;
                break;
            }
            List2 = 0;
            AddedExp = levelexp + exp + AddExp;
            for (i = 0; i < characterExpMap.size(); ++i) {
                if (AddedExp < (List2 += characterExpMap.getIntValue(i))) {
                    lv = i;
                    ep = characterExpMap.getIntValue(i) - (List2 - AddedExp);
                    chars.put("level", lv);
                    chars.put("exp", ep);
                } else {
                    if (evolvePhase != 0 || AddedExp < 9800) continue;
                    lv = 30;
                    ep = 0;
                    chars.put("level", lv);
                    chars.put("exp", ep);
                }
                break;
            }
        }
        int baseCost = 0;
        int expCost = Math.round((float)Float.parseFloat((String)new DecimalFormat("0.00").format((double)((float)characterUpgradeCostMap.getIntValue(level) * (float)exp / (float)characterExpMap.getIntValue(level)))));
        for (i = 0; i < level; ++i) {
            baseCost += characterUpgradeCostMap.getIntValue(i);
        }
        int addBaseCost = 0;
        int addExpCost = Math.round((float)Float.parseFloat((String)new DecimalFormat("0.00").format((double)((float)characterUpgradeCostMap.getIntValue(chars.getIntValue("level")) * (float)chars.getIntValue("exp") / (float)characterExpMap.getIntValue(chars.getIntValue("level"))))));
        for (int i3 = 0; i3 < chars.getIntValue("level"); ++i3) {
            addBaseCost += characterUpgradeCostMap.getIntValue(i3);
        }
        int UpgradeCost = addBaseCost + addExpCost - (baseCost + expCost);
        UserSyncData.getJSONObject("status").put("gold", (UserSyncData.getJSONObject("status").getIntValue("gold") - UpgradeCost));
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject modified = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        JSONObject troop = new JSONObject(true);
        JSONObject charss = new JSONObject(true);
        JSONObject chars_id = new JSONObject(true);
        JSONObject status = new JSONObject(true);
        status.put("gold", UserSyncData.getJSONObject("status").getIntValue("gold"));
        chars_id.put("exp", ep);
        chars_id.put("level", lv);
        chars_id.put("favorPoint", chars.getIntValue("favorPoint"));
        charss.put(String.valueOf((int)charInstId), chars_id);
        troop.put("chars", charss);
        modified.put("troop", troop);
        modified.put("status", status);
        modified.put("inventory", inventory);
        playerDataDelta.put("deleted", new JSONObject(true));
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        result.put("result", 0);
        return result;
    }

    @PostMapping(value={"/upgradeSkill"}, produces={"application/json;charset=UTF-8"})
    public JSONObject UpgradeSkill(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /charBuild/upgradeSkill");
        int charInstId = JsonBody.getIntValue("charInstId");
        int targetLevel = JsonBody.getIntValue("targetLevel");
        List Accounts = userDao.queryAccountBySecret((String)secret);
        if (Accounts.size() != 1) {
            JSONObject result = new JSONObject(true);
            result.put("result", 2);
            result.put("error", "无法查询到此账户");
            return result;
        }
        Long uid = ((Account)Accounts.get(0)).getUid();
        if (((Account)Accounts.get(0)).getBan() == 1L) {
            response.setStatus(500);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 403);
            result.put("error", "Bad Request");
            result.put("message", "error");
            return result;
        }
        JSONObject UserSyncData = JSONObject.parseObject((String)((Account)Accounts.get(0)).getUser());
        JSONObject inventory = new JSONObject(true);
        JSONObject chars = UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf((int)charInstId));
        String charid = chars.getString("charId");
        JSONArray allSkillLvlup = ArknightsApplication.characterJson.getJSONObject(String.valueOf(charid)).getJSONArray("allSkillLvlup");
        for (int i = 0; i < allSkillLvlup.size(); ++i) {
            if (targetLevel - 2 != i) continue;
            JSONArray lvlUpCost = allSkillLvlup.getJSONObject(i).getJSONArray("lvlUpCost");
            for (int l = 0; l < lvlUpCost.size(); ++l) {
                String itemid = lvlUpCost.getJSONObject(l).getString("id");
                int count = lvlUpCost.getJSONObject(l).getIntValue("count");
                UserSyncData.getJSONObject("inventory").put(itemid, (UserSyncData.getJSONObject("inventory").getIntValue(itemid) - count));
                inventory.put(itemid, UserSyncData.getJSONObject("inventory").getIntValue(itemid));
            }
        }
        UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf((int)charInstId)).put("mainSkillLvl", targetLevel);
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject modified = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        JSONObject troop = new JSONObject(true);
        JSONObject charss = new JSONObject(true);
        JSONObject chars_id = new JSONObject(true);
        chars_id.put("mainSkillLvl", targetLevel);
        charss.put(String.valueOf((int)charInstId), chars_id);
        troop.put("chars", charss);
        modified.put("troop", troop);
        modified.put("inventory", inventory);
        playerDataDelta.put("deleted", new JSONObject(true));
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        return result;
    }

    @PostMapping(value={"/setDefaultSkill"}, produces={"application/json;charset=UTF-8"})
    public JSONObject SetDefaultSkill(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /charBuild/setDefaultSkill");
        int charInstId = JsonBody.getIntValue("charInstId");
        int defaultSkillIndex = JsonBody.getInteger("defaultSkillIndex");
        List Accounts = userDao.queryAccountBySecret((String)secret);
        if (Accounts.size() != 1) {
            JSONObject result = new JSONObject(true);
            result.put("result", 2);
            result.put("error", "无法查询到此账户");
            return result;
        }
        Long uid = ((Account)Accounts.get(0)).getUid();
        if (((Account)Accounts.get(0)).getBan() == 1L) {
            response.setStatus(500);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 403);
            result.put("error", "Bad Request");
            result.put("message", "error");
            return result;
        }
        JSONObject UserSyncData = JSONObject.parseObject((String)((Account)Accounts.get(0)).getUser());
        UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf((int)charInstId)).put("defaultSkillIndex", defaultSkillIndex);
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        JSONObject modified = new JSONObject(true);
        JSONObject troop = new JSONObject(true);
        JSONObject chars = new JSONObject(true);
        chars.put(String.valueOf((int)charInstId), UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf((int)charInstId)));
        troop.put("chars", chars);
        modified.put("troop", troop);
        playerDataDelta.put("deleted", new JSONObject(true));
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        return result;
    }

    @PostMapping(value={"/boostPotential"}, produces={"application/json;charset=UTF-8"})
    public JSONObject BoostPotential(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /charBuild/boostPotential");
        int charInstId = JsonBody.getIntValue("charInstId");
        int targetRank = JsonBody.getInteger("targetRank");
        String itemId = JsonBody.getString("itemId");
        List Accounts = userDao.queryAccountBySecret((String)secret);
        if (Accounts.size() != 1) {
            JSONObject result = new JSONObject(true);
            result.put("result", 2);
            result.put("error", "无法查询到此账户");
            return result;
        }
        Long uid = ((Account)Accounts.get(0)).getUid();
        if (((Account)Accounts.get(0)).getBan() == 1L) {
            response.setStatus(500);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 403);
            result.put("error", "Bad Request");
            result.put("message", "error");
            return result;
        }
        JSONObject UserSyncData = JSONObject.parseObject((String)((Account)Accounts.get(0)).getUser());
        if (itemId.indexOf("_char_") != -1) {
            UserSyncData.getJSONObject("inventory").put(itemId, (UserSyncData.getJSONObject("inventory").getIntValue(itemId) - 1));
        } else {
            UserSyncData.getJSONObject("inventory").put(itemId, (UserSyncData.getJSONObject("inventory").getIntValue(itemId) - 4));
        }
        UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf((int)charInstId)).put("potentialRank", targetRank);
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject modified = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        JSONObject inventory = new JSONObject(true);
        JSONObject troop = new JSONObject(true);
        JSONObject chars = new JSONObject(true);
        JSONObject charid = new JSONObject(true);
        charid.put("potentialRank", targetRank);
        chars.put(String.valueOf((int)charInstId), charid);
        troop.put("chars", chars);
        inventory.put(itemId, UserSyncData.getJSONObject("inventory").getIntValue(itemId));
        modified.put("inventory", inventory);
        modified.put("troop", troop);
        playerDataDelta.put("deleted", new JSONObject(true));
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        result.put("result", 1);
        return result;
    }

    @PostMapping(value={"/evolveChar"}, produces={"application/json;charset=UTF-8"})
    public JSONObject EvolveChar(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        int i;
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /charBuild/evolveChar");
        int charInstId = JsonBody.getIntValue("charInstId");
        int destEvolvePhase = JsonBody.getIntValue("destEvolvePhase");
        List Accounts = userDao.queryAccountBySecret((String)secret);
        if (Accounts.size() != 1) {
            JSONObject result = new JSONObject(true);
            result.put("result", 2);
            result.put("error", "无法查询到此账户");
            return result;
        }
        Long uid = ((Account)Accounts.get(0)).getUid();
        if (((Account)Accounts.get(0)).getBan() == 1L) {
            response.setStatus(500);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 403);
            result.put("error", "Bad Request");
            result.put("message", "error");
            return result;
        }
        JSONObject UserSyncData = JSONObject.parseObject((String)((Account)Accounts.get(0)).getUser());
        JSONObject chars = UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf((int)charInstId));
        JSONObject inventory = new JSONObject(true);
        String charid = chars.getString("charId");
        int rarity = ArknightsApplication.characterJson.getJSONObject(String.valueOf(charid)).getIntValue("rarity");
        JSONArray phases = ArknightsApplication.characterJson.getJSONObject(String.valueOf(charid)).getJSONArray("phases");
        JSONArray skills = ArknightsApplication.characterJson.getJSONObject(String.valueOf(charid)).getJSONArray("skills");
        for (int i2 = 0; i2 < phases.size(); ++i2) {
            if (destEvolvePhase != i2) continue;
            JSONArray evolveCost = phases.getJSONObject(i2).getJSONArray("evolveCost");
            for (int l = 0; l < evolveCost.size(); ++l) {
                String itemid = evolveCost.getJSONObject(l).getString("id");
                int count = evolveCost.getJSONObject(l).getIntValue("count");
                UserSyncData.getJSONObject("inventory").put(itemid, (UserSyncData.getJSONObject("inventory").getIntValue(itemid) - count));
                inventory.put(itemid, UserSyncData.getJSONObject("inventory").getIntValue(itemid));
            }
        }
        int gold = UserSyncData.getJSONObject("status").getIntValue("gold");
        JSONObject tchar = UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf((int)charInstId));
        String charId = tchar.getString("charId");
        JSONArray charskills = tchar.getJSONArray("skills");
        if (destEvolvePhase == 1) {
            if (rarity == 2) {
                gold -= 10000;
            } else if (rarity == 3) {
                gold -= 15000;
            } else if (rarity == 4) {
                gold -= 20000;
            } else if (rarity == 5) {
                gold -= 30000;
            }
            for (i = 0; i < charskills.size(); ++i) {
                if (!charskills.getJSONObject(i).getString("skillId").equals(skills.getJSONObject(i).getString("skillId")) || skills.getJSONObject(i).getJSONObject("unlockCond").getIntValue("phase") != 1) continue;
                charskills.getJSONObject(i).put("unlock", 1);
            }
        } else if (destEvolvePhase == 2) {
            if (rarity == 3) {
                gold -= 60000;
            } else if (rarity == 4) {
                gold -= 120000;
            } else if (rarity == 5) {
                gold -= 180000;
            }
            if (tchar.getString("skin").equals((charId + "#1"))) {
                tchar.put("skin", (charId + "#2"));
            }
            for (i = 0; i < charskills.size(); ++i) {
                if (!charskills.getJSONObject(i).getString("skillId").equals(skills.getJSONObject(i).getString("skillId")) || skills.getJSONObject(i).getJSONObject("unlockCond").getIntValue("phase") != 2) continue;
                charskills.getJSONObject(i).put("unlock", 1);
            }
        }
        UserSyncData.getJSONObject("status").put("gold", gold);
        tchar.put("evolvePhase", destEvolvePhase);
        tchar.put("level", 1);
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject modified = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        JSONObject troop = new JSONObject(true);
        JSONObject status = new JSONObject(true);
        status.put("gold", gold);
        JSONObject charss = new JSONObject(true);
        charss.put(String.valueOf((int)charInstId), tchar);
        troop.put("chars", charss);
        modified.put("troop", troop);
        modified.put("inventory", inventory);
        modified.put("status", status);
        playerDataDelta.put("deleted", new JSONObject(true));
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        return result;
    }

    @PostMapping(value={"/changeCharSkin"}, produces={"application/json;charset=UTF-8"})
    public JSONObject ChangeCharSkin(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /charBuild/changeCharSkin");
        int charInstId = JsonBody.getIntValue("charInstId");
        String skinId = JsonBody.getString("skinId");
        List Accounts = userDao.queryAccountBySecret((String)secret);
        if (Accounts.size() != 1) {
            JSONObject result = new JSONObject(true);
            result.put("result", 2);
            result.put("error", "无法查询到此账户");
            return result;
        }
        Long uid = ((Account)Accounts.get(0)).getUid();
        if (((Account)Accounts.get(0)).getBan() == 1L) {
            response.setStatus(500);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 403);
            result.put("error", "Bad Request");
            result.put("message", "error");
            return result;
        }
        JSONObject UserSyncData = JSONObject.parseObject((String)((Account)Accounts.get(0)).getUser());
        UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf((int)charInstId)).put("skin", skinId);
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject modified = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        JSONObject troop = new JSONObject(true);
        JSONObject chars = new JSONObject(true);
        chars.put(String.valueOf((int)charInstId), UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf((int)charInstId)));
        troop.put("chars", chars);
        modified.put("troop", troop);
        playerDataDelta.put("deleted", new JSONObject(true));
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        return result;
    }

    @PostMapping(value={"/setCharVoiceLan"}, produces={"application/json;charset=UTF-8"})
    public JSONObject setCharVoiceLan(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /charBuild/setCharVoiceLan");
        JSONArray charList = JsonBody.getJSONArray("charList");
        String voiceLan = JsonBody.getString("voiceLan");
        List Accounts = userDao.queryAccountBySecret((String)secret);
        if (Accounts.size() != 1) {
            JSONObject result = new JSONObject(true);
            result.put("result", 2);
            result.put("error", "无法查询到此账户");
            return result;
        }
        Long uid = ((Account)Accounts.get(0)).getUid();
        if (((Account)Accounts.get(0)).getBan() == 1L) {
            response.setStatus(500);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 403);
            result.put("error", "Bad Request");
            result.put("message", "error");
            return result;
        }
        JSONObject UserSyncData = JSONObject.parseObject((String)((Account)Accounts.get(0)).getUser());
        for (int i = 0; i < charList.size(); ++i) {
            UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(charList.getString(i)).put("voiceLan", voiceLan);
        }
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject modified = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        JSONObject troop = new JSONObject(true);
        troop.put("chars", UserSyncData.getJSONObject("troop").getJSONObject("chars"));
        modified.put("troop", troop);
        playerDataDelta.put("deleted", new JSONObject(true));
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        return result;
    }

    @PostMapping(value={"/batchSetCharVoiceLan"}, produces={"application/json;charset=UTF-8"})
    public JSONObject batchSetCharVoiceLan(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /charBuild/batchSetCharVoiceLan");
        String voiceLan = JsonBody.getString("voiceLan");
        List Accounts = userDao.queryAccountBySecret((String)secret);
        if (Accounts.size() != 1) {
            JSONObject result = new JSONObject(true);
            result.put("result", 2);
            result.put("error", "无法查询到此账户");
            return result;
        }
        Long uid = ((Account)Accounts.get(0)).getUid();
        if (((Account)Accounts.get(0)).getBan() == 1L) {
            response.setStatus(500);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 403);
            result.put("error", "Bad Request");
            result.put("message", "error");
            return result;
        }
        JSONObject UserSyncData = JSONObject.parseObject((String)((Account)Accounts.get(0)).getUser());
        JSONObject voiceLangDict = ArknightsApplication.charwordTable.getJSONObject("voiceLangDict");
        JSONObject chars = UserSyncData.getJSONObject("troop").getJSONObject("chars");
        for (Map.Entry entry : chars.entrySet()) {
            JSONObject cvDictionary;
            String charId = chars.getJSONObject(entry.getKey().toString()).getString("charId");
            if (!voiceLangDict.containsKey(charId) || !(cvDictionary = voiceLangDict.getJSONObject(charId).getJSONObject("dict")).containsKey(voiceLan)) continue;
            chars.getJSONObject(entry.getKey().toString()).put("voiceLan", voiceLan);
        }
        UserSyncData.getJSONObject("status").put("globalVoiceLan", voiceLan);
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject modified = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        JSONObject troop = new JSONObject(true);
        troop.put("chars", chars);
        modified.put("troop", troop);
        playerDataDelta.put("deleted", new JSONObject(true));
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        result.put("result", 0);
        return result;
    }

    @PostMapping(value={"/unlockEquipment"}, produces={"application/json;charset=UTF-8"})
    public JSONObject unlockEquipment(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /charBuild/unlockEquipment");
        int charInstId = JsonBody.getIntValue("charInstId");
        String templateId = JsonBody.getString("templateId");
        String equipId = JsonBody.getString("equipId");
        List Accounts = userDao.queryAccountBySecret((String)secret);
        if (Accounts.size() != 1) {
            JSONObject result = new JSONObject(true);
            result.put("result", 2);
            result.put("error", "无法查询到此账户");
            return result;
        }
        Long uid = ((Account)Accounts.get(0)).getUid();
        if (((Account)Accounts.get(0)).getBan() == 1L) {
            response.setStatus(500);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 403);
            result.put("error", "Bad Request");
            result.put("message", "error");
            return result;
        }
        JSONObject UserSyncData = JSONObject.parseObject((String)((Account)Accounts.get(0)).getUser());
        UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf((int)charInstId)).getJSONObject("equip").getJSONObject(equipId).put("locked", 0);
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject modified = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        JSONObject troop = new JSONObject(true);
        JSONObject chars = new JSONObject(true);
        chars.put(String.valueOf((int)charInstId), UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf((int)charInstId)));
        troop.put("chars", chars);
        modified.put("troop", troop);
        playerDataDelta.put("deleted", new JSONObject(true));
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        return result;
    }

    @PostMapping(value={"/setEquipment"}, produces={"application/json;charset=UTF-8"})
    public JSONObject setEquipment(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /charBuild/setEquipment");
        int charInstId = JsonBody.getIntValue("charInstId");
        String templateId = JsonBody.getString("templateId");
        String equipId = JsonBody.getString("equipId");
        List Accounts = userDao.queryAccountBySecret((String)secret);
        if (Accounts.size() != 1) {
            JSONObject result = new JSONObject(true);
            result.put("result", 2);
            result.put("error", "无法查询到此账户");
            return result;
        }
        Long uid = ((Account)Accounts.get(0)).getUid();
        if (((Account)Accounts.get(0)).getBan() == 1L) {
            response.setStatus(500);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 403);
            result.put("error", "Bad Request");
            result.put("message", "error");
            return result;
        }
        JSONObject UserSyncData = JSONObject.parseObject((String)((Account)Accounts.get(0)).getUser());
        UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf((int)charInstId)).put("currentEquip", equipId);
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject modified = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        JSONObject troop = new JSONObject(true);
        JSONObject chars = new JSONObject(true);
        chars.put(String.valueOf((int)charInstId), UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf((int)charInstId)));
        troop.put("chars", chars);
        modified.put("troop", troop);
        playerDataDelta.put("deleted", new JSONObject(true));
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        return result;
    }

    @PostMapping(value={"/addonStory/unlock"}, produces={"application/json;charset=UTF-8"})
    public JSONObject addonStoryUnlock(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        JSONObject story_1;
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /charBuild/addonStory/unlock");
        String charId = JsonBody.getString("charId");
        String storyId = JsonBody.getString("storyId");
        List Accounts = userDao.queryAccountBySecret((String)secret);
        if (Accounts.size() != 1) {
            JSONObject result = new JSONObject(true);
            result.put("result", 2);
            result.put("error", "无法查询到此账户");
            return result;
        }
        Long uid = ((Account)Accounts.get(0)).getUid();
        if (((Account)Accounts.get(0)).getBan() == 1L) {
            response.setStatus(500);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 403);
            result.put("error", "Bad Request");
            result.put("message", "error");
            return result;
        }
        JSONObject UserSyncData = JSONObject.parseObject((String)((Account)Accounts.get(0)).getUser());
        JSONObject story2 = new JSONObject(true);
        story2.put("fts", (new Date().getTime() / 1000L));
        story2.put("rts", (new Date().getTime() / 1000L));
        JSONObject addon = UserSyncData.getJSONObject("troop").getJSONObject("addon");
        JSONObject char_data = new JSONObject(true);
        if (addon.containsKey(charId)) {
            if (addon.getJSONObject(charId).containsKey("story")) {
                addon.getJSONObject(charId).getJSONObject("story").put(storyId, story2);
                UserSyncData.getJSONObject("troop").put("addon", addon);
            } else {
                story_1 = new JSONObject(true);
                story_1.put(storyId, story2);
                char_data = addon.getJSONObject(charId);
                char_data.put("story", story_1);
                addon.put(charId, char_data);
                UserSyncData.getJSONObject("troop").put("addon", addon);
            }
        } else {
            story_1 = new JSONObject(true);
            story_1.put(storyId, story2);
            char_data.put("story", story_1);
            addon.put(charId, char_data);
            UserSyncData.getJSONObject("troop").put("addon", addon);
        }
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        JSONObject modified = new JSONObject(true);
        JSONObject troop = new JSONObject(true);
        troop.put("addon", addon);
        modified.put("troop", troop);
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        return result;
    }

    @PostMapping(value={"/addonStage/battleStart"}, produces={"application/json;charset=UTF-8"})
    public JSONObject addonStageBattleStart(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        JSONObject stage;
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /charBuild/addonStage/battleStart");
        String charId = JsonBody.getString("charId");
        String stageId = JsonBody.getString("stageId");
        List Accounts = userDao.queryAccountBySecret((String)secret);
        if (Accounts.size() != 1) {
            JSONObject result = new JSONObject(true);
            result.put("result", 2);
            result.put("error", "无法查询到此账户");
            return result;
        }
        Long uid = ((Account)Accounts.get(0)).getUid();
        if (((Account)Accounts.get(0)).getBan() == 1L) {
            response.setStatus(500);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 403);
            result.put("error", "Bad Request");
            result.put("message", "error");
            return result;
        }
        JSONObject UserSyncData = JSONObject.parseObject((String)((Account)Accounts.get(0)).getUser());
        JSONObject story2 = new JSONObject(true);
        story2.put("fts", -1);
        story2.put("rts", -1);
        story2.put("startTime", null);
        story2.put("startTimes", 0);
        story2.put("state", 0);
        JSONObject addon = UserSyncData.getJSONObject("troop").getJSONObject("addon");
        JSONObject char_data = new JSONObject(true);
        if (!UserSyncData.getJSONObject("troop").getJSONObject("addon").containsKey(charId)) {
            stage = new JSONObject(true);
            stage.put(stageId, story2);
            char_data.put("stage", stage);
            UserSyncData.getJSONObject("troop").getJSONObject("addon").put(charId, char_data);
            UserSyncData.getJSONObject("troop").put("addon", addon);
        }
        if (!UserSyncData.getJSONObject("troop").getJSONObject("addon").getJSONObject(charId).containsKey("stage")) {
            stage = new JSONObject(true);
            stage.put(stageId, story2);
            char_data.put("stage", stage);
            UserSyncData.getJSONObject("troop").getJSONObject("addon").put(charId, char_data);
            UserSyncData.getJSONObject("troop").put("addon", addon);
        }
        if (!UserSyncData.getJSONObject("troop").getJSONObject("addon").getJSONObject(charId).getJSONObject("stage").containsKey(stageId)) {
            stage = new JSONObject(true);
            stage.put(stageId, story2);
            char_data.put("stage", stage);
            UserSyncData.getJSONObject("troop").getJSONObject("addon").put(charId, char_data);
            UserSyncData.getJSONObject("troop").put("addon", addon);
        }
        if (UserSyncData.getJSONObject("troop").getJSONObject("addon").getJSONObject(charId).getJSONObject("stage").getJSONObject(stageId).getIntValue("state") != 3) {
            UserSyncData.getJSONObject("troop").getJSONObject("addon").getJSONObject(charId).getJSONObject("stage").getJSONObject(stageId).put("state", 1);
        }
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        JSONObject modified = new JSONObject(true);
        JSONObject troop = new JSONObject(true);
        troop.put("addon", addon);
        modified.put("troop", troop);
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        result.put("battleId", (charId + "&" + stageId));
        return result;
    }

    @PostMapping(value={"/addonStage/battleFinish"}, produces={"application/json;charset=UTF-8"})
    public JSONObject addonStageBattleFinish(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /charBuild/addonStage/battleFinish");
        List Accounts = userDao.queryAccountBySecret((String)secret);
        if (Accounts.size() != 1) {
            JSONObject result = new JSONObject(true);
            result.put("result", 2);
            result.put("error", "无法查询到此账户");
            return result;
        }
        Long uid = ((Account)Accounts.get(0)).getUid();
        if (((Account)Accounts.get(0)).getBan() == 1L) {
            response.setStatus(500);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 403);
            result.put("error", "Bad Request");
            result.put("message", "error");
            return result;
        }
        JSONObject UserSyncData = JSONObject.parseObject((String)((Account)Accounts.get(0)).getUser());
        JSONObject BattleData = Utils.BattleData_decrypt((String)JsonBody.getString("data"), (String)UserSyncData.getJSONObject("pushFlags").getString("status"));
        String stageId = BattleData.getString("battleId").substring(BattleData.getString("battleId").indexOf("&") + 1);
        String charId = BattleData.getString("battleId").substring(0, BattleData.getString("battleId").indexOf("&"));
        JSONObject stageData = UserSyncData.getJSONObject("troop").getJSONObject("addon").getJSONObject(charId).getJSONObject("stage").getJSONObject(stageId);
        int DropRate = ArknightsApplication.serverConfig.getJSONObject("battle").getIntValue("dropRate");
        int completeState = BattleData.getIntValue("completeState");
        if (ArknightsApplication.serverConfig.getJSONObject("battle").getBooleanValue("debug")) {
            completeState = 3;
        }
        JSONArray firstRewards = new JSONArray();
        if (completeState == 1) {
            if (stageData.getIntValue("state") != 3) {
                stageData.put("state", completeState);
            }
            JSONObject result = new JSONObject(true);
            JSONObject playerDataDelta = new JSONObject(true);
            JSONObject modified = new JSONObject(true);
            JSONObject troop = new JSONObject(true);
            JSONObject addon = new JSONObject(true);
            addon.put(charId, UserSyncData.getJSONObject("troop").getJSONObject("addon").getJSONObject(charId));
            troop.put("addon", addon);
            modified.put("troop", troop);
            playerDataDelta.put("modified", modified);
            result.put("playerDataDelta", playerDataDelta);
            result.put("firstRewards", firstRewards);
            result.put("result", 0);
            return result;
        }
        if (stageData.getIntValue("state") == 3) {
            JSONObject result = new JSONObject(true);
            JSONObject playerDataDelta = new JSONObject(true);
            JSONObject modified = new JSONObject(true);
            JSONObject troop = new JSONObject(true);
            JSONObject addon = new JSONObject(true);
            addon.put(charId, UserSyncData.getJSONObject("troop").getJSONObject("addon").getJSONObject(charId));
            troop.put("addon", addon);
            modified.put("troop", troop);
            playerDataDelta.put("modified", modified);
            result.put("playerDataDelta", playerDataDelta);
            result.put("firstRewards", firstRewards);
            result.put("result", 0);
            return result;
        }
        JSONObject diamondShard = new JSONObject(true);
        diamondShard.put("count", (200 * DropRate));
        diamondShard.put("id", "4003");
        diamondShard.put("type", "DIAMOND_SHD");
        firstRewards.add(diamondShard);
        UserSyncData.getJSONObject("status").put("diamondShard", (UserSyncData.getJSONObject("status").getIntValue("diamondShard") + 200 * DropRate));
        stageData.put("state", completeState);
        stageData.put("fts", (new Date().getTime() / 1000L));
        stageData.put("rts", (new Date().getTime() / 1000L));
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        JSONObject modified = new JSONObject(true);
        JSONObject troop = new JSONObject(true);
        JSONObject addon = new JSONObject(true);
        addon.put(charId, UserSyncData.getJSONObject("troop").getJSONObject("addon").getJSONObject(charId));
        troop.put("addon", addon);
        modified.put("troop", troop);
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        result.put("firstRewards", firstRewards);
        result.put("result", 0);
        return result;
    }

    @PostMapping(value={"/changeCharTemplate"}, produces={"application/json;charset=UTF-8"})
    public JSONObject changeCharTemplate(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /charBuild/changeCharTemplate");
        int charInstId = JsonBody.getIntValue("charInstId");
        String templateId = JsonBody.getString("templateId");
        List Accounts = userDao.queryAccountBySecret((String)secret);
        if (Accounts.size() != 1) {
            JSONObject result = new JSONObject(true);
            result.put("result", 2);
            result.put("error", "无法查询到此账户");
            return result;
        }
        Long uid = ((Account)Accounts.get(0)).getUid();
        if (((Account)Accounts.get(0)).getBan() == 1L) {
            response.setStatus(500);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 403);
            result.put("error", "Bad Request");
            result.put("message", "error");
            return result;
        }
        JSONObject UserSyncData = JSONObject.parseObject((String)((Account)Accounts.get(0)).getUser());
        UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf((int)charInstId)).put("currentTmpl", templateId);
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject modified = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        JSONObject troop = new JSONObject(true);
        JSONObject chars = new JSONObject(true);
        chars.put(String.valueOf((int)charInstId), UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf((int)charInstId)));
        troop.put("chars", chars);
        modified.put("troop", troop);
        playerDataDelta.put("deleted", new JSONObject(true));
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        return result;
    }
}

