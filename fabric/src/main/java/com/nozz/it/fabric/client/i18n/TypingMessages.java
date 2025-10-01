package com.nozz.it.fabric.client.i18n;

import com.nozz.it.fabric.client.config.ClientConfig;
import java.util.HashMap;
import java.util.Map;

/**
 * Translation system for typing messages
 */
public class TypingMessages {
    private static final Map<String, Translation> TRANSLATIONS = new HashMap<>();
    
    static {
        // English (default)
        TRANSLATIONS.put("en", new Translation(
            "%s is typing",
            "%s and %s are typing",
            "%s, %s, and %s are typing",
            "%s, %s, and %d others are typing"
        ));
        
        // Spanish
        TRANSLATIONS.put("es", new Translation(
            "%s está escribiendo",
            "%s y %s están escribiendo",
            "%s, %s y %s están escribiendo",
            "%s, %s y %d más están escribiendo"
        ));
        
        // Portuguese
        TRANSLATIONS.put("pt", new Translation(
            "%s está digitando",
            "%s e %s estão digitando",
            "%s, %s e %s estão digitando",
            "%s, %s e %d outros estão digitando"
        ));
        
        // French
        TRANSLATIONS.put("fr", new Translation(
            "%s est en train d'écrire",
            "%s et %s sont en train d'écrire",
            "%s, %s et %s sont en train d'écrire",
            "%s, %s et %d autres sont en train d'écrire"
        ));
        
        // German
        TRANSLATIONS.put("de", new Translation(
            "%s schreibt",
            "%s und %s schreiben",
            "%s, %s und %s schreiben",
            "%s, %s und %d andere schreiben"
        ));
        
        // Russian
        TRANSLATIONS.put("ru", new Translation(
            "%s печатает",
            "%s и %s печатают",
            "%s, %s и %s печатают",
            "%s, %s и ещё %d печатают"
        ));
        
        // Japanese
        TRANSLATIONS.put("ja", new Translation(
            "%sが入力しています",
            "%sと%sが入力しています",
            "%s、%s、%sが入力しています",
            "%s、%s、他%d人が入力しています"
        ));
        
        // Korean
        TRANSLATIONS.put("ko", new Translation(
            "%s님이 입력 중",
            "%s님과 %s님이 입력 중",
            "%s님, %s님, %s님이 입력 중",
            "%s님, %s님 외 %d명이 입력 중"
        ));
        
        // Simplified Chinese
        TRANSLATIONS.put("zh", new Translation(
            "%s正在输入",
            "%s和%s正在输入",
            "%s、%s和%s正在输入",
            "%s、%s和其他%d人正在输入"
        ));
    }
    
    /**
     * Gets the formatted message according to the current language
     */
    public static String getTypingMessage(String... players) {
        String lang = ClientConfig.getInstance().getDetectedLanguage();
        Translation trans = TRANSLATIONS.getOrDefault(lang, TRANSLATIONS.get("en"));
        
        int count = players.length;
        
        if (count == 1) {
            return String.format(trans.single, players[0]);
        } else if (count == 2) {
            return String.format(trans.dual, players[0], players[1]);
        } else if (count == 3) {
            return String.format(trans.triple, players[0], players[1], players[2]);
        } else {
            return String.format(trans.multiple, players[0], players[1], count - 2);
        }
    }
    
    private static class Translation {
        final String single;
        final String dual;
        final String triple;
        final String multiple;
        
        Translation(String single, String dual, String triple, String multiple) {
            this.single = single;
            this.dual = dual;
            this.triple = triple;
            this.multiple = multiple;
        }
    }
}