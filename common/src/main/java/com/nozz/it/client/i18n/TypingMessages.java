package com.nozz.it.client.i18n;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

/**
 * Translation system for typing messages.
 * Lives in :common so it can be shared by all platform implementations.
 */
public class TypingMessages {

    /**
     * Gets the formatted message according to the current language
     */
    public static MutableText getTypingMessage(String... players) {
        int count = players.length;

        if (count == 1) {
            return Text.translatable("istyping.message.single", players[0]);
        } else if (count == 2) {
            return Text.translatable("istyping.message.dual", players[0], players[1]);
        } else if (count == 3) {
            return Text.translatable("istyping.message.triple", players[0], players[1], players[2]);
        } else {
            return Text.translatable("istyping.message.multiple", players[0], players[1], count - 2);
        }
    }
}
