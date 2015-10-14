package org.essencemc.essence.modules.signs;

import org.essencemc.essence.EssMessage;
import org.essencemc.essence.modules.signs.config.SignData;
import org.essencemc.essencecore.arguments.internal.Argument;
import org.essencemc.essencecore.arguments.internal.ArgumentType;
import org.essencemc.essencecore.message.EText;
import org.essencemc.essencecore.message.Message;
import org.essencemc.essencecore.message.Param;
import org.essencemc.essencecore.util.Util;

import java.util.ArrayList;
import java.util.List;

public class SignParser {

    List<Argument> arguments = new ArrayList<Argument>();
    private EText error = null;

    public SignParser(SignData signData, String[] signText) {
        //Go through each line.
        for (int i = 0; i < 4; i++) {
            String syntax = signData.getLine(i);
            String text = Util.stripAllColor(signText[i]);
            char[] syntaxChars = syntax.toCharArray();
            char[] textChars = text.toCharArray();

            String placeholder = null; //Gets set when a { is found and gets set back to null at a }
            Argument argument = null; //Gets set when a } is found and the syntax is set.
            int textCharIndex = 0; //The character index of the sign text that we are pasing.
            for (char ch : syntaxChars) {
                if (ch == '{') {
                    //Start setting the syntax of the placeholder.
                    placeholder = "";
                    continue;
                }
                if (ch == '}') {
                    //Create an argument based on the syntax of the placeholder.
                    String[] split = placeholder.split(":");
                    ArgumentType type = ArgumentType.STRING;
                    String name = placeholder;
                    if (split.length > 1) {
                        type = ArgumentType.fromString(split[0]);
                        if (type == null) {
                            error = Message.INVALID_ARGUMENT_TYPE.msg().params(Param.P("input", split[0]));
                            return;
                        }
                        name = split[1];
                    }

                    argument = type.getNewArg();
                    argument.setName(name);
                    placeholder = null;
                    continue;
                }
                if (placeholder != null) {
                    //Add character to placeholder syntax till a } is found.
                    placeholder += ch;
                    continue;
                }

                if (argument == null) {
                    //Match regular characters. (in between, before or after placeholder syntax.)
                    if (ch != textChars[textCharIndex]) {
                        error = EssMessage.CORE_SIGN_SYNTAX_MISMATCH.msg().params(Param.P("line", Integer.toString(i + 1)), Param.P("char", Character.toString(textChars[textCharIndex])),
                                Param.P("expected", Character.toString(ch)), Param.P("syntax", signData.getLine(i)));
                        return;
                    }
                    textCharIndex++;
                } else {
                    //Get the text value for the current placeholder syntax.
                    String match = "";
                    do {
                        if (textCharIndex < textChars.length) {
                            match += textChars[textCharIndex++];
                        }
                    } while (textCharIndex < textChars.length && textChars[textCharIndex] != ch);
                    textCharIndex++;

                    //Parse the value.
                    argument.parse(match);
                    if (!argument.isValid()) {
                        error = argument.getError();
                        return;
                    }
                    arguments.add(argument);
                    argument = null;
                }
            }
            //If there are remaining characters and the argument hasn't been set parse those.
            if (argument != null) {
                String match = "";
                while (textCharIndex < textChars.length) {
                    match += textChars[textCharIndex];
                    textCharIndex++;
                }
                argument.parse(match);
                if (!argument.isValid()) {
                    error = argument.getError();
                    return;
                }
                arguments.add(argument);
            }
        }
    }

    public boolean isValid() {
        return error == null;
    }

    public List<Argument> getArguments() {
        return arguments;
    }

    public EText getError() {
        return error;
    }

}
