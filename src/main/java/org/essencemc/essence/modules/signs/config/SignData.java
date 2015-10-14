package org.essencemc.essence.modules.signs.config;

public class SignData {

    private String name = "";
    private String subPermission = "";
    private boolean enabled = true;
    private boolean attachedBlockAction = false;
    private int uniqueLine = 0;
    private String[] lines = new String[4];
    private String[] actions = new String[4]; //0=left 1=right 2=shift+left 3=shift+right

    public SignData(String name, boolean attachedBlockAction, int uniqueLine, String[] lines, String[] actions) {
        this(name, true, attachedBlockAction, uniqueLine, lines, actions);
    }

    public SignData(String name, boolean attachedBlockAction, int uniqueLine, String[] lines, String action) {
        this(name, true, attachedBlockAction, uniqueLine, lines, new String[] {action, action, action, action});
    }

    public SignData(String name, boolean enabled, boolean attachedBlockAction, int uniqueLine, String[] lines, String[] actions) {
        this.name = name;
        this.subPermission = name.toLowerCase();
        this.enabled = enabled;
        this.attachedBlockAction = attachedBlockAction;
        setLines(lines);
        setActions(actions);
    }

    public String getName() {
        return name;
    }

    public String getSubPermission() {
        return subPermission;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isAttachedBlockAction() {
        return attachedBlockAction;
    }

    public void setIsAttachedBlockAction(boolean attachedBlockAction) {
        this.attachedBlockAction = attachedBlockAction;
    }

    public int getUniqueLine() {
        return uniqueLine;
    }

    public void setUniqueLine(int uniqueLine) {
        this.uniqueLine = uniqueLine;
    }

    public String[] getLines() {
        return lines;
    }

    public void setLines(String[] lines) {
        if (lines.length < 4) {
            lines = new String[] {lines.length > 0 && lines[0] != null ? lines[0] : "", lines.length > 1 && lines[1] != null ? lines[1] : "",
                    lines.length > 2 && lines[2] != null ? lines[2] : "", lines.length > 3 && lines[3] != null ? lines[3] : ""};
        }
        this.lines = lines;
    }

    public String getLine(int line) {
        return lines[line];
    }

    public void setLine(int line, String string) {
        if (string == null) {
            string = "";
        }
        lines[line] = string;
    }

    public String[] getActions() {
        return actions;
    }

    public void setActions(String[] actions) {
        if (actions.length < 4) {
            actions = new String[] {actions.length > 0 && actions[0] != null ? actions[0] : "", actions.length > 1 && actions[1] != null ? actions[1] : "",
                    actions.length > 2 && actions[2] != null ? actions[2] : "", actions.length > 3 && actions[3] != null ? actions[3] : ""};
        }
        this.actions = actions;
    }

    public String getAction(int index) {
        return actions[index];
    }

    public void setAction(int index, String action) {
        if (action == null) {
            action = "";
        }
        actions[index] = action;
    }

    public String getLeftAction(boolean shift) {
        return shift ? actions[2] : actions[0];
    }

    public String getRightAction(boolean shift) {
        return shift ? actions[3] : actions[1];
    }
}
