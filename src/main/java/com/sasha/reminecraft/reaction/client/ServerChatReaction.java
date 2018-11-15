package com.sasha.reminecraft.reaction.client;

import com.github.steveice10.mc.protocol.data.message.Message;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sasha.reminecraft.ReMinecraft;
import com.sasha.reminecraft.api.event.ChatReceivedEvent;
import com.sasha.reminecraft.reaction.IPacketReactor;

public class ServerChatReaction implements IPacketReactor<ServerChatPacket> {
    @Override
    public boolean takeAction(ServerChatPacket packet) {
        Message pckMsg = Message.fromJson(removeEvents(packet.getMessage().toJson().getAsJsonObject()));
        ChatReceivedEvent chatEvent = new ChatReceivedEvent(pckMsg.getFullText(), System.currentTimeMillis());
        ReMinecraft.INSTANCE.EVENT_BUS.invokeEvent(chatEvent);
        ReMinecraft.INSTANCE.logger.log("(CHAT) " + pckMsg.getFullText());
        JsonElement elem = packet.getMessage().toJson();
        ReMinecraft.INSTANCE.sendToChildren(new ServerChatPacket(Message.fromJson(elem), packet.getType()));
        return false;
    }

    private static JsonObject removeEvents(JsonObject object) {
        if (object.has("extra")) {
            JsonArray extra = object.getAsJsonArray("extra");
            for (int i = 0; i < extra.size(); i++) {
                JsonObject extraObject = extra.get(i).getAsJsonObject();
                extra.set(i, removeEvents(extraObject));
            }
            object.add("extra", extra);
        }
        object.remove("clickEvent");
        object.remove("hoverEvent");
        object.remove("insertion");
        return object;
    }

}
