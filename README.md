# ExchangeRateChecker

Currency rate monitoring application with telegram integration. Subscribe to interesting currency rates and be notified when rates change.
Filter out small currency fluctuations by configuring a delta limit.
All the interactions will be with the telegram bot, now it has several features as:
 - Subscribe to an unlimited number of currency pairs;
 - Get notified when the exchange rate changes;
 - Check the currency instantly with the special command in the telegram bot.

## Requirements
All you need to use it is to provide an API key to the [ExchangeRateAPI](https://app.exchangerate-api.com) service.
And the Token for the Telegram bot [TelegramBotAPI](https://core.telegram.org/bots/features#creating-a-new-bot).
For that, create a new file private.properties in the repository root folder and set: 
 - 'exchangeApiKey' variable with your own key from ExchangeRateApi service,
 - 'telegramToken' variable with your telegram bot token.