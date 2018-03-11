package com.telegram;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class CurrencyBot extends TelegramLongPollingBot {

    private final CurrencyController currencyController = new CurrencyController();

    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        String currencyName = message.getText().toUpperCase();
        currencyController.getCurrencies().thenAccept(map -> {
            Double rate = map.get(currencyName);
            String responseText = rate == null ? ("currency not found: " + currencyName) : ("rate = " + rate);
            SendMessage response = new SendMessage(message.getChatId(), responseText);
            try {
                execute(response);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public String getBotUsername() {
        return "currency bot";
    }

    public String getBotToken() {
        return "562925202:AAFDUU1SuQSC-sQTLNnFqHI_iP2CjFtDfIo";
    }

}
