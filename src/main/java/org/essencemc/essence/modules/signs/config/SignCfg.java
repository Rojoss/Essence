package org.essencemc.essence.modules.signs.config;

import org.essencemc.essencecore.arguments.BoolArg;
import org.essencemc.essencecore.config.internal.EasyConfig;
import org.essencemc.essencecore.util.NumberUtil;

import java.util.*;

public class SignCfg extends EasyConfig {

    public Map<String, Map<String, String>> SIGNS = new HashMap<String, Map<String, String>>();
    private Map<String, SignData> signs = new HashMap<String, SignData>();

    public SignCfg(String fileName) {
        this.setFile(fileName);
        load();
    }

    @Override
    public void load() {
        super.load();
        //If no values load the defaults.
        if (SIGNS.size() < 1) {
            loadDefaults();
            save();
        }
        //Load all signs internally so we don't have to convert it every time.
        signs.clear();
        for (Map.Entry<String, Map<String, String>> entry : SIGNS.entrySet()) {
            Map<String, String> data = entry.getValue();
            SignData signData = new SignData(
                    entry.getKey(),
                    BoolArg.Parse(data.get("enabled")),
                    BoolArg.Parse(data.get("attached-block-action")),
                    NumberUtil.getInt(data.get("unique-line")),
                    new String[] {data.get("line-1"), data.get("line-2"), data.get("line-3"), data.get("line-4")},
                    new String[] {data.get("action-left"), data.get("action-right"), data.get("action-shift-left"), data.get("action-shift-right")}
            );
            signs.put(entry.getKey(), signData);
        }
    }

    @Override
    public void save() {
        //Convert the sign data back to config values.
        for (SignData signData : signs.values()) {
            Map<String, String> data = new TreeMap<String, String>();
            data.put("enabled", Boolean.toString(signData.isEnabled()));
            data.put("attached-block-action", Boolean.toString(signData.isAttachedBlockAction()));
            data.put("unique-line", Integer.toString(signData.getUniqueLine()));
            data.put("line-1", signData.getLine(0));
            data.put("line-2", signData.getLine(1));
            data.put("line-3", signData.getLine(2));
            data.put("line-4", signData.getLine(3));
            data.put("action-left", signData.getAction(0));
            data.put("action-right", signData.getAction(1));
            data.put("action-shift-left", signData.getAction(2));
            data.put("action-shift-right", signData.getAction(3));
            SIGNS.put(signData.getName(), data);
        }
        super.save();
    }

    private void loadDefaults() {
        setSign(new SignData("heal", false, 0, new String[]{"[heal]"}, "heal {player}"), false);
        setSign(new SignData("feed", false, 0, new String[]{"[feed]"}, "feed {player}"), false);
        setSign(new SignData("hand", true, 0, new String[]{"[hand]"}, "bc {hand}"), false);
        setSign(new SignData("test1", false, 0, new String[]{"[test1]", "{single}"}, "bc {single}"), false);
        setSign(new SignData("test2", false, 0, new String[]{"[test2]", "{single}", "{int:hour}:{int:min}:{int:sec}"}, "bc {single} {hour} {min} {sec}"), false);
        setSign(new SignData("test3", false, 0, new String[]{"[test3]", "{single}", "{hour}:{min}:{sec}"}, "bc {single} {hour} {min} {sec}"), false);
    }

    public void deleteSign(String name) {
        if (signs.containsKey(name)) {
            signs.remove(name);
            SIGNS.remove(name);
            save();
        }
    }

    public void setSign(SignData signData) {
        signs.put(signData.getName(), signData);
        save();
    }

    public void setSign(SignData signData, boolean save) {
        signs.put(signData.getName(), signData);
        if (save) {
            save();
        }
    }

    public SignData getSign(String name) {
        if (signs.containsKey(name)) {
            return signs.get(name);
        }
        return null;
    }

    public Map<String, SignData> getSigns() {
        return signs;
    }

    public List<SignData> getSignList() {
        return new ArrayList<>(signs.values());
    }

}
