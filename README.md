# ExchangeRateChecker

Simple application to check the latest exchange rates of different currencies. 
All the interactions will be with the telegram bot, now it has several features as:
 - Notification when exchange rate changes (currently only USD to RUB);
 - Function to check the currency with the special command in the telegram bot.

## Requirements
All you need to use it is to provide an API key to the [ExchangeRateAPI](https://app.exchangerate-api.com) service.
And the Token for the Telegram bot [TelegramBotAPI](https://core.telegram.org/bots/features#creating-a-new-bot).
For that, create a new file private.properties in the repository root folder and set: 
 - 'exchangeApiKey' variable with your own key from ExchangeRateApi service,
 - 'telegramToken' variable with your telegram bot token.